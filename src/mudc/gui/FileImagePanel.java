package mudc.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class FileImagePanel extends JPanel {

	private BufferedImage image;
    
    public void loadImage(String resource) {
        try {                
            image = ImageIO.read(getClass().getResourceAsStream(resource));
            setBackground(new Color(0, 0, 0, 0));
         } catch (IOException e) {
              e.printStackTrace();
         }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        //g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
	    //g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    //g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (getWidth() < getHeight()) {
            g2d.drawImage(image, 0, 0, getWidth(), getWidth(), null);
        }
        else {
            g2d.drawImage(image, 0, 0, getHeight(), getHeight(), null);
        }       
    }
}
