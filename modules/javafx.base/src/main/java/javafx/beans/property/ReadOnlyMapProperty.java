/*
 * Copyright (c) 2011, 2017, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2021, JFXcore. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package javafx.beans.property;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.MapExpression;
import javafx.collections.ObservableMap;
import javafx.util.BidirectionalValueConverter;
import javafx.util.ValueConverter;

/**
 * Superclass for all readonly properties wrapping an {@link javafx.collections.ObservableMap}.
 *
 * @see javafx.collections.ObservableMap
 * @see javafx.beans.value.ObservableMapValue
 * @see javafx.beans.binding.MapExpression
 * @see ReadOnlyProperty
 *
 * @param <K> the type of the key elements of the map
 * @param <V> the type of the value elements of the map
 * @since JavaFX 2.1
 */
public abstract class ReadOnlyMapProperty<K, V> extends MapExpression<K, V> implements ReadOnlyProperty<ObservableMap<K, V>>  {

    /**
     * The constructor of {@code ReadOnlyMapProperty}.
     */
    public ReadOnlyMapProperty() {
    }

    /**
     * Creates a bidirectional content binding of the {@link javafx.collections.ObservableMap}, that is
     * wrapped in this {@code ReadOnlyMapProperty}, and another {@code ObservableMap}.
     * <p>
     * A bidirectional content binding ensures that the content of two {@code ObservableMaps} is the
     * same. If the content of one of the maps changes, the other one will be updated automatically.
     *
     * @param map the {@code ObservableMap} this property should be bound to
     * @throws NullPointerException if {@code map} is {@code null}
     * @throws IllegalArgumentException if {@code map} is the same map that this {@code ReadOnlyMapProperty} points to
     */
    public void bindContentBidirectional(ObservableMap<K, V> map) {
        Bindings.bindContentBidirectional(this, map);
    }

    /**
     * Creates a bidirectional content binding of the {@link javafx.collections.ObservableMap}, that is
     * wrapped in this {@code ReadOnlyMapProperty}, and another {@code ObservableMap}.
     * <p>
     * A converting bidirectional binding is a binding that works in both directions. If
     * two properties {@code a} and {@code b} are linked with a converting bidirectional
     * binding and the content of {@code a} changes, the content of {@code b} is synchronized
     * such that it associates any key in {@code a} with the projection that is obtained
     * by converting the corresponding value with the specified {@code BidirectionalValueConverter}.
     * And vice versa, if the content of {@code b} changes, the content of {@code a} is
     * synchronized likewise.
     *
     * @param <S> the type of the values in the source map
     * @param map the {@code ObservableMap} this property should be bound to
     * @param converter the converter that can convert objects of type {@code S} and {@code V}
     * @throws NullPointerException if {@code map} or {@code converter} is {@code null}
     * @throws IllegalArgumentException if {@code map} is the same map that this {@code ReadOnlyMapProperty} points to
     */
    public <S> void bindContentBidirectional(ObservableMap<K, S> map, BidirectionalValueConverter<S, V> converter) {
        Bindings.bindContentBidirectional(this, map, converter);
    }

    /**
     * Deletes a bidirectional content binding between the {@link javafx.collections.ObservableMap}, that is
     * wrapped in this {@code ReadOnlyMapProperty}, and another {@code Object}.
     *
     * @param object the {@code Object} to which the bidirectional binding should be removed
     * @throws NullPointerException if {@code object} is {@code null}
     * @throws IllegalArgumentException if {@code object} is the same map that this {@code ReadOnlyMapProperty} points to
     */
    public void unbindContentBidirectional(Object object) {
        Bindings.unbindContentBidirectional(this, object);
    }

    /**
     * Creates a content binding between the {@link javafx.collections.ObservableMap}, that is
     * wrapped in this {@code ReadOnlyMapProperty}, and another {@code ObservableMap}.
     * <p>
     * A content binding ensures that the content of the wrapped {@code ObservableMaps} is the
     * same as that of the other map. If the content of the other map changes, the wrapped map will be updated
     * automatically. Once the wrapped list is bound to another map, you must not change it directly.
     *
     * @param map the {@code ObservableMap} this property should be bound to
     * @throws NullPointerException if {@code map} is {@code null}
     * @throws IllegalArgumentException if {@code map} is the same map that this {@code ReadOnlyMapProperty} points to
     */
    public void bindContent(ObservableMap<K, V> map) {
        Bindings.bindContent(this, map);
    }

    /**
     * Creates a converting content binding between the {@link javafx.collections.ObservableMap}, that is
     * wrapped in this {@code ReadOnlyMapProperty}, and another {@code ObservableMap}.
     * <p>
     * A converting content binding ensures that the wrapped {@code ObservableMap} contains all keys of
     * the other map, which are associated with projections of their corresponding values that are
     * obtained by converting each value with the specified {@code ValueConverter}.
     * If the content of the other map changes, the wrapped map will be updated automatically.
     * Once the wrapped list is bound to another map, you must not change it directly.
     *
     * @param <S> the type of the values in the source map
     * @param map the {@code ObservableMap} this property should be bound to
     * @param converter the converter than can convert an object of type {@code S} to an object of type {@code V}
     * @throws NullPointerException if {@code map} or {@code converter} is {@code null}
     * @throws IllegalArgumentException if {@code map} is the same map that this {@code ReadOnlyMapProperty} points to
     */
    public <S> void bindContent(ObservableMap<K, S> map, ValueConverter<S, V> converter) {
        Bindings.bindContent(this, map, converter);
    }

    /**
     * Deletes a content binding between the {@link javafx.collections.ObservableMap}, that is
     * wrapped in this {@code ReadOnlyMapProperty}, and another {@code Object}.
     *
     * @param object the {@code Object} to which the binding should be removed
     * @throws NullPointerException if {@code object} is {@code null}
     * @throws IllegalArgumentException if {@code object} is the same map that this {@code ReadOnlyMapProperty} points to
     */
    public void unbindContent(Object object) {
        Bindings.unbindContent(this, object);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof Map))
            return false;
        Map<K,V> m = (Map<K,V>) obj;
        if (m.size() != size())
            return false;

        try {
            for (Entry<K,V> e : entrySet()) {
                K key = e.getKey();
                V value = e.getValue();
                if (value == null) {
                    if (!(m.get(key)==null && m.containsKey(key)))
                        return false;
                } else {
                    if (!value.equals(m.get(key)))
                        return false;
                }
            }
        } catch (ClassCastException unused) {
            return false;
        } catch (NullPointerException unused) {
            return false;
        }

        return true;
    }

    /**
     * Returns a hash code for this {@code ReadOnlyMapProperty} object.
     * @return a hash code for this {@code ReadOnlyMapProperty} object.
     */
    @Override
    public int hashCode() {
        int h = 0;
        for (Entry<K,V> e : entrySet()) {
            h += e.hashCode();
        }
        return h;
    }

    /**
     * Returns a string representation of this {@code ReadOnlyMapProperty} object.
     * @return a string representation of this {@code ReadOnlyMapProperty} object.
     */
    @Override
    public String toString() {
        final Object bean = getBean();
        final String name = getName();
        final StringBuilder result = new StringBuilder(
                "ReadOnlyMapProperty [");
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ");
        }
        if ((name != null) && !name.equals("")) {
            result.append("name: ").append(name).append(", ");
        }
        result.append("value: ").append(get()).append("]");
        return result.toString();
    }

}
