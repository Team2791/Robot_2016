package org.usfirst.frc.team2791.helpers;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.util.Constants;
import org.usfirst.frc.team2791.util.NewShakerCamera;

import static org.usfirst.frc.team2791.robot.Robot.*;

/**
 * Created by Akhil on 1/28/2016.
 */
public class AutonHelper extends ShakerHelper {
    private int counter = 0;
    private double setPoint = 0;
    private double timeSinceShooterHigh = 0;
    private int previousCase = 0;
    private SendableChooser defenseNumber;
    private SendableChooser defenseToCross;
    private double angleWhenShooterSetHigh;

    public AutonHelper() {

        SmartDashboard.putNumber("Stationary Angle P", Constants.STATIONARY_ANGLE_P);
        SmartDashboard.putNumber("Stationary Angle I", Constants.STATIONARY_ANGLE_I);
        SmartDashboard.putNumber("Stationary Angle D", Constants.STATIONARY_ANGLE_D);
        SmartDashboard.putNumber("Angle P", Constants.DRIVE_ANGLE_P);
        SmartDashboard.putNumber("Angle I", Constants.DRIVE_ANGLE_I);
        SmartDashboard.putNumber("Angle D", Constants.DRIVE_ANGLE_D);
        SmartDashboard.putNumber("DISTANCE P", Constants.DRIVE_DISTANCE_P);
        SmartDashboard.putNumber("DISTANCE I", Constants.DRIVE_DISTANCE_I);
        SmartDashboard.putNumber("Distance D", Constants.DRIVE_DISTANCE_D);
        SmartDashboard.putNumber("Angle setpoint", setPoint);
        SmartDashboard.putNumber("pid distance travel", 1.0);
        SmartDashboard.getNumber("pid distance travel");
        SmartDashboard.putNumber("Auton step counter", counter);
        defenseNumber = new SendableChooser();// choose what number defense
        // robot is front of
        // 1 is all the way left 5 is all the way right
        SmartDashboard.putData("Auton Starting Position", defenseNumber);
        defenseNumber.addObject("1", "6");
        defenseNumber.addObject("2 Center", "7");// to center goal
        defenseNumber.addObject("2 Left", "14");// to the side goal
        defenseNumber.addObject("3", "8");
        defenseNumber.addObject("4 Center", "9");
        defenseNumber.addObject("4 Right", "15");// to side goal
        defenseNumber.addObject("5", "10");
        defenseNumber.addObject("test moving", "16");
        defenseNumber.addObject("test stationary", "17");
        defenseNumber = new SendableChooser();// 1 is all the way left 5 is all
        // the way right

        defenseToCross = new SendableChooser();
        SmartDashboard.putData("Auton Defnse type to cross", defenseToCross);
        defenseToCross.addObject("Low bar", "1");
        defenseToCross.addObject("Rough Terrain/Rock Wall/etc.", "2");
        defenseToCross.addObject("Cheval de Fries", "3");
        defenseToCross.addObject("Port Cullis", "4");
        defenseToCross.addObject("Gate", "5");
        counter = 0;
        previousCase = counter;
    }

