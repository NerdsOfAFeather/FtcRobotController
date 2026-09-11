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
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;

import static com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection.RIGHT;
import static com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection.UP;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import java.util.ArrayList;
import java.util.List;

/** Created by Gavin for FTC Team 6347 */
public abstract class DecodeConfig extends DecodeObjectDetection {

    public DcMotorEx leftFrontDrive = null;
    public DcMotorEx leftBackDrive = null;
    public DcMotorEx rightFrontDrive = null;
    public DcMotorEx rightBackDrive = null;
    public DcMotorEx flywheelLeft = null;
    public DcMotorEx flywheelRight = null;
    public DcMotorEx intakeMotor = null;
    public DcMotor storageMotor = null;
    public Servo releaseServo = null;
    public NormalizedColorSensor storage1;
    public NormalizedColorSensor storage2;
    public NormalizedColorSensor storage3;
    IMU imu;

    public void initDriveHardware() {
        leftFrontDrive = hardwareMap.get(DcMotorEx.class, "FrontLeftDrive");
        leftBackDrive = hardwareMap.get(DcMotorEx.class, "BackLeftDrive");
        rightFrontDrive = hardwareMap.get(DcMotorEx.class, "FrontRightDrive");
        rightBackDrive = hardwareMap.get(DcMotorEx.class, "BackRightDrive");

        leftFrontDrive.setDirection(Direction.REVERSE);
        leftBackDrive.setDirection(Direction.REVERSE);
        rightFrontDrive.setDirection(Direction.REVERSE);
        rightBackDrive.setDirection(Direction.FORWARD);

        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void initIntakeHardware() {
        intakeMotor = hardwareMap.get(DcMotorEx.class, "Intake");

        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    public void initStorageHardware() {
        storageMotor = hardwareMap.get(DcMotorEx.class, "Storage");

        storageMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        storageMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        releaseServo = hardwareMap.get(Servo.class, "Release");

        storage1 = hardwareMap.get(NormalizedColorSensor.class, "Storage1");
        storage2 = hardwareMap.get(NormalizedColorSensor.class, "Storage2");
        storage3 = hardwareMap.get(NormalizedColorSensor.class, "Storage3");

        storage1.setGain(10.0f);
        storage2.setGain(10.0f);
        storage3.setGain(10.0f);
    }

    public void initOutputHardware() {
        flywheelLeft = hardwareMap.get(DcMotorEx.class, "FlywheelLeft");
        flywheelRight = hardwareMap.get(DcMotorEx.class, "FlywheelRight");

        flywheelLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheelRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        flywheelLeft.setDirection(Direction.REVERSE);
        flywheelRight.setDirection(Direction.FORWARD);
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
        initIntakeHardware();
        initStorageHardware();
        initOutputHardware();
        initIMU();
    }

    public StorageColors getStoragePositions() {
        List<Color> colorList = new ArrayList<>();

        List<NormalizedRGBA> list = List.of(
                storage1.getNormalizedColors(),
                storage2.getNormalizedColors(),
                storage3.getNormalizedColors());

        for (NormalizedRGBA colors : list) {
            if (colors.green >= 0.3) {
                if (colors.red >= 0.3) {
                    colorList.add(Color.PURPLE);
                    continue;
                }
                colorList.add(Color.GREEN);
                continue;
            }
            colorList.add(Color.UNKNOWN);
        }

        return new StorageColors(colorList);
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

            private final int velocity;

            public SpinUp(int velocity) {
                this.velocity = velocity;
            }

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    flywheelLeft.setVelocity(velocity);
                    flywheelRight.setVelocity(velocity);
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
            return new SpinUp(925);
        }

        public Action spinUpFaster() {
            return new SpinUp(975);
        }

        public Action spinUpSlower() {
            return new SpinUp(775);
        }

        public Action spinDown() {
            return new SpinDown();
        }

    }

    public class Intake {

        private final DcMotorEx intake;
        public Intake(HardwareMap hardwareMap) {
            intake = hardwareMap.get(DcMotorEx.class, "Intake");
            intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            intake.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            intake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        private class TurnOn implements Action {

            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    intake.setPower(-1);
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
                    intake.setPower(0.0);
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

    public class Storage {

        private final DcMotorEx storage;
        public Storage(HardwareMap hardwareMap) {
            storage = hardwareMap.get(DcMotorEx.class, "Storage");
            storage.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            storage.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            storage.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        private class TurnOn implements Action {

            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    storage.setPower(1.0 / 2.0);
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
                    storage.setPower(0.0);
                    initialized = true;
                }

                return false;
            }
        }

        private class RotateOnce implements Action {

            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    storage.setTargetPosition(storage.getCurrentPosition() + 285);
                    initialized = true;
                }

                return false;
            }
        }

        private class InitialRotation implements Action {

            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    storage.setTargetPosition(storage.getCurrentPosition() + 140);
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

        public Action rotateOnce() {
            return new RotateOnce();
        }

        public Action initialRotation() {
            return new InitialRotation();
        }

    }

    public Action sleep(double time) {
        return new SleepAction(time);
    }

    public class StorageColors {
        final Color position1;
        final Color position2;
        final Color position3;

        public StorageColors(List<Color> colors) {
            if (colors.size() != 3) {
                position1 = null;
                position2 = null;
                position3 = null;
            } else {
                position1 = colors.get(0);
                position2 = colors.get(1);
                position3 = colors.get(2);
            }
        }
    }

    public enum Color {
        PURPLE, GREEN, UNKNOWN
    }

}
