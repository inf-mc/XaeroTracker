<h1 align="center">XaeroTracker</h1>

<p align="center">
  Show eligible online players on Xaero's Minimap and World Map—without requiring a server-side Xaero mod.
</p>

<p align="center">
  <a href="https://modrinth.com/plugin/xaerotracker"><img alt="Modrinth" src="https://img.shields.io/badge/Modrinth-XaeroTracker-00AF5C?logo=modrinth&logoColor=white"></a>
  <img alt="XaeroTracker 1.4.0" src="https://img.shields.io/badge/XaeroTracker-1.4.0-4C8BF5">
  <img alt="Minecraft 26.2" src="https://img.shields.io/badge/Minecraft-26.2-62B47A">
  <img alt="Paper" src="https://img.shields.io/badge/Server-Paper-222222">
  <img alt="Java 25" src="https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white">
  <a href="LICENSE.txt"><img alt="MIT License" src="https://img.shields.io/badge/License-MIT-blue"></a>
</p>

<p align="center">
  English · <a href="doc/README-zh-cn.md">简体中文</a>
</p>

![Player markers displayed by XaeroTracker](https://cdn.modrinth.com/data/ECfsUJsZ/images/3d1bea06e501608ddda199f08e4ff80adfbe049a.png)

XaeroTracker is a server-side Paper plugin that reproduces Xaero's `everyoneTracksEveryone` behaviour.
It sends the live positions of eligible online players to compatible [Xaero's Minimap](https://modrinth.com/mod/xaeros-minimap) and [Xaero's World Map](https://modrinth.com/mod/xaeros-world-map) clients,
allowing them to render player markers on the minimap and full-screen map.

## Features

- Supports both Xaero's Minimap and Xaero's World Map clients.
- Requires no Xaero mod or other plugin on the server.
- Lets players opt out of normal position sharing.
- Excludes invisible, spectator and vanished players by default.
- Provides administrative and permission-based visibility overrides.
- Can limit tracking to players in the same Bukkit world.
- Uses a configurable position-sync interval.
- Persists player tracking preferences across restarts.
- Generates a server level ID for correct map separation, including proxy networks.
- Includes English and Simplified Chinese command feedback.

## Requirements

| Component | Requirement |
|---|---|
| Server | [Paper](https://papermc.io/downloads/paper) 26.2 |
| Java | Java 25 or newer |
| Client | Xaero's Minimap or Xaero's World Map for Minecraft 26.2 |
| Server-side Xaero mod | Not required |

Version 1.4.0 targets Paper 26.2. Spigot and Folia are not part of this release's compatibility target.

## Installation

1. Download the release JAR, or [build it from source](#building-from-source).
2. Place `XaeroTracker-1.4.0-all.jar` in the server's `plugins` directory.
3. Start or fully restart the server.
4. Confirm that `XaeroTracker` is enabled in the console.
5. Have players join with a compatible Xaero client mod.

The plugin creates `plugins/XaeroTracker/config.yml` on first startup.
Its preference files are created when a tracking preference is changed for the first time.

## Visibility rules

By default, an online player is shared unless they:

- opted out with `/xt toggleTracked`;
- are invisible;
- are in spectator mode;
- have `vanished` metadata set by another plugin.

A viewer can bypass those restrictions with `/xt toggleTrackEveryone`, or for one target with `xaerotracker.tracker.<playerName>`. The `only-sync-same-world` boundary still applies to all visibility overrides.

> [!WARNING]
> Visibility overrides can reveal the coordinates of players who are invisible, vanished, spectating or opted out. Grant them only to trusted users.

## Configuration

```yaml
should-send-level-id: true
sync-cooldown: 250
only-sync-same-world: false
```

| Option | Default | Description |
|---|---:|---|
| `should-send-level-id` | `true` | Sends the server level ID required by compatible Xaero clients. Keep this enabled unless another server implementation sends it. |
| `level-id` | Generated | Randomly created and saved on first startup. Give every backend sharing one public address a different value. |
| `sync-cooldown` | `250` | Minimum delay, in milliseconds, between position updates for the same player; rounded up to whole server ticks. |
| `only-sync-same-world` | `false` | Shares positions only between players in the same Bukkit world when enabled. |

Changes require a full server restart; XaeroTracker does not provide a reload command.

### Proxy networks

Xaero uses `level-id` to distinguish maps reached through the same address. On Velocity or another proxy, keep a unique value for every backend server to prevent players from seeing the wrong map data.

### Leaves

If Leaves' built-in `xaero-map-protocol` is enabled, set `should-send-level-id` to `false` so both implementations do not send a level ID. See the [Leaves configuration reference](https://docs.leavesmc.org/en/leaves/reference/configuration).

## Commands

`/xaerotracker` and `/xt` are equivalent.

| Command | Description | Permission | Default |
|---|---|---|---|
| `/xt toggleTracked` | Toggles whether the sender can normally be tracked. Player-only. | `xaerotracker.toggleTracked` | Everyone |
| `/xt toggleTracked <player>` | Toggles whether another player can normally be tracked. | `xaerotracker.toggleTracked.others` | Operators |
| `/xt toggleTrackEveryone` | Toggles whether the sender bypasses normal visibility restrictions. Player-only. | `xaerotracker.toggleTrackEveryone` | Operators |
| `/xt toggleTrackEveryone <player>` | Toggles the visibility bypass for another player. | `xaerotracker.toggleTrackEveryone.others` | Operators |

Player preferences are stored by name. When targeting an offline player, use their exact Minecraft name and capitalization.

## Permissions

| Permission | Description | Default |
|---|---|---|
| `xaerotracker` | Access to `/xaerotracker` and `/xt`. | Everyone |
| `xaerotracker.*` | All declared XaeroTracker command permissions. | Operators |
| `xaerotracker.tracker.<playerName>` | Lets the holder track that target despite normal visibility rules, within the configured world scope. | False |

## Troubleshooting

### The plugin does not start

Confirm that the server is Paper 26.2 and that `java -version` reports Java 25 or newer. Install the `*-all.jar` release artifact.

### No player markers appear

- Confirm that the client mod is built for Minecraft 26.2.
- Xaero's Minimap **Fair-play** edition does not display entities; use the normal edition for player markers.
- Make sure entity radar is enabled in the client.
- Check the server's rules before enabling player radar.

### Players appear as dots instead of heads

Enable icons/heads in **Minimap Settings → Entity Radar Categories**. The exact menu wording can vary between Xaero versions.

### Only some players appear

Check player opt-outs, invisibility, spectator mode, vanish plugins, permission overrides and `only-sync-same-world`.

### Map data is mixed up behind a proxy

Assign a different `level-id` to every backend that players reach through the same public server address.

### Configuration changes do not take effect

Fully restart the server. There is no `/xt reload` command.
Or reload by other plugins which allow enabling/disabling other plugins.

## Building from source

Install JDK 25, clone the repository and use the included Gradle wrapper:

```bash
./gradlew clean build
```

On Windows:

```powershell
.\gradlew.bat clean build
```

The distributable plugin is written to:

```text
build/libs/XaeroTracker-<version>-all.jar
```

To launch a local Paper 26.2 development server:

```bash
./gradlew runServer
```

## Contributing

Bug reports and pull requests are welcome in the [issue tracker](https://github.com/inf-mc/XaeroTracker/issues). For compatibility reports, include the Paper build, Java version, Xaero mod versions, client loader and the steps needed to reproduce the result.

XaeroTracker is available under the [MIT License](LICENSE.txt).

> [!NOTE]
> XaeroTracker is an independent community project. It is not affiliated with, endorsed by or officially connected to xaero96 or the Xaero map mods.
