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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;

/**
 * Base class for command bindings that listen for events in the scene graph.
 *
 * @param <E> the event type
 *
 * @see KeyEventBinding
 * @see MouseEventBinding
 * @see RoutedCommandBinding
 * @see ParameterizedRoutedCommandBinding
 *
 * @since JFXcore 17
 */
public abstract class CommandBinding<E extends Event> {

    /**
     * Specifies if events are handled in the <i>capture</i> or <i>bubble</i> phase,
     * and whether they are consumed.
     */
    private final ObjectProperty<BindingMode> mode = new SimpleObjectProperty<>(this, "mode") {
        BindingMode oldValue;

        @Override
        protected void invalidated() {
            if (node != null) {
                BindingMode newValue = get();

                if (oldValue.isFilter() && !newValue.isFilter()) {
                    node.removeEventFilter(eventType, handler);
                    node.addEventHandler(eventType, handler);
                } else if (!oldValue.isFilter() && newValue.isFilter()) {
                    node.removeEventHandler(eventType, handler);
                    node.addEventFilter(eventType, handler);
                }

                oldValue = newValue;
            }
        }
    };

    private final EventType<E> eventType;
    private final EventHandler<E> handler = this::handleEvent;
    private Node node;

    protected CommandBinding(EventType<E> eventType, BindingMode mode) {
        if (eventType == null) {
            throw new IllegalArgumentException("eventType cannot be null");
        }

        if (mode == null) {
            throw new IllegalArgumentException("mode cannot be null");
        }

        this.eventType = eventType;
        this.mode.set(mode);
    }

    /**
     * Gets the event type that is handled by this binding.
     *
     * @return the event type
     */
    public final EventType<E> getEventType() {
        return eventType;
    }

    public ObjectProperty<BindingMode> modeProperty() {
        return mode;
    }

    public BindingMode getMode() {
        return mode.get();
    }

    public void setMode(BindingMode mode) {
        this.mode.set(mode);
    }

    void addedToNode(Node node) {}

    void removedFromNode(Node node) {}

    abstract void handleEvent(E event);

    Node getNode() {
        return node;
    }

    void setNode(Node node) {
        if (this.node == node) {
            return;
        }

        if (this.node != null) {
            if (node != null) {
                throw new IllegalArgumentException("Binding is already set on a node.");
            }

            if (getMode().isFilter()) {
                this.node.removeEventFilter(eventType, handler);
            } else {
                this.node.removeEventHandler(eventType, handler);
            }

            removedFromNode(this.node);
        }

        if (node != null) {
            if (getMode().isFilter()) {
                node.addEventFilter(eventType, handler);
            } else {
                node.addEventHandler(eventType, handler);
            }

            addedToNode(node);
        }

        this.node = node;
    }

}
