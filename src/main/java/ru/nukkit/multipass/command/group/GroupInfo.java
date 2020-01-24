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

package ru.nukkit.multipass.command.group;

import cn.nukkit.command.CommandSender;
import cn.nukkit.player.Player;
import ru.nukkit.multipass.command.Cmd;
import ru.nukkit.multipass.command.CmdDefine;
import ru.nukkit.multipass.permissions.Group;
import ru.nukkit.multipass.permissions.Groups;
import ru.nukkit.multipass.util.Message;
import ru.nukkit.multipass.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@CmdDefine(command = "group", alias = "groupperm", allowConsole = true, subCommands = {}, permission = "multipass.admin", description = Message.CMD_GROUP_REMOVE)
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
                return Message.GROUP_LIST.print(sender, Util.join(groupList));
            }
        } else {
            Group group = Groups.getGroup(args[0]);
            if (group == null) return Message.GROUP_INFOUNKNOWN.print(sender, args[0]);
            Message.GROUP_INFO_TITLE.print(sender, group.getName());
            List<String> ln = group.getGroupList();
            if (!ln.isEmpty()) Message.GROUP_INFO_GROUPS.print(sender, Util.join(ln));
            List<String> pln = group.getPermissionList();
            if (!pln.isEmpty()) {
                Message.GROUP_INFO_PERMTITLE.print(sender);
                pln.forEach(s -> sender.sendMessage(Message.color1(s)));
            }
        }
        return true;
    }
}
