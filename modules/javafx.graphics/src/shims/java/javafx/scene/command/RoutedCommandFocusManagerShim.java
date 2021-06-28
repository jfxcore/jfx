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

import javafx.scene.Node;

public class RoutedCommandFocusManagerShim {

    public static RoutedCommandFocusManager.FocusOwnerTrackers testFocusOwnerTrackers() {
        return RoutedCommandFocusManager.testFocusOwnerTrackers();
    }

    public static void register(Node node, RoutedCommand command) {
        RoutedCommandFocusManager.register(node, command);
    }

    public static void register(Node node, ParameterizedRoutedCommand<?> command) {
        RoutedCommandFocusManager.register(node, command);
    }

    public static void unregister(Node node, RoutedCommand command) {
        RoutedCommandFocusManager.unregister(node, command);
    }

    public static void unregister(Node node, ParameterizedRoutedCommand<?> command) {
        RoutedCommandFocusManager.unregister(node, command);
    }

}
