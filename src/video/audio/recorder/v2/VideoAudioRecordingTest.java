package video.audio.recorder.v2;

import static org.junit.Assert.*;

import org.junit.Test;

public class VideoAudioRecordingTest {

	private AudioRecorder recorder = new AudioRecorder();

	@Test
	public void captureAudio() throws Exception {
		
		recorder.record();
		recorder.play();
		assertTrue("Shouldn't be empty",recorder.audio.length>1);
	}
}
