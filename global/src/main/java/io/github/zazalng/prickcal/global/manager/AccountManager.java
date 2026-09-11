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

import io.github.zazalng.prickcal.global.contract.operator.Action;
import io.github.zazalng.prickcal.global.contract.operator.Operator;
import io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonCosts;
import io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats;
import io.github.zazalng.prickcal.global.entities.Account;
import io.github.zazalng.prickcal.global.entities.ApostleTrack;
import io.github.zazalng.prickcal.global.entities.CrayonLineUp;
import io.github.zazalng.prickcal.global.exception.PrickcalEnum;
import io.github.zazalng.prickcal.global.exception.PrickcalException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages Account lifecycle: consent, lookup, profile access, and deletion.
 */
public class AccountManager extends AbstractManager {
    protected AccountManager(ManagerFactory factory) {
        super(factory);
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
    public Account findByUid(String uid) {
        return factory.getRepos().accounts().query()
                .where("uid", uid)
                .findOne().orElseThrow(() -> new PrickcalException(PrickcalEnum.INVALID_ACCOUNT, uid));
    }

    /** Check whether a user has consented (has an account). */
    public boolean hasConsented(String uid) {
        return findByUid(uid) != null;
    }

    /** Create a new account (consent agreement). Logs the creation. */
    public Account createAccount(String uid, String logInitiatorUid) {
        Account account = new Account();
        account.setUid(uid);
        account.setOps(Operator.defaultUser());
        factory.getRepos().accounts().save(account);
        logRecord(logInitiatorUid, Action.CREATE,
                "<@" + logInitiatorUid + "> created account for <@" + uid + "> (given access '" + Operator.fromString(Operator.defaultUser()).name() + "')");
        return account;
    }

    /** Check if the user has a specific operator level. */
    public boolean hasOperator(String uid, Operator required) {
        return findByUid(uid).
                .map(a -> a.getOps() == required.getValue())
                .orElse(false);
    }

    /** Check if the user is at least a given operator level (lower value = higher rank). */
    public boolean hasMinOperator(String uid, Operator minimum) {
        return findByUid(uid)
                .map(a -> a.getOps() <= minimum.getValue())
                .orElse(false);
    }

    public Map<Integer, Integer> crayonCountByStats(String uid, CrayonStats stats) {
        if (uid == null || stats == null) {
            return Collections.emptyMap();
        }

        List<ApostleTrack> userApostles = factory.getRepos().apostleTrackers().findBy("uid", uid);
        if (userApostles == null || userApostles.isEmpty()) {
            return Collections.emptyMap();
        }

        ApostleManager apostleManager = factory.getManager(ManagersEnum.APOSTLE);
        var crayonLineupsRepo = factory.getRepos().crayonLineups();

        int totalAmount = 0;
        int totalPrice = 0;
        boolean hasMatches = false;

        for (ApostleTrack track : userApostles) {
            if (track == null || track.getApostleId() == null) {
                continue;
            }

            var crayonLineupOpt = apostleManager.findById(track.getApostleId())
                    .filter(a -> a.getCrayon() != null)
                    .flatMap(a -> crayonLineupsRepo.findById(a.getCrayon()));

            if (crayonLineupOpt.isEmpty()) {
                continue;
            }

            CrayonLineUp lineup = crayonLineupOpt.get();
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
                    hasMatches = true;
                }
            }
        }

        if (!hasMatches) {
            return Collections.emptyMap();
        }

        Map<Integer, Integer> result = new HashMap<>(1);
        result.put(totalAmount, totalPrice);
        return result;
    }

    public int getApostleOwned(String uid) {
        return Math.toIntExact(factory.getRepos().apostleTrackers().countBy("uid", uid));
    }

    /**
     * Permanently delete all data belonging to a user across all tracked tables.
     * Returns the number of affected entities (rough count).
     */
    public int deleteAllUserData(String uid) {
        int count = 0;

        var trackCount = factory.getRepos().apostleTrackers().query()
                .where("uid", uid)
                .list();
        for (var t : trackCount) {
            factory.getRepos().apostleTrackers().delete(t);
        }
        count += trackCount.size();

        var crayonCount = factory.getRepos().crayonRecords().query()
                .where("uid", uid)
                .list();
        for (var c : crayonCount) {
            factory.getRepos().crayonRecords().delete(c);
        }
        count += crayonCount.size();

        var remarkCount = factory.getRepos().apostleRemarkables().query()
                .where("uid", uid)
                .list();
        for (var r : remarkCount) {
            factory.getRepos().apostleRemarkables().delete(r);
        }
        count += remarkCount.size();

        var giftCount = factory.getRepos().giftAcquires().query()
                .where("uid", uid)
                .list();
        for (var r : giftCount) {
            factory.getRepos().giftAcquires().delete(r);
        }
        count += giftCount.size();

        var remarkLogCount = factory.getRepos().remarkableRecords().query()
                .where("uid", uid)
                .list();
        for (var r : remarkLogCount) {
            factory.getRepos().remarkableRecords().delete(r);
        }
        count += remarkLogCount.size();

        var optAccount = findByUid(uid);
        if (optAccount.isPresent()) {
            factory.getRepos().accounts().delete(optAccount.get());
            count++;
        }

        logDeleted(uid, "a User has deleted all their data (" + count + " records)");
        return count;
    }
}
