package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class SixWheelDrive extends LinearOpMode {
    // setup
    public DcMotor left1;
    public DcMotor right1;
    public DcMotor left2;
    public DcMotor right2;
    public DcMotor left3;
    public DcMotor right3;

    private double ticksPerTNO60 = 1440;

    //initialize
    public void initialize(DcMotor.ZeroPowerBehavior behv){
        left1 = hardwareMap.dcMotor.get("left1");
        right1 = hardwareMap.dcMotor.get("right1");
        left2 = hardwareMap.dcMotor.get("left2");
        right2 = hardwareMap.dcMotor.get("right2");
        left3 = hardwareMap.dcMotor.get("left3");
        right3 = hardwareMap.dcMotor.get("right3");

        left1.setDirection(DcMotorSimple.Direction.FORWARD);
        right1.setDirection(DcMotorSimple.Direction.REVERSE);
        left2.setDirection(DcMotorSimple.Direction.FORWARD);
        right2.setDirection(DcMotorSimple.Direction.REVERSE);
        left3.setDirection(DcMotorSimple.Direction.FORWARD);
        right3.setDirection(DcMotorSimple.Direction.REVERSE);

        //gamepad1.setJoystickDeadzone(0.1f);
        //gamepad2.setJoystickDeadzone(0.1f);

        setZeroPowBehv(behv);
    }

    public void setDriveMode(DcMotor.RunMode mode) {
        left1.setMode(mode);
        right1.setMode(mode);
        left2.setMode(mode);
        right2.setMode(mode);
        left3.setMode(mode);
        right3.setMode(mode);

    }
    public void forward(double power){
        left1.setPower(power);
        right1.setPower(power);
        left2.setPower(power);
        right2.setPower(power);
        left3.setPower(power);
        right3.setPower(power);

    }
    public void backward(double power){
        left1.setPower(-power);
        right1.setPower(-power);
        left2.setPower(-power);
        right2.setPower(-power);
        left3.setPower(-power);
        right3.setPower(-power);

    }
    public void turnCW(double power){
        left1.setPower(-power);
        right1.setPower(power);
        left2.setPower(-power);
        right2.setPower(power);
        left3.setPower(-power);
        right3.setPower(power);

    }
    public void turnCCW(double power){
        left1.setPower(power);
        right1.setPower(-power);
        left2.setPower(power);
        right2.setPower(-power);
        left3.setPower(power);
        right3.setPower(-power);

    }

    //can be either break or float
    public void setZeroPowBehv(DcMotor.ZeroPowerBehavior behv) {
        left1.setZeroPowerBehavior(behv);
        right1.setZeroPowerBehavior(behv);
        left2.setZeroPowerBehavior(behv);
        right2.setZeroPowerBehavior(behv);
        left3.setZeroPowerBehavior(behv);
        right3.setZeroPowerBehavior(behv);

    }

    // bby Teleop
    public void basicTeleop(){
        while(opModeIsActive()){
            forward(-gamepad1.right_stick_y);
            turnCW(gamepad1.left_stick_x);
        }
    }

    @Override
    public void runOpMode() throws InterruptedException {

    }
}
