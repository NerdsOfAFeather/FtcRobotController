/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.stampede.Stampede;

@TeleOp(name = "StampedeEnhancedTeleOp")
public class StampedeEnhancedTeleOp extends IntoTheDeepConfig {

    /* Declare OpMode members. */
    Stampede stampede;
    double x1, y1, x2;
    double speedfactor = 0.75;
    double driveAngle = 0;
    double driveAngleCheckTime = 0;
    ElapsedTime runtime = new ElapsedTime();

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

    public void initRobot() {
        stampede = new Stampede();
        initAttachmentHardware();
    }

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        initRobot();
        stampede.init(hardwareMap);
        // You can set the robot's starting orientation
        stampede.angleTracker.setOrientation(180);

        telemetry.addData("Bingus", "Bongus");
        telemetry.update();
    }

    @Override
    public void start() {
        runtime.reset();
    }

    @Override
    public void loop() {

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

        //turn correcting
        if (Math.abs(gamepad1.left_stick_y) > .2) {
            y1 = -gamepad1.left_stick_y;
        }
        if (Math.abs(gamepad1.left_stick_x) > .2) {
            x1 = gamepad1.left_stick_x;
        }
        boolean corrected = false;
        if (Math.abs(gamepad1.right_stick_x) > .2) {
            // are we turning?  If so, remember our current heading
            x2 = gamepad1.right_stick_x;
            driveAngle = stampede.angleTracker.getOrientation();
            driveAngleCheckTime = getRuntime() + 0.25;

        } else if (Math.abs(gamepad1.left_stick_x) > .2 || Math.abs(gamepad1.left_stick_y) > .2 &&
                getRuntime() > driveAngleCheckTime) {
            // we aren't turning, but we are moving.  Rotate back to the original heading when we started moving
            if (Math.abs(stampede.angleDifference(stampede.angleTracker.getOrientation(), driveAngle)) > 0.5) {
                x2 = stampede.angleDifference(stampede.angleTracker.getOrientation(), driveAngle) / 25;
                corrected = true;
            }
        } else {
            // we aren't moving at all, note which way we are facing
            driveAngle = stampede.angleTracker.getOrientation();
        }
        if (gamepad1.left_trigger > .4) {
            speedfactor = 0.25;
        } else if (gamepad1.right_trigger > .2) {
            speedfactor = 1;
        }
        x1 *= speedfactor;
        y1 *= speedfactor;
        x2 *= speedfactor;
        stampede.drive(reversedControls ? -y1 : y1, reversedControls ? -x1 : x1, x2, telemetry);
        telemetry.addData("Autoturning Active", corrected ? "Yes" : "No");

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
            boolean canMove = fArmMotor.getCurrentPosition() <= 1200;
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
        telemetry.addData("Front left/Right", "%4.2f, %4.2f", stampede.driveFrontLeft.getPower(), stampede.driveFrontRight.getPower());
        telemetry.addData("Back  left/Right", "%4.2f, %4.2f", stampede.driveRearLeft.getPower(), stampede.driveRearRight.getPower());
        telemetry.addData("EncoderRight", stampede.odopodRight.getCurrentPosition());
        telemetry.addData("EncoderCenter", stampede.odopodMiddle.getCurrentPosition());
        telemetry.addData("EncoderLeft", stampede.odopodLeft.getCurrentPosition());
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


        stampede.updateFieldPosition();
        stampede.reportTelemetry(telemetry);
        telemetry.update();
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }
}