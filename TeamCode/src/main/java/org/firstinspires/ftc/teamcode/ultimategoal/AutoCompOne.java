package org.firstinspires.ftc.teamcode.ultimategoal;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.HolonomicDriveBaseCode;

//@Autonomous(name = "auto2", group= "auto")
public class AutoCompOne extends HolonomicDriveBaseCode {
    public DcMotor intake;
    public DcMotor lf_flywheel;
    public Servo grabby;

    public void attachInit(){
        intake = hardwareMap.dcMotor.get("IN");
        lf_flywheel = hardwareMap.dcMotor.get("LF");
        grabby = hardwareMap.servo.get("grab");

        intake.setPower(0);
        lf_flywheel.setPower(0);
        grabby.setPosition(.1);
    }


    public void middleBox(){
        // will get box points when there is one ring on the feild

        //move to box
        stopMotors();
        forwardWithGyro(50, .25);
        pivotCC(60, .3);
        forwardWithGyro(210, .25);
        stopMotors();
        sideLeft(760, -.25);
        stopMotors();
        pivotCC(35, .3);
        forward( 50, .2);
        intake.setPower(.2);
       sleep(3000);
        intake.setPower(0);
        forward(30, -.2);
        stopMotors();
        pivotCC(180, .3);

    }

    public void runOpMode(){
        attachInit();
        initDriveHardware();
        waitForStart();
        middleBox();
    }
}

