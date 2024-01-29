package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.TeamColor.BLUE;
import static org.firstinspires.ftc.teamcode.TeamColor.RED;

import android.annotation.SuppressLint;
import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.List;

/**Created by Gavin for FTC Team 6347 */
public abstract class CenterStageObjectDetection extends OpMode {

    /**
     * The variable to store our instance of the AprilTag processor.
     */
    private AprilTagProcessor aprilTag;

    /**
     * The variable to store our instance of the TensorFlow Object Detection processor.
     */
    private TfodProcessor tfod;

    /**
     * The variable to store our instance of the vision portal.
     */
    private VisionPortal visionPortal;
    private OpenCvWebcam webcam;
    public static TeamColor team = TeamColor.UNSET;
    static int position;

    static CenterStagePipelineStage stage = CenterStagePipelineStage.FULL;

    protected void startAndEnableRobotVision() {
        initAprilTag();
        initTfod();
        initVisionPortal();
        enableAprilTagProcessor();
        enableTFODProcessor();
    }

    protected void closeAndDisableRobotVision() {
        disableAprilTagProcessor();
        disableTFODProcessor();
        closeVisionPortal();
    }

    /**
     * Initialize the webcam for use with EOCV
     */
    public void initEOCV(){
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam"), cameraMonitorViewId);

        // OR...  Do Not Activate the Camera Monitor View
        //webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam"));
        webcam.setPipeline(new CenterStagePipeline());

        webcam.setMillisecondsPermissionTimeout(5000);
        webcam.openCameraDeviceAsync(new CenterStageCameraOpener());
    }

    public void stopEOCV() {
        webcam.stopStreaming();
        webcam.closeCameraDevice();
    }

    /**
     * Initialize the AprilTag processor.
     */
    protected void initAprilTag() {

        // Create the AprilTag processor.
        aprilTag = new AprilTagProcessor.Builder()
                .setDrawAxes(true)
                .setDrawCubeProjection(false)
                .setDrawTagOutline(true)
                .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                .setTagLibrary(AprilTagGameDatabase.getCenterStageTagLibrary())
                .setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)

                // == CAMERA CALIBRATION ==
                // If you do not manually specify calibration parameters, the SDK will attempt
                // to load a predefined calibration for your camera.
                //.setLensIntrinsics(578.272, 578.272, 402.145, 221.506)

                // ... these parameters are fx, fy, cx, cy.

