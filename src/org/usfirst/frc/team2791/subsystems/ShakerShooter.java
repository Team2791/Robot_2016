package org.usfirst.frc.team2791.subsystems;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team2791.configuration.Constants;
import org.usfirst.frc.team2791.configuration.PID;
import org.usfirst.frc.team2791.configuration.Ports;

public class ShakerShooter extends ShakerSubsystem implements Runnable {
    private static final int updateDelayMs = 1000 / 100; // run at 100 Hz
    //    private final double[] speed = {0.25, 0.5, 0.75, 1.0};
    private final double delayTimeBeforeShooting = 0.5;// time for wheels to get to speed
    private final double delayTimeForServo = 1.0;// time for servo to push

    private CANTalon leftShooterTalon;
    private CANTalon rightShooterTalon;
    private Solenoid firstLevelSolenoid;
    private Solenoid secondLevelSolenoid;
    private Servo ballAidServo;
    private AnalogInput ballCheckingSensor;
    private boolean autoFire;
    private double fireSpeed = 1.0; //send percentage from -1 to 1
    private double time;

    public ShakerShooter() {
        // init
        //constructors
        ballAidServo = new Servo(Ports.BALL_AID_SERVO_PORT);
        leftShooterTalon = new CANTalon(Ports.SHOOTER_TALON_LEFT_PORT);
        rightShooterTalon = new CANTalon(Ports.SHOOTER_TALON_RIGHT_PORT);
        ballCheckingSensor = new AnalogInput(Ports.BALL_DISTANCE_SENSOR_PORT);
        firstLevelSolenoid = new Solenoid(Ports.PCM_MODULE, Ports.SHOOTER_PISTON_CHANNEL_FIRST_LEVEL);
        secondLevelSolenoid = new Solenoid(Ports.PCM_MODULE, Ports.SHOOTER_PISTON_CHANNEL_SECOND_LEVEL);
        //configuration
        rightShooterTalon.setInverted(true);
        leftShooterTalon.changeControlMode(CANTalon.TalonControlMode.Speed);
        SmartDashboard.putNumber("Fire Speed", fireSpeed);

    }

