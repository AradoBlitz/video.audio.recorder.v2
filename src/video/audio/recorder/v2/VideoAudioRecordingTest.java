package video.audio.recorder.v2;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VideoAudioRecordingTest {

	private AudioRecorder audioSource = new AudioRecorder();
	private AudioPlayer audio = new AudioPlayer(audioSource);
	
	private VideoRecorder videoSource = new VideoRecorder();
	private VideoPlayer video = new VideoPlayer(videoSource);

	
	@Test
	public void captureAudio() throws Exception {
	
		audio.record();
		TimeUnit.SECONDS.sleep(15);
		audio.stop();
		audio.play(System.currentTimeMillis());
		
	}

	@Test
	public void captureVideo() throws Exception {

		video.record();
		TimeUnit.SECONDS.sleep(15);
		video.stop();
		video.play(new AudioPlayer(null){

			@Override
			public void play(long timeBorder) {
				// TODO Auto-generated method stub
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		
	}
	
	@Before
	public void startCam() throws Exception {
					
		videoSource.cameraOn();
		
		audioSource.micOn();
	
		
	}

	@Test
	public void captureAudioAndVideo() throws Exception {
	
		video.record();
		audio.record();
		TimeUnit.SECONDS.sleep(25);
		video.stop();
		audio.stop();
		
		video.play(audio);

	}
	
	@After
	public void stopCam() throws Exception{
		
		videoSource.cameraOff();
		audioSource.micOff();
	}
}
