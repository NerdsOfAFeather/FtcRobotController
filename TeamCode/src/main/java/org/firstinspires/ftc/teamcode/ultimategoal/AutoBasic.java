package org.firstinspires.ftc.teamcode.ultimategoal;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.HolonomicDriveBaseCode;

//@Autonomous(name = "MecanumAuto", group = "Autonomous")
public class AutoBasic extends HolonomicDriveBaseCode {
    public DcMotor intake;
    public DcMotor lf_flywheel;
    public Servo grabby;

    public void attachInit(){
        intake = hardwareMap.dcMotor.get("IN");
        lf_flywheel = hardwareMap.dcMotor.get("LF");
        grabby = hardwareMap.servo.get("grab");

        intake.setPower(0);
        lf_flywheel.setPower(0);
        grabby.setPosition(0);
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
    public void middleBox() {
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

        if (tfod != null) {
            tfod.activate();
            tfod.setZoom(1.0, 1.78);
        }

        waitForStart();
        if(opModeIsActive()) {
            //vision thingy
         /*   if (){
                //the one ring (one)
                middleBox();
            } else if  (){
                //dwarf rings but three are in the lava (four)
                thirdBox();
           } else (){
                //the rings are all in the lava (none)
                firstBox();
            }
            thirdBox();
        }
        */
        }
    }
}