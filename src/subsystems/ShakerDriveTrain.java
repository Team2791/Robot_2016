package subsystems;

import configuration.Constants;
import configuration.Ports;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.robot.Robot.DriveType;
import org.usfirst.frc.team2791.robot.Robot.SafetyMode;

public class ShakerDriveTrain extends ShakerSubsystem {
    private Talon leftTalonA;
    private Talon leftTalonB;
    private Talon rightTalonA;
    private Talon rightTalonB;
    private RobotDrive roboDrive;
    private DriveType driveType;
    private gear gearState;
    private SafetyMode safeMode;
    private Solenoid leftDriveSolenoid;
    private Solenoid rightDriveSolenoid;
    private Command driveCommand;
    private SendableChooser driveTypeChooser;

    public ShakerDriveTrain() {
        init();

    }

    protected void init() {
        this.leftTalonA = new Talon(Ports.LEFT_TALON_PORT_A);
        this.leftTalonB = new Talon(Ports.LEFT_TALON_PORT_B);
        this.rightTalonA = new Talon(Ports.RIGHT_TALON_PORT_A);
        this.rightTalonB = new Talon(Ports.RIGHT_TALON_PORT_B);
        this.roboDrive = new RobotDrive(leftTalonA, leftTalonB, rightTalonA, rightTalonB);
        this.disable();
        this.leftDriveSolenoid = new Solenoid(Ports.LEFT_DRIVE_PISTON_CHANNEL, Ports.PCM_MODULE);
        this.rightDriveSolenoid = new Solenoid(Ports.RIGHT_DRIVE_PISTON_CHANNEL, Ports.PCM_MODULE);
        driveTypeChooser = new SendableChooser();
        SmartDashboard.putData("Sync Chooser", driveTypeChooser);
        driveTypeChooser.addDefault("Tank Drive", "TANK");
        driveTypeChooser.addObject("Arcade Drive", "ARCADE");
        driveTypeChooser.addObject("GTA Drive", "GTA");
        setDriveType((String) driveTypeChooser.getSelected());
    }

    public void run() {
        // nothing here?
    }

    public void reset() {
        this.disable();
        this.setLowGear();
        this.setDriveType((String) driveTypeChooser.getSelected());
    }

    public void update() {
        SmartDashboard.putNumber("Gear : ", isHighGear() ? 2 : 1);
        SmartDashboard.putString("Current Driver Input:", getDriveInputType());
    }

    public void disable() {
        roboDrive.stopMotor();
    }

    public void setLeftRight(double left, double right) {
        this.setDriveType((DriveType) driveTypeChooser.getSelected());
        switch (safeMode) {
            case SAFETY:
                left *= Constants.FULL_SPEED_SAFETY_MODE;
                right *= Constants.FULL_SPEED_SAFETY_MODE;
                this.setLowGear();
                break;
            case FULL_CONTROL:
                break;
        }
        switch (driveType) {
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

    public void setDriveType(String driverInputType) {
        switch (driverInputType) {
            default:
            case "TANK":
                driveType = DriveType.TANK;
                break;
            case "GTA":
                driveType = DriveType.GTA;
                break;
            case "ARCADE":
                driveType = DriveType.ARCADE;
        }
    }

    public void setDriveType(DriveType driverInputType) {
        driveType = driverInputType;
    }

    public void setSafeMode(SafetyMode safety) {
        this.safeMode = safety;
    }

    public boolean isHighGear() {
        switch (gearState) {
            case HIGH:
                return true;
            case LOW:
                return false;
        }
        return false;
    }

    public void setHighGear() {
        if (!isHighGear()) {
            gearState = gear.HIGH;
            leftDriveSolenoid.set(Constants.DRIVE_HIGH_GEAR);
            rightDriveSolenoid.set(Constants.DRIVE_HIGH_GEAR);
        }

    }

    public void setLowGear() {
        if (isHighGear()) {
            gearState = gear.LOW;
            leftDriveSolenoid.set(Constants.DRIVE_LOW_GEAR);
            rightDriveSolenoid.set(Constants.DRIVE_LOW_GEAR);
        }
    }

    public String getDriveInputType() {
        switch (driveType) {
            default:
            case TANK:
                return "TANK";
            case GTA:
                return "GTA";
            case ARCADE:
                return "ARCADE";
        }
    }

    private enum gear {
        HIGH, LOW
    }
}
