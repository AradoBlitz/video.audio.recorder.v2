package video.audio.recorder.v2;

import parallel.processing.sample.StartFlag;

public class AudioVideoRecorderV2Impl1 {

	public static void main(String[] args) {
		StartFlag startFlag = new StartFlag();
		AudioRecorder audio = new AudioRecorder(2000); //increase time recording 1000
		VideoRecorder video = new VideoRecorder(110); //50

		Thread audioRecorder = new Thread("audioRecorder") {

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
		
		video.activateCam();
		Thread videoRecorder = new Thread("videoRecorder") {
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

		audioRecorder.start();
		videoRecorder.start();

		try {
			audioRecorder.join();
			videoRecorder.join();

		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Thread videoPlayer = new Thread("video player") {
			@Override
			public void run() {

				video.play(audio);
			}
		};

		videoPlayer.start();

		try {
			videoPlayer.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
