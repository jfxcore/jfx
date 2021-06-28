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

package test.com.sun.javafx.binding;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.util.BidirectionalValueConverter;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ConvertingBidirectionalContentBindingMapTest {

    private static final BidirectionalValueConverter<String, Integer> converter = new BidirectionalValueConverter<>() {
        @Override
        public String convertBack(Integer value) {
            return Integer.toString(value);
        }

        @Override
        public Integer convert(String value) {
            return Integer.parseInt(value);
        }
    };

    private static final String key1 = "Key1";
    private static final String key2_1 = "Key2_1";
    private static final String key2_2 = "Key2_2";

    private ObservableMap<String, Integer> op1;
    private ObservableMap<String, String> op2;
    private ObservableMap<String, Integer> op3;
    private Map<String, Integer> map0_int;
    private Map<String, Integer> map1_int;
    private Map<String, Integer> map2_int;
    private Map<String, String> map0_str;
    private Map<String, String> map1_str;
    private Map<String, String> map2_str;

    @Before
    public void setUp() {
        map0_int = new HashMap<>();
        map1_int = new HashMap<>();
        map1_int.put(key1, -1);
        map2_int = new HashMap<>();
        map2_int.put(key2_1, 2);
        map2_int.put(key2_2, 1);

        map0_str = new HashMap<>();
        map1_str = new HashMap<>();
        map1_str.put(key1, "-1");
        map2_str = new HashMap<>();
        map2_str.put(key2_1, "2");
        map2_str.put(key2_2, "1");

        op1 = FXCollections.observableMap(new HashMap<>(map1_int));
        op2 = FXCollections.observableMap(new HashMap<>(map2_str));
        op3 = FXCollections.observableMap(new HashMap<>(map0_int));
    }

    @Test
    public void testBind() {
        Bindings.bindContentBidirectional(op1, op2, converter);
        System.gc(); // making sure we did not not overdo weak references
        assertEquals(map2_int, op1);
        assertEquals(map2_str, op2);

        op1.clear();
        op1.putAll(map1_int);
        assertEquals(map1_int, op1);
        assertEquals(map1_str, op2);

        op1.clear();
        op1.putAll(map0_int);
        assertEquals(map0_int, op1);
        assertEquals(map0_str, op2);

        op1.clear();
        op1.putAll(map2_int);
        assertEquals(map2_int, op1);
        assertEquals(map2_str, op2);

        op2.clear();
        op2.putAll(map1_str);
        assertEquals(map1_int, op1);
        assertEquals(map1_str, op2);

        op2.clear();
        op2.putAll(map0_str);
        assertEquals(map0_int, op1);
        assertEquals(map0_str, op2);

        op2.clear();
        op2.putAll(map2_str);
        assertEquals(map2_int, op1);
        assertEquals(map2_str, op2);
    }

    @Test
    public void testUnbind() {
        // unbind non-existing binding => no-op
        Bindings.unbindContentBidirectional(op1, op2);

        Bindings.bindContentBidirectional(op1, op2, converter);
        System.gc(); // making sure we did not not overdo weak references
        assertEquals(map2_int, op1);
        assertEquals(map2_str, op2);

        Bindings.unbindContentBidirectional(op1, op2);
        System.gc();
        assertEquals(map2_int, op1);
        assertEquals(map2_str, op2);

        op1.clear();
        op1.putAll(map1_int);
        assertEquals(map1_int, op1);
        assertEquals(map2_str, op2);

        op2.clear();
        op2.putAll(map0_str);
        assertEquals(map1_int, op1);
        assertEquals(map0_str, op2);

        // unbind in flipped order
        Bindings.bindContentBidirectional(op1, op2, converter);
        System.gc(); // making sure we did not not overdo weak references
        assertEquals(map0_int, op1);
        assertEquals(map0_str, op2);

        Bindings.unbindContentBidirectional(op2, op1);
        System.gc();
        assertEquals(map0_int, op1);
        assertEquals(map0_str, op2);

        op1.clear();
        op1.putAll(map1_int);
        assertEquals(map1_int, op1);
        assertEquals(map0_str, op2);

        op2.clear();
        op2.putAll(map2_str);
        assertEquals(map1_int, op1);
        assertEquals(map2_str, op2);
    }
    
}
