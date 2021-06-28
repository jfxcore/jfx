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
 * A routed command is a specialized command that, in contrast to all other command implementations,
 * does not encapsulate its operation directly. Instead, when a routed command is invoked, it fires
 * a {@link RoutedCommandEvent} that travels to its target in the scene graph.
 * <p>
 * If it encounters a node along the way that has a {@link RoutedCommandBinding} for this routed command,
 * the binding is invoked. Depending on the {@link CommandBinding#modeProperty() binding mode}, the event is either
 * consumed or it continues to travel along the scene graph.
 *
 * @since JFXcore 17
 */
public class RoutedCommand extends CommandBase {

    private final BooleanProperty executable;

    /**
     * Creates a new {@code RoutedCommand} instance.
     */
    public RoutedCommand() {
        super(new BooleanPropertyBase(false) {
            @Override public Object getBean() { return null; }
            @Override public String getName() { return null; }
        });

        executable = (BooleanProperty)getCondition();
    }

    /**
     * {@inheritDoc}
     * @deprecated always throws {@link UnsupportedOperationException}; use {@link #execute(EventTarget)} instead.
     */
    @Override
    @Deprecated
    public void execute() {
        throw new UnsupportedOperationException(
            RoutedCommand.class.getName() + " cannot be executed without target");
    }

    /**
     * Executes the routed command on the specified target.
     *
     * @param target the target of the command
     * @return {@code true} if the command was consumed by a {@link CommandBinding}; {@code false} otherwise
     */
    public boolean execute(EventTarget target) {
        if (!executable.get()) {
            throw new IllegalStateException("Command is not executable.");
        }

        var event = new RoutedCommandEvent(target, RoutedCommandEvent.EXECUTE, this, null);
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
