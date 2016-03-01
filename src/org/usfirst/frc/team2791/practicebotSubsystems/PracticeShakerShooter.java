package org.usfirst.frc.team2791.practicebotSubsystems;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.subsystems.ShakerSubsystem;
import org.usfirst.frc.team2791.util.Constants;


public class PracticeShakerShooter extends ShakerSubsystem implements Runnable {
    private static final int updateDelayMs = 1000 / 100; // run at 100 Hz
    // private final double[] speed = {0.25, 0.5, 0.75, 1.0};
    private final double delayTimeBeforeShooting = 0.5;// time for wheels to
    // get
    // to speed
    private final double delayTimeForServo = 0.8;// time for servo to push
    private final int encoderTicks = 128 * 4;
    private boolean autoFire;
    private CANTalon leftShooterTalon;
    private CANTalon rightShooterTalon;
    private DoubleSolenoid shortPiston;
    private DoubleSolenoid longPiston;
    private Servo ballAidServo;
    private AnalogInput ballDistanceSensor;
    private double feedForward = 0.4;
    private double closeShotSetPoint = 590;
    private double farShotSetpoint = 850;
    private boolean overrideShot = false;
    private boolean prepShot = false;
    private boolean shooterArmMoving = false;
    private double timeWhenShooterArmMoved;
    private boolean delayedArmMove = false;
    private ShooterHeight delayedShooterPos;

