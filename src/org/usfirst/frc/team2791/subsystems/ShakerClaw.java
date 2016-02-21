package org.usfirst.frc.team2791.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;

import org.usfirst.frc.team2791.configuration.Constants;
import org.usfirst.frc.team2791.configuration.Ports;

public class ShakerClaw extends ShakerSubsystem {
    private Solenoid lowClawSolenoid;
    private Solenoid highClawSolenoid;
    public ShakerClaw() {
        lowClawSolenoid = new Solenoid(Ports.PCM_MODULE, Ports.CLAW_LOWER_CHANNEL);
        highClawSolenoid = new Solenoid(Ports.PCM_MODULE,Ports.CLAW_HIGHER_CHANNEL);
        
    }

    @Override
    public void run() {
        lowClawSolenoid.set(true);
    }

    @Override
    public void updateSmartDash() {

    }

    public void set(boolean toggle) {
            lowClawSolenoid.set(toggle);
            highClawSolenoid.set(toggle);
        
    }

    @Override
    public void reset() {
		set(false);
    }

    @Override
    public void disable() {

    }
}
