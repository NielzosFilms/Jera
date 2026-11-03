# Jera ᛃ — Design Document

> A personal, self-hosted, Norse-themed pixel-art RPG that happens to track your life.
> Status: draft v0.2 (2026-11-03) — design only, nothing built yet.
> Host: `jera.othala` (Raspberry Pi "Othala").

---

## 1. Vision

**Jera** (ᛃ) is the rune of the year and the harvest: *reward that comes from patient, consistent effort over time*. That is the app.

A **game first, habit tracker second**. Real-life activities (climbing, guitar, gaming, maintenance, social events, sleep, big work projects) earn XP, grow branches of your Yggdrasil, raise your global rank, slay mythical foes, drop hoards of loot and unlock real-world rewards.

Seeing habits laid out as a character sheet is the point: visibility improves behaviour on its own, the game layer makes it fun to keep looking.

### Design principles

1. **Minimal administration.** The app suggests, the user confirms. Logging happens in one ~2 minute evening ritual, not throughout the day.
2. **Game feel from day one.** Even the MVP has a payoff screen, level-up animations and pixel art. A plain form will be abandoned.
3. **The activity log is the source of truth.** XP, levels, silver, foe HP are all *derived*. Balancing can change at any time and history is recomputed.
4. **Consequences, but forgiving.** Missing things costs something, but there are always legitimate ways to skip or catch up. You never lose levels.
5. **Data-driven content.** Skills, foes, items, runestones and sprites are data, not code. New content = new row + PNG, no redeploy.
6. **Theme is a presentation layer.** Code and domain model use plain English names (`Boss`, `Quest`, `Gold`); Norse names live in the UI/i18n layer (see §2). Keeps code readable and the theme swappable.
7. **Single user now, multi-user ready.** Every table carries `user_id`; no social features yet.

---

## 2. Theme and naming

Norse mythology, rendered in pixel art. The tone is playful-epic, not grimdark.

### 2.1 Glossary (UI name ↔ domain name)

| UI (Norse) | Domain / code | Notes |
|---|---|---|
| **The Saga** | `DailyReview` | The evening review: "write today's saga". Notification: *"The fire is lit. Tell your saga."* |
| **Feast of Glory** | Payoff screen | XP, level-ups, foe damage, loot after completing the Saga |
| **Yggdrasil** | Skill tree | Skills are branches, sub-skills are twigs; the tree visibly grows with levels |
| **Runestones** | `Achievement` | Each unlock is "carved" as a runestone in the Runestone Field |
| **Foes** | `Boss` | Mythical creatures, see §8 |
| **Saga of Slain Foes** | Bestiary | Collection of defeated foes |
| **Sagas** (quests) | `Quest` | Daily = *Chore*, Weekly = *Deed*, Monthly = *Saga Quest*, System = *Omen* |
| **Raids** | `Dungeon` | Multi-day projects with stages |
| **The Forge** | Garage / `Gear` | Vehicles and gear with durability |
| **Mead Hall** | Tavern / game backlog | Gaming backlog and the quest giver |
| **The Völva** | Quest giver NPC | Seeress who "foresees" which game you should play |
| **Silver** (hacksilver) | `Gold` currency | Spent at the Market |
| **The Market** | `ShopReward` | Self-defined IRL rewards |
| **Oaths** | `Milestone` | One-time IRL rewards sworn at certain levels ("I swore new shoes at Climbing 15") |
| **Hoard** | Loot chest | Rarity colours as classic RPG tiers |
| **Algiz Ward** | Streak Freeze token | Algiz = rune of protection |
| **Mead** | XP potion | e.g. *Mead of Poetry*: 2× Music XP for a day |
| **Blessings / Curses** | `Buff` / debuff | e.g. *Blessing of Sól*, *Curse of Weariness* |
| **Frostbitten** (Isa ᛁ) | Rusty skill state | Skill branch covered in frost when untrained 30 days |
| **Rank** | Global rank | Thrall → Karl → Drengr → Huscarl → Jarl → Einherjar |
| **Calling** | Emergent class | See §4.5 |

