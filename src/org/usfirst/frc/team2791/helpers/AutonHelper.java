package org.usfirst.frc.team2791.helpers;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.commands.AutoLineUpShot;
import org.usfirst.frc.team2791.practicebotSubsystems.PracticeShakerShooter.ShooterHeight;
import org.usfirst.frc.team2791.util.Constants;

import static org.usfirst.frc.team2791.robot.Robot.*;

/**
 * Created by Akhil on 1/28/2016.
 */
public class AutonHelper extends ShakerHelper {
    private static AutonHelper auton;
    private int counter = 0;
    private int microCounter = 0;
    private double setPoint = 0;
    private double timeSinceShooterHigh = 0;
    private int previousCase = 0;
    private SendableChooser defenseNumber;
    private SendableChooser defenseToCross;

    private AutonHelper() {
        SmartDashboard.putNumber("Stat Angle P", Constants.STATIONARY_ANGLE_P);
        SmartDashboard.putNumber("Stat Angle I", Constants.STATIONARY_ANGLE_I);
        SmartDashboard.putNumber("Stat Angle D", Constants.STATIONARY_ANGLE_D);
        SmartDashboard.putNumber("Angle P", Constants.DRIVE_ANGLE_P);
        SmartDashboard.putNumber("Angle I", Constants.DRIVE_ANGLE_I);
        SmartDashboard.putNumber("Angle D", Constants.DRIVE_ANGLE_D);
        SmartDashboard.putNumber("DISTANCE P", Constants.DRIVE_DISTANCE_P);
        SmartDashboard.putNumber("DISTANCE I", Constants.DRIVE_DISTANCE_I);
        SmartDashboard.putNumber("Distance D", Constants.DRIVE_DISTANCE_D);
        SmartDashboard.putNumber("Angle setpoint", setPoint);
        SmartDashboard.putNumber("max speed", 0.5);
        SmartDashboard.putNumber("pid distance travel", 1.0);
        SmartDashboard.putNumber("Auton step counter", counter);
        defenseNumber = new SendableChooser();// choose what number defense
        // robot is front of
        // 1 is all the way left 5 is all the way right
        defenseNumber.addObject("1", "6");
        defenseNumber.addObject("2 Center", "7");// to center goal
        defenseNumber.addObject("2 Left", "14");// to the side goal
        defenseNumber.addObject("3", "8");
        defenseNumber.addObject("4 Center", "9");
        defenseNumber.addObject("4 Right", "15");// to side goal
        defenseNumber.addObject("5", "10");
        defenseNumber.addObject("AutoLineup", 100);
        defenseNumber.addObject("AutoLineup + lowBar", 101);
        defenseNumber.addObject("test moving", "16");
        defenseNumber.addObject("test stationary", "17");
        SmartDashboard.putData("Auton Starting Position", defenseNumber);
        // the way right

        defenseToCross = new SendableChooser();
        SmartDashboard.putData("Auton Defnse type to cross", defenseToCross);
        defenseToCross.addObject("Low bar", "1");
        defenseToCross.addObject("Rough Terrain/Rock Wall/etc.", "2");
        defenseToCross.addObject("Cheval de Fries", "3");
        defenseToCross.addObject("Port Cullis", "4");
        defenseToCross.addObject("Gate", "5");
        counter = 0;
        SmartDashboard.putData("Auton Starting Position", defenseNumber);

    }

