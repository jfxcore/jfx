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

import javafx.beans.InvalidationListener;
import javafx.beans.NamedArg;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/**
 * A {@link CommandBinding} that invokes a user-specified method or command
 * if it receives a {@link RoutedCommand}.
 *
 * @since JFXcore 17
 */
public final class RoutedCommandBinding extends CommandBinding<RoutedCommandEvent> {

    @SuppressWarnings("FieldCanBeLocal")
    private final InvalidationListener invalidationListener;
    private final RoutedCommand command;
    private final Runnable executeMethod;
    private final Command executeCommand;
    private final ObservableValue<Boolean> executable;

    /**
     * Creates a new {@code RoutedCommandBinding} instance that executes a {@link Runnable}
     * when a {@link RoutedCommand} is received.
     *
     * @param command the {@link RoutedCommand} that this binding should handle
     * @param execute the code that should be executed when the {@link RoutedCommand} is received
     */
    public RoutedCommandBinding(
            @NamedArg("command") RoutedCommand command,
            @NamedArg("execute") Runnable execute) {
        this(command, execute, null);
    }

    /**
     * Creates a new {@code RoutedCommandBinding} instance that executes a {@link Runnable}
     * when a {@link RoutedCommand} is received.
     *
     * @param command the {@link RoutedCommand} that this binding should handle
     * @param execute the code that should be executed when the {@link RoutedCommand} is received
     * @param executable whether the {@code execute} code is currently executable
     */
    public RoutedCommandBinding(
            @NamedArg("command") RoutedCommand command,
            @NamedArg("execute") Runnable execute,
            @NamedArg("executable") ObservableValue<Boolean> executable) {
        super(RoutedCommandEvent.ANY, BindingMode.HANDLE_AND_CONSUME);

        if (command == null) {
            throw new IllegalArgumentException("command cannot be null");
        }

        this.command = command;
        this.executeMethod = execute;
        this.executeCommand = null;
        this.executable = executable;

        if (executable != null) {
            this.invalidationListener = observable -> RoutedCommandFocusManager.requeryExecutable(command);
            executable.addListener(new WeakInvalidationListener(invalidationListener));
        } else {
            this.invalidationListener = null;
        }
    }

    /**
     * Creates a new {@code RoutedCommandBinding} instance that executes a {@link Command}
     * when a {@link RoutedCommand} is received.
     *
     * @param command the {@link RoutedCommand} that this binding should handle
     * @param relayTo the command that should be executed when the {@link RoutedCommand} is received
     */
    public RoutedCommandBinding(
            @NamedArg("command") RoutedCommand command,
            @NamedArg("relayTo") Command relayTo) {
        super(RoutedCommandEvent.ANY, BindingMode.HANDLE_AND_CONSUME);

        if (command == null) {
            throw new IllegalArgumentException("command cannot be null");
        }

        if (relayTo == null) {
            throw new IllegalArgumentException("relayTo cannot be null");
        }

        this.command = command;
        this.executeMethod = null;
        this.executeCommand = command;
        this.executable = command.executableProperty();
        this.invalidationListener = observable -> RoutedCommandFocusManager.requeryExecutable(command);
        this.executable.addListener(new WeakInvalidationListener(invalidationListener));
    }

    @Override
    void handleEvent(RoutedCommandEvent event) {
        if (event.getCommand() == command) {
            if (event.getEventType() == RoutedCommandEvent.EXECUTE) {
                if (executeMethod != null) {
                    executeMethod.run();
                } else if (executeCommand instanceof RoutedCommand routedCommand) {
                    routedCommand.execute(getNode());
                } else if (executeCommand != null) {
                    executeCommand.execute();
                }
            } else if (event.getEventType() == RoutedCommandEvent.EXECUTABLE) {
                event.setExecutable(executable == null || executable.getValue() == Boolean.TRUE);
            }

            if (getMode().isConsume()) {
                event.consume();
            }
        }
    }

    @Override
    void addedToNode(Node node) {
        RoutedCommandFocusManager.register(node, command);
    }

    @Override
    void removedFromNode(Node node) {
        RoutedCommandFocusManager.unregister(node, command);
    }

}
