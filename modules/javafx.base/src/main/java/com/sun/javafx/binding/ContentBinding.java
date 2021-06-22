/*
 * Copyright (c) 2011, 2015, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.javafx.binding;

import javafx.beans.WeakListener;
import javafx.collections.*;
import javafx.util.ValueConverter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 */
public abstract class ContentBinding implements WeakListener {

    protected abstract WeakReference<?> getSourceRef();

    @Override
    public boolean wasGarbageCollected() {
        return getSourceRef().get() == null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        final Object source1 = getSourceRef().get();
        if (source1 == null) {
            return false;
        }

        if (obj instanceof ContentBinding) {
            final ContentBinding other = (ContentBinding)obj;
            final Object source2 = other.getSourceRef().get();
            return source1 == source2;
        }
        return false;
    }

    private static void checkParameters(Object property1, Object property2) {
        if ((property1 == null) || (property2 == null)) {
            throw new NullPointerException("All parameters must be specified.");
        }
        if (property1 == property2) {
            throw new IllegalArgumentException("Cannot bind object to itself");
        }
    }

    public static <E> Object bind(List<E> list1, ObservableList<? extends E> list2) {
        checkParameters(list1, list2);
        final ListContentBinding<E> contentBinding = new ListContentBinding<E>(list1);
        if (list1 instanceof ObservableList) {
            ((ObservableList) list1).setAll(list2);
        } else {
            list1.clear();
            list1.addAll(list2);
        }
        list2.removeListener(contentBinding);
        list2.addListener(contentBinding);
        return contentBinding;
    }

    public static <E> Object bind(Set<E> set1, ObservableSet<? extends E> set2) {
        checkParameters(set1, set2);
        final SetContentBinding<E> contentBinding = new SetContentBinding<E>(set1);
        set1.clear();
        set1.addAll(set2);
        set2.removeListener(contentBinding);
        set2.addListener(contentBinding);
        return contentBinding;
    }

    public static <K, V> Object bind(Map<K, V> map1, ObservableMap<? extends K, ? extends V> map2) {
        checkParameters(map1, map2);
        final MapContentBinding<K, V> contentBinding = new MapContentBinding<K, V>(map1);
        map1.clear();
        map1.putAll(map2);
        map2.removeListener(contentBinding);
        map2.addListener(contentBinding);
        return contentBinding;
    }

