package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "MecanumTeleOp", group = "TeleOp")
public class CrazyYearTeleop extends HolonomicDriveBaseCode {
    public DcMotor intake;
    public DcMotor lf_flywheel;
    public Servo grabby;

    public void attachInit(){
        intake = hardwareMap.dcMotor.get("IN");
        lf_flywheel = hardwareMap.dcMotor.get("LF");
        grabby = hardwareMap.servo.get("grab");

        intake.setPower(0);
        lf_flywheel.setPower(0);
        grabby.setPosition(.5);
    }

    public void attachTeleop( double inPower, double outPower){
        //Intake
        intake.setPower(-inPower);
        //phwoop`
        lf_flywheel.setPower(-outPower);
        //grabbbby
        if(gamepad2.right_bumper){
            grabby.setPosition(.55);
            telemetry.addData("grab", "down");
            telemetry.addData("servopos", grabby.getPosition());
            telemetry.update();
        }
        if(gamepad2.left_bumper){
            grabby.setPosition(.27);
            telemetry.addData("grab", "up");
            telemetry.addData("servopos", grabby.getPosition());
            telemetry.update();
        }
    }

    public void moveAndAttachTeleOp(){
        if (gamepad1.a){
            altmoveTeleop(-gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_y);
        }
        else if (gamepad1.b){
            buttonMoveTeleop();
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
        }
    }
}
