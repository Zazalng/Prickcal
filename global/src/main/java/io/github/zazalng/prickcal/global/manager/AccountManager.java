/*
 * Prickcal - A Trickcal's procession tracker for Pudel Bot
 * Copyright (C) 2026 Napapon Kamanee
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.zazalng.prickcal.global.manager;

import group.worldstandard.pudel.api.database.PluginRepository;
import io.github.zazalng.prickcal.global.contract.operator.Action;
import io.github.zazalng.prickcal.global.contract.operator.Operator;
import io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonCosts;
import io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats;
import io.github.zazalng.prickcal.global.entities.Account;
import io.github.zazalng.prickcal.global.entities.ApostleTrack;
import io.github.zazalng.prickcal.global.entities.CrayonLineUp;
import io.github.zazalng.prickcal.global.entities.CrayonRecord;
import io.github.zazalng.prickcal.global.exception.PrickcalEnum;
import io.github.zazalng.prickcal.global.exception.PrickcalException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/**
 * Owns {@link Account} rows plus the account-scoped aggregates derived from them:
 * crayon totals, owned apostle counts and full user data deletion.
 */
public class AccountManager extends AbstractManager {
    private final PluginRepository<Account> repo;

    /**
     * Binds the account repository and runs {@link #initialize()}.
     *
     * @param factory the owning factory
     */
    protected AccountManager(ManagerFactory factory) {
        super(factory);
        repo = factory.getRepos().accounts();
        initialize();
    }

    /**
     * Table name written into the audit log entries of this manager.
     *
     * @return {@code "accounts"}
     */
    @Override
    public String getTableName() {
        return "accounts";
    }

    /**
     * No setup is required; the manager is ready once constructed.
     *
     * @return this manager
     */
    @Override
    public AccountManager initialize() {
        return this;
    }

    /**
     * Intentionally empty: this manager keeps no cache to reload.
     */
    @Override
    public void reload() {

    }

    /**
     * Intentionally empty: this manager holds no state to release on shutdown.
     */
    @Override
    public void shutdown() {

    }

    /**
     * Find an account by Discord user ID.
     *
     * @param uid the Discord user id stored on the account row
     * @return the matching account, or {@link Optional#empty()} when the user has no account yet
     */
    public Optional<Account> findByUid(String uid) {
        return repo.query()
                .where("uid", uid)
                .findOne();
    }

    /**
     * Check that an account carries a recognized operator level.
     *
     * @param user the account to inspect
     * @return {@code true} when the stored operator value maps to a real {@link Operator}
     */
    public boolean isValid(Account user) {
        return Operator.fromValue(user.getOps()) != Operator.UNKNOWN;
    }

    /**
     * Create a new account (consent agreement). Logs the creation.
     *
     * @param logInitiatorUid the Discord id of the user giving consent, written to the audit log
     * @param uid the Discord id of the account being created
     * @return the persisted account, set to the default user operator level
     */
    public Account createAccount(String logInitiatorUid, String uid) {
        Account account = new Account();
        account.setUid(uid);
        account.setOps(Operator.defaultUser());
        account = repo.save(account);
        logRecord(0, Action.CREATE, """
                <@%s> created account for <@%s> (given access '%s')
                """
                .formatted(
                        logInitiatorUid,
                        uid,
                        Operator.fromValue(account.getOps()).name()
                )
        );
        return account;
    }

    /**
     * Check if the user has a specific operator level.
     *
     * @param account the account to inspect
     * @param required the operator level to compare against
     * @return {@code true} only when both operator values are exactly equal
     */
    public boolean hasOperator(Account account, Operator required) {
        return account.getOps() == required.getValue();
    }

    /**
     * Check if the user is at least a given operator level (lower value = higher rank).
     *
     * @param account the account to inspect
     * @param minimum the weakest operator level that is still sufficient
     * @return {@code true} when the account operator value is less than or equal to {@code minimum}
     */
    public boolean hasMinOperator(Account account, Operator minimum) {
        return account.getOps() <= minimum.getValue();
    }

    /**
     * Read the earliest crayon record of an account and truncate it to the start of that day.
     * Assumes the account owns at least one crayon record.
     *
     * @param account the account whose crayon records are inspected
     * @return the earliest recorded day as an instant, at the start of that day in the system default zone
     */
    public Instant getFirstDateOfRecord(Account account) {
        return factory.getRepos().crayonRecords().query()
                .where("uid", account.getId())
                .orderByAsc("record_date")
                .list()
                .getFirst()
                .getRecordDate()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();
    }

