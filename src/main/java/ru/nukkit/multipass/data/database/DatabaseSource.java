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

package ru.nukkit.multipass.data.database;

import cn.nukkit.Server;
import cn.nukkit.player.Player;
import cn.nukkit.scheduler.TaskHandler;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.nukkit.multipass.MultipassPlugin;
import ru.nukkit.multipass.data.DataProvider;
import ru.nukkit.multipass.data.Providers;
import ru.nukkit.multipass.permissions.Group;
import ru.nukkit.multipass.permissions.Permission;
import ru.nukkit.multipass.permissions.User;
import ru.nukkit.multipass.permissions.Users;
import ru.nukkit.multipass.util.TimeUtil;

import java.util.*;

public class DatabaseSource extends DataProvider {
    private String createUserTable;
    private String createUsersGroupTable;
    private String createUsersPermTable;
    private String createOrReplaceUser;
    private String deleteUser;
    private String deleteUserPerm;
    private String deleteUserGroup;
    private String deleteAllUsers;
    private String deleteAllUserPerms;
    private String deleteAllUserGroups;
    private String addUserPerm;
    private String addUserGroup;
    private String selectUserPerms;
    private String selectUserGroups;
    private String selectAllUsers;
    private String selectUser;
    private String createGroupTable;
    private String createGroupsGroupTable;
    private String createGroupsPermTable;
    private String createOrReplaceGroup;
    private String deleteGroup;
    private String deleteGroupPerm;
    private String deleteGroupGroup;
    private String addGroup;
    private String addGroupPerm;
    private String addGroupGroup;
    private String deleteAllGroups;
    private String deleteAllGroupsPerm;
    private String deleteAllGroupsGroup;
    private String selectAllGroups;
    private String selectGroupPerms;
    private String selectGroupGroups;

    private boolean enabled;

    private Sql2o sql2o;


