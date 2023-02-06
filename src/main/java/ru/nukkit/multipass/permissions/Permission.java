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

public class Permission {

    private final String name;
    private final boolean positive;

    public Permission(String name, boolean positive) {
        this.name = name;
        this.positive = positive;
    }

    public Permission(String name) {
        positive = !(name.startsWith("-"));
        this.name = this.positive ? name : name.replaceFirst("-", "");
    }

    public String getName() {
        return this.name;
    }

    public boolean isPositive() {
        return this.positive;
    }

    public String toString() {
        return positive ? name : "-" + name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return name != null && name.equalsIgnoreCase(that.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
