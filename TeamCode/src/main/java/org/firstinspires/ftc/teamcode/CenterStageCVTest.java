package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.TeamColor.BLUE;
import static org.firstinspires.ftc.teamcode.TeamColor.RED;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

/**Created by Gavin for FTC Team 6347 */
@TeleOp(name="CenterStageCVTest", group="OpMode")
public class CenterStageCVTest extends CenterStageConfig {

    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void init() {
        initDriveHardware();
        initEOCV();
    }

    @Override
    public void init_loop() {
        //telemetry.addData("Position", getPosition());
        //telemetry.addData("Bingus", "Bongus");
        //telemetry.update();
        if (gamepad1.dpad_left) {
            setStage(CenterStagePipelineStage.LEFT);
        } else if (gamepad1.dpad_right) {
            setStage(CenterStagePipelineStage.RIGHT);
        } else if (gamepad1.dpad_up) {
            setStage(CenterStagePipelineStage.CENTER);
        } else if (gamepad1.dpad_down) {
            setStage(CenterStagePipelineStage.FULL);
        } else if (gamepad1.right_bumper) {
            setStage(CenterStagePipelineStage.FILTERED_CENTER);
        } else if (gamepad1.left_bumper) {
            setStage(CenterStagePipelineStage.FILTERED_LEFT);
        }
        if (gamepad1.x) {
            team = BLUE;
        } else if (gamepad1.b) {
            team = RED;
        }
        
    }

    @Override
    public void start() {
        runtime.reset();
    }

    @Override
    public void loop() {

    }

    @Override
    public void stop() {
        stopEOCV();
    }
}
