package video.audio.recorder.v2;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class VideoAudioRecordingTest {

	private AudioRecorder recorder = new AudioRecorder();
	private VideoRecorder recorderVideo = new VideoRecorder();
	
	private volatile boolean readyToAudioRecord = false;
	private volatile boolean readyToVideoRecord = false;
	
	private volatile boolean readyToAudioPlay = false;
	private volatile boolean readyToVideoPlay = false;
	
	private Thread videoStreamRecorder = new Thread("Video recorder") {

		@Override
		public void run() {

			readyToVideoRecord = true;
			while (!readyToVideoRecord && !readyToAudioRecord) {
				System.out.println("Wait for audio recorder...");
			}
			recorderVideo.record();
			
			readyToVideoPlay = true;
			while (!readyToVideoPlay && !readyToAudioPlay) {
				System.out.println("Wait for audio player...");
			}
			recorderVideo.play();
		}

	};

	private Thread audioStreamRecorder = new Thread("Audio recorder") {

		@Override
		public void run() {

			try {
				readyToAudioRecord= true;
				while (!readyToVideoRecord && !readyToAudioRecord) {
					System.out.println("Wait for video recorder...");
				}
				recorder.record();
				readyToAudioPlay = true;
				while (!readyToVideoPlay && !readyToAudioPlay) {
					System.out.println("Wait for video player...");
				}

				recorder.play();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	};

	
	@Test
	public void captureAudio() throws Exception {

		recorder.record();
		recorder.play();
		assertTrue("Shouldn't be empty", recorder.audio.length > 1);
	}

	@Test
	public void captureVideo() throws Exception {

		recorderVideo.record();
		recorderVideo.play();
		assertTrue("Shouldn`t be empty", recorderVideo.video.size() > 1);
	}

	@Test
	public void captureAudioAndVideo() throws Exception {

		videoStreamRecorder.start();
		audioStreamRecorder.start();

		videoStreamRecorder.join();
		audioStreamRecorder.join();

	}
}
