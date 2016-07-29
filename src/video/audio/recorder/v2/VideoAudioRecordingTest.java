package video.audio.recorder.v2;

import static org.junit.Assert.*;

import org.junit.Test;

public class VideoAudioRecordingTest {

	private AudioRecorder recorder = new AudioRecorder();
	private VideoRecorder recorderVideo = new VideoRecorder();

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
}
