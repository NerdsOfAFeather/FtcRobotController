package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

/** Created by Gavin for Team 6347 */
@Autonomous(name = "CenterStageAuto", group = "Autonomous", preselectTeleOp = "CenterStageTeleOp")
public class CenterStageAuto extends CenterStageConfig {

    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void init() {
        telemetry.addData("Status", "Initializing...");
        telemetry.update();

        CenterStageMecanumDrive drive = new CenterStageMecanumDrive(hardwareMap);

        startAndEnableRobotVision();

        telemetry.addData("Status", "Ready to Run");
        telemetry.update();
    }

    @Override
    public void start() {
        runtime.reset();

        //Auto stuff here
    }

    @Override
    public void stop() {
        closeAndDisableRobotVision();
    }
}