    public void run() {
        while (true) {
            if (autoFire) {
                //sets shooter pid
                leftShooterTalon.setPID(PID.SHOOTER_P, PID.SHOOTER_I, PID.SHOOTER_D);
                rightShooterTalon.setPID(PID.SHOOTER_P, PID.SHOOTER_I, PID.SHOOTER_D);
                //gets fire speed from the dashboard (FOR TESTING ONLY!!!!)
                fireSpeed = SmartDashboard.getNumber("Fire Speed");
                //soft limits for fire speed
                fireSpeed = fireSpeed >= 1.0 ? 1.0 : fireSpeed;
                fireSpeed = fireSpeed <= -1.0 ? -1.0 : fireSpeed;
                //converts the fireSpeed percentage to speed values and sets pid to that
                setShooterSpeeds(fireSpeed * 1500.0);
                time = Timer.getFPGATimestamp();
                //buffer time for wheels to get to speed
                while (Timer.getFPGATimestamp() - time < delayTimeBeforeShooting) {
                    setShooterSpeeds(fireSpeed);
                    System.out.println("The wheels are spinning up");
                }
                //update current time
                time = Timer.getFPGATimestamp();
                //push ball
                while (Timer.getFPGATimestamp() - time < delayTimeForServo) {
                    setShooterSpeeds(fireSpeed);
                    pushBall();
                    System.out.println("The ball is being pushed out");
                }
                //reset everything
                stopMotors();
                autoFire = false;
                try {
                    //slows down the rate at which this method is called(so it doesnt run too fast)
                    Thread.sleep(updateDelayMs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void setShooterSpeeds(double syncedSpeed) {
        // this shouldn't be used except for testing or for practice bot....
        leftShooterTalon.set(syncedSpeed);
        rightShooterTalon.set(syncedSpeed);
    }


    public void disable() {
        // disable code --stops wheels
        stopMotors();
    }

    public void reset() {
        // bring robot back to starting configuration --maybe put one button on
        // dash to reset robot pos
        stopMotors();
        // setShooterLow();
    }

    public void updateSmartDash() {
        // SmartDashboard.putString("Shooter Height: ",
        // getShooterHeight().toString());
        SmartDashboard.putNumber("Ball Distance Sensor", ballCheckingSensor.getVoltage());
        SmartDashboard.putNumber("Left shooter Error", leftShooterTalon.getClosedLoopError());
        SmartDashboard.putNumber("Left shooter Error", rightShooterTalon.getClosedLoopError());

    }

    @NotNull
    public ShooterHeight getShooterHeight() {
        // get current shooter height by determining which solenoid are true
        if (firstLevelSolenoid.get() && secondLevelSolenoid.get())
            return ShooterHeight.HIGH;
        else if (firstLevelSolenoid.get())
            return ShooterHeight.MID;
        else
            return ShooterHeight.LOW;

    }

    public void setShooterLow() {
        // set shooter height to low , set both pistons to false
        firstLevelSolenoid.set(Constants.SHOOTER_LOW_STATE);
        secondLevelSolenoid.set(Constants.SHOOTER_LOW_STATE);
    }

    public void setShooterMiddle() {
        // set shooter height to middle meaning only one piston will be true
        firstLevelSolenoid.set(Constants.SHOOTER_HIGH_STATE);
        secondLevelSolenoid.set(Constants.SHOOTER_LOW_STATE);
    }

    public void setShooterHigh() {
        // both pistons will be set to true to get max height
        firstLevelSolenoid.set(Constants.SHOOTER_HIGH_STATE);
        secondLevelSolenoid.set(Constants.SHOOTER_HIGH_STATE);
    }

    public boolean hasBall() {
        // this boolean will be determined by possible sensors
        // will be used for auto firing
        return ballCheckingSensor.getAverageVoltage() > Constants.THRESHOLD_BALL_DISTANCE;
    }

    public void pushBall() {
        // will be used to push ball toward the shooter

        ballAidServo.set(1);

    }

    public void resetServoAngle() {
        // bring servo back to original position
        ballAidServo.set(0);
    }

    public void autoFire() {
        fireSpeed = SmartDashboard.getNumber("Fire Speed");
        autoFire = true;
    }


    public void stopMotors() {
        // bring both motors to stop
        leftShooterTalon.set(0.0);
        rightShooterTalon.set(0.0);
    }

    public enum ShooterHeight {
        LOW, MID, HIGH
    }


}
//This run method works but doesn't use PID
//    public void run() {
//        while (true) {
//            try {
//
//                leftShooterTalon.setPID(PID.SHOOTER_P, PID.SHOOTER_I, PID.SHOOTER_D);
//                rightShooterTalon.setPID(PID.SHOOTER_P, PID.SHOOTER_I, PID.SHOOTER_D);
//                if (autoFire) {// if auto fire
//                    // Runs the wheels at the set speed (for now gets from the smartDash)
//                    fireSpeed = SmartDashboard.getNumber("Fire Speed");
//                    //updates time current time for delays
//                    time = Timer.getFPGATimestamp();
//                    //runs the while loop until delay is met
//                    //this allows the shooter wheels buffer time before firing
//                    while (Timer.getFPGATimestamp() - time < delayTimeBeforeShooting) {
//                        setShooterSpeeds(fireSpeed);
//                        System.out.println("The wheels are spinning up");
//                    }
//                    //update time for next while loop
//                    time = Timer.getFPGATimestamp();
//                    //this while loop keeps shooters running while running servo
//                    while (Timer.getFPGATimestamp() - time < delayTimeForServo) {
//                        setShooterSpeeds(fireSpeed);
//                        pushBall();
//                        System.out.println("The ball is being pushed out");
//
//                    }
////                    while (Timer.getFPGATimestamp() - time < delayTimeForServo) {
////                        System.out.println("RESETTING SHOOTER....");
////                        resetServoAngle();
//
////                    }
//
//                    leftShooterTalon.set(0);
//                    rightShooterTalon.set(0);
//                    autoFire = false;
//
//                }
//                Thread.sleep(updateDelayMs);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }