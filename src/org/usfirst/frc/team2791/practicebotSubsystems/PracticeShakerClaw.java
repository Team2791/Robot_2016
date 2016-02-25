package org.usfirst.frc.team2791.practicebotSubsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class PracticeShakerClaw extends PracticeShakerSubsystem {
    private DoubleSolenoid clawSolenoid;
    private DoubleSolenoid clawSolenoid2;

    public PracticeShakerClaw() {
        clawSolenoid = new DoubleSolenoid(PracticePorts.SECOND_PCM_MODULE, PracticePorts.CLAW_REVERSE_CHANNEL,
                PracticePorts.CLAW_FORWARD_CHANNEL);
        clawSolenoid2 = new DoubleSolenoid(PracticePorts.SECOND_PCM_MODULE, PracticePorts.CLAW_TWO_REVERSE_CHANNEL,
                PracticePorts.CLAW_TWO_FORWARD_CHANNEL);

    }

    @Override
    public void run() {
        clawSolenoid.set(PracticeConstants.CLAW_EXTENDED_VALUE);
        clawSolenoid2.set(PracticeConstants.CLAW_EXTENDED_VALUE);
    }

    @Override
    public void updateSmartDash() {

    }

    public void set(boolean toggle) {
        if (toggle) {
            clawSolenoid.set(PracticeConstants.CLAW_EXTENDED_VALUE);
            clawSolenoid2.set(PracticeConstants.CLAW_EXTENDED_VALUE);
        } else {
            clawSolenoid.set(PracticeConstants.CLAW_RETRACTED_VALUE);
            clawSolenoid2.set(PracticeConstants.CLAW_RETRACTED_VALUE);
        }
    }

    @Override
    public void reset() {
        clawSolenoid.set(PracticeConstants.CLAW_RETRACTED_VALUE);
        clawSolenoid.set(PracticeConstants.CLAW_RETRACTED_VALUE);
    }

    @Override
    public void disable() {

    }
}