### 2.2 Visual direction

- Pixel art, 16×16 / 32×32 icons, 64×64+ for foes.
- Palette: dark wood, iron, firelight orange, frost blue, gold accents; parchment panels.
- Runes as UI ornaments (Elder Futhark), knotwork borders on panels.
- Fonts: a pixel font for UI (*Press Start 2P* / *Pixelify Sans*); optionally a rune-styled display font for headings only (readability first).
- Each skill on Yggdrasil gets a rune glyph as its icon (see §4.1).
- Asset sources: free Viking/Norse/fantasy packs on itch.io, Kenney (CC0), OpenGameArt, CraftPix freebies. Self-drawn: small item and reward icons.

---

## 3. Core loop

```
 day: live your life ── integrations collect signals (calendar, Steam, Oura, ActivityWatch, git)
        │
 evening (ntfy push: "The fire is lit. Tell your saga.")
        ▼
   THE SAGA (~2 min)
     1. Confirm suggestions ("Did you?")
     2. Quick-add / free text
     3. Mood + energy + one journal line
        ▼
   FEAST OF GLORY: XP per skill, level-ups, foe damage, hoard drops, streak
        │
 weekly/monthly: foe results, Deeds refresh, 30-day reflection at the Well of Mímir
```

### 3.1 The Saga (evening review)

Triggered by an ntfy notification at a configurable time (e.g. 21:30). Sections:

| Section | Content | Interaction |
|---|---|---|
| **Omens** (suggestions) | Calendar events of today, Steam playtime, Oura sleep, ActivityWatch focus time, git commits, due quests | Card per item: ✅ done / ❌ skipped / ✏️ adjust (duration, grade, etc.) |
| **Active raids** | Multi-day projects in progress | "Progress today?" → tick a stage |
| **Quick add** | Buttons for top activities + free-text field ("climbed, sent 6b x2, 30 min guitar") | Rule-based parser in v1, LLM later |
| **Journal** | Mood 1–5, energy 1–5, one line of text | Optional text, ratings required to complete |
| **"A quiet day"** | One tap to close an uneventful day | Still counts as a completed Saga |

Completing the Saga is itself rewarded (Mind XP, saga streak, small hoard chance).

### 3.2 Catch-up and skip rules

- A Saga can be **written up to 48 hours later** without any penalty (backfill).
- After 48h the day counts as **missed** (see §7 Consequences).
- **Rest / vacation days** can be declared in advance → day is skipped with no consequences (limited, e.g. 14 per year, or "vacation mode" which also pauses foes).
- **Algiz Wards** (streak freeze; earned, never bought) can retroactively cover a missed day.

---

## 4. Character model

Three layers:

```
 Global Level + Rank  ◀── total XP of all skills
      ▲
 Attributes (STR, DEX, END, INT, WIS, CHA, CRE)  ◀── weighted skill XP
      ▲
 Skills (Yggdrasil branches)  ◀── confirmed ActivityEvents via XP rules
```

### 4.1 Skills (initial set)

| Skill | Rune | Sub-skills | Main inputs | Attributes |
|---|---|---|---|---|
| **Climbing** | ᚢ Uruz (strength) | Bouldering, Sport climbing, Training | Sessions, sends with grades, rest days | STR, END, DEX |
| **Engineering** | ᚲ Kenaz (craft, knowledge) | Projects (raids), Learning | Work feature raids, courses, side projects | INT, CRE |
| **Gaming** | ᛈ Perthro (play, chance) | — | Intentional sessions, Steam achievements, finished games | DEX, WIS |
| **Music** | ᚹ Wunjo (joy) | Guitar | Practice minutes, songs learned (raids), techniques | DEX, CRE |
| **Mechanic** | ᚱ Raidho (journey, riding) | Car, Motorcycle | Maintenance chores, mods (raids) | INT, CRE |
| **Social** | ᚷ Gebo (gift, connection) | Events (concerts, music nights), Friends | Attended events, meetups | CHA |
| **Vitality** | ᛒ Berkano (growth, health) | Sleep | Oura sleep score, bedtime consistency | END, WIS |
| **Mind** | ᚨ Ansuz (wisdom, Odin) | Reflection | Completing Sagas, journaling | WIS |

