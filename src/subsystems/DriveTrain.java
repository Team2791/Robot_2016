//package subsystems;
//
//import configuration.Camera;
//import configuration.Constants;
//import configuration.Ports;
//import edu.wpi.first.wpilibj.RobotDrive;
//import edu.wpi.first.wpilibj.Solenoid;
//import edu.wpi.first.wpilibj.Talon;
//import edu.wpi.first.wpilibj.vision.AxisCamera;
//import org.usfirst.frc.team2791.robot.Robot;
//import shakerJoystick.Driver;
//import shakerJoystick.Operator;
//
///**
// *
// */
//public class DriveTrain implements Subsystems {
//    private Talon leftTalonA;
//    private Talon leftTalonB;
//    private Talon rightTalonA;
//    private Talon rightTalonB;
//    private Driver driverJoystick;
//    private Operator operatorJoystick;
//    private AxisCamera cam;
//    private RobotDrive roboDrive;
//    private gear gearState;
//    private Solenoid leftDriveSolenoid;
//    private Solenoid rightDriveSolenoid;
//
//    public void init() {
//    }
//
//    public void init() {
//
//        // instantiated speed controller here
//        this.leftTalonA = new Talon(Ports.LEFT_TALON_PORT_A);
//        this.leftTalonB = new Talon(Ports.LEFT_TALON_PORT_B);
//        this.rightTalonA = new Talon(Ports.RIGHT_TALON_PORT_A);
//        this.rightTalonB = new Talon(Ports.RIGHT_TALON_PORT_B);
//        this.roboDrive = new RobotDrive(leftTalonA, leftTalonB, rightTalonA, rightTalonB);
//        roboDrive.stopMotor();
//        this.leftDriveSolenoid = new Solenoid(Ports.LEFT_DRIVE_PISTON_CHANNEL, Ports.PCM_MODULE);
//        this.rightDriveSolenoid = new Solenoid(Ports.RIGHT_DRIVE_PISTON_CHANNEL, Ports.PCM_MODULE);
//
//    }
//
//    public void initTeleop() {
//        roboDrive.stopMotor();
//    }
//
//    public void initAutonomous() {
//        roboDrive.stopMotor();
//        cam = new AxisCamera(Camera.cameraPort);
//    }
//
//    public void initDisabled() {
//        roboDrive.stopMotor();
//    }
//
//    public void run(Robot.SafetyMode safeMode) {
//        switch (safeMode) {
//            case FULL_CONTROL:
//                runFullDrive();
//            case SAFETY:
//                runSafeDrive();
//        }
//    }
//
//
//    public void runDisabled() {
//        roboDrive.stopMotor();
//    }
//
//    public void runAutonomous() {
//
//    }
//
//    public void reset() {
//        roboDrive.stopMotor();
//    }
//
//
//
//
//
//
//    private void runFullDrive() {
//        switch (driveMode) {
//            case ARCADE:
//                roboDrive.arcadeDrive(driverJoystick.getAxisLeftY(), driverJoystick.getAxisRightX());
//            case GTA:
//                roboDrive.setLeftRightMotorOutputs(driverJoystick.getGtaDriveLeft(), driverJoystick.getGtaDriveRight());
//            default:
//            case TANK:
//                roboDrive.tankDrive(driverJoystick.getAxisLeftY(), driverJoystick.getAxisRightY());
//        }
//
//        if (driverJoystick.getButtonA()) {
//            setHighGear();
//        }
//        if (driverJoystick.getButtonB()) {
//            setLowGear();
//        }
//    }
//
//    private void runSafeDrive() {
//        final double SPEED_PERCENT = Constants.FULL_SPEED_SAFETY_MODE;
//        if (isHighGear())
//            setLowGear();
//        switch (driveMode) {
//            case ARCADE:
//                roboDrive.arcadeDrive(driverJoystick.getAxisLeftY() * SPEED_PERCENT, driverJoystick.getAxisRightX() * SPEED_PERCENT);
//            case GTA:
//                roboDrive.setLeftRightMotorOutputs(driverJoystick.getGtaDriveLeft() * SPEED_PERCENT, driverJoystick.getGtaDriveRight() * SPEED_PERCENT);
//            default:
//            case TANK:
//                roboDrive.tankDrive(driverJoystick.getAxisLeftY() * SPEED_PERCENT, driverJoystick.getAxisRightY() * SPEED_PERCENT);
//        }
//
//    }
//
//
//
//}
