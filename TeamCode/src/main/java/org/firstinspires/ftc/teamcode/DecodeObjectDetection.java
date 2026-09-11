package org.firstinspires.ftc.teamcode;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.opencv.Circle;
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor;
import org.firstinspires.ftc.vision.opencv.ColorRange;
import org.firstinspires.ftc.vision.opencv.ImageRegion;

import java.util.List;

/**Created by Gavin for FTC Team 6347 */
public abstract class DecodeObjectDetection extends OpMode {

    /**
     * The variable to store our instance of the AprilTag processor.
     */
    private AprilTagProcessor aprilTag = null;

    /**
     * The variable to store our instance of the green ColorBlobLocator processor.
     */
    private ColorBlobLocatorProcessor greenLocator = null;

    /**
     * The variable to store our instance of the purple ColorBlobLocator processor.
     */
    private ColorBlobLocatorProcessor purpleLocator = null;

    /**
     * The variable to store our instance of the vision portal.
     */
    private VisionPortal visionPortal = null;

    private final Position cameraPosition = new Position(DistanceUnit.INCH,
            0, 0, 0, 0);
    private final YawPitchRollAngles cameraOrientation = new YawPitchRollAngles(AngleUnit.DEGREES,
            0, -100, 0, 0);


    /**
     * Initialize the AprilTag processor.
     */
    protected void initAprilTag() {

        // Create the AprilTag processor.
        aprilTag = new AprilTagProcessor.Builder()

                // The following default settings are available to un-comment and edit as needed.
                //.setDrawAxes(false)
                //.setDrawCubeProjection(false)
                //.setDrawTagOutline(true)
                //.setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                //.setTagLibrary(AprilTagGameDatabase.getCenterStageTagLibrary())
                //.setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)
                .setCameraPose(cameraPosition, cameraOrientation)

                // == CAMERA CALIBRATION ==
                // If you do not manually specify calibration parameters, the SDK will attempt
                // to load a predefined calibration for your camera.
                .setLensIntrinsics(50, 50, 350, 200)
                // ... these parameters are fx, fy, cx, cy.

                .build();

        // Adjust Image Decimation to trade-off detection-range for detection-rate.
        // eg: Some typical detection data using a Logitech C920 WebCam
        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second (default)
        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second (default)
        // Note: Decimation can be changed on-the-fly to adapt during a match.
        aprilTag.setDecimation(4);
    }

    protected void initBlobLocators() {
        /* Build a "Color Locator" vision processor based on the ColorBlobLocatorProcessor class.
         * - Specify the color range you are looking for. Use a predefined color, or create your own
         *
         *   .setTargetColorRange(ColorRange.BLUE)     // use a predefined color match
         *     Available colors are: RED, BLUE, YELLOW, GREEN, ARTIFACT_GREEN, ARTIFACT_PURPLE
         *   .setTargetColorRange(new ColorRange(ColorSpace.YCrCb,  // or define your own color match
         *                                       new Scalar( 32, 176,  0),
         *                                       new Scalar(255, 255, 132)))
         *
         * - Focus the color locator by defining a RegionOfInterest (ROI) which you want to search.
         *     This can be the entire frame, or a sub-region defined using:
         *     1) standard image coordinates or 2) a normalized +/- 1.0 coordinate system.
         *     Use one form of the ImageRegion class to define the ROI.
         *       ImageRegion.entireFrame()
         *       ImageRegion.asImageCoordinates(50, 50,  150, 150)  100x100 pixels at upper left corner
         *       ImageRegion.asUnityCenterCoordinates(-0.5, 0.5, 0.5, -0.5)  50% width/height in center
         *
         * - Define which contours are included.
         *   You can get ALL the contours, ignore contours that are completely inside another contour.
         *     .setContourMode(ColorBlobLocatorProcessor.ContourMode.ALL_FLATTENED_HIERARCHY)
         *     .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)
         *     EXTERNAL_ONLY helps to avoid bright reflection spots from breaking up solid colors.
         *
         * - Turn the displays of contours ON or OFF.
         *     Turning these on helps debugging but takes up valuable CPU time.
         *        .setDrawContours(true)                Draws an outline of each contour.
         *        .setEnclosingCircleColor(int color)   Draws a circle around each contour. 0 to disable.
         *        .setBoxFitColor(int color)            Draws a rectangle around each contour. 0 to disable. ON by default.
         *
         *
         * - include any pre-processing of the image or mask before looking for Blobs.
         *     There are some extra processing you can include to improve the formation of blobs.
         *     Using these features requires an understanding of how they may effect the final
         *     blobs.  The "pixels" argument sets the NxN kernel size.
         *        .setBlurSize(int pixels)
         *        Blurring an image helps to provide a smooth color transition between objects,
         *        and smoother contours.  The higher the number, the more blurred the image becomes.
         *        Note:  Even "pixels" values will be incremented to satisfy the "odd number" requirement.
         *        Blurring too much may hide smaller features.  A size of 5 is good for a 320x240 image.
         *
         *     .setErodeSize(int pixels)
         *        Erosion removes floating pixels and thin lines so that only substantive objects remain.
         *        Erosion can grow holes inside regions, and also shrink objects.
         *        "pixels" in the range of 2-4 are suitable for low res images.
         *
         *     .setDilateSize(int pixels)
         *        Dilation makes objects and lines more visible by filling in small holes, and making
         *        filled shapes appear larger. Dilation is useful for joining broken parts of an
         *        object, such as when removing noise from an image.
         *        "pixels" in the range of 2-4 are suitable for low res images.
         *
         *        .setMorphOperationType(MorphOperationType morphOperationType)
         *        This defines the order in which the Erode/Dilate actions are performed.
         *        OPENING:    Will Erode and then Dilate which will make small noise blobs go away
         *        CLOSING:    Will Dilate and then Erode which will tend to fill in any small holes in blob edges.
         */
        purpleLocator = new ColorBlobLocatorProcessor.Builder()
                .setTargetColorRange(ColorRange.ARTIFACT_PURPLE)   // Use a predefined color match
                .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)
                .setRoi(ImageRegion.asUnityCenterCoordinates(-0.75, 0.75, 0.75, -0.75))
                .setDrawContours(true)   // Show contours on the Stream Preview
                .setBoxFitColor(0)       // Disable the drawing of rectangles
                .setCircleFitColor(Color.rgb(255, 255, 0)) // Draw a circle
                .setBlurSize(5)          // Smooth the transitions between different colors in image

