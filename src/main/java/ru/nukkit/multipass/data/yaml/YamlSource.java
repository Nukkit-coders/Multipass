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

package ru.nukkit.multipass.data.yaml;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import ru.nukkit.multipass.MultipassPlugin;
import ru.nukkit.multipass.data.DataProvider;
import ru.nukkit.multipass.permissions.Group;
import ru.nukkit.multipass.permissions.Groups;
import ru.nukkit.multipass.permissions.Node;
import ru.nukkit.multipass.permissions.User;
import ru.nukkit.multipass.util.Message;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class YamlSource extends DataProvider {

    private final File userDir;
    private final File groupFile;

    public YamlSource() {
        this.userDir = new File(MultipassPlugin.getPlugin().getDataFolder() + File.separator + "users");
        this.userDir.mkdirs();
        this.groupFile = new File(MultipassPlugin.getPlugin().getDataFolder() + File.separator + "groups.yml");
    }

    private File getUserFile(String user) {
        return new File(userDir + File.separator + user.toLowerCase() + ".yml");
    }

    @Override
    public User loadUser(String playerName) {
        Message.debugMessage("Loading permissions", playerName);
        File file = getUserFile(playerName);
        User user = new User(playerName);
        Config cfg = new Config(Config.YAML);
        if (file.exists()) {
            cfg.load(file.toString());
            Node node = sectionToPass(cfg.getRootSection());
            user = new User(playerName, node);

            ConfigSection worlds = cfg.getSection("worlds");
            for (String world : worlds.getKeys(false)) {
                ConfigSection ws = worlds.getSection(world);
                if (ws.isEmpty()) continue;
                Node wpass = sectionToPass(ws);
                user.setWorldPass(world, wpass);
            }
        }
        return user;
    }

    @Override
    public void removeUser(String playerName) {
        File file = getUserFile(playerName);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void saveUser(User user) {
        File file = getUserFile(user.getName());
        Message.debugMessage("Saving user file: ", file.toString());
        if (user.isEmpty()) {
            if (file.exists()) {
                file.delete();
            }
            Message.debugMessage(user.getName(), "data is empty. File removed");
        } else {
            Config cfg = new Config(file, Config.YAML);
            ConfigSection cfgSection = passToSection(user, true);
            cfg.setAll(cfgSection);
            ConfigSection worlds = new ConfigSection();
            user.getWorldPass().forEach((key, value) -> worlds.set(key, passToSection(value, false)));
            cfg.set("worlds", worlds);
            Message.debugMessage(user.getName(), " saved.");
            cfg.save(file);
        }
    }

    @Override
    public void saveUsers(Collection<User> users) {
        for (User u : users) {
            saveUser(u);
        }
    }

    @Override
    public void saveGroup(Group group) {
        saveGroups(Groups.getAll());
    }

    @Override
    public void removeGroup(String groupId) {
        saveGroups(Groups.getAll()); // просто сохраняем, поскольку сама группа из списка уже удалена
    }

    @Override
    public void saveGroups(Collection<Group> groups) {
        Config cfg = new Config();
        groups.forEach(g -> {
            ConfigSection group = passToSection(g, true);
            ConfigSection worlds = new ConfigSection();
            g.getWorldPass().forEach((key, value) -> worlds.set(key, passToSection(value, false)));
            group.set("worlds", worlds);
            cfg.set(g.getName(), group);
        });
        cfg.save(this.groupFile);
    }

    @Override
    public Collection<Group> loadGroups() {
        List<Group> groups = new ArrayList<>();
        if (!groupFile.exists()) return groups;
        Config cfg = new Config(groupFile, Config.YAML);
        cfg.getSections().forEach((key, value) -> {
            Node node = sectionToPass((ConfigSection) value);
            Group group = new Group(key, node);
            ConfigSection worlds = cfg.getSection("worlds");
            for (String world : worlds.getKeys(false)) {
                ConfigSection ws = worlds.getSection(world);
                if (ws.isEmpty()) continue;
                Node wpass = sectionToPass(ws);
                group.setWorldPass(world, wpass);
            }
            groups.add(group);
        });
        Message.debugMessage(groups.size(), "groups loaded");
        return groups;
    }

    @Override
    public boolean isStored(String userName) {
        return getUserFile(userName).exists();
    }

    @Override
    public Collection<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        for (String fileName : userDir.list()) {
            if (!fileName.matches(".+\\.yml$")) continue;
            users.add(loadUser(fileName.substring(0, fileName.length() - 4)));
        }
        return users;
    }

    @Override
    public void clearUsers() {
        for (File file : userDir.listFiles()) {
            if (!file.isDirectory()) file.delete();
        }
    }

    @Override
    public void clearGroups() {
        if (groupFile.exists()) groupFile.delete();
    }


    @Override
    public boolean isEnabled() {
        return true;
    }

    private ConfigSection passToSection(Node node, boolean saveAdditions) {
        ConfigSection section = new ConfigSection();
        section.set("groups", node.getGroupList());
        section.set("permissions", node.getPermissionList());
        if (saveAdditions) {
            section.set("prefix", node.getPrefix());
            section.set("suffix", node.getSuffix());
            section.set("priority", node.getPriority());
        }
        return section;
    }

    private Node sectionToPass(ConfigSection section) {
        Node node = new Node();
        node.setGroupList(section.getStringList("groups"));
        node.setPermissionsList(section.getStringList("permissions"));
        node.setPrefix(section.getString("prefix", ""));
        node.setSuffix(section.getString("suffix", ""));
        node.setPriority(section.getInt("priority", 0));
        return node;
    }
}