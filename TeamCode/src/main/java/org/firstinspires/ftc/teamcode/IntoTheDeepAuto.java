package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.TeamColor.BLUE_LONG;
import static org.firstinspires.ftc.teamcode.TeamColor.RED_LONG;
import static org.firstinspires.ftc.teamcode.TeamColor.UNSET;

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
        initEOCV();

        //startAndEnableRobotVision();

        telemetry.addData("Status", "Ready to Run");
        telemetry.update();
    }

    @Override
    public void init_loop() {
        if (gamepad1.x) {
            team = BLUE_LONG;
        } else if (gamepad1.b) {
            team = RED_LONG;
        }
        telemetry.addData("Team", team.toString());
        if(team != UNSET) {
            if (gamepad1.y && runtime.milliseconds() - delayTime > 500) {
                delay++;
                delayTime = runtime.milliseconds();
                if (delay > 30) {
                    delay = 30;
                }
            } else if (gamepad1.a && runtime.milliseconds() - delayTime > 500) {
                delay--;
                delayTime = runtime.milliseconds();
                if (delay < 0) {
                    delay = 0;
                }
            }
            telemetry.addData("Delay", delay);
        }
        telemetry.update();

    }

    @Override
    public void start() {
        runtime.reset();
        resetYaw();

        stopEOCV();

        traj(forward(10));
        //Auto stuff here

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
