package video.audio.recorder.v2.video;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class Screen extends JPanel {

	private Image image;

	private JFrame frame = new JFrame();
	
	public Screen(){
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);		
		frame.add(this);	
		frame.setSize(640, 480);
		frame.setVisible(true);
	}

	public void setImage(Image image) {
		this.image = image;
		repaint();
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		g.drawImage(image, 0, 0, null);
	}

	public void off() {
		frame.setVisible(false);		
	}
}
