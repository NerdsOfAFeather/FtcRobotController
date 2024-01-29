package org.firstinspires.ftc.teamcode.old;

import static org.firstinspires.ftc.teamcode.TeamColor.BLUE;
import static org.firstinspires.ftc.teamcode.TeamColor.RED;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.CenterStageConfig;

/**Created by Gavin for FTC Team 6347 */
@Autonomous(name = "OldCenterStageAutoShorter", group = "Autonomous", preselectTeleOp = "CenterStageTeleOp")
@Disabled
public class OldCenterStageAutoShorter extends CenterStageConfig {

    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void init() {
        telemetry.addData("Status", "Initializing...");
        telemetry.update();

        //initAuto();

        //startAndEnableRobotVision();
    }

    @Override
    public void init_loop() {
        if (gamepad1.x) {
            team = BLUE;
        } else if (gamepad1.b) {
            team = RED;
        }
        telemetry.addData("Position", getPosition());
        telemetry.addData("Team", team.toString());
        telemetry.update();
    }

    @Override
    public void start() {
        runtime.reset();

        //Auto stuff here

        traj(forward(5));
        if (team.equals(BLUE)) {
            traj(right(80));
        } else if (team.equals(RED)) {
            traj(left(80));
        }

        requestOpModeStop();
    }

    @Override
    public void loop() {}

    @Override
    public void stop() {
        //closeAndDisableRobotVision();
        //stopEOCV();
    }
}