package video.audio.recorder.v2;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamResolution;
import video.audio.recorder.v2.video.Screen;

public class VideoRecorder {

	private final int frameCount;

	private Webcam webcam = Webcam.getDefault();

	public List<BufferedImage> video = new ArrayList<>();

	private List<Long> time = new ArrayList<>(); 

	public VideoRecorder() {
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
	
	public void play(AudioPlayer audioRecorder) {
		Screen screen = new Screen();
		try {
			System.out.println("Play video");
			long startTime = System.currentTimeMillis();
			int counter = 0;
			
			for (int i = 0; i<video.size();i++) {
				screen.setImage(video.get(i));
				counter += 1;
				audioRecorder.play(i+1<time.size()?time.get(i+1):i);
			}
			System.out.println("Frames " + counter + " was played in "
					+ TimeUnit.SECONDS.toSeconds(System.currentTimeMillis() - startTime));
		} finally {
			screen.off();
		}

	}

	public void record() {
		for (int i = 0; i < frameCount; i++) {
			BufferedImage image = webcam.getImage();
			synchronized (this) {
				video.add(image);
				time.add(System.currentTimeMillis());
			}
		}
		System.out.println("Recorded frames: " + video.size());
	}

	public void activateCam() {
		webcam.open();
	}

	public void deactivateCam() {
		webcam.close();
	}

	public void clearBuffer() {
		video.clear();
	}

}
