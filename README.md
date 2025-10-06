# MMOStats Plugin

MMOStats is a powerful and flexible Minecraft plugin that introduces "socketable gems" to your server. Players can apply these gems to weapons, tools, and armor to permanently enhance them with stats, designed to integrate seamlessly with MMOItems.

## Features

- **Custom Gems**: Define unlimited types of gems in `config.yml`.
- **MMOItems Integration**: Gems grant real, functional stats to items if MMOItems is installed.
- **HeadDatabase Support**: Use custom heads from HeadDatabase as gem textures.
- **Persistent Stats**: Stats are stored safely in the item's PersistentDataContainer, not just lore.
- **Configurable Application Logic**: Control which items gems can be applied to (e.g., WEAPON gems on swords/axes).
- **Safe & Performant**: Designed to be lightweight and prevent exploits.
- **User-Friendly**: Simple drag-and-drop or right-click application.

## Installation

1.  Download the latest `MMOStats-x.x.x.jar` from the releases page.
2.  Place the JAR file into your server's `plugins/` directory.
3.  (Optional) Install [MMOItems](https://www.spigotmc.org/resources/mmoitems-premium.39267/) for functional stats.
4.  (Optional) Install [HeadDatabase](https://www.spigotmc.org/resources/head-database.14280/) to use custom heads for gems.
5.  Start or restart your server. The default `config.yml` and `messages.yml` will be generated.

## Commands & Permissions

| Command                                  | Permission         | Description                                     |
| ---------------------------------------- | ------------------ | ----------------------------------------------- |
| `/mmostats give <player> <id> <amount>`  | `mmostats.admin`   | Gives a player a specified amount of a defined gem. |
| `/mmostats reload`                       | `mmostats.admin`   | Reloads `config.yml` and `messages.yml`.        |

The permission `mmostats.admin` grants access to all plugin commands and is given to server operators by default.

## How It Works

### Defining a Gem

Gems are defined in the `gems` section of `config.yml`.

```yaml
gems:
  ruby_damage:
    id: "ruby_damage"
    name: "<red><bold>Ruby of Power</bold></red>"
    material: "REDSTONE"  # Or "HDB:12345" for a HeadDatabase head
    type: "WEAPON"
    stats:
      DAMAGE: 12
      CRITICAL_STRIKE_CHANCE: 5.0
    lore:
      - "<gray>Socket to add {stats}</gray>"
      - "<dark_gray>Type: {type}</dark_gray>"
```

-   **`id`**: A unique identifier for the gem. Used in the `/mmostats give` command.
-   **`name`**: The display name of the item. Supports MiniMessage or Legacy color codes.
-   **`material`**: The item's material. Use a standard Bukkit Material name (e.g., `DIAMOND`) or a HeadDatabase ID (`HDB:12345`).
-   **`type`**: The category of item this gem can be applied to. These types are mapped to actual materials in the `type-mapping` section of the config.
-   **`stats`**: A map of MMOItems stat keys to their values. These are the stats that will be added to the target item.
-   **`lore`**: The description of the gem item.

### The `{stats}` Placeholder

The `{stats}` placeholder in the lore is automatically expanded to show the stats the gem provides. It uses MMOItems' internal formatting rules. For example, `DAMAGE: 12, MAX_HEALTH: 5` becomes `+12 Damage, +5 Max Health`.

### Applying Gems

Players can apply a gem in two ways:
1.  **Drag and Drop**: Pick up the gem in their inventory and drop it onto a valid target item.
2.  **Right-Click**: Hold the gem and right-click a valid target item in their inventory.

A gem can only be applied if:
- The target item's type matches the gem's `type` (as defined in `type-mapping`).
- Applying the gem does not exceed any configured `stat-caps`.
- The item does not already have the same gem applied (if `prevent-duplicates` is true).

### Stats on Vanilla Items

If you apply a gem to a vanilla item (e.g., a normal Diamond Sword) without MMOItems installed, the stats will be added to the item's lore for display purposes. However, these stats will **not have any functional effect** unless another plugin is coded to read them from the item's Persistent Data. With MMOItems, the stats become fully functional.

