package org.usfirst.frc.team2791.helpers;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.configuration.PID;

import static org.usfirst.frc.team2791.robot.Robot.driveTrain;
import static org.usfirst.frc.team2791.robot.Robot.intake;

/**
 * Created by Akhil on 1/28/2016.
 */
public class AutonHelper extends ShakerHelper {
    private int counter = 0;
    private double setPoint = 0;
    private double lowBar_sweetSpotTime = 0;
    private double timeSinceShooterHigh = 0;
    private int microCounter = 0;
    private SendableChooser defenseNumber;
    private SendableChooser defenseToCross;

    public AutonHelper() {

        SmartDashboard.putNumber("Angle P", PID.STATIONARY_ANGLE_P);
        SmartDashboard.putNumber("Angle I", PID.STATIONARY_ANGLE_I);
        SmartDashboard.putNumber("Angle D", PID.STATIONARY_ANGLE_D);

        SmartDashboard.putNumber("DISTANCE P", PID.DRIVE_DISTANCE_P);
        SmartDashboard.putNumber("DISTANCE I", PID.DRIVE_DISTANCE_I);
        SmartDashboard.putNumber("Distance D", PID.DRIVE_DISTANCE_D);
        SmartDashboard.putNumber("Angle setpoint", setPoint);

        SmartDashboard.putNumber("pid distance travel", 1.0);
        SmartDashboard.putNumber("Auton step counter", counter);
        defenseNumber = new SendableChooser();//choose what number defense robot is front of
        //1 is all the way left 5 is all the way right
        SmartDashboard.putData("Auton Starting Position", defenseNumber);
        defenseNumber.addObject("1", "1");
        defenseNumber.addObject("2", "2");
        defenseNumber.addObject("3", "3");
        defenseNumber.addObject("4", "4");
        defenseNumber.addObject("5", "5");
        defenseNumber = new SendableChooser();//1 is all the way left 5 is all the way right

        defenseToCross = new SendableChooser();
        SmartDashboard.putData("Auton Defnse type to cross", defenseToCross);
        defenseToCross.addObject("Low bar", "1");
        defenseToCross.addObject("Rough Terrain/Rock Wall/etc.", "2");
        defenseToCross.addObject("Cheval de Fries", "3");
        defenseToCross.addObject("Port Cullis", "4");
        defenseToCross.addObject("Gate", "5");
        counter = 0;
        microCounter = 0;
    }

    public void run() {
        double pidSweetSpotEnterTime = 0;
        int defenseStartPos = Integer.parseInt(defenseNumber.getSelected().toString()) + 5;//cases 6-10 will be how to act from after finishing defense
        int defenseType = Integer.parseInt(defenseNumber.getSelected().toString());//1-5 will decide how to maneuver the defense
        switch (counter) {//auton state machine
            case 0://this state resets everything
                driveTrain.setLowGear();
                driveTrain.resetGyro();
                driveTrain.resetEncoders();
                intake.extendIntake();
                counter = defenseType;
                microCounter = 0;
                break;
            case 1://this is for low bar this will do the same thing as case 2 except bring the intake down

                counter++;
            case 2:
                traverseUnevenTerrain();
//            case 1://next few cases are working low bar
//                if (driveTrain.driveInFeet(20, 0, 0.75))
//                    counter++;
//                break;
//            case 2:
//                if (driveTrain.setAngle(60)) {
//                    counter++;
//                    driveTrain.resetEncoders();
//                }
//                break;
//            case 3:
//                if (driveTrain.driveInFeet(8, 60, 0.75)) {
//                    counter++;
//                }
//                break;
//
//            case 4:
//                shooter.setShooterHigh();
//                timeSinceShooterHigh = Timer.getFPGATimestamp();
//                counter++;
//                break;
//            case 5:
//                if (Timer.getFPGATimestamp() - timeSinceShooterHigh > 0.7) {
//                    shooter.autoFire();
//                    counter++;
//                }
//                break;
//            case 6:
//                if (!shooter.getIfAutoFire())
//                    counter = 999;
//                break;
//            // case 15:
//            // driveTrain.setAngle(SmartDashboard.getNumber("Angle setpoint"));
//            // break;
//            default:
//            case 999:
//                driveTrain.setLeftRight(0, 0);
//                System.out.println("Yea!!!!!!!! im done with auton");
//                break;

        }
        // counter = (int) SmartDashboard.getNumber("Auton step counter");
        SmartDashboard.putNumber("Auton step counter", counter);
    }

    @Override
    public void disableRun() {
        driveTrain.disable();
        counter = 0;
    }

    @Override
    public void updateSmartDash() {

        PID.STATIONARY_ANGLE_P = SmartDashboard.getNumber("Angle P");
        PID.STATIONARY_ANGLE_I = SmartDashboard.getNumber("Angle I");
        PID.STATIONARY_ANGLE_D = SmartDashboard.getNumber("Angle D");

        PID.DRIVE_DISTANCE_P = SmartDashboard.getNumber("DISTANCE P");
        PID.DRIVE_DISTANCE_I = SmartDashboard.getNumber("DISTANCE I");
        PID.DRIVE_DISTANCE_D = SmartDashboard.getNumber("Distance D");
        driveTrain.updateSmartDash();
    }

    @Override
    public void reset() {

    }

    public boolean traverseLowBar() {// This should be the distance from the neutral zone to right after the low
//         bar it waits for the pid to be good for at least 0.5 seconds before giving true
        // intake.armAttachmentDown();
        intake.extendIntake();
        return driveTrain.driveInFeet(19.4, 0, 0.5);
//        return driveTrain.driveInFeet(19.4, 0, 0.5);
        // if (!driveTrain.driveInFeet(19.4, 0, 0.5)) {
        // lowBar_sweetSpotTime = Timer.getFPGATimestamp();
        // }
        // if (Timer.getFPGATimestamp() - lowBar_sweetSpotTime > 0.5)
        // return true;
    }

    public boolean traverseUnevenTerrain() {
        // follows the same principle as traverse low bar but allows for greater
        // output for pid
        // intake.setArmAttachmentUp();
        return !driveTrain.driveInFeet(4, 0, 0.7);
    }

    public boolean traverseFunBridges()

    {
        return false;
    }

    public boolean defenseOneToShootingSpot() {
        if (driveTrain.driveInFeet(8, 0, 0.5)) {
            if (driveTrain.setAngle(60))
                return true;
        }

        return false;
    }

    public boolean overridenAutoShoot() {
        return false;
//		shooter.setShooterHigh();// brings the arm to the high pos
//		if (Timer.getFPGATimestamp() - time > 0.7) {
//			shooter.autoFire();
//		}
//
//		return false;
    }

    public void resetAutonStepCounter() {
        counter = 0;
    }

    public enum defense { // uneven terrain applies to rough terrain,moat, etc.
        // ... things that challenge our drive train
        LOW_BAR, UNEVEN_TERRAIN
    }

    public enum autonPosition {// this is used to determine how the robot should
        // act after crossing the defense
        ONE, TWO, THREE, FOUR, FIVE
    }
}
