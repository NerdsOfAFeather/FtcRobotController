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

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

import java.util.List;

//The code is structured as a LinearOpMode

/** Created by Gavin for Team 6347*/
@Autonomous(name = "Power Play Auto TFOD", group = "Robot")
@Disabled
public class PowerPlayAuto extends PowerPlayConfig {

    @Override
    public void runOpMode() {

        // Send telemetry message to signify robot waiting
        telemetry.addData("Status", "Ready to run");
        telemetry.update();

        initAuto();

        //Make a loop for tfod


        if (tfod != null){
            tfod.activate();
            tfod.setZoom(1.0, 16.0/9.0);
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
        }
        chooseProgram();
        waitForStart();
    }


    public void chooseProgram(){
        while (!isStarted()) {
            telemetry.addData("Please select an Auto to run", "");
            telemetry.addData( "A - Blue Left | B - Blue Right | X - Red Left | Y - Red Right", "");
            telemetry.update();
            if (gamepad1.a) {
                telemetry.addData("RobotPosition", "Blue Left");
                telemetry.update();
                sleep(500);
                blueLeft();
                break;
            } else if (gamepad1.b) {
                telemetry.addData("RobotPosition", "Blue Right");
                telemetry.update();
                sleep(500);
                //blueRight();
                break;
            } else if (gamepad1.x) {
                telemetry.addData("RobotPosition", "Red Left");
                telemetry.update();
                sleep(500);
                //redLeft();
                break;
            } else if (gamepad1.y) {
                telemetry.addData("RobotPosition", "Red Right");
                telemetry.update();
                sleep(500);
                //redRight();
                break;
            }
        }
    }

    public void blueLeft(){
        waitForStart();
        clampClose();
        goToStage(1);
        int desiredLocation = getDesiredLocation();
        driveForward(25, 3);
        if (desiredLocation == 1) { // Position 1?
            telemetry.addData("Detected", "Red");
            telemetry.update();
            driveRight(33, 10);
            goToStage(3); //All the way
            driveRight(3, 3);
            driveForwardSlow(1, 1);
            liftDown(1.0);
            clampOpen();
            driveBackwardSlow(1, 1);
            driveLeft(60, 20);
        } else if (desiredLocation == 2) { // Position 2?
            telemetry.addData("Detected", "Green");
            telemetry.update();
            driveRight(33, 10);
            goToStage(3); //All the way
            driveRight(3, 3);
            driveForwardSlow(1, 1);
            liftDown(1.0);
            clampOpen();
            driveBackwardSlow(1, 1);
            driveLeft(36, 20);
        } else { // Position 3?
            telemetry.addData("Detected", "Blue");
            telemetry.update();
            driveRight(33, 10);
            goToStage(3); //All the way
            driveRight(3, 3);
            driveForwardSlow(1, 1);
            liftDown(1.0);
            clampOpen();
            driveBackwardSlow(1, 1);
            driveLeft(6, 20);
        }

    }
/*
    public void blueRight(){
        waitForStart();
        clampClose();
        int desiredLocation = getDesiredLocation();
        goToStage(1);
        driveForward(1.5);
        if (desiredLocation == 1) { // Position 1?
            telemetry.addData("Detected", "Red");
            telemetry.update();
            driveForward(.5);
            driveBackwards(.7);
            driveLeft(2.5);
            goToStage(3); //All the way
            driveLeft(1.25);
            driveForwardSlow(.6);
            liftDown(1);
            clampOpen();
            driveBackwardSlow(.5);
            driveRight(7);
            driveForward(1);
        } else if (desiredLocation == 2) { // Position 2?
            telemetry.addData("Detected", "Green");
            telemetry.update();
            driveForward(.5);
            driveBackwards(.7);
            driveLeft(2.5);
            goToStage(3); //All the way
            driveLeft(1.25);
            driveForwardSlow(.6);
            liftDown(1);
            clampOpen();
            driveBackwardSlow(.5);
            driveRight(3.5);
        } else { // Position 3?
            telemetry.addData("Detected", "Blue");
            telemetry.update();
            driveForward(.5);
            driveBackwards(.7);
            driveLeft(2.5);
            goToStage(3); //All the way
            driveLeft(1.25);
            driveForwardSlow(.6);
            liftDown(1);
            clampOpen();
            driveBackwardSlow(.5);
            driveRight(7);
        }

    }

    public void redLeft(){
        waitForStart();
        clampClose();
        goToStage(1);
        int desiredLocation = getDesiredLocation();
        driveForward(1.5);
        if (desiredLocation == 1) { // Position 1?
            telemetry.addData("Detected", "Red");
            telemetry.update();
            driveForward(.5);
            driveBackwards(.7);
            driveRight(2.5);
            goToStage(3); //All the way
            driveRight(1.25);
            clampOpen();
            driveLeft(7);
            driveForward(1);
        } else if (desiredLocation == 2) { // Position 2?
            telemetry.addData("Detected", "Green");
            telemetry.update();
            driveForward(.5);
            driveBackwards(.7);
            driveRight(2.5);
            goToStage(3); //All the way
            driveRight(1.25);
            clampOpen();
            driveLeft(3.5);
        } else { // Position 3?
            telemetry.addData("Detected", "Blue");
            telemetry.update();
            driveForward(.5);
            driveBackwards(.7);
            driveRight(2.5);
            goToStage(3); //All the way
            driveRight(1.25);

            clampOpen();
            driveLeft(7);
        }
    }

    public void redRight(){
        waitForStart();
        clampClose();
        goToStage(1);
        int desiredLocation = getDesiredLocation();
        driveForward(1.5);
        if (desiredLocation == 1) { // Position 1?
            telemetry.addData("Detected", "Red");
            telemetry.update();
            driveForward(.5);
            driveBackwards(.7);
            driveLeft(2.5);
            goToStage(3);
            driveLeft(1.25);
            driveForwardSlow(.5);
            liftDown(1);
            clampOpen();
            driveBackwardSlow(.5);
            driveRight(7);
            driveForward(1);
        } else if (desiredLocation == 2) { // Position 2?
            telemetry.addData("Detected", "Green");
            telemetry.update();
            driveForward(.5);
            driveBackwards(.7);
            driveLeft(2.5);
            goToStage(3); //All the way
            driveLeft(1.25);
            driveForwardSlow(.5);
            liftDown(1);
            clampOpen();
            driveBackwardSlow(.5);
            driveRight(3.5);
        } else { // Position 3?
            telemetry.addData("Detected", "Blue");
            telemetry.update();
            driveForward(.5);
            driveBackwards(.7);
            driveLeft(2.5);
            goToStage(3); //All the way
            driveLeft(1.25);
            driveForwardSlow(.6);
            liftDown(1);
            clampOpen();
            driveBackwardSlow(.5);
            driveRight(7);
        }
    }

     */
}
