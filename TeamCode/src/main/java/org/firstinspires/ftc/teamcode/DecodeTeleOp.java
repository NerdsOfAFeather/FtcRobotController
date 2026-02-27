package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

/**Created by Gavin for FTC Team 6347 */
@TeleOp(name="DecodeTeleOp", group="OpMode")
public class DecodeTeleOp extends DecodeConfig {

    private final ElapsedTime runtime = new ElapsedTime();
    double axial;
    double lateral;
    double yaw;
    boolean slowMode;
    boolean inverted;
    boolean sorterManual = true;
    boolean releaseActive = false;

    @Override
    public void init() {
        initDriveHardware();
        initIntakeHardware();
        initStorageHardware();
        initOutputHardware();
        telemetry.addData("Bingus", "Bongus");
        telemetry.update();
    }

    @Override
    public void start() {
        runtime.reset();
    }

    @Override
    public void loop() {
        double max;
        double leftFrontPower;
        double rightFrontPower;
        double leftBackPower;
        double rightBackPower;
        double intakePower;
        double sorterPower;
        double flywheelVelocity;
        double releasePosition;

        if (gamepad1.right_bumper && !slowMode) {
            slowMode = true;
        } else if (gamepad1.left_bumper && slowMode) {
            slowMode = false;
        }

        if (gamepad1.right_trigger >= 0.3 && !inverted) {
            inverted = true;
        } else if (gamepad1.left_trigger >= 0.3 && inverted) {
            inverted = false;
        }

        // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
        if (Math.abs(gamepad1.left_stick_y) >= 0.2) {
            axial = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
        } else {
            axial = 0;
        }
        if (Math.abs(gamepad1.left_stick_x) >= 0.2) {
            lateral = gamepad1.left_stick_x;
        } else {
            lateral = 0;
        }
        if (Math.abs(gamepad1.right_stick_x) >= 0.2) {
            yaw = -gamepad1.right_stick_x;
        } else {
            yaw = 0;
        }

        leftFrontPower = axial + lateral + yaw;
        rightFrontPower = axial - lateral - yaw;
        leftBackPower = axial - lateral + yaw;
        rightBackPower = axial + lateral - yaw;
        max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));
        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        if (slowMode) {
            leftFrontPower /= 2;
            rightFrontPower /= 2;
            leftBackPower /= 2;
            rightBackPower /= 2;
        }

        if (Math.abs(gamepad2.left_stick_y) >= 0.2) {
            intakePower = -gamepad2.left_stick_y;
        } else {
            intakePower = 0;
        }

        if (gamepad2.left_stick_y >= 0.2) { // Down
            flywheelVelocity = gamepad2.left_stick_y * 900;
        } else if (gamepad2.left_stick_y <= -0.2) { // Up
            flywheelVelocity = -gamepad2.left_stick_y * 1_000;
        } else {
            flywheelVelocity = 0;
        }

        if (Math.abs(gamepad2.right_stick_y) >= 0.2) {
            sorterPower = gamepad2.right_stick_y;
        } else {
            sorterPower = 0;
        }

        releasePosition = gamepad2.right_stick_x;
        // releasePosition = releaseActive ? -1.0 : 1.0;

        // This is test code:
        //
        // Uncomment the following code to test your motor directions.
        // Each button should make the corresponding motor run FORWARD.
        //   1) First get all the motors to take to correct positions on the robot
        //      by adjusting your Robot Configuration if necessary.
        //   2) Then make sure they run in the correct direction by modifying the
        //      the setDirection() calls above.
        // Once the correct motors move in the correct direction re-comment this code.

            /*
            leftFrontPower  = gamepad1.x ? 1.0 : 0.0;  // X gamepad
            leftBackPower   = gamepad1.a ? 1.0 : 0.0;  // A gamepad
            rightFrontPower = gamepad1.y ? 1.0 : 0.0;  // Y gamepad
            rightBackPower  = gamepad1.b ? 1.0 : 0.0;  // B gamepad
            */

        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);
        intakeMotor.setPower(intakePower);
        releaseServo.setPosition(releasePosition);
        storageMotor.setPower(sorterPower);
        flywheelRight.setVelocity(flywheelVelocity);
        flywheelLeft.setVelocity(flywheelVelocity);

        // Show the elapsed game time and wheel power.
        telemetry.addData("Run Time", runtime.toString());
        telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
        telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
        telemetry.addData("EncoderRight", rightBackDrive.getCurrentPosition());
        telemetry.addData("EncoderCenter", leftBackDrive.getCurrentPosition());
        telemetry.addData("EncoderLeft", rightFrontDrive.getCurrentPosition());

        telemetry.addData("Intake Power", intakePower);

        telemetry.addLine("Storage 1")
                .addData("Red", storage1.getNormalizedColors().red)
                .addData("Green", storage1.getNormalizedColors().green)
                .addData("Blue", storage1.getNormalizedColors().blue);
        telemetry.addLine("Storage 2")
                .addData("Red", storage2.getNormalizedColors().red)
                .addData("Green", storage2.getNormalizedColors().green)
                .addData("Blue", storage2.getNormalizedColors().blue);
        telemetry.addLine("Storage 3")
                .addData("Red", storage3.getNormalizedColors().red)
                .addData("Green", storage3.getNormalizedColors().green)
                .addData("Blue", storage3.getNormalizedColors().blue);

        telemetry.addData("Storage Motor Power", sorterPower);
        telemetry.addData("Storage Motor Position", storageMotor.getCurrentPosition());
        telemetry.addData("Release Servo Position", releaseServo.getPosition());

        telemetry.addData("Flywheel Velocity", flywheelVelocity);

        // Show joystick information as some other illustrative data
        telemetry.addLine("Left joystick | ")
                .addData("x", gamepad1.left_stick_x)
                .addData("y", gamepad1.left_stick_y);
        telemetry.addLine("Right joystick | ")
                .addData("x", gamepad1.right_stick_x)
                .addData("y", gamepad1.right_stick_y);
        telemetry.addData("Slow mode", slowMode);
        telemetry.update();
    }

}

