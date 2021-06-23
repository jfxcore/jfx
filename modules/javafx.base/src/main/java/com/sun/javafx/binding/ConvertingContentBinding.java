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

import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.util.ValueConverter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConvertingContentBinding {

    private static <S, T> void checkParameters(Object property1, Object property2, ValueConverter<S, T> converter) {
        if (property1 == null || property2 == null || converter == null) {
            throw new NullPointerException("All parameters must be specified.");
        }
        if (property1 == property2) {
            throw new IllegalArgumentException("Cannot bind object to itself");
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <S, E> Object bind(List<E> list1, ObservableList<? extends S> list2, ValueConverter<S, E> converter) {
        checkParameters(list1, list2, converter);
        final ConvertingListContentBinding<S, E> contentBinding = new ConvertingListContentBinding<>(list1, converter);
        List<E> converted = new ArrayList<>(list2.size());
        for (S item : list2) {
            converted.add(converter.convert(item));
        }

        if (list1 instanceof ObservableList) {
            ((ObservableList)list1).setAll(converted);
        } else {
            list1.clear();
            list1.addAll(converted);
        }
        list2.removeListener(contentBinding);
        list2.addListener(contentBinding);
        return contentBinding;
    }

    @SuppressWarnings({"unchecked"})
    public static <S, E> Object bind(Set<E> set1, ObservableSet<? extends S> set2, ValueConverter<S, E> converter) {
        checkParameters(set1, set2, converter);
        final ConvertingSetContentBinding<S, E> contentBinding = new ConvertingSetContentBinding<>(set1, converter);
        List<E> converted = new ArrayList<>(set2.size());
        for (S item : set2) {
            converted.add(converter.convert(item));
        }
        set1.clear();
        set1.addAll(converted);
        set2.removeListener(contentBinding);
        set2.addListener(contentBinding);
        return contentBinding;
    }

    @SuppressWarnings({"unchecked"})
    public static <K, S, V> Object bind(Map<K, V> map1, ObservableMap<? extends K, ? extends S> map2, ValueConverter<S, V> converter) {
        checkParameters(map1, map2, converter);
        final ConvertingMapContentBinding<K, S, V> contentBinding = new ConvertingMapContentBinding<>(map1, converter);
        Map<K, V> converted = new HashMap<>(map2.size());
        for (Map.Entry<? extends K, ? extends S> entry : map2.entrySet()) {
            converted.put(entry.getKey(), converter.convert(entry.getValue()));
        }
        map1.clear();
        map1.putAll(converted);
        map2.removeListener(contentBinding);
        map2.addListener(contentBinding);
        return contentBinding;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static class ConvertingListContentBinding<S, E> extends ContentBinding.ListContentBinding {
        private final ValueConverter<S, E> converter;

        public ConvertingListContentBinding(List<E> list, ValueConverter<S, E> converter) {
            super(list);
            this.converter = converter;
        }

        @Override
        public void onChanged(Change change) {
            final List<E> list = (List<E>)listRef.get();
            if (list == null) {
                change.getList().removeListener(this);
            } else {
                while (change.next()) {
                    int from = change.getFrom();
                    int to = change.getTo();

                    if (change.wasPermutated()) {
                        List<E> subList = list.subList(from, to);
                        List<E> copy = new ArrayList<>(subList);

                        for (int oldIndex = from; oldIndex < to; ++oldIndex) {
                            int newIndex = change.getPermutation(oldIndex);
                            copy.set(newIndex - from, list.get(oldIndex));
                        }

                        subList.clear();
                        list.addAll(from, copy);
                    } else {
                        if (change.wasRemoved()) {
                            list.subList(from, from + change.getRemovedSize()).clear();
                        }
                        if (change.wasAdded()) {
                            List<E> newList = new ArrayList<>(change.getAddedSubList().size());
                            for (S item : (List<S>)change.getAddedSubList()) {
                                newList.add(converter.convert(item));
                            }
                            list.addAll(from, newList);
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static class ConvertingSetContentBinding<S, E> extends ContentBinding.SetContentBinding {
        private final ValueConverter<S, E> converter;

        public ConvertingSetContentBinding(Set<E> set, ValueConverter<S, E> converter) {
            super(set);
            this.converter = converter;
        }

        @Override
        public void onChanged(Change change) {
            final Set<E> set = (Set<E>)setRef.get();
            if (set == null) {
                change.getSet().removeListener(this);
            } else {
                if (change.wasRemoved()) {
                    set.remove(converter.convert((S)change.getElementRemoved()));
                } else {
                    set.add(converter.convert((S)change.getElementAdded()));
                }
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static class ConvertingMapContentBinding<K, S, V> extends ContentBinding.MapContentBinding {
        private final ValueConverter<S, V> converter;

        public ConvertingMapContentBinding(Map<K, V> map, ValueConverter<S, V> converter) {
            super(map);
            this.converter = converter;
        }

        @Override
        public void onChanged(Change change) {
            final Map<K, V> map = (Map<K, V>)mapRef.get();
            if (map == null) {
                change.getMap().removeListener(this);
            } else {
                if (change.wasRemoved()) {
                    map.remove((K)change.getKey());
                }
                if (change.wasAdded()) {
                    map.put((K)change.getKey(), converter.convert((S)change.getValueAdded()));
                }
            }
        }
    }
}
