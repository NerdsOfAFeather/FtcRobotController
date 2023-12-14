package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

/** Created by Gavin for Team 6347 */
@TeleOp(name="CenterStageConfig", group="Linear Opmode")
@Disabled
public class CenterStageConfig extends CenterStageObjectDetection {

    public DcMotorEx leftFrontDrive = null;
    public DcMotorEx leftBackDrive = null;
    public DcMotorEx rightFrontDrive = null;
    public DcMotorEx rightBackDrive = null;
    public DcMotor intakeMotor = null;
    public Servo flipperServo = null;
    public Servo clawServoL = null;
    public Servo clawServoR = null;
    public CRServo extensionServo = null;
    public CRServo retractionServo = null;
    public CenterStageMecanumDrive drive;

    public void initDriveHardware() {

        leftFrontDrive = hardwareMap.get(DcMotorEx.class, "FrontLeftDrive");
        leftBackDrive = hardwareMap.get(DcMotorEx.class, "BackLeftDrive");
        rightFrontDrive = hardwareMap.get(DcMotorEx.class, "FrontRightDrive");
        rightBackDrive = hardwareMap.get(DcMotorEx.class, "BackRightDrive");
        intakeMotor = hardwareMap.get(DcMotorEx.class, "IntakeMotor");
        flipperServo = hardwareMap.get(Servo.class, "FlipperServo");
        clawServoL = hardwareMap.get(Servo.class, "ClawServoL");
        clawServoR = hardwareMap.get(Servo.class, "ClawServoR");
        extensionServo = hardwareMap.get(CRServo.class, "extensionServo");
        retractionServo = hardwareMap.get(CRServo.class, "retractionServo");

        leftFrontDrive.setDirection(Direction.REVERSE);
        leftBackDrive.setDirection(Direction.REVERSE);
        rightFrontDrive.setDirection(Direction.FORWARD);
        rightBackDrive.setDirection(Direction.FORWARD);
        intakeMotor.setDirection(Direction.FORWARD);
        extensionServo.setDirection(Direction.FORWARD);
        retractionServo.setDirection(Direction.FORWARD);

        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

    public void initAuto() {
        drive = new CenterStageMecanumDrive(hardwareMap);
    }

    public void traj(Trajectory trajectory) {
        drive.followTrajectory(trajectory);
    }

    public Trajectory left(double distance) {
        return drive.trajectoryBuilder(new Pose2d()).strafeLeft(distance).build();
    }

    public Trajectory right(double distance) {
        return drive.trajectoryBuilder(new Pose2d()).strafeRight(distance).build();
    }

    public Trajectory forward(double distance) {
        return drive.trajectoryBuilder(new Pose2d()).forward(distance).build();
    }

    public Trajectory back(double distance) {
        return drive.trajectoryBuilder(new Pose2d()).back(distance).build();
    }

}
