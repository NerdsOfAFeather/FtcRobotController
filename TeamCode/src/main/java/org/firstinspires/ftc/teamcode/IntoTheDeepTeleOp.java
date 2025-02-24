package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**Created by Gavin for FTC Team 6347 */
@TeleOp(name="IntoTheDeepTeleOp", group="OpMode")
//@Disabled
public class IntoTheDeepTeleOp extends IntoTheDeepConfig {

    private final ElapsedTime runtime = new ElapsedTime();
    double axial;
    double lateral;
    double yaw;
    boolean slowMode;
    boolean lastRb = false;
    boolean reversedControls = false;
    boolean lastY = false;
    boolean overrideNoLift;
    double rearLiftPower = 0.0;
    double handoffTime = -1.0;
    double manualFrontClawOffsetTime = 0.0;
    boolean tryingHandoff = false;
    boolean autoMovingLift = false;
    boolean offsetForDeposit = false;
    boolean lastX = false;
    double rearElbowPos2 = 0.0;

    final double SPEED_MULTIPLIER = 0.75;

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

        // Divides the wheel speed in half (or doubles it, depends on how you look at it)
        if (gamepad1.right_bumper && !lastRb) {
            slowMode = !slowMode;
        }

        // Overrides the encoder limits on the rear lift motor
        if (gamepad2.right_bumper && !overrideNoLift) {
            overrideNoLift = true;
        } else if (gamepad2.left_bumper && overrideNoLift) {
            overrideNoLift = false;
        }

