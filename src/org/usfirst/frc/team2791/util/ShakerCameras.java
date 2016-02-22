package org.usfirst.frc.team2791.util;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Rect;
import com.ni.vision.NIVision.ShapeMode;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

//public class ShakerCameras extends AxisCamera {
public class ShakerCameras {
	private final double cameraAngleWidth = 47 / 2;
	private final int STARTX = 320;
	private final int STARTY = 0;
	private final int ENDX = 320;
	private final int ENDY = 480;
	private int cameraPixelHeight = 240;
	private double halfCamHtFt;
	private int halfCamHtPx;
	private double targetHeightFt;
	private double targetHeightPx;
	private double distance;
	private NetworkTable netTable;
	private NIVision.Image image;
	private int currentSession;
	private int sessionOne;
	private int sessionTwo;
	public ShakerCameras() {
//		try {
//
//			image = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
//			switchCameras("cam1");
//			// secondSession = NIVision.IMAQdxOpenCamera("cam2",
//			// NIVision.IMAQdxCameraControlMode.CameraControlModeController);
//			// NIVision.IMAQdxConfigureGrab(this.secondSession);
//
//			sessionOne = NIVision.IMAQdxOpenCamera("cam1",
//					NIVision.IMAQdxCameraControlMode.CameraControlModeController);
//
//			sessionTwo = NIVision.IMAQdxOpenCamera("cam2",
//					NIVision.IMAQdxCameraControlMode.CameraControlModeController);
//			halfCamHtPx = 120;
//			targetHeightFt = 1.16667;
//		} catch (Error e) {
//
//		}
	}

	public void update(boolean drawOnImage) {
//		NIVision.IMAQdxGrab(currentSession, image, 1);
//		if (drawOnImage) {
//			NIVision.imaqDrawLineOnImage(image, image, NIVision.DrawMode.DRAW_VALUE, new NIVision.Point(STARTX, STARTY),
//					new NIVision.Point(ENDX, ENDY), 130.0f);
//			NIVision.imaqDrawShapeOnImage(image, image, new Rect(STARTX, STARTY, 30, 30), DrawMode.DRAW_VALUE,
//					ShapeMode.SHAPE_RECT, 125f);
//		}
//		CameraServer.getInstance().setImage(image);
//		// CameraServer.getInstance().setImage(image);

	}

	public void switchCameras(String cameraName) {
//		
//		currentSession = NIVision.IMAQdxOpenCamera(cameraName,
//				NIVision.IMAQdxCameraControlMode.CameraControlModeController);
//		NIVision.IMAQdxConfigureGrab(currentSession);
	}

	public double getDistance() {// send next camera image
		netTable = NetworkTable.getTable("GRIP/myContoursReport");
		Double[] height = netTable.getNumberArray("height", new Double[0]);
		halfCamHtFt = (height[0] * halfCamHtFt) / halfCamHtPx;
		return distance = halfCamHtFt / (Math.tan(Math.toDegrees(cameraAngleWidth)));
	}

}
