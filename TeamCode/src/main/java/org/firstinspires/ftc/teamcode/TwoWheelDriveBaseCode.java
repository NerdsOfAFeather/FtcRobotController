package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class TwoWheelDriveBaseCode extends LinearOpMode {
    public DcMotor left;
    public DcMotor right;

    private double ticksPerTNO60 = 1440;

    //The post gear box gear ratio.
    private double gearRatio = 1.0;
    //The circumference of the drive wheel.
    private double wheelCircumference = 31.9185; // ??
    //Formula to calculate ticks per centimeter for the current drive set up.FORWARDS/BACKWARD ONLY
    private double ticksPerCm = (ticksPerTNO60 * gearRatio) / wheelCircumference;
    //Formula to calculate ticks per centimeter for the current drive set up.SIDEWAYS

    //can be either break or float
    public void setZeroPowBehv(DcMotor.ZeroPowerBehavior behv) {
      left.setZeroPowerBehavior(behv);
      right.setZeroPowerBehavior(behv);
    }
    //set up the bascics.
    public void initialize(DcMotor.ZeroPowerBehavior behv){
        left = hardwareMap.dcMotor.get("left");
        right = hardwareMap.dcMotor.get("right");

        left.setDirection(DcMotorSimple.Direction.FORWARD);
        right.setDirection(DcMotorSimple.Direction.REVERSE);

        setZeroPowBehv(behv);
    }

    //Auto
    public void forwardEncoders(double power, double targetDistance){
    setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
    double targetDistanceTicks = Math.abs(targetDistance * ticksPerCm);
    double currentDistanceTicks = 0;
        while ((Math.abs(currentDistanceTicks) < targetDistanceTicks) && opModeIsActive()) {
            telemetry.addData("Target pos ticks: ", targetDistanceTicks);
            telemetry.addData("Target Distance:", targetDistance + "cm");
            currentDistanceTicks = (right.getCurrentPosition() +
                    left.getCurrentPosition() / 2.0);
            telemetry.addData("Current pos ticks Avg: ", currentDistanceTicks);
            telemetry.addData("Current Distance cm", currentDistanceTicks / ticksPerCm);
            telemetry.update();

            left.setPower(-power);
            right.setPower(-power);
        }
    }
    public void turnEncoders(double power, double targetTurn) {
        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
        double targetTurnTicks = Math.abs(targetTurn * ticksPerCm);
        double currentTurnTicks = 0;
        while ((Math.abs(currentTurnTicks) < targetTurnTicks) && opModeIsActive()) {
            telemetry.addData("Target pos ticks: ", targetTurnTicks);
            telemetry.addData("Target Distance:", targetTurn + "cm");
            currentTurnTicks = (right.getCurrentPosition() +
                    left.getCurrentPosition() / 2.0);
            telemetry.addData("Current pos ticks Avg: ", currentTurnTicks);
            telemetry.addData("Current Distance cm", currentTurnTicks / ticksPerCm);
            telemetry.update();

            left.setPower(power);
            right.setPower(-power);
        }
    }
    public void setDriveMode(DcMotor.RunMode mode) {
        left.setMode(mode);
        right.setMode(mode);
    }
    public void forward(double power){
        left.setPower(power);
        right.setPower(power);
    }
    public void backward(double power){
        left.setPower(-power);
        right.setPower(-power);
    }
    public void turnCW(double power){
        left.setPower(-power);
        right.setPower(power);
    }
    public void turnCCW(double power){
        left.setPower(power);
        right.setPower(-power);
    }
    //Teleop
    public void basicTeleop(){
           forward(-gamepad1.right_stick_y);
           turnCW(gamepad1.left_stick_x);
           telemetry.addData("Left Motor", left.getPower());
           telemetry.addData("Right Motor", right.getPower());
    }
    @Override
    public void runOpMode() throws InterruptedException {}
}
