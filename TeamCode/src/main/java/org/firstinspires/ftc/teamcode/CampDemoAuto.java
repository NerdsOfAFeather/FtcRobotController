package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/** Created by Gavin for Team 6347*/
@Autonomous(name = "Camp Demo Auto")
public class CampDemoAuto extends PowerPlayConfig {

    @Override
    public void init() {
        initDriveHardware();
        initLift();

        telemetry.addData("Status", "Ready to run");
        telemetry.update();
    }

    @Override
    public void start() {
        telemetry.addData("Status", "Running...");
        telemetry.update();
        driveForward(.5);
        turnLeft(1.5);
        driveForward(3);
        clampClose();
        goToStage(1);
        turnRight(3);
        driveForward(7);
        clampOpen();
    }

    @Override
    public void stop() {

    }
}
