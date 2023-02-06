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

import cn.nukkit.utils.ConfigSection;
import ru.nukkit.multipass.MultipassPlugin;
import ru.nukkit.multipass.util.HighBase;
import ru.nukkit.multipass.util.LowBase;

import java.util.*;


public abstract class BaseNode extends Node {

    protected String name;
    private final Map<String, Node> worldPass;

    public BaseNode(String name) {
        super();
        this.name = name;
        this.worldPass = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    public BaseNode(String playerName, Node node) {
        super(node);
        this.name = playerName;
        this.worldPass = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    public BaseNode(BaseNode node) {
        this(node.getName(), node);
        this.worldPass.putAll(node.worldPass);
    }

    public Set<Group> getAllGroups() {
        Set<Group> algroups = new TreeSet<>(new HighBase());
        getGroups().forEach(group -> {
            if (group != null && !algroups.contains(group)) {
                algroups.add(group);
                algroups.addAll(group.getAllGroups());
            }
        });
        return algroups;
    }

    public Set<BaseNode> getAllNodes() {
        return getAllNodes(true);
    }

    public Set<BaseNode> getAllNodes(boolean acsending) {
        Set<BaseNode> nodes = new TreeSet<>(acsending ? new HighBase() : new LowBase());//new TreeSet<>(new HighBase());
        nodes.add(this);
        getGroups().forEach(group -> {
            if (group != null && !nodes.contains(group)) {
                nodes.addAll(group.getAllNodes());
            }
        });
        return nodes;
    }

    public Map<String, List<Permission>> getPermissionsMap() {
        Map<String, List<Permission>> permMap = new LinkedHashMap<>();
        permissions.forEach(p -> {
            if (!permMap.containsKey("")) permMap.put("", new ArrayList<>());
            permMap.get("").add(p);
        });
        this.worldPass.forEach((key, value) -> {
            if (!permMap.containsKey(key)) permMap.put(key, new ArrayList<>());
            value.getPermissions().forEach(p -> permMap.get(key).add(p));
        });
        return permMap;
    }

    public Collection<Permission> getPermissions(String world) {
        return hasWorldPass(world) ? getWorldPass(world).getPermissions() : Collections.EMPTY_SET;
    }

    public void setPermissions(String world, Collection<Permission> permissions) {
        getWorldPassOrCreate(world).setPermissions(permissions);
    }

    public int getPriority(String world) {
        return hasWorldPass(world) ? getWorldPass(world).getPriority() : 0;
    }

    public void setPriority(String world, int priority) {
        getWorldPassOrCreate(world).setPriority(priority);
    }

    public String getPrefix(String world) {
        return hasWorldPass(world) ? getWorldPass(world).getPrefix() : "";
    }

    public void setPrefix(String world, String prefix) {
        getWorldPassOrCreate(world).setPrefix(prefix);
    }

    public String getSuffix(String world) {
        return hasWorldPass(world) ? getWorldPass(world).getSuffix() : "";
    }

    public void setSuffix(String world, String suffix) {
        getWorldPassOrCreate(world).setSuffix(suffix);
    }

    public void addPermission(String world, String perm, boolean positive) {
        setPermission(world, new Permission(perm, positive));
    }

    public void addPermission(String world, String perm) {
        setPermission(world, new Permission(perm));
    }

    public void addPermission(String world, Permission permission) {
        getWorldPassOrCreate(world).setPermission(permission);
    }

    public Map<String, List<String>> getGroupMap() {
        Map<String, List<String>> groupMap = new HashMap<>();

        groups.forEach(g -> {
            if (!groupMap.containsKey("")) groupMap.put("", new ArrayList<>());
            groupMap.get("").add(g);
        });
        this.worldPass.forEach((key, value) -> value.getGroupList().forEach(g -> {
            if (!groupMap.containsKey(key)) groupMap.put(key, new ArrayList<>());
            groupMap.get(key).add(g);
        }));
        return groupMap;
    }

    public List<String> getGroupList(String world) {
        return hasWorldPass(world) ? getWorldPass(world).getGroupList() : Collections.EMPTY_LIST;
    }

    public List<String> getPermissionList(String world) {
        return hasWorldPass(world) ? getWorldPass(world).getPermissionList() : Collections.EMPTY_LIST;
    }

    public void addGroup(String world, String group) {
        addGroup(world, Groups.getGroup(group));
    }

    public void addGroup(String world, Group group) {
        if (group == null) return;
        getWorldPassOrCreate(world).addGroup(group);
    }

    public void setGroup(String world, String group) {
        setGroup(world, Groups.getGroup(group));
    }

    public void setGroup(String world, Group group) {
        if (group == null) return;
        getWorldPassOrCreate(world).setGroup(group);
    }


    public boolean isPermissionSet(String world, String permStr) {
        return hasWorldPass(world) && getWorldPass(world).isPermissionSet(permStr);
    }

    public void setPermissionsList(String world, List<String> permissions) {
        getWorldPassOrCreate(world).setPermissionsList(permissions);
    }

    public void setGroupList(String world, List<String> groupList) {
        getWorldPassOrCreate(world).setGroupList(groupList);
    }

    public String getName() {
        return name;
    }

    public Node getWorldPassOrCreate(String world) {
        if (world == null || world.isEmpty()) return this;
        Node node = getWorldPass(world);
        if (node == null) {
            node = new Node();
            this.worldPass.put(world, node);
        }
        return node;
    }

    public boolean hasWorldPass(String world) {
        return getWorldPass(world) != null;
    }

    private String getMirrorWorld(String currentWorld) {
        ConfigSection mirros = MultipassPlugin.getCfg().mirros;
        if (mirros != null && !mirros.isEmpty())
            for (Map.Entry<String, Object> e : mirros.entrySet())
                if (e.getValue() instanceof String) {
                    String[] worlds = ((String) e.getValue()).split(",\\s+");
                    for (String w : worlds)
                        if (w.equalsIgnoreCase(currentWorld)) return e.getKey();
                }
        return currentWorld;
    }

    public Node getWorldPass(String world) {
        if (world == null || world.isEmpty()) return null;
        String targetWorld = getMirrorWorld(world);
        return worldPass.getOrDefault(targetWorld, null);
    }

    public Map<String, Node> getWorldPass() {
        return worldPass;
    }


    public void setWorldPass(String world, Node node) {
        this.worldPass.put(world, node);
    }


    public void setPermission(String world, String perm, boolean positive) {
        setPermission(world, new Permission(perm, positive));
    }

    public void setPermission(String world, String perm) {
        setPermission(world, new Permission(perm));
    }

    public void setPermission(String world, Permission permission) {
        getWorldPassOrCreate(world).setPermission(permission);
    }

    public void removePermission(String world, String perm) {
        removePermission(world, new Permission(perm));
    }

    public void removePermission(String world, Permission permission) {
        if (hasWorldPass(world)) {
            getWorldPass(world).removePermission(permission);
            return;
        }
        removePermission(permission);
    }

    public boolean removeGroup(String world, String group) {
        if (hasWorldPass(world)) {
            return getWorldPass(world).removeGroup(group);
        }
        return removeGroup(group);
    }

    public void removeGroup(String world, Group group) {
        if (hasWorldPass(world)) {
            getWorldPass(world).removeGroup(group);
            return;
        }
        removeGroup(group);
    }

    public boolean inGroup(String world, String groupStr) {
        if (world == null) return false;
        Group group = Groups.getGroup(groupStr);
        if (group == null) return false;
        Node node = getWorldPass(world);
        if (node == null) return false;
        return node.inGroup(group.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) return false;
        BaseNode that = (BaseNode) o;
        return name != null && name.equalsIgnoreCase(that.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    public String baseToString(BaseNode baseNode) {
        StringBuilder sb = new StringBuilder();
        for (Group g : baseNode.getGroups()) {
            if (sb.length() == 0) sb.append(baseNode.getName()).append(" [").append(g.getName());
            else sb.append(", ").append(g.getName());
        }
        return sb.append("]").toString();
    }
}
