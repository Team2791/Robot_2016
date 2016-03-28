package org.usfirst.frc.team2791.competitionSubsystems;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import org.usfirst.frc.team2791.abstractSubsystems.AbstractShakerIntake;
import org.usfirst.frc.team2791.util.Constants;


public class ShakerIntake extends AbstractShakerIntake {
    protected Solenoid intakeSolenoid;
    protected Solenoid armAttachment;

    public ShakerIntake() {
        // init
        super();
        leftIntakeMotor = new Talon(Constants.INTAKE_TALON_LEFT_PORT);
        rightIntakeMotor = new Talon(Constants.INTAKE_TALON_RIGHT_PORT);
        intakeSolenoid = new Solenoid(Constants.PCM_MODULE, Constants.INTAKE_PISTON);
        armAttachment = new Solenoid(Constants.PCM_MODULE,Constants.FUN_BRIDGE_ARM_PORT);
        init();
    }

    public void retractIntake() {
        // bring intake back behind bumpers
        intakeSolenoid.set(false);

    }

    public void extendIntake() {
        // extends the intake for ball  pickup
        intakeSolenoid.set(true);

    }

    public IntakeState getIntakeState() {
        // returns state of intake in form of the enum IntakeState
        if (!intakeSolenoid.get())
            return IntakeState.RETRACTED;
        else if (intakeSolenoid.get())
            return IntakeState.EXTENDED;
        else
            return IntakeState.EXTENDED;
    }

    public void setArmAttachmentUp() {
//    	System.out.println("I moving the little flipper up");
        armAttachment.set(false);
    }

    public void setArmAttachmentDown() {
//    	System.out.println("I moving the little flipper down");
        armAttachment.set(true);
    }


    public boolean getArmAttachementUp() {
        return !armAttachment.get();
    }

    public void run() {

    }
}