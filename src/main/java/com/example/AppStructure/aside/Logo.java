package com.example.AppStructure.aside;

import com.example.components.Theme;
import javafx.scene.text.Text;

public class Logo extends Text {
    public Logo() {
        super("SababaAuto");
        setFont(Theme.getPoppinsFont(400, 24));
        setFill(Theme.GRAY_COLOR);
    }
}