                // the following options have been added to fill in perimeter holes.
                .setDilateSize(15)       // Expand blobs to fill any divots on the edges
                .setErodeSize(15)        // Shrink blobs back to original size
                .setMorphOperationType(ColorBlobLocatorProcessor.MorphOperationType.CLOSING)

                .build();

        greenLocator = new ColorBlobLocatorProcessor.Builder()
                .setTargetColorRange(ColorRange.ARTIFACT_GREEN)   // Use a predefined color match
                .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)
                .setRoi(ImageRegion.asUnityCenterCoordinates(-0.75, 0.75, 0.75, -0.75))
                .setDrawContours(true)   // Show contours on the Stream Preview
                .setBoxFitColor(0)       // Disable the drawing of rectangles
                .setCircleFitColor(Color.rgb(255, 255, 0)) // Draw a circle
                .setBlurSize(5)          // Smooth the transitions between different colors in image

                // the following options have been added to fill in perimeter holes.
                .setDilateSize(15)       // Expand blobs to fill any divots on the edges
                .setErodeSize(15)        // Shrink blobs back to original size
                .setMorphOperationType(ColorBlobLocatorProcessor.MorphOperationType.CLOSING)

                .build();
    }

    protected void buildVisionPortal() {
        VisionPortal.Builder builder = new VisionPortal.Builder();

        builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));

        builder.setCameraResolution(new Size(640, 480));

        if (greenLocator != null) {
            builder.addProcessor(greenLocator);
        }

        if (purpleLocator != null) {
            builder.addProcessor(purpleLocator);
        }

        if (aprilTag != null) {
            builder.addProcessor(aprilTag);
        }

        builder.setStreamFormat(VisionPortal.StreamFormat.MJPEG);

        visionPortal = builder.build();
    }

    protected void disableAprilTag() {
        visionPortal.setProcessorEnabled(aprilTag, false);
    }

    protected void enableAprilTag() {
        visionPortal.setProcessorEnabled(aprilTag, true);
    }

    protected void pausePortalStream() {
        visionPortal.stopStreaming();
    }

    protected void resumePortalStream() {
        visionPortal.resumeStreaming();
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

        // Step through the list of detections and display info for each one.
        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
                // Only use tags that don't have Obelisk in them
                if (!detection.metadata.name.contains("Obelisk")) {
                    telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)",
                            detection.robotPose.getPosition().x,
                            detection.robotPose.getPosition().y,
                            detection.robotPose.getPosition().z));
                    telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)",
                            detection.robotPose.getOrientation().getPitch(AngleUnit.DEGREES),
                            detection.robotPose.getOrientation().getRoll(AngleUnit.DEGREES),
                            detection.robotPose.getOrientation().getYaw(AngleUnit.DEGREES)));
                }
            } else {
                telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
                telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
            }
        }   // end for() loop

        // Add "key" information to telemetry
        telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
    }

    @SuppressLint("DefaultLocale")
    protected void telemetryColorBlob() {

        // Read the current list
        List<ColorBlobLocatorProcessor.Blob> purpleBlobs = purpleLocator.getBlobs();

        List<ColorBlobLocatorProcessor.Blob> greenBlobs = greenLocator.getBlobs();

        /*
         * The list of Blobs can be filtered to remove unwanted Blobs.
         *   Note:  All contours will be still displayed on the Stream Preview, but only those
         *          that satisfy the filter conditions will remain in the current list of
         *          "blobs".  Multiple filters may be used.
         *
         * To perform a filter
         *   ColorBlobLocatorProcessor.Util.filterByCriteria(criteria, minValue, maxValue, blobs);
         *
         * The following criteria are currently supported.
         *
         * ColorBlobLocatorProcessor.BlobCriteria.BY_CONTOUR_AREA
         *   A Blob's area is the number of pixels contained within the Contour.  Filter out any
         *   that are too big or small. Start with a large range and then refine the range based
         *   on the likely size of the desired object in the viewfinder.
         *
         * ColorBlobLocatorProcessor.BlobCriteria.BY_DENSITY
         *   A blob's density is an indication of how "full" the contour is.
         *   If you put a rubber band around the contour you would get the "Convex Hull" of the
         *   contour. The density is the ratio of Contour-area to Convex Hull-area.
         *
         * ColorBlobLocatorProcessor.BlobCriteria.BY_ASPECT_RATIO
         *   A blob's Aspect ratio is the ratio of boxFit long side to short side.
         *   A perfect Square has an aspect ratio of 1.  All others are > 1
         *
         * ColorBlobLocatorProcessor.BlobCriteria.BY_ARC_LENGTH
         *   A blob's arc length is the perimeter of the blob.
         *   This can be used in conjunction with an area filter to detect oddly shaped blobs.
         *
         * ColorBlobLocatorProcessor.BlobCriteria.BY_CIRCULARITY
         *   A blob's circularity is how circular it is based on the known area and arc length.
         *   A perfect circle has a circularity of 1.  All others are < 1
         */
        ColorBlobLocatorProcessor.Util.filterByCriteria(
                ColorBlobLocatorProcessor.BlobCriteria.BY_CONTOUR_AREA,
                150, 20000, purpleBlobs);  // filter out very small blobs.

        ColorBlobLocatorProcessor.Util.filterByCriteria(
                ColorBlobLocatorProcessor.BlobCriteria.BY_CIRCULARITY,
                0.6, 1, purpleBlobs);

        ColorBlobLocatorProcessor.Util.filterByCriteria(
                ColorBlobLocatorProcessor.BlobCriteria.BY_CONTOUR_AREA,
                150, 20000, greenBlobs);  // filter out very small blobs.

        ColorBlobLocatorProcessor.Util.filterByCriteria(
                ColorBlobLocatorProcessor.BlobCriteria.BY_CIRCULARITY,
                0.6, 1, greenBlobs);     /* filter out non-circular blobs.
         * NOTE: You may want to adjust the minimum value depending on your use case.
         * Circularity values will be affected by shadows, and will therefore vary based
         * on the location of the camera on your robot and venue lighting. It is strongly
         * encouraged to test your vision on the competition field if your event allows
         * sensor calibration time.
         */

        /*
         * The list of Blobs can be sorted using the same Blob attributes as listed above.
         * No more than one sort call should be made.  Sorting can use ascending or descending order.
         * Here is an example.:
         *   ColorBlobLocatorProcessor.Util.sortByCriteria(
         *      ColorBlobLocatorProcessor.BlobCriteria.BY_CONTOUR_AREA, SortOrder.DESCENDING, blobs);
         */

        telemetry.addLine("Circularity Radius Center");

        telemetry.addLine("Purple Blobs: ");

        // Display the Blob's circularity, and the size (radius) and center location of its circleFit.
        for (ColorBlobLocatorProcessor.Blob b : purpleBlobs) {

            Circle circleFit = b.getCircle();
            telemetry.addLine(String.format("%5.3f      %3d     (%3d,%3d)",
                    b.getCircularity(), (int) circleFit.getRadius(), (int) circleFit.getX(), (int) circleFit.getY()));
        }

        telemetry.addLine("Green Blobs: ");

        for (ColorBlobLocatorProcessor.Blob b : greenBlobs) {

            Circle circleFit = b.getCircle();
            telemetry.addLine(String.format("%5.3f      %3d     (%3d,%3d)",
                    b.getCircularity(), (int) circleFit.getRadius(), (int) circleFit.getX(), (int) circleFit.getY()));
        }

    }

    protected List<AprilTagDetection> getDetections() {
        return aprilTag.getDetections();
    }

    protected ArtifactDetection getArtifactCounts() {
        return new ArtifactDetection(purpleLocator, greenLocator);
    }


    public final void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static class ArtifactDetection {
        int purple;

        int green;

        public ArtifactDetection(ColorBlobLocatorProcessor purpleLocator, ColorBlobLocatorProcessor greenLocator) {
            this.purple = purpleLocator.getBlobs().size();
            this.green = greenLocator.getBlobs().size();
        }
    }

}

