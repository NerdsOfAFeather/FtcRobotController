package org.firstinspires.ftc.teamcode.old;

import static org.firstinspires.ftc.teamcode.TeamColor.BLUE;
import static org.firstinspires.ftc.teamcode.TeamColor.RED;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.CenterStageConfig;

/**Created by Gavin for FTC Team 6347 */
@Autonomous(name = "OldCenterStageAutoShort", group = "Autonomous", preselectTeleOp = "CenterStageTeleOp")
@Disabled
public class OldCenterStageAutoShort extends CenterStageConfig {

    private ElapsedTime runtime = new ElapsedTime();

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
        traj(forward(5));
        if (team.equals(RED)) {
            traj(right(32.5));
            traj(forward(45));
            traj(right(100));
        } else if (team.equals(BLUE)) {
            traj(left(32.5));
            traj(forward(45));
            traj(left(100));
        }

        requestOpModeStop();
    }

    @Override
    public void loop() {

    }

    @Override
    public void stop() {
        //closeAndDisableRobotVision();
    }
}
