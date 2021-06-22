/*
 * Copyright (c) 2011, 2016, Oracle and/or its affiliates. All rights reserved.
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
import javafx.collections.ObservableList;
import javafx.util.BidirectionalValueConverter;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ConvertingBidirectionalContentBindingListTest {

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

    private ObservableList<Integer> op1;
    private ObservableList<String> op2;
    private List<Integer> list0_int;
    private List<Integer> list1_int;
    private List<Integer> list2_int;
    private List<String> list0_str;
    private List<String> list1_str;
    private List<String> list2_str;

    @Before
    public void setUp() {
        list0_int = new ArrayList<>();
        list1_int = new ArrayList<>(Arrays.asList(-1));
        list2_int = new ArrayList<>(Arrays.asList(2, 1));
        list0_str = new ArrayList<>();
        list1_str = new ArrayList<>(Arrays.asList("-1"));
        list2_str = new ArrayList<>(Arrays.asList("2", "1"));

        op1 = FXCollections.observableArrayList(list1_int);
        op2 = FXCollections.observableArrayList(list2_str);
    }

    @Test
    public void testBind() {
        final List<Integer> list2_sorted_int = new ArrayList<>(Arrays.asList(1, 2));
        final List<String> list2_sorted_str = new ArrayList<>(Arrays.asList("1", "2"));

        Bindings.bindContentBidirectional(op1, op2, converter);
        System.gc(); // making sure we did not not overdo weak references
        assertEquals(list2_int, op1);
        assertEquals(list2_str, op2);

        op1.setAll(list1_int);
        assertEquals(list1_int, op1);
        assertEquals(list1_str, op2);

        op1.setAll(list0_int);
        assertEquals(list0_int, op1);
        assertEquals(list0_str, op2);

        op1.setAll(list2_int);
        assertEquals(list2_int, op1);
        assertEquals(list2_str, op2);

        FXCollections.sort(op1);
        assertEquals(list2_sorted_int, op1);
        assertEquals(list2_sorted_str, op2);

        op2.setAll(list1_str);
        assertEquals(list1_int, op1);
        assertEquals(list1_str, op2);

        op2.setAll(list0_str);
        assertEquals(list0_int, op1);
        assertEquals(list0_str, op2);

        op2.setAll(list2_str);
        assertEquals(list2_int, op1);
        assertEquals(list2_str, op2);

        FXCollections.sort(op2);
        assertEquals(list2_sorted_int, op1);
        assertEquals(list2_sorted_str, op2);
    }

    @Test
    public void testUnbind() {
        // unbind non-existing binding => no-op
        Bindings.unbindContentBidirectional(op1, op2);

        Bindings.bindContentBidirectional(op1, op2, converter);
        System.gc(); // making sure we did not not overdo weak references
        assertEquals(list2_int, op1);
        assertEquals(list2_str, op2);

        Bindings.unbindContentBidirectional(op1, op2);
        System.gc();
        assertEquals(list2_int, op1);
        assertEquals(list2_str, op2);

        op1.setAll(list1_int);
        assertEquals(list1_int, op1);
        assertEquals(list2_str, op2);

        op2.setAll(list0_str);
        assertEquals(list1_int, op1);
        assertEquals(list0_str, op2);

        // unbind in flipped order
        Bindings.bindContentBidirectional(op1, op2, converter);
        System.gc(); // making sure we did not not overdo weak references
        assertEquals(list0_int, op1);
        assertEquals(list0_str, op2);

        Bindings.unbindContentBidirectional(op2, op1);
        System.gc();
        assertEquals(list0_int, op1);
        assertEquals(list0_str, op2);

        op1.setAll(list1_int);
        assertEquals(list1_int, op1);
        assertEquals(list0_str, op2);

        op2.setAll(list2_str);
        assertEquals(list1_int, op1);
        assertEquals(list2_str, op2);
    }
}
