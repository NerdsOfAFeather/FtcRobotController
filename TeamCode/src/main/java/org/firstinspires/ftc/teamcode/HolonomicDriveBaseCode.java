package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.Locale;

public class HolonomicDriveBaseCode extends ConceptTensorFlowObjectDetectionWebcam {

    //Setting up each motor
    public DcMotor frontRight;
    public DcMotor backRight;
    public DcMotor backLeft;
    public DcMotor frontLeft;

    // These are used later to help with straight driving
    public double degree = 0;
    public BNO055IMU imu;
    Orientation angles;
    Acceleration gravity;
    //the number of motor tick counts, each motor has a different number
    private double ticksPerTorqenado = 480;
    //The post gear box gear ratio.
    private double gearRatio = 1.0;
    //The circumference of the drive wheel.
    private double wheelCircumference = 31.4; // ??
    //Formula to calculate ticks per centimeter for the current drive set up.FORWARDS/BACKWARD ONLY - correction with 2/3
    private double ticksPerCm = (ticksPerTorqenado * gearRatio) / wheelCircumference * (2.0 / 3.2);

    //this ensures that all the drivetrain motors and other variables are initiated
    public void initDriveHardware() {
        // init the motors
        frontRight = hardwareMap.dcMotor.get("fr");
        backRight = hardwareMap.dcMotor.get("br");
        frontLeft = hardwareMap.dcMotor.get("fl");
        backLeft = hardwareMap.dcMotor.get("bl");

        // set wheel direction (If a motor is put on backwards the direction may need to be reversed)
        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        //This is all used to assist forward driving with straightness
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        //Gyro stuff
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
        // (not used this year)  colorSensor = hardwareMap.colorSensor.get("color");
    }

    // this class is from the sample code
    void composeTelemetry() {

        // At the beginning of each telemetry update, grab a bunch of data
        // from the IMU that we will then display in separate lines.
        telemetry.addAction(new Runnable() {
            @Override
            public void run() {
                // Acquiring the angles is relatively expensive; we don't want
                // to do that in each of the three items that need that info, as that's
                // three times the necessary expense.
                angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
                gravity = imu.getGravity();
            }
        });

        telemetry.addLine()
                .addData("status", new Func<String>() {
                    @Override
                    public String value() {
                        return imu.getSystemStatus().toShortString();
                    }
                })
                .addData("calib", new Func<String>() {
                    @Override
                    public String value() {
                        return imu.getCalibrationStatus().toString();
                    }
                });

        telemetry.addLine()
                .addData("heading", new Func<String>() {
                    @Override
                    public String value() {
                        return formatAngle(angles.angleUnit, angles.firstAngle);
                    }
                })
                .addData("roll", new Func<String>() {
                    @Override
                    public String value() {
                        return formatAngle(angles.angleUnit, angles.secondAngle);
                    }
                })
                .addData("pitch", new Func<String>() {
                    @Override
                    public String value() {
                        return formatAngle(angles.angleUnit, angles.thirdAngle);
                    }
                });

        telemetry.addLine()
                .addData("grvty", new Func<String>() {
                    @Override
                    public String value() {
                        return gravity.toString();
                    }
                })
                .addData("mag", new Func<String>() {
                    @Override
                    public String value() {
                        return String.format(Locale.getDefault(), "%.3f",
                                Math.sqrt(gravity.xAccel * gravity.xAccel
                                        + gravity.yAccel * gravity.yAccel
                                        + gravity.zAccel * gravity.zAccel));
                    }
                });
    }

    //This is from the control hub imu, used to find angle of robot
    String formatAngle(AngleUnit angleUnit, double angle) {
        return formatDegrees(AngleUnit.DEGREES.fromUnit(angleUnit, angle));
    }

    String formatDegrees(double degrees) {
        return String.format(Locale.getDefault(), "%.1f", AngleUnit.DEGREES.normalize(degrees));
    }

