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

        fClawL.setPosition(ClawState.OPEN.flPos);
        fClawR.setPosition(ClawState.OPEN.frPos);
        rClawL.setPosition(ClawState.CLOSED.blPos);
        rClawR.setPosition(ClawState.CLOSED.brPos);
        fWrist.setPosition(FrontArm.RETRACTED.wristPos);

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
        fClawL.setPosition(ClawState.OPEN.flPos);
        fClawR.setPosition(ClawState.OPEN.frPos);
        rClawL.setPosition(ClawState.CLOSED.blPos);
        rClawR.setPosition(ClawState.CLOSED.brPos);
        fWrist.setPosition(FrontArm.WRIST_DOWN.wristPos);

        if (team == RED_RIGHT || team == BLUE_RIGHT) {
            Pose2d startPose = new Pose2d(15.0, -60.0, Math.toRadians(90.0));
            drive.setPoseEstimate(startPose);
            Trajectory builder1 = newTraj(startPose).lineTo(pt(0.0, -40.0)).build();

            Trajectory builder2 = newTraj(new Pose2d()).lineTo(pt(-10.0, 0.0)).build();

            Trajectory builder3 = newTraj(builder2.end()).lineTo(pt(15.0, 55.0)).build();

            follow(builder1);
            turnRight(150);
            follow(builder2);
            rearLiftMotor.setTargetPosition(RearLift.LOW.motorPos);
            rearLiftMotor.setPower(1.0);
            rtp(rearLiftMotor);
            while (rearLiftMotor.getCurrentPosition() <= rearLiftMotor.getTargetPosition()) {
                if (rearLiftMotor.getCurrentPosition() >= RearLift.LOW.motorPos*.75) {
                    rearArmServo.setPosition(RearArm.DEPOSIT_HIGH_SPEC.elbowPos);
                    rearWrist.setPosition(RearArm.DEPOSIT_SAMPLE.wristPos);
                }
                sleep(10);
            }
            sleep(750);
            rearLiftMotor.setTargetPosition(RearLift.IDLE.motorPos);
            rearLiftMotor.setPower(1.0);
            rtp(rearLiftMotor);
            while (rearLiftMotor.getCurrentPosition() >= rearLiftMotor.getTargetPosition()) {
                if (rearLiftMotor.getCurrentPosition() <= RearLift.LOW.motorPos*.25) {
                    rClawL.setPosition(ClawState.OPEN.blPos);
                    rClawR.setPosition(ClawState.OPEN.brPos);
                }
                sleep(10);
            }
            fWrist.setPosition(FrontArm.RETRACTED.wristPos);
            follow(builder3);
        } else if (team == RED_LEFT || team == BLUE_LEFT) {
            Pose2d startPose = new Pose2d(-45.0, -65.0, Math.toRadians(90.0));
            drive.setPoseEstimate(startPose);
            Trajectory builder1 = newTraj(startPose).lineTo(pt(-35.0, -55.0)).build();

            Trajectory builder2 = newTraj(builder1.end()).lineTo(pt(-35.0, -40.0)).build();

            follow(builder1);
            turnLeft(90);
            follow(builder2);
            turnRight(135);
            rearLiftMotor.setTargetPosition(RearLift.HIGH.motorPos);
            rearLiftMotor.setPower(1.0);
            rtp(rearLiftMotor);
            while (rearLiftMotor.getCurrentPosition() <= rearLiftMotor.getTargetPosition()){
                if (rearLiftMotor.getCurrentPosition() >= RearLift.HIGH.motorPos*.75) {
                    rearArmServo.setPosition(RearArm.DEPOSIT_SAMPLE.elbowPos);
                    rearWrist.setPosition(RearArm.DEPOSIT_SAMPLE.wristPos);
                }
                sleep(10);
            }
            rClawL.setPosition(ClawState.OPEN.blPos);
            rClawR.setPosition(ClawState.OPEN.brPos);
            sleep(500);
            rearArmServo.setPosition(RearArm.IN_ROBOT.elbowPos);
            rearWrist.setPosition(RearArm.IN_ROBOT.wristPos);
            sleep(500);
            rClawL.setPosition(ClawState.CLOSED.blPos);
            rClawR.setPosition(ClawState.CLOSED.brPos);
            rearLiftMotor.setTargetPosition(RearLift.IDLE.motorPos);
            rearLiftMotor.setPower(1.0);
            rtp(rearLiftMotor);
            while (rearLiftMotor.isBusy()) {
                sleep(10);
            }
            fWrist.setPosition(FrontArm.RETRACTED.wristPos);
        } else {
            Pose2d startPose = new Pose2d(15.0, -60.0, Math.toRadians(90.0));
            drive.setPoseEstimate(startPose);
            Trajectory builder1 = newTraj(startPose).lineTo(pt(5.0, -45.0)).build();

            Trajectory builder2 = newTraj(builder1.end()).lineTo(pt(5.0, -40.0)).build();

            follow(builder1);
            turnRight(180);
            follow(builder2);
        }
    }

    @Override
    public void loop() {}

    @Override
    public void stop() {
        stopped = true;
    }
}
