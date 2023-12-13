package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

/** Created by Gavin for Team 6347 */
@Autonomous(name = "CenterStageAutoLong", group = "Autonomous", preselectTeleOp = "CenterStageTeleOp")
public class CenterStageAuto extends CenterStageConfig {

    private ElapsedTime runtime = new ElapsedTime();

    CenterStageMecanumDrive drive;

    @Override
    public void init() {
        telemetry.addData("Status", "Initializing...");
        telemetry.update();

        drive = new CenterStageMecanumDrive(hardwareMap);

        //startAndEnableRobotVision();

        telemetry.addData("Status", "Ready to Run");
        telemetry.update();
    }

    @Override
    public void start() {
        runtime.reset();

        //Auto stuff here
        Trajectory straight1 = drive.trajectoryBuilder(new Pose2d()).forward(80).build();
        Trajectory right1 = drive.trajectoryBuilder(new Pose2d()).strafeRight(150).build();

        drive.followTrajectory(straight1);
        drive.followTrajectory(right1);
        this.stop();
        terminateOpModeNow();
    }

    @Override
    public void loop() {

    }

    @Override
    public void stop() {
        //closeAndDisableRobotVision();
    }
}