    public static AutonHelper getInstance() {
        if (auton == null)
            auton = new AutonHelper();
        return auton;
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
                intake.setArmAttachmentUp();
                counter = defenseType;
                break;
            // these first five cases will traverse the designated defense type
            case 1:
                if (traverseLowBar()) {
                    counter = 107;
                }
                break;
            case 2:
                intake.retractIntake();
                intake.setArmAttachmentUp();
                if (traverseUnevenTerrain()) {
                    counter = 107;
                }
                break;
            case 3:
                if (traverseFunBridges()) {
                    counter = 107;
                }
                break;
            case 4:
                if (traversePortCullis()) {
                    counter = 107;
                }
                break;
            case 5:
                if (traverseGate()) {
                    counter = 107;
                }
                break;
            case 100:
                AutoLineUpShot.run();
                counter = 102;
            case 101:
                if (driveTrain.setAngle(20, 0.4))
                    counter++;
                break;
            case 102:
                if (AutoLineUpShot.isRunning())
                    AutoLineUpShot.run();
                else
                    counter = 20;
                break;
            case 107:
                //pre auto lineup configuration
                intake.extendIntake();
                intake.setArmAttachmentDown();
                shooter.delayedShooterPosition(ShooterHeight.HIGH);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                intake.retractIntake();
                counter = defenseStartPos;
                break;
            // These next 5 choose where to move after crossing the defense
            case 6:
                if (defenseOneToShootingSpot()) {
                    counter = 20;
                    driveTrain.resetEncoders();
                }
                break;
            case 7:
                if (defenseTwoToCenterShootingSpot()) {
                    counter = 20;
                    driveTrain.resetEncoders();
                }
                break;
            case 8:
                if (defenseThreeToShootingSpot()) {
                    counter = 20;
                    driveTrain.resetEncoders();
                }
                break;
            case 9:
                if (defenseFourToCenterShootingSpot()) {
                    counter = 20;
                    driveTrain.resetEncoders();
                }
                break;
            case 10:
                if (defenseFiveToShootingSpot()) {
                    counter = 20;
                    driveTrain.resetEncoders();
                }
                break;
            case 11:
                if (defenseTwoToLeftShootingSpot()) {
                    counter = 20;
                    driveTrain.resetEncoders();
                }
                break;
            case 12:
                if (defenseFourToRightShootingSpot()) {
                    counter = 20;
                    driveTrain.resetEncoders();
                }
                break;
            // For pid testing and tuning of dist pid
            case 16:
                driveTrain.driveInFeet(SmartDashboard.getNumber("pid distance travel"),
                        SmartDashboard.getNumber("Angle setpoint"), 0.7);
                break;
            case 17: // This is for testing the stationary angle pid
                driveTrain.setAngle(SmartDashboard.getNumber("Angle setpoint"), 0.5);
                break;
            case 20:
                System.out.println("Done with auton");
                break;
        }
        SmartDashboard.putNumber("Auton step counter", counter);
    }

    public void disableRun() {
        driveTrain.disable();
        counter = 0;
    }

    public void updateSmartDash() {
    }

    public void reset() {

    }

    public void debug() {

    }

    public boolean traverseLowBar() {
        intake.extendIntake();
        return driveTrain.driveInFeet(7, 0, 0.5);
    }

    public boolean traverseUnevenTerrain() {
        // follows the same principle as traverse low bar but allows for greater
        // output for pid
        // intake.setArmAttachmentUp();
        return driveTrain.driveInFeet(5, 0, 0.7);
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
        switch (microCounter) {
            case 0:
                if (driveTrain.driveInFeet(14, 0, 0.5))
                    microCounter++;
            case 1:
                if (driveTrain.driveInFeet(4, 45, 0.5))
                    microCounter++;
            case 2:
                return true;

        }
        return false;
    }

    public boolean defenseTwoToCenterShootingSpot() {
        switch (microCounter) {
            case 0:
                if (driveTrain.driveInFeet(12.4, 0, 0.5))
                    microCounter++;
            case 1:
                if (driveTrain.driveInFeet(4.2, 90, 0.5))
                    microCounter++;
            case 2:
                if (driveTrain.driveInFeet(2, -90, 0.5))
                    microCounter++;
            case 3:
                return true;
        }
        return false;
    }

    public boolean defenseThreeToShootingSpot() {
        return driveTrain.driveInFeet(12, 0, 0.5);

    }

    public boolean defenseFourToCenterShootingSpot() {
        switch (microCounter) {
            case 0:
                if (driveTrain.driveInFeet(10, 0, 0.5))
                    microCounter++;
            case 1:
                if (driveTrain.driveInFeet(4.2, -90, 0.5))
                    microCounter++;
            case 2:
                if (driveTrain.driveInFeet(2, 90, 0.5))
                    microCounter++;
            case 3:
                return true;
        }
        return false;

    }

    public boolean defenseFourToRightShootingSpot() {
        switch (microCounter) {
            case 0:
                if (driveTrain.driveInFeet(14, 0, 0.5))
                    microCounter++;
            case 1:
                if (driveTrain.driveInFeet(4, -45, 0.5))
                    microCounter++;
            case 3:
                return true;
        }
        return false;
    }

    public boolean defenseFiveToShootingSpot() {
        switch (microCounter) {
            case 0:
                if (driveTrain.driveInFeet(12.4, 0, 0.5))
                    microCounter++;
            case 1:
                if (driveTrain.driveInFeet(8, -60, 0.5))
                    microCounter++;
            case 3:
                return true;
        }
        return false;

    }

    public void resetAutonStepCounter() {
        counter = 0;
    }

}