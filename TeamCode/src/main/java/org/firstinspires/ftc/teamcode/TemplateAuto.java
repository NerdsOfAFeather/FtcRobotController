package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.TeamColor.BLUE_LONG;
import static org.firstinspires.ftc.teamcode.TeamColor.RED_LONG;
import static org.firstinspires.ftc.teamcode.TeamColor.UNSET;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

/**Created by Gavin for FTC Team 6347 */
@Autonomous(name = "CenterStageAutoLong", group = "Autonomous", preselectTeleOp = "CenterStageTeleOp")
public class TemplateAuto extends TemplateConfig {
    static int delay = 0;
    private ElapsedTime runtime = new ElapsedTime();

    double delayTime = 0;

    @Override
    public void init() {
        telemetry.addData("Status", "Initializing...");
        telemetry.update();

        initAuto();
        initEOCV();

        //startAndEnableRobotVision();

        telemetry.addData("Status", "Ready to Run");
        telemetry.update();
    }

    @Override
    public void init_loop() {
        if (gamepad1.x) {
            team = BLUE_LONG;
        } else if (gamepad1.b) {
            team = RED_LONG;
        }
        telemetry.addData("Team", team.toString());
        if(team != UNSET){
            if (gamepad1.y && runtime.milliseconds() - delayTime > 500) {
                delay++;
                delayTime = runtime.milliseconds();
                if (delay>30){
                    delay=30;
                }
            } else if (gamepad1.a && runtime.milliseconds() - delayTime > 500){
                delay--;
                delayTime = runtime.milliseconds();
                if (delay<0){
                    delay=0;
                }
            }
            telemetry.addData("Delay", delay);
        }
        telemetry.addData("Position", getPosition());
        telemetry.update();

    }

    @Override
    public void start() {
        runtime.reset();
        resetYaw();

        while (getPosition() == 0) {
            sleep(10);
        }
        int pos = getPosition();
        stopEOCV();

        traj(forward(10));
        //Auto stuff here
        if (team.equals(BLUE_LONG)) {
            if (pos == 1) {
                moveArmToGround();
                traj(forward(5));
                drive.turn(0.8);
                openClawL();
                sleep(1000);
                traj(back(10));
                drive.turn(-0.8);
                closeClawL();
                moveArmToClosed();
                traj(left(47.5));
                traj(forward(30));
                sleep(delay*1000L);
                drive.turn(0.1);
                traj(forward(30));
                moveIntakeMotorUp(0.5);
                traj(left(100));
            } else if (pos == 2) {
                moveArmToGround();
                traj(forward(14));
                openClawL();
                sleep(250);
                moveArmToClosed();
                closeClawL();
                traj(back(15));
                traj(left(42.5));
                traj(forward(30));
                sleep(delay*1000L);
                drive.turn(0.1);
                traj(forward(30));
                moveIntakeMotorUp(0.5);
                traj(left(100));
            } else if (pos == 3) {
                drive.turn(-0.4);
                moveArmToGround();
                openClawL();
                sleep(250);
                moveArmToClosed();
                closeClawL();
                traj(back(5));
                drive.turn(0.4);
                traj(left(42.5));
                traj(forward(30));
                sleep(delay*1000L);
                traj(forward(32.5));
                moveIntakeMotorUp(0.5);
                drive.turn(0.15);
                traj(left(100));
            }
        } else if (team.equals(RED_LONG)) {
            if (pos == 1) {
                drive.turn(0.4);
                moveArmToGround();
                openClawL();
                sleep(500);
                moveArmToClosed();
                closeClawL();
                drive.turn(-0.4);
                traj(right(40));
                drive.turn(0.25);
                traj(forward(30));
                sleep(delay*1000L);
                drive.turn(0.25);
                traj(forward(40));
                moveIntakeMotorUp(0.5);
                drive.turn(0.15);
                traj(right(95));
            } else if (pos == 2) {
                moveArmToGround();
                traj(forward(14));
                openClawL();
                sleep(250);
                moveArmToClosed();
                closeClawL();
                traj(back(15));
                traj(right(40));
                drive.turn(0.25);
                traj(forward(30));
                sleep(delay*1000L);
                drive.turn(0.25);
                traj(forward(40));
                moveIntakeMotorUp(0.5);
                traj(right(95));
            } else if (pos == 3) {
                moveArmToGround();
                traj(forward(5));
                drive.turn(-0.8);
                openClawL();
                sleep(1000);
                traj(back(5));
                drive.turn(0.8);
                closeClawL();
                moveArmToClosed();
                traj(right(42.5));
                drive.turn(0.25);
                traj(forward(30));
                sleep(delay*1000L);
                drive.turn(0.1);
                traj(forward(40));
                moveIntakeMotorUp(0.5);
                drive.turn(0.35);
                traj(right(100));
            }
        }
        moveIntakeMotorDown(0.5);
        requestOpModeStop();
    }

    @Override
    public void loop() {}

    @Override
    public void stop() {
        //closeAndDisableRobotVision();
        Thread.currentThread().interrupt();
        super.stop();
    }
}
