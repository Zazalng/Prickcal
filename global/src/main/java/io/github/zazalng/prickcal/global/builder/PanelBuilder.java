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
package io.github.zazalng.prickcal.global.builder;

import io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostleColor;
import io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostlePosition;
import io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostleRace;
import io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats;
import io.github.zazalng.prickcal.global.entities.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.List;

/**
 * Builds UI components for the Prickcal plugin.
 * Separates UI construction logic from business logic and event handling.
 */
public class PanelBuilder {

    private static final Color ACCENT_MAIN = new Color(88, 101, 242);
    private static final Color ACCENT_CONSENT = new Color(255, 193, 7);
    private static final Color ACCENT_APOSTLE = new Color(0, 200, 83);
    private static final Color ACCENT_DANGER = new Color(237, 66, 69);
    private static final Color ACCENT_SEARCH = new Color(156, 39, 176);

    private final String btnPrefix;
    private final String modalPrefix;
    private final String stringMenuPrefix;

    public PanelBuilder(String btnPrefix, String modalPrefix, String stringMenuPrefix) {
        this.btnPrefix = btnPrefix;
        this.modalPrefix = modalPrefix;
        this.stringMenuPrefix = stringMenuPrefix;
    }

    // ==================== CONSENT PANEL ====================

    public Container buildConsentPanel() {
        return Container.of(
                TextDisplay.of("# 🙏 Consent & Data Transparency Notice"),
                Separator.create(true, Separator.Spacing.SMALL),
                TextDisplay.of("""
                        Before we can track your **Trickcal progression**, we need your consent to collect and process the information described below.
                        We believe in building this **for the community, with the community**. To support transparency, certain resources are designated as **Public Resources**. Actions performed on these resources are recorded in a shared log that can be viewed by other users who have also consented to use this plugin.
                        
                        ## Public Resources
                        The following tables are considered Public Resources:
                        * `apostle`
                        * `apostle_reviews`
                        * `crayon_line_ups`
                        * `gift_codes`
                        * `hash_tags`
                        * `logs`
                        * `stage_gear_drops`
                        When you **CREATE, UPDATE, or DELETE** data in any Public Resource, your action may be recorded in the plugin's Logs table.
                        These logs may include your **Discord User ID**, the action performed, the affected resource, the information involved, and the timestamp. Public Resource activity and associated records may be visible to other users who have consented to use this plugin.
                        
                        ## Non-Public Resources
                        
                        For resources that are **not** designated as Public Resources, activity logs may still be recorded for debugging, security, and troubleshooting purposes.
                        These logs are intended to help the developer identify and resolve problems. We will avoid collecting or exposing unnecessary personal information through these logs.
                        
                        ## What We Collect
                        Depending on how you use the plugin, we may collect:
                        * **Discord User ID** — to identify your account and associate your progression with you.
                        * **Information you provide** — data you enter or submit through the plugin.
                        * **Trickcal progression data** — information you provide for tracking your progression.
                        * **Timestamps** — when your data or actions are created, updated, or deleted.
                        * **Action logs** — records of relevant actions performed through the plugin.
                        
                        ## How We Use Your Data
                        Your data may be used to:
                        * Display and maintain your personal Trickcal progression.
                        * Calculate completion percentages, statistics, and other progression information.
                        * Provide and improve community features.
                        * Diagnose bugs and technical problems.
                        * Maintain the reliability and security of the plugin.
                        
                        ## Your Rights
                        * **Delete your data:** You can request deletion of your stored data using the **Delete Data** button.
                        * **Withdraw consent:** You can withdraw your consent to data collection at any time.
                        * **Transparency:** Public Resource activity is recorded in a shared log so that participating community members can see how those resources are being modified.
                        * **Control:** You choose whether to use the plugin by granting or withholding your consent.
                        
                        ### Important
                        By selecting **I Agree**, you confirm that you have read and understood this notice and consent to the collection and use of your data as described above, including the logging and potential visibility of activity involving Public Resources.
                        """.stripIndent()),
                Separator.create(true, Separator.Spacing.SMALL),
                ActionRow.of(
                        Button.success(btnPrefix + "consent_agree", "✅ I Agree"),
                        Button.danger(btnPrefix + "consent_disagree", "❌ I Disagree")
                )
        ).withAccentColor(ACCENT_CONSENT);
    }

