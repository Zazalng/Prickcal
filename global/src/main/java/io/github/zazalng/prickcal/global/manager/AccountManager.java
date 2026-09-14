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
import java.util.List;
import java.util.Optional;

/**
 * Manages Account lifecycle: consent, lookup, profile access, and deletion.
 */
public class AccountManager extends AbstractManager {
    private final PluginRepository<Account> repo;

    protected AccountManager(ManagerFactory factory) {
        super(factory);
        repo = factory.getRepos().accounts();
        initialize();
    }

    @Override
    public String getTableName() {
        return "accounts";
    }

    @Override
    public AccountManager initialize() {
        return this;
    }

    @Override
    public void reload() {

    }

    @Override
    public void shutdown() {

    }

    /** Find an account by Discord user ID. */
    public Optional<Account> findByUid(String uid) {
        return repo.query()
                .where("uid", uid)
                .findOne();
    }

    public Account findById(long id) {
        return repo.query()
                .where("id", id)
                .findOne().orElseThrow(() -> new PrickcalException(PrickcalEnum.INVALID_ACCOUNT, id + "(id)"));
    }

    public boolean isValid(Account user) {
        return Operator.fromValue(user.getOps()) != Operator.UNKNOWN;
    }

    public boolean isValid(long id) {
        return isValid(findById(id));
    }

    /** Create a new account (consent agreement). Logs the creation. */
    public Account createAccount(String uid, String logInitiatorUid) {
        Account account = new Account();
        account.setUid(uid);
        account.setOps(Operator.defaultUser());
        account = repo.save(account);
        logRecord(logInitiatorUid, Action.CREATE,
                "<@" + logInitiatorUid + "> created account for <@" + uid + "> (given access '" + Operator.fromValue(Operator.defaultUser()).name() + "')");
        return account;
    }

    /** Check if the user has a specific operator level. */
    public boolean hasOperator(Account account, Operator required) {
        return findById(account.getId()).getOps() == required.getValue();
    }

    /** Check if the user is at least a given operator level (lower value = higher rank). */
    public boolean hasMinOperator(Account account, Operator minimum) {
        return findById(account.getId()).getOps() <= minimum.getValue();
    }

    public String crayonCountByStats(Account account, CrayonStats stats) {
        return crayonCountByStats(account.getId(), stats);
    }

    public String crayonCountByStats(Long id, CrayonStats stats) {
        List<ApostleTrack> userApostles = apostleManager().findTracks(findById(id));
        if (userApostles == null || userApostles.isEmpty()) {
            return "0,0";
        }

        int totalAmount = 0;
        int totalPrice = 0;

        for (ApostleTrack track : userApostles) {
            if (track == null || track.getApostleId() == null) {
                continue;
            }

            CrayonLineUp lineup = apostleManager().findLineUp(track.getApostleId());
            if (!lineup.isValid()) {
                throw new PrickcalException(PrickcalEnum.INVALID_CRAYONLINEUP, String.valueOf(lineup.getId()));
            }

            List<Integer> lineUpList = lineup.getLineUp();
            List<Integer> depthList = lineup.getDepth();

            if (lineUpList == null || depthList == null) {
                continue;
            }

            // Guard against uneven list lengths
            int limit = Math.min(lineUpList.size(), depthList.size());

            for (int i = 0; i < limit; i++) {
                Integer targetStat = lineUpList.get(i);
                Integer depthValue = depthList.get(i);

                if (targetStat == null || depthValue == null || stats.getNo() != targetStat) {
                    continue;
                }

                CrayonCosts cost = CrayonCosts.fromDepth(depthValue);
                if (cost != null) {
                    totalAmount += cost.getAmount();
                    totalPrice += cost.getPrice();
                }
            }
        }

        return "%d,%d".formatted(totalAmount, totalPrice);
    }

    public BigDecimal getCrayonsSpent(Account account) {
        int candySpent = 0;
        for (CrayonRecord record : repos.crayonRecords().findBy("uid", account.getId())) {
            candySpent += record.getSpent();
        }
        return new BigDecimal(candySpent);
    }

    public BigDecimal getCrayonsSpent(Long id) {
        return getCrayonsSpent(findById(id));
    }

    public BigDecimal getCrayonsAcquired(Account account) {
        int crayonAcquired = 0;
        for (CrayonRecord record : repos.crayonRecords().findBy("uid", account.getId())) {
            crayonAcquired += record.getCrayon();
        }
        return new BigDecimal(crayonAcquired);
    }

    public BigDecimal getCrayonsAcquired(Long id) {
        return getCrayonsAcquired(findById(id));
    }

    public int getApostleOwned(Account account) {
        return Math.toIntExact(repos.apostleTrackers().countBy("uid", account.getId()));
    }

    /**
     * Permanently delete all data belonging to a user across all tracked tables.
     * Returns the number of affected entities (rough count).
     */
    public Account deleteAllUserData(String uid) {
        int count = 0;

        Optional<Account> account = findByUid(uid);
        if (account.isEmpty()) return null;

        var trackCount = repos.apostleTrackers().query()
                .where("uid", account.get().getId())
                .list();
        for (var t : trackCount) {
            repos.apostleTrackers().delete(t);
        }
        count += trackCount.size();

        var crayonCount = repos.crayonRecords().query()
                .where("uid", account.get().getId())
                .list();
        for (var c : crayonCount) {
            repos.crayonRecords().delete(c);
        }
        count += crayonCount.size();

        var remarkCount = repos.apostleRemarkables().query()
                .where("uid", account.get().getId())
                .list();
        for (var r : remarkCount) {
            repos.apostleRemarkables().delete(r);
        }
        count += remarkCount.size();

        var giftCount = repos.giftAcquires().query()
                .where("uid", account.get().getId())
                .list();
        for (var r : giftCount) {
            repos.giftAcquires().delete(r);
        }
        count += giftCount.size();

        var remarkLogCount = repos.remarkableRecords().query()
                .where("uid", account.get().getId())
                .list();
        for (var r : remarkLogCount) {
            repos.remarkableRecords().delete(r);
        }
        count += remarkLogCount.size();

        repo.delete(account.get());
        count++;

        logDeleted(uid, "a User has deleted all their data (" + count + " records)");
        return account.get();
    }
}
