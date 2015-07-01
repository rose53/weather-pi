/*
* EDisplay.java
*
* Copyright (c) 2014, rose. All rights reserved.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
* MA 02110-1301 USA
*/
package de.rose53.pi.weatherpi.display;

/**
 * @author rose
 *
 */
public enum EDisplay {
    ONE(0),
    TWO(1),
    THREE(3),
    FOUR(4);

    private final int position;

    private EDisplay(int position) {
        this.position = position;
    }

    public EDisplay getNext() {
        return this.ordinal() < EDisplay.values().length - 1? EDisplay.values()[this.ordinal() + 1]:null;
    }

    public EDisplay getPrevious() {
        return this.ordinal() > 0 ? EDisplay.values()[this.ordinal() - 1]:null;
    }

    public int getPosition() {
        return position;
    }
}
