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

public class VideoPlayer {

	private final VideoRecorder source;	 

	public VideoPlayer(VideoRecorder source) {
		this.source = source;		
	}
	
	public void play(AudioPlayer audioRecorder) {
		Screen screen = new Screen();
		try {
			System.out.println("Play video");
			long startTime = System.currentTimeMillis();
			int counter = 0;
			
			BufferedImage image;
			for (int i = 0; (image=source.getImage(i))!=null;i++) {
				screen.setImage(image);
				counter += 1;
				audioRecorder.play(0!=source.getTime(i+1)?source.getTime(i+1):source.getTime(i));
			}
			System.out.println("Frames " + counter + " was played in "
					+ TimeUnit.SECONDS.toSeconds(System.currentTimeMillis() - startTime));
		} finally {
			screen.off();
		}

	}


	public void record(int frameCount) {
		for (int i = 0; i < frameCount; i++) {
			source.record();
		}		
	}

}