    public static <S, E> Object bind(List<E> list1, ObservableList<? extends S> list2, ValueConverter<S, E> converter) {
        checkParameters(list1, list2);
        if (converter == null) {
            throw new NullPointerException("Converter cannot be null");
        }
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

    public static <S, E> Object bind(Set<E> set1, ObservableSet<? extends S> set2, ValueConverter<S, E> converter) {
        checkParameters(set1, set2);
        if (converter == null) {
            throw new NullPointerException("Converter cannot be null");
        }
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

    public static <K, S, V> Object bind(Map<K, V> map1, ObservableMap<? extends K, ? extends S> map2, ValueConverter<S, V> converter) {
        checkParameters(map1, map2);
        if (converter == null) {
            throw new NullPointerException("Converter cannot be null");
        }
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

    public static void unbind(Object obj1, Object obj2) {
        checkParameters(obj1, obj2);
        if ((obj1 instanceof List) && (obj2 instanceof ObservableList)) {
            ((ObservableList)obj2).removeListener(new ListContentBinding((List)obj1));
        } else if ((obj1 instanceof Set) && (obj2 instanceof ObservableSet)) {
            ((ObservableSet)obj2).removeListener(new SetContentBinding((Set)obj1));
        } else if ((obj1 instanceof Map) && (obj2 instanceof ObservableMap)) {
            ((ObservableMap)obj2).removeListener(new MapContentBinding((Map)obj1));
        }
    }

    private static class ListContentBinding<E> extends ContentBinding implements ListChangeListener<E> {

        private final WeakReference<List<E>> listRef;

        public ListContentBinding(List<E> list) {
            this.listRef = new WeakReference<List<E>>(list);
        }

        @Override
        public void onChanged(Change<? extends E> change) {
            final List<E> list = listRef.get();
            if (list == null) {
                change.getList().removeListener(this);
            } else {
                while (change.next()) {
                    if (change.wasPermutated()) {
                        list.subList(change.getFrom(), change.getTo()).clear();
                        list.addAll(change.getFrom(), change.getList().subList(change.getFrom(), change.getTo()));
                    } else {
                        if (change.wasRemoved()) {
                            list.subList(change.getFrom(), change.getFrom() + change.getRemovedSize()).clear();
                        }
                        if (change.wasAdded()) {
                            list.addAll(change.getFrom(), change.getAddedSubList());
                        }
                    }
                }
            }
        }

        @Override
        protected WeakReference<?> getSourceRef() {
            return listRef;
        }

        @Override
        public int hashCode() {
            final List<E> list = listRef.get();
            return (list == null)? 0 : list.hashCode();
        }
    }

    private static class SetContentBinding<E> extends ContentBinding implements SetChangeListener<E>{

        private final WeakReference<Set<E>> setRef;

        public SetContentBinding(Set<E> set) {
            this.setRef = new WeakReference<Set<E>>(set);
        }

        @Override
        public void onChanged(Change<? extends E> change) {
            final Set<E> set = setRef.get();
            if (set == null) {
                change.getSet().removeListener(this);
            } else {
                if (change.wasRemoved()) {
                    set.remove(change.getElementRemoved());
                } else {
                    set.add(change.getElementAdded());
                }
            }
        }

        @Override
        protected WeakReference<?> getSourceRef() {
            return setRef;
        }

        @Override
        public int hashCode() {
            final Set<E> set = setRef.get();
            return (set == null)? 0 : set.hashCode();
        }
    }

    private static class MapContentBinding<K, V> extends ContentBinding implements MapChangeListener<K, V> {

        private final WeakReference<Map<K, V>> mapRef;

        public MapContentBinding(Map<K, V> map) {
            this.mapRef = new WeakReference<Map<K, V>>(map);
        }

        @Override
        public void onChanged(Change<? extends K, ? extends V> change) {
            final Map<K, V> map = mapRef.get();
            if (map == null) {
                change.getMap().removeListener(this);
            } else {
                if (change.wasRemoved()) {
                    map.remove(change.getKey());
                }
                if (change.wasAdded()) {
                    map.put(change.getKey(), change.getValueAdded());
                }
            }
        }

        @Override
        protected WeakReference<?> getSourceRef() {
            return mapRef;
        }

        @Override
        public int hashCode() {
            final Map<K, V> map = mapRef.get();
            return (map == null)? 0 : map.hashCode();
        }
    }

    private static class ConvertingListContentBinding<S, E> extends ContentBinding implements ListChangeListener<S> {
        private final WeakReference<List<E>> listRef;
        private final ValueConverter<S, E> converter;

        public ConvertingListContentBinding(List<E> list, ValueConverter<S, E> converter) {
            this.listRef = new WeakReference<List<E>>(list);
            this.converter = converter;
        }

        @Override
        public void onChanged(Change<? extends S> change) {
            final List<E> list = listRef.get();
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
                            for (S item : change.getAddedSubList()) {
                                newList.add(converter.convert(item));
                            }
                            list.addAll(from, newList);
                        }
                    }
                }
            }
        }

        @Override
        protected WeakReference<?> getSourceRef() {
            return listRef;
        }

        @Override
        public int hashCode() {
            final List<E> list = listRef.get();
            return (list == null)? 0 : list.hashCode();
        }
    }

    private static class ConvertingSetContentBinding<S, E> extends ContentBinding implements SetChangeListener<S>{
        private final WeakReference<Set<E>> setRef;
        private final ValueConverter<S, E> converter;

        public ConvertingSetContentBinding(Set<E> set, ValueConverter<S, E> converter) {
            this.setRef = new WeakReference<Set<E>>(set);
            this.converter = converter;
        }

        @Override
        public void onChanged(Change<? extends S> change) {
            final Set<E> set = setRef.get();
            if (set == null) {
                change.getSet().removeListener(this);
            } else {
                if (change.wasRemoved()) {
                    set.remove(converter.convert(change.getElementRemoved()));
                } else {
                    set.add(converter.convert(change.getElementAdded()));
                }
            }
        }

        @Override
        protected WeakReference<?> getSourceRef() {
            return setRef;
        }

        @Override
        public int hashCode() {
            final Set<E> set = setRef.get();
            return (set == null)? 0 : set.hashCode();
        }
    }

    private static class ConvertingMapContentBinding<K, S, V> extends ContentBinding implements MapChangeListener<K, S>{
        private final WeakReference<Map<K, V>> mapRef;
        private final ValueConverter<S, V> converter;

        public ConvertingMapContentBinding(Map<K, V> map, ValueConverter<S, V> converter) {
            this.mapRef = new WeakReference<Map<K, V>>(map);
            this.converter = converter;
        }

        @Override
        public void onChanged(Change<? extends K, ? extends S> change) {
            final Map<K, V> map = mapRef.get();
            if (map == null) {
                change.getMap().removeListener(this);
            } else {
                if (change.wasRemoved()) {
                    map.remove(change.getKey());
                }
                if (change.wasAdded()) {
                    map.put(change.getKey(), converter.convert(change.getValueAdded()));
                }
            }
        }

        @Override
        protected WeakReference<?> getSourceRef() {
            return mapRef;
        }

        @Override
        public int hashCode() {
            final Map<K, V> map = mapRef.get();
            return (map == null)? 0 : map.hashCode();
        }
    }
}
