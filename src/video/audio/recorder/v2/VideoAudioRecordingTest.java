package video.audio.recorder.v2;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import parallel.processing.sample.StartFlag;

public class VideoAudioRecordingTest {

	private AudioRecorder audio = new AudioRecorder(2000);
	private VideoRecorder video = new VideoRecorder(110);
	private StartFlag startFlag = new StartFlag();
	
	private Thread videoStreamRecorder = new Thread("videoRecorder") {
		@Override
		public void run() {
			
			try {
				startFlag.syncLine();
				System.out.println("Go!");//help to know to start counting.
				video.record();
			} finally {
				video.deactivateCam();
			}
		}
	};

	private Thread audioStreamRecorder =  new Thread("audioRecorder") {

		@Override
		public void run() {

			try {
				startFlag.syncLine();
				audio.record();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	};

	
	@Test
	public void captureAudio() throws Exception {

		audio.record();
		audio.play(System.currentTimeMillis());
		assertTrue("Shouldn't be empty", audio.audio.length > 1);
	}

	@Test
	public void captureVideo() throws Exception {

		video.record();
		video.play(new AudioRecorder(){

			@Override
			public void play(Long timeSlot) {
				// TODO Auto-generated method stub
				assertTrue(timeSlot!=0);
			}
			
		});
		assertTrue("Shouldn`t be empty", video.video.size() > 1);
	}

	@Test
	public void captureAudioAndVideo() throws Exception {
		video.activateCam();
		videoStreamRecorder.start();
		audioStreamRecorder.start();

		videoStreamRecorder.join();
		audioStreamRecorder.join();
		
		video.play(audio);

	}
}
