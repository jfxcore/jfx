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

package test.javafx.scene.command;

import javafx.scene.Scene;
import javafx.scene.command.RoutedCommand;
import javafx.scene.layout.StackPane;
import org.junit.Before;
import org.junit.Test;

import static javafx.scene.command.RoutedCommandFocusManagerShim.*;
import static org.junit.Assert.*;

public class RoutedCommandFocusManagerTest {

    @Before
    public void cleanup() {
        testFocusOwnerTrackers().clear();
    }

    @Test
    public void testFocusOwnerTrackerIsCreatedAfterTestButtonIsAddedToScene() {
        TestButton button = new TestButton();
        RoutedCommand command = new RoutedCommand();
        register(button, command);

        // We don't have a scene yet, so there is no focus owner tracker
        assertTrue(testFocusOwnerTrackers().isEmpty());

        // After we add a scene, we have a focus owner tracker
        Scene scene = new Scene(button);
        assertEquals(1, testFocusOwnerTrackers().size());
        assertEquals(1, testFocusOwnerTrackers().get(scene).getCommands().size());
        assertEquals(1, testFocusOwnerTrackers().get(scene).getCommands().get(command).value);
    }

    @Test
    public void testFocusOwnerTrackerIsReusedForMultipleCommandsInSameScene() {
        TestButton button = new TestButton();
        RoutedCommand command1 = new RoutedCommand();
        RoutedCommand command2 = new RoutedCommand();
        register(button, command1);
        register(button, command2);
        Scene scene = new Scene(button);

        assertEquals(1, testFocusOwnerTrackers().size());
        assertEquals(2, testFocusOwnerTrackers().get(scene).getCommands().size());
        assertEquals(1, testFocusOwnerTrackers().get(scene).getCommands().get(command1).value);
        assertEquals(1, testFocusOwnerTrackers().get(scene).getCommands().get(command2).value);
    }

    @Test
    public void testCommandRefCountMatchesRegistrations() {
        TestButton button1 = new TestButton();
        TestButton button2 = new TestButton();
        RoutedCommand command = new RoutedCommand();
        register(button1, command);
        register(button2, command);
        Scene scene = new Scene(new StackPane(button1, button2));

        assertEquals(1, testFocusOwnerTrackers().size());
        assertEquals(1, testFocusOwnerTrackers().get(scene).getCommands().size());
        assertEquals(2, testFocusOwnerTrackers().get(scene).getCommands().get(command).value);

        unregister(button1, command);
        assertEquals(1, testFocusOwnerTrackers().size());
        assertEquals(1, testFocusOwnerTrackers().get(scene).getCommands().size());
        assertEquals(1, testFocusOwnerTrackers().get(scene).getCommands().get(command).value);
    }

    @Test
    public void testFocusOwnerTrackerIsRemovedAfterAllCommandsAreRemoved() {
        TestButton button = new TestButton();
        RoutedCommand command1 = new RoutedCommand();
        RoutedCommand command2 = new RoutedCommand();
        register(button, command1);
        register(button, command2);
        Scene scene = new Scene(button);

        assertEquals(1, testFocusOwnerTrackers().size());
        assertEquals(2, testFocusOwnerTrackers().get(scene).getCommands().size());

        unregister(button, command2);
        assertEquals(1, testFocusOwnerTrackers().get(scene).getCommands().size());

        unregister(button, command1);
        assertEquals(0, testFocusOwnerTrackers().size());
    }

    @Test
    public void testCommandIsAddedAfterSceneIsCreated() {
        TestButton button = new TestButton();
        RoutedCommand command1 = new RoutedCommand();
        RoutedCommand command2 = new RoutedCommand();
        register(button, command1);
        Scene scene = new Scene(button);

        assertEquals(1, testFocusOwnerTrackers().size());
        assertEquals(1, testFocusOwnerTrackers().get(scene).getCommands().size());
        
        register(button, command2);
        assertEquals(1, testFocusOwnerTrackers().size());
        assertEquals(2, testFocusOwnerTrackers().get(scene).getCommands().size());
    }

}
