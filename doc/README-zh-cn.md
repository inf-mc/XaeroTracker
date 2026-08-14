<h1 align="center">XaeroTracker</h1>

<p align="center">
  无需在服务端安装 Xaero 模组，即可在 Xaero 的小地图和世界地图上显示符合条件的在线玩家。
</p>

<p align="center">
  <a href="https://modrinth.com/plugin/xaerotracker"><img alt="Modrinth" src="https://img.shields.io/badge/Modrinth-XaeroTracker-00AF5C?logo=modrinth&logoColor=white"></a>
  <img alt="XaeroTracker 1.4.0" src="https://img.shields.io/badge/XaeroTracker-1.4.0-4C8BF5">
  <img alt="Minecraft 26.2" src="https://img.shields.io/badge/Minecraft-26.2-62B47A">
  <img alt="Paper" src="https://img.shields.io/badge/Server-Paper-222222">
  <img alt="Java 25" src="https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white">
  <a href="../LICENSE.txt"><img alt="MIT License" src="https://img.shields.io/badge/License-MIT-blue"></a>
</p>

<p align="center">
  <a href="../README.md">English</a> · 简体中文
</p>

![XaeroTracker 显示的玩家标记](https://cdn.modrinth.com/data/ECfsUJsZ/images/3d1bea06e501608ddda199f08e4ff80adfbe049a.png)

XaeroTracker 是一款 Paper 服务器插件，用于模拟 Xaero 的 `everyoneTracksEveryone` 行为。
它会将符合条件的在线玩家的实时位置发送给装有 [Xaero's Minimap](https://modrinth.com/mod/xaeros-minimap) 和 [Xaero's World Map](https://modrinth.com/mod/xaeros-world-map) 模组的客户端，
使其能够在小地图和世界地图上显示玩家标记。

## 功能

- 同时支持 Xaero's Minimap 和 Xaero's World Map 客户端。
- 服务器无需安装 Xaero 模组或其他插件。
- 允许玩家选择不参与常规位置共享。
- 默认不显示隐身、观察者模式以及被其他插件隐藏的玩家。
- 提供管理员和基于权限的可见性覆盖规则。
- 可将追踪范围限制在同一个 Bukkit 世界内。
- 支持配置位置同步间隔。
- 重启后仍会保留玩家的追踪偏好。
- 自动生成服务器 level ID，以正确分隔代理网络下的地图数据。
- 提供英语和简体中文的命令反馈。

## 运行要求

| 组件 | 要求 |
|---|---|
| 服务器 | [Paper](https://papermc.io/downloads/paper) 26.2 |
| Java | Java 25 或更高版本 |
| 客户端 | 适用于 Minecraft 26.2 的 Xaero's Minimap 或 Xaero's World Map |
| 服务端 Xaero 模组 | 不需要 |

1.4.0 版本以 Paper 26.2 为目标。Spigot 和 Folia 不在此版本的兼容性目标范围内。

## 安装

1. 下载发行版 JAR，或[从源代码构建](#从源代码构建)。
2. 将 `XaeroTracker-1.4.0-all.jar` 放入服务器的 `plugins` 目录。
3. 启动或完整重启服务器。
4. 在控制台中确认 `XaeroTracker` 已成功启用。
5. 让玩家使用兼容的 Xaero 客户端模组加入服务器。

插件会在首次启动时创建 `plugins/XaeroTracker/config.yml`；玩家偏好文件则会在首次更改对应的追踪偏好时创建。

## 可见性规则

默认情况下，在线玩家的位置会被共享，除非该玩家：

- 使用 `/xt toggleTracked` 选择不被追踪；
- 处于隐身状态；
- 处于观察者模式；
- 被其他插件设置了 `vanished` 元数据。

查看者可以使用 `/xt toggleTrackEveryone` 绕过这些限制， 也可以通过 `xaerotracker.tracker.<playerName>` 仅绕过指定目标的限制。
`only-sync-same-world` 所设定的世界范围仍适用于所有可见性覆盖规则。

> [!WARNING]
> 可见性覆盖规则可能会暴露已隐身、被隐藏、处于观察者模式或已选择退出的玩家坐标。请仅将这些权限授予可信任的用户。

## 配置

```yaml
should-send-level-id: true
sync-cooldown: 250
only-sync-same-world: false
```

| 选项 | 默认值 | 说明                                                         |
|---|---:|------------------------------------------------------------|
| `should-send-level-id` | `true` | 向兼容的 Xaero 客户端发送所需的服务器 level ID。除非已有其他服务器实现发送该 ID，否则请保持启用。 |
| `level-id` | 自动生成 | 首次启动时随机生成并保存。通过同一地址访问的每个后端服务器都应使用不同的值。                     |
| `sync-cooldown` | `250` | 同一玩家两次位置更新之间的最短间隔，单位为毫秒；该值会向上取整到完整的服务器 tick。               |
| `only-sync-same-world` | `false` | 启用后，仅在同一个 Bukkit 世界内的玩家之间共享位置。                             |

配置更改需要完整重启服务器，XaeroTracker 不提供重载命令。
或者通过其他提供启用/禁用其他插件功能的插件来重载。

### 代理网络

Xaero 使用 `level-id` 区分通过同一地址访问的地图。在 Velocity 或其他代理网络中，请为每个后端服务器设置唯一值，以防止玩家看到错误的地图数据。

### Leaves

如果启用了 Leaves 内置的 `xaero-map-protocol`，请将 `should-send-level-id` 设为 `false`，以免两个实现同时发送 level ID。详情请参阅 [Leaves 配置参考](https://docs.leavesmc.org/en/leaves/reference/configuration)。

## 命令

`/xaerotracker` 与 `/xt` 完全等效。

| 命令 | 说明 | 权限 | 默认 |
|---|---|---|---|
| `/xt toggleTracked` | 切换命令发送者是否可被常规追踪。仅限玩家执行。 | `xaerotracker.toggleTracked` | 所有人 |
| `/xt toggleTracked <player>` | 切换另一名玩家是否可被常规追踪。 | `xaerotracker.toggleTracked.others` | 管理员（OP） |
| `/xt toggleTrackEveryone` | 切换命令发送者是否绕过常规可见性限制。仅限玩家执行。 | `xaerotracker.toggleTrackEveryone` | 管理员（OP） |
| `/xt toggleTrackEveryone <player>` | 切换另一名玩家是否绕过常规可见性限制。 | `xaerotracker.toggleTrackEveryone.others` | 管理员（OP） |

玩家偏好按名称保存。指定离线玩家时，请使用其准确的 Minecraft 名称并保持大小写一致。

## 权限

| 权限 | 说明 | 默认 |
|---|---|---|
| `xaerotracker` | 允许使用 `/xaerotracker` 和 `/xt`。 | 所有人 |
| `xaerotracker.*` | 所有已声明的 XaeroTracker 命令权限。 | 管理员（OP） |
| `xaerotracker.tracker.<playerName>` | 允许权限持有者在配置的世界范围内忽略常规可见性规则并追踪指定目标。 | 不授予 |

## 故障排除

### 插件无法启动

确认服务器为 Paper 26.2，且 `java -version` 显示 Java 25 或更高版本。请安装发行版的 `*-all.jar` 文件。

### 没有出现玩家标记

- 确认客户端模组适用于 Minecraft 26.2。
- Xaero's Minimap 的 **Fair-play** 版本不会显示实体；如需显示玩家标记，请使用普通版本。
- 确认客户端已启用实体雷达。
- 启用玩家雷达前，请先确认服务器规则允许使用该功能。

### 玩家显示为圆点而不是头像

在 **Minimap Settings → Entity Radar Categories** 中启用图标或头像。具体位置可能因 Xaero 版本而异。

### 只显示部分玩家

检查玩家是否选择退出、是否隐身、是否处于观察者模式、是否被 vanish 插件隐藏，以及权限覆盖规则和 `only-sync-same-world` 设置。

### 使用代理服务器时地图数据混在一起

为通过同一地址访问的每个后端分配不同的 `level-id`。

### 配置更改没有生效

完整重启服务器。插件不提供 `/xt reload` 命令。

## 从源代码构建

安装 JDK 25，克隆仓库，然后使用项目附带的 Gradle Wrapper：

```bash
./gradlew clean build
```

在 Windows 上：

```powershell
.\gradlew.bat clean build
```

可分发的插件将生成在：

```text
build/libs/XaeroTracker-<version>-all.jar
```

如需启动本地 Paper 26.2 开发服务器：

```bash
./gradlew runServer
```

## 参与贡献

欢迎通过[问题追踪器](https://github.com/inf-mc/XaeroTracker/issues)提交错误报告和 Pull Request。提交兼容性报告时，请附上 Paper 构建版本、Java 版本、Xaero 模组版本、客户端加载器以及重现问题所需的步骤。

XaeroTracker 基于 [MIT 许可证](../LICENSE.txt)发布。

> [!NOTE]
> XaeroTracker 是一个独立的社区项目，与 xaero96 或 Xaero 地图模组不存在隶属、认可或任何官方关联。
