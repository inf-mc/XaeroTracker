# XaeroTracker
English | [中文](doc/README-zh-cn.md)

XaeroTracker is a paper plugin which simulates everyoneTracksEveryone option in Xaero's World Map mod.
With this plugin, players who have Xaero's World Map or Xaero's Minimap installed can see all other players on their map.

**XaeroTracker is not affiliated or endorsed by xaero96 and is not officially affiliated with Xaero in any way.**

# Tested Version
This plugin has only been tested on 1.20.1 and 1.21.4 .

# Config
```Yaml
should-send-level-id: true
level-id: -884219291
sync-cooldown: 250
```

## should-send-level-id
Whether to send level id to client. Only if level id is sent can the plugin funtion.
But if you are using Leaves server and turned XareoProtocol on, you should disable this.

## level-id
It will be randomly generated when the plugin is first loaded.
Client uses it to tell different servers under the same domain.
It's useful when you are using velocity proxy, every sub server should have different value of it.
## sync-cooldown
How long should a player's location can't be synced again. (unit millisecond)

# Command
You need xaerotracker permission to execute any of this.
```
/xt toggleTracked
/xt toggleTracked <player name>
/xt toggleTrackEveryone
/xt toggleTrackEveryone <player name>
```

## /xt toggleTracked
Toggle whether yourself can be tracked.
Require xaerotracker.toggleTracked permission.

## /xt toggleTracked <player name>
Toggle whether other player can be tracked.
Require xaerotracker.toggleTracked.others permission.

## /xt toggleTrackEveryone
Toggle whether you can track any other player (bypass any other restriction).
Require xaerotracker.toggleTrackEveryone permission.

## /xt toggleTrackEveryone <player name>
Toggle whether a specific player can track any other player (bypass any other restriction).
Require xaerotracker.toggleTrackEveryone.others permission.
