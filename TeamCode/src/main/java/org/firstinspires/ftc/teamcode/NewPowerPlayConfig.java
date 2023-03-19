package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

import java.util.List;

/** Created by Gavin for Team 6347*/
@TeleOp(name="NewPowerPlayConfig", group="Linear Opmode")

public class NewPowerPlayConfig extends PowerPlayObjectDetection {

    public DcMotor leftFrontDrive = null;
    public DcMotor leftBackDrive = null;
    public DcMotor rightFrontDrive = null;
    public DcMotor rightBackDrive = null;
    public DcMotor liftLiftMotor = null;
    public Servo rightClawServo = null;
    public Servo leftClawServo = null;
    public ColorSensor color;
    public BNO055IMU imu;

    public static int desiredLiftPosition = -2;

    static final int lvl0 = 0;
    static final int lvl1 = -2500;
    static final int lvl2 = -4250;
    static final int lvl3 = -5850;

    static final double AUTO_SPEED = 0.1;

    static final double     COUNTS_PER_MOTOR_REV    = 960 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // No External Gearing.
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     LIFT_COUNTS_PER_INCH    = 165 ;     // For lift motor with a 4:1, 3:1, 3:1 gearbox

    private double          robotHeading  = 0;
    private double          headingOffset = 0;
    private double          headingError  = 0;

    private double  targetHeading = 0;
    private double  turnSpeed     = 0;
    private int     leftFrontTarget    = 0;
    private int     rightFrontTarget   = 0;
    private int     leftBackTarget    = 0;
    private int     rightBackTarget   = 0;

    static final double     TURN_SPEED              = 0.2;     // Max Turn speed to limit turn rate
    static final double     HEADING_THRESHOLD       = 1.0;     // How close must the heading get to the target before moving to next step.
    static final double     P_TURN_GAIN            = 0.02;     // Larger is more responsive, but also less stable
    static final double     P_DRIVE_GAIN           = 0.0;

    RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.RIGHT;
    RevHubOrientationOnRobot.UsbFacingDirection  usbDirection  = RevHubOrientationOnRobot.UsbFacingDirection.UP;

    RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);

    public void initDriveHardware() {

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        leftFrontDrive = hardwareMap.get(DcMotor.class, "FrontLeftDrive");
        leftBackDrive = hardwareMap.get(DcMotor.class, "BackLeftDrive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "FrontRightDrive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "BackRightDrive");
        liftLiftMotor = hardwareMap.get(DcMotor.class, "LiftLiftMotor");
        rightClawServo = hardwareMap.get(Servo.class, "RightClawServo");
        leftClawServo = hardwareMap.get(Servo.class, "LeftClawServo");
        //color = hardwareMap.get(ColorSensor.class, "Color");
        imu = hardwareMap.get(BNO055IMU.class, "imu");

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit            = BNO055IMU.AngleUnit.DEGREES;
        imu.initialize(parameters);

        // Most robots need the motors on one side to be reversed to drive forward.
        // When you first test your robot, push the left joystick forward
        // and flip the direction ( FORWARD <-> REVERSE ) of any wheel that runs backwards
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

        liftLiftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        liftLiftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void initLift(){
        liftLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftLiftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void initAuto(){
        initDriveHardware();
        initLift();
        initVuforia();
        initTfod();
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void driveForward(double distance) {

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            int moveCounts = (int)(distance * COUNTS_PER_INCH);
            leftFrontTarget = leftFrontDrive.getCurrentPosition() + moveCounts;
            rightFrontTarget = rightFrontDrive.getCurrentPosition() + moveCounts;
            leftBackTarget = leftBackDrive.getCurrentPosition() + moveCounts;
            rightBackTarget = rightBackDrive.getCurrentPosition() + moveCounts;

            // Set Target FIRST, then turn on RUN_TO_POSITION
            leftFrontDrive.setTargetPosition(leftFrontTarget);
            rightFrontDrive.setTargetPosition(rightFrontTarget);
            leftBackDrive.setTargetPosition(leftBackTarget);
            rightBackDrive.setTargetPosition(rightBackTarget);

            leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // Set the required driving speed  (must be positive for RUN_TO_POSITION)
            // Start driving straight, and then enter the control loop
            leftFrontDrive.setPower(AUTO_SPEED);
            rightFrontDrive.setPower(AUTO_SPEED);
            leftBackDrive.setPower(AUTO_SPEED);
            rightBackDrive.setPower(AUTO_SPEED);

            // keep looping while we are still active, and BOTH motors are running.
            while (opModeIsActive() &&
                    (leftFrontDrive.isBusy() && rightFrontDrive.isBusy() && leftBackDrive.isBusy() && rightBackDrive.isBusy())) {

                // Determine required steering to keep on heading
                turnSpeed = getSteeringCorrection(0, P_DRIVE_GAIN);
                telemetry.addData("turnSpeed", turnSpeed);
                telemetry.addData("LeftFront", leftFrontDrive.getPower());
                telemetry.addData("RightFront", rightFrontDrive.getPower());
                telemetry.addData("LeftBack", leftBackDrive.getPower());
                telemetry.addData("RightBack", rightBackDrive.getPower());
                // Apply the turning correction to the current driving speed.
                moveRobot(AUTO_SPEED, turnSpeed, 1);
                telemetry.update();
                sleep(250);
            }

            // Stop all motion & Turn off RUN_TO_POSITION
            moveRobot(0, 0, 0);
            leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public void driveRight(double distance) {

        resetHeading();
        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            int moveCounts = (int)(distance * COUNTS_PER_INCH);
            leftFrontTarget = leftFrontDrive.getCurrentPosition() + moveCounts;
            rightFrontTarget = rightFrontDrive.getCurrentPosition() - moveCounts;
            leftBackTarget = leftBackDrive.getCurrentPosition() - moveCounts;
            rightBackTarget = rightBackDrive.getCurrentPosition() + moveCounts;

            // Set Target FIRST, then turn on RUN_TO_POSITION
            leftFrontDrive.setTargetPosition(leftFrontTarget);
            rightFrontDrive.setTargetPosition(rightFrontTarget);
            leftBackDrive.setTargetPosition(leftBackTarget);
            rightBackDrive.setTargetPosition(rightBackTarget);

            leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // Set the required driving speed  (must be positive for RUN_TO_POSITION)
            // Start driving straight, and then enter the control loop
            leftFrontDrive.setPower(AUTO_SPEED);
            rightFrontDrive.setPower(AUTO_SPEED);
            leftBackDrive.setPower(AUTO_SPEED);
            rightBackDrive.setPower(AUTO_SPEED);

            // keep looping while we are still active, and BOTH motors are running.
            while (opModeIsActive() &&
                    (leftFrontDrive.isBusy() && rightFrontDrive.isBusy() && leftBackDrive.isBusy() && rightBackDrive.isBusy())) {

                // Determine required steering to keep on heading
                turnSpeed = getSteeringCorrection(0, P_DRIVE_GAIN);

                // Apply the turning correction to the current driving speed.
                rightBackDrive.setPower(AUTO_SPEED * turnSpeed);
                rightFrontDrive.setPower(AUTO_SPEED * turnSpeed);

                telemetry.addData("LeftFront", leftFrontDrive.getPower());
                telemetry.addData("RightFront", rightFrontDrive.getPower());
                telemetry.addData("LeftBack", leftBackDrive.getPower());
                telemetry.addData("RightBack", rightBackDrive.getPower());
                telemetry.update();
            }

            // Stop all motion & Turn off RUN_TO_POSITION
            moveRobot(0, 0, 0);
            leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public void driveLeft(double distance) {

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            int moveCounts = (int)(distance * COUNTS_PER_INCH);
            leftFrontTarget = leftFrontDrive.getCurrentPosition() - moveCounts;
            rightFrontTarget = rightFrontDrive.getCurrentPosition() + moveCounts;
            leftBackTarget = leftBackDrive.getCurrentPosition() + moveCounts;
            rightBackTarget = rightBackDrive.getCurrentPosition() - moveCounts;

            // Set Target FIRST, then turn on RUN_TO_POSITION
            leftFrontDrive.setTargetPosition(leftFrontTarget);
            rightFrontDrive.setTargetPosition(rightFrontTarget);
            leftBackDrive.setTargetPosition(leftBackTarget);
            rightBackDrive.setTargetPosition(rightBackTarget);

            leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // Set the required driving speed  (must be positive for RUN_TO_POSITION)
            // Start driving straight, and then enter the control loop
            leftFrontDrive.setPower(AUTO_SPEED);
            rightFrontDrive.setPower(AUTO_SPEED);
            leftBackDrive.setPower(AUTO_SPEED);
            rightBackDrive.setPower(AUTO_SPEED);

            // keep looping while we are still active, and BOTH motors are running.
            while (opModeIsActive() &&
                    (leftFrontDrive.isBusy() && rightFrontDrive.isBusy() && leftBackDrive.isBusy() && rightBackDrive.isBusy())) {

                // Determine required steering to keep on heading
                turnSpeed = getSteeringCorrection(0, P_DRIVE_GAIN);
                // Apply the turning correction to the current driving speed.
                moveRobot(AUTO_SPEED, turnSpeed, 1);
            }

            // Stop all motion & Turn off RUN_TO_POSITION
            moveRobot(0, 0, 0);
            leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public void moveRobot(double drive, double turn, int direction) {
        double leftFrontSpeed;
        double rightFrontSpeed;
        double leftBackSpeed;
        double rightBackSpeed;

        if (direction == 1) {
            leftFrontSpeed = drive - turn;
            rightFrontSpeed = drive + turn;
            leftBackSpeed = drive - turn;
            rightBackSpeed = drive + turn;

            // Scale speeds down if either one exceeds +/- 1.0;
            double max = Math.max(Math.max(Math.abs(leftFrontSpeed),
                    Math.abs(rightFrontSpeed)),
                    Math.max(Math.abs(leftBackSpeed),
                    Math.abs(rightBackSpeed)));
            if (max > 1.0) {
                leftFrontSpeed /= max;
                rightFrontSpeed /= max;
                leftBackSpeed /= max;
                rightBackSpeed /= max;
            }
        } else {
            leftFrontSpeed = 0;
            rightFrontSpeed = 0;
            leftBackSpeed = 0;
            rightBackSpeed = 0;
        }

        leftFrontDrive.setPower(leftFrontSpeed);
        rightFrontDrive.setPower(rightFrontSpeed);
        leftBackDrive.setPower(leftBackSpeed);
        rightBackDrive.setPower(rightBackSpeed);
    }


    public double getSteeringCorrection(double desiredHeading, double proportionalGain) {
        // Get the robot heading by applying an offset to the IMU heading
        robotHeading = getRawHeading() - headingOffset;
        //telemetry.addData("Raw Heading", robotHeading);
        // Determine the heading current error
        headingError = desiredHeading - robotHeading;

        // Normalize the error to be within +/- 180 degrees
        while (headingError > 180)  headingError -= 360;
        while (headingError <= -180) headingError += 360;

        // Multiply the error by the gain to determine the required steering correction/  Limit the result to +/- 1.0
        return Range.clip(headingError * proportionalGain, -1, 1);
    }

    /**
     * read the raw (un-offset Gyro heading) directly from the IMU
     */
    public double getRawHeading() {
        Orientation angles   = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return angles.firstAngle;
    }

    /**
     * Reset the "offset" heading back to zero
     */
    public void resetHeading() {
        // Save a new heading offset equal to the current raw heading.
        headingOffset = getRawHeading();
        robotHeading = 0;
    }

    public void liftUp(double Inches, double timeoutS) {
        int newLiftLiftTarget;

        ElapsedTime driveRuntime = new ElapsedTime();

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLiftLiftTarget = liftLiftMotor.getCurrentPosition() - (int)(Inches * LIFT_COUNTS_PER_INCH);
            if (newLiftLiftTarget < -4900){
                newLiftLiftTarget = -4900;
            }
            liftLiftMotor.setTargetPosition(newLiftLiftTarget);

            // Turn On RUN_TO_POSITION
            liftLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            driveRuntime.reset();
            liftLiftMotor.setPower(AUTO_SPEED);

            // keep looping while we are still active, and there is time left, and all motors are running.
            while (opModeIsActive() && (driveRuntime.seconds() < timeoutS) && liftLiftMotor.isBusy()) {

                // Display it for the driver.
                telemetry.addData("Running to",  " %7d :%7d", newLiftLiftTarget);
                telemetry.addData("Currently at",  " at %7d :%7d", liftLiftMotor.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            liftLiftMotor.setPower(0);

            // Turn off RUN_TO_POSITION
            liftLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            sleep(250);   // optional pause after each move.
        }
    }

    public void liftDown(double Inches, double timeoutS) {
        int newLiftLiftTarget;

        ElapsedTime driveRuntime = new ElapsedTime();

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLiftLiftTarget = liftLiftMotor.getCurrentPosition() + (int)(Inches * LIFT_COUNTS_PER_INCH);
            if (newLiftLiftTarget > 0){
                newLiftLiftTarget = 0;
            }
            liftLiftMotor.setTargetPosition(newLiftLiftTarget);

            // Turn On RUN_TO_POSITION
            liftLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            driveRuntime.reset();
            liftLiftMotor.setPower(AUTO_SPEED);

            // keep looping while we are still active, and there is time left, and all motors are running.
            while (opModeIsActive() && (driveRuntime.seconds() < timeoutS) && liftLiftMotor.isBusy()) {

                // Display it for the driver.
                telemetry.addData("Running to",  " %7d :%7d", newLiftLiftTarget);
                telemetry.addData("Currently at",  " at %7d :%7d", liftLiftMotor.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            liftLiftMotor.setPower(0);

            // Turn off RUN_TO_POSITION
            liftLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            sleep(250);   // optional pause after each move.
        }
    }

    public void liftDown(double seconds){
        if (seconds != 0) {
            liftLiftMotor.setPower(0.5);
            sleep((long) (seconds * 1000));
            liftLiftMotor.setPower(0);
            sleep(250);
        }
    }

    public void clampClose(){
        leftClawServo.setPosition(0.0);
        rightClawServo.setPosition(1.0);
        sleep(1000);
    }

    public void clampOpen(){
        leftClawServo.setPosition(1.0);
        rightClawServo.setPosition(0.0);
        sleep(1000);
    }


    public void goToStage(int stage) {
        if (stage == 0) {
                // Ensure that the opmode is still active
                if (opModeIsActive()) {
                    liftLiftMotor.setTargetPosition(lvl0);

                    liftLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    liftLiftMotor.setPower(Math.abs(1));

                    while (opModeIsActive() && liftLiftMotor.isBusy()) {
                        telemetry.addData("Running to Stage", 0);
                        telemetry.update();
                    }

                    liftLiftMotor.setPower(0);
                    liftLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                    sleep(250);   // optional pause after each move.
                }
        } else if (stage == 1) {
            if (opModeIsActive()) {
                liftLiftMotor.setTargetPosition(lvl1);

                liftLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                liftLiftMotor.setPower(Math.abs(1));

                while (opModeIsActive() && liftLiftMotor.isBusy()) {
                    telemetry.addData("Running to Stage", 1);
                    telemetry.update();
                }

                liftLiftMotor.setPower(0);
                liftLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                sleep(250);   // optional pause after each move.
            }
        } else if (stage == 2){
            if (opModeIsActive()) {
                liftLiftMotor.setTargetPosition(lvl2);

                liftLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                liftLiftMotor.setPower(Math.abs(1));

                while (opModeIsActive() && liftLiftMotor.isBusy()) {
                    telemetry.addData("Running to Stage", 2);
                    telemetry.update();
                }

                liftLiftMotor.setPower(0);
                liftLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                sleep(250);   // optional pause after each move.
            }
        } else if (stage == 3){
            if (opModeIsActive()) {
                liftLiftMotor.setTargetPosition(lvl3);

                liftLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                liftLiftMotor.setPower(Math.abs(1));

                while (opModeIsActive() && liftLiftMotor.isBusy()) {
                    telemetry.addData("Running to Stage", 3);
                    telemetry.update();
                }

                liftLiftMotor.setPower(0);
                liftLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                sleep(250);   // optional pause after each move.
            }
        } else {
            telemetry.addData("Error", "Stage not programmed (yet)");
            telemetry.update();
        }
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

    public int desiredLift(){
        if (desiredLiftPosition == 0){
            return lvl0;
        } else if (desiredLiftPosition == 1){
            return lvl1;
        } else if (desiredLiftPosition == 2){
            return lvl2;
        } else if (desiredLiftPosition == 3){
            return lvl3;
        } else {
            return liftLiftMotor.getCurrentPosition();
        }
    }
}
