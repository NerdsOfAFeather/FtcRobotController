package org.firstinspires.ftc.teamcode.old;

import static org.firstinspires.ftc.teamcode.TeamColor.BLUE_LONG;
import static org.firstinspires.ftc.teamcode.TeamColor.RED_LONG;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.CenterStageConfig;

/**Created by Gavin for FTC Team 6347 */
@Autonomous(name = "OldCenterStageAutoLonger", group = "Autonomous", preselectTeleOp = "CenterStageTeleOp")
@Disabled
public class OldCenterStageAutoLonger extends CenterStageConfig {

    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void init() {
        telemetry.addData("Status", "Initializing...");
        telemetry.update();

        initAuto();
        //initEOCV();

        //startAndEnableRobotVision();

        telemetry.addData("Status", "Ready to Run");
        telemetry.update();
        clawServoL.setPosition(0.0);
        clawServoR.setPosition(1.0);
    }

    @Override
    public void init_loop() {
        if (gamepad1.x) {
            team = BLUE_LONG;
        } else if (gamepad1.b) {
            team = RED_LONG;
        }
        telemetry.addData("Team", team.toString());
        telemetry.update();
    }

    @Override
    public void start() {
        runtime.reset();

        //Auto stuff here
        clawServoL.setPosition(0.0);
        clawServoR.setPosition(1.0);
        traj(forward(5));
        if (team.equals(BLUE_LONG)) {
            traj(left(50));
            //turnLeft(.5);
            traj(right(10));
            intakeMotor2.setPower(0.5);
            try {
                wait(1);
            } catch (InterruptedException e) {
                terminateOpModeNow();
            }
            intakeMotor2.setPower(0);
            clawServoL.setPosition(0.5);
            clawServoR.setPosition(0.5);
        } else if (team.equals(RED_LONG)) {
            traj(right(50));

            traj(left(10));
            intakeMotor2.setPower(0.5);
            try {
                wait(1);
            } catch (InterruptedException e) {
                terminateOpModeNow();
            }
            intakeMotor2.setPower(0);
            clawServoL.setPosition(0.5);
            clawServoR.setPosition(0.5);
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
