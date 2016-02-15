package org.usfirst.frc.team2791.subsystems;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.configuration.Constants;
import org.usfirst.frc.team2791.configuration.PID;
import org.usfirst.frc.team2791.configuration.Ports;
import org.usfirst.frc.team2791.robot.Robot;
import org.usfirst.frc.team2791.util.BasicPID;
import org.usfirst.frc.team2791.util.ShakerGyro;
import org.usfirst.frc.team2791.util.Util;

import static org.usfirst.frc.team2791.robot.Robot.autonTimer;

public class ShakerDriveTrain extends ShakerSubsystem {
    private static BasicPID anglePID;
    private static BasicPID distancePID;
    private Talon leftTalonA;
    private Talon leftTalonB;
    private Talon rightTalonA;
    private Talon rightTalonB;
    private ShakerGyro gyro;
    private Encoder leftDriveEncoder;
    private Encoder rightDriveEncoder;
    private RobotDrive roboDrive;
    private DoubleSolenoid driveSolenoid;
    private double drivePIDOutput;
    private double anglePIDOutput;
    private double driveEncoderTicks = 128;

    public ShakerDriveTrain() {
        this.leftTalonA = new Talon(Ports.DRIVE_TALON_LEFT_PORT_FRONT);
        this.leftTalonB = new Talon(Ports.DRIVE_TALON_LEFT_PORT_BACK);
        this.rightTalonA = new Talon(Ports.DRIVE_TALON_RIGHT_PORT_FRONT);
        this.rightTalonB = new Talon(Ports.DRIVE_TALON_RIGHT_PORT_BACK);
        this.roboDrive = new RobotDrive(leftTalonA, leftTalonB, rightTalonA, rightTalonB);
        roboDrive.stopMotor();
        this.driveSolenoid = new DoubleSolenoid(Ports.PCM_MODULE, Ports.DRIVE_PISTON_FORWARD,
                Ports.DRIVE_PISTON_REVERSE);
        this.leftDriveEncoder = new Encoder(Ports.LEFT_DRIVE_ENCODER_PORT_A, Ports.LEFT_DRIVE_ENCODER_PORT_B);
        this.rightDriveEncoder = new Encoder(Ports.RIGHT_DRIVE_ENCOODER_PORT_A, Ports.RIGHT_DRIVE_ENCODER_PORT_B);
        leftDriveEncoder.reset();
        rightDriveEncoder.reset();
        leftDriveEncoder.setDistancePerPulse(Util.tickToFeet(driveEncoderTicks, 8));
        rightDriveEncoder.setDistancePerPulse(Util.tickToFeet(driveEncoderTicks, 8));
        this.gyro = new ShakerGyro(SPI.Port.kOnboardCS1);
        anglePID = new BasicPID(PID.DRIVE_ANGLE_P, PID.DRIVE_ANGLE_I, PID.DRIVE_ANGLE_D);
        distancePID = new BasicPID(PID.DRIVE_DISTANCE_P, PID.DRIVE_DISTANCE_I, PID.DRIVE_DISTANCE_D);
    }

    public boolean driveInFeet(double distance) {
        setLowGear();
        distancePID.setSetPoint(distance);
        distancePID.setMaxOutput(0.5);
        drivePIDOutput = -distancePID.updateAndGetOutput(getLeftDistance());
        anglePIDOutput = anglePID.updateAndGetOutput(getAngle());
        setLeftRight(drivePIDOutput - anglePIDOutput, drivePIDOutput + anglePIDOutput);
        return (Math.abs(distancePID.getError()) < 0.5) && (Math.abs(anglePID.getError()) < 2.5) && (Math.abs(getAvgSpeed()) < 0.1);
    }

    @Override
    public void run() {
        // nothing here?
    }

    @Override
    public void reset() {
        this.disable();
        this.setLowGear();
        this.rightDriveEncoder.reset();
        this.leftDriveEncoder.reset();

        // this.setDriveType((String) driveTypeChooser.getSelected());
    }

    @Override
    public void updateSmartDash() {
        SmartDashboard.putNumber("Gear : ", isHighGear() ? 2 : 1);
        SmartDashboard.putNumber("Left Drive Encoders Rate", leftDriveEncoder.getRate());
        SmartDashboard.putNumber("Right Drive Encoders Rate", rightDriveEncoder.getRate());
        SmartDashboard.putNumber("Auton drive PID error", distancePID.getError());
        SmartDashboard.putNumber("Auton drive angle PID error", anglePID.getError());
        SmartDashboard.putNumber("Auton timer", autonTimer.getTotalTime());
        SmartDashboard.putNumber("Auton drive PID output", drivePIDOutput);
        SmartDashboard.putNumber("Auton drive angle PID output", anglePIDOutput);
    }

    @Override
    public void disable() {
        roboDrive.stopMotor();
    }

    public void setLeftRight(double left, double right) {
        roboDrive.setLeftRightMotorOutputs(sanitizeValue(left), sanitizeValue(right));
        if (Robot.safetyMode.equals(Robot.SafetyMode.SAFETY))
            this.setLowGear();
    }

    public void setArcadeDrive(double left, double right) {
        roboDrive.arcadeDrive(left, right);
    }

    private double sanitizeValue(double value) {
        // This is just used as a threshold for the values
        // if connected to ds station or connected overriden via smartdash will
        // return value that is inputed
        switch (Robot.safetyMode) {
            case SAFETY:
            case TEST:
                return value * Constants.FULL_SPEED_SAFETY_MODE;

            case FULL_CONTROL:
                return value;
        }
        return value;
    }

    public boolean isHighGear() {

        switch (getCurrentGear()) {
            case HIGH:
                return true;
            case LOW:
                return false;
        }
        return false;
    }

    private GearState getCurrentGear() {
        if (driveSolenoid.get().equals(Constants.DRIVE_HIGH_GEAR))
            return GearState.HIGH;
        else if (driveSolenoid.get().equals(Constants.DRIVE_LOW_GEAR))
            return GearState.LOW;
        else
            return GearState.LOW;
    }

    public void setHighGear() {
        driveSolenoid.set(Constants.DRIVE_HIGH_GEAR);

    }

    public void setLowGear() {
        driveSolenoid.set(Constants.DRIVE_LOW_GEAR);

    }

    public double getLeftDistance() {
        return leftDriveEncoder.getDistance();
    }

    public double getRightDistance() {
        return rightDriveEncoder.getDistance();
    }

    public double getAngle() {
        return gyro.getAngle();
    }

    public double getAvgSpeed() {
        return (leftDriveEncoder.getRate() + rightDriveEncoder.getRate()) / 2;
    }

    public void calibrateGyro() {
        gyro.recalibrate();
    }

    public boolean isGyroCalibrating() {
        return gyro.currentlyCalibrating();
    }

    private enum GearState {
        HIGH, LOW
    }

}
