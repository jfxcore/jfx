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

import javafx.beans.property.ReadOnlyBooleanProperty;

/**
 * Represents a command that encapsulates an operation and its preconditions.
 *
 * @since JFXcore 17
 */
public interface Command {

    /**
     * Gets a property that indicates whether the command is currently executable.
     */
    ReadOnlyBooleanProperty executableProperty();

    /**
     * Gets a property that indicates whether the command is currently executing.
     */
    ReadOnlyBooleanProperty executingProperty();

    /**
     * Indicates whether the command is currently executable.
     */
    default boolean isExecutable() {
        return executableProperty().get();
    }

    /**
     * Indicates whether the command is currently executing.
     */
    default boolean isExecuting() {
        return executingProperty().get();
    }

    /**
     * Executes the command.
     *
     * @throws IllegalStateException if the command is not executable
     */
    void execute();

}
