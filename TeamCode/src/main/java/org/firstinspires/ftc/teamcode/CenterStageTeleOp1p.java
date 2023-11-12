package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="PowerPlayTeleOp1P", group="Linear Opmode")
//@Disabled
public class CenterStageTeleOp1p extends CenterStageConfig {

    // Declare OpMode members for each of the 4 motors.
    ElapsedTime runtime = new ElapsedTime();

    double axial;
    double lateral;
    double yaw;
    boolean slowMode;

    @Override
    public void init() {
        initDriveHardware();
        telemetry.addData("Bingus", "Bongus");
        telemetry.update();
    }

    @Override
    public void start() {
        runtime.reset();
    }

    @Override
    public void loop() {


        // run until the end of the match (driver presses STOP)
            double max;
            double leftFrontPower;
            double rightFrontPower;
            double leftBackPower;
            double rightBackPower;
            if (gamepad1.left_bumper && !slowMode){
                slowMode = true;
            } else if (gamepad1.left_bumper && slowMode){
                slowMode = false;
            }
            if (slowMode) {

                // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
                if (Math.abs(gamepad1.left_stick_y) >= 0.3) {
                    axial = -gamepad1.left_stick_y/2;  // Note: pushing stick forward gives negative value
                } else {
                    axial = 0;
                }
                if (Math.abs(gamepad1.left_stick_x) >= 0.3) {
                    lateral = gamepad1.left_stick_x/2;
                } else {
                    lateral = 0;
                }
                if (Math.abs(gamepad1.right_stick_x) >= 0.3) {
                    yaw = gamepad1.right_stick_x/2;
                } else {
                    yaw = 0;
                }
            } else {
                // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
                if (Math.abs(gamepad1.left_stick_y) >= 0.3) {
                    axial = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
                } else {
                    axial = 0;
                }
                if (Math.abs(gamepad1.left_stick_x) >= 0.3) {
                    lateral = gamepad1.left_stick_x;
                } else {
                    lateral = 0;
                }
                if (Math.abs(gamepad1.right_stick_x) >= 0.3) {
                    yaw = gamepad1.right_stick_x;
                } else {
                    yaw = 0;
                }
            }
            leftFrontPower = axial + lateral + yaw;
            rightFrontPower = axial - lateral - yaw;
            leftBackPower = axial - lateral + yaw;
            rightBackPower = axial + lateral - yaw;
            max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
            max = Math.max(max, Math.abs(leftBackPower));
            max = Math.max(max, Math.abs(rightBackPower));
            if (max > 1.0) {
                leftFrontPower /= max;
                rightFrontPower /= max;
                leftBackPower /= max;
                rightBackPower /= max;
            }

            // This is test code:
            //
            // Uncomment the following code to test your motor directions.
            // Each button should make the corresponding motor run FORWARD.
            //   1) First get all the motors to take to correct positions on the robot
            //      by adjusting your Robot Configuration if necessary.
            //   2) Then make sure they run in the correct direction by modifying the
            //      the setDirection() calls above.
            // Once the correct motors move in the correct direction re-comment this code.

            /*
            leftFrontPower  = gamepad1.x ? 1.0 : 0.0;  // X gamepad
            leftBackPower   = gamepad1.a ? 1.0 : 0.0;  // A gamepad
            rightFrontPower = gamepad1.y ? 1.0 : 0.0;  // Y gamepad
            rightBackPower  = gamepad1.b ? 1.0 : 0.0;  // B gamepad
            */



            // Send calculated power to wheels
            leftFrontDrive.setPower(leftFrontPower);
            rightFrontDrive.setPower(rightFrontPower);
            leftBackDrive.setPower(leftBackPower);
            rightBackDrive.setPower(rightBackPower);


            // Show the elapsed game time and wheel power.
            telemetry.addData("Left Trigger", gamepad1.left_trigger);
            telemetry.addData("Right Trigger", gamepad1.right_trigger);
            telemetry.addData("Run Time: ", runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
            // Show joystick information as some other illustrative data
            telemetry.addLine("Left joystick | ")
                    .addData("x", gamepad1.left_stick_x)
                    .addData("y", gamepad1.left_stick_y);
            telemetry.addLine("Light joystick | ")
                    .addData("x", gamepad1.right_stick_x)
                    .addData("y", gamepad1.right_stick_y);
            telemetry.addData("Slow mode", slowMode);
            telemetry.update();

    }
}
