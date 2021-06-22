/*
 * Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
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

import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.util.BidirectionalValueConverter;
import javafx.util.ValueConverter;

/**
 * Generic interface that defines the methods common to all (writable)
 * properties independent of their type.
 *
 *
 * @param <T>
 *            the type of the wrapped value
 * @since JavaFX 2.0
 */
public interface Property<T> extends ReadOnlyProperty<T>, WritableValue<T> {

    /**
     * Create a unidirection binding for this {@code Property}.
     * <p>
     * Note that JavaFX has all the bind calls implemented through weak listeners. This means the bound property
     * can be garbage collected and stopped from being updated.
     *
     * @param observable
     *            The observable this {@code Property} should be bound to.
     * @throws NullPointerException
     *             if {@code observable} is {@code null}
     */
    void bind(ObservableValue<? extends T> observable);

    /**
     * Creates a unidirectional conversion binding for this {@code Property}. If the source property
     * changes, the target property will be set to the value that is obtained by converting the
     * changed value with the specified {@link ValueConverter}.
     * <p>
     * Note that JavaFX has all the bind calls implemented through weak listeners. This means the bound property
     * can be garbage collected and stopped from being updated.
     *
     * @param <S> The type of the source property.
     * @param observable
     *            The source property.
     * @param converter
     *            The converter that can convert objects of type {@code S} to objects of type {@code T}.
     * @throws NullPointerException
     *            if {@code observable} is {@code null}
     * @since JFXcore 17
     */
    default <S> void bind(ObservableValue<? extends S> observable, ValueConverter<S, T> converter) {
        throw new UnsupportedOperationException("Conversion binding is not supported");
    }

    /**
     * Remove the unidirectional binding for this {@code Property}.
     *
     * If the {@code Property} is not bound, calling this method has no effect.
     * @see #bind(javafx.beans.value.ObservableValue)
     */
    void unbind();

    /**
     * Can be used to check, if a {@code Property} is bound.
     * @see #bind(javafx.beans.value.ObservableValue)
     *
     * @return {@code true} if the {@code Property} is bound, {@code false}
     *         otherwise
     */
    boolean isBound();

    /**
     * Create a bidirectional binding between this {@code Property} and another
     * one.
     * Bidirectional bindings exists independently of unidirectional bindings. So it is possible to
     * add unidirectional binding to a property with bidirectional binding and vice-versa. However, this practice is
     * discouraged.
     * <p>
     * It is possible to have multiple bidirectional bindings of one Property.
     * <p>
     * JavaFX bidirectional binding implementation use weak listeners. This means bidirectional binding does not prevent
     * properties from being garbage collected.
     *
     * @param other
     *            the other {@code Property}
     * @throws NullPointerException
     *             if {@code other} is {@code null}
     * @throws IllegalArgumentException
     *             if {@code other} is {@code this}
     */
    void bindBidirectional(Property<T> other);

    /**
     * Creates a bidirectional conversion binding between two properties. If any of the properties
     * changes, the other property will be set to the value that is obtained by converting the
     * changed value with the specified {@link BidirectionalValueConverter}.
     * <p>
     * Bidirectional bindings exists independently of unidirectional bindings. So it is possible to
     * add unidirectional binding to a property with bidirectional binding and vice-versa. However, this practice is
     * discouraged.
     * <p>
     * It is possible to have multiple bidirectional bindings of one Property.
     * <p>
     * JavaFX bidirectional bindings use weak listeners. This means a bidirectional binding does not prevent
     * properties from being garbage collected.
     *
     * @param <S> the type of the source property
     * @param other
     *            the source {@code Property}
     * @param converter
     *            the converter that can convert between objects of type {@code S} and {@code T}
     * @throws NullPointerException
     *             if {@code other} is {@code null}
     * @throws IllegalArgumentException
     *             if {@code other} is {@code this}
     * @since JFXcore 17
     */
    default <S> void bindBidirectional(Property<S> other, BidirectionalValueConverter<S, T> converter) {
        throw new UnsupportedOperationException("Bidirectional conversion binding is not supported");
    }

    /**
     * Remove a bidirectional binding between this {@code Property} and another
     * one.
     *
     * If no bidirectional binding between the properties exists, calling this
     * method has no effect.
     *
     * It is possible to unbind by a call on the second property. This code will work:
     *
     * <blockquote><pre>
     *     property1.bindBirectional(property2);
     *     property2.unbindBidirectional(property1);
     * </pre></blockquote>
     *
     * @param other
     *            the other {@code Property}
     * @throws NullPointerException
     *             if {@code other} is {@code null}
     * @throws IllegalArgumentException
     *             if {@code other} is {@code this}
     */
    void unbindBidirectional(Property<T> other);

}
