package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

/**Created by Gavin for FTC Team 6347*/
@TeleOp(name = "IntoTheDeepTeleOp2P", group = "Linear Opmode")
//@Disabled
public class IntoTheDeepTeleOp2P extends IntoTheDeepConfig {

    private ElapsedTime runtime = new ElapsedTime();
    double axial;
    double lateral;
    double yaw;
    boolean slowMode;
    boolean overrideNoLift;
    double time;
    boolean old;
    double finalTime = 0;

    @Override
    public void init() {
        initDriveHardware();
        initFrontArm();
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

        if (gamepad1.right_bumper && !slowMode){
            slowMode = true;
        } else if (gamepad1.left_bumper && slowMode){
            slowMode = false;
        }

        if (gamepad2.right_bumper && !overrideNoLift){
            overrideNoLift = true;
        } else if (gamepad2.left_bumper && overrideNoLift){
            overrideNoLift = false;
        }

        // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
        if (Math.abs(gamepad1.left_stick_y) >= 0.2) {
            axial = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
        } else {
            axial = 0;
        }
        if (Math.abs(gamepad1.left_stick_x) >= 0.2) {
            lateral = -gamepad1.left_stick_x;
        } else {
            lateral = 0;
        }
        if (Math.abs(gamepad1.right_stick_x) >= 0.2) {
            yaw = gamepad1.right_stick_x;
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

        if (gamepad2.right_trigger >= 0.3 && runtime.milliseconds() - frontClawTime >= 500) {
            frontClaw = toggle(frontClaw);
            frontClawTime = runtime.milliseconds();
        }

        if (gamepad2.left_trigger >= 0.3 && runtime.milliseconds() - frontWristTime >= 500) {
            if (fWrist.getPosition() == 1.0) {
                fWrist.setPosition(0.2);
            } else {
                fWrist.setPosition(1.0);
            }
            frontWristTime = runtime.milliseconds();
        }

        // fArmExtension.setPower(gamepad2.left_stick_x);

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

        if (gamepad2.dpad_left) {
            if (!old) {
                time = runtime.milliseconds();
            }
            fArmMotor.setPower(-1.0);
            old = true;
        } else if (gamepad2.dpad_right) {
            if (!old) {
                time = runtime.milliseconds();
            }
            fArmMotor.setPower(1.0);
            old = true;
        } else {
            if (old) finalTime = runtime.milliseconds() - time;
            fArmMotor.setPower(0.0);
            old = false;
        }

        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);
        double fClawLPos = frontClaw.lPos;
        double fClawRPos = frontClaw.rPos;
        boolean shouldOffset = fWrist.getPosition() == 1.0 && runtime.milliseconds() - frontWristTime < 50;
        if (frontClaw != ClawState.CLOSED) shouldOffset = false;
        if (shouldOffset) {
            fClawLPos -= ClawState.ADAPT_OFFSET;
            fClawRPos += ClawState.ADAPT_OFFSET;
        }
        telemetry.addData("Wrist Position", fWrist.getPosition());
        telemetry.addData("Time diff", runtime.milliseconds() - frontWristTime);
        telemetry.addData("Should Offset", shouldOffset);
        telemetry.addData("Claw Left Pos", fClawLPos);
        telemetry.addData("Claw Right Pos", fClawRPos);
        telemetry.addData("Time", runtime.milliseconds() - time);
        telemetry.addData("Final Time", finalTime);
        fClawL.setPosition(fClawLPos);
        fClawR.setPosition(fClawRPos);

        // Show the elapsed game time and wheel power.
        telemetry.addData("Left Trigger", gamepad1.left_trigger);
        telemetry.addData("Right Trigger", gamepad1.right_trigger);
        telemetry.addData("Run Time", runtime.toString());
        telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
        telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
        telemetry.addData("EncoderRight", rightBackDrive.getCurrentPosition());
        telemetry.addData("EncoderCenter", leftFrontDrive.getCurrentPosition());
        telemetry.addData("EncoderLeft", leftBackDrive.getCurrentPosition());
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

    @Override
    public void stop() {}
}
