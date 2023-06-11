package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

import java.util.List;

/** Created by Gavin for Team 6347*/
@TeleOp(name="PowerPlayConfig", group="Linear Opmode")
@Disabled
public class PowerPlayConfig extends PowerPlayObjectDetection {

    public DcMotor leftFrontDrive = null;
    public DcMotor leftBackDrive = null;
    public DcMotor rightFrontDrive = null;
    public DcMotor rightBackDrive = null;
    public DcMotor liftLiftMotor = null;
    public Servo rightClawServo = null;
    public Servo leftClawServo = null;
    public ColorSensor color;
    public IMU imu;

    public static int desiredLiftPosition = -2;
    public static boolean liftMoving = false;

    static final int lvl0 = 0;
    static final int lvl1 = -2500;
    static final int lvl2 = -4250;
    static final int lvl3 = -5850;

    static final double AUTO_SPEED = 0.6;

    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // No External Gearing.
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);

    RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.RIGHT;
    RevHubOrientationOnRobot.UsbFacingDirection  usbDirection  = RevHubOrientationOnRobot.UsbFacingDirection.UP;


    RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);

    public void initDriveHardware() {

        leftFrontDrive = hardwareMap.get(DcMotor.class, "FrontLeftDrive");
        leftBackDrive = hardwareMap.get(DcMotor.class, "BackLeftDrive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "FrontRightDrive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "BackRightDrive");
        liftLiftMotor = hardwareMap.get(DcMotor.class, "LiftLiftMotor");
        rightClawServo = hardwareMap.get(Servo.class, "RightClawServo");
        leftClawServo = hardwareMap.get(Servo.class, "LeftClawServo");
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

        liftLiftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    }

    public void initLift(){
        liftLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftLiftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftLiftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void initAuto() {
        initDriveHardware();
        initLift();
        initTfod();
        initVuforia();
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void ftcwait(long milis) {
        ElapsedTime waittime = new ElapsedTime();

        while (waittime.milliseconds() <= milis){
            telemetry.addData("Waiting for", milis + " ms");
            telemetry.update();
        }
    }

    public void driveForward(double seconds) {
        leftFrontDrive.setPower(AUTO_SPEED);
        leftBackDrive.setPower(AUTO_SPEED);
        rightFrontDrive.setPower(AUTO_SPEED);
        rightBackDrive.setPower(AUTO_SPEED);
        ftcwait((long) (seconds * 1000));
        leftFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightFrontDrive.setPower(0);
        rightBackDrive.setPower(0);
        ftcwait(250);
    }

    public void driveBackwards(double seconds) {
        leftFrontDrive.setPower(-AUTO_SPEED);
        leftBackDrive.setPower(-AUTO_SPEED);
        rightFrontDrive.setPower(-AUTO_SPEED);
        rightBackDrive.setPower(-AUTO_SPEED);
        ftcwait((long) (seconds * 1000));
        leftFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightFrontDrive.setPower(0);
        rightBackDrive.setPower(0);
        ftcwait(250);
    }

    public void driveRight(double seconds) {
        leftFrontDrive.setPower(AUTO_SPEED);
        leftBackDrive.setPower(-AUTO_SPEED);
        rightFrontDrive.setPower(-AUTO_SPEED);
        rightBackDrive.setPower(AUTO_SPEED);
        ftcwait((long) (seconds * 1000));
        leftFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightFrontDrive.setPower(0);
        rightBackDrive.setPower(0);
        ftcwait(250);
    }

    public void driveLeft(double seconds) {
        leftFrontDrive.setPower(-AUTO_SPEED);
        leftBackDrive.setPower(AUTO_SPEED);
        rightFrontDrive.setPower(AUTO_SPEED);
        rightBackDrive.setPower(-AUTO_SPEED);
        ftcwait((long) (seconds * 1000));
        leftFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightFrontDrive.setPower(0);
        rightBackDrive.setPower(0);
        ftcwait(250);
    }

    public void turnRight(double seconds) {
        leftFrontDrive.setPower(AUTO_SPEED);
        leftBackDrive.setPower(AUTO_SPEED);
        rightFrontDrive.setPower(-AUTO_SPEED);
        rightBackDrive.setPower(-AUTO_SPEED);
        ftcwait((long) (seconds * 1000));
        leftFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightFrontDrive.setPower(0);
        rightBackDrive.setPower(0);
        ftcwait(250);
    }

    public void turnLeft(double seconds) {
        leftFrontDrive.setPower(-AUTO_SPEED);
        leftBackDrive.setPower(-AUTO_SPEED);
        rightFrontDrive.setPower(AUTO_SPEED);
        rightBackDrive.setPower(AUTO_SPEED);
        ftcwait((long) (seconds * 1000));
        leftFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightFrontDrive.setPower(0);
        rightBackDrive.setPower(0);
        ftcwait(250);
    }

    public void liftUp(double seconds){
        liftLiftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        liftLiftMotor.setPower(-0.5);
        ftcwait((long) (seconds * 1000));
        liftLiftMotor.setPower(0);
        ftcwait(250);
        liftLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void liftDown(double seconds){
        if (seconds != 0) {
            liftLiftMotor.setPower(0.5);
            ftcwait((long) (seconds * 1000));
            liftLiftMotor.setPower(0);
            ftcwait(250);
        }
    }

    public void clampClose(){
        leftClawServo.setPosition(0.0);
        rightClawServo.setPosition(1.0);
        ftcwait(1500);
    }

    public void clampOpen(){
        leftClawServo.setPosition(1.0);
        rightClawServo.setPosition(0.0);
        ftcwait(500);
    }

    public void driveForwardSlow(double seconds){
        leftFrontDrive.setPower(AUTO_SPEED/2);
        leftBackDrive.setPower(AUTO_SPEED/2);
        rightFrontDrive.setPower(AUTO_SPEED/2);
        rightBackDrive.setPower(AUTO_SPEED/2);
        ftcwait((long) (seconds * 1000));
        leftFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightFrontDrive.setPower(0);
        rightBackDrive.setPower(0);
        ftcwait(250);
    }

    public void driveBackwardSlow(double seconds){
        leftFrontDrive.setPower(-AUTO_SPEED/2);
        leftBackDrive.setPower(-AUTO_SPEED/2);
        rightFrontDrive.setPower(-AUTO_SPEED/2);
        rightBackDrive.setPower(-AUTO_SPEED/2);
        ftcwait((long) (seconds * 1000));
        leftFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightFrontDrive.setPower(0);
        rightBackDrive.setPower(0);
        ftcwait(250);
    }

    public int getDesiredLocation() {
        if (tfod != null) {
            // getUpdatedRecognitions() will return null if no new information is available since
            // the last time that call was made.
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            if (updatedRecognitions.size() == 1) {
                if (updatedRecognitions.get(0).getLabel().equals("one")){
                    return 1;
                } else if (updatedRecognitions.get(0).getLabel().equals("two")){
                    return 3; //I screwed up (Dataset mislabeled)
                } else {
                    return 2;
                }
            }
        }
        return 0;
    }

    public void goToStage(int stage) {
        if (stage == 0) {
            liftLiftMotor.setTargetPosition(lvl0);

            liftLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            liftLiftMotor.setPower(1);

            while (liftLiftMotor.isBusy()) {
                telemetry.addData("Running to Stage", 0);
                telemetry.update();
            }

            liftLiftMotor.setPower(0);
            liftLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            ftcwait(250);   // optional pause after each move.
        } else if (stage == 1) {

                liftLiftMotor.setTargetPosition(lvl1);

                liftLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                liftLiftMotor.setPower(Math.abs(1));

                while (liftLiftMotor.isBusy()) {
                    telemetry.addData("Running to Stage", 1);
                    telemetry.update();
                }

                liftLiftMotor.setPower(0);
                liftLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                ftcwait(250);   // optional pause after each move.
        } else if (stage == 2){

                liftLiftMotor.setTargetPosition(lvl2);

                liftLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                liftLiftMotor.setPower(Math.abs(1));

                while (liftLiftMotor.isBusy()) {
                    telemetry.addData("Running to Stage", 2);
                    telemetry.update();
                }

                liftLiftMotor.setPower(0);
                liftLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                ftcwait(250);   // optional pause after each move.

        } else if (stage == 3){

                liftLiftMotor.setTargetPosition(lvl3);

                liftLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                liftLiftMotor.setPower(Math.abs(1));

                while (liftLiftMotor.isBusy()) {
                    telemetry.addData("Running to Stage", 3);
                    telemetry.update();
                }

                liftLiftMotor.setPower(0);
                liftLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                ftcwait(250);   // optional pause after each move.
        } else {
            telemetry.addData("Error", "Stage not programmed (yet)");
            telemetry.update();
        }
    }

    public int desiredLift(){
        if(desiredLiftPosition==0){
            return lvl0;
        }else if(desiredLiftPosition==1){
            return lvl1;
        }else if(desiredLiftPosition==2){
            return lvl2;
        }else if(desiredLiftPosition==3){
            return lvl3;
        }else{
            return liftLiftMotor.getCurrentPosition();
        }
    }

    public double getRawHeading() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

}