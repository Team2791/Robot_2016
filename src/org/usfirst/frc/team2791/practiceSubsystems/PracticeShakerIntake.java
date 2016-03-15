package org.usfirst.frc.team2791.practiceSubsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Talon;
import org.usfirst.frc.team2791.abstractSubsystems.AbstractShakerIntake;

public class PracticeShakerIntake extends AbstractShakerIntake  {
    private DoubleSolenoid intakeSolenoid;
    private DoubleSolenoid armAttachment;

    public PracticeShakerIntake() {
        // init
        leftIntakeMotor = new Talon(PracticePorts.INTAKE_TALON_LEFT_PORT);
        rightIntakeMotor = new Talon(PracticePorts.INTAKE_TALON_RIGHT_PORT);
        
        intakeSolenoid = new DoubleSolenoid(PracticePorts.PCM_MODULE, PracticePorts.INTAKE_PISTON_CHANNEL_FORWARD,
                PracticePorts.INTAKE_PISTON_CHANNEL_REVERSE);

        armAttachment = new DoubleSolenoid(PracticePorts.PCM_MODULE, PracticePorts.INTAKE_ARM_CHANNEL_FORWARD,
                PracticePorts.INTAKE_ARM_CHANNEL_REVERSE);
        init();

    }

    // This system does not need to do anything continiously so this method is blank 
    public void run() {
    }

    public void retractIntake() {
        // bring intake back behind bumpers
        intakeSolenoid.set(PracticeConstants.INTAKE_RECTRACTED_VALUE);

    }

    public void extendIntake() {
        // extends the intake for ball pickup
        intakeSolenoid.set(PracticeConstants.INTAKE_EXTENDED_VALUE);

    }
	
	@Override
    public IntakeState getIntakeState() {
        // returns state of intake in form of the enum IntakeState
        if (intakeSolenoid.get().equals(PracticeConstants.INTAKE_RECTRACTED_VALUE))
            return IntakeState.RETRACTED;
        else if (intakeSolenoid.get().equals(PracticeConstants.INTAKE_EXTENDED_VALUE))
            return IntakeState.EXTENDED;
        else
            return IntakeState.EXTENDED;
    }
	
	@Override
    public void setArmAttachmentUp() {
        armAttachment.set(PracticeConstants.INTAKE_ARM_UP_VALUE);
    }

	@Override
    public void setArmAttachmentDown() {
        armAttachment.set(PracticeConstants.INTAKE_ARM_DOWN_VALUE);
    }

    @Override
    public boolean getArmAttachementUp() {
        return false;
    }

}
