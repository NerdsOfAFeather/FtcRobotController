package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

public enum TeamColor {

    RED, BLUE, UNSET;

    @NonNull
    @Override
    public String toString() {
        if (this == RED) {
            return "red";
        } else if (this == BLUE) {
            return "blue";
        } else if (this == UNSET) {
            return "unset";
        } else {
            return "";
        }
    }
}
