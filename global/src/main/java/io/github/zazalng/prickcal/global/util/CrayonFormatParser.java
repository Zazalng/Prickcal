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
package io.github.zazalng.prickcal.global.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CrayonFormatParser {
    private static final Map<String, String> TOKENS = Map.of(
            "%dd", "(0[1-9]|[12]\\d|3[01])",
            "%dm", "(0[1-9]|1[0-2])",
            "%dy", "(\\d{2}|\\d{4})",
            "%cs", "(\\d+)",
            "%ca", "(\\d+)"
    );

    private static final Pattern TOKEN_PATTERN =
            Pattern.compile("%(?:dd|dm|dy|cs|ca)");

    public static Optional<Result> parse(String format, String input) {
        Matcher tokenMatcher = TOKEN_PATTERN.matcher(format);

        StringBuilder regex = new StringBuilder("^");
        List<String> tokens = new ArrayList<>();

        int lastEnd = 0;

        while (tokenMatcher.find()) {
            // Literal text between tokens.
            regex.append(Pattern.quote(
                    format.substring(lastEnd, tokenMatcher.start())
            ));

            String token = tokenMatcher.group();

            regex.append(TOKENS.get(token));
            tokens.add(token);

            lastEnd = tokenMatcher.end();
        }

        // Remaining literal text.
        regex.append(Pattern.quote(format.substring(lastEnd)));
        regex.append("$");

        Matcher matcher = Pattern.compile(regex.toString()).matcher(input);

        if (!matcher.matches()) {
            return Optional.empty();
        }

        Map<String, String> values = new LinkedHashMap<>();

        for (int i = 0; i < tokens.size(); i++) {
            values.put(tokens.get(i), matcher.group(i + 1));
        }

        return Optional.of(new Result(values));
    }

    public record Result(Map<String, String> values) {
    }
}