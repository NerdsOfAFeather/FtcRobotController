package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**Created by Gavin for FTC Team 6347*/
@TeleOp(name = "IntoTheDeepTeleOp2P", group = "Linear Opmode")
//@Disabled
public class IntoTheDeepTeleOp2P extends IntoTheDeepConfig {

    private final ElapsedTime runtime = new ElapsedTime();
    double axial;
    double lateral;
    double yaw;
    boolean slowMode;
    boolean overrideNoLift;
    double rearLiftPower = 0.0;
    double handoffTime = -1.0;
    boolean tryingHandoff = false;

    @Override
    public void init() {
        initDriveHardware();
        initFrontArm();
        initRearArm();
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
        if (gamepad1.right_bumper && !slowMode) {
            slowMode = true;
        } else if (gamepad1.left_bumper && slowMode) {
            slowMode = false;
        }

        // Overrides the encoder limits on the rear lift motor
        if (gamepad2.right_bumper && !overrideNoLift) {
            overrideNoLift = true;
        } else if (gamepad2.left_bumper && overrideNoLift) {
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

        // Apply slowMode
        if (slowMode) {
            leftFrontPower /= 2;
            rightFrontPower /= 2;
            leftBackPower /= 2;
            rightBackPower /= 2;
        }

        // Toggle front claw position (with a .5 second delay between inputs)
        if (gamepad1.right_trigger >= 0.3 && runtime.milliseconds() - frontClawTime >= 500) {
            frontClaw = toggle(frontClaw);
            frontClawTime = runtime.milliseconds();
        }

        // Toggle front wrist position (with a .5 second delay between inputs)
        if (gamepad2.left_trigger >= 0.3 && runtime.milliseconds() - frontWristTime >= 500) {
            // TODO: Add check for if we've picked up a sample correctly
            if (fWristPos == 1.0) {
                fWristPos = 0.2;
            } else {
                fWristPos = 1.0;
            }
            frontWristTime = runtime.milliseconds();
        }

        // Toggle rear claw position (with .5 second delay between inputs)
        if (gamepad2.right_trigger >= 0.3 && runtime.milliseconds() - rearClawTime > 500) {
            rearClaw = toggle(rearClaw);
            rearClawTime = runtime.milliseconds();
        }

        // Rear arm position logic
        if (gamepad2.a) {
            rearArmServoPos = 0.5; //TODO: maybe reprogram this servo?
        } else if (gamepad2.b) {
            rearArmServoPos = 0.575;
        } else if (gamepad2.x) {
            rearArmServoPos = 0.8;
        }

        // Rear wrist logic (autonomous)
        if (rearArmServoPos == 0.5) {
            rWristPos = 0.7;
        } else if (rearArmServoPos == 0.575) {
            rWristPos = 0.3;
        } else if (rearArmServoPos == 0.8) {
            rWristPos = 0.5;
        }

        // Front Arm Extension Logic
        if (Math.abs(gamepad2.left_stick_x) > 0.2) {
            rue(fArmMotor);
            fArmMotor.setPower(gamepad2.left_stick_x);
        } else if (Math.abs(gamepad2.left_stick_y) < -0.5) {
            fArmMotor.setTargetPosition(0);
            fArmMotor.setPower(1.0);
            rtp(fArmMotor);
        } else if (fArmMotor.getMode() == DcMotor.RunMode.RUN_USING_ENCODER ||
                fArmMotor.getCurrentPosition() == fArmMotor.getTargetPosition()) {
            fArmMotor.setPower(0.0);
        }

        // Rear arm height logic
        if (Math.abs(gamepad2.right_stick_y) > 0.3) {
            rue(rearLiftMotor);
            boolean canMove = rearLiftMotor.getCurrentPosition() <= R_ARM_EXTENDED && rearLiftMotor.getCurrentPosition() >= R_ARM_RETRACTED;
            if (canMove || overrideNoLift) {
                rearLiftPower = gamepad2.right_stick_y;
            }
        } else if (gamepad2.dpad_down) {
            rtp(rearLiftMotor);
            rearLiftMotor.setTargetPosition(R_ARM_RETRACTED);
            rearLiftPower = 1.0;
        } else if (gamepad2.dpad_left) {
            rtp(rearLiftMotor);
            rearLiftMotor.setTargetPosition(R_ARM_MIDDLE);
            rearLiftPower = 1.0;
        } else if (gamepad2.dpad_up) {
            rtp(rearLiftMotor);
            rearLiftMotor.setTargetPosition(R_ARM_EXTENDED);
            rearLiftPower = 1.0;
        } else if (rearLiftMotor.getMode() == DcMotor.RunMode.RUN_USING_ENCODER) {
            rearLiftPower = 0;
        }

        // Handoff Logic
        if (gamepad2.y) {
            tryingHandoff = true;
        }

        if (tryingHandoff) {
            if (handoffTime == -1.0) {
                boolean frontClawInPosition = frontClaw == ClawState.CLOSED && runtime.milliseconds() - frontClawTime >= 500;
                boolean frontWristInPosition = fWrist.getPosition() == FrontArm.RETRACTED.wristPos && runtime.milliseconds() - frontWristTime >= 500;
                boolean frontArmInPosition = fArmMotor.getCurrentPosition() == 0;

                boolean rearClawInPosition = rearClaw == ClawState.OPEN && runtime.milliseconds() - rearClawTime >= 500;
                boolean rearArmInPosition = rearArmServoPos == 0.8;
                boolean rearLiftInPosition = rearLiftMotor.getCurrentPosition() == R_ARM_RETRACTED;

                boolean everythingInPlace = frontClawInPosition && frontWristInPosition && frontArmInPosition
                        && rearClawInPosition && rearArmInPosition && rearLiftInPosition;

                if (!rearClawInPosition) {
                    rearClaw = ClawState.OPEN;
                    rearClawTime = runtime.milliseconds();
                } else if (!rearArmInPosition) {
                    rearArmServoPos = 0.8;
                } else if (!rearLiftInPosition) {
                    rtp(rearLiftMotor);
                    rearLiftMotor.setTargetPosition(R_ARM_RETRACTED);
                    rearLiftPower = 1.0;
                }
                if (!frontClawInPosition) { //TODO: Rear arm position checks
                    frontClaw = ClawState.CLOSED;
                    frontClawTime = runtime.milliseconds();
                } else if (!frontWristInPosition) {
                    fWristPos = FrontArm.RETRACTED.wristPos;
                    frontWristTime = runtime.milliseconds();
                } else if (!frontArmInPosition) {
                    fArmMotor.setTargetPosition(0);
                    fArmMotor.setPower(1.0);
                    rtp(fArmMotor);
                }
                if (everythingInPlace) {
                    handoffTime = runtime.milliseconds();
                }
            } else {
                double diff = runtime.milliseconds() - handoffTime;
                if (diff <= 500) {
                    rearClaw = ClawState.CLOSED;
                } else if (diff <= 1000) {
                    frontClaw = ClawState.OPEN;
                } else if (diff <= 1200) {
                    fWristPos = 0.7;
                } else if (diff <= 1500) {
                    rearArmServoPos = 0.7;
                } else if (diff <= 1700) {
                    fWristPos = FrontArm.RETRACTED.wristPos;
                } else {
                    tryingHandoff = false;
                }
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

        // Set drive motor powers
        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);

        // Adapt the front claw position if all of the following conditions are met:
        // 1. The front wrist is being moved to the up position
        // 2. It has been more than .7 seconds since the 'move up' command was given
        // 3. It has been less than .8 seconds since the 'move up' command was given
        double fClawLPos = frontClaw.flPos;
        double fClawRPos = frontClaw.frPos;
        double fWTimeDiff = runtime.milliseconds() - frontWristTime;
        boolean shouldOffset = fWrist.getPosition() == 1.0 && fWTimeDiff > 700 && fWTimeDiff < 800;
        if (frontClaw != ClawState.CLOSED) shouldOffset = false;
        if (shouldOffset) {
            fClawLPos -= ClawState.ADAPT_OFFSET;
            fClawRPos += ClawState.ADAPT_OFFSET;
        }

        // Set attachment actuators powers/positions
        fClawL.setPosition(fClawLPos);
        fClawR.setPosition(fClawRPos);
        rClawL.setPosition(rearClaw.blPos);
        rClawR.setPosition(rearClaw.brPos);
        fWrist.setPosition(fWristPos);
        rearWrist.setPosition(rWristPos);
        rearArmServo.setPosition(rearArmServoPos);
        rearLiftMotor.setPower(rearLiftPower);


        // Show telemetry to the DS
        telemetry.addData("Wrist Position", fWrist.getPosition());
        telemetry.addData("Time diff", fWTimeDiff);
        telemetry.addData("Should Offset", shouldOffset);
        telemetry.addData("Claw Left Pos", fClawLPos);
        telemetry.addData("Claw Right Pos", fClawRPos);
        telemetry.addData("Left Trigger", gamepad1.left_trigger);
        telemetry.addData("Right Trigger", gamepad1.right_trigger);
        telemetry.addData("Run Time", runtime.toString());
        telemetry.addData("Back Claw", rearClaw);
        telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
        telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
        telemetry.addData("EncoderRight", rightBackDrive.getCurrentPosition());
        telemetry.addData("EncoderCenter", leftFrontDrive.getCurrentPosition());
        telemetry.addData("EncoderLeft", leftBackDrive.getCurrentPosition());
        telemetry.addData("ArmExtension", fArmMotor.getCurrentPosition());
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
