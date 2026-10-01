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

import io.github.zazalng.prickcal.global.contract.operator.Operator;
import io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostleColor;
import io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostlePosition;
import io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostleRace;
import io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats;
import io.github.zazalng.prickcal.global.dto.ApostleSearch;
import io.github.zazalng.prickcal.global.entities.*;
import io.github.zazalng.prickcal.global.manager.*;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    private final ManagerFactory factory;
    private final AccountManager accountManager;
    private final ApostleManager apostleManager;
    private final SessionManager sessionManager;

    private final String btnPrefix;
    private final String modalPrefix;
    private final String stringMenuPrefix;

    /**
     * Creates the panel builder for one plugin schema.
     * <p>
     * The three prefixes are the schema-scoped bases that every custom ID this builder emits is prefixed with,
     * which is what lets the handlers in the {@code handler} package route an interaction back to a panel.
     *
     * @param factory          the manager factory used to resolve the account, apostle and session managers
     * @param btnPrefix        the prefix placed on every button custom ID
     * @param modalPrefix      the prefix placed on every modal custom ID
     * @param stringMenuPrefix the prefix placed on every string select custom ID
     */
    public PanelBuilder(ManagerFactory factory, String btnPrefix, String modalPrefix, String stringMenuPrefix) {
        this.factory = factory;
        this.btnPrefix = btnPrefix;
        this.modalPrefix = modalPrefix;
        this.stringMenuPrefix = stringMenuPrefix;
        accountManager = factory.getManager(ManagersEnum.ACCOUNT);
        apostleManager = factory.getManager(ManagersEnum.APOSTLE);
        sessionManager = factory.getManager(ManagersEnum.SESSION);
    }

    // ==================== CONSENT PANEL ====================

    /**
     * Builds the first-run consent notice panel.
     * <p>
     * A static text-only container: the data transparency notice followed by the {@code consent_agree} and
     * {@code consent_disagree} buttons. Takes no user state, so it is the same for every user.
     *
     * @return a container holding the notice and the two consent buttons
     */
    public Container buildConsentPanel() {
        return Container.of(
                TextDisplay.of("# 🙏 Consent & Data Transparency Notice - v1.0.0"),
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

    /**
     * Builds the main menu summary embed shown beside the control panel.
     * <p>
     * Renders the account's in-game name, apostle and CP counters, a per-stat crayon field for every
     * {@link CrayonStats} constant with the total flushed in at {@code UNKNOWN}, the candy spend/acquired/rate
     * trio and the first date of record. The embed colour is randomised on each build, so two calls for the
     * same account do not look identical.
     *
     * @param discordUser the Discord user the panel belongs to, used for the title fallback and avatar
     * @param account     the account whose progression is summarised
     * @return the summary embed
     */
    public MessageEmbed buildMainMenuEmbed(User discordUser, Account account) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(account.getIgn() != null ? account.getIgn() + " (%s)".formatted(discordUser.getName()) : "%s".formatted(discordUser.getName()))
                .setDescription("""
                Consent at <t:%s:R>
                Apostle: %d out of %d |  CP: %d
                """.formatted(
                account.getCreatedAt().getEpochSecond(),
                accountManager.getApostleOwned(account),
                apostleManager.listAll().size(),
                account.getCp()
                ))
                .setThumbnail(discordUser.getEffectiveAvatarUrl())
                .setFooter("Friend Code: %s".formatted(account.getFriendCode() != null ? account.getFriendCode() : "*null*"))
                .setTimestamp(account.getUpdatedAt())
                .setColor(new Color(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)));

        {
            int crayonSpend = 0;
            int crayonPossible = 0;
            for (CrayonStats stats : CrayonStats.values()) {
                if (stats == CrayonStats.UNKNOWN) {
                    embed.addField("Crayon Spent", "%d / %d".formatted(crayonSpend, crayonPossible), true);
                    continue;
                }

                String[] tracker = accountManager.crayonCountByStats(account, stats).split(",", 2);
                String[] possible = apostleManager.crayonTotalByStats(stats).split(",", 2);

                crayonSpend += Integer.parseInt(tracker[1]);
                crayonPossible += Integer.parseInt(possible[1]);

                embed.addField(stats.getName(),
                        "%s / %s".formatted(tracker[0], possible[0]),
                        true);
            }
        }

        {
            BigDecimal candySpend = accountManager.getCrayonsSpent(account);
            BigDecimal crayonAcquired = accountManager.getCrayonsAcquired(account);
            BigDecimal crayonRate;
            {
                BigDecimal denominator = candySpend.divide(BigDecimal.valueOf(20), 0, RoundingMode.UP);
                crayonRate = denominator.signum() == 0 ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : crayonAcquired.divide(denominator, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100.00")).setScale(2, RoundingMode.HALF_UP);
            }
            embed.addField("Candy Spent", candySpend.toPlainString(), true)
                    .addField("Crayon Acquired", crayonAcquired.toPlainString(), true)
                    .addField("Crayon Rate", "%s%%".formatted(crayonRate.toPlainString()), true)
                    .addField("First Date of Record", "<t:%d:R>".formatted(accountManager.getFirstDateOfRecord(account).getEpochSecond()), false);
        }

        return embed.build();
    }

    /**
     * Builds the main menu control panel.
     * <p>
     * Every button is prefixed with the button prefix. The database and administrator buttons are rendered
     * disabled unless the account is actionable at the required {@link Operator} level, but the click itself is
     * still re-checked by the handler.
     *
     * @param account the account whose permissions decide which buttons are disabled
     * @return a container holding the main menu rows
     */
    public Container buildMainMenuComponent(Account account) {
        return Container.of(
                TextDisplay.of("# 🎮 Prickcal Control Panel"),
                Separator.create(true, Separator.Spacing.SMALL),
                ActionRow.of(
                        Button.primary(btnPrefix + "apostle", "📜 Apostle"),
                        Button.primary(btnPrefix + "crayon", "🖍️ Crayon"),
                        Button.primary(btnPrefix + "profile", "👤 Profile")
                ),
                ActionRow.of(
                        Button.secondary(btnPrefix + "logs", "📋 Public Logs"),
                        Button.secondary(btnPrefix + "database", "🗄️ Database").withDisabled(!account.isActionable(Operator.EDITOR)),
                        Button.secondary(btnPrefix + "administrator", "🔧 Administrator").withDisabled(!account.isActionable(Operator.ADMIN))
                ),
                ActionRow.of(
                        Button.secondary(btnPrefix + "import_export", "📦 Import/Export"),
                        Button.success(btnPrefix + "post_profile", "📢 Post Profile")
                )
        ).withAccentColor(ACCENT_MAIN);
    }

    // ==================== APOSTLE PANEL ====================

    /**
     * Builds the crayon tracking embed for one apostle of one account.
     * <p>
     * Shows the apostle's personality, race and seat, its star progress (or "Not Owned" below init), and one
     * field per house of the resolved {@link CrayonLineUp} marked acquired or not, followed by crayons spent
     * against the lineup's total cost. The house fields are omitted when the apostle has no lineup.
     *
     * @param account the account whose track is rendered
     * @param apostle the apostle being rendered
     * @return the tracking embed
     */
    public MessageEmbed buildTrackEmbed(Account account, Apostle apostle) {
        ApostleTrack track = apostleManager.findTrack(account, apostle);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(apostle.trueName());
        embed.setDescription("""
                🎭: %s
                🚩: %s
                🏹: %s
                ⭐: %s
                """.formatted(
                ApostleColor.fromNo(apostle.getColor()).getPersonality(),
                ApostleRace.fromNo(apostle.getRace()).getName(),
                ApostlePosition.fromNo(apostle.getPosition()).getSeat(),
                track.getCurrentStar() < apostle.getInit() ? "Not Owned" :
                        "%d / %d%s".formatted(
                                track.getCurrentStar(),
                                apostle.getMax(),
                                apostle.missingPiece(track)
                        )
        ));

        if (apostle.getPic() != null) {
            embed.setThumbnail(apostle.getPic());
        }
        embed.setFooter("Last Updated");
        embed.setTimestamp(track.getUpdatedAt());
        embed.setColor(ApostleColor.fromNo(apostle.getColor()).getColor());

        CrayonLineUp lineUp = apostleManager.findLineUp(apostle);
        if (lineUp != null) {
            List<Short> lineUpValues = lineUp.getLineUp();
            List<Boolean> crayonValues = track.getCrayons();

            String[] houseLabels = {
                    "House 1A", "House 1B", "House 2A", "House 2B", "House 2C",
                    "House 3A", "House 3B", "House 3C", "House 3D"
            };

            for (int i = 0; i < lineUpValues.size() && i < houseLabels.length; i++) {
                short statValue = lineUpValues.get(i);
                CrayonStats stat = CrayonStats.fromNo(statValue);
                boolean acquired = i < crayonValues.size() && crayonValues.get(i);
                String fieldValue = stat != CrayonStats.UNKNOWN
                        ? (stat.getName() + " - " + (acquired ? "✅" : "❌"))
                        : "Invalid Stat";
                embed.addField(houseLabels[i], fieldValue, true);
            }
            embed.addField("Crayons Spent", track.totalSpent(lineUp.getDepth()) + " / " + lineUp.totalCost(), false);
        }

        return embed.build();
    }

    /**
     * Builds the public, account-independent apostle embed.
     * <p>
     * Carries the apostle's identity, release status, hashtags and, when a {@link CrayonLineUp} exists, the
     * stat required by each house plus the total crayons and certificate pieces still needed.
     *
     * @param apostle the apostle being rendered
     * @return the public apostle embed
     */
    public MessageEmbed buildApostleEmbed(Apostle apostle) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(apostle.trueName())
                .setFooter("Last Updated")
                .setTimestamp(apostle.getUpdatedAt())
                .setColor(ApostleColor.fromNo(apostle.getColor()).getColor())
                .setDescription("""
                🎭: %s
                🚩: %s
                🏹: %s
                🌸: %s
                ### Hashtag
                %s
                """.formatted(
                ApostleColor.fromNo(apostle.getColor()).getPersonality(),
                ApostleRace.fromNo(apostle.getRace()).getName(),
                ApostlePosition.fromNo(apostle.getPosition()).getSeat(),
                apostle.getMax() > 5 ? "✅ Has Release." : "❌ Not Release.",
                apostleManager.parseHashTag(apostle)
        ));
        if (apostle.getPic() != null) {
            embed.setThumbnail(apostle.getPic());
        }
        CrayonLineUp lineUp = apostleManager.findLineUp(apostle);
        if (lineUp != null) {
            List<Short> lineUpValues = lineUp.getLineUp();

            String[] houseLabels = {
                    "House 1A", "House 1B", "House 2A", "House 2B", "House 2C",
                    "House 3A", "House 3B", "House 3C", "House 3D"
            };

            for (int i = 0; i < lineUpValues.size() && i < houseLabels.length; i++) {
                short statValue = lineUpValues.get(i);
                CrayonStats stat = CrayonStats.fromNo(statValue);
                String fieldValue = stat != CrayonStats.UNKNOWN
                        ? (stat.getName())
                        : "Invalid Stat";
                embed.addField(houseLabels[i], fieldValue, true);
            }
            embed.addField("Crayons Needed", String.valueOf(lineUp.totalCost()), false)
                    .addField("Certificate Needed", apostle.missingPiece(), true);
        }


        return embed.build();
    }

    /**
     * Builds the crayon grid control panel for one apostle.
     * <p>
     * This is the one builder that writes session state: when the account has no staged track it seeds one from
     * {@link ApostleManager#findTrack(Account, Apostle)}, so the toggles always have nine house flags to draw.
     * Each house button is prefixed {@code crayon_toggle_<index>}, and the panel also carries the star
     * increase/decrease, reset, confirm, switch, post and back buttons.
     *
     * @param account the account whose staged track the grid edits
     * @param apostle the apostle the grid belongs to
     * @return a container holding the nine toggle buttons and the panel action rows
     */
    public Container buildApostleComponent(Account account, Apostle apostle) {
        String[] houseLabels = {"1A", "1B", "2A", "2B", "2C", "3A", "3B", "3C", "3D"};

        if (sessionManager.getApostleTrackState(account.getId()) == null) {
            sessionManager.setApostleTrackState(account.getId(), apostleManager.findTrack(account, apostle));
        }

        ApostleTrack state = sessionManager.getApostleTrackState(account.getId());

        ActionRow row1 = buildCrayonRow(0, 2, state.getCrayons(), houseLabels);
        ActionRow row2 = buildCrayonRow(2, 5, state.getCrayons(), houseLabels);
        ActionRow row3 = buildCrayonRow(5, 9, state.getCrayons(), houseLabels);

        return Container.of(
                TextDisplay.of("### 🖍️ Crayon Grid — " + apostle.trueName()),
                Separator.create(true, Separator.Spacing.SMALL),
                row1,
                row2,
                row3,
                TextDisplay.of("### %s".formatted(state.printStar(apostle))),
                Separator.create(true, Separator.Spacing.SMALL),
                ActionRow.of(
                        Button.danger(btnPrefix + "apostle_increase_false", "➖⭐"),
                        Button.success(btnPrefix + "apostle_increase_true", "➕⭐"),
                        Button.secondary(btnPrefix + "crayon_reset", "🔄 Reset")
                ),
                ActionRow.of(
                        Button.success(btnPrefix + "crayon_confirm", "✅ Confirm"),
                        Button.primary(btnPrefix + "apostle_switching", "🔀 Switch Apostle")
                ),
                ActionRow.of(
                        Button.secondary(btnPrefix + "post_apostle", "📢 Post Apostle"),
                        Button.secondary(btnPrefix + "post_tracker", "📢 Post Tracking")
                ),
                ActionRow.of(
                        Button.primary(btnPrefix + "back_main", "⬅️ Back")
                )
        ).withAccentColor(ApostleColor.fromNo(apostle.getColor()).getColor());
    }

    /**
     * Builds the profile editor panel.
     * <p>
     * Displays the account's operator level, IGN, friend code and crayon format, and offers an edit button per
     * section, the {@code profile_leak} toggle (styled by its current state), an upload button enabled only for
     * {@link Operator#DEV}, and the delete-data entry point.
     *
     * @param account the account being displayed and edited
     * @return a container holding the profile rows
     */
    public Container buildProfileComponent(Account account) {
        return Container.of(
                TextDisplay.of("### Profile Editor — <@%s>".formatted(account.getUid())),
                Separator.create(true, Separator.Spacing.SMALL),
                TextDisplay.of("""
                        Operator: %s
                        IGN: `%s`
                        Friend Code: `%s`
                        Crayon Formatted: `%s`
                        
                        ### Ability
                        %s%s
                        """.formatted(
                        Operator.fromValue(account.getOps()).name(),
                        account.getIgn(),
                        account.getFriendCode(),
                        account.getCrayonFormat(),
                        Operator.fromValue(account.getOps()).getAbilities(),
                        account.isLeak() ? "\n- Leak Content Visibility" : ""
                )),
                Separator.create(true, Separator.Spacing.SMALL),
                ActionRow.of(
                        Button.primary(btnPrefix + "profile_ign", "🪶 IGN"),
                        Button.primary(btnPrefix + "profile_code", "🪶 Friend Code"),
                        Button.primary(btnPrefix + "profile_format", "🪶 Crayon Format"),
                        account.isLeak() ? Button.danger(btnPrefix + "profile_leak", "Leak?") : Button.success(btnPrefix + "profile_leak", "Leak?")
                ),
                ActionRow.of(
                        Button.primary(btnPrefix + "upload_pdf", "Upload Templated").withDisabled(!account.isActionable(Operator.DEV))
                ),
                ActionRow.of(
                        Button.secondary(btnPrefix + "back_main", "⬅️ Back"),
                        Button.danger(btnPrefix + "delete_data", "🗑️ Delete Data")
                )
        ).withAccentColor(ACCENT_APOSTLE);
    }

    /**
     * Builds one row of the crayon grid as toggle buttons.
     * <p>
     * Each button is styled by whether its house is acquired, and carries {@code crayon_toggle_<i>} as its
     * custom id so the button handler can flip that exact flag.
     *
     * @param start  the first house index to include, inclusive
     * @param end    the house index to stop before, exclusive
     * @param state  the acquired flags being rendered
     * @param labels the house labels indexed by house number
     * @return a single action row of house toggle buttons
     */
    private ActionRow buildCrayonRow(int start, int end, List<Boolean> state, String[] labels) {
        List<Button> buttons = new ArrayList<>();
        for (int i = start; i < end; i++) {
            boolean acquired = state.get(i);
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

    // ==================== LOGS PANEL ====================

    /**
     * Builds the public logs panel from the most recent {@link Log} rows.
     * <p>
     * Each entry is rendered as a relative timestamp with its action and table name; an empty or null list
     * renders a "no recent activity" note instead.
     *
     * @param recentLogs the log rows to display, newest first
     * @return a container holding the log listing
     */
    public Container buildLogsPanel(List<Log> recentLogs) {
        StringBuilder sb = new StringBuilder("# 📋 Public Logs\n\n");
        if (recentLogs == null || recentLogs.isEmpty()) {
            sb.append("_No recent activity._");
        } else {
            for (Log log : recentLogs) {
                sb.append("* <t:%s:R> `[%s - %s]` %s\n"
                        .formatted(
                                log.getCreatedAt(),
                                log.getAction(),
                                log.getTableName(),
                                log.getToString()
                        )
                );
            }
        }

        return Container.of(
                TextDisplay.of(sb.toString().stripTrailing()),
                Separator.create(true, Separator.Spacing.SMALL),
                ActionRow.of(
                        Button.secondary(btnPrefix + "back_main", "⬅️ Back")
                )
        ).withAccentColor(ACCENT_MAIN);
    }

    // ==================== SUB-PANELS ====================

    /**
     * Builds the database management panel.
     * <p>
     * Currently a placeholder with only a back button; the caller is responsible for the permission check.
     *
     * @return a container holding the placeholder database panel
     */
    public Container buildDatabasePanel() {
        return Container.of(
                TextDisplay.of("# 🗄️ Database Management\n\n_Coming soon — database browsing & editing will be available in a future update._"),
                Separator.create(true, Separator.Spacing.SMALL),
                ActionRow.of(
                        Button.secondary(btnPrefix + "back_main", "⬅️ Back")
                )
        ).withAccentColor(ACCENT_MAIN);
    }

    /**
     * Builds the administrator panel.
     * <p>
     * Currently a placeholder with only a back button; the caller is responsible for the permission check.
     *
     * @return a container holding the placeholder administrator panel
     */
    public Container buildAdministratorPanel() {
        return Container.of(
                TextDisplay.of("# 🔧 Administrator Panel\n\n_Admin controls coming in a future update._"),
                Separator.create(true, Separator.Spacing.SMALL),
                ActionRow.of(
                        Button.secondary(btnPrefix + "back_main", "⬅️ Back")
                )
        ).withAccentColor(ACCENT_DANGER);
    }

    /**
     * Builds the irreversible data deletion confirmation panel.
     * <p>
     * States exactly which record types are removed and offers {@code delete_confirm} or {@code delete_cancel};
     * no deletion happens until the button handler runs.
     *
     * @return a container holding the confirmation warning and the two choices
     */
    public Container buildDeleteDataConfirmPanel() {
        return Container.of(
                TextDisplay.of("""
                        # 🗑️ Delete All Your Data
                        
                        ⚠️ **This action is irreversible!**
                        All your tracked data will be permanently removed:
                        * Apostle tracking records
                        * Crayon records
                        * Remarkable records
                        * Gift acquired records
                        * Your account profile
                        
                        Are you sure?"""),
                Separator.create(true, Separator.Spacing.SMALL),
                ActionRow.of(
                        Button.danger(btnPrefix + "delete_confirm", "✅ Yes, Delete Everything"),
                        Button.secondary(btnPrefix + "delete_cancel", "❌ Cancel")
                )
        ).withAccentColor(ACCENT_DANGER);
    }

    /**
     * Builds the import/export panel.
     * <p>
     * Currently a placeholder with only a back button.
     *
     * @return a container holding the placeholder import/export panel
     */
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

    /**
     * Builds the apostle list panel used to switch the session's current apostle.
     * <p>
     * Reads the session's {@link ApostleSearch}; when its result set is empty the full apostle list is loaded
     * into it. A single result short-circuits to {@link #buildApostleComponent(Account, Apostle)}, committing
     * that apostle as current and clearing the staged track and search. Otherwise it renders the current page of
     * a single-select string menu, adding {@code pagination:-1} and {@code pagination:1} options only when the
     * corresponding page exists, plus the deep search and back buttons.
     *
     * @param account the account whose session supplies the search and receives the selection
     * @return the apostle crayon panel when the search resolves to one apostle, otherwise the paging list panel
     */
    public Container buildApostleListPanel(Account account) {
        ApostleSearch apostleSearch = sessionManager.getApostleSearch(account.getId());

        List<Apostle> searchResult = apostleSearch.getSfResult();

        if (searchResult.isEmpty()) {
            apostleSearch.getSfResult().addAll(apostleManager.listAll());
        }

        if (searchResult.size() == 1) {
            Apostle apostle = searchResult.getFirst();

            sessionManager.setCurrentApostle(account.getId(), apostle);
            sessionManager.removeApostleTrackState(account.getId());
            sessionManager.removeApostleSearch(account.getId());

            return buildApostleComponent(account, apostle);
        }

        int startIndex = apostleSearch.getSfStartIndex();
        int endIndex = Math.min(
                apostleSearch.getSfEndIndex(),
                searchResult.size()
        );

        StringSelectMenu.Builder menu =
                StringSelectMenu.create(
                                stringMenuPrefix + "select_apostle"
                        )
                        .setPlaceholder("Select an Apostle to view")
                        .setRequiredRange(1, 1)
                        .setRequired(true);

        if (apostleSearch.hasPreviousPage()) {
            menu.addOption(
                    "🔼---Previous---🔼",
                    "pagination:-1"
            );
        }

        for (int i = startIndex; i < endIndex; i++) {
            Apostle apostle = searchResult.get(i);

            menu.addOption(
                    apostle.getName(),
                    String.valueOf(apostle.getId())
            );
        }

        if (apostleSearch.hasNextPage()) {
            menu.addOption(
                    "🔽---Next---🔽",
                    "pagination:1"
            );
        }

        return Container.of(
                TextDisplay.of(
                        "# 🔀 Switch Apostle\n" +
                                "Select an apostle to view its crayon grid:"
                ),
                Separator.create(
                        true,
                        Separator.Spacing.SMALL
                ),
                ActionRow.of(menu.build()),
                Separator.create(
                        true,
                        Separator.Spacing.SMALL
                ),
                ActionRow.of(
                        Button.primary(
                                btnPrefix + "deep_search_modal",
                                "🔍 Deep Search"
                        ),
                        Button.secondary(
                                btnPrefix + "back_main",
                                "⬅️ Back"
                        )
                )
        ).withAccentColor(ACCENT_SEARCH);
    }
}
