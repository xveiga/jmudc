package mudc.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class FileImagePanelDynamicIcon extends JPanel {

	private BufferedImage image;
	private String fileExtension;
	private int renderingMode = 0;
	private Color textColor = null;

	public void loadImage(String resource, String fileExt, Color letteringColor) {
    	fileExtension = "." + fileExt;
    	textColor = letteringColor;
        try {                
            image = ImageIO.read(getClass().getResourceAsStream(resource));
            setBackground(new Color(0, 0, 0, 0));
         } catch (IOException e) {
              e.printStackTrace();
         }
        renderingMode = fileExtension.length()-1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        //g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    //g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    //g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    
	    // Get square bounds 
	    int squareSize = 0;
        if (getWidth() < getHeight()) {
        	squareSize = getWidth();
        }
        else {
        	squareSize = getHeight();
        } 
        g2d.drawImage(image, 0, 0, squareSize, squareSize, null); // Draw background image
        
        // Draw dynamic lettering
        int squareSegment = Math.round(squareSize/6);
        g2d.setColor(textColor);
        
        //Different text "modes" for different sizes (up to 4 letters)
        switch (renderingMode) {
        case 1:
            g2d.setFont(new Font("Tahoma", Font.PLAIN, squareSegment*3));
        	break;
        case 2:
        	g2d.setFont(new Font("Tahoma", Font.PLAIN, Math.round(squareSegment*2.4f)));
        	break;
        case 3:
        	g2d.setFont(new Font("Tahoma", Font.PLAIN, Math.round(squareSegment*1.8f)));
        	break;
        case 4:
        	g2d.setFont(new Font("Tahoma", Font.PLAIN, Math.round(squareSegment*1.55f)));
        	break;
        default:
        		g2d.setFont(new Font("Tahoma", Font.BOLD, squareSegment*3));
        		fileExtension = "?";
        }
        
        // Calculate the space the font will take to offset the String drawing.
        FontMetrics fm = g2d.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(fileExtension, g2d);
        int x = (squareSize - (int) r.getWidth()) / 2;
        int y = (squareSize - (int) r.getHeight()) / 2 + fm.getAscent();
        
        // Draw string
        g2d.drawString(fileExtension, x, y);
    }
}
