package org.usfirst.frc.team2791.helpers;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static org.usfirst.frc.team2791.robot.Robot.driveTrain;
import static org.usfirst.frc.team2791.robot.Robot.driverJoystick;


public class DriveHelper extends ShakerHelper {

    private SendableChooser driveTypeChooser;


    public DriveHelper() {
        driveTypeChooser = new SendableChooser();
        SmartDashboard.putData("Drive Chooser", driveTypeChooser);
        driveTypeChooser.addDefault("Tank Drive", "TANK");
        driveTypeChooser.addObject("Arcade Drive", "ARCADE");
        driveTypeChooser.addObject("GTA Drive", "GTA");
        driveTypeChooser.addObject("Single Arcade", "SINGLE_ARCADE");


    }

    public void teleopRun() {
        // Reads the current drive type to chooser what layout should be used
        // Tank: Left Stick (Y Dir) and Right Stick (Y Dir)
        // GTA: Left Trigger, Right Trigger, Right Stick (Y Dir)
        // Arcade: Left Stick (Y Dir), Right Stick (X Dir)
        switch (getDriveType()) {
            default:
            case TANK:
                driveTrain.setLeftRight(-driverJoystick.getAxisLeftY(), -driverJoystick.getAxisRightY());
                break;
            case GTA:
                driveTrain.setLeftRight(driverJoystick.getGtaDriveLeft(), driverJoystick.getGtaDriveRight());
                break;
            case ARCADE:
                driveTrain.setLeftRight(-driverJoystick.getAxisLeftY(), -driverJoystick.getAxisRightX());
                break;
            case SINGLE_ARCADE:
                driveTrain.setLeftRight(-driverJoystick.getAxisLeftY(), -driverJoystick.getAxisLeftX());
                break;
        }
        // Driver button layout
        // RB HIGH GEAR
        // LB LOW GEAR
        if (driverJoystick.getButtonRB())
            driveTrain.setHighGear();
        if (driverJoystick.getButtonLB())
            driveTrain.setLowGear();
        //resets all subsystems under the driver
        if (driverJoystick.getButtonSel())
            driveTrain.reset();

    }

    public void disableRun() {
        // runs disable methods of subsystems that fall under the driver
        driveTrain.disable();
    }

    public void updateSmartDash() {
        // updateSmartDash the smartDashboard values of subsystems
        driveTrain.updateSmartDash();
        SmartDashboard.putString("Current Driver Input:", getDriveType().toString());

    }

    public void reset() {

    }

    
    public DriveType getDriveType() {
        //reads data of the smart dashboard and converts to enum DriveType
        String driverInputType = (String) driveTypeChooser.getSelected();
        switch (driverInputType) {
            default:
            case "GTA":
                return DriveType.GTA;
            case "ARCADE":
                return DriveType.ARCADE;
            case "TANK":
                return DriveType.TANK;
            case "SINGLE_ARCADE":
                return DriveType.SINGLE_ARCADE;
        }
    }


    public enum DriveType {
        TANK, ARCADE, GTA, SINGLE_ARCADE
    }

}
