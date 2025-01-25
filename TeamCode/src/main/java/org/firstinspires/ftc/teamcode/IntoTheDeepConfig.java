package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import static com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection.DOWN;
import static com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection.LEFT;

import androidx.annotation.Nullable;

import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

/** Created by Gavin for FTC Team 6347 */
@Config
public abstract class IntoTheDeepConfig extends IntoTheDeepObjectDetection {

    public DcMotorEx leftFrontDrive = null;
    public DcMotorEx leftBackDrive = null;
    public DcMotorEx rightFrontDrive = null;
    public DcMotorEx rightBackDrive = null;
    public Servo fClawL = null;
    public Servo fClawR = null;
    public Servo fWrist = null;
    public DcMotorEx fArmMotor = null;
    public Servo rClawL = null;
    public Servo rClawR = null;
    public Servo rearArmServo = null;
    public Servo rearWrist = null;
    public DcMotorEx rearLiftMotor = null;
    public IntoTheDeepMecanumDrive drive;
    IMU imu;

    ClawState rearClaw = ClawState.CLOSED;
    ClawState frontClaw = ClawState.CLOSED;
    FrontArm frontArm = FrontArm.RETRACTED;
    RearArm rearArm = RearArm.IN_ROBOT;

    double rearClawTime = 0;
    double frontClawTime = 0;
    double frontWristTime = 0;

    double rWristPos = 0.5;
    double rearArmServoPos = 1.0;

    public void initAttachmentHardware() {
        fArmMotor = hardwareMap.get(DcMotorEx.class, "FrontArmMotor");
        fClawL = hardwareMap.get(Servo.class, "fClawL");
        fClawR = hardwareMap.get(Servo.class, "fClawR");
        fWrist = hardwareMap.get(Servo.class, "FrontWrist");
        rClawL = hardwareMap.get(Servo.class, "rClawL");
        rClawR = hardwareMap.get(Servo.class, "rClawR");
        rearWrist = hardwareMap.get(Servo.class, "RearWrist");
        rearArmServo = hardwareMap.get(Servo.class, "RearArm");
        rearLiftMotor = hardwareMap.get(DcMotorEx.class, "LiftMotor");

        rearLiftMotor.setDirection(Direction.FORWARD);
        fArmMotor.setDirection(Direction.FORWARD);

        rearLiftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rearLiftMotor.setMotorDisable();

        fArmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rearLiftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        fArmMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rearLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void initDriveHardware() {

        leftFrontDrive = hardwareMap.get(DcMotorEx.class, "FrontLeftDrive");
        leftBackDrive = hardwareMap.get(DcMotorEx.class, "BackLeftDrive");
        rightFrontDrive = hardwareMap.get(DcMotorEx.class, "FrontRightDrive");
        rightBackDrive = hardwareMap.get(DcMotorEx.class, "BackRightDrive");

        leftFrontDrive.setDirection(Direction.FORWARD);
        leftBackDrive.setDirection(Direction.FORWARD);
        rightFrontDrive.setDirection(Direction.REVERSE);
        rightBackDrive.setDirection(Direction.REVERSE);

        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void initIMU() {
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(DOWN, LEFT)));
    }

    public void resetYaw() {
        imu.resetYaw();
    }

    public YawPitchRollAngles getRawAngles() {
        return imu.getRobotYawPitchRollAngles();
    }

    public void initAuto() {
        initDriveHardware();
        initAttachmentHardware();
        initIMU();
        initEOCV();
        drive = new IntoTheDeepMecanumDrive(hardwareMap);
    }

    enum FrontArm {
        EXTENDED(1, 0.9),
        EXTENDED_DOWN(1, 0.2),
        WRIST_DOWN(0, 0.2),
        RETRACTED(0, 0.9)
        ;

        final int extensionPos; // 1: Extended; 0: Retracted
        final double wristPos;  // 0.0: Up;     1.0: Down

        FrontArm(int extensionPos, double wristPos) {
            this.extensionPos = extensionPos;
            this.wristPos = wristPos;
        }
    }

    enum RearLift {
        IDLE(0),
        LOW(2180),
        HIGH(4800)
        ;

        final int motorPos;

        RearLift(int motorPos) {
            this.motorPos = motorPos;
        }
    }

    enum ClawState {
        //      Front Left,Front Right,Back Left,Back Right
        OPEN   (0.6, 0.7, 0.8, 0.2),
        CLOSED (1.0, 0.3, 0.2, 0.8)
        ;

        final double flPos;
        final double frPos;
        final double blPos;
        final double brPos;

        static final double ADAPT_OFFSET = 0.1;

        ClawState(double flPos, double frPos, double blPos, double brPos) {
            this.flPos = flPos;
            this.frPos = frPos;
            this.blPos = blPos;
            this.brPos = brPos;
        }
    }

    static ClawState toggle(ClawState state) {
        if (state == ClawState.OPEN) {
            state = ClawState.CLOSED;
        } else if (state == ClawState.CLOSED) {
            state = ClawState.OPEN;
        }
        return state;
    }

    enum RearArm {
        IN_ROBOT(1.0, 0.45, RearLift.IDLE),
        DEPOSIT_SAMPLE(0.4, 0.6, RearLift.HIGH),
        DEPOSIT_LOW_SPEC(0.3, 0.9, RearLift.IDLE),
        DEPOSIT_HIGH_SPEC(0.3, 0.6, RearLift.LOW),
        PICKUP_SPEC(0.0, 0.3, RearLift.IDLE)
        ;

        final double elbowPos;
        final double wristPos;
        final RearLift liftHeight;


        RearArm(double elbowPos, double wristPos, RearLift liftHeight) {
            this.elbowPos = elbowPos;
            this.wristPos = wristPos;
            this.liftHeight = liftHeight;
        }
    }

    static void rtp(DcMotor motor) {
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    static void rue(DcMotor motor) {
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void follow(Trajectory trajectory) {
        drive.followTrajectory(trajectory);
    }

    public TrajectoryBuilder newTraj() {
        return newTraj(new Pose2d());
    }

    public TrajectoryBuilder newTraj(Pose2d startPos) {
        return drive.trajectoryBuilder(startPos);
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

    public void turnLeft(int deg) {
        drive.turn(Math.toRadians(deg));
    }

    public void turnRight(int deg) {
        drive.turn(-Math.toRadians(deg));
    }

    static Vector2d pt(int x, int y) {
        return new Vector2d(x, y);
    }

    static double rad(double deg) {
        return Math.toRadians(deg);
    }
}
