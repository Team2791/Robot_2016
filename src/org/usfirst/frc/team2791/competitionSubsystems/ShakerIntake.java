package org.usfirst.frc.team2791.competitionSubsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import org.usfirst.frc.team2791.abstractSubsystems.AbstractShakerIntake;
import org.usfirst.frc.team2791.util.Constants;


public class ShakerIntake extends AbstractShakerIntake {
    protected DoubleSolenoid intakeSolenoid;
    //protected DoubleSolenoid armAttachment;
    protected DoubleSolenoid armAttachment;
    public ShakerIntake() {
        // init
        super();
        leftIntakeMotor = new Talon(Constants.INTAKE_TALON_LEFT_PORT);
        rightIntakeMotor = new Talon(Constants.INTAKE_TALON_RIGHT_PORT);
        intakeSolenoid = new DoubleSolenoid(Constants.PCM_MODULE, Constants.INTAKE_PISTON,Constants.INTAKE_PISTON_REVERSE);
        //armAttachment = new Solenoid(Constants.FUN_BRIDGE_ARM_PORT);
        armAttachment = new DoubleSolenoid(Constants.PCM_MODULE,Constants.FUN_BRIDGE_ARM_PORT,Constants.FUN_BRIDGE_ARM_PORT_REVERSE);
        init();
    }

    public void retractIntake() {
        // bring intake back behind bumpers
    	System.out.println("Retracting intake");
        intakeSolenoid.set(DoubleSolenoid.Value.kForward);

    }

    public void extendIntake() {
        // extends the intake for ball  pickup
    	System.out.println("Extending intake");
        intakeSolenoid.set(DoubleSolenoid.Value.kReverse);

    }

    public IntakeState getIntakeState() {//might be obsolete with double solenoid
        // returns state of intake in form of the enum IntakeState
        if (intakeSolenoid.get().equals(DoubleSolenoid.Value.kReverse))
            return IntakeState.RETRACTED;
        else if (intakeSolenoid.get().equals(DoubleSolenoid.Value.kForward))
            return IntakeState.EXTENDED;
        else
            return IntakeState.EXTENDED;
    }

    public void setArmAttachmentUp() {
//    	System.out.println("I moving the little flipper up");
        //armAttachment.set(false);
        armAttachment.set(DoubleSolenoid.Value.kForward);
    }

    public void setArmAttachmentDown() {
//    	System.out.println("I moving the little flipper down");
       // armAttachment.set(true);
        armAttachment.set(DoubleSolenoid.Value.kReverse);
    }


    public boolean getArmAttachementUp() {
        //return !armAttachment.get();
        return armAttachment.get().equals(DoubleSolenoid.Value.kForward);
    }

    public void run() {

    }
}