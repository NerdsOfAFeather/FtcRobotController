package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

/**Created by Gavin for FTC Team 6347 */
@TeleOp(name="CenterStageTeleOp", group="OpMode")
public class CenterStageTeleOp extends CenterStageConfig {

    private ElapsedTime runtime = new ElapsedTime();
    double axial;
    double lateral;
    double yaw;
    boolean slowMode;
    double clawLPos = 0.5;
    double clawRPos = 0.5;
    double clawLtime = 0;
    double clawRtime = 0;
    boolean servoLOpen = false;
    boolean servoROpen = false;

    @Override
    public void init() {
        initDriveHardware();
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
        double intakePower2;
        double liftPower;
        double wristPower;

        if (gamepad1.right_bumper && !slowMode){
            slowMode = true;
        } else if (gamepad1.left_bumper && slowMode){
            slowMode = false;
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

        if (gamepad1.left_trigger >= 0.3 && runtime.milliseconds() - clawLtime > 500) {
            if (servoLOpen) {
                clawLPos = 0.5; // Close
                servoLOpen = false;
            } else {
                clawLPos = 1.0;
                servoLOpen = true;
            }
            clawLtime = runtime.milliseconds();
        }
        if (gamepad1.right_trigger >= 0.3 && runtime.milliseconds() - clawRtime > 500) {
            if (servoROpen) {
                clawRPos = 0.5; // Close
                servoROpen = false;
            } else {
                clawRPos = 0.0;
                servoROpen = true;
            }
            clawRtime = runtime.milliseconds();
        }

        if (gamepad2.dpad_up) {
            wristPower = 1;
        } else if (gamepad2.dpad_down) {
            wristPower = -1;
        } else {
            wristPower = 0;
        }

        if (Math.abs(gamepad2.left_stick_y) >= 0.2) { // Up =  Down = 0
            intakePower = Math.pow(-gamepad2.left_stick_y, 2);
            if (gamepad2.left_stick_y < 0) {
                intakePower = -intakePower;
            }
        } else {
            intakePower = 0;
        }

        if (Math.abs(gamepad2.right_stick_y) >= 0.2) { // Up = 0, Down = 560, Backdrop value =
            intakePower2 = Math.pow(gamepad2.right_stick_y*.6, 2);
            if (gamepad2.right_stick_y < 0) {
                intakePower2 = -intakePower2;
            }
        } else {
            intakePower2 = 0;
        }

        if (gamepad2.right_bumper) {
            liftPower = 1;
        } else if (gamepad2.left_bumper) {
            liftPower = -1;
        } else {
            liftPower = 0;
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
        intakeMotor2.setPower(intakePower2);
        liftMotor.setPower(liftPower);
        clawServoL.setPosition(clawLPos);
        clawServoR.setPosition(clawRPos);
        wristServo.setPower(wristPower);

        // Show the elapsed game time and wheel power.
        telemetry.addData("Left Trigger", gamepad1.left_trigger);
        telemetry.addData("Right Trigger", gamepad1.right_trigger);
        telemetry.addData("Run Time", runtime.toString());
        telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
        telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
        telemetry.addData("Intake Power", intakePower);
        telemetry.addData("EncoderRight", rightBackDrive.getCurrentPosition());
        telemetry.addData("EncoderCenter", leftFrontDrive.getCurrentPosition());
        telemetry.addData("EncoderLeft", rightFrontDrive.getCurrentPosition());
        telemetry.addData("intakeMotor", intakeMotor.getCurrentPosition());
        telemetry.addData("intakeMotor2", intakeMotor2.getCurrentPosition());
        telemetry.addData("Lift Motor", liftMotor.getCurrentPosition());
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
