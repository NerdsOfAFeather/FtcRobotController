package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**Created by Gavin for FTC Team 6347 */
@TeleOp(name="CenterStageTeleOp", group="OpMode")
public class CenterStageTeleOp extends CenterStageConfig {

    private ElapsedTime runtime = new ElapsedTime();

    double axial;
    double lateral;
    double yaw;
    boolean slowMode;
    boolean servoIndependence;
    double flipperPos;
    double clawLPos = 0;
    double clawRPos = 0.5;

    @Override
    public void init() {
        initDriveHardware();
        telemetry.addData("Bingus", "Bongus");
        telemetry.update();
    }

    @Override
    public void start() {
        runtime.reset();
        clawLPos = 0.5;
        clawRPos = 0.5;
        flipperPos = 0.85;
    }

    @Override
    public void loop() {
        double max;
        double leftFrontPower;
        double rightFrontPower;
        double leftBackPower;
        double rightBackPower;
        double intakePower;
        double liftPower;
        double liftPowerL;
        double liftPowerR;

        if (gamepad1.right_bumper && !slowMode){
            slowMode = true;
        } else if (gamepad1.left_bumper && slowMode){
            slowMode = false;
        }

        if (gamepad2.right_bumper) {
            servoIndependence = true;
        } else if (gamepad2.left_bumper) {
            servoIndependence = false;
        }

        // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
        if (Math.abs(gamepad1.left_stick_y) >= 0.3) {
            axial = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
        } else {
            axial = 0;
        }
        if (Math.abs(gamepad1.left_stick_x) >= 0.3) {
            lateral = gamepad1.left_stick_x;
        } else {
            lateral = 0;
        }
        if (Math.abs(gamepad1.right_stick_x) >= 0.3) {
            yaw = gamepad1.right_stick_x;
        } else {
            yaw = 0;
        }

        if (slowMode) {
            axial /= 2;
            lateral /= 2;
            yaw /= 2;
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

        // NOTE: -1.0 is UP towards the backdrop; 1.0 is DOWN towards the robot
        if (gamepad2.a) {
            flipperPos += 0.005;
            //flipperPos = 0.85;
        } else if (gamepad2.y) {
            //flipperPos = 0.0;
            flipperPos -= 0.005;
        }

        if (gamepad2.right_trigger >= 0.3) {
            clawLPos = 1.0;
            clawRPos = 0.0;
        } else if (gamepad2.left_trigger >= 0.3) {
            clawLPos = 0.5;
            clawRPos = 0.5;
        }

        if (!servoIndependence) {

            if (Math.abs(gamepad2.left_stick_y) >= 0.3) {
                intakePower = (-gamepad2.left_stick_y)/2;
            } else {
                intakePower = 0;
            }


            if (Math.abs(gamepad2.right_stick_y) >= 0.3) {
                liftPower = -gamepad2.right_stick_y;
            } else {
                liftPower = 0;
            }

            liftPowerL = liftPower;
            liftPowerR = liftPower;

        } else {

            if (Math.abs(gamepad2.left_stick_y) >= 0.3) {
                liftPowerL = (-gamepad2.left_stick_y)/2;
            } else {
                liftPowerL = 0;
            }

            if (Math.abs(gamepad2.right_stick_y) >= 0.3) {
                liftPowerR = -gamepad2.right_stick_y;
            } else {
                liftPowerR = 0;
            }

            intakePower = 0;

        }

        if (flipperPos > 1) {
            flipperPos = 1;
        } else if (flipperPos < 0) {
            flipperPos = 0;
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
        intakeMotor.setPower(intakePower);
        clawServoL.setPosition(clawLPos);
        clawServoR.setPosition(clawRPos);
        flipperServo.setPosition(flipperPos);
        extensionServo.setPower(liftPowerL);
        retractionServo.setPower(liftPowerR);

        // Show the elapsed game time and wheel power.
        telemetry.addData("Left Trigger", gamepad1.left_trigger);
        telemetry.addData("Right Trigger", gamepad1.right_trigger);
        telemetry.addData("Run Time", runtime.toString());
        telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
        telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
        telemetry.addData("Intake Power", intakePower);
        telemetry.addData("Flipper Servo", flipperPos);
        telemetry.addData("EncoderRight", rightBackDrive.getCurrentPosition());
        telemetry.addData("EncoderCenter", leftFrontDrive.getCurrentPosition());
        telemetry.addData("EncoderLeft", rightFrontDrive.getCurrentPosition());
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