    public DatabaseSource() {
        enabled = false;

        this.sql2o = Providers.getSql2o();
        if (this.sql2o == null) return;

        prepareQueries();

        try (Connection con = sql2o.beginTransaction(java.sql.Connection.TRANSACTION_SERIALIZABLE)) {
            con.createQuery(createUserTable).executeUpdate();
            con.createQuery(createUsersGroupTable).executeUpdate();
            con.createQuery(createUsersPermTable).executeUpdate();
            con.createQuery(createGroupTable).executeUpdate();
            con.createQuery(createGroupsGroupTable).executeUpdate();
            con.createQuery(createGroupsPermTable).executeUpdate();
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        runRecheck();

        enabled = true;
    }

    private void prepareQueries() {
        String tablePrefix = MultipassPlugin.getCfg().tablePrefix;
        String mpUsers = tablePrefix + "users"; //mp_users
        String mpUsersGroup = tablePrefix + "users_group"; //mp_users_group
        String mpUsersPerm = tablePrefix + "users_perm"; //mp_users_perm
        String mpGroups = tablePrefix + "groups"; //mp_groups
        String mpGroupsGroup = tablePrefix + "groups_group"; //mp_groups_group
        String mpGroupsPerm = tablePrefix + "groups_perm"; //mp_groups_perm

        createUserTable = "CREATE TABLE IF NOT EXISTS " + mpUsers + " (name VARCHAR(200) NOT NULL, " +
                "prefix VARCHAR(200), suffix VARCHAR(200), priority INT, PRIMARY KEY(name))";

        createUsersGroupTable = "CREATE TABLE IF NOT EXISTS " + mpUsersGroup + " (user_id VARCHAR(200) NOT NULL, " +
                "world VARCHAR(200), subgroup VARCHAR(200))";

        createUsersPermTable = "CREATE TABLE IF NOT EXISTS " + mpUsersPerm + " (user_id VARCHAR(200) NOT NULL, " +
                "world VARCHAR(200), permission VARCHAR(200), positive TINYINT)";

        selectUser = "SELECT * FROM " + mpUsers + " WHERE name = :name";

        createOrReplaceUser = "REPLACE INTO " + mpUsers + " VALUES (:name, :prefix, :suffix, :priority)";

        deleteUser = "DELETE FROM " + mpUsers + " WHERE name = :name";

        deleteUserPerm = "DELETE FROM " + mpUsersPerm + " WHERE user_id = :name";

        deleteUserGroup = "DELETE FROM " + mpUsersGroup + " WHERE user_id = :name";

        addUserPerm = "INSERT INTO " + mpUsersPerm + " (user_id, world, permission, positive) " +
                "VALUES (:owner, :world, :permission, :positive)";

        addUserGroup = "INSERT INTO " + mpUsersGroup + " (user_id, world, subgroup) VALUES (:owner, :world, :subgroup)";

        selectUserPerms = "SELECT * FROM " + mpUsersPerm + " WHERE user_id = :user";

        selectUserGroups = "SELECT * FROM " + mpUsersGroup + " WHERE user_id = :user";

        selectAllUsers = "SELECT * FROM " + mpUsers;

        deleteAllUsers = "DELETE FROM " + mpUsers;

        deleteAllUserPerms = "DELETE FROM " + mpUsersPerm;

        deleteAllUserGroups = "DELETE FROM " + mpUsersGroup;

        createGroupTable = "CREATE TABLE IF NOT EXISTS " + mpGroups + " (name VARCHAR(200) NOT NULL, " +
                "prefix VARCHAR(200), suffix VARCHAR(200), priority INT, PRIMARY KEY(name))";

        createOrReplaceGroup = "REPLACE INTO " + mpGroups + " VALUES (:name, :prefix, :suffix, :priority)";

        addGroup = "INSERT INTO " + mpGroups + " (name, prefix, suffix, priority) " +
                "VALUES (:name, :prefix, :suffix, :priority)";

        addGroupPerm = "INSERT INTO " + mpGroupsPerm + " (group_id, world, permission, positive) " +
                "VALUES (:owner, :world, :permission, :positive)";

        addGroupGroup = "INSERT INTO " + mpGroupsGroup + " (group_id, world, subgroup) VALUES (:owner, :world, :subgroup)";

        createGroupsGroupTable = "CREATE TABLE IF NOT EXISTS " + mpGroupsGroup + " (group_id VARCHAR(200) NOT NULL, " +
                "world VARCHAR(200), subgroup VARCHAR(200))";

        createGroupsPermTable = "CREATE TABLE IF NOT EXISTS " + mpGroupsPerm + " (group_id VARCHAR(200) NOT NULL, " +
                "world VARCHAR(200), permission VARCHAR(200), positive TINYINT)";

        deleteAllGroups = "DELETE FROM " + mpGroups;

        deleteAllGroupsPerm = "DELETE FROM " + mpGroupsPerm;

        deleteGroup = "DELETE FROM " + mpGroups + " WHERE name = :name";

        deleteGroupPerm = "DELETE FROM " + mpGroupsPerm + " WHERE group_id = :name";

        deleteAllGroupsGroup = "DELETE FROM " + mpGroupsGroup;

        deleteGroupGroup = "DELETE FROM " + mpGroupsGroup + " WHERE group_id = :name";

        selectAllGroups = "SELECT * FROM " + mpGroups;

        selectGroupPerms = "SELECT * FROM " + mpGroupsPerm + " WHERE group_id = :group";

        selectGroupGroups = "SELECT * FROM " + mpGroupsGroup + " WHERE group_id = :group";
    }

    private TaskHandler runRecheck() {
        Long rescanTime = TimeUtil.parseTime(MultipassPlugin.getCfg().multiServerRecheck);
        if (rescanTime <= 0) return null;
        int delay = Math.max(TimeUtil.timeToTicks(rescanTime).intValue(), 20);
        return Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(MultipassPlugin.getPlugin(), () -> {
            Collection<Group> groups = null;
            List<User> users = new ArrayList<>();
            groups = loadGroups();
            for (Player player : Server.getInstance().getOnlinePlayers().values()) {
                User user = loadUser(player.getName());
                if (user != null) users.add(user);
            }
            if (groups != null) updateAllGroups(groups);
            if (users != null && !users.isEmpty()) {
                users.forEach(user -> Users.setUser(new User(user.getName(), user)));
            }
        }, delay, delay, true);
    }

    @Override
    public void saveUser(User user) {
        if (!enabled) return;
        try (Connection con = sql2o.beginTransaction(java.sql.Connection.TRANSACTION_SERIALIZABLE)) {
            con.createQuery(createOrReplaceUser)
                    .addParameter("name", user.getName())
                    .addParameter("prefix", user.getPrefix())
                    .addParameter("suffix", user.getSuffix())
                    .addParameter("priority", user.getPriority())
                    .executeUpdate();
            con.createQuery(deleteUserPerm)
                    .addParameter("name", user.getName())
                    .executeUpdate();
            con.createQuery(deleteUserGroup)
                    .addParameter("name", user.getName())
                    .executeUpdate();
            for (Map.Entry<String, List<Permission>> e : user.getPermissionsMap().entrySet()) {
                for (Permission p : e.getValue()) {
                    con.createQuery(addUserPerm)
                            .addParameter("owner", user.getName())
                            .addParameter("world", e.getKey())
                            .addParameter("permission", p.getName())
                            .addParameter("positive", p.isPositive() ? 1 : 0)
                            .executeUpdate();
                }
            }
            for (Map.Entry<String, List<String>> e : user.getGroupMap().entrySet()) {
                for (String g : e.getValue()) {
                    con.createQuery(addUserGroup)
                            .addParameter("owner", user.getName())
                            .addParameter("world", e.getKey())
                            .addParameter("subgroup", g)
                            .executeUpdate();
                }
            }
            con.commit();
        }
    }

    @Override
    public User loadUser(String playerName) {
        User user = new User(playerName);

        EntityTable userRecord;
        try (Connection con = sql2o.open()) {
            userRecord = con.createQuery(selectUser)
                    .addParameter("name", playerName)
                    .executeAndFetchFirst(EntityTable.class);
        }
        if (userRecord == null) return user;

        user.setPrefix(userRecord.prefix);
        user.setSuffix(userRecord.suffix);
        user.setPriority(userRecord.priority);

        List<PermTable> pt;
        try (Connection con = sql2o.open()) {
            pt = con.createQuery(selectUserPerms)
                    .throwOnMappingFailure(false)
                    .addColumnMapping("user", "owner")
                    .addParameter("user", playerName)
                    .executeAndFetch(PermTable.class);
        }
        if (pt != null) {
            for (PermTable p : pt) {
                user.setPermission(p.world, p.permission, p.positive);
            }
        }

        List<GroupsTable> gt;
        try (Connection con = sql2o.open()) {
            gt = con.createQuery(selectUserGroups)
                    .throwOnMappingFailure(false)
                    .addColumnMapping("user", "owner")
                    .addParameter("user", playerName)
                    .executeAndFetch(GroupsTable.class);
        }
        if (gt != null) {
            for (GroupsTable g : gt) {
                user.addGroup(g.world, g.subgroup);
            }
        }
        return user;
    }

    @Override
    public void removeUser(String playerName) {
        if (!enabled) return;
        try (Connection con = sql2o.beginTransaction(java.sql.Connection.TRANSACTION_SERIALIZABLE)) {
            con.createQuery(deleteUser)
                    .addParameter("name", playerName)
                    .executeUpdate();
            con.createQuery(deleteUserGroup)
                    .addParameter("name", playerName)
                    .executeUpdate();
            con.createQuery(deleteUserPerm)
                    .addParameter("name", playerName)
                    .executeUpdate();
            con.commit();
        }
    }

    @Override
    public void saveGroups(Collection<Group> all) {
        if (!enabled) return;
        try (Connection con = sql2o.beginTransaction(java.sql.Connection.TRANSACTION_SERIALIZABLE)) {
            con.createQuery(deleteAllGroups).executeUpdate();
            con.createQuery(deleteAllGroupsPerm).executeUpdate();
            con.createQuery(deleteAllGroupsGroup).executeUpdate();

            Query groupQuery = con.createQuery(addGroup);


            for (Group group : all) {
                Query groupGroupQuery = null;
                Query groupPermQuery = null;
                groupQuery
                        .addParameter("name", group.getName())
                        .addParameter("prefix", group.getPrefix())
                        .addParameter("suffix", group.getSuffix())
                        .addParameter("priority", group.getPriority());

                Map<String, List<Permission>> perms = group.getPermissionsMap();
                if (!perms.isEmpty()) {
                    groupPermQuery = con.createQuery(addGroupPerm);
                    for (Map.Entry<String, List<Permission>> e : perms.entrySet()) {
                        for (Permission p : e.getValue())
                            groupPermQuery
                                    .addParameter("owner", group.getName())
                                    .addParameter("world", e.getKey())
                                    .addParameter("permission", p.getName())
                                    .addParameter("positive", p.isPositive());
                    }
                }

                Map<String, List<String>> subgroups = group.getGroupMap();

                if (!subgroups.isEmpty()) {
                    groupGroupQuery = con.createQuery(addGroupGroup);
                    for (Map.Entry<String, List<String>> e : subgroups.entrySet()) {
                        for (String g : e.getValue()) {
                            groupGroupQuery.addParameter("owner", group.getName())
                                    .addParameter("world", e.getKey())
                                    .addParameter("subgroup", g);
                        }
                    }
                }
                if (groupPermQuery != null) groupPermQuery.executeUpdate();
                if (groupGroupQuery != null) groupGroupQuery.executeUpdate();
            }
            groupQuery.executeUpdate();
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<Group> loadGroups() {
        if (!enabled) return Collections.EMPTY_LIST;
        List<EntityTable> selGroups;
        try (Connection con = sql2o.open()) {
            selGroups = con.createQuery(selectAllGroups).executeAndFetch(EntityTable.class);
        }
        if (selGroups == null) return Collections.EMPTY_LIST;
        List<Group> groups = new ArrayList<>();
        for (EntityTable groupTable : selGroups) {
            Group group = new Group(groupTable.name, groupTable.prefix, groupTable.suffix, groupTable.priority);
            List<PermTable> permTables = null;
            try (Connection con = sql2o.open()) {
                permTables = con.createQuery(selectGroupPerms)
                        .throwOnMappingFailure(false)
                        .addColumnMapping("group_id", "owner")
                        .addParameter("group", group.getName())
                        .executeAndFetch(PermTable.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (permTables != null) {
                for (PermTable permTable : permTables) {
                    group.addPermission(permTable.world, permTable.permission, permTable.positive);
                }
            }
            List<GroupsTable> groupsTables;
            try (Connection con = sql2o.open()) {
                groupsTables = con.createQuery(selectGroupGroups)
                        .throwOnMappingFailure(false)
                        .addColumnMapping("group_id", "owner")
                        .addParameter("group", group.getName())
                        .executeAndFetch(GroupsTable.class);
            }
            if (groupsTables != null) {
                for (GroupsTable groupsTable : groupsTables) {
                    group.addGroup(groupsTable.world, groupsTable.subgroup);
                }
            }
            groups.add(group);
        }
        return groups;
    }

    @Override
    public boolean isStored(String userName) {
        if (!enabled) return false;
        EntityTable userRecord;
        try (Connection con = sql2o.open()) {
            userRecord = con.createQuery(selectUser)
                    .addParameter("name", userName)
                    .executeAndFetchFirst(EntityTable.class);
        }
        return userRecord != null;
    }

    @Override
    public Collection<User> getAllUsers() {
        if (!enabled) return Collections.EMPTY_LIST;


        List<EntityTable> selUsers;
        try (Connection con = sql2o.open()) {
            selUsers = con.createQuery(selectAllUsers).executeAndFetch(EntityTable.class);
        }
        if (selUsers == null) return Collections.EMPTY_LIST;

        List<User> users = new ArrayList<>();

        for (EntityTable userTable : selUsers) {
            User user = new User(userTable.name, userTable.prefix, userTable.suffix, userTable.priority);
            List<PermTable> permTables;
            try (Connection con = sql2o.open()) {
                permTables = con.createQuery(selectGroupPerms)
                        .throwOnMappingFailure(false)
                        .addColumnMapping("user", "owner")
                        .addParameter("user", user.getName())
                        .executeAndFetch(PermTable.class);
            }
            if (permTables != null) {
                for (PermTable permTable : permTables) {
                    user.addPermission(permTable.world, permTable.permission, permTable.positive);
                }
            }
            List<GroupsTable> groupsTables;
            try (Connection con = sql2o.open()) {
                groupsTables = con.createQuery(selectGroupGroups)
                        .throwOnMappingFailure(false)
                        .addColumnMapping("user", "owner")
                        .addParameter("user", user.getName())
                        .executeAndFetch(GroupsTable.class);
            }
            if (groupsTables != null) {
                for (GroupsTable groupsTable : groupsTables) {
                    user.addGroup(groupsTable.world, groupsTable.subgroup);
                }
            }
            users.add(user);
        }

        return users;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void clearUsers() {
        if (!enabled) return;
        try (Connection con = sql2o.beginTransaction(java.sql.Connection.TRANSACTION_SERIALIZABLE)) {
            con.createQuery(deleteAllUserPerms).executeUpdate();
            con.createQuery(deleteAllUserGroups).executeUpdate();
            con.createQuery(deleteAllUsers).executeUpdate();
            con.commit();
        }
    }

    @Override
    public void clearGroups() {
        if (!enabled) return;
        try (Connection con = sql2o.beginTransaction(java.sql.Connection.TRANSACTION_SERIALIZABLE)) {
            con.createQuery(deleteAllGroupsPerm).executeUpdate();
            con.createQuery(deleteAllGroupsGroup).executeUpdate();
            con.createQuery(deleteAllGroups).executeUpdate();
            con.commit();
        }


    }

    @Override
    public void saveUsers(Collection<User> users) {
        if (!enabled) return;
        try (Connection con = sql2o.beginTransaction(java.sql.Connection.TRANSACTION_SERIALIZABLE)) {
            con.createQuery(deleteAllUserPerms).executeUpdate();
            con.createQuery(deleteAllUserGroups).executeUpdate();
            con.createQuery(deleteAllUsers).executeUpdate();

            Query userQuery = con.createQuery(createOrReplaceUser);
            Query userGroupQuery = null;
            Query userPermQuery = null;
            for (User user : users) {
                userQuery.addParameter("name", user.getName())
                        .addParameter("prefix", user.getPrefix())
                        .addParameter("suffix", user.getSuffix())
                        .addParameter("priority", user.getPriority());

                Map<String, List<Permission>> perms = user.getPermissionsMap();
                if (!perms.isEmpty()) {
                    userPermQuery = con.createQuery(addUserPerm);
                    for (Map.Entry<String, List<Permission>> e : perms.entrySet()) {
                        for (Permission p : e.getValue())
                            userPermQuery.addParameter("owner", user.getName())
                                    .addParameter("world", e.getKey())
                                    .addParameter("permission", p.getName())
                                    .addParameter("positive", p.isPositive() ? 1 : 0);
                    }
                }

                Map<String, List<String>> subgroups = user.getGroupMap();

                if (!subgroups.isEmpty()) {
                    userGroupQuery = con.createQuery(addUserGroup);
                    for (Map.Entry<String, List<String>> e : subgroups.entrySet()) {
                        for (String g : e.getValue()) {
                            userGroupQuery.addParameter("owner", user.getName())
                                    .addParameter("world", e.getKey())
                                    .addParameter("subgroup", g);
                        }
                    }
                }
            }
            userQuery.executeUpdate();
            if (userPermQuery != null) userPermQuery.executeUpdate();
            if (userGroupQuery != null) userGroupQuery.executeUpdate();
            con.commit();
        }
    }

    @Override
    public void saveGroup(Group group) {
        if (!enabled) return;
        try (Connection con = sql2o.beginTransaction(java.sql.Connection.TRANSACTION_SERIALIZABLE)) {
            con.createQuery(createOrReplaceGroup)
                    .addParameter("name", group.getName())
                    .addParameter("prefix", group.getPrefix())
                    .addParameter("suffix", group.getSuffix())
                    .addParameter("priority", group.getPriority())
                    .executeUpdate();
            con.createQuery(deleteGroupPerm)
                    .addParameter("name", group.getName())
                    .executeUpdate();
            con.createQuery(deleteGroupGroup)
                    .addParameter("name", group.getName())
                    .executeUpdate();
            for (Map.Entry<String, List<Permission>> e : group.getPermissionsMap().entrySet()) {
                for (Permission p : e.getValue()) {
                    con.createQuery(addGroupPerm)
                            .addParameter("owner", group.getName())
                            .addParameter("world", e.getKey())
                            .addParameter("permission", p.getName())
                            .addParameter("positive", p.isPositive() ? 1 : 0)
                            .executeUpdate();
                }
            }
            for (Map.Entry<String, List<String>> e : group.getGroupMap().entrySet()) {
                for (String g : e.getValue()) {
                    con.createQuery(addGroupGroup)
                            .addParameter("owner", group.getName())
                            .addParameter("world", e.getKey())
                            .addParameter("subgroup", g)
                            .executeUpdate();
                }
            }
            con.commit();
        }
    }

    @Override
    public void removeGroup(String groupId) {
        if (!enabled) return;
        try (Connection con = sql2o.beginTransaction(java.sql.Connection.TRANSACTION_SERIALIZABLE)) {
            con.createQuery(deleteGroup)
                    .addParameter("name", groupId)
                    .executeUpdate();
            con.createQuery(deleteGroupGroup)
                    .addParameter("name", groupId)
                    .executeUpdate();
            con.createQuery(deleteGroupPerm)
                    .addParameter("name", groupId)
                    .executeUpdate();
            con.commit();
        }
    }
}
