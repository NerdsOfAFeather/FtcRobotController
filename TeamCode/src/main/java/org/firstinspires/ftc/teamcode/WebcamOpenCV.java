/*package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;

@Disabled
public class WebcamOpenCV extends LinearOpMode {

    OpenCvCamera webcam;

    public void startOpenCv(){
        int cameraMoniterVeiwId = hardwareMap.appContext.getResources().getIdentifier("cameraMoniterVeiwId", "id",
                hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMoniterVeiwId);
        webcam.setPipeline(new SamplePipeline());

        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
            }
        });
        telemetry.addLine("Waiting for Start");
        telemetry.update();
    }

    public void runOpenCv(){
        while (opModeIsActive()){
            telemetry.addData("Frame Count", webcam.getFrameCount());
            telemetry.addData("FPS", String.format("%.2f",webcam.getFps()));
            telemetry.addData("Total frame time ms", webcam.getTotalFrameTimeMs());
            telemetry.addData("Overhead time ms", webcam.getOverheadTimeMs());
            telemetry.addData("Pipeline time ms", webcam.getPipelineTimeMs());
            telemetry.addData("Theoretivcal Max FPS", webcam.getCurrentPipelineMaxFps());
            telemetry.update();

            if (gamepad1.a){
                webcam.stopStreaming();
            }
            sleep(100);
        }
    }

    class SamplePipeline extends OpenCvPipeline {

        boolean viewportPaused;

        @Override
        public Mat processFrame(Mat input) {
            Imgproc.rectangle(
                    input,
                    new Point(
                            input.cols() / 4,
                            input.cols() / 4),
                    new Point(
                            input.cols() * (3f / 4f),
                            input.rows() * (3f / 4f)),
                    new Scalar(0, 255, 0),
                    4);
            return input;
        }

        @Override
        public void onViewportTapped(){
            viewportPaused = !viewportPaused;

            if (viewportPaused){
                webcam.pauseViewport();
            }else{
                webcam.resumeViewport();
            }
        }
    }



    @Override
    public void runOpMode() throws InterruptedException {
    }
}
*/