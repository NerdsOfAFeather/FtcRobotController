package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="Fast and Furious")
public class BabiesFirstSteps extends OpMode {
    private ElapsedTime runtime = new ElapsedTime();

    private DcMotor leftDrive = null;

    private DcMotor rightDrive = null;

    @Override
    public void init() {
        telemetry.addData("status", "initializing");
        telemetry.update();

        leftDrive=hardwareMap.get(DcMotor.class,"left_motor");
        rightDrive=hardwareMap.get(DcMotor.class,"right_motor");

        leftDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        rightDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData("status", "initialized");
        telemetry.update();
    }

    @Override
    public void start() {
        runtime.reset();
    }

    @Override
    public void loop() {
        double leftPower;
        double rightPower;

        double drive = -gamepad1.left_stick_y;
        double turn  = gamepad1.left_stick_x;
        leftPower = Range.clip(drive + turn, -1, 1);
        rightPower = Range.clip(drive - turn, -1, 1);

        leftDrive.setPower(leftPower);
        rightDrive.setPower(rightPower);

        telemetry.addData("status", "runtime" + runtime.toString());
        telemetry.addData("motors", "left(%.2f), right(%.2f)",rightPower,leftPower);
    }
    @Override
    public void stop() {
    }
}

