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

package ru.nukkit.multipass.command;

import cn.nukkit.command.CommandSender;
import cn.nukkit.player.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import ru.nukkit.multipass.command.group.*;
import ru.nukkit.multipass.command.perm.*;
import ru.nukkit.multipass.command.user.*;
import ru.nukkit.multipass.util.Message;
import ru.nukkit.multipass.util.Paginator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Commander {
    private static List<Cmd> commands = new ArrayList<Cmd>();
    private static PluginBase plugin;

    public static void init(PluginBase plg) {
        plugin = plg;

        //Perm command
        addNewCommand(PermHelp.class);
        addNewCommand(PermReload.class);
        addNewCommand(PermRefresh.class);
        addNewCommand(PermCheck.class);
        addNewCommand(PermExport.class);
        addNewCommand(PermImport.class);

        // Group command
        addNewCommand(GroupInfo.class);
        addNewCommand(GroupCreate.class);
        addNewCommand(GroupRemove.class);
        addNewCommand(GroupRemovePerm.class);
        addNewCommand(GroupSetPerm.class);
        addNewCommand(GroupSetGroup.class);
        addNewCommand(GroupAddGroup.class);
        addNewCommand(GroupRemoveGroup.class);
        addNewCommand(GroupSetPrefix.class);
        addNewCommand(GroupSetSuffix.class);

        //User command
        addNewCommand(UserInfo.class);
        addNewCommand(UserRemove.class);
        addNewCommand(UserAddGroup.class);
        addNewCommand(UserRemoveGroup.class);
        addNewCommand(UserRemovePerm.class);
        addNewCommand(UserSetGroup.class);
        addNewCommand(UserSetPerm.class);
        addNewCommand(UserSetPrefix.class);
        addNewCommand(UserSetSuffix.class);
    }


    public static PluginBase getPlugin() {
        return plugin;
    }

    private static boolean isRegistered(String cmdStr) {
        for (Cmd cmd : commands)
            if (cmd.getCommand().equalsIgnoreCase(cmdStr)) return true;
        return false;
    }

    public static boolean addNewCommand(Class<? extends Cmd> cmd) {
        try {
            return addNewCommand(cmd.newInstance(), null);
        } catch (Exception e) {
            Message.debugMessage("Failed to register command: ", cmd.getName());
            return false;
        }
    }

    public static boolean addNewCommand(Class<? extends Cmd> cmd, Message description) {
        try {
            return addNewCommand(cmd.newInstance(), description);
        } catch (Exception e) {
            Message.debugMessage("Failed to register command: ", cmd.getName());
            return false;
        }
    }


    public static boolean addNewCommand(Cmd cmd) {
        return addNewCommand(cmd, null);
    }

    public static boolean addNewCommand(Cmd cmd, Message description) {
        if (cmd.getCommand() == null) return false;
        if (cmd.getCommand().isEmpty()) return false;
        if (!isRegistered(cmd.getCommand())) {
            CommandExecutor newCmd = new CommandExecutor(cmd.getCommand());
            newCmd.setDescription(description == null ? cmd.getDescription() : description.getText("NOCOLOR"));
            newCmd.setAliases(cmd.getAliases());
            plugin.getServer().getCommandMap().register(plugin, newCmd);
            Message.CMD_REGISTERED.debug(cmd.toString());
        }
        commands.add(cmd);
        return true;
    }

    public static boolean isPluginYml(String cmdStr) {
        return plugin.getDescription().getCommands().containsKey(cmdStr);
    }

    public static void printHelp(CommandSender sender, int page) {
        List<String> helpList = new ArrayList<String>();
        for (Cmd cmd : commands) {
            helpList.add(TextFormat.GREEN + cmd.getHelpString());
        }
        int pageHeight = (sender instanceof Player) ? 9 : 1000;
        Paginator.printPage(sender, helpList, Message.HLP_TITLE.getText('e', '6', (plugin.getName() + " " + plugin.getDescription().getVersion()))
                , "[%1% / %2%]", "No help", page, pageHeight, false);
    }

    public static String unsplit(String[] args) {
        return unsplit(args, 0);
    }

    public static String unsplit(String[] args, int num) {
        if (args.length <= num) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = num; i < args.length; i++) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(args[i]);
        }
        return sb.toString();
    }

    public static Player getPlayer(CommandSender sender) {
        return sender instanceof Player ? (Player) sender : null;
    }

    public static boolean execute(CommandSender sender, String cmdLabel, String[] args) {
        for (Cmd cmd : commands) {
            if (!cmd.isCommand(cmdLabel)) continue;
            if (cmd.executeCommand(sender, args))
                return Message.debugMessage("Executed command:", sender.getName(), cmdLabel, cmd.toString(), new ArrayList<>(Arrays.asList(args)).toString());
        }
        Message.debugMessage("Command not executed:", sender.getName(), cmdLabel, new ArrayList<>(Arrays.asList(args)).toString());
        return Message.CMD_FAILED.print(sender, "/perm help");
    }

    public static String getCommandByAlias(String cmdLabel) {
        for (Cmd cmd : commands)
            if (cmd.isCommand(cmdLabel)) return cmd.getCommand();
        return null;
    }

    public static boolean isPluginCommand(String cmdLabel) {
        return getCommandByAlias(cmdLabel) != null;
    }
}
