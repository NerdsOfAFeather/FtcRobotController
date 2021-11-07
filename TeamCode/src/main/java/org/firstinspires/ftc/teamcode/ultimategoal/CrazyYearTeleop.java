package org.firstinspires.ftc.teamcode.ultimategoal;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.HolonomicDriveBaseCode;

//@TeleOp(name = "MecanumTeleOp", group = "TeleOp")
public class CrazyYearTeleop extends HolonomicDriveBaseCode {
    public DcMotor intake;
    public DcMotor lf_flywheel;
    public DcMotor rf_flywheel;
    public Servo right;
    public Servo left;


    public void attachInit(){
        intake = hardwareMap.dcMotor.get("IN");
        lf_flywheel = hardwareMap.dcMotor.get("LF");
        rf_flywheel = hardwareMap.dcMotor.get("RF");
        right = hardwareMap.servo.get("rs");
        left = hardwareMap.servo.get("ls");


        intake.setPower(0);
        lf_flywheel.setPower(0);

        rf_flywheel.setPower(0);
       
    }

    public void attachTeleop( double inPower, double outPower){
        //Intake
        intake.setPower(-inPower);
        //phwoop`
        lf_flywheel.setPower(outPower);
        rf_flywheel.setPower(-outPower);

    }

    public void moveAndAttachTeleOp(){
        if(gamepad1.x){
            if (!gamepad1.y ) {
                alignWithWall(20, .2, 2);
            } else {stopMotors(); }
        }
        buttonMoveTeleop();
        if(gamepad2.a){
            right.setPosition(.5);
           left.setPosition(.3);
        } if (gamepad2.x){
            right.setPosition(.94);
            left.setPosition(0);
        }
        attachTeleop(gamepad2.right_stick_y, -gamepad2.left_stick_y);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initDriveHardware();
        attachInit();
        waitForStart();
        while (opModeIsActive()) {
            moveAndAttachTeleOp();
            telemetry.addData("rightdis", distanceSensorR.getDistance(DistanceUnit.CM));
            telemetry.addData("leftdis", distanceSensorL.getDistance(DistanceUnit.CM));
            telemetry.update();
        }
    }
}
