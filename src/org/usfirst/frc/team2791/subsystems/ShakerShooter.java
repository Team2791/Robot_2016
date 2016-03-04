package org.usfirst.frc.team2791.subsystems;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.util.Constants;

public class ShakerShooter extends ShakerSubsystem implements Runnable {
    private static final int updateDelayMs = 1000 / 100; // run at 100 Hz
    //time that the shooter wheels have to be at the proper speed
    private static final double delayTimeBeforeShooting = 0.5;
    private static ShakerShooter shooterInstance;
    private final double delayTimeForServo = 0.8;// time for servo to push
    //how many ticks the encoder has
    private final int encoderTicks = 128 * 4;
    //shooter can talons
    private CANTalon leftShooterTalon;
    private CANTalon rightShooterTalon;
    //shooter arm positiion pistons
    private DoubleSolenoid shortPiston;
    private DoubleSolenoid longPiston;
    //servo that pushes ball into the shooter
    private Servo ballAidServo;
    //sensor that allows us to know that the ball is in the shooter
    private AnalogInput ballDistanceSensor;
    //feed forward of the shooter wheel pid
    private double feedForward = 0.4;
    //setpoints to acheicve target depending on the pos of the shooter arm
    private double closeShotSetPoint = 590;
    private double farShotSetpoint = 850;
    //boolean that decides weahter autofiring should occur
    private boolean autoFire = false;
    //manual override boolean for the autofire
    private boolean overrideShot = false;
    //prepshot decides whether to run the shooter wheels before hand to save time
    private boolean prepShot = false;
    //check is shooter arm is moving
    private boolean shooterArmMoving = false;
    //time when the shooter arm last moved
    private double timeWhenShooterArmMoved;
    //boolean that decides whether the arm should move in a delay motion
    private boolean delayedArmMove = false;
    //shooter height setpoint
    private ShooterHeight delayedShooterPos;

    private ShakerShooter() {
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
        //sets limits in each direction such that shooters don't run reverse with pid
        leftShooterTalon.configPeakOutputVoltage(+12.0f, 0);
        rightShooterTalon.configPeakOutputVoltage(+12.0f, 0);
        //put the shooter pid values on the dashboard
        SmartDashboard.putNumber("Shooter p", Constants.SHOOTER_P);
        SmartDashboard.putNumber("Shooter i", Constants.SHOOTER_I);
        SmartDashboard.putNumber("Shooter d", Constants.SHOOTER_D);
        SmartDashboard.putNumber("FeedForward", feedForward);
        //put setpoints on the dashboard
        SmartDashboard.putNumber("closeShotSetpoint", closeShotSetPoint);
        SmartDashboard.putNumber("farShotSetpoint", farShotSetpoint);
        //izone for the talons
        //izone is how close the shooter has to be to the setpoint before using i in pid
        leftShooterTalon.setIZone(500);
        rightShooterTalon.setIZone(500);
        //choose the type of sensor attached to the talon
        leftShooterTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        rightShooterTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        //how many ticks are in the feed back device
        leftShooterTalon.configEncoderCodesPerRev(encoderTicks);
        rightShooterTalon.configEncoderCodesPerRev(encoderTicks);
        //control mode - speed(rpms), voltage(how many volts to be sent to the talons)
        //percentage(voltage sent/ 12 v)
        leftShooterTalon.changeControlMode(TalonControlMode.Speed);
        rightShooterTalon.changeControlMode(TalonControlMode.Speed);
        //enable the talons
        leftShooterTalon.enableControl();
        rightShooterTalon.enableControl();
        leftShooterTalon.enable();
        rightShooterTalon.enable();

        leftShooterTalon.configNominalOutputVoltage(0, 0);
        rightShooterTalon.configNominalOutputVoltage(0, 0);

    }

    public static ShakerShooter getInstance() {
        if (shooterInstance == null)
            shooterInstance = new ShakerShooter();
        return shooterInstance;
    }

    public void run() {
        try {
            while (true) {
                // if the shooter arm is moving just run the intake slightly to
                // pull the ball in
                if (shooterArmMoving) {
                    while (Timer.getFPGATimestamp() - timeWhenShooterArmMoved < 0.9) {
                        setShooterSpeeds(-0.7, false);
                    }
                    shooterArmMoving = false;
                }
                //wait a few seconds before moving the arm
                //this is used to allow the intake time before bringing the arm down
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
                // update the setPoints from the dashboard
                closeShotSetPoint = SmartDashboard.getNumber("closeShotSetpoint");
                farShotSetpoint = SmartDashboard.getNumber("farShotSetpoint");
                // choose the setpoint by getting arm pos
                double setPoint = getShooterHeight().equals(ShooterHeight.MID) ? farShotSetpoint : closeShotSetPoint;
                // this to allow the shooters to give sometime to speed up
                if (prepShot) {
                    System.out.println("I am currently prepping the shot");
                    setShooterSpeeds(setPoint, true);
                    if (overrideShot || autoFire) {
                        System.out.println("finished prepping the shot");
                        prepShot = false;
                    }
                }
                if (autoFire) {
                    System.out.println("Auto Firing starting");
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
                    setShooterSpeeds(0, true);
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
        } else
            stopMotors();
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
        //set the values for delay movement
        //waits a few seconds before bringing the arm down
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
        shooterArmMoving = true;
        timeWhenShooterArmMoved = Timer.getFPGATimestamp();
        // both pistons will be set to true to get low height
        shortPiston.set(Constants.SMALL_PISTON_HIGH_STATE); // was reverse
        // //this is short
        // one
        longPiston.set(Constants.LARGE_PISTON_HIGH_STATE);

    }

    public void setShooterMiddle() {
        // set shooter height to middle meaning only one piston will be true
        shooterArmMoving = true;
        timeWhenShooterArmMoved = Timer.getFPGATimestamp();
        shortPiston.set(Constants.SMALL_PISTON_LOW_STATE);
        longPiston.set(Constants.LARGE_PISTON_HIGH_STATE);
    }

    public void setShooterHigh() {
        // set shooter height to low , set both pistons to false
        shooterArmMoving = true;
        timeWhenShooterArmMoved = Timer.getFPGATimestamp();
        shortPiston.set(Constants.SMALL_PISTON_LOW_STATE);
        longPiston.set(Constants.LARGE_PISTON_LOW_STATE);
        // short needs to switch
    }

    public void overrideAutoShot() {
        overrideShot = true;
    }

    public void prepShot() {
        prepShot = true;
    }

    public boolean getIfPreppingShot() {
        return prepShot;

    }

    public boolean getIfAutoFire() {
        return autoFire;

    }

    public enum ShooterHeight {
        LOW, MID, HIGH
    }
}