    /**
     * Sum the crayons an account has unlocked for one stat across all of its apostle tracks.
     *
     * @param account the account whose apostle tracks are inspected
     * @param stats the crayon stat to total
     * @return a {@code "{amount},{price}"} string; {@code "-1,-1"} when {@code stats} is
     *         {@link CrayonStats#UNKNOWN} and {@code "0,0"} when the account tracks no apostle
     * @throws PrickcalException if a tracked apostle resolves to an invalid crayon line-up
     */
    public String crayonCountByStats(Account account, CrayonStats stats) {
        if (stats == CrayonStats.UNKNOWN) return "-1,-1";

        List<ApostleTrack> userApostles = apostleManager().findTracks(account);
        if (userApostles == null || userApostles.isEmpty()) {
            return "0,0";
        }

        int totalAmount = 0;
        int totalPrice = 0;

        for (ApostleTrack track : userApostles) {
            if (track == null || track.getApostleId() == null) {
                continue;
            }

            CrayonLineUp lineup = apostleManager().findLineUp(apostleManager().findById(track.getApostleId()));
            if (!lineup.isValid()) {
                throw new PrickcalException(PrickcalEnum.INVALID_CRAYONLINEUP, String.valueOf(lineup.getId()));
            }

            List<Short> lineUpList = lineup.getLineUp();
            List<Integer> depthList = lineup.getDepth();

            if (lineUpList == null || depthList == null) {
                continue;
            }

            // Guard against uneven list lengths
            int limit = Math.min(lineUpList.size(), depthList.size());

            for (int i = 0; i < limit; i++) {
                Short targetStat = lineUpList.get(i);
                Integer depthValue = depthList.get(i);

                if (targetStat == null || depthValue == null || stats.getNo() != targetStat) {
                    continue;
                }

                CrayonCosts cost = CrayonCosts.fromDepth(depthValue);
                if (cost != null) {
                    if (track.getCrayons().get(i)) {
                        totalAmount += cost.getAmount();
                        totalPrice += cost.getPrice();
                    }
                }
            }
        }

        return "%d,%d".formatted(totalAmount, totalPrice);
    }

    /**
     * Total the candy an account has spent across every crayon record.
     *
     * @param account the account to total
     * @return the sum of the spent amounts, zero when the account has no record
     */
    public BigDecimal getCrayonsSpent(Account account) {
        int candySpent = 0;
        for (CrayonRecord record : repos.crayonRecords().findBy("uid", account.getId())) {
            candySpent += record.getSpent();
        }
        return new BigDecimal(candySpent);
    }

    /**
     * Total the crayons an account has acquired across every crayon record.
     *
     * @param account the account to total
     * @return the sum of the acquired crayons, zero when the account has no record
     */
    public BigDecimal getCrayonsAcquired(Account account) {
        int crayonAcquired = 0;
        for (CrayonRecord record : repos.crayonRecords().findBy("uid", account.getId())) {
            crayonAcquired += record.getCrayon();
        }
        return new BigDecimal(crayonAcquired);
    }

    /**
     * Count the apostles an account currently owns.
     *
     * @param account the account to count for
     * @return the number of apostle tracks whose current star is not zero
     */
    public int getApostleOwned(Account account) {
        return repos.apostleTrackers().query()
                .where("uid", account.getId())
                .whereNot("current_star", 0)
                .list()
                .size();
    }

    /**
     * Permanently delete all data belonging to a user across all tracked tables.
     * Deletes the apostle tracks, crayon records, apostle remarkables, gift acquires and
     * remarkable records, then the account itself; the record count written to the audit
     * log is a rough total of the rows visited.
     *
     * @param uid the Discord id of the user, whose account is read from the session cache
     * @return the now-deleted account
     */
    public Account deleteAllUserData(String uid) {
        int count = 0;

        Account account = sessionManager().getAccountCache(uid);

        var trackCount = repos.apostleTrackers().query()
                .where("uid", account.getId())
                .list();
        for (var t : trackCount) {
            repos.apostleTrackers().delete(t);
        }
        count += trackCount.size();

        var crayonCount = repos.crayonRecords().query()
                .where("uid", account.getId())
                .list();
        for (var c : crayonCount) {
            repos.crayonRecords().delete(c);
        }
        count += crayonCount.size();

        var remarkCount = repos.apostleRemarkables().query()
                .where("uid", account.getId())
                .list();
        for (var r : remarkCount) {
            repos.apostleRemarkables().delete(r);
        }
        count += remarkCount.size();

        var giftCount = repos.giftAcquires().query()
                .where("uid", account.getId())
                .list();
        for (var r : giftCount) {
            repos.giftAcquires().delete(r);
        }
        count += giftCount.size();

        var remarkLogCount = repos.remarkableRecords().query()
                .where("uid", account.getId())
                .list();
        for (var r : remarkLogCount) {
            repos.remarkableRecords().delete(r);
        }
        count += remarkLogCount.size();

        repo.delete(account);
        count++;

        logDeleted(account.getId(), "a User has deleted all their data (" + count + " records)");
        return account;
    }
}
