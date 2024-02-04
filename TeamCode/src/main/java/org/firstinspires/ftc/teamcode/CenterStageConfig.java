package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import static com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection.RIGHT;
import static com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection.UP;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

/** Created by Gavin for FTC Team 6347 */
public abstract class CenterStageConfig extends CenterStageObjectDetection {

    public DcMotorEx leftFrontDrive = null;
    public DcMotorEx leftBackDrive = null;
    public DcMotorEx rightFrontDrive = null;
    public DcMotorEx rightBackDrive = null;
    public DcMotor intakeMotor = null;
    public DcMotor intakeMotor2 = null;
    public DcMotor liftMotor = null;
    public Servo clawServoL = null;
    public Servo clawServoR = null;
    public CenterStageMecanumDrive drive;
    IMU imu;

    private static final double TURN_SPEED = 0.5;
    protected static final int ARM_GROUND = 560;
    protected static final int ARM_BACKDROP = 260;


    public void initDriveHardware() {

        leftFrontDrive = hardwareMap.get(DcMotorEx.class, "FrontLeftDrive");
        leftBackDrive = hardwareMap.get(DcMotorEx.class, "BackLeftDrive");
        rightFrontDrive = hardwareMap.get(DcMotorEx.class, "FrontRightDrive");
        rightBackDrive = hardwareMap.get(DcMotorEx.class, "BackRightDrive");
        intakeMotor = hardwareMap.get(DcMotorEx.class, "IntakeMotor");
        intakeMotor2 = hardwareMap.get(DcMotorEx.class, "IntakeMotor2");
        liftMotor = hardwareMap.get(DcMotorEx.class, "LiftMotor");
        clawServoL = hardwareMap.get(Servo.class, "ClawServoL");
        clawServoR = hardwareMap.get(Servo.class, "ClawServoR");

        leftFrontDrive.setDirection(Direction.REVERSE);
        leftBackDrive.setDirection(Direction.REVERSE);
        rightFrontDrive.setDirection(Direction.FORWARD);
        rightBackDrive.setDirection(Direction.FORWARD);
        intakeMotor.setDirection(Direction.FORWARD);
        intakeMotor2.setDirection(Direction.FORWARD);

        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intakeMotor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


    }

    public void initIMU() {
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(RIGHT, UP)));
    }

    public void resetYaw() {
        imu.resetYaw();
    }

    public void turnTo(double angle) {
        double rawYawAngle = getRawAngles().getYaw(AngleUnit.DEGREES);
        double difference = angle - rawYawAngle;

        if (difference > 0) {
            leftFrontDrive.setPower(TURN_SPEED);
            leftBackDrive.setPower(TURN_SPEED);
            rightFrontDrive.setPower(-TURN_SPEED);
            rightBackDrive.setPower(-TURN_SPEED);
            while (getRawAngles().getYaw(AngleUnit.DEGREES) < angle) {
                sleep(10);
            }
            leftFrontDrive.setPower(0);
            leftBackDrive.setPower(0);
            rightFrontDrive.setPower(0);
            rightBackDrive.setPower(0);
        } else if (difference < 0) {
            leftFrontDrive.setPower(-TURN_SPEED);
            leftBackDrive.setPower(-TURN_SPEED);
            rightFrontDrive.setPower(TURN_SPEED);
            rightBackDrive.setPower(TURN_SPEED);
            while (getRawAngles().getYaw(AngleUnit.DEGREES) > angle) {
                sleep(10);
            }
            leftFrontDrive.setPower(0);
            leftBackDrive.setPower(0);
            rightFrontDrive.setPower(0);
            rightBackDrive.setPower(0);
        }
        sleep(250);
    }

    public YawPitchRollAngles getRawAngles() {
        return imu.getRobotYawPitchRollAngles();
    }

    public void initAuto() {
        initDriveHardware();
        initIMU();
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

    public void turnLeft(double seconds) {
        leftFrontDrive.setPower(-1);
        rightFrontDrive.setPower(0);
        leftBackDrive.setPower(-1);
        rightBackDrive.setPower(0);
        sleep((long) (seconds * 1000L));
        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
    }

    public void moveArmToGround() {
        intakeMotor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        intakeMotor2.setPower(0.5);
        intakeMotor2.setTargetPosition(ARM_GROUND);
        while (intakeMotor2.isBusy()) {
            sleep(10);
        }
        intakeMotor2.setPower(0.0);
        intakeMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void moveArmToClosed() {
        intakeMotor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        intakeMotor2.setPower(0.5);
        intakeMotor2.setTargetPosition(0);
        while (intakeMotor2.isBusy()) {
            sleep(10);
        }
        intakeMotor2.setPower(0.0);
        intakeMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void closeClawL() {
        clawServoL.setPosition(0.5);
    }

    public void closeClawR() {
        clawServoR.setPosition(0.5);
    }

    public void openClawL() {
        clawServoL.setPosition(1.0);
    }

    public void openClawR() {
        clawServoL.setPosition(0.0);
    }



}
