package video.audio.recorder.v2.example2;

import java.util.Collection;
import java.util.List;

import video.audio.recorder.v2.AudioRecorder;

public class AudioStreamRecorder extends Thread {

	private AudioRecorder recorder;
	public boolean stop;

	public AudioStreamRecorder(AudioRecorder recorder) {
		this.recorder = recorder;
		// TODO Auto-generated constructor stub
	}

	public byte[] getCollectedSound() {
		return recorder.audio;
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				recorder.record();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
