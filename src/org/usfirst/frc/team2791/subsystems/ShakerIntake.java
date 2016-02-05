package org.usfirst.frc.team2791.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Talon;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team2791.configuration.Constants;
import org.usfirst.frc.team2791.configuration.Ports;

public class ShakerIntake extends ShakerSubsystem {
    private Talon rightIntakeMotor;
    private Talon leftIntakeMotor;
    private DoubleSolenoid intakeSolenoid;

    public ShakerIntake() {
        //init
        this.leftIntakeMotor = new Talon(Ports.INTAKE_TALON_LEFT_PORT);
        this.rightIntakeMotor = new Talon(Ports.INTAKE_TALON_RIGHT_PORT);
        leftIntakeMotor.setInverted(true);
        this.intakeSolenoid = new DoubleSolenoid(Ports.PCM_MODULE, Ports.INTAKE_PISTON_CHANNEL_FORWARD,
                Ports.INTAKE_PISTON_CHANNEL_REVERSE);

    }

    public void run() {
    }

    public void updateSmartDash() {
    }

    public void reset() {
        //runs methods to bring back to original position
        retractIntake();
        stopMotors();
    }

    public void disable() {
        //when disabled makes sure that motors are stopped
        stopMotors();
    }

    public void retractIntake() {
        //bring intake back behind bumpers
        intakeSolenoid.set(Constants.INTAKE_RECTRACTED_VALUE);

    }

    public void extendIntake() {
        //extends the intake for ball pickup
        intakeSolenoid.set(Constants.INTAKE_EXTENDED_VALUE);

    }

    @NotNull
    public IntakeState getIntakeState() {
        //returns state of intake in form of the enum IntakeState
        System.out.println(intakeSolenoid.get().equals(Constants.INTAKE_RECTRACTED_VALUE));
        if (intakeSolenoid.get().equals(Constants.INTAKE_RECTRACTED_VALUE))
            return IntakeState.RETRACTED;
        else if (intakeSolenoid.get().equals(Constants.INTAKE_EXTENDED_VALUE))
            return IntakeState.EXTENDED;
        else
            return IntakeState.EXTENDED;
    }

    public void stopMotors() {
        //sends 0 to both motors to stop them
        leftIntakeMotor.set(0.0);
        rightIntakeMotor.set(0.0);
    }

    public void pullBall() {
        //runs intake inward
        leftIntakeMotor.set(Constants.INTAKE_SPEED);
        rightIntakeMotor.set(Constants.INTAKE_SPEED);
    }

    public void pushBall() {
        //runs intake outward
        leftIntakeMotor.set(-Constants.INTAKE_SPEED);
        rightIntakeMotor.set(-Constants.INTAKE_SPEED);

    }

    public enum IntakeState {
        RETRACTED, EXTENDED
    }
}
