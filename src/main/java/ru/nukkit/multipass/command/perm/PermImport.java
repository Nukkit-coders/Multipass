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

import cn.nukkit.command.CommandSender;
import cn.nukkit.player.Player;
import ru.nukkit.multipass.command.Cmd;
import ru.nukkit.multipass.command.CmdDefine;
import ru.nukkit.multipass.util.Exporter;
import ru.nukkit.multipass.util.Message;

@CmdDefine(command = "perm", alias = "permission", allowConsole = true, subCommands = "import", permission = "multipass.admin", description = Message.CMD_PERM_IMPORT)
public class PermImport extends Cmd {
    @Override
    public boolean execute(CommandSender sender, Player player, String[] args) {
        boolean overwrite = false;
        String fileName = null;
        if (args.length == 1) {
            fileName = "import";
        } else if (args.length == 2) {
            if (args[1].matches("(?i)overwrite|over")) {
                overwrite = true;
                fileName = "import";
            } else fileName = args[1];
        } else if (args.length == 3) {
            if (args[1].matches("(?i)overwrite|over")) {
                overwrite = true;
            } else {
                fileName = args[2];
            }
        }
        if (fileName == null) return Message.CMD_PERM_IMPORT.print(sender);
        Exporter exporter = new Exporter(fileName);
        if (exporter.importPermissions(overwrite)) Message.PERM_IMPORT_OK.print(sender, fileName);
        else Message.PERM_IMPORT_FAILED.print(sender, fileName);
        return true;
    }
}
