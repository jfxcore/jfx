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
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

class RoutedCommandFocusManager {

    private static final FocusOwnerTrackers focusOwnerTrackers = new FocusOwnerTrackers();

    // only for testing
    static FocusOwnerTrackers testFocusOwnerTrackers() {
        return focusOwnerTrackers;
    }

    static void register(Node node, RoutedCommand command) {
        registerCommand(node, command);
    }

    static void register(Node node, ParameterizedRoutedCommand<?> command) {
        registerCommand(node, command);
    }

    static void unregister(Node node, RoutedCommand command) {
        unregisterCommand(node, command);
    }

    static void unregister(Node node, ParameterizedRoutedCommand<?> command) {
        unregisterCommand(node, command);
    }

    static void requeryExecutable(Command command) {
        outer: for (FocusOwnerTracker tracker : focusOwnerTrackers.values()) {
            for (Command cmd : tracker.commands.keySet()) {
                if (command == cmd) {
                    if (command instanceof RoutedCommand routedCommand) {
                        routedCommand.queryExecutable(tracker.scene.getFocusOwner());
                    } else if (command instanceof ParameterizedRoutedCommand<?> routedCommand) {
                        routedCommand.queryExecutable(tracker.scene.getFocusOwner());
                    }

                    continue outer;
                }
            }
        }
    }

    private static void registerCommand(Node node, Command command) {
        SceneTracker sceneTracker = (SceneTracker)node.getProperties().get(SceneTracker.class.getName());
        if (sceneTracker == null) {
            sceneTracker = new SceneTracker(node);
            node.getProperties().put(SceneTracker.class.getName(), sceneTracker);
        }

        sceneTracker.addCommand(command);

        requeryExecutable(command);
    }

    private static void unregisterCommand(Node node, Command command) {
        if (!node.hasProperties()) {
            return;
        }

        SceneTracker sceneTracker = (SceneTracker)node.getProperties().get(SceneTracker.class.getName());
        if (sceneTracker != null && sceneTracker.removeCommand(command)) {
            sceneTracker.close();
            node.getProperties().remove(SceneTracker.class.getName());
        }

        requeryExecutable(command);
    }

    public static class FocusOwnerTrackers extends WeakHashMap<Scene, FocusOwnerTracker> {
        public FocusOwnerTracker getOrCreate(Scene scene) {
            FocusOwnerTracker tracker = super.get(scene);
            if (tracker == null) {
                tracker = new FocusOwnerTracker(scene);
                put(scene, tracker);
            }

            return tracker;
        }
    }

    public static class MutableInteger {
        public int value;
    }

    public static class FocusOwnerTracker implements InvalidationListener, AutoCloseable {
        private final Map<Command, MutableInteger> commands = new HashMap<>();
        private final Scene scene;

        public FocusOwnerTracker(Scene scene) {
            this.scene = scene;
            scene.focusOwnerProperty().addListener(this);
            focusOwnerChanged(scene.getFocusOwner());
        }

        @Override
        public void close() {
            scene.focusOwnerProperty().removeListener(this);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void invalidated(Observable observable) {
            focusOwnerChanged(((ObservableValue<Node>)observable).getValue());
        }

        public Map<Command, MutableInteger> getCommands() {
            return commands;
        }

        void focusOwnerChanged(Node focusOwner) {
            for (Command command : commands.keySet()) {
                if (command instanceof RoutedCommand routedCommand) {
                    routedCommand.queryExecutable(focusOwner);
                } else if (command instanceof ParameterizedRoutedCommand<?> routedCommand) {
                    routedCommand.queryExecutable(focusOwner);
                }
            }
        }

        void addCommand(Command command) {
            MutableInteger refs = commands.get(command);
            if (refs != null) {
                refs.value++;
            } else {
                refs = new MutableInteger();
                refs.value = 1;
                commands.put(command, refs);
            }
        }

        void removeCommand(Command command) {
            MutableInteger refs = commands.get(command);
            if (refs != null && refs.value > 1) {
                refs.value--;
            } else {
                commands.remove(command);

                if (commands.isEmpty()) {
                    focusOwnerTrackers.remove(scene);
                }
            }
        }
    }

    private static class SceneTracker implements InvalidationListener, AutoCloseable {
        private final List<Command> commands = new ArrayList<>(2);
        private final Node node;
        private Scene scene;

        SceneTracker(Node node) {
            this.node = node;
            node.sceneProperty().addListener(this);
            sceneChanged(node.getScene());
        }

        @Override
        public void close() {
            node.sceneProperty().removeListener(this);
        }

        @Override
        public void invalidated(Observable observable) {
            sceneChanged(node.getScene());
        }

        void addCommand(Command command) {
            commands.add(command);

            if (this.scene != null) {
                FocusOwnerTracker tracker = focusOwnerTrackers.get(this.scene);
                if (tracker != null) {
                    tracker.addCommand(command);
                }
            }
        }

        boolean removeCommand(Command command) {
            if (this.scene != null) {
                FocusOwnerTracker tracker = focusOwnerTrackers.get(this.scene);
                if (tracker != null) {
                    tracker.removeCommand(command);
                }
            }

            commands.remove(command);
            return commands.isEmpty();
        }

        private void sceneChanged(Scene scene) {
            if (this.scene != null) {
                FocusOwnerTracker tracker = focusOwnerTrackers.get(this.scene);
                if (tracker != null) {
                    for (Command command : commands) {
                        tracker.removeCommand(command);
                    }
                }
            }

            if (scene != null) {
                FocusOwnerTracker tracker = focusOwnerTrackers.getOrCreate(scene);
                for (Command command : commands) {
                    tracker.addCommand(command);
                }
            }

            this.scene = scene;
        }
    }

}
