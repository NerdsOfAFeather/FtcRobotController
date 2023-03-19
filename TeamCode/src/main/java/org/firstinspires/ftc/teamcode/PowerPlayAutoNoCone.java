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

import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

import java.util.List;

//The code is structured as a LinearOpMode

/** Created by Gavin for Team 6347*/
@Autonomous(name = "PowerPlay Auto", group = "Robot")

public class PowerPlayAutoNoCone extends OldPowerPlayConfig {

    @Override
    public void runOpMode() {

        // Send telemetry message to signify robot waiting
        telemetry.addData("Status", "Initializing...");
        telemetry.update();

        initDriveHardware();
        initLift();
        initVuforia();
        initTfod();

        //Make a loop for tfod
        if (tfod != null){
            tfod.activate();
            tfod.setZoom(1.0, 16.0/9.0);
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
        }
        telemetry.addData("Status", "Ready to run");
        telemetry.update();
        waitForStart();
        clampClose();
        goToStage(1);
        int desiredLocation = getDesiredLocation();
        driveForward(2.25);
        if (desiredLocation == 1) { // Position 1?
            telemetry.addData("Detected", "One");
            telemetry.update();
            sleep(10000);
            driveLeft(3);
            goToStage(0);
        } else if (desiredLocation == 2) { // Position 2?
            telemetry.addData("Detected", "Two");
            telemetry.update();
            sleep(5000);
        } else { // Position 3?
            telemetry.addData("Detected", "Three");
            telemetry.update();
            sleep(10000);
            driveRight(2.75);
            goToStage(0);
        }
    }
}