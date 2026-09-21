# Prickcal

A Pudel Discord Bot plugin for personally tracking Trickcal progression.

**Version:** 1.0.0 \
**Author:** Zazalng (Napapon Kamanee) \
**License:** GNU Affero General Public License v3.0 \
**Repository:** https://github.com/Zazalng/Prickcal

---

## About

Prickcal (Trickcal for Pudel) is a plugin for the [Pudel Discord Bot](https://github.com/World-Standard-Group) that
provides interactive control panels for tracking in-game progression — including Apostle (character) collection, Crayon
(farming) records, gift codes, hashtags, and stages.

Built with Discord's Components v2 system (Container, TextDisplay, Separator, ActionRow) for a modern, embed-free UI.

---

## Features

- **Apostle Tracking** — Track owned apostles with a color-coded crayon grid, star rating, and per-attribute progress
- **Crayon Record Parsing** — Auto-detect and record crayon summons from user-defined message formats via context menu
  or `/prickcal` command
- **Public Logs** — Shared audit log for community-visible actions on public resources (apostles, gift codes, hashtags,
  etc.)
- **Profile Management** — IGN, friend code, operator level, and contribution points (CP)
- **Consent & Data Privacy** — Explicit consent panel with data transparency notice before tracking starts
- **Deep Search** — Filter apostles by name, race, color, and position
- **Context Menu Integration** — User context menu for quick profile lookup, message context menu for crayon recording
- **Post to Channel** — Share apostle profiles and tracking progress as public messages

---

## Modules

This multi-module Maven project contains:

| Module     | Description                                                                                 |
|------------|---------------------------------------------------------------------------------------------|
| **global** | Main plugin with full feature set — commands, handlers, managers, entities, and UI builders |
| **korea**  | Korea-specific variant/extension of the plugin (in-development)                             |

---

## Commands and Interactions

### Slash Command

| Command     | Description                                                                                        |
|-------------|----------------------------------------------------------------------------------------------------|
| `/prickcal` | Opens the main control panel. Optional `string_record` parameter for direct crayon recording input |

### Context Menu Commands

| Menu Type | Name          | Description                                                       |
|-----------|---------------|-------------------------------------------------------------------|
| User      | View Record   | Shows a user's tracked profile embed (ephemeral)                  |
| Message   | Crayon Record | Parses a crayon summon record from a message's content/attachment |

### Control Panel Buttons

From the main panel, users can navigate to:

- **Apostle** — Browse and track apostles with interactive crayon toggle grid
- **Crayon** — View user crayon statistics (missing / finding / tracking)
- **Profile** — Manage IGN, friend code, operator level
- **Database** — Browse/edit database tables (coming soon)
- **Public Logs** — View community audit logs
- **Administrator** — Admin controls (coming soon)
- **Import/Export** — Data import/export (coming soon)
- **Post Profile** — Share profile publicly
- **Delete Data** — Permanently remove all tracked data

---

## Architecture

The plugin follows a layered architecture:

```
@Plugin (Prickcal.java)
  ├── Handlers (PrickcalButtonHandler, PrickcalModalHandler, PrickcalSelectMenuHandler)
  │     └── Thin event routers → delegate to managers
  ├── Managers (AccountManager, ApostleManager, SessionManager)
  │     └── Business logic, repository access, audit logging
  ├── PanelBuilder
  │     └── Pure UI construction (no business logic)
  └── RepositoryProvider
        └── Data access via Pudel API repositories
```

### Data Model (Entity Tables)

| Entity            | Table                | Type            |
|-------------------|----------------------|-----------------|
| Account           | `accounts`           | Non-public      |
| Apostle           | `apostles`           | Public Resource |
| ApostleRemarkable | `apostles_reviews`   | Public Resource |
| ApostleTrack      | `apostle_tracks`     | Non-public      |
| CrayonLineUp      | `crayon_line_ups`    | Public Resource |
| CrayonRecord      | `crayon_records`     | Non-public      |
| GiftCode          | `gift_codes`         | Public Resource |
| GiftAcquired      | `gift_acquired`      | Non-public      |
| Hashtag           | `hash_tags`          | Public Resource |
| RemarkableRecord  | `remarkable_records` | Non-public      |
| StageGearDrop     | `stage_gear_drops`   | Public Resource |
| Log               | `logs`               | Public Resource |

Actions performed on **Public Resources** are recorded in a shared audit log visible to all consented users.

---

## Crayon Format

Users define a custom message format for auto-detecting crayon farming. The format uses these tokens:

| Token | Meaning                                         | Example        |
|-------|-------------------------------------------------|----------------|
| `%dd` | Day of month (01-31)                            | `15`           |
| `%dm` | Month (01-12)                                   | `03`           |
| `%dy` | Year (2 or 4 digits; 2-digit gets +2000)        | `25` or `2025` |
| `%cs` | Candy spent (must be >= 20 and divisible by 20) | `200`          |
| `%ca` | Crayons acquired                                | `8`            |

Default format: `%dd/%dm/%dy %cs %ca`

All five tokens are required for a valid format.

---

## Build

**Requirements:** JDK 25+, Maven 3+

```bash
mvn clean package
```

The `global` module produces a shaded JAR ready for deployment to Pudel's `plugins/` directory.

---

## License

This project is licensed under the **GNU Affero General Public License v3.0**. See [LICENSE](./LICENSE) for details.