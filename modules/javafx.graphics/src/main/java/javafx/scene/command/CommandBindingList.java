/*
 * Copyright (c) 2021 JFXcore. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 or later,
 * as published by the Free Software Foundation. This particular file is
 * designated as subject to the "Classpath" exception as provided in the
 * LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package javafx.scene.command;

import com.sun.javafx.collections.TrackableObservableList;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;

class CommandBindingList extends TrackableObservableList<CommandBinding<?>> {

    public static final String KEY = CommandBindingList.class.getName();

    private final Node node;

    public CommandBindingList(Node node) {
        this.node = node;
    }

    @Override
    protected void onChanged(ListChangeListener.Change<CommandBinding<?>> c) {
        while (c.next()) {
            if (c.wasRemoved()) {
                for (CommandBinding<?> binding : c.getRemoved()) {
                    binding.setNode(null);
                }
            }

            if (c.wasAdded()) {
                for (CommandBinding<?> binding : c.getAddedSubList()) {
                    binding.setNode(node);
                }
            }
        }
    }

}
