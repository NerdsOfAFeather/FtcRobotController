package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

public class CenterStageIMUTest extends CenterStageConfig {

    @Override
    public void init() {
        initIMU();
        resetYaw();
    }

    @Override
    public void init_loop() {
        YawPitchRollAngles angles = getRawAngles();
        telemetry.addData("Yaw (Z)", "%.2f Deg. (Heading)", angles.getYaw(AngleUnit.DEGREES));
        telemetry.addData("Pitch (X)", "%.2f Deg.", angles.getPitch(AngleUnit.DEGREES));
        telemetry.addData("Roll (Y)", "%.2f Deg.\n", angles.getRoll(AngleUnit.DEGREES));
        telemetry.update();
        sleep(10);
    }

    @Override
    public void start() {
        requestOpModeStop();
    }

    @Override
    public void loop() {

    }

    @Override
    public void stop() {

    }
}
