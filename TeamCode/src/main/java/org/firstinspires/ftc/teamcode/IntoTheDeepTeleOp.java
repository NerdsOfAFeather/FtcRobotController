package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**Created by Gavin for FTC Team 6347 */
@TeleOp(name="IntoTheDeepTeleOp", group="OpMode")
public class IntoTheDeepTeleOp extends IntoTheDeepConfig {

    private ElapsedTime runtime = new ElapsedTime();
    double axial;
    double lateral;
    double yaw;
    boolean slowMode;
    boolean overrideNoLift;
    int switchTimeout = 0;
    boolean switching = false;
    double clawLPos = 0.5;
    double clawRPos = 0.5;
    double clawLtime = 0;
    double clawRtime = 0;
    boolean servoLOpen = false;
    boolean servoROpen = false;

    @Override
    public void init() {
        initDriveHardware();
        initAttachmentHardware();
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
        double rearArmPower;
        double liftPower;

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

        if (gamepad1.left_trigger >= 0.3 && runtime.milliseconds() - frontClawTime > 500) {
            frontClaw = toggle(frontClaw);
            frontClawTime = runtime.milliseconds();
        }
        if (gamepad1.right_trigger >= 0.3 && runtime.milliseconds() -rearClawTime > 500) {
            rearClaw = toggle(rearClaw);
            rearClawTime = runtime.milliseconds();
        }

        if (gamepad2.a) {
            frontArm = FrontArm.EXTENDED;
        }
        if (gamepad2.b && frontArm == FrontArm.EXTENDED) {
            frontArm = FrontArm.WRIST_DOWN;
        }
        if (gamepad2.x) {
            frontArm = FrontArm.RETRACTED;
        }
        if (gamepad2.y && frontArm == FrontArm.RETRACTED) {
            // TODO: There needs to be a hasSample check somewhere here
            if (!switching) {
                switching = true;
                switchTimeout = (int) runtime.milliseconds();
                rearClaw = ClawState.CLOSED;
            }
            if (switching && runtime.milliseconds() - switchTimeout > 1000) {
                frontClaw = ClawState.OPEN;
            }
            if (runtime.milliseconds() - switchTimeout > 1500) {
                switching = false;
            }
        }

        if (gamepad2.dpad_down) {
            rearLift = RearLift.IDLE;
            rtp(rearLiftMotor);
        } else if (gamepad2.dpad_left) {
            rearLift = RearLift.LOW;
            rtp(rearLiftMotor);
        } else if (gamepad2.dpad_up) {
            rearLift = RearLift.HIGH;
            rtp(rearLiftMotor);
        }

        if (canMoveLift() || overrideNoLift) {
            if (Math.abs(gamepad2.right_stick_y) >= 0.2) { // Up = 0, Down = 560, Backdrop value =
                rearLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                liftPower = Math.pow(gamepad2.right_stick_y * .8, 2);
                if (gamepad2.right_stick_y < 0) {
                    liftPower = -liftPower;
                }
            } else if (rearLiftMotor.getMode().equals(DcMotor.RunMode.RUN_USING_ENCODER)) {
                liftPower = 0;
            } else {
                rearLiftMotor.setTargetPosition(rearLift.motorPos);
                int diff = rearLiftMotor.getCurrentPosition() - rearLiftMotor.getTargetPosition();
                if (Math.abs(diff) > 20) {
                    liftPower = 0.5;
                } else if (Math.abs(diff) > 5) {
                    liftPower = 0.25;
                } else {
                    liftPower = 0;
                }
            }
        } else {
            liftPower = 0;
        }

        if (gamepad2.right_trigger > 0.2) {
            rearArmExtended = true;
            rtp(rearArmMotor);
        } else if (gamepad2.left_trigger > 0.2) {
            rearArmExtended = true;
            rtp(rearArmMotor);
        }

        if (Math.abs(gamepad2.left_stick_x) >= 0.2) { // Up = 0, Down = 560, Backdrop value =
            rearArmMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rearArmPower = Math.pow(gamepad2.left_stick_x * .8, 2);
            if (gamepad2.left_stick_x < 0) {
                rearArmPower = -rearArmPower;
            }
        } else if (rearArmMotor.getMode().equals(DcMotor.RunMode.RUN_USING_ENCODER)) {
            rearArmPower = 0;
        } else {
            int target = rearArmExtended ? R_ARM_EXTENDED : R_ARM_RETRACTED;
            rearArmMotor.setTargetPosition(target);
            int diff = rearArmMotor.getCurrentPosition() - rearArmMotor.getTargetPosition();
            if (Math.abs(diff) > 20) {
                rearArmPower = 0.5;
            } else if (Math.abs(diff) > 5) {
                rearArmPower = 0.25;
            } else {
                rearArmPower = 0;
            }
        }

        switch (frontArm) {
            case RETRACTED: {
                if (frontClaw == ClawState.CLOSED) {
                    if (wristInPosition) {
                        fArmExtension.setPosition(frontArm.extensionPos);
                    }
                } else if (frontClaw == ClawState.OPEN) {
                    if (rearClaw != ClawState.CLOSED) {
                        frontClaw = ClawState.CLOSED;
                    }
                } // TODO: There needs to be a hasSample check somewhere here
            }
            case EXTENDED: {
                if (frontClaw == ClawState.CLOSED) {
                    if (wristInPosition) {
                        fArmExtension.setPosition(frontArm.extensionPos);
                    }
                } else if (frontClaw == ClawState.OPEN) {
                    frontClaw = ClawState.CLOSED;
                }
            }
            case WRIST_DOWN: {
                fArmExtension.setPosition(frontArm.extensionPos);
                fWrist.setPosition(frontArm.wristPos);
            }
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
        rearLiftMotor.setPower(liftPower);
        rearArmMotor.setPower(rearArmPower);
        fClawL.setPosition(frontClaw.lPos);
        fClawR.setPosition(frontClaw.rPos);

        rClawL.setPosition(rearClaw.lPos);
        rClawR.setPosition(rearClaw.rPos);

        // Show the elapsed game time and wheel power.
        telemetry.addData("Left Trigger", gamepad1.left_trigger);
        telemetry.addData("Right Trigger", gamepad1.right_trigger);
        telemetry.addData("Run Time", runtime.toString());
        telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
        telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
        telemetry.addData("Lift Power", liftPower);
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

    @Override
    public void stop() {}
}
