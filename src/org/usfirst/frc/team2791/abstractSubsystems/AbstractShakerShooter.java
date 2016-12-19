package org.usfirst.frc.team2791.abstractSubsystems;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.util.Constants;


public abstract class AbstractShakerShooter extends ShakerSubsystem implements Runnable {
    protected static final int updateDelayMs = 1000 / 100; // run at 100 Hz
    // time that the shooter wheels have to be at the proper speed
    protected static final double delayTimeBeforeShooting = 0.2;
    // boolean that decides whether the arm should move in a delay motion
    public static boolean delayedArmMove = false;
    protected final double delayTimeForServo = 0.8;// time for servo to push
    // how many ticks the encoder has
    protected final int encoderTicks = 128 * 4;
    // shooter can talons
    protected CANTalon leftShooterTalon;
    protected CANTalon rightShooterTalon;
    // shooter arm positiion pistons
//	private DoubleSolenoid shortPiston;
//	private DoubleSolenoid longPiston;

    // servo that pushes ball into the shooter
    protected Servo ballAidServo;
    protected boolean addExtraPower = false;
    // sensor that allows us to know that the ball is in the shooter
    protected AnalogInput ballDistanceSensor;
    // feed forward of the shooter wheel pid
    protected double feedForward = 0.31;
    protected boolean cancelShot = false;
    // setpoints to acheicve target depending on the pos of the shooter arm
    protected double manualSetPoint;
    protected boolean useManualSetPoint = false;
    protected double closeShotSetPoint = 600; // WAS 585 SWITCHED TO 600 FOR ROBOT RUMBLE
    //    protected double farShotSetpoint = 820;//original far distance
    protected double farShotSetpoint = 820; // was 795 
    // boolean that decides weahter autofiring should occur
    protected boolean autoFire = false;
    // manual override boolean for the autofire
    protected boolean overrideShot = false;
    protected double autonShootSpeed = 100;
    protected boolean autonShot = false;
    // prepshot decides whether to run the shooter wheels before hand to save
    // time
    protected boolean prepShot = false;
    // check is shooter arm is moving
    protected boolean shooterArmMoving = false;
    // time when the shooter arm last moved
    protected double timeWhenShooterArmMoved;
    // shooter height setpoint
    protected ShooterHeight delayedShooterPos;
    protected Servo clampServo;

    public AbstractShakerShooter() {
    }

    // THIS METHOD NEED STO BE CALLED BY THE SUB CLASS
    protected void init() {
        // shooter talon config
        rightShooterTalon.setInverted(false);
        rightShooterTalon.reverseOutput(false);
        leftShooterTalon.reverseOutput(false);
        leftShooterTalon.reverseSensor(true);
        rightShooterTalon.reverseSensor(false);
        // sets limits in each direction such that shooters don't run
        // reverse
        // with pid
        leftShooterTalon.configPeakOutputVoltage(+12.0f, 0);
        rightShooterTalon.configPeakOutputVoltage(+12.0f, 0);
        // put the shooter pid values on the dashboard
        SmartDashboard.putNumber("Shooter p", Constants.SHOOTER_P);
        SmartDashboard.putNumber("Shooter i", Constants.SHOOTER_I);
        SmartDashboard.putNumber("Shooter d", Constants.SHOOTER_D);
        SmartDashboard.putNumber("FeedForward", feedForward);
        // put setpoints on the dashboard
        SmartDashboard.putNumber("closeShotSetpoint", closeShotSetPoint);
        SmartDashboard.putNumber("farShotSetpoint", farShotSetpoint);
        // izone for the talons
        // izone is how close the shooter has to be to the setpoint before
        // using
        // i in pid
        leftShooterTalon.setIZone(500);
        rightShooterTalon.setIZone(500);
        // choose the type of sensor attached to the talon
        leftShooterTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        rightShooterTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        // how many ticks are in the feed back device
        leftShooterTalon.configEncoderCodesPerRev(encoderTicks);
        rightShooterTalon.configEncoderCodesPerRev(encoderTicks);
        // control mode - speed(rpms), voltage(how many volts to be sent to
        // the
        // talons)
        // percentage(voltage sent/ 12 v)
        leftShooterTalon.changeControlMode(TalonControlMode.Speed);
        rightShooterTalon.changeControlMode(TalonControlMode.Speed);
        // enable the talons
        leftShooterTalon.enableControl();
        rightShooterTalon.enableControl();
        leftShooterTalon.enable();
        rightShooterTalon.enable();

        leftShooterTalon.configNominalOutputVoltage(0, 0);
        rightShooterTalon.configNominalOutputVoltage(0, 0);
    }

