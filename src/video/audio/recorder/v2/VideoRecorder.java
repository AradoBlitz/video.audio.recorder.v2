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
import com.sun.swing.internal.plaf.synth.resources.synth;

import video.audio.recorder.v2.video.Screen;

public class VideoRecorder {

	private final int frameCount;
	
	private Webcam webcam = Webcam.getDefault();

	public List<BufferedImage> video = new ArrayList<>();

	public VideoRecorder(){
		this(1000);
	} 
	
	public VideoRecorder(int frameCount) {
		
		this.frameCount = frameCount;
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

	public synchronized void play() {
		Screen screen = new Screen();
		try {
			System.out.println("Play video");
			long startTime = System.currentTimeMillis();
			int counter = 0;
			for (BufferedImage image : video) {
				screen.setImage(image);
				counter += 1;
				try {
					TimeUnit.MILLISECONDS.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("Frames " + counter + " was played in "
					+ TimeUnit.SECONDS.toSeconds(System.currentTimeMillis() - startTime));
		} finally {
			screen.off();
		}

	}

	public void record() {
		webcam.open();
		try {
			for (int i = 0; i < frameCount; i++) {
				BufferedImage image = webcam.getImage();
				synchronized (this) {
					video.add(image);
				}
			}
			System.out.println("Recorded frames: " + video.size());
		} finally {
			webcam.close();
		}

	}
	
	public static void main(String[] args) {
		VideoRecorder videoRecorder = new VideoRecorder();
		while (true) {
			videoRecorder.record();
			videoRecorder.play();
		}
	}

}
