/*
 * Copyright (c) 2021, JFXcore. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  JFXcore designates this
 * particular file as subject to the "Classpath" exception as provided
 * in the LICENSE file that accompanied this code.
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
 */

package com.sun.javafx.binding;

import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.util.BidirectionalValueConverter;
import java.lang.ref.WeakReference;

public class ConvertingBidirectionalBinding<T, U> extends BidirectionalBinding {

    private static <S, T> void checkParameters(Object property1, Object property2, BidirectionalValueConverter<S, T> converter) {
        if (property1 == null || property2 == null || converter == null) {
            throw new NullPointerException("All parameters must be specified.");
        }
        if (property1 == property2) {
            throw new IllegalArgumentException("Cannot bind property to itself");
        }
    }

    public static <T, S> Object bind(Property<T> property1, Property<S> property2, BidirectionalValueConverter<S, T> converter) {
        checkParameters(property1, property2, converter);
        final var binding = new ConvertingBidirectionalBinding<>(property1, property2, converter);
        property1.addListener(binding);
        property2.addListener(binding);
        return binding;
    }

    private final WeakReference<Property<U>> propertyRef1;
    private final WeakReference<Property<T>> propertyRef2;
    private final BidirectionalValueConverter<T, U> converter;
    private U oldValue1;
    private T oldValue2;
    private boolean updating;

    public ConvertingBidirectionalBinding(Property<U> property1, Property<T> property2, BidirectionalValueConverter<T, U> converter) {
        super(property1, property2);
        this.converter = converter;
        oldValue2 = property2.getValue();
        oldValue1 = converter.convert(oldValue2);
        propertyRef1 = new WeakReference<>(property1);
        propertyRef2 = new WeakReference<>(property2);
        property1.setValue(oldValue1);
    }

    @Override
    protected Object getProperty1() {
        return propertyRef1.get();
    }

    @Override
    protected Object getProperty2() {
        return propertyRef2.get();
    }

    @Override
    public void invalidated(Observable observable) {
        if (!updating) {
            final Property<U> property1 = this.propertyRef1.get();
            final Property<T> property2 = this.propertyRef2.get();
            if ((property1 == null) || (property2 == null)) {
                if (property1 != null) {
                    property1.removeListener(this);
                }
                if (property2 != null) {
                    property2.removeListener(this);
                }
            } else {
                try {
                    updating = true;
                    if (property1 == observable) {
                        U newValue = property1.getValue();
                        property2.setValue(converter.convertBack(newValue));
                        oldValue1 = newValue;
                    } else {
                        T newValue = property2.getValue();
                        property1.setValue(converter.convert(newValue));
                        oldValue2 = newValue;
                    }
                } catch (RuntimeException e) {
                    try {
                        if (property1 == observable) {
                            property1.setValue(oldValue1);
                        } else {
                            property2.setValue(oldValue2);
                        }
                    } catch (Exception e2) {
                        e2.addSuppressed(e);
                        unbind(property1, property2);
                        throw new RuntimeException(
                                "Bidirectional binding failed together with an attempt"
                                        + " to restore the source property to the previous value."
                                        + " Removing the bidirectional binding from properties " +
                                        property1 + " and " + property2, e2);
                    }
                    throw new RuntimeException(
                            "Bidirectional binding failed, setting to the previous value", e);
                } finally {
                    updating = false;
                }
            }
        }
    }
}