    public void run() {
        try {
            while (true) {
                // if the shooter arm is moving just run the intake slightly to
                // pull the ball in
                if (shooterArmMoving) {
                    while (Timer.getFPGATimestamp() - timeWhenShooterArmMoved < 1) {
                        setShooterSpeeds(-0.7, false);
                    }
                    stopMotors();
                    shooterArmMoving = false;
                    clampBall();
//                    prepShot = true;
                }
                // wait a few seconds before moving the arm
                // this is used to allow the intake time before bringing the arm
                // down
                if (delayedArmMove) {
                    delayArmMove();
                }

                double setPoint = getSetPoint();
                if (prepShot) {
                    prepShot(setPoint);
                }
                if (autoFire) {
                    releaseBall();
                    internalAutoFire(setPoint);
                }

                resetAutoFireVariables();
                // slows down the rate at which this method is called(so it
                // doesn't run too fast)

                Thread.sleep(updateDelayMs);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void resetAutoFireVariables() {
        // auto fire is done if it reaches here
        overrideShot = false;
        autoFire = false;
        cancelShot = false;
        addExtraPower = false;
        manualSetPoint = closeShotSetPoint;
        useManualSetPoint = false;
    }

    private void internalAutoFire(double setPoint) {
        System.out.println("Auto Firing starting");
        // set the shooter speeds to the set point
        setShooterSpeeds(setPoint, true);
        // Just a variable to make sure that pid is good for a
        // certain amount of time
        double whenTheWheelsStartedBeingTheRightSpeed = Timer.getFPGATimestamp();
        // basically just wait for the difference in time to be
        // greater than the delay
        // this allows the shooter to get to speed
        while (Timer.getFPGATimestamp() - whenTheWheelsStartedBeingTheRightSpeed < delayTimeBeforeShooting) {
            // if manual override is activated then skip the delay
            // and go straight to next step
            if (overrideShot || cancelShot)
                break;
            // if there is sufficient error then stay in the while
            // loop
            if (!shooterAtSpeed()) {
                // if the wheels aren't at speed reset the count
                whenTheWheelsStartedBeingTheRightSpeed = Timer.getFPGATimestamp();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            setShooterSpeeds(setPoint, true);
        }
        // this is used for the servo
        double time = Timer.getFPGATimestamp();
        // push ball
        // the servo is run for a bit forward
        if (!cancelShot) {
            System.out.println("starting autofire servo push");
            while (Timer.getFPGATimestamp() - time < delayTimeForServo) {
                setShooterSpeeds(setPoint, true);
                pushBall();
            }
            double tempTime = Timer.getFPGATimestamp();
            while (Timer.getFPGATimestamp() - tempTime < 0.5) {
                setShooterSpeeds(setPoint, true);
            }
            // resets everything
            resetServoAngle();
            stopMotors();
            overrideShot = false;
            System.out.println("Finishing autofire");
        } else {
            System.out.println("The shot was calceled");
        }

    }

    private void prepShot(double setPoint) {
        setShooterSpeeds(setPoint, true);
        if (overrideShot || autoFire || cancelShot) {
            System.out.println("finished prepping the shot");
            prepShot = false;

        }
    }

    private double getSetPoint() {
        // if run auto fire (run shooter wheels, and run servo)
        // update the setPoints from the dashboard
        if (useManualSetPoint) {
//            System.out.print("My shooter wheel setpoint is " + manualSetPoint);
            useManualSetPoint = false;
            return manualSetPoint;
        }

        closeShotSetPoint = SmartDashboard.getNumber("closeShotSetpoint");
        farShotSetpoint = SmartDashboard.getNumber("farShotSetpoint");
//        System.out.println(("My shooter wheel setpoint is " + (getShooterHeight().equals(ShooterHeight.MID) ? farShotSetpoint : closeShotSetPoint)));
        return getShooterHeight().equals(ShooterHeight.MID) ? farShotSetpoint : closeShotSetPoint;
    }

    public void setAutonShotMode(boolean enableAutonShot) {
        autonShot = enableAutonShot;
    }

    private void delayArmMove() {
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        delayedArmMove = false;
    }

    public boolean shooterAtSpeed() {
        double total_error = Math.abs(leftShooterTalon.getError()) + Math.abs(rightShooterTalon.getError());
        return total_error < 200;
    }

    public void resetShooterAutoStuff() {
        overrideShot = false;
        prepShot = false;
        cancelShot = true;
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

        } else if (!autoFire && !prepShot) {
            // if shooters is not autofiring or prepping the shot then use
            // inputs given, including 0
            leftShooterTalon.changeControlMode(TalonControlMode.PercentVbus);
            rightShooterTalon.changeControlMode(TalonControlMode.PercentVbus);
            leftShooterTalon.set(targetSpeed);
            rightShooterTalon.set(targetSpeed);
        }
    }

    public void updateSmartDash() {
        // update the smartdashbaord with values
        SmartDashboard.putBoolean("Does shooter have ball", hasBall());
        SmartDashboard.putBoolean("Is auto firing", autoFire);
        SmartDashboard.putBoolean("Is preparing shot", prepShot);
    }

    public void debug() {
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
        // set the values for delay movement
        // waits a few seconds before bringing the arm down
        delayedArmMove = true;
        delayedShooterPos = pos;
    }

    public void reset() {
        // stop the motors
        stopMotors();
        // reset the PID on the Talons
        leftShooterTalon.reset();
        rightShooterTalon.reset();
    }

    public void stopMotors() {
        // set the motors to 0 to stop
        leftShooterTalon.set(0);
        rightShooterTalon.set(0);
    }

    public void disable() {
        // disable code will stop motors
        stopMotors();
        autoFire = false;
        prepShot = false;
        SmartDashboard.putNumber("right speed", rightShooterTalon.getSpeed());
        SmartDashboard.putNumber("left speed", leftShooterTalon.getSpeed());
    }

    public boolean hasBall() {
        // returns the sensor value
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

    public void autoFire(double setPoint) {
        autoFire = true;
        useManualSetPoint = true;
        manualSetPoint = setPoint;
    }

    public abstract ShooterHeight getShooterHeight();

    public void setShooterLow() {
        // both pistons will be set to true to get max height
        timeWhenShooterArmMoved = Timer.getFPGATimestamp();
        // both pistons will be set to true to get low height
        moveShooterPistonsLow();
        releaseBall();

    }

    public void setShooterMiddle() {
        // set shooter height to middle meaning only one piston will be true
        shooterArmMoving = true;
        timeWhenShooterArmMoved = Timer.getFPGATimestamp();
        moveShooterPistonsMiddle();
    }

    public void setShooterHigh() {
        // set shooter height to low , set both pistons to false
        shooterArmMoving = true;
        timeWhenShooterArmMoved = Timer.getFPGATimestamp();
        moveShooterPistonsHigh();
    }


    protected abstract void moveShooterPistonsLow();

    protected abstract void moveShooterPistonsMiddle();

    protected abstract void moveShooterPistonsHigh();

    public boolean getIfShooterHigh() {
        return (getShooterHeight().equals(ShooterHeight.HIGH));
    }

    public boolean getIfShooterMiddle() {
        return (getShooterHeight().equals(ShooterHeight.MID));
    }

    public boolean getIfShooterLow() {
        return (getShooterHeight().equals(ShooterHeight.LOW));
    }

    public void overrideAutoShot() {
        overrideShot = true;
    }

    public void prepShot() {
        System.out.println("I am currently prepping the shot");
        prepShot = true;
    }

    public boolean getIfPreppingShot() {
        return prepShot;

    }

    public boolean getIfAutoFire() {
        return autoFire;

    }

    public void releaseBall() {
        //This method uses a secondary servo to release the ball for firing
//        clampServo.set(0);
    }

    public void clampBall() {//this method uses a secondary servo to clamp down on the ball
//        clampServo.set(0.7);
    }

    public enum ShooterHeight {
        LOW, MID, HIGH
    }
}