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

package ru.nukkit.multipass.command.user;

import cn.nukkit.command.CommandSender;
import cn.nukkit.player.Player;
import ru.nukkit.multipass.command.Cmd;
import ru.nukkit.multipass.command.CmdDefine;
import ru.nukkit.multipass.permissions.Users;
import ru.nukkit.multipass.util.Message;
import ru.nukkit.multipass.util.Util;

@CmdDefine(command = "user", alias = "userperm", allowConsole = true, subCommands = {"[A-Za-z0-9 ]{1,15}", "setsuffix|suffix|sx", "\\S+"}, permission = "multipass.admin", description = Message.CMD_USER_SETSUFFIX)
public class UserSetSuffix extends Cmd {
    @Override
    public boolean execute(CommandSender sender, Player player, String[] args) {
        String userName = args[0];
        String suffix = Util.join(args, 2);
        Users.setSuffix(userName, suffix);
        Util.informMessage(userName, Message.USER_SUFFIX_OK_INFORM, suffix);
        return Message.USER_SUFFIX_OK.print(sender, userName, suffix);
    }
}
