package video.audio.recorder.v2;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import video.audio.recorder.v2.example2.AudioStreamRecorder;
import video.audio.recorder.v2.example2.VideoStreamRecorder;

public class Example2 {

	private AudioRecorder recorder = new AudioRecorder(50);
	private VideoRecorder recorderVideo = new VideoRecorder(50);
	private List<BufferedImage> collectedImages = new ArrayList<>();
	private List<byte[]> collectedSoundBytes = new ArrayList<>();
	private VideoStreamRecorder videoStreamRecorder = new VideoStreamRecorder(recorderVideo);
	private AudioStreamRecorder audioStreamRecorder = new AudioStreamRecorder(recorder);

	@Test
	public void captureAudioAndVideo() throws Exception {

		videoStreamRecorder.start();
		audioStreamRecorder.start();

		TimeUnit.SECONDS.sleep(222);

		for (int i = 0; i < 1000; i++) {
			collectedImages.addAll(videoStreamRecorder.getCollectedImages());
			collectedSoundBytes.add(audioStreamRecorder.getCollectedSound());
		}

		assertEquals(1000, collectedSoundBytes.size());
		assertEquals(1100, collectedImages.size());

	}
	
	@Test
	public void countingSample() throws Exception {
		int count=4;
		assertEquals(0, count-4);
		count+=5;
		assertEquals(5, count-4);
		count+=5;
		assertEquals(10, count-4);
		
	}
}
