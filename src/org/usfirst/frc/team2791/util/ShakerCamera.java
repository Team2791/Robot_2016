package org.usfirst.frc.team2791.util;

import com.ni.vision.NIVision;
import edu.wpi.first.wpilibj.CameraServer;

public class ShakerCamera {
    private final int STARTX = 320;
    private final int STARTY = 0;
    private final int ENDX = 320;
    private final int ENDY = 480;
    private int session;
    private NIVision.Image image;

    public ShakerCamera() {
        image = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
        session = NIVision.IMAQdxOpenCamera("cam1",
                NIVision.IMAQdxCameraControlMode.CameraControlModeController);
        NIVision.IMAQdxConfigureGrab(session);
    }

    public void update(boolean drawOnImage) {
        NIVision.IMAQdxGrab(session, image, 1);
        if (drawOnImage) {
            NIVision.imaqDrawLineOnImage(image, image, NIVision.DrawMode.DRAW_VALUE, new NIVision.Point(STARTX, STARTY),
                    new NIVision.Point(ENDX, ENDY), 130.0f);
        }
        CameraServer.getInstance().setImage(image);
    }
}
