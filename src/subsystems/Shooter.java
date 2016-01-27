package subsystems;

import configuration.Constants;
import configuration.Ports;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;

/**
 *
 */
public class Shooter extends ShakerSubsystem {
    private Talon leftShooterTalon;
    private Talon rightShooterTalon;
    private Solenoid levelOnePiston;
    //private Solenoid levelTwoPiston;
    private ShooterHeight shooterHeight;

    public Shooter() {
        init();
    }

    public void init() {
        leftShooterTalon = new Talon(Ports.SHOOTER_TALON_LEFT);
        leftShooterTalon.setInverted(true);
        rightShooterTalon = new Talon(Ports.SHOOTER_TALON_RIGHT);
        levelOnePiston = new Solenoid(Ports.FIRST_HEIGHT_PISTON_PORT);
        //levelTwoPiston = new Solenoid(Ports.SECOND_HEIGHT_PISTON_PORT);
        shooterHeight = getShooterHeight();
    }

    public void run() {
        leftShooterTalon.set(Constants.SHOOTER_SPEED);
        rightShooterTalon.set(Constants.SHOOTER_SPEED);
    }

    public void run(double syncedSpeed) {
        leftShooterTalon.set(syncedSpeed);
        rightShooterTalon.set(syncedSpeed);
    }

    public void run(double left, double right) {
        leftShooterTalon.set(left);
        rightShooterTalon.set(right);
    }

    private ShooterHeight getShooterHeight() {
        if (levelOnePiston.get()) {//if true then it is extended
            //if (levelTwoPiston.get())//if true second piston is also extended
            //return ShooterHeight.HIGH;
            return ShooterHeight.MID;
        }
        return ShooterHeight.LOW;
    }

    public void setMidLevel() {
        levelOnePiston.set(true);
    }

    public void setHighLevel() {
        //setMidLevel();
        //levelTwoPiston.set(true);
    }

    public void update() {

    }

    public void stopMotors() {
        leftShooterTalon.set(0.0);
        leftShooterTalon.set(0.0);
    }

    public void disable() {
        stopMotors();
    }

    public void reset() {

    }

    private enum ShooterHeight {
        LOW, MID, HIGH
    }

}
