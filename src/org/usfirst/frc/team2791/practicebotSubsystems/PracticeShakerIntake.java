package org.usfirst.frc.team2791.practicebotSubsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.subsystems.ShakerSubsystem;

public class PracticeShakerIntake extends ShakerSubsystem {
    private Talon rightIntakeMotor;
    private Talon leftIntakeMotor;
    private DoubleSolenoid intakeSolenoid;
    private DoubleSolenoid armAttachment;

    public PracticeShakerIntake() {
        // init
        this.leftIntakeMotor = new Talon(PracticePorts.INTAKE_TALON_LEFT_PORT);
        this.rightIntakeMotor = new Talon(PracticePorts.INTAKE_TALON_RIGHT_PORT);
        leftIntakeMotor.setInverted(true);
        this.intakeSolenoid = new DoubleSolenoid(PracticePorts.PCM_MODULE, PracticePorts.INTAKE_PISTON_CHANNEL_FORWARD,
                PracticePorts.INTAKE_PISTON_CHANNEL_REVERSE);

        armAttachment = new DoubleSolenoid(PracticePorts.PCM_MODULE, PracticePorts.INTAKE_ARM_CHANNEL_FORWARD,
                PracticePorts.INTAKE_ARM_CHANNEL_REVERSE);

    }

    @Override
    public void run() {
    }

    @Override
    public void updateSmartDash() {
        SmartDashboard.putString("Intake state", getIntakeState().toString());
    }

    @Override
    public void reset() {
        // runs methods to bring back to original position
        retractIntake();
        stopMotors();
    }

    @Override
    public void disable() {
        // when disabled makes sure that motors are stopped
        stopMotors();
    }

    public void retractIntake() {
        // bring intake back behind bumpers
        intakeSolenoid.set(PracticeConstants.INTAKE_RECTRACTED_VALUE);

    }

    public void extendIntake() {
        // extends the intake for ball pickup
        intakeSolenoid.set(PracticeConstants.INTAKE_EXTENDED_VALUE);

    }

    public IntakeState getIntakeState() {
        // returns state of intake in form of the enum IntakeState
//        System.out.println("intake extended: " + intakeSolenoid.get().equals(PracticeConstants.INTAKE_RECTRACTED_VALUE));
        if (intakeSolenoid.get().equals(PracticeConstants.INTAKE_RECTRACTED_VALUE))
            return IntakeState.RETRACTED;
        else if (intakeSolenoid.get().equals(PracticeConstants.INTAKE_EXTENDED_VALUE))
            return IntakeState.EXTENDED;
        else
            return IntakeState.EXTENDED;
    }

    public void stopMotors() {
        // sends 0 to both motors to stop them
        leftIntakeMotor.set(0.0);
        rightIntakeMotor.set(0.0);
    }

    public void pullBall() {
        // runs intake inward
        leftIntakeMotor.set(PracticeConstants.INTAKE_SPEED);
        rightIntakeMotor.set(PracticeConstants.INTAKE_SPEED);
    }

    public void pushBall() {
        // runs intake outward
        leftIntakeMotor.set(-PracticeConstants.INTAKE_SPEED);
        rightIntakeMotor.set(-PracticeConstants.INTAKE_SPEED);

    }

    public void setArmAttachmentUp() {
        armAttachment.set(PracticeConstants.INTAKE_ARM_UP_VALUE);
    }

    public void setArmAttachmentDown() {
        armAttachment.set(PracticeConstants.INTAKE_ARM_DOWN_VALUE);
    }

    public enum IntakeState {
        RETRACTED, EXTENDED
    }
}