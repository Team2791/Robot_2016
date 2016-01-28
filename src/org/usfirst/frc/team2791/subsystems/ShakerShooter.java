package org.usfirst.frc.team2791.subsystems;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import org.usfirst.frc.team2791.configuration.Constants;
import org.usfirst.frc.team2791.configuration.Ports;


public class ShakerShooter extends ShakerSubsystem {
    private Talon leftShooterTalon;
    private Talon rightShooterTalon;
    private Solenoid leftShooterPiston;
    private Solenoid rightShooterPiston;
    private boolean robotHasBall;

    public ShakerShooter() {
        init();
    }

    public void init() {
        leftShooterTalon = new Talon(Ports.SHOOTER_TALON_LEFT_PORT);
        leftShooterTalon.setInverted(true);
        rightShooterTalon = new Talon(Ports.SHOOTER_TALON_RIGHT_PORT);
        leftShooterPiston = new Solenoid(Ports.SHOOTER_PISTON_LEFT_CHANNEL);
        rightShooterPiston = new Solenoid(Ports.SHOOTER_PISTON_RIGHT_CHANNEL);

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

    public void disable() {
        stopMotors();
    }

    public void reset() {
        stopMotors();
        setShooterRetracted();
    }

    public void update() {

    }

    //helpers methods
    public boolean isShooterHigh() {
        return rightShooterPiston.get() && leftShooterPiston.get();
    }

    public void setShooterExtended(boolean highState) {
        leftShooterPiston.set(Constants.SHOOTER_HIGH_STATE);
        rightShooterPiston.set(Constants.SHOOTER_HIGH_STATE);
    }

    public void setShooterRetracted() {
        leftShooterPiston.set(Constants.SHOOTER_LOW_STATE);
        rightShooterPiston.set(Constants.SHOOTER_LOW_STATE);
    }

    public boolean hasBall() {
        return robotHasBall;
    }

    public void moveBallForward() {

    }

    public void stopMotors() {
        leftShooterTalon.set(0.0);
        leftShooterTalon.set(0.0);
    }


}