Each skill has a **level rubric** (inspired by "Leveling Up in Real Life"): a short human description of what certain levels mean, shown next to the XP bar. Example Climbing: *"Lv 10: consistent 6a boulderer, climbs 2x/week"*.

Sub-skill XP rolls up to the parent skill (100%).

### 4.2 Attributes

| Attr | Meaning | Fed by (weight) |
|---|---|---|
| STR | Strength | Bouldering 0.6, Training 0.6 |
| DEX | Dexterity | Climbing 0.3, Guitar 0.5, Gaming 0.3 |
| END | Endurance | Sport climbing 0.6, Sleep 0.4 |
| INT | Intellect | Engineering 0.6, Mechanic 0.4 |
| WIS | Wisdom | Mind 0.7, Sleep 0.3, Gaming 0.2 |
| CHA | Charisma | Social 1.0 |
| CRE | Creativity | Guitar 0.4, Engineering 0.3, Mods 0.4 |

Weights are data and tunable. Attributes drive the radar-chart character sheet and can be requirements for unlocks (titles, items).

### 4.3 Levels, XP curve and ranks

All constants tunable in config.

- Skill: XP required to go from level `n` to `n+1` = `round(100 * n^1.5)`
- Global: XP required to go from level `n` to `n+1` = `round(400 * n^1.5)` over the sum of all skill XP
- Attribute value = `floor(sqrt(weighted XP / 50))` (grows slowly, feels like stat points)

Global **ranks**:

| Global level | Rank | Flavour |
|---|---|---|
| 1 | **Thrall** | Everyone starts somewhere |
| 10 | **Karl** | Free farmer, master of your own land |
| 20 | **Drengr** | Bold warrior, proven in deeds |
| 35 | **Huscarl** | Sworn elite household warrior |
| 50 | **Jarl** | Ruler of a domain |
| 75 | **Einherjar** | Chosen for Valhalla |

Each rank-up spawns a **rank trial** foe (§8).

### 4.4 Initial assessment (onboarding: "The Norns weigh your past")

To avoid starting a 6b climber at level 1, onboarding asks for current state per skill and seeds an `ActivityEvent` of type `INITIAL_ASSESSMENT` with the equivalent XP.

- Climbing: current max boulder / lead grade → mapped via a grade table (e.g. Font 5a ≈ Lv 4, 6a ≈ Lv 9, 6b ≈ Lv 12, 6c ≈ Lv 15, 7a ≈ Lv 19).
- Other skills: self-rated against the rubric (1–10 slider → level).

### 4.5 Calling (emergent class)

Never chosen; computed from the XP distribution of the last 90 days.

| Dominant skill(s) | Calling |
|---|---|
| Climbing | **Berserker** |
| Engineering | **Runesmith** |
| Music | **Skald** |
| Gaming | **Wanderer** (of the Nine Realms) |
| Mechanic | **Dwarf-smith** |
| Social | **Thane** |
| Vitality / Mind | **Seer** |
| Climbing + Engineering | **Battle-Runesmith** |
| Music + Social | **Court Skald** |
| Balanced across 4+ skills | **Allfather's Chosen** |

A calling grants a small passive (+5% XP in its skills). Purely flavour + small bonus.

---

## 5. XP rules per skill

XP rules map `ActivityType` + payload → XP for one or more skills. They are stored as data with a small typed rule engine (no scripting in v1).

