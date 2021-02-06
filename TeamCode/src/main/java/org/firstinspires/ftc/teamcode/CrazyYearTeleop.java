package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
@TeleOp(name = "MecanumTeleOp", group = "TeleOp")
public class CrazyYearTeleop extends HolonomicDriveBaseCode {
    public DcMotor intake;
    public DcMotor lf_flywheel;
    public DcMotor rt_flywheel;

    public void attachInit(){
        intake = hardwareMap.dcMotor.get("IN");
        lf_flywheel = hardwareMap.dcMotor.get("LF");
        rt_flywheel = hardwareMap.dcMotor.get("RF");
        intake.setPower(0);
        lf_flywheel.setPower(0);
        rt_flywheel.setPower(0);
    }

    public void attachTeleop( double inPower, double outPower){
        //Intake
        intake.setPower(-inPower);
        //phwoop`


        lf_flywheel.setPower(-outPower);
        rt_flywheel.setPower(outPower);
    }

    public void moveAndAttachTeleOp(){
        //altmoveTeleop(-gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_y);
        buttonMoveTeleop();
        attachTeleop(gamepad2.right_stick_y, gamepad2.left_stick_y);
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
