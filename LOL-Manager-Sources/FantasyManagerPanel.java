import java.io.IOException;

import javax.swing.*;

import org.json.JSONException;

/**
 * @author Maxime GILLET
 *
 */

/** Front class to call the SearchForHistory swing worker */
public class FantasyManagerPanel extends JPanel {

	/**
	 * Default ID
	 */
	private static final long serialVersionUID = 1L;
	
	public JScrollPane spane ;
	public JPanel content;
	
	/** Constructor */
	FantasyManagerPanel(SummonerInfo CurrentSummonerDisplayed , MainPanel MainParent) throws IOException, JSONException{
		
		 CurrentSummonerDisplayed.FScore = new FantasyScore();
		 content = new JPanel();
		 
		 TaskBarInstance(CurrentSummonerDisplayed , MainParent);
         
	}
	
	/** Create the progress Bar window */
	void TaskBarInstance(SummonerInfo sum ,MainPanel MainParent) throws JSONException, IOException{
				 
	    // Create and set up the window.
	    JFrame frame = new JFrame();
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setLocation(MainParent.getLocationOnScreen().x+280, MainParent.getLocationOnScreen().y+500);
	    frame.setUndecorated(true);
	    // Create and set up the content pane.
	    JComponent newContentPane = new SearchForHistory(sum, this , MainParent);
	    frame.setContentPane(newContentPane);
	    // Display the window.
	    frame.pack();
	    frame.setVisible(true);
	    
	}

	
}
