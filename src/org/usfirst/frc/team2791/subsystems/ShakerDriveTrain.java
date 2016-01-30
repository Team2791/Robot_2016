package org.usfirst.frc.team2791.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.configuration.Constants;
import org.usfirst.frc.team2791.configuration.Ports;
import org.usfirst.frc.team2791.robot.Robot;

public class ShakerDriveTrain extends ShakerSubsystem {
    private Talon leftTalonA;
    private Talon leftTalonB;
    private Talon rightTalonA;
    private Talon rightTalonB;
    private RobotDrive roboDrive;
    private DriveType driveType;
    private gear gearState;
    private DoubleSolenoid driveSolenoid;
    private SendableChooser driveTypeChooser;


    public ShakerDriveTrain() {
        init();
    }

    protected void init() {
        this.leftTalonA = new Talon(Ports.DRIVE_TALON_LEFT_PORT_FRONT);
        this.leftTalonB = new Talon(Ports.DRIVE_TALON_LEFT_PORT_BACK);
        this.rightTalonA = new Talon(Ports.DRIVE_TALON_RIGHT_PORT_FRONT);
        this.rightTalonB = new Talon(Ports.DRIVE_TALON_RIGHT_PORT_BACK);
        this.roboDrive = new RobotDrive(leftTalonA, leftTalonB, rightTalonA, rightTalonB);
        roboDrive.stopMotor();
        this.driveSolenoid = new DoubleSolenoid(Ports.DRIVE_PISTON_FORWARD, Ports.DRIVE_PISTON_REVERSE);
        driveTypeChooser = new SendableChooser();
        SmartDashboard.putData("Sync Chooser", driveTypeChooser);
        driveTypeChooser.addDefault("Tank Drive", "TANK");
        driveTypeChooser.addObject("Arcade Drive", "ARCADE");
        driveTypeChooser.addObject("GTA Drive", "GTA");
        driveType = DriveType.TANK;
        refreshDriverInputType();
    }

    public void run() {
        //nothing here?
    }

    public void reset() {
        this.disable();
        this.setLowGear();
        //this.setDriveType((String) driveTypeChooser.getSelected());
    }

    public void update() {
        SmartDashboard.putNumber("Gear : ", isHighGear() ? 2 : 1);
        SmartDashboard.putString("Current Driver Input:", getDriveType().toString());
    }

    public void disable() {
        roboDrive.stopMotor();
    }

    public void setLeftRight(double left, double right) {
        switch (Robot.safetyMode) {
            case SAFETY:
                left *= Constants.FULL_SPEED_SAFETY_MODE;
                right *= Constants.FULL_SPEED_SAFETY_MODE;
                this.setLowGear();
                break;
            case FULL_CONTROL:
                break;
        }
        switch (driveType) {
            default:
            case TANK:
                roboDrive.setLeftRightMotorOutputs(left, right);
                break;
            case GTA:
                roboDrive.setLeftRightMotorOutputs(left, right);
                break;
            case ARCADE:
                roboDrive.arcadeDrive(left, right);
        }
    }

    public boolean isHighGear() {
        refreshGearState();
        switch (gearState) {
            case HIGH:
                return true;
            case LOW:
                return false;
        }
        return false;
    }

    private void refreshGearState() {
        if (driveSolenoid.get().equals(Constants.DRIVE_HIGH_GEAR))
            gearState = gear.HIGH;
        else if (driveSolenoid.get().equals(Constants.DRIVE_LOW_GEAR))
            gearState = gear.LOW;
        else gearState = gear.LOW;//removing null pointer exceptions testing purposes only
    }

    private void refreshDriverInputType() {
        String temp = (String) driveTypeChooser.getSelected();
        if (temp != null) {
            setDriveType(temp);
        }
    }

    public void setHighGear() {
        if (!isHighGear() && !Robot.safetyMode.equals(Robot.SafetyMode.SAFETY)) {
            gearState = gear.HIGH;
            driveSolenoid.set(Constants.DRIVE_HIGH_GEAR);
        }

    }

    public void setLowGear() {
        if (isHighGear()) {
            gearState = gear.LOW;
            driveSolenoid.set(Constants.DRIVE_LOW_GEAR);
        }
    }

    public void safetyOverride() {

    }

    public DriveType getDriveType() {
        return driveType;
    }

    public void setDriveType(String driverInputType) {
        if (driverInputType.equals("GTA")) {
            driveType = DriveType.GTA;
        } else if (driverInputType.equals("ARCADE")) {
            driveType = DriveType.ARCADE;
        } else if (driverInputType.equals("TANK")) {
            driveType = DriveType.TANK;
        } else
            driveType = DriveType.GTA;
    }

    private enum gear {
        HIGH, LOW
    }

    public enum DriveType {
        TANK, ARCADE, GTA
    }

}
