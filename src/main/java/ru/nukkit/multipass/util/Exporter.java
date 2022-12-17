package ru.nukkit.multipass.util;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import ru.nukkit.multipass.MultipassPlugin;
import ru.nukkit.multipass.data.Providers;
import ru.nukkit.multipass.permissions.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class Exporter {

    private File file;

    public Exporter(String filename) {
        file = new File(MultipassPlugin.getPlugin().getDataFolder() + File.separator + filename + ".yml");
    }

    public Exporter() {
        this("import");
    }


    public boolean importPermissions(boolean overwrite) {
        if (!file.exists()) return false;
        Config cfg = new Config(file, Config.YAML);
        ConfigSection groupCfg = cfg.getSection("groups");
        ConfigSection userCfg = cfg.getSection("users");
        if (groupCfg.isEmpty() && userCfg.isEmpty()) return false;
        List<Group> groups = new ArrayList<>();

        groupCfg.getSections().entrySet().forEach(e -> {
            Node node = sectionToPass((ConfigSection) e.getValue());
            Group group = new Group(e.getKey(), node);
            ConfigSection worlds = cfg.getSection("worlds");
            for (String world : worlds.getKeys(false)) {
                ConfigSection ws = worlds.getSection(world);
                if (ws.isEmpty()) continue;
                Node wpass = sectionToPass(ws);
                group.setWorldPass(world, wpass);
            }
            groups.add(group);
        });

        List<User> users = new ArrayList<>();
        userCfg.getSections().entrySet().forEach(e -> {
            Node node = sectionToPass((ConfigSection) e.getValue());
            String name = e.getKey();
            /*try {
                UUID uuid = UUID.fromString(name);
                String fromUuid = Server.getInstance().getOfflinePlayer(uuid).getName();
                if (fromUuid != null) {
                    name = fromUuid;
                    System.out.println("Converted " + uuid + " to " + name);
                }
            } catch (Exception ignore) {
            }*/
            User user = new User(name, node);
            ConfigSection worlds = cfg.getSection("worlds");
            for (String world : worlds.getKeys(false)) {
                ConfigSection ws = worlds.getSection(world);
                if (ws.isEmpty()) continue;
                Node wpass = sectionToPass(ws);
                user.setWorldPass(world, wpass);
            }
            users.add(user);
        });
        /*if (groups.isEmpty() && users.isEmpty()) return false;
        Groups.updateGroups(groups, overwrite);
        Users.updateUsers(users, overwrite);
        Providers.saveGroups();
        Providers.saveUsers(users);*/
        CompletableFuture.runAsync(() -> {
            groups.forEach((g) -> Providers.getCurrentProvider().saveGroup(g));
            System.out.println(groups.size() + " groups imported");
            CompletableFuture.runAsync(() -> {
                users.forEach((u) -> Providers.getCurrentProvider().saveUser(u));
                System.out.println(users.size() + " users imported");
            });
        });
        return true;
    }


    public boolean exportPermissions() {
        ConfigSection groupSection = new ConfigSection();
        Groups.getAll().forEach(g -> {
            ConfigSection group = passToSection(g);
            ConfigSection worlds = new ConfigSection();
            g.getWorldPass().entrySet().forEach(e -> {
                worlds.set(e.getKey(), passToSection(e.getValue()));
            });
            if (!worlds.isEmpty()) group.set("worlds", worlds);
            groupSection.set(g.getName(), group);
        });

        ConfigSection userSection = new ConfigSection();
        Providers.getCurrentProvider().getAllUsers().forEach(u -> {
            ConfigSection users = passToSection(u);
            ConfigSection worlds = new ConfigSection();
            u.getWorldPass().entrySet().forEach(e -> {
                worlds.set(e.getKey(), passToSection(e.getValue()));
            });
            if (!worlds.isEmpty()) users.set("worlds", worlds);
            userSection.set(u.getName(), users);
        });
        if (groupSection.isEmpty() && userSection.isEmpty()) return false;
        if (!file.exists()) file.delete();
        Config cfg = new Config();
        if (!groupSection.isEmpty()) cfg.set("groups", groupSection);
        if (!userSection.isEmpty()) cfg.set("users", userSection);
        cfg.save(file);
        return true;
    }


    private ConfigSection passToSection(Node node) {
        ConfigSection section = new ConfigSection();
        section.set("groups", node.getGroupList());
        section.set("permissions", node.getPermissionList());
        section.set("prefix", node.getPrefix());
        section.set("suffix", node.getSuffix());
        section.set("priority", node.getPriority());
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
