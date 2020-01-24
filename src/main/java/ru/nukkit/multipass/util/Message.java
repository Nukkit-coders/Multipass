/*
    Multipass, Nukkit permissions plugin
    Copyright (C) 2016  fromgate, nukkit.ru

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.nukkit.multipass.util;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Location;
import cn.nukkit.player.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import ru.nukkit.multipass.MultipassPlugin;

import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


public enum Message {

    //Default (lang) messages
    LNG_LOAD_FAIL("Failed to load languages from file. Default message used"),
    LNG_SAVE_FAIL("Failed to save lang file"),
    LNG_PRINT_FAIL("Failed to print message %1%. Sender object is null."),
    LNG_CONFIG("[MESSAGES] Messages: %1% Language: %2% Save translate file: %1% Debug mode: %3%"),
    WORD_UNKNOWN("Unknown"),
    WRONG_PERMISSION("You have not enough permissions to execute this command"),
    PERMISSION_FAIL("You have not enough permissions to execute this command", 'c'),
    PLAYER_COMMAD_ONLY("You can use this command in-game only!", 'c'),
    CMD_REGISTERED("Command registered: %1%"),
    CMD_FAILED("Failed to execute command. Type %1% to get help!"),
    HLP_TITLE("%1% | Help"),


    CMD_PERM_DESC("Main Multipass command, type /perm help for more  info"),
    CMD_PERM_HELP("/perm help [page] - show help"),
    CMD_PERM_UPDATE("/perm refresh - recalculate permissions for all players"),
    CMD_PERM_RELOAD("/perm reload - reload Multipass configuration"),
    CMD_USER("/user <player> - show info about player permissions"),
    CMD_USER_REMOVE("/user remove <user> - remove user"),
    CMD_USER_SETPERM("/user <player> setperm [world] <permission> - add permission to player"),
    CMD_USER_REMOVEPERM("/user <player> rmvperm [world] <permission> - remove player permission"),
    CMD_USER_SETGROUP("/user <player> setgroup [world] <group> - move player in group"),
    CMD_USER_ADDGROUP("/user <player> addgroup [world] <group> - add player in group"),
    CMD_USER_REMOVEGROUP("/user <player> removegroup [world] <group> - remove player from group"),
    CMD_GROUP_CREATE("/group create <group> - create new group"),
    CMD_GROUP_ADDGROUP("/group <group1> addgroup [world] <group2> - add group2 to group1"),
    CMD_GROUP_REMOVEGROUP("/group <group1> remove [world] <group2> - remove group1 from group2"),
    CMD_GROUP_SETPERM("/group <group> setperm [world] <permission> - add permission to group"),
    CMD_GROUP_SETPREFIX("/group <group> setprefix <prefix> - set prefix for group"),
    CMD_GROUP_SETSUFFIX("/group <group> setsuffix <suffix> - set suffix for group"),
    CMD_GROUP_REMOVEPERM("/group <group> removeperm <permission> - remove permission from group"),
    CMD_GROUP_REMOVE("/group remove <group> - remove group"),
    CMD_PERM_CHECK("/perm check <player> <permission> - check player permission line"),
    CMD_USER_SETPREFIX("/user <player> setprefix <user prefix> - set prefix"),
    CMD_USER_SETSUFFIX("/user <player> setsuffix <user suffix> - set suffix"),
    CMD_PERM_EXPORT("/perm export [fileName] - export all permissions to file"),
    CMD_PERM_IMPORT("/perm import [overwrite] [filename] - import permissions from file"),

    PERM_USER_NOTREGISTER("User %1% is not registered on this server!"),

    PERM_USER_INFO("%1%'s permissions info"),
    PERM_USER_GROUPS("Groups: %1%"),
    PERM_USER_PERMS("Permissions:"),

    PERM_GROUP_NOTFOUND("Group %1% is unknown!"),
    PERM_GROUP_GROUPS("Groups: %1%"),
    PERM_GROUP_PERMS("Permissions:"),
    PERM_GROUP_INFO("Group %1% info"),
    USER_SETPERM_OK("User %1% permission configured: %2%"),
    USER_SETPERMW_OK("User %1% permission configured: %2% (world %3%)"),
    USER_SETPERM_OK_INFORM("Your permissions was set: %1%"),
    USER_SETPERMW_OK_INFORM("Your permissions was set: %1% (world: %3%)"),

    USER_REMOVEPERM_NOTFOUND("User %1% not found. Cannot remove permission"),
    USER_REMOVEPERM_NOTSET("Failed to remove permission %1% from user %2%. It was not configured", 'c', '4'),
    USER_REMOVEPERMW_NOTSET("Failed to remove permission %1% (world %3) from user %2%. It was not configured", 'c', '4'),
    USER_REMOVEPERM_OK("Permission %1% removed from user %2%"),
    USER_REMOVEPERMW_OK("Permission %1% (world %3%) removed from user %2%"),
    USER_REMOVEPERM_OK_INFORM("Permission %1% was removed from you"),
    USER_REMOVEPERMW_OK_INFORM("Permission %1% removed from user %2%"),
    USER_SETGROUP_OK("Player %1% moved to group %2%"),
    USER_SETGROUPW_OK("Player %1% moved to group %2% (world %3%)"),

    USER_SETGROUP_OK_INFORM("You moved to group %1%"),
    USER_SETGROUPW_OK_INFORM("You moved to group %1% (world %2%)"),
    USER_ADDGROUP_OK("Player %1% added to group %2%"),
    USER_ADDGROUPW_OK("Player %1% added to group %2% (world %3%)"),
    USER_ADDGROUP_OK_INFORM("You were added to group %1%"),
    USER_ADDGROUPW_OK_INFORM("You were added to group %1% (world %2%)"),

    USER_REMOVEGROUP_NOTFOUND("User %1% not found. Cannot remove him from the group", 'c', '4'),
    USER_REMOVEGROUP_NOTSET("Failed to remove player %2% from group %1%. It was not configured", 'c', '4'),
    USER_REMOVEGROUPW_NOTSET("Failed to remove player %2% from group %1% (world %3%). It was not configured", 'c', '4'),
    USER_REMOVEGROUP_OK("Player %2% removed from group %1%"),
    USER_REMOVEGROUPW_OK("Player %2% removed from group %1% (world %3%)"),
    USER_REMOVEGROUP_OK_INFORM("You was removed from the group %1%"),
    USER_REMOVEGROUPW_OK_INFORM("You was removed from the group %1% (world %2%)"),
    USER_NOTEXIST("Failed to remove user. User %1% is not exist", 'c', '4'),
    USER_REMOVE_OK("User %1% removed"),

    GROUP_EXIST("Failed to create new group. Group %1% already exists", 'c', '4'),
    GROUP_NOTEXIST("Failed to remove group. Group %1% is not exist", 'c', '4'),
    GROUP_CREATE_OK("Group %1% created"),
    GROUP_REMOVE_OK("Group %1% removed"),
    GROUP_ADDGROUP_NOTEXIST("Group %1% is not exist. Please check group name or create new group", 'c', '4'),
    GROUP_ADDGROUP_OK("Group %2% was added to group %1%"),
    GROUP_ADDGROUPW_OK("Group %2% was added to group %1% (world %3%)"),

    GROUP_REMOVEGROUP_NOTEXIST("Group %1% is not exist. Please check group name or create new group", 'c', '4'),
    GROUP_REMOVEGROUP_OK("Group %2% was removed from group %1%"),
    GROUP_REMOVEGROUPW_OK("Group %2% was removed from group %1% (world %3%)"),

    GROUP_SETGROUP_NOTEXIST("Group %1% is not exist. Please check group name or create new group", 'c', '4'),
    GROUP_SETGROUP_OK("Group %2% was added to group %1%. All other groups were removed"),
    GROUP_SETGROUPW_OK("Group %2% was added to group %1% (world %3%). All other groups were removed"),

    GROUP_SETPERM_NOTEXIST("Failed to set permission. Group %1% is not exist", 'c', '4'),
    GROUP_SETPERM_OK("Group %1% permission configured: %2%"),
    GROUP_SETPERMW_OK("Group %1% permission configured: %2% (world: %3%)"),

    GROUP_REMOVEPERM_NOTEXIST("Failed to remove permission. Group %1% is not exist", 'c', '4'),
    GROUP_REMOVEPERM_PERMUNSET("Failed to remove permission. Permission %2% is not configured for group %1%", 'c', '4'),
    GROUP_REMOVEPERM_OK("Permission %2% removed from group %1%"),
    GROUP_REMOVEPERMW_OK("Permission %2% removed from group %1% (world: %3%)"),

    GROUP_SETPREFIX_NOTEXIST("Failed to set prefix. Group %1% is not exist", 'c', '4'),
    GROUP_SETSUFFIX_NOTEXIST("Failed to set suffix. Group %1% is not exist", 'c', '4'),
    GROUP_SETPREFIX_OK("Prefix of group %1% is changed to %2%"),
    GROUP_SETSUFFIX_OK("Suffix of group %1% is changed to %2%"),

    GROUP_LIST("Available groups: %1%"),
    GROUP_LISTNOTFOUND("Failed to find any group!"),
    GROUP_INFOUNKNOWN("Failed to show group info. Group %1% was not found"),
    GROUP_INFO_GROUPS("Groups: %1%"),
    GROUP_INFO_TITLE("Group %1% info"),
    GROUP_INFO_PERMTITLE("Permissions:"),
    PERM_CHECK_NULLPLAYER("Failed to check permission. Player %1% is offline"),
    PERM_CHECK_HAS("Player %1% has permission %2%"),
    PERM_CHECK_HASNT("Player %1% has not permission %2%"),
    PERM_RELOADED("Configuration reloaded!"),
    PERM_UPDATED("Player permissions updated"),
    REMOVED_GROUP_DETECTED("User %1% is trying to use unknown group %2%. Please check user permissions/groups"),
    PERM_USER_PREFIX("Prefixes: %1%"),
    PERM_USER_SUFFIX("Suffixes: %1%"),
    PROVIDER_FAILED("Failed to init data provider: %1%", 'c'),
    USER_PREFIX_OK("Prefix of user %1% is set to %2%"),
    USER_PREFIX_OK_INFORM("Your prefix was changed to %1%"),
    USER_SUFFIX_OK("Suffix of user %1% is set to %2%"),
    USER_SUFFIX_OK_INFORM("Your suffix was changed to %1%", 'c'),

    USER_ADDGROUP_NOTEXIST("Failed to add user %1% to group %2%. Group is not exist", 'c'),
    USER_SETGROUP_NOTEXIST("Failed to move user %1% to group %2%. Group is not exist", 'c'),
    LOG_UNKNOWN_GROUP_DETECTED("Unknown subgroup detected: %1%. Please check your users/groups configuration", 'c'),
    LOG_UNKNOWN_GROUP_DETECTED_USER("User %2% is member of unknown group: %1%. Please check user configuration", 'c'),
    LOG_UNKNOWN_GROUP_DETECTED_GROUP("Group %2% is contains unknown subgroup %1%. Please check group configuration", 'c'),

    DB_DBLIB_NOTFOUND("DbLib required for MySQL/SQLite support", 'c'),
    LOG_UNKNOWN_DATAPROVIDER("Unknown data provider: %1%. Will use YAML provider", 'c', '4'),
    LOG_DATAPROVIDER("Data provider: %1%"),

    PERM_EXPORT_OK("Permissions exported to file: %1%"),
    PERM_EXPORT_FAILED("Permissions export failed", 'c'),
    PERM_IMPORT_OK("Permissions imported from file: %1%"),
    PERM_IMPORT_FAILED("Permissions import failed", 'c'),
    LOG_DATAPROVIDER_FAIL("Failed to enable Data Provider. Please check your configuration", 'c');


    private static boolean debugMode = false;
    private static String language = "default";
    private static boolean saveLanguage = false;
    private static char c1 = 'a';
    private static char c2 = '2';

    private static PluginBase plugin = null;

    private static Map<String, Long> logOnce = new HashMap<>();

    /**
     * This is my favorite debug routine :) I use it everywhere to print out variable values
     *
     * @param s - array of any object that you need to print out.
     *          Example:
     *          Message.BC ("variable 1:",var1,"variable 2:",var2)
     */
    public static void BC(Object... s) {
        if (!debugMode) return;
        if (s.length == 0) return;
        StringBuilder sb = new StringBuilder("&3[").append(plugin.getDescription().getName()).append("]&f ");
        for (Object str : s)
            sb.append(str.toString()).append(" ");
        plugin.getServer().broadcastMessage(TextFormat.colorize(sb.toString().trim()));
    }

    /**
     * Send current message to log files
     *
     * @param s
     * @return — always returns true.
     * Examples:
     * Message.ERROR_MESSAGE.log(variable1); // just print in log
     * return Message.ERROR_MESSAGE.log(variable1); // print in log and return value true
     */
    public boolean log(Object... s) {
        plugin.getLogger().info(getText(s));
        return true;
    }

    public boolean logOnce(Object... s) {
        return logOnce(86400, s);
    }

    public boolean logOnce(int seconds, Object... s) {
        String msg = getText(s);
        long curTime = System.currentTimeMillis();
        if (!logOnce.containsKey(msg) || logOnce.get(msg) + (seconds * 1000) < curTime) {
            log(s);
            logOnce.put(msg, curTime);
        }
        return true;
    }

    /**
     * Same as log, but will printout nothing if debug mode is disabled
     *
     * @param s
     * @return — always returns true.
     */
    public boolean debug(Object... s) {
        if (debugMode) plugin.getLogger().info(TextFormat.clean(getText(s)));
        return true;
    }

    /**
     * Show a message to player in center of screen (this routine unfinished yet)
     *
     * @param seconds — how much time (in seconds) to show message
     * @param sender  — Player
     * @param s
     * @return — always returns true.
     */
    public boolean tip(int seconds, CommandSender sender, Object... s) {
        if (sender == null) return Message.LNG_PRINT_FAIL.log(this.name());
        final Player player = sender instanceof Player ? (Player) sender : null;
        final String message = getText(s);
        if (player == null) sender.sendMessage(message);
        else for (int i = 0; i < seconds; i++)
            Server.getInstance().getScheduler().scheduleDelayedTask(MultipassPlugin.getPlugin(), new Runnable() {
                @SuppressWarnings("deprecation")
                public void run() {
                    if (player.isOnline()) player.sendTip(message);
                }
            }, 20 * i);
        return true;
    }

    /**
     * Show a message to player in center of screen
     *
     * @param sender — Player
     * @param s
     * @return — always returns true.
     */
    @SuppressWarnings("deprecation")
    public boolean tip(CommandSender sender, Object... s) {
        if (sender == null) return Message.LNG_PRINT_FAIL.log(this.name());
        Player player = sender instanceof Player ? (Player) sender : null;
        String message = getText(s);
        if (player == null) sender.sendMessage(message);
        else player.sendTip(message);
        return true;
    }

    /**
     * Send message to Player or to ConsoleSender
     *
     * @param sender
     * @param s
     * @return — always returns true.
     */
    public boolean print(CommandSender sender, Object... s) {
        if (sender == null) return Message.LNG_PRINT_FAIL.debug(this.name());
        sender.sendMessage(getText(s));
        return true;
    }

    /**
     * Send message to all players or to players with defined permission
     *
     * @param permission
     * @param s
     * @return — always returns true.
     * <p>
     * Examples:
     * Message.MSG_BROADCAST.broadcast ("pluginname.broadcast"); // send message to all players with permission "pluginname.broadcast"
     * Message.MSG_BROADCAST.broadcast (null); // send message to all players
     */
    public boolean broadcast(String permission, Object... s) {
        for (Player player : plugin.getServer().getOnlinePlayers().values()) {
            if (permission == null || player.hasPermission(permission)) print(player, s);
        }
        return true;
    }


    /**
     * Get formated text.
     *
     * @param keys * Keys - are parameters for message and control-codes.
     *             Parameters will be shown in position in original message according for position.
     *             This keys are used in every method that prints or sends message.
     *             <p>
     *             Example:
     *             <p>
     *             EXAMPLE_MESSAGE ("Message with parameters: %1%, %2% and %3%");
     *             Message.EXAMPLE_MESSAGE.getText("one","two","three"); //will return text "Message with parameters: one, two and three"
     *             <p>
     *             * Color codes
     *             You can use two colors to define color of message, just use character symbol related for color.
     *             <p>
     *             Message.EXAMPLE_MESSAGE.getText("one","two","three",'c','4');  // this message will be red, but word one, two, three - dark red
     *             <p>
     *             * Control codes
     *             Control codes are text parameteres, that will be ignored and don't shown as ordinary parameter
     *             - "SKIPCOLOR" - use this to disable colorizing of parameters
     *             - "NOCOLOR" (or "NOCOLORS") - return uncolored text, clear all colors in text
     *             - "FULLFLOAT" - show full float number, by default it limit by two symbols after point (0.15 instead of 0.1483294829)
     * @return
     */
    public String getText(Object... keys) {
        char[] colors = new char[]{color1 == null ? c1 : color1, color2 == null ? c2 : color2};
        if (keys.length == 0) return TextFormat.colorize("&" + colors[0] + this.message);
        String str = this.message;
        boolean noColors = false;
        boolean skipDefaultColors = false;
        boolean fullFloat = false;
        String prefix = "";
        int count = 1;
        int c = 0;
        DecimalFormat fmt = new DecimalFormat("####0.##");
        for (int i = 0; i < keys.length; i++) {
            String s = keys[i].toString();
            if (c < 2 && keys[i] instanceof Character) {
                colors[c] = (Character) keys[i];
                c++;
                continue;
            } else if (s.startsWith("prefix:")) {
                prefix = s.replace("prefix:", "");
                continue;
            } else if (s.equals("SKIPCOLOR")) {
                skipDefaultColors = true;
                continue;
            } else if (s.equals("NOCOLORS") || s.equals("NOCOLOR")) {
                noColors = true;
                continue;
            } else if (s.equals("FULLFLOAT")) {
                fullFloat = true;
                continue;
            } else if (keys[i] instanceof Location) {
                Location loc = (Location) keys[i];
                if (fullFloat)
                    s = loc.getLevel().getName() + "[" + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + "]";
                else
                    s = loc.getLevel().getName() + "[" + fmt.format(loc.getX()) + ", " + fmt.format(loc.getY()) + ", " + fmt.format(loc.getZ()) + "]";
            } else if (keys[i] instanceof Double || keys[i] instanceof Float) {
                if (!fullFloat) s = fmt.format((Double) keys[i]);
            }

            String from = (new StringBuilder("%").append(count).append("%")).toString();
            String to = skipDefaultColors ? s : (new StringBuilder("&").append(colors[1]).append(s).append("&").append(colors[0])).toString();
            str = str.replace(from, to);
            count++;
        }
        str = TextFormat.colorize(prefix.isEmpty() ? "&" + colors[0] + str : prefix + " " + "&" + colors[0] + str);
        if (noColors) str = TextFormat.clean(str);
        return str;
    }

    private void initMessage(String message) {
        this.message = message;
    }

    private String message;
    private Character color1;
    private Character color2;

    Message(String msg) {
        message = msg;
        this.color1 = null;
        this.color2 = null;
    }

    Message(String msg, char color1, char color2) {
        this.message = msg;
        this.color1 = color1;
        this.color2 = color2;
    }

    Message(String msg, char color) {
        this(msg, color, color);
    }

    @Override
    public String toString() {
        return this.getText("NOCOLOR");
    }

    /**
     * Initialize current class, load messages, etc.
     * Call this file in onEnable method after initializing plugin configuration
     *
     * @param plg
     */

    public static void init(PluginBase plg) {
        plugin = plg;
        language = MultipassPlugin.getCfg().language;
        if (language.equalsIgnoreCase("default")) language = Server.getInstance().getLanguage().getLang();
        else if (language.length() > 3) language = language.substring(0, 3);
        debugMode = MultipassPlugin.getCfg().debugMode;
        saveLanguage = MultipassPlugin.getCfg().saveLanguage;

        initMessages();
        if (saveLanguage) saveMessages();
        LNG_CONFIG.debug(Message.values().length, language, true, debugMode);
    }

    /**
     * Enable debugMode
     *
     * @param debug
     */
    public static void setDebugMode(boolean debug) {
        debugMode = debug;
    }

    public static boolean isDebug() {
        return debugMode;
    }


    private static void initMessages() {
        File f = new File(plugin.getDataFolder() + File.separator + language + ".lng");
        Config lng = null;
        if (!f.exists()) {
            lng = new Config(f, Config.YAML);
            InputStream is = plugin.getClass().getResourceAsStream("/lang/" + language + ".lng");
            lng.load(is);
            if (!f.delete()) {
                System.gc();
                f.delete();
            }
        } else lng = new Config(f, Config.YAML);
        for (Message key : Message.values())
            key.initMessage(lng.getString(key.name().toLowerCase(), key.message));
    }

    private static void saveMessages() {
        File f = new File(plugin.getDataFolder() + File.separator + language + ".lng");
        Config lng = new Config(f, Config.YAML);
        for (Message key : Message.values())
            lng.set(key.name().toLowerCase(), key.message);
        try {
            lng.save();
        } catch (Exception e) {
            LNG_SAVE_FAIL.log();
            if (debugMode) e.printStackTrace();
            return;
        }
    }

    /**
     * Send message (formed using join method) to server log if debug mode is enabled
     *
     * @param s
     */
    public static boolean debugMessage(Object... s) {
        if (debugMode) plugin.getLogger().info(TextFormat.clean(join(s)));
        return true;
    }

    /**
     * Join object array to string (separated by space)
     *
     * @param s
     */
    public static String join(Object... s) {
        StringBuilder sb = new StringBuilder();
        for (Object o : s) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(o.toString());
        }
        return sb.toString();
    }

    public static String color1(String message) {
        return TextFormat.colorize("&" + c1 + message);
    }

    public static String color2(String message) {
        return TextFormat.colorize("&" + c2 + message);
    }
}