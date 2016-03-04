package org.usfirst.frc.team2791.subsystems;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.util.BasicPID;
import org.usfirst.frc.team2791.util.Constants;
import org.usfirst.frc.team2791.util.ShakerGyro;
import org.usfirst.frc.team2791.util.Util;

public class ShakerDriveTrain extends ShakerSubsystem {
    private static ShakerDriveTrain driveTrainInstance;
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
    private Solenoid driveSolenoid;
    private double drivePIDOutput;
    private double anglePIDOutput;
    private double driveEncoderTicks = 128;
    private double driveTimePIDGoodTime = 0;
    private double angleTimePIDGoodTime = 0;

    private ShakerDriveTrain() {
        // instanciate the four talons for the drive train
        this.leftTalonA = new Talon(Constants.DRIVE_TALON_LEFT_PORT_FRONT);
        this.leftTalonB = new Talon(Constants.DRIVE_TALON_LEFT_PORT_BACK);
        this.rightTalonA = new Talon(Constants.DRIVE_TALON_RIGHT_PORT_FRONT);
        this.rightTalonB = new Talon(Constants.DRIVE_TALON_RIGHT_PORT_BACK);
        // use the talons to create a roboDrive (it has methods that allow for
        // easier control)
        this.roboDrive = new RobotDrive(leftTalonA, leftTalonB, rightTalonA, rightTalonB);
        // stop all motors right away just in case
        roboDrive.stopMotor();
        // shifting solenoid
        this.driveSolenoid = new Solenoid(Constants.PCM_MODULE, Constants.DRIVE_SHIFTING_PISTON);
        // drive encoders
        this.leftDriveEncoder = new Encoder(Constants.LEFT_DRIVE_ENCODER_PORT_A, Constants.LEFT_DRIVE_ENCODER_PORT_B);
        this.rightDriveEncoder = new Encoder(Constants.RIGHT_DRIVE_ENCOODER_PORT_A,
                Constants.RIGHT_DRIVE_ENCODER_PORT_B);
        // clear the drive encoders
        leftDriveEncoder.reset();
        rightDriveEncoder.reset();
        leftDriveEncoder.setDistancePerPulse(Util.tickToFeet(driveEncoderTicks, 8));
        rightDriveEncoder.setDistancePerPulse(-Util.tickToFeet(driveEncoderTicks, 8));
        gyro = new ShakerGyro(SPI.Port.kOnboardCS1);
        (new Thread(gyro)).start();
        anglePID = new BasicPID(Constants.DRIVE_ANGLE_P, Constants.DRIVE_ANGLE_I, Constants.DRIVE_ANGLE_D);
        distancePID = new BasicPID(Constants.DRIVE_DISTANCE_P, Constants.DRIVE_DISTANCE_I, Constants.DRIVE_DISTANCE_D);

        anglePID.setMaxOutput(0.5);
        anglePID.setMinOutput(-0.5);
        distancePID.setInvertOutput(true);
        anglePID.setInvertOutput(true);
        anglePID.setIZone(15);
    }

    public static ShakerDriveTrain getInstance() {
        if (driveTrainInstance == null)
            driveTrainInstance = new ShakerDriveTrain();
        return driveTrainInstance;
    }

    public boolean driveInFeet(double distance, double angle, double maxOutput) {
        distance *= 12;// convert distance from feet to inches
        setLowGear();
        distancePID.setSetPoint(distance);
        anglePID.setSetPoint(angle);
        distancePID.setMaxOutput(maxOutput);
        distancePID.setMinOutput(-maxOutput);
        anglePID.setMaxOutput(maxOutput / 2);
        anglePID.setMinOutput(-maxOutput / 2);
        anglePID.changeGains(Constants.DRIVE_ANGLE_P, Constants.DRIVE_ANGLE_I, Constants.DRIVE_ANGLE_D);
        distancePID.changeGains(Constants.DRIVE_DISTANCE_P, Constants.DRIVE_DISTANCE_I, Constants.DRIVE_DISTANCE_D);
        drivePIDOutput = distancePID.updateAndGetOutput(this.getRightDistance());
        anglePIDOutput = anglePID.updateAndGetOutput(getAngle());
        setLeftRightVoltage(drivePIDOutput + anglePIDOutput, drivePIDOutput - anglePIDOutput);
        if (!(Math.abs(distancePID.getError()) < 1) && (Math.abs(anglePID.getError()) < 2.5))
            // Makes sure pid is good error is minimal
            driveTimePIDGoodTime = Timer.getFPGATimestamp();
        else if (Timer.getFPGATimestamp() - driveTimePIDGoodTime > 0.5)
            // then makes sure that certain time has passed to be absolutely
            // positive
            return true;
        return false;

    }

