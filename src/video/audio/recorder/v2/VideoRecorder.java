package video.audio.recorder.v2;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamResolution;

import video.audio.recorder.v2.video.Screen;

public class VideoRecorder {

	private Webcam webcam = Webcam.getDefault();

	public List video = Collections.synchronizedList(new ArrayList<>());

	public VideoRecorder() {
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		WebcamListener camListener = new WebcamListener() {

			@Override
			public void webcamOpen(WebcamEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println(arg0.toString() + "webcamOpen");
			}

			@Override
			public void webcamImageObtained(WebcamEvent arg0) {

			}

			@Override
			public void webcamDisposed(WebcamEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println(arg0.toString() + "webcamDisposed");
			}

			@Override
			public void webcamClosed(WebcamEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println(arg0.toString() + "wbcamClosed");
			}
		};
		webcam.addWebcamListener(camListener);
	}
	Screen screen = new Screen();
	public void play() {
		
		try {
			System.out.println("Play video");
			for (Object item : video) {
				BufferedImage image = (BufferedImage) item;
				screen.setImage(image);
				try {
					TimeUnit.MILLISECONDS.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} finally {
			screen.off();
		}

	}

	public void record() {
		webcam.open();
		try {
			for (int i = 0; i < 1000; i++) {
				BufferedImage image = webcam.getImage();
				video.add(image);
			}

		} finally {
			webcam.close();
		}

	}

}
