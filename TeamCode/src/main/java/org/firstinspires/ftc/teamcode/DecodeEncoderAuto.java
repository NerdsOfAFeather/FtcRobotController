package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.TeamColor.BLUE_LONG;
import static org.firstinspires.ftc.teamcode.TeamColor.BLUE_SHORT;
import static org.firstinspires.ftc.teamcode.TeamColor.RED_LONG;
import static org.firstinspires.ftc.teamcode.TeamColor.RED_SHORT;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;

/**Created by Gavin for FTC Team 6347 */
@Autonomous(name = "DecodeEncoderAuto", group = "Autonomous", preselectTeleOp = "DecodeTeleOp")
@Disabled
public class DecodeEncoderAuto extends DecodeConfig {

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

        releaseServo.setPosition(0.5);

        if (team == BLUE_SHORT || team == RED_SHORT) {
            Pose2d startPos = new Pose2d(0.0, 0.0, 0);

            Vector2d launchPos = new Vector2d(-20.0, 0.0);

            MecanumDrive drive = new MecanumDrive(hardwareMap, startPos);

            TrajectoryActionBuilder tab1 = drive.actionBuilder(startPos).lineToX(-20.0);

            TrajectoryActionBuilder tab2 = drive.actionBuilder(new Pose2d(launchPos, 0))
                    .lineToX(-10.0)
                    .strafeTo(new Vector2d(-10.0, team == BLUE_SHORT ? -20.0 : 20.0));

            Flywheels flywheels = new Flywheels(hardwareMap);
            Storage flappers = new Storage(hardwareMap);
            Intake intake = new Intake(hardwareMap);

            Actions.runBlocking(new SequentialAction(
                    tab1.build(),
                    intake.turnOn(),
                    flywheels.spinUpSlower(),
                    sleep(4.0),
                    flappers.turnOn(),
                    sleep(0.8),
                    flappers.turnOff(),
                    sleep(0.3),
                    flappers.turnOn(),
                    sleep(0.8),
                    flappers.turnOff(),
                    sleep(0.3),
                    flappers.turnOn(),
                    sleep(0.8),
                    flappers.turnOff(),
                    flywheels.spinDown(),
                    intake.turnOff(),
                    tab2.build()
            ));
        } else if (team == BLUE_LONG || team == RED_LONG) {
            Pose2d startPos = new Pose2d(0, 0, 0);

            Vector2d launchPos = new Vector2d(0.0, 0.0);

            MecanumDrive drive = new MecanumDrive(hardwareMap, startPos);

            TrajectoryActionBuilder tab = drive.actionBuilder(new Pose2d(launchPos, 0))
                    .lineToX(10.0);

            Flywheels flywheels = new Flywheels(hardwareMap);
            Storage flappers = new Storage(hardwareMap);

            Actions.runBlocking(new SequentialAction(
                    flywheels.spinUp(),
                    sleep(3.0),
                    flappers.turnOn(),
                    sleep(0.8),
                    flappers.turnOff(),
                    sleep(0.3),
                    flappers.turnOn(),
                    sleep(0.8),
                    flappers.turnOff(),
                    sleep(0.3),
                    flappers.turnOn(),
                    sleep(2.0),
                    flappers.turnOff(),
                    flywheels.spinDown(),
                    tab.build()
            ));
        }

    }

    @Override
    public void loop() {
    }

}