    public boolean setAngle(double angle, double maxOutput) {
        setLowGear();
        anglePID.setInvertOutput(true);
        anglePID.setSetPoint(angle);
        anglePID.setMaxOutput(maxOutput);
        anglePID.setMinOutput(-maxOutput);
        anglePID.setIZone(4);
        anglePID.changeGains(Constants.STATIONARY_ANGLE_P, Constants.STATIONARY_ANGLE_I, Constants.STATIONARY_ANGLE_D);
        anglePIDOutput = anglePID.updateAndGetOutput(getAngle());
        setLeftRightVoltage(anglePIDOutput, -anglePIDOutput);
        if (!(Math.abs(anglePID.getError()) < 1))
            // Makes sure pid is good error is minimal
            angleTimePIDGoodTime = Timer.getFPGATimestamp();
        else if (Timer.getFPGATimestamp() - angleTimePIDGoodTime > 0.5)
            // then makes sure that certain time has passed to be absolutely
            // positive
            return true;
        return false;

    }

    public void run() {
        // nothing here?
    }

    public void reset() {
        this.disable();
        this.setLowGear();
        this.rightDriveEncoder.reset();
        this.leftDriveEncoder.reset();
    }

    public void updateSmartDash() {
        // put values on the smart dashboard
        SmartDashboard.putNumber("Gear : ", isHighGear() ? 2 : 1);
    }

    public void debug() {
        SmartDashboard.putNumber("Left Drive Encoders Rate", leftDriveEncoder.getRate());
        SmartDashboard.putNumber("Right Drive Encoders Rate", rightDriveEncoder.getRate());
        SmartDashboard.putNumber("Auton drive PID error", distancePID.getError());
        SmartDashboard.putNumber("Auton drive angle PID error", anglePID.getError());
        SmartDashboard.putNumber("Current gyro angle", getAngle());
        SmartDashboard.putNumber("Current angle pid error", anglePID.getError());
        SmartDashboard.putNumber("PID OUTPUT: ", anglePIDOutput);
        SmartDashboard.putNumber("Average dist", getAvgDist());
        SmartDashboard.putNumber("drivePID output", drivePIDOutput);
        SmartDashboard.putNumber("encoder left", getLeftDistance());
        SmartDashboard.putNumber("encoder right", getRightDistance());

    }

    public void disable() {
        // Stops all the motors
        roboDrive.stopMotor();
    }

    public void setLeftRight(double left, double right) {
        // sets the left and right motors
        roboDrive.setLeftRightMotorOutputs(left, right);
    }

    public void setLeftRightVoltage(double leftVoltage, double rightVoltage) {
        leftVoltage *= 12;
        rightVoltage *= 12;
        leftVoltage /= ControllerPower.getInputVoltage();
        rightVoltage /= ControllerPower.getInputVoltage();
        setLeftRight(leftVoltage, rightVoltage);
    }

    public void setArcadeDrive(double left, double right) {
        // Set values for the Arcade drive
        roboDrive.arcadeDrive(left, right);
    }

    public boolean isHighGear() {
        // check the gear state, whether it is high or low
        switch (getCurrentGear()) {
            case HIGH:
                return true;
            case LOW:
                return false;
        }
        return false;
    }

    private GearState getCurrentGear() {
        if (driveSolenoid.get())
            return GearState.HIGH;
        else if (!driveSolenoid.get())
            return GearState.LOW;
        else
            return GearState.LOW;
    }

    public void setHighGear() {
        // put the gear into the high state
        driveSolenoid.set(true);
    }

    public void setLowGear() {
        // put gear into the low state
        driveSolenoid.set(false);
    }

    public void resetEncoders() {
        // zero the encoders
        leftDriveEncoder.reset();
        rightDriveEncoder.reset();
    }

    public double getLeftDistance() {
        // distance of left encoder
        return leftDriveEncoder.getDistance();
    }

    public double getRightDistance() {
        // distance of right encoder
        return rightDriveEncoder.getDistance();
    }

    public void resetGyro() {
        // zero the gyro
        gyro.reset();
    }

    public double getGyroRate() {
        // recalibrate the gyro for
        return gyro.getRate();
    }

    public double getAngle() {
        // Get the current gyro angle
        return gyro.getAngle();
    }

    public double getAvgSpeed() {
        // average of both encoder velocities
        return (leftDriveEncoder.getRate() + rightDriveEncoder.getRate()) / 2;
    }

    public double getAvgDist() {
        // average distance of both encoders
        return (leftDriveEncoder.getDistance() + rightDriveEncoder.getDistance()) / 2;
    }

    public void calibrateGyro() {
        // recalibrate the gyro
        gyro.recalibrate();
    }

    public boolean isGyroCalibrating() {
        // Check if the gyro is currently calibrating
        return gyro.currentlyCalibrating();
    }

    private enum GearState {
        HIGH, LOW
    }
}