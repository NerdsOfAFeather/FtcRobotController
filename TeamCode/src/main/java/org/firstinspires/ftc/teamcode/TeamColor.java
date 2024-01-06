package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

/**Created by Gavin for FTC Team 6347*/
public enum TeamColor {

    RED, BLUE, UNSET;

    @NonNull
    @Override
    public String toString() {
        if (this == RED) {
            return "RED";
        } else if (this == BLUE) {
            return "BLUE";
        } else if (this == UNSET) {
            return "UNSET | X - BLUE | B - RED";
        } else {
            return "";
        }
    }
}
