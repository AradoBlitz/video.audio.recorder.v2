package video.audio.recorder.v2.player;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import video.audio.recorder.v2.video.Screen;

public class VideoStreamPlayer extends Thread {

	private int counter = 0;
	private List<BufferedImage> imagesToPlay = new ArrayList<>();
	public boolean stop;

	@Override
	public void run() {
		Screen screen = new Screen();
		try {
			while (!stop) {
				System.out.println("Play video");
				long startTime = System.currentTimeMillis();
				
				for (int i = counter; i < imagesToPlay.size(); i++) {

					screen.setImage(imagesToPlay.get(i));
					
					try {
						TimeUnit.MILLISECONDS.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println("Frames " + imagesToPlay.size() + " was played in "
						+ TimeUnit.SECONDS.toSeconds(System.currentTimeMillis() - startTime));
			}
		} finally {
			screen.off();
		}

	}

	public void play(List<BufferedImage> imagesToPlay) {
		this.imagesToPlay = imagesToPlay;
		this.counter = 0;
	}

}
