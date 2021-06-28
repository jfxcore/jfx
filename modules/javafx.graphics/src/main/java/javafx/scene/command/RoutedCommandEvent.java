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
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * The event that routed commands use to communicate invocations through the scene graph.
 *
 * @since JFXcore 17
 */
public final class RoutedCommandEvent extends Event {

    public static EventType<RoutedCommandEvent> ANY = new EventType<>(Event.ANY, "ANY");
    public static EventType<RoutedCommandEvent> EXECUTE = new EventType<>(ANY, "EXECUTED");
    public static EventType<RoutedCommandEvent> EXECUTABLE = new EventType<>(ANY, "EXECUTABLE");

    private final Command command;
    private final Object parameter;

    // The event might be copied as it makes its way through the scene graph.
    // Using an array here instead of a plain boolean allows us to retrieve the value,
    // even if it was set on a copy of the original event.
    private final boolean[] executable;

    public RoutedCommandEvent(
            EventTarget target, EventType<? extends RoutedCommandEvent> eventType, Command command, Object parameter) {
        super(target, target, eventType);
        this.command = command;
        this.parameter = parameter;
        this.executable = new boolean[1];
    }

    public Command getCommand() {
        return command;
    }

    public Object getParameter() {
        return parameter;
    }

    public boolean isExecutable() {
        return executable[0];
    }

    public void setExecutable(boolean executable) {
        this.executable[0] = executable;
    }

    @Override
    public RoutedCommandEvent copyFor(Object newSource, EventTarget newTarget) {
        return (RoutedCommandEvent)super.copyFor(newSource, newTarget);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<? extends RoutedCommandEvent> getEventType() {
        return (EventType<? extends RoutedCommandEvent>)super.getEventType();
    }

}
