import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Maxime GILLET
 *
 */
public class FavoriteObject extends JComponent implements ActionListener, MouseListener{
	
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1L;
	
	public JButton RemoveFavorite;
	public JButton ShowDetails;
	private JLabel LeagueIcon;
	private JLabel Name;
	private JLabel LP;
	private JLabel Division;
	public SummonerInfo FavoriteSummonerInfo = null;
	private MainPanel parentRef;
		
	//Default Constructor
	FavoriteObject() throws IOException{
	

	}
	
	//Constructor passing a summoner info
	FavoriteObject(SummonerInfo newSummoner, MainPanel ref) throws IOException{
		parentRef = ref;
		FavoriteSummonerInfo = newSummoner;

		//Layout Setting and dimensions
		FlowLayout LineLayout = new FlowLayout();
		//Border thinBorder = LineBorder.createBlackLineBorder();
		Border raisedbevel = BorderFactory.createRaisedBevelBorder();
		Border loweredbevel = BorderFactory.createLoweredBevelBorder();
		Border thinBorder = BorderFactory.createCompoundBorder(raisedbevel, loweredbevel);
		
		setLayout(LineLayout);
		setBorder(thinBorder);


		//Set Button to Remove summoner from favorite Button
		Image imgLoader = ImageIO.read(getClass().getResource("Resources/close.png"));
		RemoveFavorite = new JButton();
		RemoveFavorite.addActionListener(this);
		RemoveFavorite.setPreferredSize(new Dimension(50, 50));
		RemoveFavorite.setIcon(new ImageIcon(imgLoader));
		RemoveFavorite.setToolTipText("Removes this summoner from you favorites");
		RemoveFavorite.addMouseListener(this);
		
		//Set Name
		Name = new JLabel("<html><font color= 'white' face='Comic Sans MS'>"+newSummoner.Name+"</font></html>");
		Name.setPreferredSize(new Dimension(100, 50));
		
		//Set Tier Icon
		imgLoader = GetLeagueIcon();
		LeagueIcon = new JLabel();
		LeagueIcon.setPreferredSize(new Dimension(55, 50));
		LeagueIcon.setIcon(new ImageIcon(imgLoader));
		
		//Set Division
		Division = new JLabel("<html><font color= 'white' face='Comic Sans MS'>"+" Division "+newSummoner.Division+" "+"</font></html>");
		Division.setPreferredSize(new Dimension(80, 50));
		
		//Set LP
		LP = new JLabel("<html><font color= 'white' face='Comic Sans MS'>"+" "+newSummoner.LP+" LP "+"</font></html>");
		LP.setPreferredSize(new Dimension(50, 50));
		
		//Set Button to display details of summoner info
		ShowDetails = new JButton("Details");
		ShowDetails.addActionListener(this);
		ShowDetails.setPreferredSize(new Dimension(100, 50));
		
		JPanel bck = new JPanel();
		//bck.setPreferredSize(new Dimension(450,65));
		bck.setBackground(new Color(121,190,219,50));

		//Add to container
		bck.add(RemoveFavorite);
		bck.add(Name);
		bck.add(LeagueIcon);
		bck.add(Division);
		bck.add(LP);
		bck.add(ShowDetails);
		setMaximumSize(new Dimension(480,80));
		add(bck);


	}
	
	//Load League Icon corresponding to summoner league (local call)
	Image GetLeagueIcon() throws IOException{
		
		if(FavoriteSummonerInfo.League == null)
			return ImageIO.read(getClass().getResource("Resources/LeagueIcons/unranked.png"));
		else
		return ImageIO.read(getClass().getResource("Resources/LeagueIcons/"+FavoriteSummonerInfo.League+".png"));
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		
		// TODO Auto-generated method stub
		if( evt.getSource() == RemoveFavorite){
			JPanel parent = (JPanel) RemoveFavorite.getParent().getParent().getParent();
			//Passer un this au constructeur pour avoir la référence
			FavoriteManagerPanel manager = (FavoriteManagerPanel) RemoveFavorite.getParent().getParent().getParent().getParent().getParent().getParent();
			for ( int i = 0;  i < manager.FavoritePlayers.size(); i++){
	            String tempName = manager.FavoritePlayers.get(i).Name;
	            if(tempName.equals(FavoriteSummonerInfo.Name)){
	            	manager.FavoritePlayers.remove(i);
	            }
	        }
			
			manager.getParent().getParent().revalidate();
			
			manager.getParent().getParent().repaint();

			parent.remove(this);
			

		}
		if( evt.getSource() == ShowDetails){
			
			if(parentRef.FantasyManager != null)
				parentRef.remove(parentRef.FantasyManager);
			parentRef.currentSummonerDisplayed = FavoriteSummonerInfo;
			parentRef.CreateInfoPanel(1);
	

			parentRef.getParent().revalidate();
			parentRef.getParent().repaint();
			
		}
		

		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		parentRef.getParent().revalidate();
		parentRef.getParent().repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		parentRef.getParent().revalidate();
		parentRef.getParent().repaint();
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		parentRef.getParent().revalidate();
		parentRef.getParent().repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	

}
