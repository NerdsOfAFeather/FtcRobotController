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
    public void park(){
        forward(230, .25);
    }
    public void firstBox(){
        //the rings have all been thrown in the fire
        stopMotors();
        sideLeft(800, .2);
        stopMotors();
        forward( 126, -.2);
        stopMotors();
        sideLeft(400,.2);
        stopMotors();
        forward(30, .2);
        stopMotors();
        sideRight(600,.2);
        stopMotors();
        forward(30, -.2);
    }
    public void secondBox() {
        // one ring to rule them all
        stopMotors();
        sideLeft(400, .2);
        stopMotors();
        forward( 210, -.2);
        stopMotors();
        forward(30, .2);
        stopMotors();
        forward(15, .2);
    }
        public void thirdBox(){
        // elven rings and also the one ring
        stopMotors();
        sideLeft(800, .2);
        stopMotors();
        forward( 270, -.2);
        stopMotors();
        sideLeft(400,.2);
        stopMotors();
        forward(30, .2);
        stopMotors();
        sideRight(600,.2);
        stopMotors();
        forward(70, .2);
        }

    @Override
    public void runOpMode() {
        initDriveHardware();
        attachInit();
        initVuforia();
        initTfod();

     /*   if (tfod != null) {
            tfod.activate();
            tfod.setZoom(1.0, 1.78);
        } */
        waitForStart();

       /* if(opModeIsActive()) {
            forward(100, -.2);
            //vision thingy
            if (){
                //the one ring
            } else if  (){
                //dwarf rings but three are in the lava
            } else (){
                //the rings are all in the lava
            }
            thirdBox();
        } */
    }
}