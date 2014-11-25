import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;



/**
 * @author Maxime GILLET
 *
 */

/** Favorite Manager panel handling the display list of the summoners we are following */
public class FavoriteManagerPanel extends JPanel implements ActionListener {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 1L;
	
	public JPanel PanelTab;
	public ArrayList<SummonerInfo> FavoritePlayers ; 
	private MainPanel parentRef;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	//Default Constructor
	FavoriteManagerPanel(MainPanel ref) throws IOException{
		parentRef = ref;
		InstantiateFavoriteList();
		
	}
	
	//Instantiate empty PanelTab
	void InstantiateFavoriteList() throws IOException{
		
		FavoritePlayers = new ArrayList<SummonerInfo>();
		PanelTab = new JPanel();
		BoxLayout ColumnLayout = new BoxLayout(PanelTab,BoxLayout.Y_AXIS);
		PanelTab.setLayout(ColumnLayout);
		PanelTab.setBackground(new Color(121,190,219,50));
		ColumnLayout = new BoxLayout(this,BoxLayout.Y_AXIS);
		setLayout(ColumnLayout);
		add(new JLabel("<html><font color='white' face='Verdana'>Favorite Summoners : </font></html>"));
		
		JScrollPane scrollPane = new JScrollPane(PanelTab);
		scrollPane.setPreferredSize(new Dimension(510,320));
		scrollPane.setBackground(new Color(0,0,0,0));
	    scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener(){
	        @Override
	        public void adjustmentValueChanged(AdjustmentEvent e) {
	            getParent().getParent().getParent().repaint();
	        }
	    });
	    add(scrollPane);
	    

	    setBackground(new Color(0,0,0,0));
	}

	//Add new summoner to Favorite PanelTab
	public void AddFavorite(SummonerInfo newSummoner){
		
		boolean isNew = true;
		for(int i = 0 ; i < FavoritePlayers.size() ; i ++){
			
			if(FavoritePlayers.get(i).Name.equals(newSummoner.Name) && FavoritePlayers.get(i).Region.equals(newSummoner.Region)){
				
				JOptionPane.showMessageDialog(new JFrame(), "This player is already in your favorite list !");
				isNew = false;
			}
		}
		if(isNew){
			try {
				FavoritePlayers.add(newSummoner);
				FavoriteObject temp = new FavoriteObject(newSummoner, parentRef);
				PanelTab.add(temp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
				
		getParent().revalidate();
	   	getParent().repaint();
	}

}
