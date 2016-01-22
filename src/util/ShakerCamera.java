package util;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ShapeMode;
import edu.wpi.first.wpilibj.CameraServer;

public class ShakerCamera {
    private static int session;
    private static Image frame;
    private static final int Y_IMAGE_RES=240;
    private static NIVision.Rect rect;

    public ShakerCamera(int sessionNumber) {
        this.session = sessionNumber;
        session = NIVision.IMAQdxOpenCamera("cam0", NIVision.IMAQdxCameraControlMode.CameraControlModeController);
        rect = new NIVision.Rect(10, 10, 100, 100);
        NIVision.IMAQdxStartAcquisition(session);
        update();
    }

    public static void update() {
        NIVision.IMAQdxGrab(session, frame, 1);
        NIVision.imaqDrawShapeOnImage(frame, frame, rect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_OVAL, 0.0f);
        CameraServer.getInstance().setImage(frame);
    }

//    public void CalculateDist(Target targets) {
//        //vertical target is 32 inches fixed
//        double targetHeight = 32.0;
//
//        //get vertical pixels from targets
//        int height = targets.VerticalTarget.height;
//
//        //d = Tft*FOVpixel/(2*Tpixel*tanΘ)
//        targets.targetDistance = Y_IMAGE_RES * targetHeight
//                / (height * 12 * 2 * math.tan(VIEW_ANGLE * PI / (180 * 2)));
//    }
}
