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

package ru.nukkit.multipass.data;

import cn.nukkit.Server;
import org.sql2o.Sql2o;
import ru.nukkit.dblib.DbLib;
import ru.nukkit.multipass.MultipassPlugin;
import ru.nukkit.multipass.data.database.DatabaseSource;
import ru.nukkit.multipass.data.yaml.YamlSource;
import ru.nukkit.multipass.permissions.Group;
import ru.nukkit.multipass.permissions.Groups;
import ru.nukkit.multipass.permissions.User;
import ru.nukkit.multipass.util.Cfg;
import ru.nukkit.multipass.util.Message;
import ru.nukkit.multipass.util.MultiTask;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

public enum Providers {

    YAML(YamlSource.class),
    DATABASE(DatabaseSource.class);

    private final Class<? extends DataProvider> clazz;
    private DataProvider source;

    Providers(Class<? extends DataProvider> clazz) {
        this.clazz = clazz;
    }

    public DataProvider getSource() {
        try {
            source = clazz.newInstance();
        } catch (Exception e) {
            source = null;
            Message.PROVIDER_FAILED.log(this.name());
        }
        return this.source;
    }

    private static DataProvider currentProvider;


    public static void init() {
        Providers dp = getByName(MultipassPlugin.getCfg().dataSource);
        if (dp == null) {
            Message.LOG_UNKNOWN_DATAPROVIDER.log(MultipassPlugin.getCfg().dataSource);
            dp = YAML;
        } else {
            Message.LOG_DATAPROVIDER.log(dp.name());
        }
        currentProvider = dp.getSource();
        if (currentProvider == null || !currentProvider.isEnabled()) {
            Message.LOG_DATAPROVIDER_FAIL.log(MultipassPlugin.getCfg().dataSource);
        }
    }

    public static CompletableFuture<User> loadUser(String playerName) {
        CompletableFuture<User> result = new CompletableFuture<>();
        new MultiTask() {
            @Override
            public void onRun() {
                result.complete(currentProvider.loadUser(playerName));
            }
        }.start();
        return result;
    }

    public static CompletableFuture<Boolean> isRegistered(String userName) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        new MultiTask() {
            @Override
            public void onRun() {
                result.complete(currentProvider.isStored(userName));
            }
        }.start();
        return result;
    }

    public static CompletableFuture<Boolean> saveUser(User user) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        new MultiTask() {
            @Override
            public void onRun() {
                currentProvider.saveUser(user);
                result.complete(true);
            }
        }.start();
        return result;
    }


    public static CompletableFuture<Boolean> saveGroup(Group group) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        new MultiTask() {
            @Override
            public void onRun() {
                currentProvider.saveGroup(group);
                result.complete(true);
            }
        }.start();
        return result;
    }

    public static CompletableFuture<Boolean> saveUsers(Collection<User> users) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        new MultiTask() {
            @Override
            public void onRun() {
                currentProvider.saveUsers(users);
                result.complete(true);
            }
        }.start();
        return result;
    }

    public static CompletableFuture<Boolean> saveGroups() {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        new MultiTask() {
            @Override
            public void onRun() {
                currentProvider.saveGroups(Groups.getAll());
                result.complete(true);
            }
        }.start();
        return result;
    }

    public static CompletableFuture<Map<String, Group>> loadGroups() {
        CompletableFuture<Map<String, Group>> result = new CompletableFuture<>();
        new MultiTask() {
            @Override
            public void onRun() {
                Map<String, Group> groups = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                currentProvider.loadGroups().forEach(group -> groups.put(group.getName(), group));
                result.complete(groups);
            }
        }.start();
        return result;
    }

    public static Providers getByName(String name) {
        for (Providers dp : values()) {
            if (name.equalsIgnoreCase(dp.name())) return dp;
        }
        return null;
    }

    public static CompletableFuture<Boolean> clearUsers() {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        new MultiTask() {
            @Override
            public void onRun() {
                currentProvider.clearUsers();
                result.complete(true);
            }
        }.start();
        return result;
    }

    public static CompletableFuture<Boolean> clearGroups() {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        new MultiTask() {
            @Override
            public void onRun() {
                currentProvider.clearGroups();
                result.complete(true);
            }
        }.start();
        return result;
    }

    public static CompletableFuture<Collection<User>> getAllUsers() {
        CompletableFuture<Collection<User>> result = new CompletableFuture<>();
        new MultiTask() {
            @Override
            public void onRun() {
                result.complete(currentProvider.getAllUsers());
            }
        }.start();
        return result;
    }


    public static Sql2o getSql2o() {
        if (Server.getInstance().getPluginManager().getPlugin("DbLib") == null) {
            Message.DB_DBLIB_NOTFOUND.log();
            return null;
        }
        Cfg cfg = MultipassPlugin.getCfg();
        switch (cfg.dblibSource.toLowerCase()) {
            case "dblib":
            case "default":
                return DbLib.getSql2o();
            case "sqlite":
                File dbFile = new File(MultipassPlugin.getPlugin().getDataFolder() + File.separator + cfg.customSQLite);
                return DbLib.getSql2o(DbLib.getSqliteUrl(dbFile), "", "");
            case "mysql":
                return DbLib.getSql2oMySql(cfg.mysqlHost, cfg.mysqlPort, cfg.mysqlDb, cfg.mysqlName, cfg.mysqlPassword);
        }
        return null;
    }

    public static DataProvider getCurrentProvider() {
        return currentProvider;
    }

    public static CompletableFuture<Boolean> removeGroup(String groupId) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        new MultiTask() {
            @Override
            public void onRun() {
                getCurrentProvider().removeGroup(groupId);
                result.complete(true);
            }
        }.start();
        return result;
    }


    public static CompletableFuture<Boolean> removeUser(String userName) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        new MultiTask() {
            @Override
            public void onRun() {
                getCurrentProvider().removeUser(userName);
                result.complete(true);
            }
        }.start();
        return result;
    }
}