    //can be either break or float
    public void setZeroPowBehv(DcMotor.ZeroPowerBehavior behv) {
        frontLeft.setZeroPowerBehavior(behv);
        frontRight.setZeroPowerBehavior(behv);
        backLeft.setZeroPowerBehavior(behv);
        backRight.setZeroPowerBehavior(behv);
    }

    // Sometimes the motors are on backwards and this fixes that
    public void setDirection() {
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    //Auto commands

    //for backwards use negative power
    //Pass in centimeters.
    public void forward(double targetDistance, double power) {
        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
        double targetDistanceTicks = Math.abs(targetDistance * ticksPerCm);
        double currentDistanceTicks = 0;
        while ((Math.abs(currentDistanceTicks) < targetDistanceTicks) && opModeIsActive()) {
            telemetry.addData("Target pos ticks: ", targetDistanceTicks);
            telemetry.addData("Target Distance:", targetDistance + "cm");
            currentDistanceTicks = (
                    frontRight.getCurrentPosition()+
                            frontLeft.getCurrentPosition() +
                           backRight.getCurrentPosition() +
                           backLeft.getCurrentPosition()) / 4.0;
            telemetry.addData("Current pos ticks Avg: ", currentDistanceTicks);
            telemetry.addData("Current Distance cm", currentDistanceTicks / ticksPerCm);
            telemetry.update();

            frontLeft.setPower(power);
            frontRight.setPower(power);
            backLeft.setPower(power);
            backRight.setPower(power);
        }
        stopMotors();
    }

    //stop. Just stop
    public void stopMotors() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    //move forward forever
    public void forwardForever( double speed) {
        frontLeft.setPower(speed);
        frontRight.setPower(speed);
        backLeft.setPower(speed);
        backRight.setPower(speed);
    }

    public void backwardForever(double speed) {
        frontLeft.setPower(-speed);
        frontRight.setPower(-speed);
        backLeft.setPower(-speed);
        backRight.setPower(-speed);
    }

    public void leftwardForever( double speed) {
        frontLeft.setPower(speed);
        frontRight.setPower(-speed);
        backLeft.setPower(-speed);
        backRight.setPower(speed);
    }

    public void rightwardForever(double speed) {
        frontLeft.setPower(-speed);
        frontRight.setPower(speed);
        backLeft.setPower(speed);
        backRight.setPower(-speed);
    }

    //can pass run with encoder or run to position
    public void setDriveMode(DcMotor.RunMode mode) {
        frontLeft.setMode(mode);
        frontRight.setMode(mode);
        backLeft.setMode(mode);
        backRight.setMode(mode);
    }

    //Rightwards
    public void sideRight(double targetDistance, double power) {
        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
        double currentDistance = 0;
        while ((Math.abs(currentDistance) < targetDistance) && opModeIsActive()) {
            currentDistance = frontLeft.getCurrentPosition();
            frontLeft.setPower(-power);
            frontRight.setPower(power);
            backLeft.setPower(power);
            backRight.setPower(-power);
        }
        stopMotors();
    }

    //move left.
    //when the robot reaches the target encoder tick distance, it will stop,(hopefully)
    public void sideLeft(double targetDistance, double power) {
        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
        double currentDistance = 0;
        while (Math.abs(currentDistance) < targetDistance && opModeIsActive()) {
            currentDistance = frontRight.getCurrentPosition();
            frontLeft.setPower(power);
            frontRight.setPower(-power);
            backLeft.setPower(-power);
            backRight.setPower(power);
        }
        stopMotors();
    }

    //clockwise is 0 cc is 1

    public void pivotCW(double degree, double power) {

        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);

        double currentDegree = 0;
        while ((currentDegree < degree) && opModeIsActive()) {
            // Division by ten below takes into account the 5:1 encoder count to angular degree ratio, and averages the front wheels for a more accurate sense of turning
            currentDegree = (frontLeft.getCurrentPosition() + backLeft.getCurrentPosition()) / 10;
            frontLeft.setPower(power);
            frontRight.setPower(-power);
            backRight.setPower(-power);
        }
        stopMotors();
    }


