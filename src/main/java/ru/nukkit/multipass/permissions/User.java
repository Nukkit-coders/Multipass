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
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.player.Player;
import ru.nukkit.multipass.MultipassPlugin;
import ru.nukkit.multipass.util.Message;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class User extends BaseNode {

    PermissionAttachment attachment;

    public User(String playerName) {
        super(playerName);
        this.priority = MultipassPlugin.getCfg().userPriority;
    }

    public User(String playerName, String prefix, String suffix, int priority) {
        super(playerName);
        this.prefix = prefix;
        this.suffix = suffix;
        this.priority = priority;
    }


    public User(String playerName, Node node) {
        super(playerName, node);
    }

    public User(String playerName, BaseNode node) {
        super(playerName, node);
    }

    public void recalculatePermissions() {
        emptyToDefault();
        Message.debugMessage("Recalculate permissions:", this.getName());
        if (getAttachment() == null) return;

        Player player = Server.getInstance().getPlayerExact(this.name);
        String world = useWorlds() ? player.getLevel().getName() : null;

        Map<String, Boolean> perms = new LinkedHashMap<>();

        Set<BaseNode> nodes = this.getAllNodes(false);

        nodes.forEach(node -> {

            if (MultipassPlugin.getCfg().groupPermission && !node.equals(this))
                perms.put("permission.group." + node.getName(), true);

            node.getPermissions().forEach(perm -> perms.put(perm.getName(), perm.isPositive()));
            if (world != null) node.getPermissions(world).forEach(perm -> perms.put(perm.getName(), perm.isPositive()));
        });

        attachment.clearPermissions();

        player.recalculatePermissions();
        if (!perms.isEmpty()) attachment.setPermissions(perms);
        player.recalculatePermissions();
        if (Message.isDebug()) perms.entrySet().forEach(e -> {
            Message.debugMessage(e.getValue() ? "+" : "-", e.getKey());
        });

        Server.getInstance().getScheduler().scheduleDelayedTask(MultipassPlugin.getPlugin(),  () -> player.sendCommandData(), 10);
    }

    public PermissionAttachment getAttachment() {
        Player player = Server.getInstance().getPlayerExact(this.name);
        attachment = player == null ? null : (attachment == null ? player.addAttachment(MultipassPlugin.getPlugin()) : attachment);
        return attachment;
    }

    private boolean useWorlds() {
        return MultipassPlugin.getCfg().enableWorldSupport;
    }

    public boolean isEmpty() {
        return this.permissions.isEmpty() &&
                (groups.isEmpty() || (groups.size() == 1 && (Groups.isDefault(groups.stream().toArray(String[]::new)[0])))) &&
                this.getWorldPass().isEmpty() &&
                this.prefix.isEmpty() && this.suffix.isEmpty() && (this.priority == MultipassPlugin.getCfg().userPriority);
    }

    private void emptyToDefault() {
        String groupDefault = MultipassPlugin.getCfg().defaultGroup;
        if (groupDefault == null || groupDefault.isEmpty()) return;
        if (!this.isEmpty()) return;
        this.setGroup(groupDefault);
    }
}
