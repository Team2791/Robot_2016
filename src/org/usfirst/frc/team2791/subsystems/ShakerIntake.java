package org.usfirst.frc.team2791.subsystems;

import edu.wpi.first.wpilibj.Talon;
import org.usfirst.frc.team2791.configuration.Constants;
import org.usfirst.frc.team2791.configuration.Ports;

/**
 * Created by Akhil on 1/28/2016.
 */
public class ShakerIntake extends ShakerSubsystem {
    private Talon rightIntakeMotor;
    private Talon leftIntakeMotor;
//    private DoubleSolenoid rightIntakeSolenoid;
//    private DoubleSolenoid leftIntakeSolenoid;

    public ShakerIntake() {
        init();
    }

    protected void init() {
        this.leftIntakeMotor = new Talon(Ports.INTAKE_TALON_LEFT_PORT);
        this.rightIntakeMotor = new Talon(Ports.INTAKE_TALON_RIGHT_PORT);
        leftIntakeMotor.setInverted(true);
//        this.leftIntakeSolenoid = new DoubleSolenoid(Ports.PCM_MODULE, Ports.INTAKE_PISTON_LEFT_CHANNEL_FORWARD, Ports.INTAKE_PISTON_LEFT_CHANNEL_REVERSE);
//        this.rightIntakeSolenoid = new DoubleSolenoid(Ports.PCM_MODULE, Ports.INTAKE_PISTON_RIGHT_CHANNEL_FORWARD, Ports.INTAKE_PISTON_RIGHT_CHANNEL_REVERSE);
    }

    public void run() {
    }

    public void update() {

    }

    public void reset() {

    }

    public void disable() {

    }

    public void retractIntake() {

    }

    public void extendIntake() {

    }

    public void pullBall() {
        leftIntakeMotor.set(Constants.SHOOTER_SPEED);
        rightIntakeMotor.set(Constants.SHOOTER_SPEED);
    }

    public void pushBall() {
        leftIntakeMotor.set(-Constants.SHOOTER_SPEED);
        rightIntakeMotor.set(-Constants.SHOOTER_SPEED);

    }
}
