package com.example.components;

import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Theme {
    // Define colors
    public static Color BACKGROUND_COLOR = Color.web("#F8F8F8");
    public static final Color NAVY_COLOR = Color.web("#171926");
    public static final Color LIGHT_NAVY_COLOR = Color.web("#24293C");
    public static final Color WHITE_COLOR = Color.web("#FFFFFF");
    public static final Color BORDER_COLOR = Color.web("#DEE5F0");
    public static final Color BLUE_COLOR = Color.web("#2954ED");
    public static final Color GRAY_COLOR = Color.web("#C1C5CD");

    private static final String FONT_PATH = "/com/example/fonts/";

    public static Font getPoppinsFont(int weight, double size) {
        String fontFileName = mapWeightToFontFile(weight);
        return loadFont(FONT_PATH + fontFileName, size);
    }

    private static String mapWeightToFontFile(int weight) {
        switch (weight) {
            case 100:
                return "Poppins-Thin.ttf";
            case 200:
                return "Poppins-ExtraLight.ttf";
            case 300:
                return "Poppins-Light.ttf";
            case 400:
                return "Poppins-Regular.ttf";
            case 500:
                return "Poppins-Medium.ttf";
            case 600:
                return "Poppins-SemiBold.ttf";
            case 700:
                return "Poppins-Bold.ttf";
            case 800:
                return "Poppins-ExtraBold.ttf";
            case 900:
                return "Poppins-Black.ttf";
            default:
                return null;
        }
    }

    private static Font loadFont(String path, double size) {
        return Font.loadFont(Theme.class.getResourceAsStream(path), size);
    }

    public static DropShadow createShadow() {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(149, 157, 165, 0.2));
        shadow.setRadius(24);
        shadow.setOffsetY(8);
        shadow.setOffsetX(0);
        return shadow;
    }
}
