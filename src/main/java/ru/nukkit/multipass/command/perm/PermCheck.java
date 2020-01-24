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

package ru.nukkit.multipass.command.perm;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.player.Player;
import ru.nukkit.multipass.command.Cmd;
import ru.nukkit.multipass.command.CmdDefine;
import ru.nukkit.multipass.util.Message;

@CmdDefine(command = "perm", alias = "permission", allowConsole = true, subCommands = {"check|chk|test|tst", "\\S+", "\\S+"}, permission = "multipass.admin", description = Message.CMD_PERM_CHECK)
public class PermCheck extends Cmd {
    @Override
    public boolean execute(CommandSender sender, Player player, String[] args) {
        Player p = Server.getInstance().getPlayerExact(args[1]);
        if (p == null) return Message.PERM_CHECK_NULLPLAYER.print(sender, args[1]);
        Message m = p.hasPermission(args[2]) ? Message.PERM_CHECK_HAS : Message.PERM_CHECK_HASNT;
        return m.print(sender, p.getName(), args[2]);
    }
}
