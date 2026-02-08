# TitleGenerator
这是一个纯命令版的TITLE快速生成插件，无需GUI，通过命令快速生成和执行TITLE命令！

## 📦 插件信息

- **插件名**: TitleGenerator
- **命令**: `/titlecmd` 或 `/tcmd`
- **权限**: `titlecommand.use` (默认OP)
- **依赖**: 无

## 🎮 完整命令列表

### 1. 快速生成并执行TITLE
```bash
/titlecmd create <目标> <主标题> [副标题] [颜色] [渐入] [停留] [渐出]
```
**参数说明**:
- `<目标>`: 玩家选择器 (`@a`, `@p`, `@r`, 玩家名)
- `<主标题>`: 标题内容（支持&颜色代码）
- `[副标题]`: 副标题内容（可选，支持&颜色代码）
- `[颜色]`: 颜色名称（可选，默认白色）
  - 支持：`black`, `dark_blue`, `dark_green`, `dark_aqua`, `dark_red`, `dark_purple`, `gold`, `gray`, `dark_gray`, `blue`, `green`, `aqua`, `red`, `light_purple`, `yellow`, `white`
- `[渐入]`: 渐入时间（ticks，默认20）
- `[停留]`: 停留时间（ticks，默认60）
- `[渐出]`: 渐出时间（ticks，默认20）

### 2. 生成命令但不执行（仅显示）
```bash
/titlecmd generate <目标> <主标题> [副标题] [颜色] [渐入] [停留] [渐出]
```

### 3. 查看已保存的TITLE配置
```bash
/titlecmd list
```

### 4. 保存配置到文件
```bash
/titlecmd save <配置名> <目标> <主标题> [副标题] [颜色] [渐入] [停留] [渐出]
```

### 5. 加载并执行保存的配置
```bash
/titlecmd load <配置名>
```

### 6. 删除保存的配置
```bash
/titlecmd remove <配置名>
```

### 7. 帮助命令
```bash
/titlecmd help
/titlecmd ?
```

### 8. 重载配置
```bash
/titlecmd reload
```

## 📝 详细使用教程

### 基础用法

#### 1. 简单TITLE（默认颜色，默认时间）
```bash
# 对所有玩家显示白色标题
/titlecmd create @a "欢迎来到服务器"

# 对最近玩家显示
/titlecmd create @p "你好！"

# 带副标题
/titlecmd create @a "游戏开始" "准备战斗！"
```

#### 2. 指定颜色
```bash
# 红色标题
/titlecmd create @a "警告！" red

# 金色标题+绿色副标题
/titlecmd create @a "VIP玩家" "欢迎回来！" gold

# 指定两种不同颜色
/titlecmd create @a "主标题" "副标题" red
```

#### 3. 自定义时间
```bash
# 快速显示（10ticks渐入，40停留，10渐出）
/titlecmd create @a "快速提示" "" white 10 40 10

# 长时间显示
/titlecmd create @a "重要公告" "请仔细阅读" yellow 40 200 40
```

### 高级用法

#### 4. 使用颜色代码（&符号）
```bash
# 在文本中使用&颜色代码
/titlecmd create @a "&c红色&a绿色&b蓝色"

# 混合颜色
/titlecmd create @a "&l&6金色粗体标题"
```

#### 5. 保存常用配置
```bash
# 保存欢迎配置
/titlecmd save welcome @a "&6欢迎光临" "&e享受游戏时光" gold 20 100 20

# 保存警告配置
/titlecmd save warning @a "&c警告！" "&7请勿作弊" red 10 80 10

# 保存胜利配置
/titlecmd save victory @a "&a&l胜利！" "&e恭喜通关" green 30 120 30
```

#### 6. 加载配置
```bash
# 加载并执行保存的配置
/titlecmd load welcome
/titlecmd load warning
/titlecmd load victory
```

#### 7. 管理配置
```bash
# 查看所有保存的配置
/titlecmd list

# 删除配置
/titlecmd remove warning
```

## 🔧 配置文件

插件会在 `plugins/TitleCommand/` 目录生成：

```
config.yml           # 插件配置
titles/              # 保存的TITLE配置
  welcome.yml
  warning.yml
  ...
```

### config.yml 示例
```yaml
# TitleCommand 配置

defaults:
  # 默认时间（ticks）
  fade-in: 20
  stay: 60
  fade-out: 20
  
  # 默认颜色
  color: "white"
  
  # 默认目标
  target: "@a"

settings:
  # 是否允许颜色代码（&符号）
  allow-color-codes: true
  
  # 最大保存配置数
  max-saved-titles: 50
  
  # 是否记录执行日志
  log-executions: true
```

### 保存的TITLE配置格式
```yaml
# welcome.yml
target: "@a"
title: "&6欢迎光临"
subtitle: "&e享受游戏时光"
color: "gold"
fade-in: 20
stay: 100
fade-out: 20
```
