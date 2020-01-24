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

package ru.nukkit.multipass.permissions;

import cn.nukkit.Server;
import cn.nukkit.player.Player;
import ru.nukkit.multipass.MultipassPlugin;
import ru.nukkit.multipass.data.Providers;
import ru.nukkit.multipass.event.PermissionsUpdateEvent;
import ru.nukkit.multipass.util.Message;
import ru.nukkit.multipass.util.WorldParam;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;


public class Users {

    private static ConcurrentMap<String, User> users = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);

    public static CompletableFuture<User> loadUser(Player player) {
        return loadUser(player.getName());
    }

    public static CompletableFuture<User> loadUser(String playerName) {
        CompletableFuture<User> result = new CompletableFuture<>();
        Providers.loadUser(playerName).whenComplete((loadedUser, e) -> {
            if (e != null) {
                e.printStackTrace();
            } else {
                User user = loadedUser == null ? new User(playerName) : loadedUser;
                user.recalculatePermissions();
                users.put(playerName, user);
                result.complete(user);
            }
        });
        return result;
    }

    public static CompletableFuture<User> getUser(String playerName) {
        if (users.containsKey(playerName)) {
            CompletableFuture<User> result = new CompletableFuture<>();
            result.complete(users.get(playerName));
            return result;
        } else {
            return loadUser(playerName);
        }
    }

    public static void closeUser(Player player) {
        if (users.containsKey(player.getName())) users.remove(player.getName());
    }

    public static void closeOfflineUsers() {
        for (Iterator<Map.Entry<String, User>> iterator = users.entrySet().iterator(); iterator.hasNext(); ) {
            if (Server.getInstance().getPlayerExact(iterator.next().getKey()) == null) iterator.remove();
        }
    }


    public static CompletableFuture<Boolean> isRegistered(String userName) {
        if (users.containsKey(userName)) {
            CompletableFuture<Boolean> result = new CompletableFuture<>();
            result.complete(true);
            return result;
        } else {
            return Providers.isRegistered(userName);
        }
    }

    public static void addGroup(String id, WorldParam wp) {
        if (wp.hasWorld()) addGroup(wp.world, id, wp.param);
        else addGroup(id, wp.param);
    }

    public static void addGroup(String world, String id, String group) {
        Users.getUser(id).whenComplete((user, e) -> {
            if (e == null) {
                user.addGroup(world, group);
                saveUser(user);
            }
        });
    }

    public static void addGroup(String id, String group) {
        Users.getUser(id).whenComplete((user, e) -> {
            if (e == null) {
                user.addGroup(group);
                saveUser(user);
            }
        });
    }

    public static boolean inGroup(String userName, WorldParam wp) {
        return wp.hasWorld() ? inGroup(wp.world, userName, wp.param) : inGroup(userName, wp.param);
    }

    public static boolean inGroup(String userName, String group) {
        try {
            User user = Users.getUser(userName).get();
            return user.inGroup(group);
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean inGroup(String world, String userName, String group) {
        try {
            User user = Users.getUser(userName).get();
            return user.inGroup(world, group);
        } catch (Exception e) {
        }
        return false;
    }

    public static void removeGroup(String userName, WorldParam wp) {
        if (wp == null) {
            removeGroup(userName, wp.param);
        } else {
            removeGroup(wp.world, userName, wp.param);
        }
    }

    public static void removeGroup(String userName, String group) {
        Users.getUser(userName).whenComplete((user, e) -> {
            if (e == null) {
                if (user.inGroup(group)) user.removeGroup(group);
                saveUser(user);
            }
        });
    }

    public static void removeGroup(String world, String userName, String group) {
        Users.getUser(userName).whenComplete((user, e) -> {
            if (e == null) {
                if (user.inGroup(group)) {
                    user.removeGroup(world, group);
                }
                saveUser(user);
            }
        });
    }

    public static boolean isPermissionSet(String userName, WorldParam wp) {
        return wp.hasWorld() ? isPermissionSet(wp.world, userName, wp.param) : isPermissionSet(userName, wp.param);

    }

    public static boolean isPermissionSet(String world, String userName, String permStr) {
        try {
            User user = Users.getUser(userName).get();
            return user.isPermissionSet(world, permStr);
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean isPermissionSet(String userName, String permStr) {
        try {
            User user = Users.getUser(userName).get();
            return user.isPermissionSet(permStr);
        } catch (Exception e) {
        }
        return false;
    }

    public static void removePermission(String userName, WorldParam wp) {
        if (wp.hasWorld()) removePermission(wp.world, userName, wp.param);
        else removePermission(userName, wp.param);
    }

    public static void removePermission(String userName, String permStr) {
        Users.getUser(userName).whenComplete((user, e) -> {
            if (e == null) {
                user.removePermission(permStr);
                saveUser(user);
            }
        });
    }

    public static void removePermission(String world, String userName, String permStr) {
        Users.getUser(userName).whenComplete((user, e) -> {
            if (e == null) {
                user.removePermission(world, permStr);
                saveUser(user);
            }
        });
    }

    public static void setGroup(String userName, WorldParam wp) {
        if (wp.hasWorld()) setGroup(wp.world, userName, wp.param);
        else setGroup(userName, wp.param);
    }

    public static void setGroup(String userName, String groupStr) {
        Users.getUser(userName).whenComplete((user, e) -> {
            if (e == null) {
                user.setGroup(groupStr);
                saveUser(user);
            }
        });
    }

    public static void setGroup(String world, String userName, String groupStr) {
        Users.getUser(userName).whenComplete((user, e) -> {
            if (e == null) {
                user.setGroup(groupStr);
                saveUser(user);
            }
        });

    }

    public static void setPrefix(String userName, String prefix) {
        Users.getUser(userName).whenComplete((user, e) -> {
            if (e == null) {
                user.setPrefix(prefix);
                saveUser(user);
            }
        });
    }

    public static void setSuffix(String userName, String suffix) {
        Users.getUser(userName).whenComplete((user, e) -> {
            if (e == null) {
                user.setSuffix(suffix);
                saveUser(user);
            }
        });
    }

    public static void setPriority(String userName, int priority) {
        Users.getUser(userName).whenComplete((user, e) -> {
            if (e == null) {
                user.setPriority(priority);
                saveUser(user);
            }
        });
    }

    public static void recalculatePermissions(String userName) {
        Users.getUser(userName).whenComplete((user, e) -> {
            if (e == null) {
                Server.getInstance().getScheduler().scheduleTask(MultipassPlugin.getPlugin(), user::recalculatePermissions);
            }
        });
    }

    public static void setPermission(String userName, WorldParam wp) {
        if (wp.hasWorld()) {
            setPermission(wp.world, userName, wp.param);
        } else {
            setPermission(userName, wp.param);
        }
    }

    public static void setPermission(String userName, String permStr) {
        Users.getUser(userName).whenComplete((user, e) -> {
            if (e == null) {
                user.setPermission(permStr);
                saveUser(user);
            }
        });
    }

    public static void setPermission(String world, String userName, String permission) {
        Users.getUser(userName).whenComplete((user, e) -> {
            if (e == null) {
                user.setPermission(world, permission);
                saveUser(user);
            }
        });
    }

    public static void reloadUsers() {
        users.clear();
        Server.getInstance().getOnlinePlayers().values().forEach(player -> loadUser(player.getName()));
    }

    public static void recalculatePermissions() {
        Message.debugMessage("Recalculating online users permissions");
        Server.getInstance().getOnlinePlayers().values().forEach(player -> {
            Users.getUser(player.getName()).whenComplete((user, e) -> {
                if (e == null) {
                    user.recalculatePermissions();
                }
            });
        });
    }

    public static void saveUser(User user) {
        Message.debugMessage("Saving user: " + user.getName());
        user.recalculatePermissions();
        Providers.saveUser(user);
        PermissionsUpdateEvent event = new PermissionsUpdateEvent(user.getName());
        Server.getInstance().getPluginManager().callEvent(event);
    }

    public static void setUser(User user) {
        if (user == null) return;
        users.put(user.getName(), user);
        user.recalculatePermissions();
    }

    public static void updateUsers(List<User> newUsers, boolean overwrite) {
        if (overwrite) {
            users.clear();
        }
        for (User user : newUsers) {
            users.put(user.getName(), user);
        }
        Iterator<Map.Entry<String, User>> iterator = users.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, User> e = iterator.next();
            Player player = Server.getInstance().getPlayerExact(e.getKey());
            if (player == null) iterator.remove();
            else e.getValue().recalculatePermissions();
        }
    }


    public static boolean isLoaded(String playerName) {
        return users.containsKey(playerName);
    }

    public static void remove(String id) {
        if (users.containsKey(id)) {
            users.remove(id);
        }
        Providers.removeUser(id).whenComplete((remove, e) -> {
            if (e != null) {
                e.printStackTrace();
            } else {
                Player player = Server.getInstance().getPlayerExact(id);
                if (player != null) {
                    setUser(new User(id));
                }
            }
        });
    }
}