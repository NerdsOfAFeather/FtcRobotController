package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.TeamColor.BLUE;
import static org.firstinspires.ftc.teamcode.TeamColor.RED;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.util.ElapsedTime;

/**Created by Gavin for FTC Team 6347 */
@Autonomous(name = "CenterStageAutoLonger", group = "Autonomous", preselectTeleOp = "CenterStageTeleOp")
//@Disabled
public class CenterStageAutoLonger extends CenterStageConfig {

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
        clawServoL.setPosition(0.0);
        clawServoR.setPosition(1.0);
        traj(forward(5));
        if (team.equals(BLUE)) {
            traj(left(50));
            turnLeft(.5);
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
        } else if (team.equals(RED)) {
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
