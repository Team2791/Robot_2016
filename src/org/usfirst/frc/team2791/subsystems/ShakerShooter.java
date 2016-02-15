package org.usfirst.frc.team2791.subsystems;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.configuration.Constants;
import org.usfirst.frc.team2791.configuration.PID;
import org.usfirst.frc.team2791.configuration.Ports;

public class ShakerShooter extends ShakerSubsystem implements Runnable {
    private static final int updateDelayMs = 1000 / 100; // run at 100 Hz
    // private final double[] speed = {0.25, 0.5, 0.75, 1.0};
    private final double delayTimeBeforeShooting = 0.5;// time for wheels to
    // get
    // to speed
    private final double delayTimeForServo = 0.5;// time for servo to push
    private final int encoderTicks = 128 * 4;
    private double fireSpeed;
    private boolean autoFire;
    private CANTalon leftShooterTalon;
    private CANTalon rightShooterTalon;
    private DoubleSolenoid shortPiston;
    private DoubleSolenoid longPiston;
    private Servo ballAidServo;
    private AnalogInput ballDistanceSensor;
    private double feedForward = 0.2273;
    private double setPoint = 1200;

    public ShakerShooter() {
        leftShooterTalon = new CANTalon(Ports.SHOOTER_TALON_LEFT_PORT);
        rightShooterTalon = new CANTalon(Ports.SHOOTER_TALON_RIGHT_PORT);
        ballAidServo = new Servo(Ports.BALL_AID_SERVO_PORT);
        longPiston = new DoubleSolenoid(Ports.PCM_MODULE, Ports.LONG_PISTON_FORWARD, Ports.LONG_PISTON_REVERSE);
        shortPiston = new DoubleSolenoid(Ports.SECOND_PCM_MODULE, Ports.SHORT_PISTON_FORWARD,
                Ports.SHORT_PISTON_REVERSE);
        ballDistanceSensor = new AnalogInput(Ports.BALL_DISTANCE_SENSOR_PORT);
        rightShooterTalon.setInverted(true);
        // true, false, true, false // right sensor correct, direction wrong,
        // left vis versa
        // false false true true // was working correctly maybe
        rightShooterTalon.reverseOutput(false);
        leftShooterTalon.reverseOutput(false);
        leftShooterTalon.reverseSensor(true);
        rightShooterTalon.reverseSensor(true);
        leftShooterTalon.configPeakOutputVoltage(+12.0f, 0);
        rightShooterTalon.configPeakOutputVoltage(0, -12.0f);
        //
        SmartDashboard.putNumber("Shooter p", PID.SHOOTER_P);
        SmartDashboard.putNumber("Shooter i", PID.SHOOTER_I);
        SmartDashboard.putNumber("Shooter d", PID.SHOOTER_D);
        PID.SHOOTER_P = SmartDashboard.getNumber("Shooter p");
        PID.SHOOTER_I = SmartDashboard.getNumber("Shooter i");
        PID.SHOOTER_D = SmartDashboard.getNumber("Shooter d");
        SmartDashboard.putNumber("FeedForward", feedForward);
        SmartDashboard.putNumber("SetPoint", setPoint);
        leftShooterTalon.setIZone(100);
        rightShooterTalon.setIZone(100);
        leftShooterTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        rightShooterTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        leftShooterTalon.configEncoderCodesPerRev(encoderTicks);
        rightShooterTalon.configEncoderCodesPerRev(encoderTicks);

        leftShooterTalon.changeControlMode(TalonControlMode.Speed);
        rightShooterTalon.changeControlMode(TalonControlMode.Speed);
        leftShooterTalon.enableControl();
        rightShooterTalon.enableControl();
        leftShooterTalon.enable();
        rightShooterTalon.enable();
        leftShooterTalon.configNominalOutputVoltage(0, 0);
        rightShooterTalon.configNominalOutputVoltage(0, 0);

    }

