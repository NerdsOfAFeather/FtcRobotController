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

import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

/** Created by Gavin for FTC Team 6347 */
public abstract class IntoTheDeepConfig extends IntoTheDeepObjectDetection {

    public DcMotorEx leftFrontDrive = null;
    public DcMotorEx leftBackDrive = null;
    public DcMotorEx rightFrontDrive = null;
    public DcMotorEx rightBackDrive = null;
    public Servo fClawL = null;
    public Servo fClawR = null;
    public Servo fWrist = null;
    public Servo fArmExtension = null;
    public Servo rClawL = null;
    public Servo rClawR = null;
    public DcMotorEx rearArmMotor = null;
    public DcMotorEx rearLiftMotor = null;
    public IntoTheDeepMecanumDrive drive;
    IMU imu;

    ClawState rearClaw = ClawState.CLOSED;
    ClawState frontClaw = ClawState.CLOSED;
    RearLift rearLift = RearLift.IDLE;
    FrontArm frontArm = FrontArm.RETRACTED;

    double rearClawTime = 0;
    double frontClawTime = 0;

    boolean wristInPosition = true;
    boolean rearArmExtended = false;

    // Stores if the robot has a sample in its control
    // TODO: Need to add a distance sensor to the front arm that controls this
    boolean hasSample = false;

    private static final double TURN_SPEED = 0.5;

    public static final int R_ARM_RETRACTED = 0;
    public static final int R_ARM_EXTENDED = 500;

    public void initAttachmentHardware() {
        fClawL = hardwareMap.get(Servo.class, "FrontClawLeft");
        fClawR = hardwareMap.get(Servo.class, "FrontClawRight");
        fWrist = hardwareMap.get(Servo.class, "FrontWrist");
        fArmExtension = hardwareMap.get(Servo.class, "FrontArmExtension");
        rClawL = hardwareMap.get(Servo.class, "RearClawLeft");
        rClawR = hardwareMap.get(Servo.class, "RearClawRight");
        rearArmMotor = hardwareMap.get(DcMotorEx.class, "RearArm");
        rearLiftMotor = hardwareMap.get(DcMotorEx.class, "LiftMotor");

        rearLiftMotor.setDirection(Direction.FORWARD);
        rearArmMotor.setDirection(Direction.FORWARD);

        rearLiftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rearArmMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void initDriveHardware() {

        leftFrontDrive = hardwareMap.get(DcMotorEx.class, "FrontLeftDrive");
        leftBackDrive = hardwareMap.get(DcMotorEx.class, "BackLeftDrive");
        rightFrontDrive = hardwareMap.get(DcMotorEx.class, "FrontRightDrive");
        rightBackDrive = hardwareMap.get(DcMotorEx.class, "BackRightDrive");

        leftFrontDrive.setDirection(Direction.REVERSE);
        leftBackDrive.setDirection(Direction.REVERSE);
        rightFrontDrive.setDirection(Direction.FORWARD);
        rightBackDrive.setDirection(Direction.FORWARD);

        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void initIMU() {
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(RIGHT, UP)));
    }

    public void resetYaw() {
        imu.resetYaw();
    }

    public YawPitchRollAngles getRawAngles() {
        return imu.getRobotYawPitchRollAngles();
    }

    public void initAuto() {
        initDriveHardware();
        initIMU();
        drive = new IntoTheDeepMecanumDrive(hardwareMap);
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

    enum FrontArm {
        EXTENDED(1, 0.0),
        WRIST_DOWN(1, 1.0),
        RETRACTED(0, 0.0)
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
        LOW(1000),
        HIGH(3000)
        ;

        int motorPos;

        RearLift(int motorPos) {
            this.motorPos = motorPos;
        }
    }

    enum ClawState {
        OPEN(1.0, 0.0),
        CLOSED(0.0, 1.0)
        ;

        double lPos;
        double rPos;

        ClawState(double lPos, double rPos) {
            this.lPos = lPos;
            this.rPos = rPos;
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

    static void rtp(DcMotor motor) {
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    /**
     * Houses logic for the rear lift of the robot being allowed to move.
     * TODO: I probably missed some logic elements that need to be added
     * @return whether or not the lift can move in the current robot configuration
     */
    boolean canMoveLift() {
        if (frontArm == FrontArm.RETRACTED) {
            // If we don't have a sample and the front arm is retracted,
            // there's no way to break something by moving
            if (!hasSample) return true;

            if (frontClaw == ClawState.OPEN) return true;
        }
        return false;
    }
}