### Climbing ᚢ
- Session: 40 XP base + 1 XP/min (cap 180 min).
- Send: XP by grade relative to personal max: `max-2` = 5, `max-1` = 10, `max` = 25, **new max = 150 + runestone "Breaking <grade>"**. Flash ×1.5.
- Rest day logged after a hard session → blessing **Recovered** (+10% Climbing XP next session).
- Projects (a specific boulder/route you keep trying) are foes (§8).
- TopLogger import: optional, later.

### Engineering ᚲ (work = raids only)
- No daily work tracking.
- A big feature is a **raid** with 3–6 stages (e.g. Design → Backend → Frontend → Review → Shipped). Stage clear = 50 XP, raid clear = 200 XP + hoard.
- Git commits may be attached as evidence; no XP for commits themselves.
- Learning (course, book, talk): 1 XP/min.

### Gaming ᛈ
- Intentional session (logged in the Saga, Steam playtime prefilled): 1 XP/min, diminishing after 4h (×0.5).
- Steam achievement: 5 XP (rare achievements more).
- Finished a game ("Credits Rolled"): 150 XP + entry in the Hall of Heroes.
- An evening of gaming deals damage to **Jörmungandr, the Doomscroll Serpent** (§8).

### Music ᚹ
- Guitar practice: 1.2 XP/min.
- Learning a song = raid (intro → verse → chorus → full song → play it clean). Clear = 150 XP + runestone.
- Guitar strings have durability (drains per practice hour) → chore in the Forge.

### Mechanic ᚱ (the Forge, §9)
- Completing a maintenance chore: 30–100 XP depending on the job.
- Mod = raid.

### Social ᚷ
- Attended event (concert, music night, festival): 80 XP. New venue/genre: +40 XP bonus.
- Meetup with friends: 40 XP.
- Monthly Saga Quest: *"Join a gathering this month"* (big reward).
- Calendar events tagged (e.g. `#event`) are suggested as omens in the Saga.

### Vitality ᛒ (Oura)
- Daily sleep score → XP: `max(0, score - 60)` (score 85 → 25 XP).
- Bedtime consistency (within ±30 min of target for the week): weekly bonus 100 XP.
- **Blessing of Sól**: readiness ≥ 85 → +10% all XP today. Readiness < 60 → soft suggestion "consider a rest day" (no penalty).
- Sleep is partially outside your control, so **bad sleep never costs anything**, it just gives less XP.
- Note: the Oura ring is removed while climbing, so Oura is not used for climbing data.

### Mind ᚨ
- Completing the Saga: 15 XP; with journal line: +10 XP.
- 30-day reflection at the **Well of Mímir** (per-skill review: keep, raise or lower targets): 100 XP.

---

## 6. Quests and raids

| Type (UI) | Description | Generation |
|---|---|---|
| **Chore** (daily) | Small, recurring (e.g. "15 min guitar") | From `QuestTemplate` with recurrence |
| **Deed** (weekly) | e.g. "Climb 2x this week" | From template |
| **Saga Quest** (monthly) | e.g. "Join a gathering" | From template |
| **Omen** (system) | Generated by the app: maintenance due, Völva's game pick, comeback quest | Automatic |
| **Raid** | Multi-day project with stages | Created manually (work feature, song, mod) |

- Quest templates are defined once; instances spawn automatically → no ongoing administration.
- Quests auto-complete when a matching confirmed `ActivityEvent` arrives (e.g. a climbing session progresses "Climb 2x this week").
- Expired Chores just disappear; failed Deeds/Saga Quests have consequences (§7).

---

## 7. Consequences

Goal: missing things should *sting a bit* but never make you want to quit. **XP and levels are never removed.**

| Event | Consequence |
|---|---|
| Saga missed (after 48h grace, no Algiz Ward) | Saga streak resets; active foes **heal 15%**; **Curse of Weariness** (-10% XP) for the next day |
| Deed / Saga Quest failed | The related foe heals; no reward |
| Maintenance overdue | Gear shows a **Rusted** status, chore XP reward drops over time (do it early = more XP) |
| Skill not trained for 30 days | Branch turns **Frostbitten** (Isa ᛁ) on Yggdrasil. No XP loss. First session back gives a "Thaw" bonus |

