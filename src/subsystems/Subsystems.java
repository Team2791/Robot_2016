package subsystems;

public interface Subsystems {

    void init();

    void initTeleop();

    void initDisabled();

    void initAutonomous();

    void runTeleop();

    void runDisabled();

    void runAutonomous();

    void reset();

}
