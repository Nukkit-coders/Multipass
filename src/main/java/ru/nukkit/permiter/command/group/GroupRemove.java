package ru.nukkit.permiter.command.group;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import ru.nukkit.permiter.command.Cmd;
import ru.nukkit.permiter.command.CmdDefine;
import ru.nukkit.permiter.permissions.Groups;
import ru.nukkit.permiter.util.Message;

/**
 * Created by Igor on 07.05.2016.
 */
@CmdDefine(command = "group", alias = "groupperm", allowConsole = true, subCommands = {"remove|rmv|delete|del", "\\S+"}, permission = "permiter.admin", description = Message.CMD_GROUP_REMOVE)
public class GroupRemove extends Cmd {


    @Override
    public boolean execute(CommandSender sender, Player player, String[] args) {
        String id = args[1];
        if (!Groups.exist(id)) return Message.GROUP_NOTEXIST.print(sender, id);
        Groups.create(id);
        return Message.GROUP_CREATE_OK.print(sender, id);
    }
}