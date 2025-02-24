package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.TeamColor.BLUE_LEFT;
import static org.firstinspires.ftc.teamcode.TeamColor.BLUE_RIGHT;
import static org.firstinspires.ftc.teamcode.TeamColor.RED_LEFT;
import static org.firstinspires.ftc.teamcode.TeamColor.RED_RIGHT;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.stampede.DriveTo;
import org.firstinspires.ftc.teamcode.stampede.Stampede;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

@SuppressWarnings("unused")
@Autonomous(name = "StampedePoweredAuto", group = "Autonomous", preselectTeleOp = "StampedeEnhancedTeleOp")
public class IntoTheDeepAuto extends IntoTheDeepConfig {
    boolean liftPosSet = false;
    boolean liftInPosition = false;
    DriveTo driveTo;
    Stampede stampede;
    // This is the FIRST state for the State Machine
    String nextState = "actionStart";
    // We'll set this when we need to wait for an action to complete rather than check if the lift or drive is busy.
    double wait = 0;
    int count = 4;

    // For where coordinates are on the field for our different auto modes (diff start positions, ect.)
    HashMap<String, double[]> drivePositionsAudienceRed = new HashMap<>();
    HashMap<String, double[]> drivePositionsAudienceBlue = new HashMap<>();
    HashMap<String, double[]> drivePositionsBackRed = new HashMap<>();
    HashMap<String, double[]> drivePositionsBackBlue = new HashMap<>();
    HashMap<String, double[]> drivePositions;

    @Override
    public void init() {
        stampede = new Stampede();
        stampede.init(hardwareMap);
        initAttachmentHardware();
        rearArmServo.setPosition(RearArm.IN_ROBOT.elbowPos);
        rearWrist.setPosition(RearArm.IN_ROBOT.wristPos);
        rClawL.setPosition(ClawState.CLOSED.blPos);
        rClawR.setPosition(ClawState.CLOSED.brPos);
        fClawL.setPosition(ClawState.CLOSED.flPos);
        fClawR.setPosition(ClawState.CLOSED.frPos);
        fWrist.setPosition(0.5);

        driveTo = new DriveTo(stampede, telemetry);

        //x, y, heading for start positions
        drivePositionsAudienceRed.put("start", new double[]{-48, -72, 90});
        drivePositionsAudienceBlue.put("start", new double[]{0, -72, 90});
        drivePositionsBackRed.put("start", new double[]{0, -72, 90});
        drivePositionsBackBlue.put("start", new double[]{-48, -72, 90});

        drivePositionsAudienceRed.put("Position 1", new double[]{-48, -60, 90});
        drivePositionsAudienceBlue.put("Position 1", new double[]{0, -48, 90});
        drivePositionsBackRed.put("Position 1", new double[]{0, -48, 90});
        drivePositionsBackBlue.put("Position 1", new double[]{-48, -60, 90});

        drivePositionsAudienceRed.put("Position 2", new double[]{-60, -60, 90});
        drivePositionsAudienceBlue.put("Position 2", new double[]{72, -48, 90});
        drivePositionsBackRed.put("Position 2", new double[]{0, -48, 270});
        drivePositionsBackBlue.put("Position 2", new double[]{-60, -60, 90});

        drivePositionsAudienceRed.put("Position 3", new double[]{-69, -66, 45});
        drivePositionsAudienceBlue.put("Position 3", new double[]{0, -36, 270});
        drivePositionsBackRed.put("Position 3", new double[]{0, -36, 270});
        drivePositionsBackBlue.put("Position 3", new double[]{-69, -66, 45});

        drivePositionsAudienceRed.put("Position 4", new double[]{-54, -46, 90});
        drivePositionsBackBlue.put("Position 4", new double[]{-54, -46, 90});

        drivePositionsAudienceRed.put("Position 5", new double[]{-62, -46, 90});
        drivePositionsBackBlue.put("Position 5", new double[]{-62, -46, 90});

        drivePositionsAudienceRed.put("Position 6", new double[]{-62, -46, 90});
        drivePositionsBackBlue.put("Position 6", new double[]{-62, -46, 90});
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
        //Pick from our hashmap for color and side
        if (team.equals(RED_LEFT)) {
            drivePositions = drivePositionsAudienceRed;
        } else if (team.equals(BLUE_RIGHT)) {
            drivePositions = drivePositionsAudienceBlue;
        } else if (team.equals(RED_RIGHT)) {
            drivePositions = drivePositionsBackRed;
        } else {
            drivePositions = drivePositionsBackBlue;
        }

        //setting start position, [0] is x, [1] is y, [2] is heading
        stampede.xFieldPos = drivePositions.get("start")[0];
        stampede.yFieldPos = drivePositions.get("start")[1];
        stampede.headingField = drivePositions.get("start")[2];
        stampede.angleTracker.setOrientation(stampede.headingField);
    }

