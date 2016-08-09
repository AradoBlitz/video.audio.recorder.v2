package video.audio.recorder.v2.example2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import video.audio.recorder.v2.AudioRecorder;

public class AudioStreamRecorder extends Thread {

	private AudioRecorder recorder;
	public boolean stop;
	private List<byte[]> audioList = new ArrayList<>();

	public AudioStreamRecorder(AudioRecorder recorder) {
		this.recorder = recorder;
		// TODO Auto-generated constructor stub
	}

	Object lock = new Object();

	public List<byte[]> getCollectedSound() {
		List<byte[]> copy = new ArrayList<>(audioList);
		synchronized (lock) {
			audioList.clear();
		}
		return copy;
	}

	@Override
	public void run() {
		while (!stop) {
			try {

				recorder.record();
				synchronized (lock) {
					audioList.add(recorder.audio);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
