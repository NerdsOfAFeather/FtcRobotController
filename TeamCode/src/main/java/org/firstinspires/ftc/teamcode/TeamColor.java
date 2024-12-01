package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

/**Created by Gavin for FTC Team 6347*/
public enum TeamColor {

    RED_RIGHT, BLUE_RIGHT, RED_LEFT, BLUE_LEFT, UNSET;

    @NonNull
    @Override
    public String toString() {
        if (this == RED_RIGHT) {
            return "RED RIGHT";
        } else if (this == BLUE_RIGHT) {
            return "BLUE RIGHT";
        } else if (this == BLUE_LEFT) {
            return "BLUE LEFT";
        } else if (this == RED_LEFT) {
            return "RED LEFT";
        } else if (this == UNSET) {
            return "UNSET | X - BLUE RIGHT | B - RED RIGHT | A - RED LEFT | Y - BLUE LEFT";
        } else {
            return "";
        }
    }
}
