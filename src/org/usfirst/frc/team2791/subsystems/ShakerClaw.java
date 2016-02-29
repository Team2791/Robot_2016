package org.usfirst.frc.team2791.subsystems;

import edu.wpi.first.wpilibj.Solenoid;
import org.usfirst.frc.team2791.util.Constants;

public class ShakerClaw extends ShakerSubsystem {
    private Solenoid lowClawSolenoid;
    private Solenoid highClawSolenoid;

    public ShakerClaw() {
        lowClawSolenoid = new Solenoid(Constants.PCM_MODULE, Constants.CLAW_LOWER_CHANNEL);
        highClawSolenoid = new Solenoid(Constants.PCM_MODULE, Constants.CLAW_HIGHER_CHANNEL);

    }

    public void run() {
        set(true);
    }

    
    public void updateSmartDash() {

    }

    public void set(boolean toggle) {
        lowClawSolenoid.set(toggle);
        highClawSolenoid.set(toggle);

    }

    public void reset() {
        set(false);
    }

    public void disable() {

    }
}