Mitigations:
- 48h backfill window.
- **Algiz Wards**: earned 1 per 7-day saga streak, max 3 in inventory; also a hoard drop.
- Declared rest/vacation days.

---

## 8. Foes (bosses)

Foes have a sprite (idle + hit animation), HP, damage rules and rewards.

| Foe type | Foe | HP / damage |
|---|---|---|
| **Recurring weekly** | **Jörmungandr, the Doomscroll Serpent** | Damaged by each evening spent gaming, playing guitar, climbing or socialising instead of scrolling |
| | **The Barrow Draugr** (the hermit) | Undead that hoards alone in its barrow. Damaged by social events; heals when a month passes without one |
| | **Níðhöggr, Gnawer of Roots** (procrastination) | Gnaws at Yggdrasil's roots. Damaged by completing a quest marked as "avoided task" |
| **Project foe** | A climbing project ("the blue 6c in the cave"), shown as a **Jötunn** (giant) | Each session on it deals damage; sending it is the killing blow |
| **Raid foe** | Final stage of a raid | Clear stage = kill |
| **Rank trial** | Appears at rank-ups (e.g. *Fenrir* for Jarl) | Requires a mix of skills within a week |

Slaying a foe: XP, silver, a hoard, and an entry in the **Saga of Slain Foes** (bestiary — gamer collection hook).

Damage formula (default): `damage = XP of matching events`; foe HP scales to the user's average weekly XP so foes stay challenging as you level.

---

## 9. The Forge (gear with durability)

Vehicles and other gear are equipment with **durability bars** that drain automatically. Low durability spawns maintenance chores. This turns maintenance planning into zero-admin.

| Gear | Maintenance items (examples, intervals configurable) |
|---|---|
| Car | Oil + filter (12 mo / 15.000 km), tyres (check 3 mo), APK (yearly, fixed date), insurance/road tax reminders, wash/detailing (1 mo) |
| Motorcycle | Chain lube (500 km / 2 weeks in season), chain tension (1.000 km), oil (yearly), tyres, battery tender in winter, APK if applicable |
| Climbing shoes | Resole (by hours/sessions climbed) |
| Guitar | Strings (by practice hours), setup (yearly) |

- Durability drains on **time and/or usage** (km, hours, sessions). Usage comes from logged activities (climbing sessions, practice minutes) or occasional odometer updates in the Saga (optional field).
- Fixed-date items (APK, insurance) are simply dated chores.
- **Mods** are raids attached to a vehicle; completed mods show as upgrade slots ("enchantments") on the gear page.
- Each vehicle is shown as a pixel "steed" sprite (self-drawn or from an asset pack).

---

## 10. Rewards

Two currencies:

- **XP**: progress, never decreases.
- **Silver**: earned alongside XP (default: 1 silver per 10 XP + quest/foe rewards), spent at the Market.

### 10.1 Reward types

| Type | Description | Example |
|---|---|---|
| **The Market** | User-defined, repeatable IRL rewards with a price in silver | Takeaway night 150s, new game from wishlist 600s, concert ticket 800s |
| **Oaths** (milestones) | One-time IRL rewards sworn at skill/global levels | Climbing Lv 15 → new shoes; Guitar Lv 10 → pedal; Rank Drengr → weekend trip |
| **Runestones** (achievements) | Rule-based, carved in the Runestone Field | "Breaking 7a", "30-day saga streak", "Credits Rolled ×10", "Five gatherings in a year" |
| **Hoards** (loot) | Random on Saga completion, quest clear, foe slain | Silver pouch, Algiz Ward, Mead (2× XP one skill for a day), cosmetic items |
| **Titles** | Display title under name, from runestones/attributes | "the Bear-Strong" (STR 20), "Silver-Tongued" (CHA 20) |
| **Cosmetics** | Avatar items, panel frames, Yggdrasil decorations | Self-drawn 16×16/32×32 icons |

