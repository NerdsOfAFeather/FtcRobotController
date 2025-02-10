package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.TeamColor.*;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.util.ElapsedTime;

/**Created by Gavin for FTC Team 6347 */
@Autonomous(name = "IntoTheDeepAuto", group = "Autonomous", preselectTeleOp = "IntoTheDeepTeleOp2p")
@Disabled
public class IntoTheDeepAuto extends IntoTheDeepConfig {
    static int delay = 0;
    static boolean stopped = false;
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void init() {
        telemetry.addData("Status", "Initializing...");
        telemetry.update();

        initAuto();

        fClawL.setPosition(ClawState.OPEN.flPos);
        fClawR.setPosition(ClawState.OPEN.frPos);
        rClawL.setPosition(ClawState.CLOSED.blPos);
        rClawR.setPosition(ClawState.CLOSED.brPos);
        fWrist.setPosition(FrontArm.RETRACTED.wristPos);

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

        fClawL.setPosition(ClawState.OPEN.flPos);
        fClawR.setPosition(ClawState.OPEN.frPos);
        rClawL.setPosition(ClawState.CLOSED.blPos);
        rClawR.setPosition(ClawState.CLOSED.brPos);
        fWrist.setPosition(FrontArm.WRIST_DOWN.wristPos);

        if (team == RED_RIGHT) {

        }
    }

    @Override
    public void loop() {}

    @Override
    public void stop() {
        stopped = true;
    }
}
