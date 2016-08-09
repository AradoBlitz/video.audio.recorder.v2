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
import video.audio.recorder.v2.player.AudioStremPlayer;
import video.audio.recorder.v2.player.VideoStreamPlayer;

public class Example2 {

	private AudioRecorder recorder = new AudioRecorder(50);
	private VideoRecorder recorderVideo = new VideoRecorder(50);
	private List<BufferedImage> collectedImages = new ArrayList<>();
	private List<byte[]> collectedSoundBytes = new ArrayList<>();
	private VideoStreamRecorder videoStreamRecorder = new VideoStreamRecorder(recorderVideo);
	private AudioStreamRecorder audioStreamRecorder = new AudioStreamRecorder(recorder);
	private VideoStreamPlayer videoStreamPlayer = new VideoStreamPlayer();
	private AudioStremPlayer audioStreamPlayer = new AudioStremPlayer(recorder);

	@Test
	public void captureAudioAndVideo() throws Exception {

		videoStreamRecorder.start();
		audioStreamRecorder.start();

		TimeUnit.SECONDS.sleep(222);

		for (int i = 0; i < 1; i++) {
			System.out.println("Iteration #"+ i);
			collectedImages.addAll(videoStreamRecorder.getCollectedImages());
			collectedSoundBytes.addAll(audioStreamRecorder.getCollectedSound());
		}

	/*	assertEquals(1000, collectedSoundBytes.size());
		assertEquals(1100, collectedImages.size());*/
		videoStreamRecorder.stop=true;
		audioStreamRecorder.stop=true;
		
		videoStreamRecorder.interrupt();
		audioStreamRecorder.interrupt();
		
		videoStreamRecorder.join();
		audioStreamRecorder.join(); 
		
		videoStreamPlayer.start();
		audioStreamPlayer.start();
		synchronized (recorder) {
			recorder.wait();	
		}
		TimeUnit.SECONDS.sleep(30);
		
		List<BufferedImage> imagesToPlay = new ArrayList<>();
		for (int i = 0; i < collectedImages.size(); i++)
			imagesToPlay.add(collectedImages.get(i));

		List<byte[]> audioBytesToPlay = new ArrayList<>();
		for (int i = 0; i < collectedSoundBytes.size(); i++)
			audioBytesToPlay.add(collectedSoundBytes.get(i));
		
		audioStreamPlayer.play(audioBytesToPlay);
		videoStreamPlayer.play(imagesToPlay);
		
		
		TimeUnit.SECONDS.sleep(222);
		
		videoStreamPlayer.stop=true;
		audioStreamPlayer.stop=true;
		
		videoStreamPlayer.join();
		audioStreamPlayer.join();
	}

	@Test
	public void countingSample() throws Exception {
		int count = 4;
		assertEquals(0, count - 4);
		count += 5;
		assertEquals(5, count - 4);
		count += 5;
		assertEquals(10, count - 4);

	}
}
