# XaeroTracker
XaeroTracker is a paper plugin which simulates everyoneTracksEveryone option in Xaero's World Map mod.
With this plugin, players who have Xaero's World Map or Xaero's Minimap installed can see all other players on their map.

**XaeroTracker is not affiliated or endorsed by xaero96 and is not officially affiliated with Xaero in any way.**

XaeroTracker 是一个 Paper 插件，可以模拟 Xaero's World Map 的 everyoneTracksEveryone 选项，
因此所有安装了 Xaero's World Map 或 Xaero's Minimap 的玩家可以在他们的地图上看到所有其他玩家。

**XaeroTracker 并不附属于 Xaero96 和 Xaero。**

# Tested Version（测试版本）
This plugin has only been tested on 1.20.1 and 1.21.4 .

本插件只在 1.20.1 和 1.21.4 上经过测试

# Config（设置）
```Yaml
should-send-level-id: true
level-id: -884219291
sync-cooldown: 250
```

## should-send-level-id
Whether to send level id to client. Only if level id is sent can the plugin funtion.
But if you are using Leaves server and turned XareoProtocol on, you should disable this.

是否要向客户端发送 level id，玩家追踪功能依赖于此工作，
但如果你使用 Leaves 并开启了 Xaero 协议支持，需要关闭此选项。

## level-id
It will be randomly generated when the plugin is first loaded.
Client uses it to tell different servers under the same domain.
It's useful when you are using velocity proxy, every sub server should have different value of it.

会在首次加载插件时随机生成，用于区别同一域名下的不同服务器，
这对于 velocity 十分有用，所有子服的 level-id 应不一样。

## sync-cooldown
How long should a player's location can't be synced again. (unit millisecond)

一个玩家在共享位置后多久无法被再次共享，单位毫秒。

# Command（命令）
You need xaerotracker permission to execute any of this.

你需要 xaerotracker 权限来运行它们。
```
/xt toggleTracked
/xt toggleTracked <player name（玩家名）>
/xt toggleTrackEveryone
/xt toggleTrackEveryone <player name（玩家名）>
```

## /xt toggleTracked
Toggle whether yourself can be tracked.
Need xaerotracker.toggleTracked permission.

切换你是否会被追踪。
需要 xaerotracker.toggleTracked 权限。
## /xt toggleTracked <player name（玩家名）>
Toggle whether other player can be tracked.
Need xaerotracker.toggleTracked.others permission.

切换任意其他玩家是否会被追踪。
需要 xaerotracker.toggleTracked.others 权限。
## /xt toggleTrackEveryone
Toggle whether you can track any other player (bypass any other restriction).
Need xaerotracker.toggleTrackEveryone permission.

切换你是否可以无视任何限制追踪其他玩家。
需要 xaerotracker.toggleTrackEveryone 权限。
## /xt toggleTrackEveryone <player name（玩家名）>
Toggle whether a specific player can track any other player (bypass any other restriction).
Need xaerotracker.toggleTrackEveryone.others permission.

切换指定玩家是否可以无视任何限制追踪其他玩家。
需要 xaerotracker.toggleTrackEveryone.others 权限。