    public void run() {
        double pidSweetSpotEnterTime = 0;
        // cases 6-10 will be how to act from after finishing defense
        int defenseStartPos = Integer.parseInt(defenseNumber.getSelected().toString());
        // 1-5 will decide how to maneuver the defense
        int defenseType = Integer.parseInt(defenseToCross.getSelected().toString());
        switch (counter) {// auton state machine
            case 0:// this state resets everything
                driveTrain.setLowGear();
                driveTrain.resetGyro();
                driveTrain.resetEncoders();
                intake.extendIntake();
                previousCase = counter;
                counter = defenseType;
                break;
            // these first five cases will traverse the designated defense type
            case 1:
                if (traverseLowBar()) {
                    previousCase = counter;
                    counter = defenseStartPos;
                }
                break;
            case 2:
                if (traverseUnevenTerrain()) {
                    previousCase = counter;
                    counter = defenseStartPos;
                }
                break;
            case 3:
                if (traverseFunBridges()) {
                    previousCase = counter;
                    counter = defenseStartPos;
                }
                break;
            case 4:
                if (traversePortCullis()) {
                    previousCase = counter;
                    counter = defenseStartPos;
                }
                break;
            case 5:
                if (traverseGate()) {
                    previousCase = counter;
                    counter = defenseStartPos;
                }
                break;

            // These next 5 choose where to move after crossing the defense
            case 6:
                if (defenseOneToShootingSpot()) {
                    previousCase = counter;
                    counter = 20;
                    driveTrain.resetEncoders();
                }
                break;
            case 7:
                if (defenseTwoToCenterShootingSpot()) {
                    previousCase = counter;
                    counter = 20;
                    driveTrain.resetEncoders();
                }
                break;
            case 8:
                if (defenseThreeToShootingSpot()) {
                    previousCase = counter;
                    counter = 20;
                    driveTrain.resetEncoders();
                }
                break;
            case 9:
                if (defenseFourToCenterShootingSpot()) {
                    previousCase = counter;
                    counter = 20;
                    driveTrain.resetEncoders();
                }
                break;
            case 10:
                if (defenseFiveToShootingSpot()) {
                    previousCase = counter;
                    counter = 20;
                    driveTrain.resetEncoders();
                }
                break;
            case 11:
                if (defenseTwoToLeftShootingSpot()) {
                    previousCase = counter;
                    counter = 20;
                    driveTrain.resetEncoders();
                }
                break;
            case 12:
                if (defenseFourToRightShootingSpot()) {
                    previousCase = counter;
                    counter = 20;
                    driveTrain.resetEncoders();
                }
                break;
            // For pid testing and tuning of dist pid
            case 16:
                previousCase = counter;
                driveTrain.driveInFeet(SmartDashboard.getNumber("pid distance travel"),
                        SmartDashboard.getNumber("Angle setpoint"), 0.5);
                break;

            case 17: // This is for testing the stationary angle pid
                previousCase = counter;
                driveTrain.setAngle(SmartDashboard.getNumber("Angle setpoint"));
                break;

            case 20:// 20-23 are the auto shooting procedure
                shooter.setShooterHigh();
                angleWhenShooterSetHigh = driveTrain.getAngle();
                timeSinceShooterHigh = Timer.getFPGATimestamp();
                counter++;
                break;
            case 21:
                if (Timer.getFPGATimestamp() - timeSinceShooterHigh > 0.7) {
                    shooter.autoFire();
                    counter++;
                }
                break;
            case 22:
                if (!shooter.getIfAutoFire())
                    counter = 999;
                break;
            case 999:
                driveTrain.setLeftRight(0, 0);
                System.out.println("Yea!!!!!!!! im done with auton");
                System.out.println("Last Known case before end of auton " + previousCase);
                counter++;
                break;
            // automatic vision code
            case 30:// still in testing phases
                //THE NEW CAMERA CLASS HAS NOT BEEN INITIALIZED!!!!!!!!!!!
                double setPoint = angleWhenShooterSetHigh + NewShakerCamera.ParticleReport.ThetaDifference.getValue();
                driveTrain.setAngle(setPoint);
            default:
                break;

            // case 1://next few cases are working low bar
            // if (driveTrain.driveInFeet(20, 0, 0.75))
            // counter++;
            // break;
            // case 2:
            // if (driveTrain.setAngle(60)) {
            // counter++;
            // driveTrain.resetEncoders();
            // }
            // break;
            // case 3:
            // if (driveTrain.driveInFeet(8, 60, 0.75)) {
            // counter++;
            // }
            // break;
            //
            // case 4:
            // shooter.setShooterHigh();
            // timeSinceShooterHigh = Timer.getFPGATimestamp();
            // counter++;
            // break;
            // case 5:
            // if (Timer.getFPGATimestamp() - timeSinceShooterHigh > 0.7) {
            // shooter.autoFire();
            // counter++;
            // }
            // break;
            // case 6:
            // if (!shooter.getIfAutoFire())
            // counter = 999;
            // break;

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

        Constants.STATIONARY_ANGLE_P = SmartDashboard.getNumber("Stat Angle P");
        Constants.STATIONARY_ANGLE_I = SmartDashboard.getNumber("Stat Angle I");
        Constants.STATIONARY_ANGLE_D = SmartDashboard.getNumber("Stat Angle D");

        Constants.DRIVE_ANGLE_P = SmartDashboard.getNumber("Angle P");
        Constants.DRIVE_ANGLE_I = SmartDashboard.getNumber("Angle I");
        Constants.DRIVE_ANGLE_D = SmartDashboard.getNumber("Angle D");

        Constants.DRIVE_DISTANCE_P = SmartDashboard.getNumber("DISTANCE P");
        Constants.DRIVE_DISTANCE_I = SmartDashboard.getNumber("DISTANCE I");
        Constants.DRIVE_DISTANCE_D = SmartDashboard.getNumber("Distance D");
        driveTrain.updateSmartDash();
    }

    @Override
    public void reset() {

    }

    public boolean traverseLowBar() {
        intake.extendIntake();
        return driveTrain.driveInFeet(7, 0, 0.5);
    }

    public boolean traverseUnevenTerrain() {
        // follows the same principle as traverse low bar but allows for greater
        // output for pid
        // intake.setArmAttachmentUp();
        return !driveTrain.driveInFeet(5, 0, 0.7);
    }

    public boolean traverseFunBridges() {// this is for future autons
        return false;
    }

    public boolean traversePortCullis() {// future auton
        return false;
    }

    public boolean traverseGate() {// future auton
        return false;
    }

    public boolean defenseOneToShootingSpot() {
        if (driveTrain.driveInFeet(12.4, 0, 0.5)) {
            if (driveTrain.driveInFeet(8, 60, 0.5))
                return true;
        }

        return false;
    }

    public boolean defenseTwoToLeftShootingSpot() {
        if (driveTrain.driveInFeet(14, 0, 0.5)) {
            if (driveTrain.driveInFeet(4, 45, 0.5))
                return true;
        }

        return false;
    }

    public boolean defenseTwoToCenterShootingSpot() {
        if (driveTrain.driveInFeet(12.4, 0, 0.5)) {
            if (driveTrain.driveInFeet(4.2, 90, 0.5)) {
                if (driveTrain.driveInFeet(2, -90, 0.5))

                    return true;
            }
        }

        return false;
    }

    public boolean defenseThreeToShootingSpot() {
        return driveTrain.driveInFeet(12, 0, 0.5);

    }

    public boolean defenseFourToCenterShootingSpot() {
        if (driveTrain.driveInFeet(10, 0, 0.5)) {
            if (driveTrain.driveInFeet(4.2, -90, 0.5)) {
                if (driveTrain.driveInFeet(2, 90, 0.5))

                    return true;
            }
        }

        return false;
    }

    public boolean defenseFourToRightShootingSpot() {
        if (driveTrain.driveInFeet(14, 0, 0.5)) {
            if (driveTrain.driveInFeet(4, -45, 0.5))
                return true;
        }

        return false;
    }

    public boolean defenseFiveToShootingSpot() {
        if (driveTrain.driveInFeet(12.4, 0, 0.5)) {
            if (driveTrain.driveInFeet(8, -60, 0.5))
                return true;
        }

        return false;
    }


    public void resetAutonStepCounter() {
        counter = 0;
    }


}