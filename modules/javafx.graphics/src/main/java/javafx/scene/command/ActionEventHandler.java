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

import com.sun.javafx.scene.command.GetWindowHelper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.PopupWindow;
import javafx.stage.Window;

class ActionEventHandler implements EventHandler<ActionEvent> {

    private static final String KEY = ActionEventHandler.class.getName();

    public static Command getCommand(CommandSource source) {
        ActionEventHandler handler = getActionEventHandler(source);
        if (handler != null) {
            return handler.command;
        }

        return null;
    }

    public static void setCommand(CommandSource source, Command command) {
        ensureActionEventHandler(source).setCommand(command);
    }

    public static Object getParameter(CommandSource source) {
        ActionEventHandler handler = getActionEventHandler(source);
        if (handler != null) {
            return handler.parameter;
        }

        return null;
    }

    public static void setParameter(CommandSource source, Object parameter) {
        ensureActionEventHandler(source).parameter = parameter;
    }

    private static ActionEventHandler getActionEventHandler(CommandSource source) {
        if (source == null) {
            throw new IllegalArgumentException("source cannot be null");
        }

        if (source instanceof Node node) {
            return node.hasProperties() ? (ActionEventHandler)node.getProperties().get(KEY) : null;
        }

        return (ActionEventHandler)source.getProperties().get(KEY);
    }

    private static ActionEventHandler ensureActionEventHandler(CommandSource source) {
        ActionEventHandler handler = getActionEventHandler(source);
        if (handler == null) {
            source.getProperties().put(KEY, handler = new ActionEventHandler(source));
        }

        return handler;
    }

    private final CommandSource source;
    private Command command;
    private Object parameter;

    private ActionEventHandler(CommandSource source) {
        this.source = source;
    }

    private void setCommand(Command command) {
        this.command = command;

        source.disableProperty().unbind();
        source.removeEventHandler(ActionEvent.ACTION, this);

        updateExecutable();

        if (command != null) {
            source.disableProperty().bind(command.executableProperty().not());
            source.addEventHandler(ActionEvent.ACTION, this);
        }
    }

    private void updateExecutable() {
        if (command instanceof RoutedCommand || command instanceof ParameterizedRoutedCommand) {
            routedCall(this::queryRoutedCommandExecutable);
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void handle(ActionEvent event) {
        if (command instanceof RoutedCommand || command instanceof ParameterizedRoutedCommand) {
            routedCall(this::executeRoutedCommand);
        } else if (command instanceof ParameterizedCommand) {
            ((ParameterizedCommand)command).execute(parameter);
        } else if (command != null) {
            command.execute();
        }
    }

    private void routedCall(EventTargetCall call) {
        Window window = GetWindowHelper.getWindow(source);
        while (window != null) {
            Scene scene = window.getScene();
            if (scene != null) {
                Node focusOwner = scene.getFocusOwner();
                if (focusOwner != null && call.apply(focusOwner)) {
                    break;
                }
            }

            window = getOwnerWindow(window);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private boolean executeRoutedCommand(EventTarget target) {
        if (command instanceof RoutedCommand) {
            return ((RoutedCommand)command).execute(target);
        }

        if (command instanceof ParameterizedRoutedCommand) {
            return ((ParameterizedRoutedCommand)command).execute(parameter, target);
        }

        return false;
    }

    @SuppressWarnings({"rawtypes"})
    private boolean queryRoutedCommandExecutable(EventTarget target) {
        if (command instanceof RoutedCommand) {
            return ((RoutedCommand)command).queryExecutable(target);
        }

        if (command instanceof ParameterizedRoutedCommand) {
            return ((ParameterizedRoutedCommand)command).queryExecutable(target);
        }

        return false;
    }

    private Window getOwnerWindow(Window window) {
        return window instanceof PopupWindow popupWindow ? popupWindow.getOwnerWindow() : null;
    }

    private interface EventTargetCall {
        boolean apply(EventTarget target);
    }

}
