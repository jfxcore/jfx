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
import javafx.util.BidirectionalValueConverter;
import javafx.util.ValueConverter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 */
public abstract class BidirectionalContentBinding implements WeakListener {

    protected abstract WeakReference<?> getProperty1();

    protected abstract WeakReference<?> getProperty2();

    @Override
    public boolean wasGarbageCollected() {
        return (getProperty1().get() == null) || (getProperty2().get() == null);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        final Object propertyA1 = getProperty1().get();
        final Object propertyA2 = getProperty2().get();
        if ((propertyA1 == null) || (propertyA2 == null)) {
            return false;
        }

        if (obj instanceof BidirectionalContentBinding) {
            final BidirectionalContentBinding otherBinding = (BidirectionalContentBinding)obj;
            final Object propertyB1 = otherBinding.getProperty1().get();
            final Object propertyB2 = otherBinding.getProperty2().get();
            if ((propertyB1 == null) || (propertyB2 == null)) {
                return false;
            }

            if ((propertyA1 == propertyB1) && (propertyA2 == propertyB2)) {
                return true;
            }
            if ((propertyA1 == propertyB2) && (propertyA2 == propertyB1)) {
                return true;
            }
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

    public static <E> Object bind(ObservableList<E> list1, ObservableList<E> list2) {
        checkParameters(list1, list2);
        final ListContentBinding<E> binding = new ListContentBinding<E>(list1, list2);
        list1.setAll(list2);
        list1.addListener(binding);
        list2.addListener(binding);
        return binding;
    }

    public static <E> Object bind(ObservableSet<E> set1, ObservableSet<E> set2) {
        checkParameters(set1, set2);
        final SetContentBinding<E> binding = new SetContentBinding<E>(set1, set2);
        set1.clear();
        set1.addAll(set2);
        set1.addListener(binding);
        set2.addListener(binding);
        return binding;
    }

    public static <K, V> Object bind(ObservableMap<K, V> map1, ObservableMap<K, V> map2) {
        checkParameters(map1, map2);
        final MapContentBinding<K, V> binding = new MapContentBinding<K, V>(map1, map2);
        map1.clear();
        map1.putAll(map2);
        map1.addListener(binding);
        map2.addListener(binding);
        return binding;
    }

    public static <S, E> Object bind(ObservableList<E> list1, ObservableList<S> list2, BidirectionalValueConverter<S, E> converter) {
        checkParameters(list1, list2);
        if (converter == null) {
            throw new NullPointerException("Converter cannot be null");
        }
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

    public static <S, E> Object bind(ObservableSet<E> set1, ObservableSet<S> set2, BidirectionalValueConverter<S, E> converter) {
        checkParameters(set1, set2);
        if (converter == null) {
            throw new NullPointerException("Converter cannot be null");
        }
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

    public static <K, S, V> Object bind(ObservableMap<K, V> map1, ObservableMap<K, S> map2, BidirectionalValueConverter<S, V> converter) {
        checkParameters(map1, map2);
        if (converter == null) {
            throw new NullPointerException("Converter cannot be null");
        }
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

    public static void unbind(Object obj1, Object obj2) {
        checkParameters(obj1, obj2);
        if ((obj1 instanceof ObservableList) && (obj2 instanceof ObservableList)) {
            final ObservableList list1 = (ObservableList)obj1;
            final ObservableList list2 = (ObservableList)obj2;
            final ListContentBinding binding = new ListContentBinding(list1, list2);
            list1.removeListener(binding);
            list2.removeListener(binding);
        } else if ((obj1 instanceof ObservableSet) && (obj2 instanceof ObservableSet)) {
            final ObservableSet set1 = (ObservableSet)obj1;
            final ObservableSet set2 = (ObservableSet)obj2;
            final SetContentBinding binding = new SetContentBinding(set1, set2);
            set1.removeListener(binding);
            set2.removeListener(binding);
        } else if ((obj1 instanceof ObservableMap) && (obj2 instanceof ObservableMap)) {
            final ObservableMap map1 = (ObservableMap)obj1;
            final ObservableMap map2 = (ObservableMap)obj2;
            final MapContentBinding binding = new MapContentBinding(map1, map2);
            map1.removeListener(binding);
            map2.removeListener(binding);
        }
    }

    private static class ListContentBinding<E> extends BidirectionalContentBinding implements ListChangeListener<E>{

        private final WeakReference<ObservableList<E>> propertyRef1;
        private final WeakReference<ObservableList<E>> propertyRef2;

        private boolean updating = false;


        public ListContentBinding(ObservableList<E> list1, ObservableList<E> list2) {
            propertyRef1 = new WeakReference<ObservableList<E>>(list1);
            propertyRef2 = new WeakReference<ObservableList<E>>(list2);
        }

        @Override
        public void onChanged(Change<? extends E> change) {
            if (!updating) {
                final ObservableList<E> list1 = propertyRef1.get();
                final ObservableList<E> list2 = propertyRef2.get();
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
                        final ObservableList<E> dest = (list1 ==  change.getList())? list2 : list1;
                        while (change.next()) {
                            if (change.wasPermutated()) {
                                dest.remove(change.getFrom(), change.getTo());
                                dest.addAll(change.getFrom(), change.getList().subList(change.getFrom(), change.getTo()));
                            } else {
                                if (change.wasRemoved()) {
                                    dest.remove(change.getFrom(), change.getFrom() + change.getRemovedSize());
                                }
                                if (change.wasAdded()) {
                                    dest.addAll(change.getFrom(), change.getAddedSubList());
                                }
                            }
                        }
                    } finally {
                        updating = false;
                    }
                }
            }
        }

        @Override
        protected WeakReference<?> getProperty1() {
            return propertyRef1;
        }

        @Override
        protected WeakReference<?> getProperty2() {
            return propertyRef2;
        }

        @Override
        public int hashCode() {
            final ObservableList<E> list1 = propertyRef1.get();
            final ObservableList<E> list2 = propertyRef2.get();
            final int hc1 = (list1 == null)? 0 : list1.hashCode();
            final int hc2 = (list2 == null)? 0 : list2.hashCode();
            return hc1 * hc2;
        }
    }

    private static class SetContentBinding<E> extends BidirectionalContentBinding implements SetChangeListener<E> {

        private final WeakReference<ObservableSet<E>> propertyRef1;
        private final WeakReference<ObservableSet<E>> propertyRef2;

        private boolean updating = false;


        public SetContentBinding(ObservableSet<E> list1, ObservableSet<E> list2) {
            propertyRef1 = new WeakReference<ObservableSet<E>>(list1);
            propertyRef2 = new WeakReference<ObservableSet<E>>(list2);
        }

        @Override
        public void onChanged(Change<? extends E> change) {
            if (!updating) {
                final ObservableSet<E> set1 = propertyRef1.get();
                final ObservableSet<E> set2 = propertyRef2.get();
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
                        final Set<E> dest = (set1 == change.getSet())? set2 : set1;
                        if (change.wasRemoved()) {
                            dest.remove(change.getElementRemoved());
                        } else {
                            dest.add(change.getElementAdded());
                        }
                    } finally {
                        updating = false;
                    }
                }
            }
        }

        @Override
        protected WeakReference<?> getProperty1() {
            return propertyRef1;
        }

        @Override
        protected WeakReference<?> getProperty2() {
            return propertyRef2;
        }

        @Override
        public int hashCode() {
            final ObservableSet<E> set1 = propertyRef1.get();
            final ObservableSet<E> set2 = propertyRef2.get();
            final int hc1 = (set1 == null)? 0 : set1.hashCode();
            final int hc2 = (set2 == null)? 0 : set2.hashCode();
            return hc1 * hc2;
        }
    }

    private static class MapContentBinding<K, V> extends BidirectionalContentBinding implements MapChangeListener<K, V> {

        private final WeakReference<ObservableMap<K, V>> propertyRef1;
        private final WeakReference<ObservableMap<K, V>> propertyRef2;

        private boolean updating = false;


        public MapContentBinding(ObservableMap<K, V> list1, ObservableMap<K, V> list2) {
            propertyRef1 = new WeakReference<ObservableMap<K, V>>(list1);
            propertyRef2 = new WeakReference<ObservableMap<K, V>>(list2);
        }

        @Override
        public void onChanged(Change<? extends K, ? extends V> change) {
            if (!updating) {
                final ObservableMap<K, V> map1 = propertyRef1.get();
                final ObservableMap<K, V> map2 = propertyRef2.get();
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
                        final Map<K, V> dest = (map1 == change.getMap())? map2 : map1;
                        if (change.wasRemoved()) {
                            dest.remove(change.getKey());
                        }
                        if (change.wasAdded()) {
                            dest.put(change.getKey(), change.getValueAdded());
                        }
                    } finally {
                        updating = false;
                    }
                }
            }
        }

