package video.audio.recorder.v2;

import static org.junit.Assert.*;

import org.junit.Test;

import parallel.processing.sample.StartFlag;

public class VideoAudioRecordingTest {

	private AudioRecorder audioSource = new AudioRecorder();
	private AudioPlayer audio = new AudioPlayer(audioSource);
	
	private VideoRecorder videoSource = new VideoRecorder();
	private VideoPlayer video = new VideoPlayer(videoSource);
	
	
	private Thread videoStreamRecorder = new Thread("videoRecorder") {
		@Override
		public void run() {
			
			try {
				System.out.println("Go!");//help to know to start counting.
				video.record(110);
			} finally {
				videoSource.deactivateCam();
			}
		}
	};

	private Thread audioStreamRecorder =  new Thread("audioRecorder") {

		@Override
		public void run() {

			try {
				audioSource.record();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	};

	
	@Test
	public void captureAudio() throws Exception {

		audioSource.record();
		audio.play(System.currentTimeMillis());
		
	}

	@Test
	public void captureVideo() throws Exception {

		video.record(110);
		video.play(new AudioPlayer(null){

			@Override
			public void play(long timeBorder) {
				// TODO Auto-generated method stub
				assertTrue(timeBorder!=0);
			}
			
		});
		
	}

	@Test
	public void captureAudioAndVideo() throws Exception {
		videoSource.activateCam();
		videoStreamRecorder.start();
		audioStreamRecorder.start();

		videoStreamRecorder.join();
		audioStreamRecorder.join();
		
		video.play(audio);

	}
}
