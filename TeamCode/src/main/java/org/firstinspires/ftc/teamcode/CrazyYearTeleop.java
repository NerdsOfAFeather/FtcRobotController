package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
@TeleOp(name = "MecanumTeleOp", group = "TeleOp")
public class CrazyYearTeleop extends HolonomicDriveBaseCode {
    public DcMotor intake;

    public void attachInit(){
        intake = hardwareMap.dcMotor.get("IN");
        intake.setPower(0);
    }

    public void moveAndAttachTeleOp(){
        moveTeleOp();
        if(gamepad1.left_bumper == true){
            intake.setPower(-1);
        }intake.setPower(0);
        if(gamepad1.right_bumper == true){
            intake.setPower(1);
        }intake.setPower(0);
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
