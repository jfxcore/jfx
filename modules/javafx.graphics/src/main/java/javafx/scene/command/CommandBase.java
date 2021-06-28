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

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;

/**
 * Provides implementations for {@link Command#executableProperty()} and {@link Command#executingProperty()}.
 *
 * @since JFXcore 17
 */
public abstract class CommandBase implements Command {

    private final ObservableValue<Boolean> condition;
    private final ReadOnlyBooleanWrapper executable = new ReadOnlyBooleanWrapper(this, "executable", true);
    private final ReadOnlyBooleanWrapper executing = new ReadOnlyBooleanWrapper(this, "executing", false);

    protected CommandBase() {
        this.condition = null;
        initialize();
    }

    protected CommandBase(ObservableValue<Boolean> condition) {
        this.condition = condition;
        initialize();
    }

    private void initialize() {
        if (condition != null) {
            executable.bind(Bindings.createBooleanBinding(() -> {
                if (condition instanceof ObservableBooleanValue) {
                    return ((ObservableBooleanValue)condition).get() && !executing.get();
                }

                Boolean value = condition.getValue();
                return !executing.get() && value != null && value;
            }, condition, executing));
        } else {
            executable.bind(executing.not());
        }
    }

    /**
     * Gets the condition that determines this command's executability.
     */
    protected ObservableValue<Boolean> getCondition() {
        return condition;
    }

    @Override
    public final ReadOnlyBooleanProperty executableProperty() {
        return executable.getReadOnlyProperty();
    }

    @Override
    public final ReadOnlyBooleanProperty executingProperty() {
        return executing.getReadOnlyProperty();
    }

    /**
     * Signals the start of command execution.
     * This method should be called by derived classes before the command is executed.
     *
     * @throws IllegalStateException if the command is not executable
     */
    protected void startExecution() {
        if (!isExecutable()) {
            throw new IllegalStateException("Command is not executable.");
        }

        executing.set(true);
    }

    /**
     * Signals the end of command execution.
     * This method should be called by derived classes after the command has been executed.
     */
    protected void endExecution() {
        executing.set(false);
    }

}
