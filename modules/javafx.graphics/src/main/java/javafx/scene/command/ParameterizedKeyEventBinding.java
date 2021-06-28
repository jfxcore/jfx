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

import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Executes a command when a {@link KeyEvent} with a specified {@link KeyCode} was received.
 *
 * @since JFXcore 17
 */
public class ParameterizedKeyEventBinding<T> extends ParameterizedEventBindingBase<KeyEvent, T> {

    private final ObjectProperty<KeyCode> keyCode = new SimpleObjectProperty<>(this, "keyCode");

    public ParameterizedKeyEventBinding(@NamedArg("command") ParameterizedCommand<T> command) {
        super(KeyEvent.KEY_RELEASED, command, BindingMode.HANDLE_AND_CONSUME);
    }

    public ParameterizedKeyEventBinding(
            @NamedArg("eventType") EventType<KeyEvent> eventType,
            @NamedArg("command") ParameterizedCommand<T> command) {
        super(eventType, command, BindingMode.HANDLE_AND_CONSUME);
    }

    public ObjectProperty<KeyCode> keyCodeProperty() {
        return keyCode;
    }

    public KeyCode getKeyCode() {
        return keyCode.get();
    }

    public void setKeyCode(KeyCode keyCode) {
        this.keyCode.set(keyCode);
    }

    @Override
    void handleEvent(KeyEvent event) {
        if (event.getCode() == keyCode.get()) {
            fireCommand(event);
        }
    }

}
