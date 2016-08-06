package video.audio.recorder.v2.player;

import java.util.ArrayList;
import java.util.List;

import video.audio.recorder.v2.AudioRecorder;

public class AudioStremPlayer extends Thread {

	private List<byte[]> audioBytesToPlay = new ArrayList<>();
	private int counter = 0;
	private AudioRecorder recorder;
	public boolean stop;

	public AudioStremPlayer(AudioRecorder recorder) {
		this.recorder = recorder;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		synchronized (recorder) {
			recorder.notify();	
		}
		try {
		System.out.println("Audio player started.");
		while (!stop) {
			System.out.println("Audio player started. In loop...");
			for (int i = counter; i < audioBytesToPlay.size(); i++){
				System.out.println("Audio player playing...");
				recorder.play(audioBytesToPlay.get(i));
			}
		}
		System.out.println("Audio player stopedS.");
		} catch (Exception e) {
			System.out.println("Audio player error: " + e);
		}
	}

	public void play(List<byte[]> audioBytesToPlay) {
		this.audioBytesToPlay = audioBytesToPlay;
		this.counter = 0;

	}

}
