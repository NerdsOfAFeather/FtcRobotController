/* Copyright (c) 2021 FIRST. All rights reserved.
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


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by Gavin for Team 6347
 */
@TeleOp(name = "PowerPlayTeleOp1P", group = "Linear Opmode")
public class PowerPlayTeleOp1P extends PowerPlayConfig {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();

    public double axial;
    public double lateral;
    public double yaw;
    public boolean slowMode;
    public double liftPressTime = 0;

    @Override
    public void init() {
        initDriveHardware();
        initLift();
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
        double liftLiftPower;
        if (gamepad1.a && !slowMode) {
            slowMode = true;
        } else if (gamepad1.a && slowMode) {
            slowMode = false;
        }
        if (slowMode) {

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            if (Math.abs(gamepad1.left_stick_y) >= 0.3) {
                axial = -gamepad1.left_stick_y / 2;  // Note: pushing stick forward gives negative value
            } else {
                axial = 0;
            }
            if (Math.abs(gamepad1.left_stick_x) >= 0.3) {
                lateral = gamepad1.left_stick_x / 2;
            } else {
                lateral = 0;
            }
            if (Math.abs(gamepad1.right_stick_x) >= 0.3) {
                yaw = gamepad1.right_stick_x / 2;
            } else {
                yaw = 0;
            }
        } else {
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

            /*
            leftFrontPower  = gamepad1.x ? 1.0 : 0.0;  // X gamepad
            leftBackPower   = gamepad1.a ? 1.0 : 0.0;  // A gamepad
            rightFrontPower = gamepad1.y ? 1.0 : 0.0;  // Y gamepad
            rightBackPower  = gamepad1.b ? 1.0 : 0.0;  // B gamepad
            */

        if (runtime.milliseconds() - liftPressTime >= 250 || runtime.milliseconds() < 250) {
            if (gamepad1.right_bumper) {
                liftPressTime = runtime.milliseconds();
                if (desiredLiftPosition == -2) {
                    desiredLiftPosition = 0;
                }
                if (desiredLiftPosition >= 2) {
                    desiredLiftPosition = 2;
                }
                desiredLiftPosition++;
            } else if (gamepad1.left_bumper) {
                liftPressTime = runtime.milliseconds();
                if (desiredLiftPosition <= 1) {
                    desiredLiftPosition = 1;
                }
                desiredLiftPosition--;
            }
        }

        if (gamepad1.dpad_up) {
            desiredLiftPosition = -2;
            liftLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            liftLiftPower = -1;
            if (liftLiftMotor.getCurrentPosition() < lvl3 - 500) {
                liftLiftPower = 0;
            }
        } else if (gamepad1.dpad_down){
            desiredLiftPosition = -2;
            liftLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            liftLiftPower = 1;
            if (liftLiftMotor.getCurrentPosition() > lvl0) {
                liftLiftPower = 0;
            }
        } else if (desiredLiftPosition != -2) {
            liftLiftMotor.setTargetPosition(desiredLift());
            liftLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            liftLiftPower = 1.0;
        } else {
            liftLiftPower = 0.0;
        }

        if (gamepad1.left_trigger >= 0.4) {
            leftClawServo.setPosition(1.0);
            rightClawServo.setPosition(0.0);
        }
        if (gamepad1.right_trigger >= 0.4) {
            leftClawServo.setPosition(0.0);
            rightClawServo.setPosition(1.0);
        }

        // Send calculated power to wheels
        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);
        liftLiftMotor.setPower(liftLiftPower);

        // Show the elapsed game time and wheel power.
        telemetry.addData("Left Trigger", gamepad1.left_trigger);
        telemetry.addData("Right Trigger", gamepad1.right_trigger);
        telemetry.addData("Left Claw Position", leftClawServo.getPosition());
        telemetry.addData("Right Claw Position", rightClawServo.getPosition());
        telemetry.addData("Run Time: ", runtime.toString());
        telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
        telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
        // Show joystick information as some other illustrative data
        telemetry.addLine("Left joystick | ")
                .addData("x", gamepad1.left_stick_x)
                .addData("y", gamepad1.left_stick_y);
        telemetry.addLine("Light joystick | ")
                .addData("x", gamepad1.right_stick_x)
                .addData("y", gamepad1.right_stick_y);
        telemetry.addData("Slow mode", slowMode);
        telemetry.update();

    }
}