    public PracticeShakerShooter() {
        leftShooterTalon = new CANTalon(Constants.SHOOTER_TALON_LEFT_PORT);
        rightShooterTalon = new CANTalon(Constants.SHOOTER_TALON_RIGHT_PORT);
        ballAidServo = new Servo(Constants.BALL_AID_SERVO_PORT);
        longPiston = new DoubleSolenoid(Constants.PCM_MODULE, Constants.LONG_PISTON_FORWARD,
                Constants.LONG_PISTON_REVERSE);
        shortPiston = new DoubleSolenoid(Constants.PCM_MODULE, Constants.SHORT_PISTON_FORWARD,
                Constants.SHORT_PISTON_REVERSE);
        ballDistanceSensor = new AnalogInput(Constants.BALL_DISTANCE_SENSOR_PORT);
        rightShooterTalon.setInverted(false);
        rightShooterTalon.reverseOutput(false);
        leftShooterTalon.reverseOutput(false);
        leftShooterTalon.reverseSensor(true);
        rightShooterTalon.reverseSensor(false);
        leftShooterTalon.configPeakOutputVoltage(+12.0f, 0);
        rightShooterTalon.configPeakOutputVoltage(+12.0f, 0);
        SmartDashboard.putNumber("Shooter p", Constants.SHOOTER_P);
        SmartDashboard.putNumber("Shooter i", Constants.SHOOTER_I);
        SmartDashboard.putNumber("Shooter d", Constants.SHOOTER_D);
        Constants.SHOOTER_P = SmartDashboard.getNumber("Shooter p");
        Constants.SHOOTER_I = SmartDashboard.getNumber("Shooter i");
        Constants.SHOOTER_D = SmartDashboard.getNumber("Shooter d");
        SmartDashboard.putNumber("FeedForward", feedForward);
        SmartDashboard.putNumber("closeShotSetpoint", closeShotSetPoint);
        SmartDashboard.putNumber("farShotSetpoint", farShotSetpoint);
        leftShooterTalon.setIZone(500);
        rightShooterTalon.setIZone(500);
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
        try {
            while (true) {
                // update the setPoints from the dashboard
                closeShotSetPoint = SmartDashboard.getNumber("closeShotSetpoint");
                farShotSetpoint = SmartDashboard.getNumber("farShotSetpoint");
                // choose the setpoint by getting arm pos
                double setPoint = getShooterHeight().equals(ShooterHeight.MID) ? farShotSetpoint : closeShotSetPoint;
                // this to allow the shooters to give sometime to speed up
                if (prepShot) {
                    setShooterSpeeds(setPoint, true);
                    if (overrideShot || autoFire)
                        prepShot = false;
                }
                // if the shooter arm is moving just run the intake slightly to
                // pull the ball in
                if (shooterArmMoving) {
                    while (Timer.getFPGATimestamp() - timeWhenShooterArmMoved < 0.6) {
                        setShooterSpeeds(-0.7, false);
                    }
                    shooterArmMoving = false;
                }
                if (delayedArmMove) {
                    Thread.sleep(1000);
                    switch (delayedShooterPos) {
                        case LOW:
                            setShooterLow();
                            break;
                        case MID:
                            setShooterMiddle();
                            break;
                        case HIGH:
                            setShooterHigh();
                            break;
                    }
                    delayedArmMove = false;
                }
                // if run auto fire (run shooter wheels, and run servo)
                if (autoFire) {
                    // set the shooter speeds to the set point
                    setShooterSpeeds(setPoint, true);
                    // Just a variable to make sure that pid is good for a
                    // certain amount of time
                    double whenTheWheelsStartedBeingTheRightSpeed = Timer.getFPGATimestamp();
                    // basically just wait for the difference in time to be
                    // greater than the delay
                    // this allows the shooter to get to speed
                    while (Timer.getFPGATimestamp()
                            - whenTheWheelsStartedBeingTheRightSpeed < delayTimeBeforeShooting) {
                        // if manual override is activated then skip the delay
                        // and go straight to next step
                        if (overrideShot)
                            break;
                        // if there is sufficient error then stay in the while
                        // loop
                        if (!(Math.abs(leftShooterTalon.getError()) < 50
                                && Math.abs(rightShooterTalon.getError()) < 50)) {
                            // if the wheels aren't at speed reset the count
                            whenTheWheelsStartedBeingTheRightSpeed = Timer.getFPGATimestamp();
                        }
                        Thread.sleep(10);
                        setShooterSpeeds(setPoint, true);
                    }
                    // this is used for the servo
                    double time = Timer.getFPGATimestamp();
                    // push ball
                    // the servo is run for a bit forward
                    while (Timer.getFPGATimestamp() - time < delayTimeForServo) {
                        Thread.sleep(10);
                        setShooterSpeeds(setPoint, true);
                        pushBall();
                    }
                    // resets everything
                    resetServoAngle();
                    stopMotors();
                    overrideShot = false;
                }
                // auto fire is done if it reaches here
                autoFire = false;

                // slows down the rate at which this method is called(so it
                // doesn't run too fast)
                Thread.sleep(updateDelayMs);

            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setShooterSpeeds(double targetSpeed, boolean withPID) {
        if (withPID) {
            // if pid should be used then we have to switch the talons to
            // velocity mode
            leftShooterTalon.changeControlMode(TalonControlMode.Speed);
            rightShooterTalon.changeControlMode(TalonControlMode.Speed);
            // update the pid and feedforward values
            leftShooterTalon.setP(SmartDashboard.getNumber("Shooter p"));
            leftShooterTalon.setI(SmartDashboard.getNumber("Shooter i"));
            leftShooterTalon.setD(SmartDashboard.getNumber("Shooter d"));
            rightShooterTalon.setP(SmartDashboard.getNumber("Shooter p"));
            rightShooterTalon.setI(SmartDashboard.getNumber("Shooter i"));
            rightShooterTalon.setD(SmartDashboard.getNumber("Shooter d"));
            leftShooterTalon.setF(SmartDashboard.getNumber("FeedForward"));
            rightShooterTalon.setF(SmartDashboard.getNumber("FeedForward"));
            // set the speeds (THEY ARE IN RPMS)
            leftShooterTalon.set(targetSpeed);
            rightShooterTalon.set(targetSpeed);

        } else if (!autoFire || !prepShot) {
            // if shooters is not autofiring or prepping the shot then use
            // inputs given, including 0
            leftShooterTalon.changeControlMode(TalonControlMode.PercentVbus);
            rightShooterTalon.changeControlMode(TalonControlMode.PercentVbus);
            leftShooterTalon.set(targetSpeed);
            rightShooterTalon.set(targetSpeed);
        }
    }

    @Override
    public void updateSmartDash() {
        // update the smartdashbaord with values
        SmartDashboard.putBoolean("Does shooter have ball", hasBall());
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

    }

    public void delayedShooterPosition(ShooterHeight pos) {
        delayedArmMove = true;
    }

    @Override
    public void reset() {
        //stop the motors
        stopMotors();
        // reset the PID on the Talons
        leftShooterTalon.reset();
        rightShooterTalon.reset();
    }

    public void stopMotors() {
        //set the motors to 0 to stop
        leftShooterTalon.set(0);
        rightShooterTalon.set(0);
    }

    public void disable() {
        //disable code will stop motors
        stopMotors();
        SmartDashboard.putNumber("right speed", rightShooterTalon.getSpeed());
        SmartDashboard.putNumber("left speed", leftShooterTalon.getSpeed());
    }

    public boolean hasBall() {
        //returns the sensor value
        return ballDistanceSensor.getVoltage() > 0.263;
    }

    public void pushBall() {
        // will be used to push ball toward the shooter
        ballAidServo.set(0.5);
    }

    public void resetServoAngle() {
        // bring servo back to original position
        ballAidServo.set(1);
    }

    public void autoFire() {
        autoFire = true;
    }

    public ShooterHeight getShooterHeight() {
        // get current shooter height by determining which solenoid are true
        if (shortPiston.get().equals(PracticeConstants.SMALL_PISTON_HIGH_STATE)
                && longPiston.get().equals(PracticeConstants.LARGE_PISTON_HIGH_STATE)) {
            // System.out.println("I think that the shooter is in the high
            // pos");
            return ShooterHeight.HIGH;
        } else if (longPiston.get().equals(PracticeConstants.LARGE_PISTON_HIGH_STATE)) {
            // System.out.println("I think that the shooter is in the mid pos");
            return ShooterHeight.MID;
        } else {

            // System.out.println("I think that the shooter is in the low pos");
            return ShooterHeight.LOW;
        }
    }

    public void setShooterLow() {
        // both pistons will be set to true to get max height
        shortPiston.set(PracticeConstants.SMALL_PISTON_LOW_STATE);
        longPiston.set(PracticeConstants.LARGE_PISTON_LOW_STATE);

    }

    public void setShooterMiddle() {
        // set shooter height to middle meaning only one piston will be true
        shooterArmMoving = true;
        timeWhenShooterArmMoved = Timer.getFPGATimestamp();
        shortPiston.set(PracticeConstants.SMALL_PISTON_LOW_STATE);
        longPiston.set(PracticeConstants.LARGE_PISTON_HIGH_STATE);
    }

    public void setShooterHigh() {
        // set shooter height to low , set both pistons to false
        shooterArmMoving = true;
        timeWhenShooterArmMoved = Timer.getFPGATimestamp();
        shortPiston.set(PracticeConstants.SMALL_PISTON_HIGH_STATE);
        longPiston.set(PracticeConstants.LARGE_PISTON_HIGH_STATE);
        // short needs to switch
    }

    public void overrideAutoShot() {
        overrideShot = true;
    }

    public void prepShot() {
        prepShot = true;
    }

    public boolean getIfAutoFire() {
        return autoFire;

    }

    public enum ShooterHeight {
        LOW, MID, HIGH
    }

}