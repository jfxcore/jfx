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

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.command.BindingMode;
import javafx.scene.command.CommandSource;
import javafx.scene.command.ParameterizedRoutedCommand;
import javafx.scene.command.ParameterizedRoutedCommandBinding;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ParameterizedRoutedCommandTest {

    Stage stage;
    Pane root;

    @Before
    @SuppressWarnings("unused")
    public void start() {
        this.stage = new Stage();
        root = new Pane();
        root.setId("root");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @After
    @SuppressWarnings("unused")
    public void stop() {
        stage.close();
    }

    /**
     * Pane
     * └─── Pane
     *      └─── Node
     */
    private Pane nestedPanes(Node node) {
        Pane pane1 = new Pane();
        pane1.setId("pane1");
        Pane pane2 = new Pane();
        pane2.setId("pane2");
        pane1.getChildren().add(pane2);
        pane2.getChildren().add(node);
        return pane1;
    }

    /**
     * When a command is installed on a control, it is not executable if there is no binding
     * for the command in the scene graph. Adding a binding makes the command executable.
     */
    @Test
    public void addingBindingToSceneGraphMakesCommandExecutable() {
        TestButton button = new TestButton();

        root.getChildren().setAll(nestedPanes(button));
        button.requestFocus();

        var command = new ParameterizedRoutedCommand<String>();
        CommandSource.setOnAction(button, command);
        assertFalse(command.isExecutable());

        CommandSource.getBindings(root).add(new ParameterizedRoutedCommandBinding<>(command, s -> {}));
        assertTrue(command.isExecutable());
    }

    /**
     * A button fires a routed command, which is handled by the root node in the capturing phase.
     * Since the command is not consumed, it is handled a second time in the bubbling phase.
     *
     * Pane (root): filter, handler
     * └─── Pane
     *      └─── Pane
     *           └─── Button: source, target
     */
    @Test
    public void parameterizedRoutedCommandIsFilteredAndHandledAtRoot() {
        List<String> list = new ArrayList<>();
        TestButton button = new TestButton();

        root.getChildren().setAll(nestedPanes(button));
        button.requestFocus();

        ParameterizedRoutedCommand<String> command = new ParameterizedRoutedCommand<>();
        CommandSource.setOnAction(button, command);
        CommandSource.setParameter(button, "foo");
        var filter = new ParameterizedRoutedCommandBinding<>(command, s -> list.add("filter-" + s));
        filter.setMode(BindingMode.FILTER);
        CommandSource.getBindings(root).add(filter);
        var handler = new ParameterizedRoutedCommandBinding<>(command, s -> list.add("handler-" + s));
        CommandSource.getBindings(root).add(handler);

        button.fire();

        assertEquals(2, list.size());
        assertEquals("filter-foo", list.get(0));
        assertEquals("handler-foo", list.get(1));
    }

    /**
     * A button fires a routed command, which is handled by the root node in the capturing phase.
     * Since the command is consumed, it does not travel back to the root in the bubbling phase.
     *
     * Pane (root): filter, handler
     * └─── Pane
     *      └─── Pane
     *           └─── Button: source, target
     */
    @Test
    public void parameterizedRoutedCommandIsNotHandledInBubblePhaseWhenConsumedInCapturePhase() {
        List<String> list = new ArrayList<>();
        TestButton button = new TestButton();

        root.getChildren().setAll(nestedPanes(button));
        button.requestFocus();

        ParameterizedRoutedCommand<String> command = new ParameterizedRoutedCommand<>();
        CommandSource.setOnAction(button, command);
        CommandSource.setParameter(button, "foo");
        var filter = new ParameterizedRoutedCommandBinding<>(command, s -> list.add("filter-" + s));
        filter.setMode(BindingMode.FILTER_AND_CONSUME);
        CommandSource.getBindings(root).add(filter);
        var handler = new ParameterizedRoutedCommandBinding<>(command, s -> list.add("handler-" + s));
        CommandSource.getBindings(root).add(handler);

        button.fire();

        assertEquals(1, list.size());
        assertEquals("filter-foo", list.get(0));
    }

}