                .build();

    }

    protected void initVisionPortal() {
        // Create the vision portal by using a builder.
        VisionPortal.Builder builder = new VisionPortal.Builder();

        // Set the camera
        builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam"));

        // Choose a camera resolution
        builder.setCameraResolution(new Size(640, 480));

        // Enable the RC preview (LiveView).  Set "false" to omit camera monitoring.
        builder.enableLiveView(true);

        // Set the stream format; MJPEG uses less bandwidth than default YUY2.
        builder.setStreamFormat(VisionPortal.StreamFormat.MJPEG);

        // Choose whether or not LiveView stops if no processors are enabled.
        // If set "true", monitor shows solid orange screen if no processors enabled.
        // If set "false", monitor shows camera view without annotations.
        builder.setAutoStopLiveView(false);

        // Set and enable the processor.
        builder.addProcessor(aprilTag);
        builder.addProcessor(tfod);

        // Build the Vision Portal, using the above settings.
        visionPortal = builder.build();
    }

    protected void enableAprilTagProcessor() {
        visionPortal.setProcessorEnabled(aprilTag, true);
    }

    protected void disableAprilTagProcessor() {
        visionPortal.setProcessorEnabled(aprilTag, false);
    }

    protected void enableTFODProcessor() {
        visionPortal.setProcessorEnabled(tfod, true);
    }

    protected void disableTFODProcessor() {
        visionPortal.setProcessorEnabled(tfod, false);
    }

    protected void closeVisionPortal() {
        visionPortal.close();
    }


    /**
     * Add telemetry about AprilTag detections.
     */
    @SuppressLint("DefaultLocale")
    protected void telemetryAprilTag() {

        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        telemetry.addData("# AprilTags Detected", currentDetections.size());

        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
                telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
            } else {
                telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
                telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
            }
        }

        telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
        telemetry.addLine("RBE = Range, Bearing & Elevation");

    }

    protected void initTfod() {

        // Create the TensorFlow processor by using a builder.
        tfod = new TfodProcessor.Builder()

                // Use setModelAssetName() if the TF Model is built in as an asset.
                // Use setModelFileName() if you have downloaded a custom team model to the Robot Controller.
                //.setModelAssetName(TFOD_MODEL_ASSET)
                //.setModelFileName(TFOD_MODEL_FILE)

                //.setModelLabels(LABELS)
                //.setIsModelTensorFlow2(true)
                //.setIsModelQuantized(true)
                //.setModelInputSize(300)
                //.setModelAspectRatio(16.0 / 9.0)

                .build();

        // Set confidence threshold for TFOD recognitions, at any time.
        tfod.setMinResultConfidence(0.75f);

    }

    /**
     * Add telemetry about TensorFlow Object Detection (TFOD) recognitions.
     */
    private void telemetryTfod() {

        List<Recognition> currentRecognitions = tfod.getRecognitions();
        telemetry.addData("# Objects Detected", currentRecognitions.size());

        // Step through the list of recognitions and display info for each one.
        for (Recognition recognition : currentRecognitions) {
            double x = (recognition.getLeft() + recognition.getRight()) / 2 ;
            double y = (recognition.getTop()  + recognition.getBottom()) / 2 ;

            telemetry.addData(""," ");
            telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100);
            telemetry.addData("- Position", "%.0f / %.0f", x, y);
            telemetry.addData("- Size", "%.0f x %.0f", recognition.getWidth(), recognition.getHeight());
        }

    }

    public final void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static int getPosition() {
        return position;
    }

    public void setStage(CenterStagePipelineStage newStage){
        stage = newStage;
    }

    class CenterStagePipeline extends OpenCvPipeline {



        boolean viewportPaused;

        @Override
        public Mat processFrame(Mat input) {
            Rect lRect = new Rect(point(0,0), point(input.cols()/3f, input.rows()));
            Rect rRect = new Rect(point(input.cols()*(2f/3f),0), point(input.cols(), input.rows()));

            Mat converted = input.clone();
            Imgproc.cvtColor(input, converted, Imgproc.COLOR_BGR2HSV);
            Mat left = converted.submat(lRect);
            Mat right = converted.submat(rRect);

            double[] bgr = input.get(input.rows()/2, input.cols()/2);
            double[] hsv = converted.get(converted.rows()/2, converted.cols()/2);
            telemetry.addData("BGR", bgr[0] +", " + bgr[1] + ", " + bgr[2]);
            telemetry.addData("HSV", hsv[0] + ", " + hsv[1] + ", " + hsv[2]);
            telemetry.addData("Position", getPosition());
            telemetry.update();

            Mat filteredL = new Mat();
            Mat filteredR = new Mat();
            int totalPixels = converted.rows() * converted.cols();

            if (team.equals(TeamColor.RED)) {
                Core.inRange(left, new Scalar(100, 0, 0), new Scalar(130, 255, 255), filteredL); // RED 100-130 BLUE 0-30
                Core.inRange(right, new Scalar(100, 0, 0), new Scalar(130, 255, 255), filteredR);
            } else {
                Core.inRange(left, new Scalar(0, 0, 0), new Scalar(30, 255, 255), filteredL);
                Core.inRange(right, new Scalar(0, 0, 0), new Scalar(30, 255, 255), filteredR);
            }

            Imgproc.rectangle(converted, lRect, new Scalar(255,0,0));
            Imgproc.rectangle(converted, rRect, new Scalar(0,0,255));

            int lPer = Core.countNonZero(filteredL);
            int rPer = Core.countNonZero(filteredR);
            lPer = lPer / totalPixels;
            rPer = rPer / totalPixels;
            if (lPer >= 0.5f) {
                position = 1;
            } else if (rPer >= 0.5f) {
                position = 3;
            } else {
                position = 2;
            }

            switch (stage){
                case LEFT:
                    return left;
                case RIGHT:
                    return right;
                case FILTERED_LEFT:
                    return filteredL;
                case FILTERED_RIGHT:
                    return filteredR;
                case CENTER:
                    return converted.submat(0, converted.rows(), converted.cols()/3, (int) (converted.cols()*(2f/3f)));
                default:
                    return converted;
            }
        }

        @Override
        public void onViewportTapped() {

            viewportPaused = !viewportPaused;

            if(viewportPaused)
            {
                webcam.pauseViewport();
            }
            else
            {
                webcam.resumeViewport();
            }
        }

        private Point point(double x, double y) {
            return new Point(x, y);
        }
    }

    class CenterStageCameraOpener implements OpenCvCamera.AsyncCameraOpenListener {

        @Override
        public void onOpened() {
            webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
        }

        @Override
        public void onError(int errorCode) {
            RobotLog.setGlobalErrorMsg("Error in OpenCV Camera Opening. Error Code: " + errorCode);
        }
    }

    public enum CenterStagePipelineStage {

        FULL, LEFT, RIGHT, CENTER, FILTERED_LEFT, FILTERED_RIGHT;
    }

}

