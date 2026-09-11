package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;
import static org.firstinspires.ftc.teamcode.TeamColor.*;

/**Created by Gavin for FTC Team 6347 */
@Autonomous(name = "DecodeAuto", group = "Autonomous", preselectTeleOp = "DecodeTeleOp")
public class DecodeAuto extends DecodeConfig {

    private final ElapsedTime runtime = new ElapsedTime();

    Pose2d initialPos = new Pose2d(0, 0, 0);
    MecanumDrive mecanumDrive;
    TeamColor team = BLUE_SHORT;

    @Override
    public void init() {
        telemetry.addData("Status", "Initializing...");
        telemetry.update();

        mecanumDrive = new MecanumDrive(hardwareMap, initialPos);

        initAuto();

        //startAndEnableRobotVision();

        telemetry.addData("Status", "Ready to Run");
        telemetry.update();
    }

    @Override
    public void init_loop() {
        if (gamepad1.x) {
            team = BLUE_SHORT; // Near Goal
        } else if (gamepad1.b) {
            team = RED_SHORT; // Near Goal
        } else if (gamepad1.a) {
            team = RED_LONG; // Front Wall
        } else if (gamepad1.y) {
            team = BLUE_LONG; // Front Wall
        }
        telemetry.addData("Status", "Ready to Run");
        telemetry.addData("Team", team.toString());
        telemetry.update();
    }

    @Override
    public void start() {
        runtime.reset();
    }

    @Override
    public void loop() {
        rightFrontDrive.setPower(-0.5);
        leftFrontDrive.setPower(-0.5);
        rightBackDrive.setPower(-0.5);
        leftBackDrive.setPower(-0.5);
    }

    @Override
    public void stop() {}
}
