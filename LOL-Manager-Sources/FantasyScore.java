import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Maxime GILLET
 *
 */

public class FantasyScore implements Serializable{
	
	/**
	 * Default ID
	 */
	private static final long serialVersionUID = 1L;
	
	/** Total Player Points	 */
	int Kills = 0;//+2 ea
	int Deaths = 0;//-0.5 ea
	int Assits = 0;//+1.5 ea
	int Minions = 0;//+0.01 ea
	int TripleKills = 0 ;//+2 ea
	int QuadraKills = 0;//+5 ea
	int PentaKills = 0;//+10 ea
	int AssistOrKillMaster = 0;//+2 (if assists or kills >= 10)

	/** Total Team Points	 */
	boolean FirstBlood = false;//+2 
	int Towers = 0;//+1 ea
	int DragonKills = 0;//+1 ea 
	int BaronKills = 0;//+2 ea
	boolean Victory = false;//+2
	
	ArrayList <Game> game = new ArrayList<Game>();
	
	/** Calculate and return Player Score	 */
	float PlayerScore(){
		
		return (float) (Kills*2 - Deaths*(0.5) + Assits*1.5 + Minions*0.01 + TripleKills*2 + QuadraKills*5 + PentaKills*10+AssistOrKillMaster*2);
		
	}
	
	/** Calculate and return Global team Score	 */
	float TeamScore(){
		
		float score = 0;
		
		if(FirstBlood)
			score += 2;
		if(Victory)
			score += 2;	
		
		score += DragonKills*1 + BaronKills*2;
		
		return score;
		
	}

	
}