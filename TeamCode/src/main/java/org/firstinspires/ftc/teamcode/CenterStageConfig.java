package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

/** Created by Gavin for Team 6347 */
@TeleOp(name="CenterStageConfig", group="Linear Opmode")
@Disabled
public class CenterStageConfig extends CenterStageObjectDetection {

    public DcMotorEx leftFrontDrive = null;
    public DcMotorEx leftBackDrive = null;
    public DcMotorEx rightFrontDrive = null;
    public DcMotorEx rightBackDrive = null;
    public DcMotorEx intakeMotor = null;
    public Servo flipperServo = null;

    public void initDriveHardware() {

        leftFrontDrive = hardwareMap.get(DcMotorEx.class, "FrontLeftDrive");
        leftBackDrive = hardwareMap.get(DcMotorEx.class, "BackLeftDrive");
        rightFrontDrive = hardwareMap.get(DcMotorEx.class, "FrontRightDrive");
        rightBackDrive = hardwareMap.get(DcMotorEx.class, "BackRightDrive");
        //intakeMotor = hardwareMap.get(DcMotorEx.class, "Intake");
        //flipperServo = hardwareMap.get(Servo.class, "FlipperServo");

        leftFrontDrive.setDirection(Direction.REVERSE);
        leftBackDrive.setDirection(Direction.REVERSE);
        rightFrontDrive.setDirection(Direction.FORWARD);
        rightBackDrive.setDirection(Direction.FORWARD);
        //intakeMotor.setDirection(Direction.FORWARD);

    }

}
