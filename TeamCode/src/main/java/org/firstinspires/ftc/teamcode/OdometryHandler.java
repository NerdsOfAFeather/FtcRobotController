package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import java.util.ArrayList;
import java.util.List;

public class OdometryHandler extends PowerPlayConfig {

    private static boolean threedeadwheels;
    private static DcMotor xEncoder;
    private static DcMotor zEncoder1;
    private static DcMotor zEncoder2;
    private static IMU robotIMU;
    private static ArrayList<String> steps = new ArrayList<>();
    private static List<Integer> stepsint = new ArrayList<>();


    static final double     COUNTS_PER_MOTOR_REV    = 8192 ;   // eg: GoBILDA 312 RPM Yellow Jacket
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // No External Gearing.
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);


    /**
     * <p>
     * For use with 2 encoders:
     * <p>
     * Set encoderX as the one facing east-west on your robot
     * <p>
     * Set encoderZ as the one facing north-south on your robot
     * <p>
     * Set imu as your robot's IMU
    */
    public void configureOdometry(DcMotor encoderX, DcMotor encoderZ, IMU imu){
        threedeadwheels = false;
        xEncoder = encoderX;
        zEncoder1 = encoderZ;
        robotIMU = imu;
    }

    /**
     * For use with 3 encoders:
     * Set encoderX as the one facing east-west on your robot
     * Set encoderZ as one facing north-south on your robot
     * Set encoderZ2 as the other one facing north-south on your robot
     */
    public void configureOdometry(DcMotor encoderX, DcMotor encoderZ, DcMotor encoderZ2){
        threedeadwheels = true;
        xEncoder = encoderX;
        zEncoder1 = encoderZ;
        zEncoder2 = encoderZ2;
    }

    /**
     * Step formatting:
     * 1. strait or turn (s or t)
     * 2. Direction (c or cc for turning / x or z for strait )
     * 3. Distance or degrees (integer) supports negative
     */
    public void addStep(String string, int integer){
        steps.add(string);
        stepsint.add(integer);
    }

    public void start(){
        if (steps == null || stepsint == null) {
            return;
        }
        for (int i = 0; i <= steps.size(); i++){
            String step = steps.get(i);
            int stepint = stepsint.get(i);
            if (step.contains("s")){
                if (step.contains("x")){
                    while (xEncoder.getCurrentPosition() <= inchesToCounts(stepint)){

                    }
                } else if (step.contains("z")){

                }
            } else if (step.contains("t")){
                if (step.contains("c")){

                } else if (step.contains("cc")){

                }
            }
        }
    }

    private int inchesToCounts(int inches){
        return (int) (inches * COUNTS_PER_INCH);
    }

}
