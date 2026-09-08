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

import io.github.zazalng.prickcal.global.contract.operator.Operator;
import io.github.zazalng.prickcal.global.entities.Account;

import java.util.Optional;

/**
 * Centralized access-control checks based on {@link Operator} levels.
 * Keeps permission logic out of handlers so it's consistent and testable.
 */
public class PermissionManager {

    private final RepositoryProvider repos;

    public PermissionManager(RepositoryProvider repos) {
        this.repos = repos;
    }

    /** Resolve the operator level of a user, defaulting to {@link Operator#UNKNOWN}. */
    public Operator getOperator(String uid) {
        return repos.accounts().query()
                .where("uid", uid)
                .findOne()
                .map(a -> Operator.fromString(a.getOps()))
                .orElse(Operator.UNKNOWN);
    }

    /** Check whether a user has exactly the ADMIN operator. */
    public boolean isAdmin(String uid) {
        return getOperator(uid) == Operator.ADMIN;
    }

    /** Check whether a user has at least EDITOR privileges (ADMIN or EDITOR). */
    public boolean isEditorOrAbove(String uid) {
        Operator op = getOperator(uid);
        return op == Operator.ADMIN || op == Operator.EDITOR;
    }

    /** Check whether a user has consented (i.e. exists as a non-UNKNOWN account). */
    public boolean hasConsented(String uid) {
        return getOperator(uid) != Operator.UNKNOWN;
    }

    /**
     * Verify that the account exists and its ops field matches the required value.
     * Returns the account if valid, empty if not.
     */
    public Optional<Account> requireOperator(String uid, Operator required) {
        Optional<Account> opt = repos.accounts().query()
                .where("uid", uid)
                .findOne();
        if (opt.isPresent() && opt.get().getOps() == required.getValue()) {
            return opt;
        }
        return Optional.empty();
    }
}
