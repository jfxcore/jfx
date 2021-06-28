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
import javafx.util.BidirectionalValueConverter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConvertingBidirectionalContentBinding {

    private static <S, E> void checkParameters(Object property1, Object property2, BidirectionalValueConverter<S, E> converter) {
        if (property1 == null || property2 == null || converter == null) {
            throw new NullPointerException("All parameters must be specified.");
        }
        if (property1 == property2) {
            throw new IllegalArgumentException("Cannot bind object to itself");
        }
    }

    @SuppressWarnings("unchecked")
    public static <S, E> Object bind(ObservableList<E> list1, ObservableList<S> list2, BidirectionalValueConverter<S, E> converter) {
        checkParameters(list1, list2, converter);
        final ConvertingListContentBinding<S, E> binding = new ConvertingListContentBinding<>(list1, list2, converter);
        List<E> converted = new ArrayList<>(list2.size());
        for (S item : list2) {
            converted.add(converter.convert(item));
        }
        list1.setAll(converted);
        list1.addListener(binding);
        list2.addListener(binding);
        return binding;
    }

    @SuppressWarnings("unchecked")
    public static <S, E> Object bind(ObservableSet<E> set1, ObservableSet<S> set2, BidirectionalValueConverter<S, E> converter) {
        checkParameters(set1, set2, converter);
        final ConvertingSetContentBinding<S, E> binding = new ConvertingSetContentBinding<>(set1, set2, converter);
        List<E> converted = new ArrayList<>(set2.size());
        for (S item : set2) {
            converted.add(converter.convert(item));
        }
        set1.clear();
        set1.addAll(converted);
        set1.addListener(binding);
        set2.addListener(binding);
        return binding;
    }

    @SuppressWarnings("unchecked")
    public static <K, S, V> Object bind(ObservableMap<K, V> map1, ObservableMap<K, S> map2, BidirectionalValueConverter<S, V> converter) {
        checkParameters(map1, map2, converter);
        final ConvertingMapContentBinding<K, S, V> binding = new ConvertingMapContentBinding<>(map1, map2, converter);
        Map<K, V> converted = new HashMap<>(map2.size());
        for (Map.Entry<K, S> entry : map2.entrySet()) {
            converted.put(entry.getKey(), converter.convert(entry.getValue()));
        }
        map1.clear();
        map1.putAll(converted);
        map1.addListener(binding);
        map2.addListener(binding);
        return binding;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static class ConvertingListContentBinding<S, E> extends BidirectionalContentBinding.ListContentBinding {
        private final BidirectionalValueConverter<S, E> converter;

        public ConvertingListContentBinding(ObservableList<E> list1, ObservableList<S> list2, BidirectionalValueConverter<S, E> converter) {
            super(list1, list2);
            this.converter = converter;
        }

        @Override
        public void onChanged(Change change) {
            if (!updating) {
                final ObservableList<E> list1 = (ObservableList<E>)propertyRef1.get();
                final ObservableList<S> list2 = (ObservableList<S>)propertyRef2.get();
                if ((list1 == null) || (list2 == null)) {
                    if (list1 != null) {
                        list1.removeListener(this);
                    }
                    if (list2 != null) {
                        list2.removeListener(this);
                    }
                } else {
                    try {
                        updating = true;
                        ObservableList dest = list1 == change.getList() ? list2 : list1;
                        boolean convertBack = dest == list2;

                        while (change.next()) {
                            int from = change.getFrom();
                            int to = change.getTo();

                            if (change.wasPermutated()) {
                                List subList = dest.subList(from, to);
                                List copy = new ArrayList(subList);

                                for (int oldIndex = from; oldIndex < to; ++oldIndex) {
                                    int newIndex = change.getPermutation(oldIndex);
                                    copy.set(newIndex - from, dest.get(oldIndex));
                                }

                                subList.clear();
                                dest.addAll(from, copy);
                            } else {
                                if (change.wasRemoved()) {
                                    dest.remove(from, from + change.getRemovedSize());
                                }
                                if (change.wasAdded()) {
                                    if (change.getAddedSize() == 1) {
                                        if (convertBack) {
                                            dest.add(from, converter.convertBack((E)change.getList().get(from)));
                                        } else {
                                            dest.add(from, converter.convert((S)change.getList().get(from)));
                                        }
                                    } else {
                                        List newList = new ArrayList<>(to - from);
                                        for (Object item : change.getAddedSubList()) {
                                            if (convertBack) {
                                                newList.add(converter.convertBack((E)item));
                                            } else {
                                                newList.add(converter.convert((S)item));
                                            }
                                        }
                                        dest.addAll(from, newList);
                                    }
                                }
                            }
                        }
                    } finally {
                        updating = false;
                    }
                }
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static class ConvertingSetContentBinding<S, E> extends BidirectionalContentBinding.SetContentBinding {
        private final BidirectionalValueConverter<S, E> converter;

        public ConvertingSetContentBinding(ObservableSet<E> list1, ObservableSet<S> list2, BidirectionalValueConverter<S, E> converter) {
            super(list1, list2);
            this.converter = converter;
        }

        @Override
        public void onChanged(Change change) {
            if (!updating) {
                final ObservableSet<E> set1 = (ObservableSet<E>)propertyRef1.get();
                final ObservableSet<S> set2 = (ObservableSet<S>)propertyRef2.get();
                if ((set1 == null) || (set2 == null)) {
                    if (set1 != null) {
                        set1.removeListener(this);
                    }
                    if (set2 != null) {
                        set2.removeListener(this);
                    }
                } else {
                    try {
                        updating = true;
                        if (set1 == change.getSet()) {
                            if (change.wasRemoved()) {
                                set2.remove(converter.convertBack((E)change.getElementRemoved()));
                            } else {
                                set2.add(converter.convertBack((E)change.getElementAdded()));
                            }
                        } else {
                            if (change.wasRemoved()) {
                                set1.remove(converter.convert((S)change.getElementRemoved()));
                            } else {
                                set1.add(converter.convert((S)change.getElementAdded()));
                            }
                        }
                    } finally {
                        updating = false;
                    }
                }
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static class ConvertingMapContentBinding<K, S, V> extends BidirectionalContentBinding.MapContentBinding {
        private final BidirectionalValueConverter<S, V> converter;

        public ConvertingMapContentBinding(ObservableMap<K, V> list1, ObservableMap<K, S> list2, BidirectionalValueConverter<S, V> converter) {
            super(list1, list2);
            this.converter = converter;
        }

        @Override
        public void onChanged(Change change) {
            if (!updating) {
                final ObservableMap<K, V> map1 = (ObservableMap<K, V>)propertyRef1.get();
                final ObservableMap<K, S> map2 = (ObservableMap<K, S>)propertyRef2.get();
                if ((map1 == null) || (map2 == null)) {
                    if (map1 != null) {
                        map1.removeListener(this);
                    }
                    if (map2 != null) {
                        map2.removeListener(this);
                    }
                } else {
                    try {
                        updating = true;
                        if (map1 == change.getMap()) {
                            if (change.wasRemoved()) {
                                map2.remove((K)change.getKey());
                            } else {
                                map2.put((K)change.getKey(), converter.convertBack((V)change.getValueAdded()));
                            }
                        } else {
                            if (change.wasRemoved()) {
                                map1.remove((K)change.getKey());
                            } else {
                                map1.put((K)change.getKey(), converter.convert((S)change.getValueAdded()));
                            }
                        }
                    } finally {
                        updating = false;
                    }
                }
            }
        }
    }
}
