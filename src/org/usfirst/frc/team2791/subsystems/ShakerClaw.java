package org.usfirst.frc.team2791.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.usfirst.frc.team2791.configuration.Constants;
import org.usfirst.frc.team2791.configuration.Ports;

public class ShakerClaw extends ShakerSubsystem {
    private DoubleSolenoid clawSolenoid;

    public ShakerClaw() {
        clawSolenoid = new DoubleSolenoid(Ports.SECOND_PCM_MODULE, Ports.CLAW_REVERSE_CHANNEL, Ports.CLAW_FORWARD_CHANNEL);

    }

    @Override
    public void run() {
        clawSolenoid.set(Constants.CLAW_EXTENDED_VALUE);
    }

    @Override
    public void updateSmartDash() {

    }

    public void set(boolean toggle) {
        if (toggle)
            clawSolenoid.set(Constants.CLAW_EXTENDED_VALUE);
        else
            clawSolenoid.set(Constants.CLAW_RETRACTED_VALUE);
    }

    @Override
    public void reset() {
        clawSolenoid.set(Constants.CLAW_RETRACTED_VALUE);
    }

    @Override
    public void disable() {

    }
}
