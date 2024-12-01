package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.TeamColor.*;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

/**Created by Gavin for FTC Team 6347 */
@Autonomous(name = "IntoTheDeepAutoLong", group = "Autonomous", preselectTeleOp = "IntoTheDeepTeleOp")
public class IntoTheDeepAuto extends IntoTheDeepConfig {
    static int delay = 0;
    private ElapsedTime runtime = new ElapsedTime();

    double delayTime = 0;  // 24 forward, 16 side, 10 side

    @Override
    public void init() {
        telemetry.addData("Status", "Initializing...");
        telemetry.update();

        initAuto();

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

        fWrist.setPosition(1.0);
        fClawL.setPosition(ClawState.CLOSED.lPos);
        fClawR.setPosition(ClawState.CLOSED.rPos);

        traj(forward(10));
        //Auto stuff here
        if (team == BLUE_LEFT) {
            traj(left(16));
            turnLeft(135);
            fWrist.setPosition(0.1);
            fClawL.setPosition(ClawState.OPEN.lPos);
            fClawR.setPosition(ClawState.OPEN.rPos);
            sleep(500);
            fWrist.setPosition(1.0);
            turnRight(135);
            traj(left(10));
            fWrist.setPosition(0.1);
            sleep(250);
            fClawL.setPosition(ClawState.CLOSED.lPos);
            fClawR.setPosition(ClawState.CLOSED.rPos);
            sleep(500);
            turnLeft(135);
            fWrist.setPosition(0.1);
            fClawL.setPosition(ClawState.OPEN.lPos);
            fClawR.setPosition(ClawState.OPEN.rPos);
            sleep(500);
            fWrist.setPosition(1.0);
            turnRight(135);
            traj(back(20));
        } else if (team == RED_RIGHT) {

            traj(right(16));
            turnRight(135);
            fWrist.setPosition(0.1);
            fClawL.setPosition(ClawState.OPEN.lPos);
            fClawR.setPosition(ClawState.OPEN.rPos);
            sleep(500);
            fWrist.setPosition(1.0);
            turnLeft(135);
            traj(right(10));
            fWrist.setPosition(0.1);
            sleep(250);
            fClawL.setPosition(ClawState.CLOSED.lPos);
            fClawR.setPosition(ClawState.CLOSED.rPos);
            sleep(500);
            turnLeft(135);
            fWrist.setPosition(0.1);
            fClawL.setPosition(ClawState.OPEN.lPos);
            fClawR.setPosition(ClawState.OPEN.rPos);
            sleep(500);
            fWrist.setPosition(1.0);
            turnRight(135);
            traj(back(20));
        }

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
