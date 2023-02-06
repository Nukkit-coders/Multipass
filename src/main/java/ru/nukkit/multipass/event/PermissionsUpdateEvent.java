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

package ru.nukkit.multipass.event;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;

public class PermissionsUpdateEvent extends Event {

    private final String user;

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public PermissionsUpdateEvent() {
        this.user = null;
    }

    public PermissionsUpdateEvent(String userName) {
        this.user = userName;
    }

    /**
     * Get user, which permissions was changed.
     * Returns null permissions updated for all users.
     *
     * @return
     */
    public String getUser() {
        return this.user;
    }

    /**
     * Check is Permission update related to single player or it was a mass update
     * (for example changing groups)
     *
     * @return
     */
    public boolean isMassUpdate() {
        return user == null;
    }
}