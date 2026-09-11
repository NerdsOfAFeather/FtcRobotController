package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**Created by Gavin for FTC Team 6347 */
@TeleOp(name="DecodeTeleOp", group="Ayyyy this makes it be at the top of the list")
public class DecodeTeleOp extends DecodeConfig {

    private final ElapsedTime runtime = new ElapsedTime();
    double axial;
    double lateral;
    double yaw;
    boolean slowMode;
    boolean inverted;
    int outputPos = 0;
    boolean outputManual = true;
    boolean lastDown = false;
    double flywheelPowerMultiplier = 1.0;

    @Override
    public void init() {
        initDriveHardware();
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
        // double intakePower;
        double outputPower;
        double flywheelVelocity;

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

        if (inverted) {
            leftFrontPower = -leftFrontPower;
            rightFrontPower = -rightFrontPower;
            leftBackPower = -leftBackPower;
            rightBackPower = -rightBackPower;
        }

        // 180 deg = 120 ticks
        // Initial = 0 deg
        // 2nd = 90 deg
        // Final = 270 deg
//        if (gamepad2.a) {
//            outputPos = nextAvailable(outputMotor, 0);
//            outputManual = false;
//            outputPower = 0.25;
//        } else if (gamepad2.b) {
//            outputPos = nextAvailable(outputMotor, 60);
//            outputManual = false;
//            outputPower = 0.25;
//        } else if (gamepad2.y) {
//            outputPos = nextAvailable(outputMotor, 60);
//            outputManual = false;
//            outputPower = 0.25;
//        } else
        if (Math.abs(gamepad2.right_stick_y) >= 0.2) {
            outputManual = true;
            outputPower = -gamepad2.right_stick_y / 16;
        } else if (outputManual) {
            outputPower = 0;
        } else {
            outputPower = .25;
        }

        if (gamepad2.dpad_down) {
            if (!lastDown) {
                flywheelPowerMultiplier -= 0.1;
                if (flywheelPowerMultiplier <= 0.0) {
                    flywheelPowerMultiplier = 1.0;
                }
            }
            lastDown = true;
        } else {
            lastDown = false;
        }

        if (gamepad2.left_stick_y >= 0.2) { // Down
            flywheelVelocity = gamepad2.left_stick_y * 10_000 * flywheelPowerMultiplier;
        } else if (gamepad2.left_stick_y <= -0.2) { // Up
            flywheelVelocity = -gamepad2.left_stick_y * 10_500 * flywheelPowerMultiplier;
        } else {
            flywheelVelocity = 0;
        }

        if (outputManual) {
            outputMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else {
            outputMotor.setTargetPosition(outputPos);
            outputMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }

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

        outputMotor.setPower(outputPower);
        flywheelLeft.setVelocity(flywheelVelocity);
        flywheelRight.setVelocity(flywheelVelocity);

        // Show the elapsed game time and wheel power.
        telemetry.addData("Left Trigger", gamepad1.left_trigger);
        telemetry.addData("Right Trigger", gamepad1.right_trigger);
        telemetry.addData("Run Time", runtime.toString());
        telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
        telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
        //telemetry.addData("Intake Power", intakePower);
        telemetry.addData("EncoderRight", rightBackDrive.getCurrentPosition());
        telemetry.addData("EncoderCenter", leftBackDrive.getCurrentPosition());
        telemetry.addData("EncoderLeft", rightFrontDrive.getCurrentPosition());
        telemetry.addData("Output Encoder", outputMotor.getCurrentPosition());
        telemetry.addData("FlyLeft Encoder", flywheelLeft.getCurrentPosition());
        telemetry.addData("FlyRight Encoder", flywheelRight.getCurrentPosition());
        telemetry.addData("Output Power", outputPower);
        telemetry.addData("Flywheel Velocity", flywheelVelocity);
        telemetry.addData("Flywheel Power Multiplier", flywheelPowerMultiplier);
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
