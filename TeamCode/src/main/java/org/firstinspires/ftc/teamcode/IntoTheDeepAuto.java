package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.TeamColor.*;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

/**Created by Gavin for FTC Team 6347 */
@Autonomous(name = "IntoTheDeepAuto", group = "Autonomous", preselectTeleOp = "IntoTheDeepTeleOp2p")
public class IntoTheDeepAuto extends IntoTheDeepConfig {
    static int delay = 0;
    static boolean stopped = false;
    private ElapsedTime runtime = new ElapsedTime();

    double delayTime = 0;  // 24 forward, 16 side, 10 side

    @Override
    public void init() {
        telemetry.addData("Status", "Initializing...");
        telemetry.update();

        initAuto();

        fWrist.setPosition(1.0);
        fClawL.setPosition(ClawState.CLOSED.lPos);
        fClawR.setPosition(ClawState.CLOSED.rPos);

        //startAndEnableRobotVision();

        telemetry.addData("Status", "Ready to Run");
        telemetry.update();
    }

    @Override
    public void init_loop() {
        if (gamepad1.x) {
            team = BLUE_RIGHT;
        } else if (gamepad1.b) {
            team = RED_RIGHT;
        } else if (gamepad1.a) {
            team = RED_LEFT;
        } else if (gamepad1.y) {
            team = BLUE_LEFT;
        }
        telemetry.addData("Team", team.toString());
        telemetry.update();

    }

    @Override
    public void start() {
        runtime.reset();
        resetYaw();


    }

    @Override
    public void loop() {
        fWrist.setPosition(1.0);
        fClawL.setPosition(ClawState.CLOSED.lPos);
        fClawR.setPosition(ClawState.CLOSED.rPos);

        traj(forward(10));
        //Auto stuff here
        if (team == BLUE_LEFT) {
            turnLeft(90);
            traj(forward(35));
            fWrist.setPosition(0.2);
            sleep(500);
            fClawL.setPosition(ClawState.OPEN.lPos);
            fClawR.setPosition(ClawState.OPEN.rPos);
            sleep(500);
            fWrist.setPosition(1.0);
            turnRight(90);
            traj(forward(23));
            fWrist.setPosition(0.2);
            sleep(1000);
            fClawL.setPosition(ClawState.CLOSED.lPos);
            fClawR.setPosition(ClawState.CLOSED.rPos);
            sleep(1000);
            fWrist.setPosition(1.0);
            sleep(500);
            turnLeft(150);
            traj(forward(24));
            fWrist.setPosition(0.2);
            sleep(500);
            fClawL.setPosition(ClawState.OPEN.lPos);
            fClawR.setPosition(ClawState.OPEN.rPos);
            sleep(500);
            fWrist.setPosition(1.0);
        } else if (team == RED_LEFT) {
            turnLeft(90);
            traj(forward(50));
            turnLeft(45);
            fWrist.setPosition(0.1);
            sleep(500);
            fClawL.setPosition(ClawState.OPEN.lPos);
            fClawR.setPosition(ClawState.OPEN.rPos);
            sleep(500);
            fWrist.setPosition(1.0);
            turnRight(125);
            traj(forward(23));
            fWrist.setPosition(0.1);
            sleep(1000);
            fClawL.setPosition(ClawState.CLOSED.lPos);
            fClawR.setPosition(ClawState.CLOSED.rPos);
            sleep(1000);
            fWrist.setPosition(1.0);
            sleep(500);
            turnLeft(150);
            traj(forward(24));
            fWrist.setPosition(0.1);
            sleep(500);
            fClawL.setPosition(ClawState.OPEN.lPos);
            fClawR.setPosition(ClawState.OPEN.rPos);
            sleep(500);
            fWrist.setPosition(1.0);
            turnRight(10);
            traj(forward(5));
        } else if (team == BLUE_RIGHT) {
            turnRight(87);
            traj(forward(30));
            fWrist.setPosition(0.1);
            sleep(1000);
            fClawL.setPosition(ClawState.OPEN.lPos);
            fClawR.setPosition(ClawState.OPEN.rPos);
            sleep(1000);
            fWrist.setPosition(1.0);
            turnLeft(90);
            traj(forward(20));
            turnLeft(90);
            traj(forward(90));
            turnRight(90);
            traj(forward(30));
            turnLeft(90);
            traj(back(20));
        } else if (team == RED_RIGHT) {
            turnRight(90);
            traj(forward(30));
            fWrist.setPosition(0.1);
            sleep(1000);
            fClawL.setPosition(ClawState.OPEN.lPos);
            fClawR.setPosition(ClawState.OPEN.rPos);
            sleep(1000);
            fWrist.setPosition(1.0);
            turnLeft(90);
            traj(forward(20));
            turnLeft(90);
            traj(forward(90));
            turnRight(90);
            traj(forward(30))   ;
            turnLeft(90);
            traj(back(20));
        }

        requestOpModeStop();
    }

    @Override
    public void stop() {
        stopped = true;
    }
}
