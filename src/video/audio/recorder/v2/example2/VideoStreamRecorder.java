package video.audio.recorder.v2.example2;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import video.audio.recorder.v2.VideoRecorder;

public class VideoStreamRecorder extends Thread {

	private VideoRecorder recorderVideo;

	public VideoStreamRecorder(VideoRecorder recorderVideo) {
		this.recorderVideo = recorderVideo;
		// TODO Auto-generated constructor stub
	}
	
	

	@Override
	public void run() {
		while (true) {
			recorderVideo.record();
		}
	}



	public List<BufferedImage> getCollectedImages() {
		List<BufferedImage> collectedImages = recorderVideo.video;
		recorderVideo.video = new ArrayList<>();
		return collectedImages;
	}

}
