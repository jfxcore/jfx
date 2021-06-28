package com.sun.javafx.scene.command;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GetWindowHelper {

    private static final List<Pair<Class<?>, Function<Object, Window>>> mappers = new ArrayList<>();

    static {
        mappers.add(new Pair<>(Node.class, node -> {
            Scene scene = ((Node)node).getScene();
            return scene != null ? scene.getWindow() : null;
        }));
    }

    public static Window getWindow(Object obj) {
        for (var pair : mappers) {
            if (pair.getKey().isInstance(obj)) {
                return pair.getValue().apply(obj);
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> void register(Class<T> klass, Function<T, Window> mapper) {
        mappers.add(new Pair<>(klass, (Function<Object, Window>)mapper));
    }

}
