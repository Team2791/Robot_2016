package subsystems;

import configuration.Camera;
import configuration.Ports;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.vision.AxisCamera;
import org.usfirst.frc.team2791.robot.Robot;
import shakerJoystick.Driver;
import shakerJoystick.Operator;

/**
 *
 */
public class DriveTrain implements Subsystems {
    private Talon leftTalonA;
    private Talon leftTalonB;
    private Talon rightTalonA;
    private Talon rightTalonB;
    private Driver driverJoystick;
    private Operator operatorJoystick;
    private AxisCamera cam;
    private RobotDrive roboDrive;
    private Robot.SafetyMode safeMode;
    private driveType driveMode;

    public void init() {
    }

    public void init(shakerJoystick.Driver driveJoy, shakerJoystick.Operator opJoy, driveType drt) {
        // TODO Auto-generated method stub
        // instantiated speed controller here
        this.driverJoystick = driveJoy;
        this.operatorJoystick = opJoy;
        this.leftTalonA = new Talon(Ports.leftTalonPortA);
        this.leftTalonB = new Talon(Ports.leftTalonPortB);
        this.rightTalonA = new Talon(Ports.rightTalonPortA);
        this.rightTalonB = new Talon(Ports.rightTalonPortB);
        this.roboDrive = new RobotDrive(leftTalonA, leftTalonB, rightTalonA, rightTalonB);
        this.driveMode = drt;
    }

    public void initTeleop() {
        // TODO Auto-generated method stub

    }

    public void initDisabled() {
        // TODO Auto-generated method stub

    }

    public void runTeleop() {

        roboDrive.arcadeDrive(driverJoystick.getAxisLeftY(), driverJoystick.getAxisRightX());
    }

    public void initAutonomous() {
        cam = new AxisCamera(Camera.cameraPort);
    }

    public void runDisabled() {

    }

    public void runAutonomous() {

    }

    public void reset() {

    }

    private void shift_up() {

    }

    private void shift_down() {

    }

    public void setSafetyMode(Robot.SafetyMode safetyMode) {
        this.safeMode = safetyMode;
    }

    public void setDriveMode(driveType drt) {
        this.driveMode = drt;
    }

    public enum driveType {
        TANK, ARCADE
    }

    private enum gear {
        HIGH, LOW
    }
}
