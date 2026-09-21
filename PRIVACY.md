# Privacy Policy — Prickcal

**Version:** 1.0.0\
**Effective:** 2026-06-17\
**Operator:** World Standard Group (Napapon Kamanee)\
**Repository:** https://github.com/Zazalng/Prickcal \
**Contact:** zazalng.founder@worldstandard.group

---

## Overview

Prickcal ("the Plugin") is an open-source Discord plugin that helps you track your personal Trickcal in-game
progression. This policy explains what data the Plugin collects, how it is used, stored, and shared, and what rights you
have over your data.

The Plugin is built on top of the [Pudel Discord Bot](https://github.com/World-Standard-Group) and uses Pudel's built-in
database and plugin platform. Your use of Discord itself is additionally governed by Discord's Privacy Policy.

---

## 1. Data We Collect

### 1.1 Discord Account Information

- **Discord User ID** — used to identify your account, link your progression data to you, and display your profile
- **Discord Username / Display Name** — used in profile displays and audit logs when you perform actions on public
  resources

### 1.2 Information You Provide

| Category           | Data Collected                                                      | Purpose                                          |
|--------------------|---------------------------------------------------------------------|--------------------------------------------------|
| Profile            | In-Game Name (IGN), Friend Code                                     | Display on your profile embed                    |
| Crayon Records     | Candy spent, Crayons acquired, Record date, Optional screenshot URL | Track your crayon farming history                |
| Apostle Tracking   | Star rating, Acquired crayon toggles per house, Timestamps          | Track your Apostle collection progress           |
| Gift Codes         | Gift code strings, acquired status                                  | Community gift code registry                     |
| Remarkable Records | Remarkable data                                                     | Track remarkable from notice table of remarkable |
| Stage Gear Drops   | Stage gear drop records                                             | Community gear drop data                         |
| Hashtags           | Hashtag text, sentiment (pro/con/nature)                            | Community Apostle review system                  |

### 1.3 Automatically Collected Data

- **Timestamps** — creation and last-update timestamps on all records
- **Action Logs** — when you CREATE, UPDATE, or DELETE data in a Public Resource (see Section 2), the action type,
  affected table, affected data summary, and your Discord User ID may be recorded in the Plugin's audit log
- **Interaction Metadata** — which buttons, menus, or commands you interact with (used for debugging only; not stored
  long-term)

### 1.4 Data We Do NOT Collect

- Direct messages or message content outside the crayon recording feature (only processed to parse format tokens; not
  stored)
- Voice or video data
- IP addresses
- Browser fingerprints or device identifiers
- Location data (beyond the Discord client's server region metadata, which we do not store)
- Payment or financial information

---

## 2. Public Resources vs Non-Public Data

Some Plugin data tables are designated as **Public Resources**. These records are community-visible and actions on them
are logged in a shared audit log that can be viewed by any consented Plugin user.

| Public Resources   | Access Level                         |
|--------------------|--------------------------------------|
| `apostles`         | Community-visible (read)             |
| `crayon_line_ups`  | Community-visible (read)             |
| `gift_codes`       | Community-visible (read, contribute) |
| `hash_tags`        | Community-visible (read, contribute) |
| `stage_gear_drops` | Community-visible (read, contribute) |
| `logs`             | Community-visible (read only)        |

**Non-Public Data** (visible only to you and the Plugin operator):

- Apostle tracking progress
- Crayon records
- Accounts table (your consent state, operator level)

---

## 3. How We Use Your Data

- **Display your progression** — show your Apostle collection, crayon stats, and profile in control panels
- **Calculate statistics** — completion percentages, crayon acquisition rates, contribution points
- **Provide community features** — gift code sharing, hashtag reviews, stage gear drop aggregation
- **Audit and accountability** — public logs help the community see how shared resources are being modified
- **Debugging and security** — diagnose bugs, prevent abuse, maintain reliability (limited, short-lived access)
- **Improve the Plugin** — aggregated usage patterns help prioritize features

---

## 4. Data Storage and Security

### 4.1 Storage Location

Data is stored in Pudel's plugin database (hosted on the server where the Pudel Discord Bot runs). The database
infrastructure is operated by the hosting provider.

### 4.2 Retention

We retain your data until:

- You delete it through the Plugin's "Delete Data" button (instant removal)
- The Plugin is uninstalled on the hosting provider (all associated data is removed)

Log records in Public Resources may persist after your account deletion to maintain audit trail integrity. These logs
contain your Discord User ID but no other personal data.

### 4.3 Security Measures

- Database access is limited to the Pudel bot process and authorized administrators
- No external network access to the database is exposed
- The Plugin stores only data necessary for its functioning
- Discord User IDs stored in logs are not accessible outside the Plugin context

---

## 5. Data Sharing

**We do not sell your personal data.**

We may share data only in the following circumstances:

- **Within Discord** — your progression data is displayed within the Discord interface to you and (for public resources)
  to other consented users
- **With Discord** — Discord has access to all data that passes through its platform per Discord's Privacy Policy
- **Legal requirement** — if required by applicable law or valid legal process

---

## 6. Your Rights

You have the following rights regarding your data:

| Right                | How to Exercise                                                       |
|----------------------|-----------------------------------------------------------------------|
| **Access**           | View your data through the Plugin's profile and statistics panels     |
| **Rectification**    | Update your IGN, friend code, and crayon format through the Plugin UI |
| **Deletion**         | Use the "Delete Data" button in the Plugin's control panel            |
| **Withdraw Consent** | Do not consent when prompted; or contact the operator to revoke       |
| **Export**           | Request a data export at zazalng.founder@worldstandard.group          |
| **Object**           | If you object to any data use, withdraw consent and request deletion  |

To exercise any right, contact: **zazalng.founder@worldstandard.group**

---

## 7. Children's Privacy

The Plugin is not directed at children under 13. Discord's Terms of Service require users to be at least 13 years old.
If we learn that a user under 13 has provided personal data without parental consent, we will delete it promptly.

---

## 8. Changes to This Policy

We may update this Privacy Policy as the Plugin evolves. Material changes will be announced through the Plugin's Discord
interface or the GitHub repository's release notes. The "Effective" date at the top of this policy will reflect the most
recent change.

---

## 9. Contact

For questions, concerns, or data requests:

- **Email:** zazalng.founder@worldstandard.group
- **Project:** https://github.com/Zazalng/Prickcal
- **Operator:** World Standard Group — Napapon Kamanee

---

## Related Documents

- [Terms of Use](./TERMS_OF_USE.md)
- [LICENSE (AGPL v3.0)](./LICENSE)