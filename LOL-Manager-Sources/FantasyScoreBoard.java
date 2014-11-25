import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

/**
 * @author Maxime GILLET
 *
 */


/** Panel displaying the Fantasy (LCS like) score board.*/
public class FantasyScoreBoard extends JPanel {
	
	
	/**
	 * ID
	 */
	private static final long serialVersionUID = 1L;
	
	JLabel TitleElement;
	JPanel ScoreBoardContainer;
	JPanel Line1;
	JPanel Line1Scores;
	JPanel Line2;
	JPanel Line2Scores;
	JPanel FinalScore;
	
	FantasyScoreBoard(Game game){
		
		//Set the Title 
		String Result = "LEAVE";
		if(game.Victory)
			Result = "VICTORY";
		else Result = "DEFEAT";
		TitleElement = new JLabel(game.gameMode+" - "+game.subType);
		Border thinBorder = LineBorder.createBlackLineBorder();
		TitleElement.setBorder(thinBorder);
		setBackground(new Color(0,0,0,50));
		add(TitleElement);

		ScoreBoardContainer = new JPanel();
		BoxLayout myLayout = new BoxLayout(ScoreBoardContainer , BoxLayout.Y_AXIS);
		ScoreBoardContainer.setLayout(myLayout);
		ScoreBoardContainer.setBorder(thinBorder);
		
		//Set Score Board
		Line1 = new JPanel();
		Line1.setBackground(Color.BLACK);
		Line1.setPreferredSize(new Dimension(300,30));
		GridLayout grid = new GridLayout(1,0);
		Line1.setLayout(grid);
		Line1.add(new JLabel());
		Line1.add(new JLabel("<html><font color= #D5D5D5>Kills</font></html>"));
		Line1.add(new JLabel("<html><font color= #D5D5D5>Deaths</font></html>"));
		Line1.add(new JLabel("<html><font color= #D5D5D5>Assists</font></html>"));
		Line1.add(new JLabel("<html><font color= #D5D5D5>Minions</font></html>"));
		Line1.add(new JLabel());
		
		ScoreBoardContainer.add(Line1);
		Line1Scores = new JPanel();
		Line1Scores.setBackground(Color.GRAY);
		Line1Scores.setPreferredSize(new Dimension(300,30));
		Line1Scores.setLayout(grid);
		Line1Scores.add(new JLabel());
		Line1Scores.add(new JLabel("<html><font color= 'white'>"+game.Kills+"</font></html>"));
		Line1Scores.add(new JLabel("<html><font color='white'>"+game.Deaths+"</font></html>"));
		Line1Scores.add(new JLabel("<html><font color= 'white'>"+game.Assits+"</font></html>"));
		Line1Scores.add(new JLabel("<html><font color= 'white'>"+game.Minions+"</font></html>"));
		Line1Scores.add(new JLabel());
		ScoreBoardContainer.add(Line1Scores);
		
		Line2 = new JPanel();
		Line2.setBackground(Color.BLACK);
		Line2.setPreferredSize(new Dimension(300,30));
		Line2.setLayout(grid);
		Line2.add(new JLabel());
		Line2.add(new JLabel("<html><font color= #D5D5D5>Triple</font></html>"));
		Line2.add(new JLabel("<html><font color= #D5D5D5>Quadra</font></html>"));
		Line2.add(new JLabel("<html><font color= #D5D5D5>Penta</font></html>"));
		Line2.add(new JLabel("<html><font color= #D5D5D5>K/A>10</font></html>"));
		Line2.add(new JLabel());
		ScoreBoardContainer.add(Line2);
		
		Line2Scores = new JPanel();
		Line2Scores.setBackground(Color.GRAY);
		Line2Scores.setPreferredSize(new Dimension(300,30));
		Line2Scores.setLayout(grid);
		Line2Scores.add(new JLabel());
		Line2Scores.add(new JLabel("<html><font color= 'white'>"+game.TripleKills+"</font></html>"));
		Line2Scores.add(new JLabel("<html><font color='white'>"+game.QuadraKills+"</font></html>"));
		Line2Scores.add(new JLabel("<html><font color= 'white'>"+game.PentaKills+"</font></html>"));
		Line2Scores.add(new JLabel("<html><font color= 'white'>"+game.AssistOrKillMaster+"</font></html>"));
		Line2Scores.add(new JLabel());
		ScoreBoardContainer.add(Line2Scores);
		
		
		FinalScore = new JPanel();
		FinalScore.setBackground(Color.BLACK);
		FinalScore.setPreferredSize(new Dimension(300,30));
		FinalScore.add(new JLabel("<html><font color=#D5D5D5>"+Result+" - "+game.championName+" - Total Score : "+game.PlayerScore()+"</font></html>"));
		ScoreBoardContainer.add(FinalScore);
		
		add(ScoreBoardContainer);
		setPreferredSize(new Dimension(310,200));
		setMaximumSize(new Dimension(310,200));
 		setBackground(new Color(0,0,0,0));

	}	 

}
