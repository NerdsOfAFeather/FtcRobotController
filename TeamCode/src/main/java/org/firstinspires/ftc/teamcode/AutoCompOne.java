package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "auto2", group= "auto")
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
        grabby.setPosition(0);
    }

    public void middleBox(int speed){
        // will get box points if there is one ring on the feild
        // otherwise, will shoot two-three rings and park

        //move to box
        stopMotors();
        sideLeft(400, -.2);
        stopMotors();
        forward( 210, .2);
        intake.setPower(-.5);
        stopMotors();
        grabby.setPosition(.5);
        intake.setPower(0);
        forward(40, -.2);
        stopMotors();
        sideLeft(200, -.5);
        pivotCC(180, .5);
        lf_flywheel.setPower(1);
        grabby.setPosition(.2);
        grabby.setPosition(.5);
        grabby.setPosition(.2);
        grabby.setPosition(.5);
        grabby.setPosition(.2);
        forward(15, .2);
    }
}

