package org.usfirst.frc.team2791.util;

import com.ni.vision.NIVision;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

//public class ShakerCamera extends AxisCamera {
public class ShakerCamera {
    private final int STARTX = 320;
    private final int STARTY = 0;
    private final int ENDX = 320;
    private final int ENDY = 480;
    private double halfCamHtFt;
    private NetworkTable netTable;
    private int session;
    private NIVision.Image image;

    public ShakerCamera() {
//		try {
//
        image = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
        session = NIVision.IMAQdxOpenCamera("cam1",
                NIVision.IMAQdxCameraControlMode.CameraControlModeController);
        NIVision.IMAQdxConfigureGrab(session);
//			halfCamHtPx = 120;
//			targetHeightFt = 1.16667;
//		} catch (Error e) {
//
//		}
    }

    public void update(boolean drawOnImage) {
        NIVision.IMAQdxGrab(session, image, 1);
        if (drawOnImage) {
            NIVision.imaqDrawLineOnImage(image, image, NIVision.DrawMode.DRAW_VALUE, new NIVision.Point(STARTX, STARTY),
                    new NIVision.Point(ENDX, ENDY), 130.0f);
//			NIVision.imaqDrawShapeOnImage(image, image, new Rect(STARTX, STARTY, 30, 30), DrawMode.DRAW_VALUE,
//					ShapeMode.SHAPE_RECT, 125f);
        }
        CameraServer.getInstance().setImage(image);
    }


//	public double getDistance() {// send next camera image
//		netTable = NetworkTable.getTable("GRIP/myContoursReport");
//		Double[] height = netTable.getNumberArray("height", new Double[0]);
//		halfCamHtFt = (height[0] * halfCamHtFt) / halfCamHtPx;
//		return distance = halfCamHtFt / (Math.tan(Math.toDegrees(cameraAngleWidth)));
//	}

}
