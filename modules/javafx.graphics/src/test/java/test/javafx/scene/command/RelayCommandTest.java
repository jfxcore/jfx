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

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.command.Command;
import javafx.scene.command.CommandSource;
import javafx.scene.command.RelayCommand;
import org.junit.Test;

import static org.junit.Assert.*;

public class RelayCommandTest {

    @Test
    public void conditionParameterControlsExecutability() {
        var condition = new SimpleBooleanProperty();
        var command = new RelayCommand(() -> {}, condition);
        assertFalse(command.isExecutable());
        condition.set(true);
        assertTrue(command.isExecutable());

        condition.set(false);
        command = new RelayCommand(() -> {}, condition, ex -> {});
        assertFalse(command.isExecutable());
        condition.set(true);
        assertTrue(command.isExecutable());
    }

    @Test
    public void commandIsInvokedWhenButtonFires() {
        TestButton button = new TestButton();
        boolean[] flag = new boolean[1];
        var command = new RelayCommand(() -> flag[0] = true);
        CommandSource.setOnAction(button, command);
        button.fire();

        assertTrue(flag[0]);
    }

    @Test
    public void buttonIsDisabledWhenCommandIsRunning() {
        TestButton button = new TestButton();
        var command = new RelayCommand(() -> assertTrue(button.isDisabled()));
        CommandSource.setOnAction(button, command);

        assertFalse(button.isDisabled());
        button.fire();
        assertFalse(button.isDisabled());
    }

    @Test
    public void commandExecutingIsTrueWhenCommandIsRunning() {
        TestButton button = new TestButton();
        Command[] commandRef = new Command[1];
        var command = new RelayCommand(() -> assertTrue(commandRef[0].isExecuting()));
        commandRef[0] = command;
        CommandSource.setOnAction(button, command);

        assertFalse(command.isExecuting());
        button.fire();
        assertFalse(command.isExecuting());
    }

    @Test
    public void commandThrowsExceptionIfNoExceptionHandlerSet() {
        TestButton button = new TestButton();
        var command = new RelayCommand(() -> { throw new RuntimeException("foo"); });
        CommandSource.setOnAction(button, command);

        try {
            button.fire();
            fail();
        } catch (RuntimeException ex) {
            assertEquals("foo", ex.getMessage());
        }
    }

    @Test
    public void exceptionIsDelegatedToExceptionHandler() {
        TestButton button = new TestButton();
        var command = new RelayCommand(
            () -> { throw new RuntimeException("foo"); },
            ex -> assertEquals("foo", ex.getMessage()));
        CommandSource.setOnAction(button, command);
        button.fire();
    }

}
