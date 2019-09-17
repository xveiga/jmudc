package mudc.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.VolatileImage;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class TabPanelAnimator extends JPanel {

	private JPanel[] panels = null;
	private boolean enableAnimations = false;
	private boolean imgRender = true;
	private int panelWidth;
	private int panelHeight;
	private int currentPosition = 0;
	private Thread animationThread = new Thread();
	private Runnable onAnimationFinish = null;
	private boolean isAnimationRunning = false;
	private VolatileImage[] images = null;
	private int[] imagePositions = null;

	public TabPanelAnimator(JPanel p1, JPanel p2, JPanel p3, JPanel p4, JPanel p5) {

		setLayout(null);

		imagePositions = new int[5];
		images = new VolatileImage[5];
		panels = new JPanel[5];
		panels[0] = p1;
		panels[1] = p2;
		panels[2] = p3;
		panels[3] = p4;
		panels[4] = p5;

		for (int i=0; i<5; i++) {
			add(panels[i]);
		}
	}

	public void onResize(int newWidth, int newHeight) {
		panelWidth = newWidth;
		panelHeight = newHeight;
		setSizes(panelWidth * currentPosition);
	}

	public void setAnimationEnabled(boolean status) {
		enableAnimations = status;
	}

	public boolean getAnimationEnabled() {
		return enableAnimations;
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public void setOnFinishRunnable(Runnable r) {
		onAnimationFinish = r;
	}

	public void slideTo(int index, int stepCount, int timeBetweenSteps, float acceleration) {
		if (enableAnimations && index != currentPosition) { // If animations are enabled and the target position is
															// different from current.
			System.out.println(
					"[GUI Slide Animator] Started animation from position " + currentPosition + " to " + index + ".");
			if (!animationThread.isAlive()) {
				animationThread = new Thread() {
					@Override
					public void run() {
						isAnimationRunning = true;
						double stepIncrement = (Math.PI / 2 / stepCount); // Calculate the step size
						double animationState = 0.0f; // Start at 0, ends at 1. Constant rate.
						double position = 0.0f; // Position after "acceleration" function (sine).
						while (animationState < Math.PI / 2) { // Sine starts "accelerating" at 0 degrees and stops
																// "decelerating" at 90 degrees.
							animationState += stepIncrement; // Increment state
							position = Math.pow(Math.sin(animationState), acceleration); // Calculate animation position
							final int effectivePosition = (int) Math
									.round(panelWidth * (currentPosition + (index - currentPosition) * position)); // Scale
																													// animation
																													// function
																													// to
																													// the
																													// number
																													// of
																													// slides
																													// to
																													// perform,
																													// and
																													// to
																													// current
							if (imgRender) {
								setImagePos(effectivePosition);
							}
							else {
								SwingUtilities.invokeLater(new Runnable() {
									@Override
									public void run() {
										setPositions(effectivePosition);
									}
								});
							}
							try {
								Thread.sleep(timeBetweenSteps);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						currentPosition = index;
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								isAnimationRunning = false;
								if (!imgRender) {
									setPositions(panelWidth * currentPosition); // Set final position (if something got stuck).
								} else {
									freeImages();
								}
							}
						});
						System.out.println("[GUI Slide Animator] Animation finished.");
						if (onAnimationFinish != null) {
							onAnimationFinish.run();
						}
					}
				};
				animationThread.start();
				initImages();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						setPositions(panelWidth * index);
					}
				});
			}
		} else {
			currentPosition = index;
			setPositions(panelWidth * currentPosition); // Set final position directly.
			if (onAnimationFinish != null) {
				onAnimationFinish.run();
			}
		}
	}

	private void setPositions(int slidePixels) { //TODO: Slide only visible panels
		for (int i=0; i<panels.length; i++) {
			panels[i].setLocation(-slidePixels + panelWidth * i, 0);
		}
		repaint();
		/*panels[0].setLocation(-slidePixels, 0);
		panels[1].setLocation(-slidePixels + panelWidth, 0);
		panels[2].setLocation(-slidePixels + panelWidth * 2, 0);
		panels[3].setLocation(-slidePixels + panelWidth * 3, 0);
		panels[4].setLocation(-slidePixels + panelWidth * 4, 0);*/
	}

	private void setSizes(int slidePixels) {
		for (int i=0; i<panels.length; i++) {
			panels[i].setBounds(-slidePixels + panelWidth * i, 0, panelWidth, panelHeight);
		}
		/*panels[0].setBounds(-slidePixels, 0, panelWidth, panelHeight);
		panels[1].setBounds(-slidePixels + panelWidth, 0, panelWidth, panelHeight);
		panels[2].setBounds(-slidePixels + panelWidth * 2, 0, panelWidth, panelHeight);
		panels[3].setBounds(-slidePixels + panelWidth * 3, 0, panelWidth, panelHeight);
		panels[4].setBounds(-slidePixels + panelWidth * 4, 0, panelWidth, panelHeight);*/
	}
	
	private void initImages() {
		// Draw each panel into an image
		if (imgRender) {
			for (int i=0; i<panels.length; i++) {
				images[i] = createVolatileImage(getWidth(),getHeight());
				images[i].setAccelerationPriority(0.75f);
				panels[i].paint(images[i].getGraphics());
			}
		}
	}
	
	private void setImagePos(int slidePixels) {
			for (int i=0; i<panels.length; i++) {
				imagePositions[i] = -slidePixels + panelWidth * i;
			}
			repaint();
	}
	
	private void freeImages() {
		for (int i=0; i<panels.length; i++) {
			images[i].flush();
		}
	}
	
	@Override
	public void paint(Graphics g) {
		if (imgRender && isAnimationRunning) {
			Graphics2D g2d = (Graphics2D) g;
			for (int i=0; i<images.length; i++) {
				g2d.drawImage(images[i], imagePositions[i], 0, this);
			}
		} else {
			super.paint(g);
		}
	}
	
}
