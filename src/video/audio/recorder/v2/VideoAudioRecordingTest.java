package video.audio.recorder.v2;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class VideoAudioRecordingTest {

	private AudioRecorder recorder = new AudioRecorder();
	private VideoRecorder recorderVideo = new VideoRecorder();
	private Object arbitrator = new Object();

	@Test
	public void captureAudio() throws Exception {
		
		recorder.record();
		recorder.play();
		assertTrue("Shouldn't be empty",recorder.audio.length>1);
	}
	
	@Test
	public void captureVideo() throws Exception {
		
		recorderVideo.record();
		recorderVideo.play();
		assertTrue("Shouldn`t be empty",recorderVideo.video.size()>1);
	}
	
	@Test
	public void captureAudioAndVideo() throws Exception {
	
		
		new Thread("Video recorder"){

			@Override
			public void run() {
				
			synchronized (arbitrator) {
				try {
					arbitrator.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			recorderVideo.record();
				
			}
			
		}.start();
		
		new Thread("Audio recorder"){

			@Override
			public void run() {
				
				try {
					synchronized (arbitrator) {
						arbitrator.notify();	
					}
					
					recorder.record();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}.start();
	
		TimeUnit.MINUTES.sleep(1);
		new Thread("Video player"){

			@Override
			public void run() {
				synchronized (arbitrator) {
					try {
						arbitrator.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				recorderVideo.play();	
				
			}
			
		}.start();
	
		new Thread("Audio player"){

			@Override
			public void run() {
				
				try {
					synchronized (arbitrator) {
						arbitrator.notify();	
					}
					recorder.play();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}.start();
		
		TimeUnit.MINUTES.sleep(1);
	}
}