- Purchased/earned IRL rewards go to an **inventory** with "claimed" state, so you can track that you actually treated yourself.
- Keep the Market for *extras*; don't gate normal life behind silver.

### 10.2 Loot tables

Loot tables are data: `LootTable` → entries with weight and item. Rarity tiers: Common / Uncommon / Rare / Epic / Legendary (optionally themed: Iron / Bronze / Silver / Gold / Mythic).

---

## 11. The Mead Hall (gaming backlog)

Solves the "nothing to play" feeling.

- Steam library + wishlist synced as a **backlog** (non-Steam games can be added manually).
- Game statuses: *Unplayed, Playing, Finished, Dropped, Abandoned*. Dropping is a valid, guilt-free outcome.
- **The Völva** (seeress NPC) foresees 3 random picks from the backlog, filtered by mood tag: *short session, story, co-op, brain off, challenge*. Accepting gives a small XP bonus; "try it for 30 minutes" is a valid quest.
- **Hall of Heroes**: finished games with date and playtime.
- Optional later: HowLongToBeat lookup for session-length filtering.

---

## 12. Data sources and integrations

All integrations produce **suggested** `ActivityEvent`s (status `PENDING`) that are confirmed in the Saga as omens. Only explicitly trusted sources (Oura sleep) auto-confirm.

| Source | Data | Mechanism | Auto-confirm | Phase |
|---|---|---|---|---|
| Manual / Saga | Everything | Web UI | yes | 1 |
| Calendar | Events of today (tag-matched to activity types, e.g. `#climb`, `#event`) | ICS URL polling | no | 4 |
| Oura | Sleep score, readiness, bedtime | Oura API v2 (OAuth2 app; verify current personal-token status) | yes | 3 |
| Steam | Recent playtime, achievements, library, wishlist | Steam Web API with API key | no | 3 |
| ActivityWatch | Time per app/category on the Linux PCs (IDE, games, guitar tab sites) | Small agent/cron on each PC pushes daily summary to the API | no | 4 |
| Git | Commits per repo (evidence for raids) | Agent on PC or GitHub/GitLab API | n/a (evidence only) | 4 |
| TopLogger | Climbing ascents | Unofficial API — investigate | no | later |
| Free text | Any | Rule parser v1 → LLM (Claude API) later | no | 1 / 4 |

Integration architecture: each source is an `EventSource` implementation (scheduled poller or push endpoint) producing a common `SuggestedEvent` DTO. Sources are idempotent via `(source, external_id)` unique key.

---

## 13. Domain model

Domain names are plain English (see §2.1 for UI names).

```
User
 ├─ Skill (tree: parent_id, rune, rubric, attribute weights)     [seeded per user from defaults]
 ├─ ActivityType (skill mappings, XP rule, payload schema)       e.g. CLIMB_SESSION, SEND, GUITAR_PRACTICE
 ├─ ActivityEvent  ◀── SOURCE OF TRUTH
 │     id, user_id, occurred_at, activity_type_id, payload(jsonb),
 │     source, external_id, status(PENDING|CONFIRMED|REJECTED), review_id
 ├─ DailyReview (date, mood, energy, note, completed_at, backfilled)
 ├─ QuestTemplate (recurrence, criteria, rewards) ─▶ Quest (instance, progress, status, due)
 ├─ Dungeon ─▶ DungeonStage
 ├─ Gear (type: CAR|MOTORCYCLE|SHOES|GUITAR|…, sprite) ─▶ MaintenanceItem (interval_days, interval_usage, last_done)
 ├─ BossDefinition (sprite, hp formula, damage rules, rewards) ─▶ BossEncounter (hp, status, period)
 ├─ ItemDefinition (rarity, sprite, effect) ─▶ InventoryEntry
 ├─ LootTable ─▶ LootEntry
 ├─ AchievementDefinition (rule, runestone sprite) ─▶ AchievementUnlock
 ├─ ShopReward (price) / Milestone (skill+level or global level) ─▶ RewardClaim
 ├─ Buff (type, multiplier, expires_at)
 ├─ Game (backlog: steam_app_id, status, mood tags, playtime)
 └─ IntegrationConfig (source, credentials ref, settings)

Derived / projections (rebuildable from ActivityEvent + definitions):
  SkillProgress (xp, level), AttributeValue, GlobalProgress (level, rank), GoldLedger,
  Streak, Calling, BossEncounter.hp
```

