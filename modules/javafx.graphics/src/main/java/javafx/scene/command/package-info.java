/**
 * Provides an implementation of the command pattern for JavaFX.
 *
 * <h2>Introduction</h2>
 * A command is an abstraction that decouples the implementation of an operation from its invocation.
 * The object that invokes an operation is called the <b>command source</b>, while the object on which
 * the operation is defined and which contains the implementation of the operation is called the
 * <b>command target</b>.
 * <p>
 * Commands are created by implementing the {@link javafx.scene.command.Command} or
 * {@link javafx.scene.command.ParameterizedCommand} interface. The parameterized version takes in
 * a single argument, which is supplied by the command source when the command is invoked.
 * {@link javafx.scene.command.CommandBase} can be used as a base class where all methods except
 * {@link javafx.scene.command.Command#execute()} are already implemented.
 * <p>
 * A command can be installed on any object that implements {@link javafx.scene.command.CommandSource},
 * which includes sub-classes of {@link javafx.scene.control.ButtonBase} and {@link javafx.scene.control.MenuItem}.
 * For ease of use, the {@link javafx.scene.command.RelayCommand} and {@link javafx.scene.command.ParameterizedRelayCommand}
 * classes are provided, which are command implementations where the command logic is implemented by a
 * user-specified function:
 * <pre><code>
 * var command = new RelayCommand(() -> {
 *     System.out.println("Command was executed.");
 * });
 *
 * var button = new Button();
 * Command.setOnAction(button, command);
 * </code></pre>
 * An installed command "takes over" the {@code disabled} property of the command source, which means that
 * the command source control is only enabled if the command is actually executable. Whether a command is
 * executable depends on the implementation, but usually requires that the command is not currently
 * executing (which prevents reentrant executions) and that user-specified preconditions of the command
 * are satisfied.
 *
 * <h2>Routed commands</h2>
 * Routed commands are different from regular commands in that they don't contain or execute the command logic
 * themselves. Instead, when a routed command is invoked by a command source, the routed command will use
 * the JavaFX event system to travel to the currently focused node through the scene graph. On any node that
 * it encounters, the routed command will look for a {@link javafx.scene.command.RoutedCommandBinding} or
 * {@link javafx.scene.command.ParameterizedRoutedCommandBinding} that can handle the routed command.
 * <p>
 * If a matching command binding is found, the command binding will then execute its associated operation
 * on the current node. If no command binding is found, the command will not be executable (and the command
 * source will be disabled).
 * <p>
 * Routed commands can be used in situations where the command target can change depending on the currently
 * focused node. For example, {@link javafx.scene.control.TextInputControl} contains bindings for a set of
 * commands defined by {@link javafx.scene.control.TextInputCommands}.
 * The following code creates a {@link javafx.scene.control.MenuItem} that is enabled when a
 * {@link javafx.scene.control.TextField} is focused, and which will paste the content of the current
 * clipboard into the text field.
 * <pre><code>
 * // Create the menu
 * var menuItem = new MenuItem("Paste");
 * var menuBar = new MenuBar(new Menu("Edit", null, menuItem));
 * getChildren().add(menuBar);
 *
 * // Install the 'paste' command
 * Command.setOnAction(menuItem, TextInputCommands.Paste);
 *
 * // Create a text field that can receive the command
 * getChildren().add(new TextField());
 * </code></pre>
 * Note that this code works because {@link javafx.scene.control.MenuBar} is not focus-traversable,
 * so clicking on the menu will not change the current focus owner. If the 'paste' command were installed
 * on a focus-traversable button instead, clicking on the button would transfer focus to the button, and
 * therefore send the routed command to the button instead of the selected text field.
 *
 * <h2>Event bindings</h2>
 * In addition to the routed command bindings described earlier, {@link javafx.scene.command.KeyEventBinding}
 * and {@link javafx.scene.command.MouseEventBinding} invoke a command if a specified key event or mouse event
 * was received.
 *
 * @since JFXcore 17
 */
package javafx.scene.command;
