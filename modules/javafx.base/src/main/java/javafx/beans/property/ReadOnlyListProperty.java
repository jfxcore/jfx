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

import java.util.List;
import java.util.ListIterator;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ListExpression;
import javafx.collections.ObservableList;
import javafx.util.BidirectionalValueConverter;
import javafx.util.ValueConverter;

/**
 * Superclass for all readonly properties wrapping an {@link javafx.collections.ObservableList}.
 *
 * @see javafx.collections.ObservableList
 * @see javafx.beans.value.ObservableListValue
 * @see javafx.beans.binding.ListExpression
 * @see ReadOnlyProperty
 *
 * @param <E> the type of the {@code List} elements
 * @since JavaFX 2.1
 */
public abstract class ReadOnlyListProperty<E> extends ListExpression<E>
        implements ReadOnlyProperty<ObservableList<E>>  {

    /**
     * The constructor of {@code ReadOnlyListProperty}.
     */
    public ReadOnlyListProperty() {
    }

    /**
     * Creates a bidirectional content binding of the {@link javafx.collections.ObservableList}, that is
     * wrapped in this {@code ReadOnlyListProperty}, and another {@code ObservableList}.
     * <p>
     * A bidirectional content binding ensures that the content of two {@code ObservableLists} is the
     * same. If the content of one of the lists changes, the other one will be updated automatically.
     *
     * @param list the {@code ObservableList} this property should be bound to
     * @throws NullPointerException if {@code list} is {@code null}
     * @throws IllegalArgumentException if {@code list} is the same list that this {@code ReadOnlyListProperty} points to
     */
    public void bindContentBidirectional(ObservableList<E> list) {
        Bindings.bindContentBidirectional(this, list);
    }

    /**
     * Creates a bidirectional content binding of the {@link javafx.collections.ObservableList}, that is
     * wrapped in this {@code ReadOnlyListProperty}, and another {@code ObservableList}.
     * <p>
     * A converting bidirectional binding is a binding that works in both directions. If
     * two properties {@code a} and {@code b} are linked with a converting bidirectional
     * binding and the content of {@code a} changes, the content of {@code b} is synchronized
     * such that it contains the projections of all elements of {@code a} that were obtained
     * by converting each element with the specified {@code BidirectionalValueConverter}.
     * And vice versa, if the content of {@code b} changes, the content of {@code a} is
     * synchronized likewise.
     *
     * @param <S> the type of the elements contained in the source list
     * @param list the {@code ObservableList} this property should be bound to
     * @param converter the converter that can convert objects of type {@code S} and {@code E}
     * @throws NullPointerException if {@code list} or {@code converter} is {@code null}
     * @throws IllegalArgumentException if {@code list} is the same list that this {@code ReadOnlyListProperty} points to
     */
    public <S> void bindContentBidirectional(ObservableList<S> list, BidirectionalValueConverter<S, E> converter) {
        Bindings.bindContentBidirectional(this, list, converter);
    }

    /**
     * Deletes a bidirectional content binding between the {@link javafx.collections.ObservableList}, that is
     * wrapped in this {@code ReadOnlyListProperty}, and another {@code Object}.
     *
     * @param object the {@code Object} to which the bidirectional binding should be removed
     * @throws NullPointerException if {@code object} is {@code null}
     * @throws IllegalArgumentException if {@code object} is the same list that this {@code ReadOnlyListProperty} points to
     */
    public void unbindContentBidirectional(Object object) {
        Bindings.unbindContentBidirectional(this, object);
    }

    /**
     * Creates a content binding between the {@link javafx.collections.ObservableList}, that is
     * wrapped in this {@code ReadOnlyListProperty}, and another {@code ObservableList}.
     * <p>
     * A content binding ensures that the content of the wrapped {@code ObservableLists} is the
     * same as that of the other list. If the content of the other list changes, the wrapped list will be updated
     * automatically. Once the wrapped list is bound to another list, you must not change it directly.
     *
     * @param list the {@code ObservableList} this property should be bound to
     * @throws NullPointerException if {@code list} is {@code null}
     * @throws IllegalArgumentException if {@code list} is the same list that this {@code ReadOnlyListProperty} points to
     */
    public void bindContent(ObservableList<E> list) {
        Bindings.bindContent(this, list);
    }

    /**
     * Creates a converting content binding between the {@link javafx.collections.ObservableList}, that is
     * wrapped in this {@code ReadOnlyListProperty}, and another {@code ObservableList}.
     * <p>
     * A converting content binding ensures that the wrapped {@code ObservableList} contains projections of
     * all elements of the other list by converting each element with the specified {@code ValueConverter}.
     * If the content of the other list changes, the wrapped list will be updated automatically.
     * Once the wrapped list is bound to another list, you must not change it directly.
     *
     * @param <S> the type of the elements contained in the source list
     * @param list the {@code ObservableList} this property should be bound to
     * @param converter the converter that can convert an object of type {@code S} to an object of type {@code E}
     * @throws NullPointerException if {@code list} or {@code converter} is {@code null}
     * @throws IllegalArgumentException if {@code list} is the same list that this {@code ReadOnlyListProperty} points to
     */
    public <S> void bindContent(ObservableList<S> list, ValueConverter<S, E> converter) {
        Bindings.bindContent(this, list, converter);
    }

    /**
     * Deletes a content binding between the {@link javafx.collections.ObservableList}, that is
     * wrapped in this {@code ReadOnlyListProperty}, and another {@code Object}.
     *
     * @param object the {@code Object} to which the binding should be removed
     * @throws NullPointerException if {@code object} is {@code null}
     * @throws IllegalArgumentException if {@code object} is the same list that this {@code ReadOnlyListProperty} points to
     */
    public void unbindContent(Object object) {
        Bindings.unbindContent(this, object);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof List)) {
            return false;
        }
        final List list = (List)obj;

        if (size() != list.size()) {
            return false;
        }

        ListIterator<E> e1 = listIterator();
        ListIterator e2 = list.listIterator();
        while (e1.hasNext() && e2.hasNext()) {
            E o1 = e1.next();
            Object o2 = e2.next();
            if (!(o1==null ? o2==null : o1.equals(o2)))
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        for (E e : this)
            hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
        return hashCode;
    }

    /**
     * Returns a string representation of this {@code ReadOnlyListProperty} object.
     * @return a string representation of this {@code ReadOnlyListProperty} object.
     */
    @Override
    public String toString() {
        final Object bean = getBean();
        final String name = getName();
        final StringBuilder result = new StringBuilder(
                "ReadOnlyListProperty [");
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
