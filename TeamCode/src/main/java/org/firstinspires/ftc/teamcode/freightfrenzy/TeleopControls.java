package org.firstinspires.ftc.teamcode.freightfrenzy;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.TwoWheelDriveBaseCode;

@TeleOp(name = "FreightFrenzyTeleop", group = "TeleOp")
public class TeleopControls extends TwoWheelDriveBaseCode {
    public DcMotor spin;

    public void attachInit(){
        spin = hardwareMap.dcMotor.get("SP");
        spin.setPower(0);
    }

    public void attachTeleop(){
        spin.setPower(gamepad2.left_stick_y);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initialize(DcMotor.ZeroPowerBehavior.BRAKE);
        attachInit();
        waitForStart();
        while (opModeIsActive()) {
            attachTeleop();
            basicTeleop();
            telemetry.update();
        }
    }

}
