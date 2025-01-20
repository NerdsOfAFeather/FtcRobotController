package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.TeamColor.*;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

/**Created by Gavin for FTC Team 6347 */
@Autonomous(name = "IntoTheDeepAuto", group = "Autonomous", preselectTeleOp = "IntoTheDeepTeleOp2p")
public class IntoTheDeepAuto extends IntoTheDeepConfig {
    static int delay = 0;
    static boolean stopped = false;
    private ElapsedTime runtime = new ElapsedTime();



    @Override
    public void init() {
        telemetry.addData("Status", "Initializing...");
        telemetry.update();

        initAuto();

        fWrist.setPosition(1.0);
        fClawL.setPosition(ClawState.CLOSED.flPos);
        fClawR.setPosition(ClawState.CLOSED.frPos);

        //startAndEnableRobotVision();

        telemetry.addData("Status", "Ready to Run");
        telemetry.update();
    }

    @Override
    public void init_loop() {
        if (gamepad1.x) {
            team = BLUE_RIGHT;
        } else if (gamepad1.b) {
            team = RED_RIGHT;
        } else if (gamepad1.a) {
            team = RED_LEFT;
        } else if (gamepad1.y) {
            team = BLUE_LEFT;
        }
        telemetry.addData("Team", team.toString());
        telemetry.update();

    }

    @Override
    public void start() {
        runtime.reset();
        resetYaw();
    }

    @Override
    public void loop() {
        fClawL.setPosition(ClawState.OPEN.flPos);
        fClawR.setPosition(ClawState.OPEN.frPos);
        rClawL.setPosition(ClawState.CLOSED.blPos);
        fClawR.setPosition(ClawState.CLOSED.brPos);
        fWrist.setPosition(FrontArm.WRIST_DOWN.wristPos);

        if (team == RED_RIGHT) {
            Pose2d startPose = new Pose2d(15.0, -65.0, 90.0);
            Trajectory moveToDepositPreload = newTraj(startPose)
                    .splineToLinearHeading(new Pose2d(-10.0, -35.0, Math.toRadians(270.0)), 0.0)
                    .build();

            Trajectory pushGroundSamples = newTraj(moveToDepositPreload.end())
                    .splineToLinearHeading(new Pose2d(38.0, -20.0, Math.toRadians(270.0)), Math.toRadians(90.0))
                    .splineToLinearHeading(new Pose2d(45.0, -10.0, Math.toRadians(270.0)), Math.toRadians(270.0))
                    .splineToLinearHeading(new Pose2d(45.0, -55.0, Math.toRadians(270.0)), Math.toRadians(90.0))
                    .splineToLinearHeading(new Pose2d(50.0, -10.0, Math.toRadians(270.0)), Math.toRadians(0.0))
                    .splineToLinearHeading(new Pose2d(57.0, -10.0, Math.toRadians(270.0)), Math.toRadians(270.0))
                    .splineToLinearHeading(new Pose2d(57.0, -55.0, Math.toRadians(270.0)), Math.toRadians(90.0))
                    .splineToSplineHeading(new Pose2d(37.0, -57.0, Math.toRadians(90.0)), Math.toRadians(270.0))
                    .build();

            Trajectory moveToDepositSpec2 = newTraj(pushGroundSamples.end())
                    .splineToSplineHeading(new Pose2d(20.0, -40.0, Math.toRadians(270.0)), Math.toRadians(180.0))
                    .splineToLinearHeading(new Pose2d(-5.0, -35.0, Math.toRadians(270.0)), Math.toRadians(0.0))
                    .build();

            Trajectory moveToPickupSpec3 = newTraj(moveToDepositSpec2.end())
                    .splineToLinearHeading(new Pose2d(37.0, -57.0, Math.toRadians(90.0)), Math.toRadians(270.0))
                    .build();

            Trajectory moveToDepositSpec3 = newTraj(moveToPickupSpec3.end())
                    .splineToSplineHeading(new Pose2d(20.0, -40.0, Math.toRadians(270.0)), Math.toRadians(180.0))
                    .splineToLinearHeading(new Pose2d(0.0, -35.0, Math.toRadians(270.0)), Math.toRadians(0.0))
                    .build();

            Trajectory moveToPark = newTraj(moveToDepositSpec3.end())
                    .splineToLinearHeading(new Pose2d(60.0, -57.0, Math.toRadians(90.0)), Math.toRadians(270.0))
                    .build();

            follow(moveToDepositPreload);
            depositSpec();
            follow(pushGroundSamples);
            rClawL.setPosition(ClawState.CLOSED.blPos);
            rClawR.setPosition(ClawState.CLOSED.brPos);
            sleep(500);
            rearArmServo.setPosition(1.0);
            rearArmServo.setPosition(0.45);
            follow(moveToDepositSpec2);
            depositSpec();
            follow(moveToPickupSpec3);
            rClawL.setPosition(ClawState.CLOSED.blPos);
            rClawR.setPosition(ClawState.CLOSED.brPos);
            sleep(500);
            rearArmServo.setPosition(1.0);
            rearArmServo.setPosition(0.45);
            follow(moveToDepositSpec3);
            depositSpec();
            follow(moveToPark);
        }

        requestOpModeStop();
    }

    void depositSpec() {
        rearLiftMotor.setTargetPosition(RearLift.LOW.motorPos);
        rtp(rearLiftMotor);
        rearLiftMotor.setPower(1.0);
        while (rearLiftMotor.isBusy()) {
            sleep(10);
        }
        rearLiftMotor.setPower(0.0);
        rearArmServo.setPosition(0.3);
        rearWrist.setPosition(0.7);
        sleep(500);
        rearLiftMotor.setTargetPosition(RearLift.IDLE.motorPos);
        rtp(rearLiftMotor);
        rearLiftMotor.setPower(1.0);
        while (rearLiftMotor.isBusy()) {
            sleep(10);
            if (rearLiftMotor.getCurrentPosition() >= -1600) {
                rClawL.setPosition(ClawState.OPEN.blPos);
                rClawR.setPosition(ClawState.OPEN.brPos);
            }
        }
        rearLiftMotor.setPower(0.0);
        rearArmServo.setPosition(0.0);
        rearWrist.setPosition(0.3);
    }

    @Override
    public void stop() {
        stopped = true;
    }
}