    @Override
    public void loop() {
        stampede.updateFieldPosition();
        telemetry.addData("Field Position (Coordinates)", "%.2f, %.2f, %.2f", stampede.xFieldPos, stampede.yFieldPos, stampede.headingField);
        telemetry.addData("IMU Orientation", "IMU %.2f", stampede.angleTracker.getOrientation());
        telemetry.addData("Next action", nextState);

        driveTo.sendTelemetry(telemetry);
        driveTo.updateDrive();

        if (!isBusy()) {
            // Variable used in getDeclaredMethod cannot be changed within the loop because it is being used,
            // so we create another variable so that we may change nextState!
            String currentState = nextState;
            try {
                // Inspect our own class to see if we have an action method with the name we specified.

                Method stateMethod = this.getClass().getMethod(currentState);
                // Invoke it with our class instance.
                stateMethod.invoke(this);
                // Catch exceptions to keep the compiler happy.
            } catch (NoSuchMethodException exc) {
                // This will be caught when we haven't defined the method (e.g., for "done").
            } catch (IllegalAccessException exc) {
                // We don't expect this one.
                telemetry.addData("exception", "IllegalAccessException when calling " + currentState + " " + exc);
            } catch (InvocationTargetException exc) {
                // We don't expect this one.
                telemetry.addData("exception", "InvocationTargetException when calling " + currentState + " " +
                        exc.getTargetException() + " " + exc.getTargetException().getStackTrace()[0]);
            }
        }
    }

    @Override
    public void stop() {
        stampede.drive(0.0, 0.0, 0.0, telemetry);
        driveTo.areWeThereYet = true;
    }

    // Conditions that the robot is busy in.
    Boolean isBusy() {
        // If robot isn't there yet its busy.
        if (!driveTo.areWeThereYet) {
            return true;
        }
        /*
        You can check for other busy conditions like this (e.g., for any other motors you want to move in auto).

        if (robot.isLiftBusy()) {
            return true;
        }
        */
        return getRuntime() < wait;
    }

    // This is the State Machine, it's the "steps" the robot will follow.
    public void actionStart() {
        driveTo.setTargetPosition(drivePositions.get("Position 1"), .25);
        // This is how you can add a wait.
        //wait = getRuntime() + 5;
        // Name what the next action should be.
        nextState = "actionStep2";
    }

    public void actionStep2() {
        // stopBetween is whether the robot will stop between positions, or just drive through the position.
        driveTo.setTargetPosition(drivePositions.get("Position 2"), .25, false);
        nextState = "actionStep3";
    }

    public void actionStep3() {
        driveTo.setTargetPosition(drivePositions.get("Position 3"), .5);
        if (drivePositions.equals(drivePositionsAudienceRed) || drivePositions.equals(drivePositionsBackBlue)) 
            nextState = "actionRaiseLiftForSample";
        else 
            nextState = "actionStop";
    }

