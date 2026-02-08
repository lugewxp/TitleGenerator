package luewxp.serv00.net.titleGenerator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TitleGenerator extends JavaPlugin implements TabExecutor {

    private Set<String> colorNames;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        createDefaultConfig();
        config = getConfig();
        initializeColorNames();
        getCommand("titlegen").setExecutor(this);
        getCommand("titlegen").setTabCompleter(this);

        getLogger().info("TitleGenerator 插件已启用！");
    }

    @Override
    public void onDisable() {
        getLogger().info("TitleGenerator 插件已禁用！");
    }

    private void createDefaultConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            File configFile = new File(getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                getLogger().info("创建默认配置文件...");
                saveDefaultConfig();
            }
        } catch (Exception e) {
            getLogger().severe("创建配置文件时出错: " + e.getMessage());
        }
    }

    private void initializeColorNames() {
        colorNames = new HashSet<>(Arrays.asList(
                "black", "dark_blue", "dark_green", "dark_aqua", "dark_red",
                "dark_purple", "gold", "gray", "dark_gray", "blue", "green",
                "aqua", "red", "light_purple", "yellow", "white"
        ));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "execute":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "用法: /titlegen execute <目标> <主标题> [副标题] [颜色]");
                    return true;
                }
                return handleExecute(sender, args);
            case "generate":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "用法: /titlegen generate <目标> <主标题> [副标题] [颜色]");
                    return true;
                }
                return handleGenerate(sender, args);
            case "reload":
                if (!sender.hasPermission("titlegen.admin")) {
                    sender.sendMessage(ChatColor.RED + "你没有权限使用此命令。");
                    return true;
                }
                reloadConfig();
                config = getConfig();
                sender.sendMessage(ChatColor.GREEN + "配置文件已重载。");
                return true;
            case "help":
            default:
                sendHelp(sender);
                return true;
        }
    }

    private boolean handleExecute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("titlegen.use")) {
            sender.sendMessage(ChatColor.RED + "你没有权限使用此命令。");
            return true;
        }

        String[] titleArgs = parseTitleArgs(args);
        if (titleArgs == null) {
            sender.sendMessage(ChatColor.RED + "无效的参数。");
            return true;
        }

        String target = titleArgs[0];
        String title = titleArgs[1];
        String subtitle = titleArgs[2];
        String color = titleArgs[3];

        executeTitleCommands(sender, target, title, subtitle, color);
        return true;
    }

    private boolean handleGenerate(CommandSender sender, String[] args) {
        if (!sender.hasPermission("titlegen.use")) {
            sender.sendMessage(ChatColor.RED + "你没有权限使用此命令。");
            return true;
        }

        String[] titleArgs = parseTitleArgs(args);
        if (titleArgs == null) {
            sender.sendMessage(ChatColor.RED + "无效的参数。");
            return true;
        }

        String target = titleArgs[0];
        String title = titleArgs[1];
        String subtitle = titleArgs[2];
        String color = titleArgs[3];

        generateTitleCommands(sender, target, title, subtitle, color);
        return true;
    }

    private String[] parseTitleArgs(String[] args) {
        if (args.length < 3) return null;

        String target = args[1];
        String title = args[2];
        String subtitle = "";
        String color = config.getString("default-color", "white");

        if (args.length == 3) {
            if (isColor(args[2])) {
                title = args[2];
                color = args[2];
            }
        } else if (args.length == 4) {
            if (isColor(args[3])) {
                subtitle = args[3];
                color = args[3];
            } else {
                subtitle = args[3];
            }
        } else if (args.length >= 5) {
            subtitle = args[3];
            color = args[4];
        }

        return new String[]{target, title, subtitle, color};
    }

    private boolean isColor(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        String lowerStr = str.toLowerCase();
        if (colorNames.contains(lowerStr)) {
            return true;
        }

        if (str.startsWith("#")) {
            String hex = str.substring(1);
            if (hex.length() == 6 || hex.length() == 3) {
                try {
                    Long.parseLong(hex, 16);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }

        return false;
    }

    private void executeTitleCommands(CommandSender sender, String target, String title, String subtitle, String color) {
        int fadeIn = config.getInt("times.fade-in", 20);
        int stay = config.getInt("times.stay", 60);
        int fadeOut = config.getInt("times.fade-out", 20);

        String timesCmd = String.format("title %s times %d %d %d", target, fadeIn, stay, fadeOut);
        String titleCmd = String.format("title %s title %s", target, buildJson(title, color));

        try {
            Bukkit.dispatchCommand(sender, timesCmd);
            Bukkit.dispatchCommand(sender, titleCmd);

            if (subtitle != null && !subtitle.isEmpty()) {
                String subtitleCmd = String.format("title %s subtitle %s", target, buildJson(subtitle, color));
                Bukkit.dispatchCommand(sender, subtitleCmd);
            }

            sender.sendMessage(ChatColor.GREEN + "标题已发送到 " + target);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "执行命令时出错: " + e.getMessage());
        }
    }

    private void generateTitleCommands(CommandSender sender, String target, String title, String subtitle, String color) {
        int fadeIn = config.getInt("times.fade-in", 20);
        int stay = config.getInt("times.stay", 60);
        int fadeOut = config.getInt("times.fade-out", 20);

        sender.sendMessage(ChatColor.GOLD + "=== 生成的命令 ===");
        sender.sendMessage(ChatColor.YELLOW + "/title " + target + " times " + fadeIn + " " + stay + " " + fadeOut);
        sender.sendMessage(ChatColor.YELLOW + "/title " + target + " title " + buildJson(title, color));

        if (subtitle != null && !subtitle.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "/title " + target + " subtitle " + buildJson(subtitle, color));
        }
    }

    private String buildJson(String text, String color) {
        String jsonColor;
        if (color.startsWith("#")) {
            jsonColor = color;
        } else {
            jsonColor = color.toLowerCase();
        }
        return String.format("{\"text\":\"%s\",\"color\":\"%s\"}", text.replace("\"", "\\\""), jsonColor);
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== TitleGenerator 帮助 ===");
        sender.sendMessage(ChatColor.YELLOW + "/titlegen execute <目标> <主标题> [副标题] [颜色]");
        sender.sendMessage(ChatColor.YELLOW + "  - 生成并立即执行标题");
        sender.sendMessage(ChatColor.YELLOW + "/titlegen generate <目标> <主标题> [副标题] [颜色]");
        sender.sendMessage(ChatColor.YELLOW + "  - 仅生成命令，不执行");
        sender.sendMessage(ChatColor.YELLOW + "/titlegen reload");
        sender.sendMessage(ChatColor.YELLOW + "  - 重载配置文件");
        sender.sendMessage(ChatColor.YELLOW + "/titlegen help");
        sender.sendMessage(ChatColor.YELLOW + "  - 显示此帮助");
        sender.sendMessage(ChatColor.GRAY + "颜色: 颜色名称或十六进制代码 (#RRGGBB 或 #RGB)");
        sender.sendMessage(ChatColor.GRAY + "颜色名称: " + String.join(", ", colorNames));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("titlegen.use")) {
                completions.add("execute");
                completions.add("generate");
                completions.add("help");
            }
            if (sender.hasPermission("titlegen.admin")) {
                completions.add("reload");
            }
        } else if (args.length == 2) {
            completions.add("@a");
            completions.add("@p");
            completions.add("@r");
            completions.add("@s");
            for (Player p : Bukkit.getOnlinePlayers()) {
                completions.add(p.getName());
            }
        } else if (args.length == 4 || args.length == 5) {
            completions.addAll(colorNames);
            completions.add("#FFFFFF");
            completions.add("#FF0000");
            completions.add("#00FF00");
            completions.add("#0000FF");
            completions.add("#FFFF00");
            completions.add("#FF00FF");
            completions.add("#00FFFF");
        }

        String currentArg = args[args.length - 1];
        if (!currentArg.isEmpty()) {
            completions.removeIf(s -> !s.toLowerCase().startsWith(currentArg.toLowerCase()));
        }

        return completions;
    }
}