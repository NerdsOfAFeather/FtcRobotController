package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.TeamColor.BLUE;
import static org.firstinspires.ftc.teamcode.TeamColor.RED;
import static org.firstinspires.ftc.teamcode.TeamColor.UNSET;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

/**Created by Gavin for FTC Team 6347 */
@Autonomous(name = "CenterStageAutoShorter", group = "Autonomous", preselectTeleOp = "CenterStageTeleOp")
public class CenterStageAutoShorter extends CenterStageConfig {

    private ElapsedTime runtime = new ElapsedTime();
    TeamColor team = UNSET;

    @Override
    public void init() {
        telemetry.addData("Status", "Initializing...");
        telemetry.update();

        initAuto();
        initDriveHardware();

        clawServoL.setPosition(1.0);
        clawServoR.setPosition(0.0);

        //startAndEnableRobotVision();
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

        if (team.equals(RED)) {
            traj(forward(5));
            traj(right(80));
        } else if (team.equals(BLUE)) {
            traj(forward(5));
            traj(left(80));
        }

        sleep(750);
        intakeMotor.setPower(-.25);
        sleep(2000);
        intakeMotor.setPower(0);
        clawServoR.setPosition(1.0);
        clawServoL.setPosition(0.0);



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
