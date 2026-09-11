package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
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

        if (team == BLUE_SHORT || team == RED_SHORT) {
            Pose2d startPos = new Pose2d(0.0, 0.0, 0);

            Vector2d launchPos = new Vector2d(-65.0, 0.0);

            MecanumDrive drive = new MecanumDrive(hardwareMap, startPos);

            TrajectoryActionBuilder tab1 = drive.actionBuilder(startPos).lineToX(-65.0);

            TrajectoryActionBuilder tab2 = drive.actionBuilder(new Pose2d(launchPos, 0))
                    .strafeTo(new Vector2d(-50.0, team == BLUE_SHORT ? 40.0 : -40.0));

            Flywheels flywheels = new Flywheels(hardwareMap);
            Flappers flappers = new Flappers(hardwareMap);

            Actions.runBlocking(new SequentialAction(
                    tab1.build(),
                    flywheels.spinUpSlower(),
                    sleep(5.0),
                    flappers.turnOn(),
                    sleep(1.0),
                    flappers.turnOff(),
                    sleep(1.0),
                    flappers.turnOn(),
                    sleep(1.0),
                    flappers.turnOff(),
                    sleep(1.0),
                    flappers.turnOn(),
                    sleep(3.0),
                    flappers.turnOff(),
                    flywheels.spinDown(),
                    sleep(2.0),
                    tab2.build()
            ));
        } else if (team == BLUE_LONG || team == RED_LONG) {
            Pose2d startPos = new Pose2d(0, 0, 0);

            Vector2d launchPos = new Vector2d(0.0, 0.0);

            MecanumDrive drive = new MecanumDrive(hardwareMap, startPos);

            TrajectoryActionBuilder tab = drive.actionBuilder(new Pose2d(launchPos, 0))
                    .lineToX(32.0);

            Flywheels flywheels = new Flywheels(hardwareMap);
            Flappers flappers = new Flappers(hardwareMap);

            Actions.runBlocking(new SequentialAction(
                    flywheels.spinUp(),
                    sleep(5.0),
                    flappers.turnOn(),
                    sleep(1.0),
                    flappers.turnOff(),
                    sleep(1.0),
                    flappers.turnOn(),
                    sleep(1.0),
                    flappers.turnOff(),
                    sleep(1.0),
                    flappers.turnOn(),
                    sleep(3.0),
                    flappers.turnOff(),
                    flywheels.spinDown(),
                    sleep(2.0),
                    tab.build()
            ));
        }

    }

    @Override
    public void loop() {
    }

}
