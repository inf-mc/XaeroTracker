# XaeroTracker
[English](../README.md) | 中文

XaeroTracker 是一个 Paper 插件，可以模拟 Xaero's World Map 的 everyoneTracksEveryone 选项，
因此所有安装了 Xaero's World Map 或 Xaero's Minimap 的玩家可以在他们的地图上看到所有其他玩家。

**XaeroTracker 并不附属于 Xaero96 和 Xaero。**

# 测试版本
本插件只在 1.20.1 和 1.21.4 上经过测试

# 设置
```Yaml
should-send-level-id: true
level-id: -884219291
sync-cooldown: 250
```

## should-send-level-id
是否要向客户端发送 level id，玩家追踪功能依赖于此工作，
但如果你使用 Leaves 并开启了 Xaero 协议支持，需要关闭此选项。

## level-id
会在首次加载插件时随机生成，用于区别同一域名下的不同服务器，
这对于 velocity 十分有用，所有子服的 level-id 应不一样。

## sync-cooldown
一个玩家在共享位置后多久无法被再次共享，单位毫秒。

# 命令
你需要 xaerotracker 权限来运行它们。
```
/xt toggleTracked
/xt toggleTracked <玩家名>
/xt toggleTrackEveryone
/xt toggleTrackEveryone <玩家名>
```

## /xt toggleTracked
切换你是否会被追踪。
需要 xaerotracker.toggleTracked 权限。

## /xt toggleTracked <玩家名>
切换任意其他玩家是否会被追踪。
需要 xaerotracker.toggleTracked.others 权限。

## /xt toggleTrackEveryone
切换你是否可以无视任何限制追踪其他玩家。
需要 xaerotracker.toggleTrackEveryone 权限。

## /xt toggleTrackEveryone <玩家名>
切换指定玩家是否可以无视任何限制追踪其他玩家。
需要 xaerotracker.toggleTrackEveryone.others 权限。