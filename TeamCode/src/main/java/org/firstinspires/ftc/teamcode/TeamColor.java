package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

/**Created by Gavin for FTC Team 6347*/
public enum TeamColor {

    RED_LONG, BLUE_LONG, RED_SHORT, BLUE_SHORT, UNSET;

    @NonNull
    @Override
    public String toString() {
        if (this == RED_LONG) {
            return "RED";
        } else if (this == BLUE_LONG) {
            return "BLUE";
        } else if (this == BLUE_SHORT) {
            return "BLUE";
        } else if (this == RED_SHORT) {
            return "RED";
        } else if (this == UNSET) {
            return "UNSET | X - BLUE | B - RED";
        } else {
            return "";
        }
    }
}
