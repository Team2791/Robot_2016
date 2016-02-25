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

    @Override
    public void run() {
        set(true);
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
