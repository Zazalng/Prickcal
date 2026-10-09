package io.github.zazalng.prickcal.global.util;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public final class TimeFormatUtil {
    private static final ZoneId ZONE = ZoneId.systemDefault();

    public static String of(Instant time, String format) {
        LocaleFormat obj = new LocaleFormat(format);

        return switch (obj.getType()) {
            case 'f' -> fullDateTime(time, obj);
            case 's' -> shortDateTime(time, obj);
            case 'd' -> shortDate(time, obj);
            case 'r' -> relativeTime(time, obj);
            default -> shortDate(time, obj);
        };
    }

    // Equivalent to :f (Short Date/Time) -> "October 7, 2026 9:44 AM"
    private static String shortDateTime(Instant time, LocaleFormat format) {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT)
                .withLocale(format.getLocale())
                .withZone(ZONE)
                .format(time);
    }

    // Equivalent to :F (Long Date/Time) -> "Wednesday, October 7, 2026 9:44 AM"
    private static String fullDateTime(Instant time, LocaleFormat format) {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.SHORT)
                .withLocale(format.getLocale())
                .withZone(ZONE)
                .format(time);
    }

    // Equivalent to :d (Short Date) -> "10/07/2026"
    private static String shortDate(Instant time, LocaleFormat format) {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                .withLocale(format.getLocale())
                .withZone(ZONE)
                .format(time);
    }

    private static String relativeTime(Instant target, LocaleFormat format) {
        Duration duration = Duration.between(Instant.now(), target);
        boolean isFuture = !duration.isNegative();
        duration = duration.abs();

        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long month = days / 30;

        String unit;
        if (month > 0) {
            unit = month + (month == 1 ? " month" : " months");
        } else if (days > 0) {
            unit = days + (days == 1 ? " day" : " days");
        } else if (hours > 0) {
            unit = hours + (hours == 1 ? " hour" : " hours");
        } else if (minutes > 0) {
            unit = minutes + (minutes == 1 ? " minute" : " minutes");
        } else {
            unit = seconds + (seconds == 1 ? " second" : " seconds");
        }

        Locale locale = format.getLocale();
        String language = locale.getLanguage();
        String prefix = switch (language) {
            case "th" -> isFuture ? "ในอีก " : "";
            case "ja" -> isFuture ? "あと " : "";
            default -> isFuture ? "in " : "";
        };
        String suffix = switch (language) {
            case "th" -> isFuture ? "" : " ที่แล้ว";
            case "ja" -> isFuture ? "" : " 前";
            default -> isFuture ? "" : " ago";
        };

        return prefix + unit + suffix;
    }

    public static final class LocaleFormat {
        private static final Locale DEFAULT_LOCALE = Locale.US;

        private final char type;
        private final Locale locale;

        public LocaleFormat(String format) {
            if (format == null || format.isBlank()) {
                type = 'd';
                locale = DEFAULT_LOCALE;
                return;
            }

            String[] parts = format.toLowerCase(Locale.ROOT).split(":", 2);

            type = switch (parts[0]) {
                case "s", "f", "d", "r" -> parts[0].charAt(0);
                default -> 'd';
            };

            locale = parts.length == 2
                    ? parseLocale(parts[1])
                    : DEFAULT_LOCALE;
        }

        private static Locale parseLocale(String value) {
            String[] parts = value.split("-", 2);

            if (parts.length != 2) {
                return DEFAULT_LOCALE;
            }

            return Locale.of(
                    parts[0].toLowerCase(Locale.ROOT),
                    parts[1].toUpperCase(Locale.ROOT)
            );
        }

        public char getType() {
            return type;
        }

        public Locale getLocale() {
            return locale;
        }
    }
}