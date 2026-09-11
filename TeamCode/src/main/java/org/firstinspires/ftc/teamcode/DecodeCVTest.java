package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

/**Created by Gavin for FTC Team 6347 */
@TeleOp(name="DecodeCVTest", group="OpMode")
@Disabled
public class DecodeCVTest extends DecodeConfig {

    private final ElapsedTime runtime = new ElapsedTime();

    @Override
    public void init() {
        initDriveHardware();
        initAprilTag();
        initBlobLocators();
        buildVisionPortal();

        // Wait for the DS start button to be touched.
        telemetry.addData("DS preview on/off", "3 dots, Camera Stream");
        telemetry.addData(">", "Touch START to start OpMode");
        telemetry.update();

    }

    @Override
    public void start() {
        runtime.reset();
    }

    @Override
    public void loop() {

        telemetryAprilTag();
        telemetryColorBlob();

        // Push telemetry to the Driver Station.
        telemetry.update();

        // Save CPU resources; can resume streaming when needed.
        if (gamepad1.dpad_down) {
            pausePortalStream();
        } else if (gamepad1.dpad_up) {
            resumePortalStream();
        }

        // Share the CPU.
        sleep(20);
    }

    @Override
    public void stop() {
        closeVisionPortal();
    }
}
