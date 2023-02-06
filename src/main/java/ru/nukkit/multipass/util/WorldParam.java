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

public class WorldParam {
    public String world;
    public String param;

    public WorldParam(String param) {
        this.world = "";
        this.param = param;
        if (param.contains(":")) {
            String[] ln = param.split(":");
            this.world = ln[0];
            this.param = ln[1];
        }
    }

    public WorldParam(String[] args, int num) {
        if (args.length <= num + 1) {
            this.world = "";
            this.param = args[num];
        } else {
            this.world = args[num];
            this.param = args[num + 1];
        }
    }

    public boolean hasWorld() {
        return !world.isEmpty();
    }

    public Message message(Message msg, Message worldMsg) {
        return world.isEmpty() ? msg : worldMsg;
    }
}