### Derivation strategy

- On every confirmed event → domain event `ActivityConfirmed` → listeners update projections (XP, silver, quests, foes, runestones, loot).
- A **rebuild** command replays all events in order through the current rules to regenerate projections (used after rebalancing).
- Randomness (loot) must be deterministic on replay: seed RNG with the event id, and store granted loot as its own records so a replay doesn't re-roll previously claimed items.

---

## 14. Architecture and tech stack

### Backend
- **Kotlin + Spring Boot** (current stable, Java 25 LTS runtime), Gradle Kotlin DSL.
- Base package e.g. `jera` (or `nl.<you>.jera`).
- **Modular monolith**, packages per bounded context: `character`, `activity`, `review`, `quest`, `combat`, `garage`, `reward`, `tavern`, `integration`, `user`.
- Spring Data JPA (or jOOQ) + **PostgreSQL**, **Flyway** migrations.
- Spring application events for the derivation pipeline (in-process, no broker needed).
- `@Scheduled` jobs: quest spawning, durability drain, integration polling, daily rollover, notifications.
- Spring Security: single local account with session login now; OIDC-ready later (Pocket ID / Authelia).
- REST API (OpenAPI generated → typed Angular client).

### Frontend
- **Angular** (current stable), standalone components, signals.
- Pixel-art Norse UI: custom component styles (NES.css as a starting point), parchment/wood/iron panels, knotwork borders, `image-rendering: pixelated`.
- All Norse terminology via Angular i18n / a central label file, so names can be tweaked without touching components.
- Sprite animation via CSS sprite sheets with `steps()`; no game engine required. Consider a small canvas component only for the Feast of Glory / foe fights if CSS isn't enough.
- Yggdrasil skill tree: SVG or canvas, branches grow/leaf out with levels, frost overlay for Frostbitten skills.
- Sound effects (horn on level-up, hit sounds) optional, with mute toggle.
- PWA install for phone home screen, ᛃ as app icon.

### Assets
- Sprites stored in a mounted volume (`/data/sprites`), referenced by path from definitions → add content without redeploy.
- Sources: Norse/Viking/fantasy packs on itch.io, Kenney (CC0), OpenGameArt, CraftPix freebies. **Record license + attribution per asset** in `assets/LICENSES.md`.
- Pick one main pack for UI frames and one for monsters for a consistent look.
- Self-drawn: small item and reward icons (16×16 / 32×32), ᛃ logo.

### Notifications
- **ntfy** (self-hosted) for: evening Saga reminder, maintenance due, foe slain, weekly summary. Messages written in theme ("Jörmungandr stirs…").

---

## 15. Deployment (Othala)

- Raspberry Pi 4/5 with ≥4 GB RAM, **SSD instead of SD card** for Postgres. Hostname: `jera.othala` (or `othala/jera` path).
- Multi-arch images built with `docker buildx --platform linux/arm64` (or built on the Pi).
- Spring Boot memory: set `-XX:MaxRAMPercentage` / explicit `-Xmx384m`; consider CDS/AOT cache for startup time.

