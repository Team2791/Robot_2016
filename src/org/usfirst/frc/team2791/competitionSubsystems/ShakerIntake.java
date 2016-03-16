package org.usfirst.frc.team2791.competitionSubsystems;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import org.usfirst.frc.team2791.abstractSubsystems.AbstractShakerIntake;
import org.usfirst.frc.team2791.util.Constants;


public class ShakerIntake extends AbstractShakerIntake {
    protected Solenoid intakeSolenoid;
    protected Relay armAttachment;

    public ShakerIntake() {
        // init
        super();
        leftIntakeMotor = new Talon(Constants.INTAKE_TALON_LEFT_PORT);
        rightIntakeMotor = new Talon(Constants.INTAKE_TALON_RIGHT_PORT);
        intakeSolenoid = new Solenoid(Constants.PCM_MODULE, Constants.INTAKE_PISTON);
        armAttachment = new Relay(3);
        init();
    }

    public void retractIntake() {
        // bring intake back behind bumpers
        intakeSolenoid.set(true);

    }

    public void extendIntake() {
        // extends the intake for ball pickup
        intakeSolenoid.set(false);

    }

    public IntakeState getIntakeState() {
        // returns state of intake in form of the enum IntakeState
        if (intakeSolenoid.get())
            return IntakeState.RETRACTED;
        else if (!intakeSolenoid.get())
            return IntakeState.EXTENDED;
        else
            return IntakeState.EXTENDED;
    }

    public void setArmAttachmentUp() {
        armAttachment.set(Relay.Value.kReverse);
    }

    public void setArmAttachmentDown() {
        armAttachment.set(Relay.Value.kForward);
    }


    public boolean getArmAttachementUp() {
        return armAttachment.get().equals(Relay.Value.kReverse);
    }

    public void run() {

    }
}
