package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.TeamColor.BLUE_LONG;
import static org.firstinspires.ftc.teamcode.TeamColor.RED_LONG;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

/**Created by Gavin for FTC Team 6347 */
@TeleOp(name="CenterStageCVTest", group="OpMode")
//@Disabled
public class TemplateCVTest extends TemplateConfig {

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
            setStage(TemplatePipelineStage.LEFT);
        } else if (gamepad1.dpad_right) {
            setStage(TemplatePipelineStage.RIGHT);
        } else if (gamepad1.dpad_up) {
            setStage(TemplatePipelineStage.CENTER);
        } else if (gamepad1.dpad_down) {
            setStage(TemplatePipelineStage.FULL);
        } else if (gamepad1.right_bumper) {
            setStage(TemplatePipelineStage.FILTERED_CENTER);
        } else if (gamepad1.left_bumper) {
            setStage(TemplatePipelineStage.FILTERED_LEFT);
        }
        if (gamepad1.x) {
            team = BLUE_LONG;
        } else if (gamepad1.b) {
            team = RED_LONG;
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
