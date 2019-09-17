package mudc.gui;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class AssistantSlideshowAnimator extends JPanel {
	
	private static final long serialVersionUID = 6574637370062927721L;
	
	private JPanel panel0;
	private JPanel panel1;
	private JPanel panel2;
	private int panelWidth;
	private int panelHeight;
	private int panelStatus = 1;
	private int currentPanel = 0;
	private JPanel[] slideshow;
	private Thread animationThread = new Thread();
	
	/*
	 * How it works:
	 * Three JPanels are created. They are positioned so there is always one on the middle (visible) and another two on the left.
	 * The left panel always contains the previous slide, and the right panel contains the next slide.
	 * The panels are reorganized dynamically so this is always true.
	 * 
	 * There are three posible statuses (indicated by panelStatus):
	 * 
	 * Status 0:
	 * 
	 *  |  Panel 2 |  Panel 0  |  Panel 1 |
	 *  | (Hidden) | (Visible) | (Hidden) |
	 *  
	 * Status 1:
	 * 
	 *  |  Panel 0 |  Panel 1  |  Panel 2 |
	 *  | (Hidden) | (Visible) | (Hidden) |
	 *  
	 * Status 2:
	 * 
	 *  |  Panel 1 |  Panel 2  |  Panel 0 |
	 *  | (Hidden) | (Visible) | (Hidden) |
	 *  
	 *  When a right slide is performed, the panel to the right slides to the center and the center panel slides to the left.
	 *  The panel on the left is "transported" to the right side, and the contents are updated to the next slide.
	 *  When the latest slide is reached, the panel to the right is empty, and the sliding action is locked.
	 *  
	 *  The same applies to the left slide, but in reverse. When the first slide is reached, the panel on the left is empty.
	 *  
	 */

	public AssistantSlideshowAnimator(JPanel[] slides) {
		
		slideshow = slides;
		
		/*for (int i=0; i<slideshow.length; i++) {
			if (slideshow[i] == null) {
				JPanel placeholderPanel = new JPanel();
				placeholderPanel.setBackground(Color.PINK);
				slideshow[i] = placeholderPanel;
			}
		}*/
		
		setLayout(null);
		
		panel0 = new JPanel(); // Placeholder panel
		panel1 = slideshow[0]; // First slide
		panel2 = slideshow[1]; // Second slide
		
		add(panel0);
		add(panel1);
		add(panel2);
	}
	
	public void onResize(int newWidth, int newHeight) {
		panelWidth = newWidth;
		panelHeight = newHeight;
		
		switch (panelStatus) {
		case 0:
			SwingUtilities.invokeLater(new Runnable () {
				public void run() {
					panel2.setBounds(-panelWidth, 0, panelWidth, panelHeight);
					panel0.setBounds(0, 0, panelWidth, panelHeight);
					panel1.setBounds(panelWidth, 0, panelWidth, panelHeight);
					panel0.revalidate();
				}
			});
			break;
		case 1:
			SwingUtilities.invokeLater(new Runnable () {
				public void run() {
					panel0.setBounds(-panelWidth, 0, panelWidth, panelHeight);
					panel1.setBounds(0, 0, panelWidth, panelHeight);
					panel2.setBounds(panelWidth, 0, panelWidth, panelHeight);
					panel1.revalidate();
				}
			});
			break;
		case 2:
			SwingUtilities.invokeLater(new Runnable () {
				public void run() {
					panel1.setBounds(-panelWidth, 0, panelWidth, panelHeight);
					panel2.setBounds(0, 0, panelWidth, panelHeight);
					panel0.setBounds(panelWidth, 0, panelWidth, panelHeight);
					panel2.revalidate();
				}
			});
			break;
		}
	}
	
	public void slideRight(int stepCount, int timeBetweenSteps, float acceleration) {
		if (!animationThread.isAlive()) { // Check that no other animation is active
			if (currentPanel >= 0 && currentPanel < slideshow.length-1) { // Check that we are between the slideshow bounds.
				// For a right slide, between 0 and one less than the last one. slideshow.length is one more than the last index.
				
				animationThread = new Thread () {
					public void run() {
						
						// System.out.println("pre: " + currentPanel + "-" + panelStatus); // Debug
						
						double stepIncrement = (Math.PI/2/stepCount); // Calculate the step size
						double animationState = 0.0f; // Start at 0, ends at 1. Constant rate.
						double position = 0.0f; // Position after "acceleration" function (sine).
						while (animationState < Math.PI/2) { // Sine starts "accelerating" at 0 degrees and stops "decelerating" at 90 degrees.
							animationState += stepIncrement; // Increment state
							position = Math.pow(Math.sin(animationState), acceleration); // Calculate position
							final int pixelPosition = (int) Math.round(1 - panelWidth*position); // Scale animation function to a right slide and panel width. One minus reverses the direction.
							switch (panelStatus) { // Depending on the position of the sliding panels, different actions are needed. (See status explanation above).
							case 0:
								SwingUtilities.invokeLater(new Runnable () {
									public void run() {
										panel0.setBounds(pixelPosition, 0, panelWidth, panelHeight);
										panel1.setBounds(pixelPosition + panelWidth, 0, panelWidth, panelHeight);
									}
								});
								break;
							case 1:
								SwingUtilities.invokeLater(new Runnable () {
									public void run() {
										panel1.setBounds(pixelPosition, 0, panelWidth, panelHeight);
										panel2.setBounds(pixelPosition + panelWidth, 0, panelWidth, panelHeight);
									}
								});
								break;
							case 2:
								SwingUtilities.invokeLater(new Runnable () {
									public void run() {
										panel0.setBounds(pixelPosition + panelWidth, 0, panelWidth, panelHeight);
										panel2.setBounds(pixelPosition, 0, panelWidth, panelHeight);
									}
								});
								break;
							}
							try {
								Thread.sleep(timeBetweenSteps);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						
						// Now, set the current slide as the next one.
						currentPanel += 1;
						
						// Set final value if animation didn't end exactly at the point (floating point precision error...)
						// Also, set new "status"
						switch (panelStatus) {
						case 0:
							// Load next panel on the right.
							// Maybe it would be better to run it with SwingUtilities.invokeAndWait()...
							SwingUtilities.invokeLater(new Runnable () {
								public void run() {
									remove(panel2); // Remove old panel
									if (currentPanel < slideshow.length-1) {
										panel2 = slideshow[currentPanel+1]; // Load next panel on right
									}
									else {
										panel2 = new JPanel(); // When the end of the slideshow is reached, load a blank panel.
									}
									panel0.setBounds(-panelWidth, 0, panelWidth, panelHeight); // Set panel0 on the left
									panel1.setBounds(0, 0, panelWidth, panelHeight); // Set panel1 on the middle
									panel2.setBounds(panelWidth, 0, panelWidth, panelHeight); // Set panel2 to the right
									add(panel2); // Add new panel
									panel2.validate(); // Revalidate and repaint new panel so it appears correctly.
									panel2.repaint();
								}
							});
							panelStatus = 1;
							break;
						case 1:
							SwingUtilities.invokeLater(new Runnable () {
								public void run() {
									remove(panel0); // Remove old panel
									if (currentPanel < slideshow.length-1) {
										panel0 = slideshow[currentPanel+1]; // Load next panel on right
									}
									else {
										panel0 = new JPanel(); // When the end of the slideshow is reached, load a blank panel.
									}
									panel0.setBounds(panelWidth, 0, panelWidth, panelHeight); // Set panel0 on the right
									panel1.setBounds(-panelWidth, 0, panelWidth, panelHeight); // Set panel1 on the left now
									panel2.setBounds(0, 0, panelWidth, panelHeight); // Set panel2 to origin
									add(panel0); // Add new panel
									panel0.validate(); // Revalidate and repaint new panel so it appears correctly.
									panel0.repaint();
								}
							});
							panelStatus = 2;
							break;
						case 2:
							SwingUtilities.invokeLater(new Runnable () {
								public void run() {
									remove(panel1); // Remove old panel
									if (currentPanel < slideshow.length-1) {
										panel1 = slideshow[currentPanel+1]; // Load next panel on right
									}
									else {
										panel1 = new JPanel(); // When the end of the slideshow is reached, load a blank panel.
									}
									panel0.setBounds(0, 0, panelWidth, panelHeight); // Set panel0 on center
									panel1.setBounds(panelWidth, 0, panelWidth, panelHeight); // Set panel1 on the right
									panel2.setBounds(-panelWidth, 0, panelWidth, panelHeight); // Set panel2 to the left
									add(panel1); // Add new panel
									panel1.validate(); // Revalidate and repaint new panel so it appears correctly.
									panel1.repaint();
								}
							});
							panelStatus = 0;
							break;
						}
						//System.out.println("post: " + currentPanel + "-" + panelStatus); // Debug
					}
				};
				animationThread.start();
			}
		}
	}
	
	public void slideLeft(int stepCount, int timeBetweenSteps, float acceleration) {
		if (!animationThread.isAlive()) { // Check that no other animation is active
			if (currentPanel > 0 && currentPanel <= slideshow.length) { // Check that we are between the slideshow bounds.
				// For a left slide, between the last and the number one (second slide).
				
				//System.out.println("pre: " + currentPanel + "-" + panelStatus); // Debug
				
				animationThread = new Thread () {
					public void run() {
						double stepIncrement = (Math.PI/2/stepCount); // Calculate the step size
						double animationState = 0.0f; // Start at 0, ends at 1. Constant rate.
						double position = 0.0f; // Position after "acceleration" function (sine).
						while (animationState < Math.PI/2) { // Sine starts "accelerating" at 0 degrees and stops "decelerating" at 90 degrees.
							animationState += stepIncrement; // Increment state
							position = Math.pow(Math.sin(animationState), acceleration); // Calculate position
							final int pixelPosition = (int) Math.round(panelWidth*position); // Scale animation function to a left slide and panel width.
							switch (panelStatus) { // Depending on the position of the sliding panels, different actions are needed. (See status explanation above).
							case 0:
								SwingUtilities.invokeLater(new Runnable () {
									public void run() {
										panel2.setBounds(pixelPosition - panelWidth, 0, panelWidth, panelHeight);
										panel0.setBounds(pixelPosition, 0, panelWidth, panelHeight);
									}
								});
								break;
							case 1:
								SwingUtilities.invokeLater(new Runnable () {
									public void run() {
										panel0.setBounds(pixelPosition - panelWidth, 0, panelWidth, panelHeight);
										panel1.setBounds(pixelPosition, 0, panelWidth, panelHeight);
									}
								});
								break;
							case 2:
								SwingUtilities.invokeLater(new Runnable () {
									public void run() {
										panel1.setBounds(pixelPosition - panelWidth, 0, panelWidth, panelHeight);
										panel2.setBounds(pixelPosition, 0, panelWidth, panelHeight);
									}
								});
								break;
							}
							try {
								Thread.sleep(timeBetweenSteps);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						
						// Now, set the current slide as the previous one.
						currentPanel -= 1;
						
						// Set final value if animation didn't end exactly at the point (floating point precision error...)
						// Also, set new "status"
						switch (panelStatus) {
						case 0:
							// Load next panel on the left.
							// Maybe it would be better to run it with SwingUtilities.invokeAndWait()...
							SwingUtilities.invokeLater(new Runnable () {
								public void run() {
									remove(panel1); // Remove old panel
									if (currentPanel > 0) {
										panel1 = slideshow[currentPanel-1]; // Load next panel on left
									}
									else {
										panel1 = new JPanel(); // When the end of the slideshow is reached, load a blank panel.
									}
									panel1.setBounds(-panelWidth, 0, panelWidth, panelHeight); // Set panel1 on the left
									panel2.setBounds(0, 0, panelWidth, panelHeight); // Set panel2 on the middle
									panel0.setBounds(panelWidth, 0, panelWidth, panelHeight); // Set panel0 to the right
									add(panel1); // Add new panel
									panel1.validate(); // Revalidate and repaint new panel so it appears correctly.
									panel1.repaint();
								}
							});
							panelStatus = 2;
							break;
						case 1:
							SwingUtilities.invokeLater(new Runnable () {
								public void run() {
									remove(panel2); // Remove old panel
									if (currentPanel > 0) {
										panel2 = slideshow[currentPanel-1]; // Load next panel on left
									}
									else {
										panel2 = new JPanel(); // When the end of the slideshow is reached, load a blank panel.
									}
									panel1.setBounds(panelWidth, 0, panelWidth, panelHeight); // Set panel1 on the right
									panel2.setBounds(-panelWidth, 0, panelWidth, panelHeight); // Set panel2 on the left now
									panel0.setBounds(0, 0, panelWidth, panelHeight); // Set panel0 to origin
									add(panel2); // Add new panel
									panel2.validate(); // Revalidate and repaint new panel so it appears correctly.
									panel2.repaint();
								}
							});
							panelStatus = 0;
							break;
						case 2:
							SwingUtilities.invokeLater(new Runnable () {
								public void run() {
									remove(panel0); // Remove old panel
									if (currentPanel > 0) {
										panel0 = slideshow[currentPanel-1]; // Load next panel on left
									}
									else {
										panel0 = new JPanel(); // When the end of the slideshow is reached, load a blank panel.
									}
									panel1.setBounds(0, 0, panelWidth, panelHeight); // Set panel0 on center
									panel2.setBounds(panelWidth, 0, panelWidth, panelHeight); // Set panel1 on the right
									panel0.setBounds(-panelWidth, 0, panelWidth, panelHeight); // Set panel2 to the left
									add(panel0); // Add new panel
									panel0.validate(); // Revalidate and repaint new panel so it appears correctly.
									panel0.repaint();
								}
							});
							panelStatus = 1;
							break;
						}
						//System.out.println("post: " + currentPanel + "-" + panelStatus); // Debug
					}
				};
				animationThread.start();
			}
		}
	}
}
