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

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;

/**
 * Base class for bindings that listen for input events on a {@link Node}
 * and execute a command if the specified event was received.
 *
 * @param <E> the event type
 *
 * @see KeyEventBinding
 * @see MouseEventBinding
 *
 * @since JFXcore 17
 */
public abstract class EventBindingBase<E extends Event> extends CommandBinding<E> {

    private final Command command;

    protected EventBindingBase(EventType<E> eventType, Command command, BindingMode mode) {
        super(eventType, mode);
        this.command = command;
    }

    /**
     * Gets the command that is invoked by this binding.
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Fires the command. If the command is not executable, this method is a no-op.
     *
     * @param event the event that caused the command to be invoked
     */
    protected void fireCommand(E event) {
        if (command != null && command.isExecutable()) {
            if (command instanceof RoutedCommand) {
                ((RoutedCommand)command).execute(getNode());
            } else {
                command.execute();
            }

            if (getMode().isConsume()) {
                event.consume();
            }
        }
    }

}