    public void actionRaiseLiftForSample() {
        if (!liftPosSet) {
            rearLiftMotor.setTargetPosition(RearArm.DEPOSIT_SAMPLE.liftHeight.motorPos);
            rearLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rearLiftMotor.setPower(1.0);
            liftPosSet = true;
            nextState = "actionRaiseLiftForSample";
        } else if (!liftInPosition) {
            rearLiftMotor.setPower(1.0);
            if (Math.abs(rearLiftMotor.getTargetPosition() - rearLiftMotor.getCurrentPosition()) <= 50)
                liftInPosition = true;
            nextState = "actionRaiseLiftForSample";
        } else {
            rearLiftMotor.setPower(0.0);
            rearArmServo.setPosition(RearArm.DEPOSIT_SAMPLE.elbowPos);
            rearWrist.setPosition(RearArm.DEPOSIT_SAMPLE.wristPos);
            wait = getRuntime() + 2;
            nextState = "actionDepositSample";
        }
        
    }

    public void actionDepositSample() {
        rClawL.setPosition(ClawState.OPEN.blPos);
        rClawR.setPosition(ClawState.OPEN.brPos);
        wait = getRuntime() + 2;
        liftPosSet = false;
        liftInPosition = false;
        nextState = "actionRetractArm";
    }

    public void actionRetractArm() {
        rearArmServo.setPosition(RearArm.IN_ROBOT.elbowPos);
        rearWrist.setPosition(RearArm.IN_ROBOT.wristPos);
        if (!liftPosSet) {
            rearLiftMotor.setTargetPosition(RearArm.IN_ROBOT.liftHeight.motorPos);
            rearLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rearLiftMotor.setPower(1.0);
            liftPosSet = true;
            nextState = "actionRetractArm";
        } else if (!liftInPosition) {
            rearLiftMotor.setPower(1.0);
            if (Math.abs(rearLiftMotor.getTargetPosition() - rearLiftMotor.getCurrentPosition()) <= 50)
                liftInPosition = true;
            nextState = "actionRetractArm";
        } else {
            rearLiftMotor.setPower(0.0);
            if (count == 6)
                nextState = "actionStop";
            else
                nextState = "actionDriveToNextPresetSample";
        }
    }

    public void actionDriveToNextPresetSample() {
        driveTo.setTargetPosition(drivePositions.get("Position " + count), .5);
        fClawL.setPosition(ClawState.OPEN.flPos);
        fClawR.setPosition(ClawState.OPEN.frPos);
        nextState = "actionLowerWrist";
    }

    public void actionLowerWrist() {
        fWrist.setPosition(FrontArm.WRIST_DOWN.wristPos);
        wait = getRuntime() + 1;
        nextState = "actionGrabSample";
    }

    public void actionGrabSample() {
        fClawL.setPosition(ClawState.CLOSED.flPos);
        fClawR.setPosition(ClawState.CLOSED.frPos);
        wait = getRuntime() + 1;
        nextState = "actionRaiseWrist";
    }

    public void actionRaiseWrist() {
        fWrist.setPosition(FrontArm.RETRACTED.wristPos);
        wait = getRuntime() + 1;
        nextState = "actionCloseRearClaw";
    }

    public void actionCloseRearClaw() {
        rClawL.setPosition(ClawState.CLOSED.blPos);
        rClawR.setPosition(ClawState.CLOSED.brPos);
        wait = getRuntime() + 1;
        nextState = "actionOpenFrontClaw";
    }

    public void actionOpenFrontClaw() {
        fClawL.setPosition(ClawState.OPEN.flPos);
        fClawR.setPosition(ClawState.OPEN.frPos);
        wait = getRuntime() + 1;
        nextState = "actionStep3";
        count++;
    }

    public void actionStop() {
        stampede.drive(0.0, 0.0, 0.0, telemetry);
        driveTo.areWeThereYet = true;
        nextState = "actionDone";
    }
}