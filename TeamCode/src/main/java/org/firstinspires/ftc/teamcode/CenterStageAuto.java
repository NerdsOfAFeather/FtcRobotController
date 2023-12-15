package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.TeamColor.BLUE;
import static org.firstinspires.ftc.teamcode.TeamColor.RED;
import static org.firstinspires.ftc.teamcode.TeamColor.UNSET;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

/**Created by Gavin for FTC Team 6347 */
@Autonomous(name = "CenterStageAutoLong", group = "Autonomous", preselectTeleOp = "CenterStageTeleOp")
public class CenterStageAuto extends CenterStageConfig {

    private ElapsedTime runtime = new ElapsedTime();
    TeamColor team = UNSET;

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
        if (gamepad1.a) {
            team = BLUE;
        } else if (gamepad1.b) {
            team = RED;
        }
        telemetry.addData("Team", team.toString());
        telemetry.update();
    }

    @Override
    public void start() {
        runtime.reset();

        //Auto stuff here
        traj(forward(80));
        if (team.equals(BLUE)) {
            traj(right(150));
        } else if (team.equals(RED)) {
            traj(left(150));
        }

        this.stop();
        terminateOpModeNow();
    }

    @Override
    public void loop() {}

    @Override
    public void stop() {
        //closeAndDisableRobotVision();
    }
}
