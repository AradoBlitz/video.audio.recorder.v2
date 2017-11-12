package video.audio.recorder.v2;

import static org.junit.Assert.*;

import org.junit.Test;

import parallel.processing.sample.StartFlag;

public class VideoAudioRecordingTest {

	private AudioRecorder audioSource = new AudioRecorder();
	private AudioPlayer audio = new AudioPlayer(audioSource);
	private VideoPlayerRecorder video = new VideoPlayerRecorder(110);
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

		video.record();
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
		video.activateCam();
		videoStreamRecorder.start();
		audioStreamRecorder.start();

		videoStreamRecorder.join();
		audioStreamRecorder.join();
		
		video.play(audio);

	}
}
