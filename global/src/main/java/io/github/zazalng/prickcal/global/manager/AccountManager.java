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
import io.github.zazalng.prickcal.global.entities.Account;

import java.util.Optional;

/**
 * Manages Account lifecycle: consent, lookup, profile access, and deletion.
 * All Account mutations are logged through {@link LogManager}.
 */
public class AccountManager {

    private final RepositoryProvider repos;
    private final LogManager logManager;

    public AccountManager(RepositoryProvider repos, LogManager logManager) {
        this.repos = repos;
        this.logManager = logManager;
    }

    /** Find an account by Discord user ID. */
    public Optional<Account> findByUid(String uid) {
        return repos.accounts().query()
                .where("uid", uid)
                .findOne();
    }

    /** Check whether a user has consented (has an account). */
    public boolean hasConsented(String uid) {
        return findByUid(uid).isPresent();
    }

    /** Create a new account (consent agreement). Logs the creation. */
    public Account createAccount(String uid, String logInitiatorUid) {
        Account account = new Account();
        account.setUid(uid);
        repos.accounts().save(account);
        logManager.record(logInitiatorUid, "accounts", Action.CREATE,
                "<@" + logInitiatorUid + "> created account for <@" + uid + ">");
        return account;
    }

    /** Check if the user has a specific operator level. */
    public boolean hasOperator(String uid, Operator required) {
        return findByUid(uid)
                .map(a -> a.getOps() == required.getValue())
                .orElse(false);
    }

    /** Check if the user is at least a given operator level (lower value = higher rank). */
    public boolean hasMinOperator(String uid, Operator minimum) {
        return findByUid(uid)
                .map(a -> a.getOps() <= minimum.getValue())
                .orElse(false);
    }

    /**
     * Permanently delete all data belonging to a user across all tracked tables.
     * Returns the number of affected entities (rough count).
     */
    public int deleteAllUserData(String uid) {
        int count = 0;

        var trackCount = repos.apostleTrackers().query()
                .where("uid", uid)
                .list();
        for (var t : trackCount) {
            repos.apostleTrackers().delete(t);
        }
        count += trackCount.size();

        var crayonCount = repos.crayonRecords().query()
                .where("uid", uid)
                .list();
        for (var c : crayonCount) {
            repos.crayonRecords().delete(c);
        }
        count += crayonCount.size();

        var remarkCount = repos.apostleRemarkables().query()
                .where("uid", uid)
                .list();
        for (var r : remarkCount) {
            repos.apostleRemarkables().delete(r);
        }
        count += remarkCount.size();

        var optAccount = findByUid(uid);
        if (optAccount.isPresent()) {
            repos.accounts().delete(optAccount.get());
            count++;
        }

        logManager.deleted(uid, "accounts", "User deleted all their data (" + count + " records)");
        return count;
    }
}
