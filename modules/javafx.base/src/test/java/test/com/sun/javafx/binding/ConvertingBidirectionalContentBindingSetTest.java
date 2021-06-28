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
import javafx.collections.ObservableSet;
import javafx.util.BidirectionalValueConverter;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ConvertingBidirectionalContentBindingSetTest {

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

    private ObservableSet<Integer> op1;
    private ObservableSet<String> op2;
    private Set<Integer> set0_int;
    private Set<Integer> set1_int;
    private Set<Integer> set2_int;
    private Set<String> set0_str;
    private Set<String> set1_str;
    private Set<String> set2_str;

    @Before
    public void setUp() {
        set0_int = new HashSet<>();
        set1_int = new HashSet<>();
        set1_int.add(-1);
        set2_int = new HashSet<>();
        set2_int.add(2);
        set2_int.add(1);

        set0_str = new HashSet<>();
        set1_str = new HashSet<>();
        set1_str.add("-1");
        set2_str = new HashSet<>();
        set2_str.add("2");
        set2_str.add("1");

        op1 = FXCollections.observableSet(new HashSet<>(set1_int));
        op2 = FXCollections.observableSet(new HashSet<>(set2_str));
    }

    @Test
    public void testBind() {
        Bindings.bindContentBidirectional(op1, op2, converter);
        System.gc(); // making sure we did not not overdo weak references
        assertEquals(set2_int, op1);
        assertEquals(set2_str, op2);

        op1.clear();
        op1.addAll(set1_int);
        assertEquals(set1_int, op1);
        assertEquals(set1_str, op2);

        op1.clear();
        op1.addAll(set0_int);
        assertEquals(set0_int, op1);
        assertEquals(set0_str, op2);

        op1.clear();
        op1.addAll(set2_int);
        assertEquals(set2_int, op1);
        assertEquals(set2_str, op2);

        op2.clear();
        op2.addAll(set1_str);
        assertEquals(set1_int, op1);
        assertEquals(set1_str, op2);

        op2.clear();
        op2.addAll(set0_str);
        assertEquals(set0_int, op1);
        assertEquals(set0_str, op2);

        op2.clear();
        op2.addAll(set2_str);
        assertEquals(set2_int, op1);
        assertEquals(set2_str, op2);
    }

    @Test
    public void testUnbind() {
        // unbind non-existing binding => no-op
        Bindings.unbindContentBidirectional(op1, op2);

        Bindings.bindContentBidirectional(op1, op2, converter);
        System.gc(); // making sure we did not not overdo weak references
        assertEquals(set2_int, op1);
        assertEquals(set2_str, op2);

        Bindings.unbindContentBidirectional(op1, op2);
        System.gc();
        assertEquals(set2_int, op1);
        assertEquals(set2_str, op2);

        op1.clear();
        op1.addAll(set1_int);
        assertEquals(set1_int, op1);
        assertEquals(set2_str, op2);

        op2.clear();
        op2.addAll(set0_str);
        assertEquals(set1_int, op1);
        assertEquals(set0_str, op2);

        // unbind in flipped order
        Bindings.bindContentBidirectional(op1, op2, converter);
        System.gc(); // making sure we did not not overdo weak references
        assertEquals(set0_int, op1);
        assertEquals(set0_str, op2);

        Bindings.unbindContentBidirectional(op2, op1);
        System.gc();
        assertEquals(set0_int, op1);
        assertEquals(set0_str, op2);

        op1.clear();
        op1.addAll(set1_int);
        assertEquals(set1_int, op1);
        assertEquals(set0_str, op2);

        op2.clear();
        op2.addAll(set2_str);
        assertEquals(set1_int, op1);
        assertEquals(set2_str, op2);
    }
    
}
