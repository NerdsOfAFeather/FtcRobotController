package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/** Created by Gavin for Team 6347 */
@TeleOp(name = "IMU Telemetry")

public class IMUTelemetry extends NewPowerPlayConfig{

    @Override
    public void runOpMode() {
        initAuto();
        waitForStart();
        while (opModeIsActive()) {
            telemetry.addData("IMU Heading", getRawHeading());
            telemetry.addData("Steering Correction", getSteeringCorrection(0, P_DRIVE_GAIN));
            telemetry.update();
        }
    }
}
