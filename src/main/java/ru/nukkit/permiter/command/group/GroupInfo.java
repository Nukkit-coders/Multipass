package ru.nukkit.permiter.command.group;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import ru.nukkit.permiter.command.Cmd;
import ru.nukkit.permiter.command.CmdDefine;
import ru.nukkit.permiter.permissions.Group;
import ru.nukkit.permiter.permissions.Groups;
import ru.nukkit.permiter.util.Message;
import ru.nukkit.permiter.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by Igor on 12.05.2016.
 */

@CmdDefine(command = "group", alias = "groupperm", allowConsole = true, subCommands = {}, permission = "permiter.admin", description = Message.CMD_GROUP_REMOVE)
public class GroupInfo extends Cmd {


    @Override
    public boolean execute(CommandSender sender, Player player, String[] args) {
        if (args.length > 1) return false;
        if (args.length == 0) {
            Collection<Group> groups = Groups.getAll();
            if (groups == null || groups.isEmpty()) return Message.GROUP_LISTNOTFOUND.print(sender);
            else {
                List<String> groupList = new ArrayList<>();
                groups.forEach(g -> groupList.add(g.getName()));
                return Message.GROUP_LIST.print(sender, StringUtil.join(groupList));
            }
        } else {
            Group group = Groups.getGroup(args[0]);
            if (group == null) return Message.GROUP_INFOUNKNOWN.print(sender, args[0]);
            Message.GROUP_INFO_TITLE.print(sender, group.getName());
            List<String> ln = group.getGroupList();
            if (!ln.isEmpty()) Message.GROUP_INFO_GROUPS.print(sender, StringUtil.join(ln));
            ln = group.getPermissionList();
            if (!ln.isEmpty()) {
                Message.GROUP_INFO_PERMTITLE.print(sender);
                ln.forEach(s -> sender.sendMessage(Message.color1(s)));
            }
        }
        return true;
    }
}