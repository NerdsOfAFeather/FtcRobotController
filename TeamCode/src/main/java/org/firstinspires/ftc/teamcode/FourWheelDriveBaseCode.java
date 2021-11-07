package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class FourWheelDriveBaseCode extends LinearOpMode {
    public DcMotor leftFront;
    public DcMotor leftBack;
    public DcMotor rightFront;
    public DcMotor rightBack;

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
        leftFront.setZeroPowerBehavior(behv);
        leftBack.setZeroPowerBehavior(behv);
        rightFront.setZeroPowerBehavior(behv);
        rightBack.setZeroPowerBehavior(behv);
    }
    //set up the basics.
    public void initialize(DcMotor.ZeroPowerBehavior behv){
        leftFront = hardwareMap.dcMotor.get("lf");
        leftBack = hardwareMap.dcMotor.get("lb");
        rightFront = hardwareMap.dcMotor.get("rf");
        rightBack = hardwareMap.dcMotor.get("rb");

        leftFront.setDirection(DcMotorSimple.Direction.FORWARD);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.REVERSE);
        rightBack.setDirection(DcMotorSimple.Direction.FORWARD);

        //gamepad1.setJoystickDeadzone(0.1f);
        //gamepad2.setJoystickDeadzone(0.1f);

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
            currentDistanceTicks = (rightFront.getCurrentPosition() +
                    leftFront.getCurrentPosition() + rightBack.getCurrentPosition()
                    + leftBack.getCurrentPosition() / 2.0);
            telemetry.addData("Current pos ticks Avg: ", currentDistanceTicks);
            telemetry.addData("Current Distance cm", currentDistanceTicks / ticksPerCm);
            telemetry.update();

            leftFront.setPower(-power);
            leftBack.setPower(-power);
            rightFront.setPower(-power);
            rightBack.setPower(-power);
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
            currentTurnTicks = (leftFront.getCurrentPosition());
            telemetry.addData("Current pos ticks Avg: ", currentTurnTicks);
            telemetry.addData("Current Distance cm", currentTurnTicks / ticksPerCm);
            telemetry.update();

            leftFront.setPower(power);
            leftBack.setPower(power);
            rightFront.setPower(-power);
            rightBack.setPower(-power);
        }
    }
    public void setDriveMode(DcMotor.RunMode mode) {
        leftFront.setMode(mode);
        rightFront.setMode(mode);
    }
    public void forward(double power){
        leftFront.setPower(power);
        leftBack.setPower(power);
        rightFront.setPower(power);
        rightBack.setPower(power);
    }
    public void backward(double power){
        leftFront.setPower(-power);
        leftBack.setPower(-power);
        rightFront.setPower(-power);
        rightBack.setPower(-power);
    }
    public void turnCW(double power){
        leftFront.setPower(-power);
        leftFront.setPower(-power);
        rightFront.setPower(power);
        rightBack.setPower(power);
    }
    public void turnCCW(double power){
        leftFront.setPower(power);
        leftBack.setPower(power);
        rightFront.setPower(-power);
        rightBack.setPower(-power);
    }

    // Teleop
    public void basicTeleop(){
        while(opModeIsActive()){
            forward(gamepad1.right_stick_y);
            turnCW(gamepad1.left_stick_x);
        }
    }
    @Override
    public void runOpMode() throws InterruptedException {
    }
}
