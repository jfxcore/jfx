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
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;

/**
 * Defines an object that can invoke a command.
 * <p>
 * When a command is installed on a command source, the command will "take over" the {@code disable}
 * property of the command source and bind it to its {@link Command#executableProperty()}.
 * If the command is executable, the command source is enabled; if the command is not executable, the
 * command source is disabled.
 * <p>
 * The command is invoked when the command source handles an {@link ActionEvent}.
 * For button-like controls, this usually happens when the control is clicked.
 *
 * @since JFXcore 17
 */
public interface CommandSource extends EventTarget {

    /**
     * Gets a command from the command source that was installed using {@link #setOnAction(CommandSource, Command)}.
     *
     * @param source the command source
     * @return the command implementation
     */
    static Command getOnAction(CommandSource source) {
        return ActionEventHandler.getCommand(source);
    }

    /**
     * Installs a command into the command source that is invoked when a {@link javafx.event.ActionEvent} is received.
     *
     * @param source the command source
     * @param command the command implementation
     */
    static void setOnAction(CommandSource source, Command command) {
        ActionEventHandler.setCommand(source, command);
    }

    /**
     * Gets the parameter that is passed to the command implementation when the command is invoked.
     *
     * @param source the source object of the command
     * @return the parameter that is passed to the command implementation
     */
    static Object getParameter(CommandSource source) {
        return ActionEventHandler.getParameter(source);
    }

    /**
     * Sets the parameter that is passed to the command implementation when the command is invoked.
     *
     * @param source the source object of the command
     * @param parameter the parameter that is passed to the command implementation
     */
    static void setParameter(CommandSource source, Object parameter) {
        ActionEventHandler.setParameter(source, parameter);
    }

    /**
     * Gets the bindings that are registered on the specified {@link Node}.
     */
    static ObservableList<CommandBinding<?>> getBindings(Node source) {
        CommandBindingList eventBindings = source.hasProperties() ?
                (CommandBindingList)source.getProperties().get(CommandBindingList.KEY) : null;

        if (eventBindings == null) {
            source.getProperties().put(CommandBindingList.KEY, eventBindings = new CommandBindingList(source));
        }

        return eventBindings;
    }

    ObservableMap<Object, Object> getProperties();

    BooleanProperty disableProperty();

    <E extends Event> void addEventHandler(EventType<E> eventType, EventHandler<? super E> eventHandler);

    <E extends Event> void removeEventHandler(EventType<E> eventType, EventHandler<? super E> eventHandler);

}
