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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.event.Event;
import javafx.event.EventTarget;

/**
 * The parameterized version of {@link RoutedCommand}.
 *
 * @param <T> the parameter type
 *
 * @since JFXcore 17
 */
public class ParameterizedRoutedCommand<T> extends CommandBase implements ParameterizedCommand<T> {

    private final BooleanProperty executable;

    /**
     * Creates a new {@code ParameterizedRoutedCommand} instance.
     */
    public ParameterizedRoutedCommand() {
        super(new BooleanPropertyBase(true) {
            @Override public Object getBean() { return null; }
            @Override public String getName() { return null; }
        });

        executable = (BooleanProperty)getCondition();
    }

    /**
     * {@inheritDoc}
     * @deprecated always throws {@link UnsupportedOperationException}; use {@link #execute(T, EventTarget)} instead.
     */
    @Override
    @Deprecated
    public void execute(T parameter) {
        throw new UnsupportedOperationException(
            ParameterizedRoutedCommand.class.getName() + " cannot be executed without target");
    }

    /**
     * Executes the routed command on the specified target.
     *
     * @param parameter the command parameter
     * @param target the target of the command
     * @return {@code true} if the command was consumed by a {@link CommandBinding}; {@code false} otherwise
     */
    public boolean execute(T parameter, EventTarget target) {
        if (!executable.get()) {
            throw new IllegalStateException("Command is not executable.");
        }

        var event = new RoutedCommandEvent(target, RoutedCommandEvent.EXECUTE, this, parameter);
        Event.fireEvent(target, event);

        return event.isConsumed();
    }

    @SuppressWarnings("DuplicatedCode")
    boolean queryExecutable(EventTarget target) {
        if (target != null) {
            RoutedCommandEvent event = new RoutedCommandEvent(target, RoutedCommandEvent.EXECUTABLE, this, null);
            Event.fireEvent(target, event);
            executable.set(event.isExecutable());
            return event.isConsumed();
        }

        executable.set(false);
        return false;
    }

}
