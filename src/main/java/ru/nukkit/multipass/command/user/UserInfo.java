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

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import ru.nukkit.multipass.Multipass;
import ru.nukkit.multipass.command.Cmd;
import ru.nukkit.multipass.command.CmdDefine;
import ru.nukkit.multipass.permissions.Users;
import ru.nukkit.multipass.util.Message;
import ru.nukkit.multipass.util.Paginator;
import ru.nukkit.multipass.util.Util;

import java.util.ArrayList;
import java.util.List;

@CmdDefine(command = "user", alias = "userperm", allowConsole = true, subCommands = "[A-Za-z0-9 ]{1,15}", permission = "multipass.admin", description = Message.CMD_USER)
public class UserInfo extends Cmd {
    @Override
    public boolean execute(CommandSender sender, Player player, String[] args) {
        if (args.length != 1) return false;
        String userName = args[0];
        Users.isRegistered(userName).whenComplete((register, e) -> {
            if (e != null) {
                e.printStackTrace();
            } else {
                if (register) {
                    int page = args.length == 3 && args[2].matches("\\d+") ? Integer.parseInt(args[2]) : 1;
                    Users.getUser(userName).whenComplete((user, e2) -> {
                        if (e2 != null) {
                            e2.printStackTrace();
                        } else {

                            List<String> print = new ArrayList<>();
                            List<String> ln = Multipass.getPrefixes(userName);
                            if (!ln.isEmpty()) print.add(Message.PERM_USER_PREFIX.getText(Util.join(ln)));
                            ln = Multipass.getSuffixes(userName);
                            if (!ln.isEmpty()) print.add(Message.PERM_USER_SUFFIX.getText(Util.join(ln)));
                            ln = user.getGroupList();
                            if (!ln.isEmpty()) print.add(Message.PERM_USER_GROUPS.getText(Util.join(ln)));
                            List<String> pln = user.getPermissionList();
                            if (!pln.isEmpty()) {
                                print.add(Message.PERM_USER_PERMS.getText());
                                for (String s : pln) {
                                    print.add(Message.color2(s));
                                }
                            }
                            Paginator.printPage(sender, print, Message.PERM_USER_INFO.getText(userName), page);
                        }
                    });

                } else {
                    Message.PERM_USER_NOTREGISTER.print(sender, userName);
                }
            }
        });
        return true;
    }
}
