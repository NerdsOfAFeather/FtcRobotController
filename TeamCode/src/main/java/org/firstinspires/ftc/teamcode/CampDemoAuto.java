package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/** Created by Gavin for Team 6347*/
@Autonomous(name = "Camp Demo Auto")
public class CampDemoAuto extends PowerPlayConfig {

    @Override
    public void init() {
        initAuto();

        telemetry.addData("Status", "Ready to run");
        telemetry.update();
    }

    @Override
    public void start() {
        telemetry.addData("Status", "Running...");
        telemetry.update();
        goToStage(1);
        driveForward(.5);
        turnLeftDegrees(90);
        driveForward(4);
        goToStage(0);
        clampClose();
        goToStage(1);
        turnRightDegrees(180);
        driveForward(7);
        clampOpen();
    }
}