    //this is not calibrated for the robot itsself yet, but turns
    public void pivotCC(double degree, double power) {

        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);

        double currentDegree = 0;
        while (currentDegree < degree && opModeIsActive()) {
            currentDegree = (frontRight.getCurrentPosition() + backRight.getCurrentPosition()) / 2;
            frontLeft.setPower(-power);
            frontRight.setPower(power);
            backLeft.setPower(-power);
            backRight.setPower(power);
        }
        stopMotors();
    }

    //hopefully straight sideways
    public void sidewaysWithGyro(double targetDistance, double power) {
        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
        double targetDistanceTicks = Math.abs(targetDistance * ticksPerCm * 4.0 / 3.2);
        double currentDistanceTicks = 0;
        degree = 0;
        AngularVelocity lastAngleV = proportionalDriveStuff(imu.getAngularVelocity());
        while ((Math.abs(currentDistanceTicks) < targetDistanceTicks) && opModeIsActive()) {
            lastAngleV = proportionalDriveStuff(lastAngleV);

            telemetry.addData("Target pos ticks: ", targetDistanceTicks);
            telemetry.addData("Target Distance:", targetDistance + "cm");
            currentDistanceTicks = (frontLeft.getCurrentPosition() + backRight.getCurrentPosition() / 2.0);

            telemetry.addData("Current pos ticks Avg: ", currentDistanceTicks);
            telemetry.addData("Current Distance cm", currentDistanceTicks / ticksPerCm);
            telemetry.update();

            frontLeft.setTargetPosition((int) targetDistanceTicks);
            frontRight.setTargetPosition((int) -targetDistanceTicks);
            backLeft.setTargetPosition((int) -targetDistanceTicks);
            backRight.setTargetPosition((int) targetDistanceTicks);

            //Straighten out
            if (degree > 3) {
                setDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
                frontLeft.setPower(power + .1);
                frontRight.setPower(-power);
                backLeft.setPower(power + .1);
                backRight.setPower(-power);
            }
            if (degree < -3) {
                setDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
                frontLeft.setPower(power);
                frontRight.setPower(-power - .1);
                backLeft.setPower(power);
                backRight.setPower(-power - .1);
            } else {
                setDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
                frontLeft.setPower(power);
                frontRight.setPower(-power);
                backLeft.setPower(power);
                backRight.setPower(-power);
            }

        }
        stopMotors();
    }

    //straight forwards and back
    //pass centimeter distance and negate power if nessesary
    public void forwardWithGyro(double targetDistance, double power) {
        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
        double targetDistanceTicks = Math.abs(targetDistance * ticksPerCm);
        double currentDistanceTicks = 0;
        degree = 0;
        AngularVelocity lastAngleV = proportionalDriveStuff(imu.getAngularVelocity());
        while ((Math.abs(currentDistanceTicks) < targetDistanceTicks) && opModeIsActive()) {
            lastAngleV = proportionalDriveStuff(lastAngleV);

            telemetry.addData("Target pos ticks: ", targetDistanceTicks);
            telemetry.addData("Target Distance:", targetDistance + "cm");

            frontLeft.setTargetPosition((int) targetDistanceTicks);
            frontRight.setTargetPosition((int) targetDistanceTicks);
            backLeft.setTargetPosition((int) targetDistanceTicks);
            backRight.setTargetPosition((int) targetDistanceTicks);

            currentDistanceTicks = (frontRight.getCurrentPosition() +
                    frontLeft.getCurrentPosition() +
                    backRight.getCurrentPosition() +
                    backLeft.getCurrentPosition()) / 4.0;
            telemetry.addData("Current pos ticks Avg: ", currentDistanceTicks);
            telemetry.addData("Current Distance cm", currentDistanceTicks / ticksPerCm);
            telemetry.update();
            //Straighten out

            if (degree > 3) {
                setDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
                frontLeft.setPower(power + .1);
                frontRight.setPower(power);
                backLeft.setPower(power + .1);
                backRight.setPower(power);
            } else if (degree < -3) {
                setDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
                frontLeft.setPower(power);
                frontRight.setPower(power - .1);
                backLeft.setPower(power);
                backRight.setPower(power - .1);
            } else {
                setDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
                frontLeft.setPower(power);
                frontRight.setPower(power);
                backLeft.setPower(power);
                backRight.setPower(power);
            }

        }
        stopMotors();
        setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    //im not really sure what this is for tbh
    public AngularVelocity proportionalDriveStuff(AngularVelocity angleV) {
        telemetry.addData("AV", imu.getAngularVelocity());
        long last = angleV.acquisitionTime;
        AngularVelocity rate = imu.getAngularVelocity();
        long current = rate.acquisitionTime;
        double degreeChange = (current - last) * rate.yRotationRate / 1.0e9;
        last = current;
        degree = degreeChange + degree;
        return rate;
    }

    // pulled from daniel's code
    // Below this is Daniel based versions

    public static final double GYRO_ERROR_THRESHOLD = 5;
    public static final double P_GYRO_TURN_COEFF = 0.02;
    //reference
    // imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.XZY, AngleUnit.DEGREES)

    private double getGyroError(double targetAngle) {
        double error = targetAngle - imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.XZY, AngleUnit.DEGREES).thirdAngle;

        // keep the error on a range of -179 to 180
        while (opModeIsActive() && error > 180) error -= 360;
        while (opModeIsActive() && error <= -180) error += 360;

        return error;
    }
    
    public void gyroPivot(double targetDistance, double power) {
        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
        double targetDistanceTicks = Math.abs(targetDistance * ticksPerCm * 3.5 / 4.0);
        double currentDistanceTicks = 0;
        degree = 0;
        AngularVelocity lastAngleV = proportionalDriveStuff(imu.getAngularVelocity());
        while ((Math.abs(currentDistanceTicks) < targetDistanceTicks) && opModeIsActive()) {
            lastAngleV = proportionalDriveStuff(lastAngleV);

            telemetry.addData("Target pos ticks: ", targetDistanceTicks);
            telemetry.addData("Target Distance:", targetDistance + "cm");
            currentDistanceTicks = (frontLeft.getCurrentPosition() + backLeft.getCurrentPosition() / 2.0);

            telemetry.addData("Current pos ticks Avg: ", currentDistanceTicks);
            telemetry.addData("Current Distance cm", currentDistanceTicks / ticksPerCm);
            telemetry.update();

            frontLeft.setTargetPosition((int) targetDistanceTicks);
            frontRight.setTargetPosition((int) -targetDistanceTicks);
            backLeft.setTargetPosition((int) targetDistanceTicks);
            backRight.setTargetPosition((int) -targetDistanceTicks);

            setDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
            frontLeft.setPower(power);
            frontRight.setPower(-power);
            backLeft.setPower(power);
            backRight.setPower(-power);

            //Straighten out
            if (degree > 2) {
                setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
                frontLeft.setPower(power + .1);
                frontRight.setPower(-power);
                backLeft.setPower(power + .1);
                backRight.setPower(-power);
            }
            if (degree < -2) {
                setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
                frontLeft.setPower(power);
                frontRight.setPower(-power - .1);
                backLeft.setPower(power);
                backRight.setPower(-power - .1);
            }

        }
        stopMotors();
    }

    //TELEOP Code

    public void moveTeleOp() {
        //the left Joystick controls movement, and the right controls turning.
        //in order to counteract the differences between the basic formula for strafing and the one for moving forwards and backwards,
        //we use trigonometry to derive relevant powers for each wheel tio move at the correct angles.
        // translating polar coordanates of joystick to polar coordinates of mechenum drive
        double r = Math.hypot(gamepad1.right_stick_x, gamepad1.left_stick_y);
        double robotAngle = Math.atan2(gamepad1.left_stick_y, gamepad1.right_stick_x) - Math.PI / 4;
        double rightX = gamepad1.left_stick_x;
        double fr = r * Math.cos(robotAngle) + rightX;
        double fl = r * Math.sin(robotAngle) - rightX;
        double bl = r * Math.sin(robotAngle) + rightX;
        double br = r * Math.cos(robotAngle) - rightX;

        double intakePower = 0.0;

        if (Math.abs(fr) > 1 || Math.abs(fl) > 1 || Math.abs(br) > 1 || Math.abs(bl) > 1) {
            double max1 = Math.max(fr, fl);
            double max2 = Math.max(br, bl);
            double maxMax = Math.max(max1, max2);
            fr = fr / maxMax;
            fl = fl / maxMax;
            br = fl / maxMax;
            bl = bl / maxMax;
        } 

        // slow buttons
        if (gamepad1.left_trigger > 0) {
            frontRight.setPower(-fr / 4);
            frontLeft.setPower(-fl / 4);
            backLeft.setPower(-bl / 4);
            backRight.setPower(-br / 4);
        } else if (gamepad1.right_trigger > 0) {
            frontRight.setPower(-fr / 2);
            frontLeft.setPower(-fl / 2);
            backLeft.setPower(-bl / 2);
            backRight.setPower(-br / 2);
        } else {
            frontRight.setPower(-fr);
            frontLeft.setPower(-fl);
            backLeft.setPower(-bl);
            backRight.setPower(-br);
        }
    }

    public void buttonMoveTeleop(){
        double speedy;
        //sloooow buttons
        if (gamepad1.left_trigger == 1){
             speedy = .5;
        } else {
            speedy = 1;
        }

        //Dpad driving
        if(gamepad1.dpad_up){
            forwardForever(speedy);
        } else if (gamepad1.dpad_down){
            backwardForever(speedy);
        } else if (gamepad1.dpad_right){
            rightwardForever(speedy);
        } else if (gamepad1.dpad_left){
            leftwardForever(speedy);
        } else{
            stopMotors();
        }

        //turn
        double power = gamepad1.right_stick_x ;
        if(gamepad1.right_trigger ==1){
            power = power/2;
        }else {
            frontLeft.setPower(-power/2);
            frontRight.setPower(power/2);
            backLeft.setPower(-power/2);
            backRight.setPower(power/2);
        }
    }

    double[] wheelPowers = new double[4];
    public void altmoveTeleop(double x, double y, double rotation)
    {

        wheelPowers[0] = x + y + rotation;
        wheelPowers[1] = -x + y - rotation;
        wheelPowers[2] = -x + y + rotation;
        wheelPowers[3] = x + y - rotation;

        normalize(wheelPowers);

        frontLeft.setPower(wheelPowers[0]);
        frontRight.setPower(wheelPowers[1]);
        backLeft.setPower(wheelPowers[2]);
        backRight.setPower(wheelPowers[3]);
    }   //holonomicDrive

    public void normalize(double[] nums)
    {
        double maxMagnitude = Math.abs(wheelPowers[0]);
        for (int i = 1; i < wheelPowers.length; i++)
        {
            double magnitude = Math.abs(wheelPowers[i]);
            if (magnitude > maxMagnitude)
            {
                maxMagnitude = magnitude;
            }
        }

        if (maxMagnitude > 1.0)
        {
            for (int i = 0; i < wheelPowers.length; i++)
            {
                wheelPowers[i] /= maxMagnitude;
            }
        }
    }   //normalizeInPlace

    @Override
    public void runOpMode() throws InterruptedException {
    }
}

