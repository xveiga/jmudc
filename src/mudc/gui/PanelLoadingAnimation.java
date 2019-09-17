package mudc.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class PanelLoadingAnimation extends JPanel {
	
	private static final long serialVersionUID = -5846788210911146615L;

	private Thread animationThread = new Thread();
	
	private static final int timeBetweenSteps = 16;
	private static final int nballs = 10;
	private static final int offsetdiv = 20;
	
	private double position = 0;
	private int[] pixelPositions = null;
	private int ballSize = 0;
	private int[] ballHeights = null;
	private int[] ballWidths = null;
	private boolean run = false;

	public PanelLoadingAnimation() {
		setLayout(null);
		ComponentAdapter componentAdapter = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				ballSize = getWidth()/nballs/2;
			}
		};
		addComponentListener(componentAdapter);
		pixelPositions = new int[nballs];
		ballHeights = new int[nballs];
		ballWidths = new int[nballs];
	}
	
	public void setAnimationEnabled(boolean state) {
		if (state) {
			if (!animationThread.isAlive()) {
				animationThread = new Thread () {
					public void run () {
						run = true;
						System.out.println("[Loading animation] Animation started.");
						while (run) {
							calculateAnimation();
							SwingUtilities.invokeLater(new Runnable () {
								public void run() {
									repaint();
								}
							});
							try {
								Thread.sleep(timeBetweenSteps);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}	
					}
				};
				animationThread.start();
			}
		}
		else {
			run = false;
			System.out.println("[Loading animation] Animation stopped.");
		}
	}
	
	public boolean getAnimationEnabled() {
		return run;
	}
	
	private void calculateAnimation() {
		position += Math.PI/96;
		if (position >= Math.PI) {
			position = 0;
		}
		for (int i=0; i<nballs; i++) {
			pixelPositions[i] = (int) Math.round(Math.abs(Math.sin(position+i*Math.PI/offsetdiv))*getHeight());
			if (pixelPositions[i] <= ballSize) {
				pixelPositions[i] = (pixelPositions[i]+ballSize)/2;
				ballHeights[i] = pixelPositions[i];
				ballWidths[i] = ballSize*2-pixelPositions[i];
			}
			else {
				ballHeights[i] = ballSize;
				ballWidths[i] = ballSize;
			}
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(new Color(255, 255, 255, 255));
		g2d.fillRect(0, 0, getWidth(), getHeight());
		g2d.setColor(new Color(255, 80, 32, 255));
		for (int i=0; i<nballs; i++) {
			g2d.fillOval(getWidth()*(i+1)/(nballs+1)-ballWidths[i]/2, getHeight()-pixelPositions[i], ballWidths[i], ballHeights[i]);
		}
	}

}
