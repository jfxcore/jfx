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

package javafx.theme.caspian;

import com.sun.javafx.PlatformUtil;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.theme.Theme;
import javafx.beans.binding.ListBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.Objects;

public class CaspianTheme implements Theme {

    private static final String WINDOWS_HIGH_CONTRAST_ON_KEY = "Windows.SPI_HighContrastOn";

    private final ObservableList<String> baseStylesheets = FXCollections.observableArrayList();
    private final ObservableList<String> accessibilityStylesheets = FXCollections.observableArrayList();
    private final ObservableList<String> allStylesheets;
    private Map<String, String> platformThemeProperties;

    public CaspianTheme(Map<String, String> platformThemeProperties) {
        this.platformThemeProperties = platformThemeProperties;

        baseStylesheets.add("com/sun/javafx/scene/control/skin/caspian/caspian.css");

        if (Platform.isSupported(ConditionalFeature.INPUT_TOUCH)) {
            baseStylesheets.add("com/sun/javafx/scene/control/skin/caspian/embedded.css");

            if (com.sun.javafx.util.Utils.isQVGAScreen()) {
                baseStylesheets.add("com/sun/javafx/scene/control/skin/caspian/embedded-qvga.css");
            }

            if (PlatformUtil.isAndroid()) {
                baseStylesheets.add("com/sun/javafx/scene/control/skin/caspian/android.css");
            }

            if (PlatformUtil.isIOS()) {
                baseStylesheets.add("com/sun/javafx/scene/control/skin/caspian/ios.css");
            }
        }

        if (Platform.isSupported(ConditionalFeature.TWO_LEVEL_FOCUS)) {
            baseStylesheets.add("com/sun/javafx/scene/control/skin/caspian/two-level-focus.css");
        }

        if (Platform.isSupported(ConditionalFeature.VIRTUAL_KEYBOARD)) {
            baseStylesheets.add("com/sun/javafx/scene/control/skin/caspian/fxvk.css");
        }

        if (!Platform.isSupported(ConditionalFeature.TRANSPARENT_WINDOW)) {
            baseStylesheets.add("com/sun/javafx/scene/control/skin/caspian/caspian-no-transparency.css");
        }

        updateAccessibilityStylesheets();

        allStylesheets = new ListBinding<>() {
            {
                bind(baseStylesheets, accessibilityStylesheets);
            }

            @Override
            @SuppressWarnings("unchecked")
            protected ObservableList<String> computeValue() {
                return FXCollections.concat(baseStylesheets, accessibilityStylesheets);
            }
        };
    }

    @Override
    public ObservableList<String> getStylesheets() {
        return allStylesheets;
    }

    @Override
    public void platformThemeChanged(Map<String, String> properties) {
        boolean accessibilityThemeChanged = !Objects.equals(
                platformThemeProperties.get(WINDOWS_HIGH_CONTRAST_ON_KEY),
                properties.get(WINDOWS_HIGH_CONTRAST_ON_KEY));

        this.platformThemeProperties = properties;

        if (accessibilityThemeChanged) {
            updateAccessibilityStylesheets();
        }
    }

    private void updateAccessibilityStylesheets() {
        // check to see if there is an override to enable a high-contrast theme
        final String userTheme = AccessController.doPrivileged(
            (PrivilegedAction<String>) () -> System.getProperty("com.sun.javafx.highContrastTheme"));

        boolean highContrastOn = "true".equals(platformThemeProperties.get(WINDOWS_HIGH_CONTRAST_ON_KEY));
        String accessibilityTheme = null;

        if (highContrastOn || userTheme != null) {
            // caspian has only one high contrast theme, use it regardless of the user or platform theme.
            accessibilityTheme = "com/sun/javafx/scene/control/skin/caspian/highcontrast.css";
        }

        if (accessibilityTheme != null) {
            accessibilityStylesheets.setAll(accessibilityTheme);
        } else {
            accessibilityStylesheets.clear();
        }
    }

}