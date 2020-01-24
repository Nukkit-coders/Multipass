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
import cn.nukkit.player.Player;

import java.util.Collection;

public class Util {

    public static String join(Collection<String> collection) {
        return join(collection, ", ");
    }

    public static String join(String[] ln, int num) {
        if (ln.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ln.length; i++) {
            if (i < num) continue;
            if (sb.length() > 0) sb.append(" ");
            sb.append(ln[i]);
        }
        return sb.toString();
    }

    public static String join(Collection<String> collection, String divider) {
        if (divider == null) divider = " ";
        StringBuilder sb = new StringBuilder();
        for (String s : collection) {
            if (sb.length() > 0) sb.append(divider);
            sb.append(s);
        }
        return sb.toString();
    }

    public static Player getPlayer(String userName) {
        return Server.getInstance().getPlayerExact(userName);
    }

    public static void informMessage(String userName, Message message, Object... o) {
        Player player = getPlayer(userName);
        if (player == null || !player.hasPermission("multipass.informed-user")) return;
        message.print(player, o);
    }
}