        if (gamepad1.y && !lastY) {
            reversedControls = !reversedControls;
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

        if (reversedControls) {
            axial = -axial;
            lateral = -lateral;
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

        leftFrontPower *= SPEED_MULTIPLIER;
        rightFrontPower *= SPEED_MULTIPLIER;
        leftBackPower *= SPEED_MULTIPLIER;
        rightBackPower *= SPEED_MULTIPLIER;

        // Apply slowMode
        if (slowMode) {
            leftFrontPower /= 2;
            rightFrontPower /= 2;
            leftBackPower /= 2;
            rightBackPower /= 2;
        }

        // Toggle front claw position (with a .5 second delay between inputs)
        // TODO: Replace these timeouts with a was last pressed but still keep track of the time just in case
        if (gamepad1.right_trigger >= 0.3 && runtime.milliseconds() - frontClawTime >= 500) {
            frontClaw = toggle(frontClaw);
            frontClawTime = runtime.milliseconds();
        }

        // Toggle front wrist position (with a .5 second delay between inputs)
        if (gamepad1.left_trigger >= 0.3 && runtime.milliseconds() - frontWristTime >= 500) {
            // TODO: Add check for if we've picked up a sample correctly
            if (frontArm.wristPos == FrontArm.WRIST_DOWN.wristPos) {
                frontArm = FrontArm.RETRACTED;
            } else {
                frontArm = FrontArm.WRIST_DOWN;
            }
            frontWristTime = runtime.milliseconds();
        }

        // Toggle rear claw position (with .5 second delay between inputs)
        if (gamepad2.right_trigger >= 0.3 && runtime.milliseconds() - rearClawTime > 500) {
            rearClaw = toggle(rearClaw);
            rearClawTime = runtime.milliseconds();
        }

        // Front Arm Extension Logic
        if (gamepad1.dpad_up) {
            boolean canMove = fArmMotor.getCurrentPosition() <= 1300;
            rue(fArmMotor);
            if (canMove || overrideNoLift) {
                fArmMotor.setPower(1.0);
            } else {
                fArmMotor.setPower(0.0);
            }
        } else if (gamepad1.dpad_down) {
            boolean canMove = fArmMotor.getCurrentPosition() >= 0;
            rue(fArmMotor);
            if (canMove || overrideNoLift) {
                fArmMotor.setPower(-1.0);
            } else {
                fArmMotor.setPower(0.0);
            }
        } else if (gamepad1.dpad_left) {
            fArmMotor.setTargetPosition(0);
            fArmMotor.setPower(1.0);
            rtp(fArmMotor);
        } else if (fArmMotor.getMode() == DcMotor.RunMode.RUN_USING_ENCODER ||
                fArmMotor.getCurrentPosition() == fArmMotor.getTargetPosition()) {
            fArmMotor.setPower(0.0);
        }

        // Rear arm height logic
        if (Math.abs(gamepad2.right_stick_y) > 0.2) {
            autoMovingLift = false;
            rue(rearLiftMotor);
            boolean canMove; // Down - 0, Basket 1/top rung - -2180
            if (gamepad2.right_stick_y > 0.0) {
                canMove = rearLiftMotor.getCurrentPosition() >= RearLift.IDLE.motorPos;
            } else {
                canMove = rearLiftMotor.getCurrentPosition() <= RearLift.HIGH.motorPos;
            }
            if (canMove || overrideNoLift) {
                rearLiftPower = -gamepad2.right_stick_y;
            } else {
                rearLiftPower = 0.0;
            }
        } else if (gamepad2.dpad_down) {
            if (gamepad2.back) {
                rearLiftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            }
            rearArm = RearArm.IN_ROBOT;
            autoMovingLift = true;
        } else if (gamepad2.a) {
            rearArm = RearArm.PICKUP_SPEC;
            autoMovingLift = true;
        } else if (gamepad2.dpad_left) {
            rearArm = RearArm.DEPOSIT_HIGH_SPEC;
            autoMovingLift = true;
        } else if (gamepad2.b && !gamepad2.start) {
            rearArm = RearArm.DEPOSIT_LOW_SPEC;
            autoMovingLift = true;
        } else if (gamepad2.dpad_up) {
            rearArm = RearArm.DEPOSIT_SAMPLE;
            autoMovingLift = true;
        } else if (!autoMovingLift) {
            rearLiftPower = 0;
        }
        if (autoMovingLift) {
             rearLiftMotor.setTargetPosition(rearArm.liftHeight.motorPos);
             rearLiftPower = 1.0;
             rtp(rearLiftMotor);
        }
        if (gamepad2.x && !lastX) {
            offsetForDeposit = !offsetForDeposit;
        }

        // Handoff Logic
        if (gamepad2.y) {
            tryingHandoff = !tryingHandoff;
        }
        // TODO: Add a button cache-ing system that would allow for the drivers to hit (for example) y,
        //  then dpad up, and the lift would automatically extend after it was done with the handoff

        boolean frontClawPositionSet = frontClaw == ClawState.CLOSED;
        boolean frontClawInPosition = frontClawPositionSet && runtime.milliseconds() - frontClawTime >= 500;
        boolean frontWristPositionSet = fWrist.getPosition() == FrontArm.RETRACTED.wristPos;
        boolean frontWristInPosition = frontWristPositionSet && runtime.milliseconds() - frontWristTime >= 750;
        boolean frontArmPositionSet = fArmMotor.getTargetPosition() == 0;
        boolean frontArmInPosition = fArmMotor.getCurrentPosition() <= 0;

        boolean rearClawPositionSet = rearClaw == ClawState.OPEN;
        boolean rearClawInPosition = rearClawPositionSet && runtime.milliseconds() - rearClawTime >= 500;
        boolean rearArmInPosition = rearArmServoPos == 1.0; // TODO: Make a timer here
        boolean rearLiftPositionSet = rearLiftMotor.getTargetPosition() == RearLift.IDLE.motorPos;
        boolean rearLiftInPosition = rearLiftMotor.getCurrentPosition() <= RearLift.IDLE.motorPos;

        boolean everythingInPlace = frontClawInPosition && frontWristInPosition && frontArmInPosition
                && rearClawInPosition && rearArmInPosition && rearLiftInPosition;


        if (tryingHandoff) {
            if (handoffTime == -1.0) {

                if (!rearClawPositionSet) {
                    rearClaw = ClawState.OPEN;
                    rearClawTime = runtime.milliseconds();
                } else if (!rearArmInPosition) {
                    rearArmServoPos = 1.0;
                } else if (!rearLiftPositionSet) {
                    rearLiftMotor.setTargetPosition(RearLift.IDLE.motorPos);
                    rearLiftPower = 1.0;
                    rtp(rearLiftMotor);
                }
                if (!frontClawPositionSet) {
                    frontClaw = ClawState.CLOSED;
                    frontClawTime = runtime.milliseconds();
                } else if (!frontWristPositionSet) {
                    frontArm = FrontArm.RETRACTED;
                    frontWristTime = runtime.milliseconds();
                } else if (!frontArmPositionSet) {
                    fArmMotor.setTargetPosition(0);
                    fArmMotor.setPower(1.0);
                    rtp(fArmMotor);
                }
                if (everythingInPlace) {
                    fArmMotor.setPower(0.0);
                    rearLiftPower = 0.0;
                    handoffTime = runtime.milliseconds();
                }
            } else {
                double diff = runtime.milliseconds() - handoffTime;
                if (diff <= 500) {
                    rearClaw = ClawState.CLOSED;
                } else if (diff <= 1000) {
                    frontClaw = ClawState.OPEN;
                } else if (diff <= 1200) {
                    frontArm = FrontArm.WRIST_DOWN;
                } else if (diff <= 1500) {
                    rearArmServoPos = 0.7;
                } else if (diff <= 1700) {
                    frontArm = FrontArm.RETRACTED;
                } else {
                    tryingHandoff = false;
                    handoffTime = -1.0;
                }
            }
        }

        // Set drive motor powers
        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);

        if (gamepad1.left_bumper) {
            manualFrontClawOffsetTime = runtime.milliseconds();
        }

        // Adapt the front claw position if all of the following conditions are met:
        // 1. The front wrist is being moved to the up position
        // 2. It has been more than .7 seconds since the 'move up' command was given
        // 3. It has been less than .78 seconds since the 'move up' command was given
        double fClawLPos = frontClaw.flPos;
        double fClawRPos = frontClaw.frPos;
        double fWTimeDiff = runtime.milliseconds() - frontWristTime;
        boolean shouldOffset = fWrist.getPosition() == FrontArm.RETRACTED.wristPos && fWTimeDiff > 800 && fWTimeDiff < 900;
        if (frontClaw != ClawState.CLOSED) shouldOffset = false;
        if (shouldOffset || runtime.milliseconds() - manualFrontClawOffsetTime <= 100.0) {
            fClawLPos -= ClawState.ADAPT_OFFSET;
            fClawRPos += ClawState.ADAPT_OFFSET;
        }
        double rearElbowOffset = offsetForDeposit ? 0.1 : 0.00;
        rearElbowOffset += gamepad2.left_stick_y/10.0;

        if (manualFrontClawOffsetTime >= 0.0) {
            manualFrontClawOffsetTime--;
        }

        double rearElbowPos = rearArm.elbowPos - rearElbowOffset;
        if (rearArm == RearArm.DEPOSIT_HIGH_SPEC && rearLiftMotor.getCurrentPosition() >= rearLiftMotor.getTargetPosition()) {
            rearElbowPos = RearArm.PICKUP_SPEC.elbowPos;
        }

        rearElbowPos2 += gamepad2.left_stick_x /100;

        // Set attachment actuators powers/positions
        fClawL.setPosition(fClawLPos);
        fClawR.setPosition(fClawRPos);
        rClawL.setPosition(rearClaw.blPos);
        rClawR.setPosition(rearClaw.brPos);
        fWrist.setPosition(frontArm.wristPos);
        rearWrist.setPosition(rearArm.wristPos);
        rearArmServo.setPosition(rearElbowPos);
        rearLiftMotor.setPower(rearLiftPower);

        lastX = gamepad2.x;
        lastRb = gamepad1.right_bumper;
        lastY = gamepad1.y;

        // Show telemetry to the DS
        telemetry.addData("Wrist Position", fWrist.getPosition());
        telemetry.addData("Time diff", fWTimeDiff);
        telemetry.addData("Should Offset", shouldOffset);
        telemetry.addData("Claw Left Pos", fClawLPos);
        telemetry.addData("Claw Right Pos", fClawRPos);
        telemetry.addData("Left Trigger", gamepad1.left_trigger);
        telemetry.addData("Right Trigger", gamepad1.right_trigger);
        telemetry.addData("Trying Handoff", tryingHandoff);
        telemetry.addData("Run Time", runtime.toString());
        telemetry.addData("Back Claw", rearClaw);
        telemetry.addData("Rear Arm", rearArm);
        telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
        telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
        telemetry.addData("EncoderRight", rightBackDrive.getCurrentPosition());
        telemetry.addData("EncoderCenter", leftFrontDrive.getCurrentPosition());
        telemetry.addData("EncoderLeft", leftBackDrive.getCurrentPosition());
        telemetry.addData("ArmExtension", fArmMotor.getCurrentPosition());
        telemetry.addData("VerticalArm", rearLiftMotor.getCurrentPosition());
        telemetry.addLine("Left joystick | ")
                .addData("x", gamepad1.left_stick_x)
                .addData("y", gamepad1.left_stick_y);
        telemetry.addLine("Right joystick | ")
                .addData("x", gamepad1.right_stick_x)
                .addData("y", gamepad1.right_stick_y);
        telemetry.addLine("Back Left joystick | ")
                .addData("x", gamepad2.left_stick_x)
                .addData("y", gamepad2.left_stick_y);
        telemetry.addLine("Back Right joystick | ")
                .addData("x", gamepad2.right_stick_x)
                .addData("y", gamepad2.right_stick_y);
        telemetry.addData("Slow mode", slowMode);
        telemetry.addData("frontClawPositionSet", frontClawPositionSet);
        telemetry.addData("frontClawInPosition", frontClawInPosition);
        telemetry.addData("frontWristPositionSet", frontWristPositionSet);
        telemetry.addData("frontWristInPosition", frontWristInPosition);
        telemetry.addData("frontArmPositionSet", frontArmPositionSet);
        telemetry.addData("frontArmInPosition", frontArmInPosition);

        telemetry.addData("rearClawPositionSet", rearClawPositionSet);
        telemetry.addData("rearClawInPosition", rearClawInPosition);
        telemetry.addData("rearArmInPosition", rearArmInPosition);
        telemetry.addData("rearLiftPositionSet", rearLiftPositionSet);
        telemetry.addData("rearLiftInPosition", rearLiftInPosition);

        telemetry.addData("everythingInPlace", everythingInPlace);
        telemetry.addData("handoffTime", handoffTime);
        telemetry.addData("rearArmServoPos", rearArmServoPos);
        telemetry.addData("rearElbowPOS2", rearElbowPos2);

        telemetry.update();
    }

}
