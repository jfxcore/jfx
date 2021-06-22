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

package javafx.util;

/**
 * A converter that converts objects of types {@code S} and {@code T}.
 *
 * @param <S> the type of the first object
 * @param <T> the type of the second object
 * @since JFXcore 17
 */
public interface BidirectionalValueConverter<S, T> extends ValueConverter<S, T> {

    /**
     * Converts an object of type {@code T} to an object of type {@code S}.
     * <p>This operation is the inverse of {@link #convert(Object)}.
     *
     * @param value the value to be converted
     * @return the converted value
     */
    S convertBack(T value);

}
