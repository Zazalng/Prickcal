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

/**
 * Parses a template format string containing specific placeholders against an input string.
 * <p>
 * The parser recognizes the following placeholders:
 * %dd – two‑digit day of month (01‑31)
 * %dm – two‑digit month (01‑12)
 * %dy – two‑ or four‑digit year
 * %cs – one or more digits representing candy spent
 * %ca – one or more digits representing crayons acquired
 * <p>
 * Each placeholder is replaced by its corresponding regular‑expression fragment while
 * literal text between tokens is escaped and included verbatim. The assembled pattern is
 * anchored at both ends and matched against the supplied input. On a successful match an
 * {@link Result} containing a map from each placeholder token to its captured substring is
 * returned wrapped in an {@code Optional}; otherwise an empty {@code Optional} is produced.
 * <p>
 * The {@code Result} provides read‑only access to the captured values via its
 * {@code values()} method.
 */
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

    /**
     * Matches an input string against a template format and extracts the captured placeholder values.
     * <p>
     * Each recognised token is substituted with its own regex fragment, every literal run between tokens is
     * {@link Pattern#quote(String)}-escaped and included verbatim, and the assembled pattern is anchored at both
     * ends. A format containing no token is therefore treated as a literal and only matches that exact text.
     * <p>
     * Malformed input is not an error here: a non-matching input simply yields an empty {@code Optional}. Range
     * and divisibility rules ({@code %dd} 01-31, {@code %dm} 01-12, a two-digit year, candies spent as a
     * multiple of 20) are also not enforced beyond the digit shape. Callers such as
     * {@link io.github.zazalng.prickcal.global.Prickcal#parsingRecordCrayon(Account, String, String)} must check
     * that all five tokens are present and that the values are semantically valid.
     * <p>
     * A token repeated in the format is captured more than once but collapses to a single map entry holding the
     * last occurrence, because the returned map is keyed by token.
     *
     * @param format the template containing the {@code %dd}, {@code %dm}, {@code %dy}, {@code %cs} and {@code %ca} tokens
     * @param input  the string to match, for example a Discord message body
     * @return a {@link Result} mapping each token to its captured substring, or {@link Optional#empty()} if the input does not match the format
     */
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

    /**
     * The captured values of a successful parse.
     * <p>
     * The map is keyed by the literal token text, for example {@code %dd}, in the order the tokens appear in the
     * format, and each value is the substring that token captured. A format with no tokens yields an empty map,
     * and a format missing some tokens yields a map that lacks those keys.
     *
     * @param values the token-to-captured-substring map produced by {@link #parse(String, String)}
     */
    public record Result(Map<String, String> values) {
    }
}