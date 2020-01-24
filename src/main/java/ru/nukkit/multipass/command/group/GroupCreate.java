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
import ru.nukkit.multipass.permissions.Groups;
import ru.nukkit.multipass.util.Message;

@CmdDefine(command = "group", alias = "groupperm", allowConsole = true, subCommands = {"create|new", "\\S+"}, permission = "multipass.admin", description = Message.CMD_GROUP_CREATE)
public class GroupCreate extends Cmd {
    @Override
    public boolean execute(CommandSender sender, Player player, String[] args) {
        String id = args[1];
        if (Groups.exist(id)) return Message.GROUP_EXIST.print(sender, id);
        Groups.create(id);
        return Message.GROUP_CREATE_OK.print(sender, id);
    }
}