        @Override
        protected WeakReference<?> getProperty1() {
            return propertyRef1;
        }

        @Override
        protected WeakReference<?> getProperty2() {
            return propertyRef2;
        }

        @Override
        public int hashCode() {
            final ObservableMap<K, V> map1 = propertyRef1.get();
            final ObservableMap<K, V> map2 = propertyRef2.get();
            final int hc1 = (map1 == null)? 0 : map1.hashCode();
            final int hc2 = (map2 == null)? 0 : map2.hashCode();
            return hc1 * hc2;
        }
    }

    private static class ConvertingListContentBinding<S, E> extends BidirectionalContentBinding implements ListChangeListener<Object> {
        private final WeakReference<ObservableList<E>> propertyRef1;
        private final WeakReference<ObservableList<S>> propertyRef2;
        private final BidirectionalValueConverter<S, E> converter;
        private boolean updating = false;

        public ConvertingListContentBinding(ObservableList<E> list1, ObservableList<S> list2, BidirectionalValueConverter<S, E> converter) {
            this.propertyRef1 = new WeakReference<>(list1);
            this.propertyRef2 = new WeakReference<>(list2);
            this.converter = converter;
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public void onChanged(Change<?> change) {
            if (!updating) {
                final ObservableList<E> list1 = propertyRef1.get();
                final ObservableList<S> list2 = propertyRef2.get();
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

        @Override
        protected WeakReference<?> getProperty1() {
            return propertyRef1;
        }

        @Override
        protected WeakReference<?> getProperty2() {
            return propertyRef2;
        }

        @Override
        public int hashCode() {
            final ObservableList<E> list1 = propertyRef1.get();
            final ObservableList<S> list2 = propertyRef2.get();
            final int hc1 = (list1 == null)? 0 : list1.hashCode();
            final int hc2 = (list2 == null)? 0 : list2.hashCode();
            return hc1 * hc2;
        }
    }

    private static class ConvertingSetContentBinding<S, E> extends BidirectionalContentBinding implements SetChangeListener<Object> {
        private final WeakReference<ObservableSet<E>> propertyRef1;
        private final WeakReference<ObservableSet<S>> propertyRef2;
        private final BidirectionalValueConverter<S, E> converter;
        private boolean updating = false;


        public ConvertingSetContentBinding(ObservableSet<E> list1, ObservableSet<S> list2, BidirectionalValueConverter<S, E> converter) {
            this.propertyRef1 = new WeakReference<>(list1);
            this.propertyRef2 = new WeakReference<>(list2);
            this.converter = converter;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onChanged(Change<?> change) {
            if (!updating) {
                final ObservableSet<E> set1 = propertyRef1.get();
                final ObservableSet<S> set2 = propertyRef2.get();
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

        @Override
        protected WeakReference<?> getProperty1() {
            return propertyRef1;
        }

        @Override
        protected WeakReference<?> getProperty2() {
            return propertyRef2;
        }

        @Override
        public int hashCode() {
            final ObservableSet<E> set1 = propertyRef1.get();
            final ObservableSet<S> set2 = propertyRef2.get();
            final int hc1 = (set1 == null)? 0 : set1.hashCode();
            final int hc2 = (set2 == null)? 0 : set2.hashCode();
            return hc1 * hc2;
        }
    }

    private static class ConvertingMapContentBinding<K, S, V> extends BidirectionalContentBinding implements MapChangeListener<K, Object> {
        private final WeakReference<ObservableMap<K, V>> propertyRef1;
        private final WeakReference<ObservableMap<K, S>> propertyRef2;
        private final BidirectionalValueConverter<S, V> converter;
        private boolean updating = false;

        public ConvertingMapContentBinding(ObservableMap<K, V> list1, ObservableMap<K, S> list2, BidirectionalValueConverter<S, V> converter) {
            this.propertyRef1 = new WeakReference<>(list1);
            this.propertyRef2 = new WeakReference<>(list2);
            this.converter = converter;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onChanged(Change<? extends K, ?> change) {
            if (!updating) {
                final ObservableMap<K, V> map1 = propertyRef1.get();
                final ObservableMap<K, S> map2 = propertyRef2.get();
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
                                map2.remove(change.getKey());
                            } else {
                                map2.put(change.getKey(), converter.convertBack((V)change.getValueAdded()));
                            }
                        } else {
                            if (change.wasRemoved()) {
                                map1.remove(change.getKey());
                            } else {
                                map1.put(change.getKey(), converter.convert((S)change.getValueAdded()));
                            }
                        }
                    } finally {
                        updating = false;
                    }
                }
            }
        }

        @Override
        protected WeakReference<?> getProperty1() {
            return propertyRef1;
        }

        @Override
        protected WeakReference<?> getProperty2() {
            return propertyRef2;
        }

        @Override
        public int hashCode() {
            final ObservableMap<K, V> map1 = propertyRef1.get();
            final ObservableMap<K, S> map2 = propertyRef2.get();
            final int hc1 = (map1 == null)? 0 : map1.hashCode();
            final int hc2 = (map2 == null)? 0 : map2.hashCode();
            return hc1 * hc2;
        }
    }
}
