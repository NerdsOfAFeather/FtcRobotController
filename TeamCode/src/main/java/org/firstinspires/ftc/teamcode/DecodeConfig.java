package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.SleepAction;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import static com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection.RIGHT;
import static com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection.UP;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

/** Created by Gavin for FTC Team 6347 */
public abstract class DecodeConfig extends DecodeObjectDetection {

    public DcMotorEx leftFrontDrive = null;
    public DcMotorEx leftBackDrive = null;
    public DcMotorEx rightFrontDrive = null;
    public DcMotorEx rightBackDrive = null;
    public DcMotorEx flywheelLeft = null;
    public DcMotorEx flywheelRight = null;
    public DcMotor outputMotor = null;
    IMU imu;

    public void initDriveHardware() {

        leftFrontDrive = hardwareMap.get(DcMotorEx.class, "FrontLeftDrive");
        leftBackDrive = hardwareMap.get(DcMotorEx.class, "BackLeftDrive");
        rightFrontDrive = hardwareMap.get(DcMotorEx.class, "FrontRightDrive");
        rightBackDrive = hardwareMap.get(DcMotorEx.class, "BackRightDrive");

        leftFrontDrive.setDirection(Direction.REVERSE);
        leftBackDrive.setDirection(Direction.FORWARD);
        rightFrontDrive.setDirection(Direction.REVERSE);
        rightBackDrive.setDirection(Direction.FORWARD);

        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void initIntakeHardware() {

    }

    public void initOutputHardware() {

        outputMotor = hardwareMap.get(DcMotorEx.class, "Output");
        flywheelLeft = hardwareMap.get(DcMotorEx.class, "FlywheelLeft");
        flywheelRight = hardwareMap.get(DcMotorEx.class, "FlywheelRight");

        outputMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        flywheelLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheelRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        flywheelLeft.setDirection(Direction.REVERSE);
        flywheelRight.setDirection(Direction.FORWARD);

        outputMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        outputMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
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
    }

    public int nextAvailable(DcMotor motor, int position) {
        // Position should always be between 0 and 119 (inclusive)
        int currentPos = motor.getCurrentPosition();
        int remainder = currentPos % 120;
        int toAdd;
        if (position > remainder) {
            toAdd = position - remainder;
        } else if (position < remainder) {
            toAdd = (120 - currentPos) + position;
        } else {
            toAdd = 120;
        }
        return currentPos + toAdd;
    }

    public class LastGamepadState {
        public boolean a;
        public boolean b;
        public boolean x;
        public boolean y;

        public LastGamepadState(Gamepad gamepad) {
            a = gamepad.a;
            b = gamepad.b;
            x = gamepad.x;
            y = gamepad.y;
        }
    }

    public class Flywheels {
        private final DcMotorEx flywheelLeft;
        private final DcMotorEx flywheelRight;

        public Flywheels(HardwareMap hardwareMap) {
            flywheelLeft = hardwareMap.get(DcMotorEx.class, "FlywheelLeft");
            flywheelRight = hardwareMap.get(DcMotorEx.class, "FlywheelRight");

            flywheelLeft.setDirection(Direction.REVERSE);
            flywheelRight.setDirection(Direction.FORWARD);
        }

        private class SpinUp implements Action {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    flywheelLeft.setPower(1.0);
                    flywheelRight.setPower(1.0);
                    initialized = true;
                }

                double velL = flywheelLeft.getVelocity();
                double velR = flywheelRight.getVelocity();
                packet.put("shooterVelocity", velL);
                packet.put("shooterVelocity", velR);
                return false;
            }
        }

        private class SpinDown implements Action {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    flywheelLeft.setPower(0.0);
                    flywheelRight.setPower(0.0);
                    initialized = true;
                }

                double velL = flywheelLeft.getVelocity();
                double velR = flywheelRight.getVelocity();
                packet.put("shooterVelocity", velL);
                packet.put("shooterVelocity", velR);
                return velL > 500.0 || velR > 500.0;
            }
        }

        public Action spinUp() {
            return new SpinUp();
        }

        public Action spinDown() {
            return new SpinDown();
        }

    }

    public class Flappers {

        private DcMotorEx output;
        public Flappers(HardwareMap hardwareMap) {
            output = hardwareMap.get(DcMotorEx.class, "Output");
            output.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            output.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            output.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        private class TurnOn implements Action {

            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    output.setPower(1.0 / 16.0);
                    initialized = true;
                }

                return false;
            }
        }

        private class TurnOff implements Action {

            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    output.setPower(0.0);
                    initialized = true;
                }

                return false;
            }
        }

        public Action turnOn() {
            return new TurnOn();
        }

        public Action turnOff() {
            return new TurnOff();
        }

    }

    public Action sleep(double time) {
        return new SleepAction(time);
    }

}
