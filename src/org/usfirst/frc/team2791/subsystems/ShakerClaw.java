package org.usfirst.frc.team2791.subsystems;


import edu.wpi.first.wpilibj.Solenoid;
import org.usfirst.frc.team2791.configuration.Ports;

public class ShakerClaw extends ShakerSubsystem {
    private Solenoid levelOneSolenoid;
    private Solenoid levelTwoSolenoid;

    public ShakerClaw() {
        levelOneSolenoid = new Solenoid(Ports.PCM_MODULE, Ports.CLAW_LEVEL_ONE_SOLENOID_PORT);
        levelTwoSolenoid = new Solenoid(Ports.PCM_MODULE, Ports.CLAW_LEVEL_TWO_SOLENOID_PORT);
    }

    public void run() {
        levelOneSolenoid.set(true);
        levelTwoSolenoid.set(true);
    }

    public void updateSmartDash() {

    }

    public void set(boolean toggle) {
        levelOneSolenoid.set(toggle);
        levelTwoSolenoid.set(toggle);
    }

    public void reset() {
        levelOneSolenoid.set(false);
        levelTwoSolenoid.set(false);
    }

    public void disable() {

    }
}