```yaml
# docker-compose.yml (sketch)
services:
  jera:
    image: jera/app:latest                 # Spring Boot jar, serves built Angular from /static
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/jera
      SPRING_DATASOURCE_USERNAME: jera
      SPRING_DATASOURCE_PASSWORD_FILE: /run/secrets/db_password
      JAVA_TOOL_OPTIONS: "-Xmx384m"
    volumes:
      - ./data/sprites:/data/sprites
    depends_on: [db]
    restart: unless-stopped

  db:
    image: postgres:17
    environment:
      POSTGRES_DB: jera
      POSTGRES_USER: jera
      POSTGRES_PASSWORD_FILE: /run/secrets/db_password
    volumes:
      - ./data/postgres:/var/lib/postgresql/data
    restart: unless-stopped

  ntfy:
    image: binwiederhier/ntfy
    command: serve
    volumes:
      - ./data/ntfy:/var/cache/ntfy
    restart: unless-stopped

  caddy:
    image: caddy:2
    ports: ["80:80", "443:443"]
    volumes:
      - ./Caddyfile:/etc/caddy/Caddyfile
      - ./data/caddy:/data
    restart: unless-stopped
```

- Remote access: Tailscale/WireGuard rather than exposing to the internet.
- **Backups**: nightly `pg_dump` to another machine/NAS (the activity log is irreplaceable — it's your saga).

---

## 16. Roadmap

### Phase 1 — Core loop (target: usable daily)
- [ ] Project skeleton: Gradle (Kotlin), Angular, Docker Compose, arm64 build, Flyway, auth (single user, `user_id` everywhere)
- [ ] Skills (with runes), attributes, activity types seeded from defaults
- [ ] ActivityEvent log + XP engine + projections + rebuild command
- [ ] Initial assessment onboarding ("The Norns weigh your past")
- [ ] The Saga: quick add, rule-based free-text parser, mood/energy/journal, "a quiet day", 48h backfill
- [ ] Feast of Glory payoff screen with level-up animation
- [ ] Character sheet: global level/rank, skill bars with rubric, attribute radar (Yggdrasil can start as a simple list)
- [ ] Central label file for Norse terminology
- [ ] Basic ntfy evening reminder

### Phase 2 — Structure and rewards
- [ ] Quest templates (Chores, Deeds, Saga Quests) + automatic instances, auto-completion
- [ ] Raids (work features, songs, mods)
- [ ] The Forge: gear, durability, maintenance chores
- [ ] Silver, the Market, Oaths, reward claims
- [ ] Runestones (achievement rule engine)
- [ ] Consequences: streaks, Algiz Wards, Curse of Weariness, rest/vacation days

### Phase 3 — Game layer
- [ ] Foes with sprites and HP (Jörmungandr, Barrow Draugr, Níðhöggr, Jötunn projects), Saga of Slain Foes
- [ ] Hoards, Mead, blessings/curses, titles, cosmetics
- [ ] Callings (emergent classes)
- [ ] Visual Yggdrasil skill tree with growth and frost
- [ ] Oura integration (sleep XP, Blessing of Sól)
- [ ] Mead Hall: Steam sync, backlog, the Völva's picks, Hall of Heroes

### Phase 4 — Automation
- [ ] Calendar ICS omens
- [ ] ActivityWatch agent for the Linux PCs
- [ ] Git evidence for raids
- [ ] LLM free-text parsing and Well of Mímir 30-day summaries
- [ ] Investigate TopLogger

### Later / maybe
- Multi-user: partner account, co-op foes, a shared longhouse
- Native/mobile companion (Health Connect, NFC tags for one-tap logging)
- Event discovery for Social (venue agendas)

---

## 17. Open questions

- Exact XP balance per activity — start with the defaults above, tune after ~2 weeks of real use (rebuild makes this safe).
- Grade-to-level mapping table for climbing (boulder Font vs. sport French scale separately?).
- Should silver be spendable on Algiz Wards, or only earned via streaks/hoards?
- Oura API auth: confirm whether personal access tokens are still supported or an OAuth2 app is required.
- Which asset pack(s) to standardise on (Norse UI frames + monster sprites).
- Bedtime target for the sleep consistency bonus.
- Rune-styled display font: find one that stays readable at pixel sizes, or skip it.
