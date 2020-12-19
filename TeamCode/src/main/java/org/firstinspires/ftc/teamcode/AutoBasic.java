package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

import java.util.List;

@Autonomous(name = "MecanumAuto", group = "Autonomous")
public class AutoBasic extends HolonomicDriveBaseCode {
    public DcMotor intake;

    public void attachInit() {
        intake = hardwareMap.dcMotor.get("IN");
        intake.setPower(0);
    }

    @Override
    public void runOpMode() {
        initDriveHardware();
        attachInit();
        initVuforia();
        initTfod();

        if (tfod != null) {
            tfod.activate();
            tfod.setZoom(1.0, 1.78);
        }
        waitForStart();
        if (opModeIsActive()) {
            int strat = 0;
            while (opModeIsActive()) {
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());
                        // step through the list of recognitions and display boundary info.
                        int i = 0;

                        for (Recognition recognition : updatedRecognitions) {
                            telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                            telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                    recognition.getLeft(), recognition.getTop());
                            telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                    recognition.getRight(), recognition.getBottom());
                            telemetry.update();
                            if (recognition.getLabel() == "Quad" && strat == 0){
                                strat = 1;

                            }
                            else if (recognition.getLabel() == "Single" && strat == 0){
                                strat = 2;

                            }
                        }
                        telemetry.update();

                        }

                    if(strat == 0){
                        //default
                        sideLeft(-200, -.5);
                        forwardWithGyro(63, .5);
                        forwardWithGyro(-63, .5);
                    }else if (strat == 1){
                        // one ring to rule them all
                        sideLeft(-200, -.5);
                        forwardWithGyro(63, .5);
                        forwardWithGyro(-63, .5);
                        sideLeft(100, .5);
                    }else if (strat == 2){
                        // elven rings and also the one ring
                        sideLeft(-160, -.5);
                        forwardWithGyro(50, .5);
                        sideLeft(40, .5);
                    }

                }
            }
        }

        if (tfod != null) {
            tfod.shutdown();
        }

        }

       // forwardWithGyro(220, .25);

    }
