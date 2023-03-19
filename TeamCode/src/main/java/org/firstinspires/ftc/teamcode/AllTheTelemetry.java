package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/** Created by Gavin for Team 6347 */
@TeleOp(name = "Telemetry")
@Disabled
public class AllTheTelemetry extends NewPowerPlayConfig{

    @Override
    public void runOpMode() {
        initDriveHardware();
        initLift();
        waitForStart();
        while (opModeIsActive()) {
            telemetry.addData("Left Trigger", gamepad1.left_trigger);
            telemetry.addData("Right Trigger", gamepad1.right_trigger);
            telemetry.addData("Left Claw Position", leftClawServo.getPosition());
            telemetry.addData("Right Claw Position", rightClawServo.getPosition());
            telemetry.addLine("Left joystick | ")
                    .addData("x", gamepad1.left_stick_x)
                    .addData("y", gamepad1.left_stick_y);
            telemetry.addLine("Light joystick | ")
                    .addData("x", gamepad1.right_stick_x)
                    .addData("y", gamepad1.right_stick_y);
            telemetry.addLine("Color sensor | ")
                    .addData("Red", color.red())
                    .addData("Green", color.green())
                    .addData("Blue", color.blue())
                    .addData("ARGB", color.argb());
            telemetry.addData("DesiredPosition", getDesiredLocation());
            telemetry.addData("Lift Motor", liftLiftMotor.getCurrentPosition());
            telemetry.update();
        }
    }
}
