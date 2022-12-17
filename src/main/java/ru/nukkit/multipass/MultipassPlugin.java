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

package ru.nukkit.multipass;

import cn.nukkit.plugin.PluginBase;
import ru.nukkit.multipass.command.Commander;
import ru.nukkit.multipass.data.Providers;
import ru.nukkit.multipass.permissions.Groups;
import ru.nukkit.multipass.util.Cfg;
import ru.nukkit.multipass.util.Message;

public class MultipassPlugin extends PluginBase {

    private static MultipassPlugin instance;
    private Cfg cfg;

    public static MultipassPlugin getPlugin() {
        return instance;
    }

    public static Cfg getCfg() {
        return instance.cfg;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveResources();
        cfg = new Cfg();
        cfg.load();
        Message.init(this);
        Commander.init(this);
        Providers.init();
        Groups.init();
        getServer().getPluginManager().registerEvents(new LeelooListener(), this);
    }

    private void saveResources() {
        this.getDataFolder().mkdirs();
        this.saveDefaultConfig();
        this.saveResource("groups.yml", false);
    }
}