    // ==================== MAIN MENU ====================

    public MessageEmbed buildMainMenuEmbed(User discordUser, Account account,
                                           List<CrayonStats> crayonStats,
                                           CrayonRecord crayonRecord) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(discordUser.getName(), null, discordUser.getEffectiveAvatarUrl());
        embed.setTitle(account.getIgn() != null ? account.getIgn() : discordUser.getName());
        embed.setThumbnail(discordUser.getEffectiveAvatarUrl());
        String footer = "";
        if (account.getFriendCode() != null) footer += "Friend Code: " + account.getFriendCode();
        embed.setFooter(footer.isEmpty() ? null : footer);
        embed.setColor(new Color(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)));

        for (CrayonStats stats : crayonStats) {
            embed.addField(stats.getName(),
                    account.getUid() + "_own/" + "apostle_" + stats.getNo(),
                    true);
        }

        int totalCrayons = (crayonRecord != null) ? crayonRecord.getCrayon() : 0;
        int totalSpent = (crayonRecord != null) ? crayonRecord.getSpent() : 0;
        embed.addField("Total Crayons Used", String.valueOf(totalCrayons), true);
        embed.addField("Candy Spent", String.valueOf(totalSpent), true);

        return embed.build();
    }

    public Container buildMainMenuComponent() {
        return Container.of(
                TextDisplay.of("# 🎮 Prickcal Control Panel"),
                Separator.create(true, Separator.Spacing.SMALL),
                ActionRow.of(
                        Button.primary(btnPrefix + "apostle", "👤 Apostle"),
                        Button.secondary(btnPrefix + "import_export", "📦 Import/Export"),
                        Button.secondary(btnPrefix + "database", "🗄️ Database")
                ),
                ActionRow.of(
                        Button.danger(btnPrefix + "administrator", "🔧 Administrator"),
                        Button.secondary(btnPrefix + "logs", "📋 Public Logs"),
                        Button.success(btnPrefix + "public_post", "📢 Public Post")
                ),
                ActionRow.of(
                        Button.danger(btnPrefix + "delete_data", "🗑️ Delete Data")
                )
        ).withAccentColor(ACCENT_MAIN);
    }

    // ==================== APOSTLE PANEL ====================

    public MessageEmbed buildApostleEmbed(User discordUser, Account account,
                                          Apostle apostle, ApostleTrack track,
                                          CrayonLineUp lineUp) {
        EmbedBuilder embed = new EmbedBuilder();

        String authorText = (track != null ? "" : "Not Owning - ")
                + "Apostle of " + (account.getIgn() != null ? account.getIgn() : discordUser.getName());
        int currentStar = (track != null) ? track.getCurrentStar() : apostle.getInit();
        int maxStar = apostle.getMax();
        embed.setAuthor(authorText + " | " + currentStar + "/" + maxStar,
                null, discordUser.getEffectiveAvatarUrl());

        String title = apostle.getName();
        if (apostle.getElydn() != null && !apostle.getElydn().isEmpty()) {
            title += " (" + apostle.getElydn() + ")";
        }
        embed.setTitle(title);

        if (apostle.getPic() != null) {
            embed.setThumbnail(apostle.getPic());
        }
        embed.setFooter("Last Updated");

        if (track != null) {
            embed.setTimestamp(Instant.now());
        }

        ApostleColor apostleColor = ApostleColor.fromNo(apostle.getColor());
        embed.setColor(apostleColor.getColor());
        embed.setDescription("#" + (apostle.getHashtag() != null ? apostle.getHashtag() : "none"));

        if (lineUp != null && track != null) {
            List<Integer> lineUpValues = lineUp.getLineUp();
            List<Boolean> crayonValues = track.getCrayons();

            String[] houseLabels = {
                    "House 1A", "House 1B", "House 2A", "House 2B", "House 2C",
                    "House 3A", "House 3B", "House 3C", "House 3D"
            };

            int totalSlots = lineUpValues.size();
            for (int i = 0; i < totalSlots && i < houseLabels.length; i++) {
                int statValue = lineUpValues.get(i);
                CrayonStats stat = CrayonStats.fromNo(statValue);
                boolean acquired = i < crayonValues.size() && crayonValues.get(i);
                String fieldValue = stat != CrayonStats.UNKNOWN
                        ? (stat.getName() + " - " + (acquired ? "✅" : "❌"))
                        : "Invalid Stat";
                embed.addField(houseLabels[i], fieldValue, true);
            }

            long trueCount = 0;
            for (boolean b : crayonValues) if (b) trueCount++;
            embed.addField("Crayons Spent", trueCount + " / " + totalSlots, false);
        }

        return embed.build();
    }

    public Container buildApostleComponent(String userId, Apostle apostle,
                                           ApostleTrack track, CrayonLineUp lineUp,
                                           boolean[] toggleState) {
        List<Boolean> currentCrayons = (track != null) ? track.getCrayons() : Collections.nCopies(9, false);
        boolean[] effectiveState;

        if (toggleState != null) {
            effectiveState = toggleState;
        } else {
            effectiveState = new boolean[currentCrayons.size()];
            for (int i = 0; i < currentCrayons.size(); i++) {
                effectiveState[i] = currentCrayons.get(i);
            }
        }

        List<Integer> lineUpValues = (lineUp != null) ? lineUp.getLineUp()
                : Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0);

        String[] houseLabels = {"1A", "1B", "2A", "2B", "2C", "3A", "3B", "3C", "3D"};

        ActionRow row1 = buildCrayonRow(0, 3, lineUpValues, effectiveState, houseLabels);
        ActionRow row2 = buildCrayonRow(3, 6, lineUpValues, effectiveState, houseLabels);
        ActionRow row3 = buildCrayonRow(6, 9, lineUpValues, effectiveState, houseLabels);

        return Container.of(
                TextDisplay.of("### 🖍️ Crayon Grid — " + apostle.getName()),
                Separator.create(true, Separator.Spacing.SMALL),
                row1,
                row2,
                row3,
                Separator.create(true, Separator.Spacing.SMALL),
                ActionRow.of(
                        Button.success(btnPrefix + "crayon_confirm", "✅ Confirm"),
                        Button.secondary(btnPrefix + "crayon_reset", "🔄 Reset"),
                        Button.primary(btnPrefix + "switch_apostle", "🔀 Switch Apostle"),
                        Button.secondary(btnPrefix + "apostle_public_post", "📢 Public Post")
                )
        ).withAccentColor(ACCENT_APOSTLE);
    }

    private ActionRow buildCrayonRow(int start, int end, List<Integer> lineUpValues,
                                     boolean[] state, String[] labels) {
        List<Button> buttons = new ArrayList<>();
        for (int i = start; i < end && i < lineUpValues.size(); i++) {
            CrayonStats stat = CrayonStats.fromNo(lineUpValues.get(i));
            boolean acquired = i < state.length && state[i];
            String label = labels[i] + " " + (acquired ? "✅" : "⬜");
            String btnId = btnPrefix + "crayon_toggle_" + i;
            if (acquired) {
                buttons.add(Button.success(btnId, label));
            } else {
                buttons.add(Button.danger(btnId, label));
            }
        }
        return ActionRow.of(buttons);
    }

    // ==================== SWITCH APOSTLE MODAL ====================

    public String getSwitchApostleModalId() {
        return modalPrefix + "switch_apostle_search";
    }

    // ==================== DEEP SEARCH PANEL ====================

    public Container buildDeepSearchPanel(String userId, List<Apostle> results,
                                          int page, int pageSize) {
        int totalPages = (int) Math.ceil((double) results.size() / pageSize);
        int start = page * pageSize;
        int end = Math.min(start + pageSize, results.size());
        List<Apostle> pageResults = results.subList(start, end);

        StringBuilder sb = new StringBuilder();
        sb.append("# 🔍 Deep Search\n\n");
        if (results.isEmpty()) {
            sb.append("_No results found._");
        } else {
            sb.append("**Total Results:** ").append(results.size()).append("\n");
            sb.append("**Page:** ").append(page + 1).append("/").append(totalPages).append("\n\n");
            for (int i = 0; i < pageResults.size(); i++) {
                Apostle a = pageResults.get(i);
                ApostleColor color = ApostleColor.fromNo(a.getColor());
                sb.append("**").append(start + i + 1).append(".** ").append(a.getName());
                if (a.getElydn() != null && !a.getElydn().isEmpty()) {
                    sb.append(" (").append(a.getElydn()).append(")");
                }
                sb.append(" — ").append(color.getPersonality());
                sb.append("\n");
            }
        }

        StringSelectMenu.Builder colorMenu = StringSelectMenu.create(stringMenuPrefix + "deep_color")
                .setPlaceholder("Filter by Personality (Color)")
                .setRequiredRange(0, 1);
        for (ApostleColor c : ApostleColor.values()) {
            if (c != ApostleColor.UNKNOW) {
                colorMenu.addOption(c.getPersonality(), String.valueOf(c.getNo()));
            }
        }

        StringSelectMenu.Builder posMenu = StringSelectMenu.create(stringMenuPrefix + "deep_position")
                .setPlaceholder("Filter by Position")
                .setRequiredRange(0, 1);
        for (ApostlePosition p : ApostlePosition.values()) {
            if (p != ApostlePosition.UNKNOW) {
                posMenu.addOption(p.getSeat(), String.valueOf(p.getNo()));
            }
        }

        StringSelectMenu.Builder raceMenu = StringSelectMenu.create(stringMenuPrefix + "deep_race")
                .setPlaceholder("Filter by Race")
                .setRequiredRange(0, 1);
        for (ApostleRace r : ApostleRace.values()) {
            if (r != ApostleRace.UNKNOWN) {
                raceMenu.addOption(r.getName(), String.valueOf(r.getNo()));
            }
        }

        return Container.of(
                TextDisplay.of(sb.toString()),
                Separator.create(true, Separator.Spacing.SMALL),
                ActionRow.of(
                        Button.primary(btnPrefix + "deep_name_input", "🔤 Search by Name")
                ),
                Separator.create(false, Separator.Spacing.SMALL),
                ActionRow.of(colorMenu.build()),
                Separator.create(false, Separator.Spacing.SMALL),
                ActionRow.of(posMenu.build()),
                Separator.create(false, Separator.Spacing.SMALL),
                ActionRow.of(raceMenu.build()),
                Separator.create(true, Separator.Spacing.SMALL),
                ActionRow.of(
                        Button.secondary(btnPrefix + "deep_prev", "⬅️ Previous")
                                .withDisabled(page <= 0),
                        Button.secondary(btnPrefix + "deep_page", "📄 " + (page + 1) + "/" + totalPages)
                                .withDisabled(true),
                        Button.primary(btnPrefix + "deep_next", "Next ➡️")
                                .withDisabled(page >= totalPages - 1)
                ),
                ActionRow.of(
                        Button.success(btnPrefix + "deep_select", "✅ Select Result")
                                .withDisabled(results.size() != 1),
                        Button.danger(btnPrefix + "deep_cancel", "❌ Cancel")
                )
        ).withAccentColor(ACCENT_SEARCH);
    }

    // ==================== LOGS PANEL ====================

    public Container buildLogsPanel(List<Log> recentLogs) {
        StringBuilder sb = new StringBuilder("# 📋 Public Logs\n\n");
        if (recentLogs == null || recentLogs.isEmpty()) {
            sb.append("_No recent activity._");
        } else {
            for (Log log : recentLogs) {
                sb.append("• `[").append(log.getAction()).append("]` ")
                        .append(log.getTableName()).append(" - ")
                        .append(log.getToString()).append("\n");
            }
        }

        return Container.of(
                TextDisplay.of(sb.toString()),
                Separator.create(true, Separator.Spacing.SMALL),
                ActionRow.of(
                        Button.secondary(btnPrefix + "back_main", "⬅️ Back")
                )
        ).withAccentColor(ACCENT_MAIN);
    }

    // ==================== SUB-PANELS ====================

    public Container buildDatabasePanel() {
        return Container.of(
                TextDisplay.of("# 🗄️ Database Management\n\n_Coming soon — database browsing & editing will be available in a future update._"),
                Separator.create(true, Separator.Spacing.SMALL),
                ActionRow.of(
                        Button.secondary(btnPrefix + "back_main", "⬅️ Back")
                )
        ).withAccentColor(ACCENT_MAIN);
    }

    public Container buildAdministratorPanel() {
        return Container.of(
                TextDisplay.of("# 🔧 Administrator Panel\n\n_Admin controls coming in a future update._"),
                Separator.create(true, Separator.Spacing.SMALL),
                ActionRow.of(
                        Button.secondary(btnPrefix + "back_main", "⬅️ Back")
                )
        ).withAccentColor(ACCENT_DANGER);
    }

    public Container buildDeleteDataConfirmPanel() {
        return Container.of(
                TextDisplay.of("# 🗑️ Delete All Your Data\n\n" +
                        "⚠️ **This action is irreversible!**\n\n" +
                        "All your tracked data will be permanently removed:\n" +
                        "• Apostle tracking records\n" +
                        "• Crayon records\n" +
                        "• Remarkable records\n" +
                        "• Gift acquired records\n" +
                        "• Your account profile\n\n" +
                        "Are you sure?"),
                Separator.create(true, Separator.Spacing.SMALL),
                ActionRow.of(
                        Button.danger(btnPrefix + "delete_confirm", "✅ Yes, Delete Everything"),
                        Button.secondary(btnPrefix + "delete_cancel", "❌ Cancel")
                )
        ).withAccentColor(ACCENT_DANGER);
    }

    public Container buildImportExportPanel() {
        return Container.of(
                TextDisplay.of("# 📦 Import/Export\n\n_Coming in a future update._"),
                Separator.create(true, Separator.Spacing.SMALL),
                ActionRow.of(
                        Button.secondary(btnPrefix + "back_main", "⬅️ Back")
                )
        ).withAccentColor(ACCENT_MAIN);
    }

    // ==================== APOSTLE LIST PANEL (for switch) ====================

    public Container buildApostleListPanel(List<Apostle> allApostles) {
        StringSelectMenu.Builder menu = StringSelectMenu.create(stringMenuPrefix + "select_apostle")
                .setPlaceholder("Select an Apostle to view")
                .setRequiredRange(1, 1);

        for (Apostle a : allApostles) {
            String label = a.getName();
            if (label.length() > 100) label = label.substring(0, 97) + "...";
            menu.addOption(label, String.valueOf(a.getId()));
        }

        return Container.of(
                TextDisplay.of("# 🔀 Switch Apostle\nSelect an apostle to view its crayon grid:"),
                Separator.create(true, Separator.Spacing.SMALL),
                ActionRow.of(menu.build()),
                Separator.create(true, Separator.Spacing.SMALL),
                ActionRow.of(
                        Button.primary(btnPrefix + "deep_search_modal", "🔍 Deep Search"),
                        Button.secondary(btnPrefix + "back_main", "⬅️ Back")
                )
        ).withAccentColor(ACCENT_APOSTLE);
    }
}
