package video.audio.recorder.v2;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamResolution;
import com.sun.corba.se.impl.orbutil.RepositoryIdUtility;

import video.audio.recorder.v2.video.Screen;

public class VideoPlayerRecorder extends VideoRecorder {

	private final int frameCount;

	 

	public VideoPlayerRecorder() {
		this(1000);
	}

	public VideoPlayerRecorder(int frameCount) {

		this.frameCount = frameCount;
		
	}
	
	public void play(AudioPlayer audioRecorder) {
		Screen screen = new Screen();
		try {
			System.out.println("Play video");
			long startTime = System.currentTimeMillis();
			int counter = 0;
			
			BufferedImage image;
			for (int i = 0; (image=getImage(i))!=null;i++) {
				screen.setImage(image);
				counter += 1;
				audioRecorder.play(0!=getTime(i+1)?getTime(i+1):getTime(i));
			}
			System.out.println("Frames " + counter + " was played in "
					+ TimeUnit.SECONDS.toSeconds(System.currentTimeMillis() - startTime));
		} finally {
			screen.off();
		}

	}


	public void record() {
		for (int i = 0; i < frameCount; i++) {
			super.record();
		}		
	}

}
