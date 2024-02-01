package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.TeamColor.BLUE;
import static org.firstinspires.ftc.teamcode.TeamColor.RED;
import static org.firstinspires.ftc.teamcode.TeamColor.UNSET;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.util.ElapsedTime;

/**Created by Gavin for FTC Team 6347 */
@Autonomous(name = "CenterStageAutoLong", group = "Autonomous", preselectTeleOp = "CenterStageTeleOp")
public class CenterStageAuto extends CenterStageConfig {
    static int delay = 0;
    private ElapsedTime runtime = new ElapsedTime();

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
            team = BLUE;
        } else if (gamepad1.b) {
            team = RED;
        }
        telemetry.addData("Team", team.toString());
        if(team != UNSET){
            if (gamepad1.y) {
                delay++;
                if (delay>30){
                    delay=30;
                }
            } else if (gamepad1.a){
                delay--;
                if (delay<0){
                    delay=0;
                }
            }
            telemetry.addData("Delay", delay);
        }
        telemetry.update();

    }

    @Override
    public void start() {
        runtime.reset();

        int pos = getPosition();

        traj(forward(10));
        //Auto stuff here
        if (team.equals(BLUE)) {
            if (pos == 1) {
                turnTo(-40);
                moveArmToGround();
                openClawL();
                sleep(250);
                closeClawL();
                moveArmToClosed();
                turnTo(0);
                traj(back(5));
                traj(left(32.5));
                traj(forward(20));
                sleep(delay*1000L);
                traj(forward(25));
                traj(left(100));
            } else if (pos == 2) {
                traj(forward(10));
                moveArmToGround();
                openClawL();
                sleep(250);
                closeClawL();
                moveArmToClosed();
                traj(back(15));
                traj(left(32.5));
                traj(forward(20));
                sleep(delay*1000L);
                traj(forward(25));
                traj(left(100));
            } else if (pos == 3) {
                turnTo(40);
                moveArmToGround();
                openClawL();
                sleep(250);
                closeClawL();
                moveArmToClosed();
                turnTo(0);
                traj(back(5));
                traj(left(32.5));
                traj(forward(20));
                sleep(delay*1000L);
                traj(forward(25));
                traj(left(100));
            }
        } else if (team.equals(RED)) {
            if (pos == 1) {
                turnTo(-40);
                moveArmToGround();
                openClawL();
                sleep(250);
                closeClawL();
                moveArmToClosed();
                turnTo(0);
                traj(back(5));
                traj(right(32.5));
                traj(forward(20));
                sleep(delay*1000L);
                traj(forward(25));
                traj(right(100));
            } else if (pos == 2) {
                traj(forward(10));
                moveArmToGround();
                openClawL();
                sleep(250);
                closeClawL();
                moveArmToClosed();
                traj(back(15));
                traj(right(32.5));
                traj(forward(20));
                sleep(delay*1000L);
                traj(forward(25));
                traj(right(100));
            } else if (pos == 3) {
                turnTo(40);
                moveArmToGround();
                openClawL();
                sleep(250);
                closeClawL();
                moveArmToClosed();
                turnTo(0);
                traj(back(5));
                traj(right(32.5));
                traj(forward(20));
                sleep(delay*1000L);
                traj(forward(25));
                traj(right(100));
            }
        }
        requestOpModeStop();
    }

    @Override
    public void loop() {}

    @Override
    public void stop() {
        //closeAndDisableRobotVision();
    }
}