    @Override
    public void run() {
        while (true) {
            if (autoFire) {

                while (!(Math.abs(leftShooterTalon.getError()) < 20 && Math.abs(rightShooterTalon.getError()) < 20)) {
                    setShooterSpeeds(SmartDashboard.getNumber("SetPoint"), true);
                }
                double time = Timer.getFPGATimestamp();
                // give it a few seconds to hold speed
                while (Timer.getFPGATimestamp() - time < delayTimeBeforeShooting) {
                    setShooterSpeeds(SmartDashboard.getNumber("SetPoint"), false);
                }
                time = Timer.getFPGATimestamp();

                // push ball
                while (Timer.getFPGATimestamp() - time < delayTimeForServo) {
                    setShooterSpeeds(SmartDashboard.getNumber("SetPoint"), false);
                    pushBall();
                    System.out.println("The ball is being pushed out");
                }
                // reset everything
                resetServoAngle();
                stopMotors();
            }
            autoFire = false;
            try {
                // slows down the rate at which this method is called(so it
                // doesn't run too fast)
                Thread.sleep(updateDelayMs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setShooterSpeeds(double targetSpeed, boolean withPID) {
        if (withPID) {
            leftShooterTalon.setP(SmartDashboard.getNumber("Shooter p"));
            leftShooterTalon.setI(SmartDashboard.getNumber("Shooter i"));
            leftShooterTalon.setD(SmartDashboard.getNumber("Shooter d"));
            rightShooterTalon.setP(SmartDashboard.getNumber("Shooter p"));
            rightShooterTalon.setI(SmartDashboard.getNumber("Shooter i"));
            rightShooterTalon.setD(SmartDashboard.getNumber("Shooter d"));
            leftShooterTalon.setF(SmartDashboard.getNumber("FeedForward"));
            rightShooterTalon.setF(SmartDashboard.getNumber("FeedForward"));
            leftShooterTalon.set(targetSpeed);
            rightShooterTalon.set(targetSpeed);// these values are in rpms

        } else if (!autoFire) {
            System.out.println("i am running without pid");
            leftShooterTalon.changeControlMode(TalonControlMode.PercentVbus);
            rightShooterTalon.changeControlMode(TalonControlMode.PercentVbus);
            leftShooterTalon.set(targetSpeed);
            rightShooterTalon.set(targetSpeed);
        } else {
            stopMotors();
        }
        System.out.println("is fire state auto " + autoFire);
    }

    @Override
    public void updateSmartDash() {
        // TODO Auto-generated method stub
        SmartDashboard.putNumber("LeftShooterSpeed", leftShooterTalon.getEncVelocity());
        SmartDashboard.putNumber("RightShooterSpeed", rightShooterTalon.getEncVelocity());
        SmartDashboard.putNumber("Left Shooter Error", leftShooterTalon.getClosedLoopError());
        SmartDashboard.putNumber("Right Shooter Error", -rightShooterTalon.getClosedLoopError());
        SmartDashboard.putString("Current shooter setpoint", getShooterHeight().toString());
        SmartDashboard.putNumber("left output voltage", leftShooterTalon.getOutputVoltage());
        SmartDashboard.putNumber("left speed", -leftShooterTalon.getEncVelocity());
        SmartDashboard.putNumber("right output voltage", rightShooterTalon.getOutputVoltage());
        SmartDashboard.putNumber("right speed", rightShooterTalon.getEncVelocity());
        SmartDashboard.putNumber("Right error", rightShooterTalon.getError());
        SmartDashboard.putNumber("Left error", leftShooterTalon.getError());
        SmartDashboard.putBoolean("Shooter at speed", leftShooterTalon.getSpeed() == SmartDashboard.getNumber("SetPoint"));

    }

    @Override
    public void reset() {
        stopMotors();
        // reset the PID on the Talons
        leftShooterTalon.reset();
        rightShooterTalon.reset();
        // TODO Auto-generated method stub

    }

    public void stopMotors() {
        leftShooterTalon.set(0);
        rightShooterTalon.set(0);
    }

    @Override
    public void disable() {
        // TODO Auto-generated method stub
        stopMotors();
        SmartDashboard.putNumber("right speed", rightShooterTalon.getSpeed());
        SmartDashboard.putNumber("left speed", leftShooterTalon.getSpeed());
    }

    public boolean hasBall() {
        return ballDistanceSensor.getVoltage() > 0.25 && ballDistanceSensor.getVoltage() < 0.30;
    }

    public void pushBall() {
        // will be used to push ball toward the shooter
        ballAidServo.set(1);

    }

    public void resetServoAngle() {
        // bring servo back to original position
        ballAidServo.set(0);
    }

    public void autoFire(double speed) {
        autoFire = true;
        fireSpeed = speed;
    }

    public ShooterHeight getShooterHeight() {
        // get current shooter height by determining which solenoid are true
        if (shortPiston.get().equals(Constants.SMALL_PISTON_HIGH_STATE)
                && longPiston.get().equals(Constants.LARGE_PISTON_HIGH_STATE))
            return ShooterHeight.HIGH;
        else if (longPiston.get().equals(Constants.LARGE_PISTON_HIGH_STATE))
            return ShooterHeight.MID;
        else
            return ShooterHeight.LOW;

    }

    public void setShooterLow() {
        // set shooter height to low , set both pistons to false
        shortPiston.set(Constants.SMALL_PISTON_LOW_STATE);
        longPiston.set(Constants.LARGE_PISTON_LOW_STATE);
        // short needs to switch
    }

    public void setShooterMiddle() {
        // set shooter height to middle meaning only one piston will be true
        shortPiston.set(Constants.SMALL_PISTON_LOW_STATE);
        longPiston.set(Constants.LARGE_PISTON_HIGH_STATE);
    }

    public void setShooterHigh() {
        // both pistons will be set to true to get max height
        shortPiston.set(Constants.SMALL_PISTON_HIGH_STATE); // was reverse
        // //this is short
        // one
        longPiston.set(Constants.LARGE_PISTON_HIGH_STATE);
    }

    public enum ShooterHeight {
        LOW, MID, HIGH
    }

}