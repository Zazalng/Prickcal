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