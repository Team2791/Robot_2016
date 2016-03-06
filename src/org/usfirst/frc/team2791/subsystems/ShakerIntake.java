//package org.usfirst.frc.team2791.subsystems;
//
//import edu.wpi.first.wpilibj.Relay;
//import edu.wpi.first.wpilibj.Solenoid;
//import edu.wpi.first.wpilibj.Talon;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import org.usfirst.frc.team2791.util.Constants;
//
//public class ShakerIntake extends ShakerSubsystem {
//    private static ShakerIntake intakeInstance;
//    private Talon rightIntakeMotor;
//    private Talon leftIntakeMotor;
//    private Solenoid intakeSolenoid;
//    private Relay armAttachment;
//
//    private ShakerIntake() {
//        // init
//        this.leftIntakeMotor = new Talon(Constants.INTAKE_TALON_LEFT_PORT);
//        this.rightIntakeMotor = new Talon(Constants.INTAKE_TALON_RIGHT_PORT);
//        leftIntakeMotor.setInverted(true);
//        this.intakeSolenoid = new Solenoid(Constants.PCM_MODULE, Constants.INTAKE_PISTON);
//        armAttachment = new Relay(3);
//
//    }
//
//    public static ShakerIntake getInstance() {
//        if (intakeInstance == null)
//            intakeInstance = new ShakerIntake();
//        return intakeInstance;
//    }
//
//    public void run() {
//    }
//
//
//    public void updateSmartDash() {
//    }
//
//    public void debug() {
//        SmartDashboard.putString("Intake state", getIntakeState().toString());
//    }
//
//
//    public void reset() {
//        // runs methods to bring back to original position
//        retractIntake();
//        stopMotors();
//    }
//
//    public void disable() {
//        // when disabled makes sure that motors are stopped
//        stopMotors();
//    }
//
//    public void retractIntake() {
//        // bring intake back behind bumpers
//        intakeSolenoid.set(true);
//
//    }
//
//    public void extendIntake() {
//        // extends the intake for ball pickup
//        intakeSolenoid.set(false);
//
//    }
//
//    public IntakeState getIntakeState() {
//        // returns state of intake in form of the enum IntakeState
//        if (intakeSolenoid.get())
//            return IntakeState.RETRACTED;
//        else if (!intakeSolenoid.get())
//            return IntakeState.EXTENDED;
//        else
//            return IntakeState.EXTENDED;
//    }
//
//    public void stopMotors() {
//        // sends 0 to both motors to stop them
//        leftIntakeMotor.set(0.0);
//        rightIntakeMotor.set(0.0);
//    }
//
//    public void pullBall() {
//        // runs intake inward
//        leftIntakeMotor.set(Constants.INTAKE_SPEED);
//        rightIntakeMotor.set(Constants.INTAKE_SPEED);
//    }
//
//    public void pushBall() {
//        // runs intake outward
//        leftIntakeMotor.set(-Constants.INTAKE_SPEED);
//        rightIntakeMotor.set(-Constants.INTAKE_SPEED);
//
//    }
//
//    public void setArmAttachmentUp() {
//        armAttachment.set(Relay.Value.kReverse);
//    }
//
//    public void setArmAttachmentDown() {
//        armAttachment.set(Relay.Value.kForward);
//    }
//
//    public enum IntakeState {
//        RETRACTED, EXTENDED
//    }
//}
