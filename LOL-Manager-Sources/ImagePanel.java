import java.awt.*;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

class ImagePanel extends JPanel {

	/**
	 * Default ID
	 */
	private static final long serialVersionUID = 1L;
	private Image img;
  
  public ImagePanel(String path)  {
	  
    try {
    	if(getClass().getResource(path) !=null){
    		this.img = ImageIO.read(getClass().getResource(path));
    		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
    	    setPreferredSize(size);
    	    setMinimumSize(size);
    	    setMaximumSize(size);
    	    setSize(size);
    	    setLayout(null);
    	}
    	else{
    		System.out.println("image not found !");
    	}
	} catch (IOException e) {
		System.out.println("Invalid format or dimensions for background Image");
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

  }

  public void paintComponent(Graphics g) {
    g.drawImage(img, 0, 0, null);
  }

}