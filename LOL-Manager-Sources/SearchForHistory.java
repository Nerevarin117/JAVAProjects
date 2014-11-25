import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.border.Border;

import org.json.JSONObject;

public class SearchForHistory extends JPanel implements PropertyChangeListener {
	

	/**
	 * Default ID
	 */
	private static final long serialVersionUID = 1L;

	private JProgressBar progressBar;

	private SearchForHistoryTask task;


	/**
	 *  Class Handling the long task of retrieving game data from history
	 */
	public class SearchForHistoryTask extends SwingWorker<Integer, String>{
	
		  private SummonerInfo currentSum;
		  private FantasyManagerPanel parentRef;
		  private MainPanel MainParentRef;
		  
		  public SearchForHistoryTask(SummonerInfo sum , FantasyManagerPanel ref , MainPanel MainParent) {
			  currentSum = sum;
			  parentRef = ref;
			  MainParentRef = MainParent;
		  }
		  
		  @Override
		  protected Integer doInBackground() throws Exception {
		
			//Riot API Call
			URL oracle = new URL("https://"+currentSum.Region+".api.pvp.net/api/lol/"+currentSum.Region+"/v1.3/game/by-summoner/"+currentSum.ID+"/recent?api_key=ff35712f-5c41-43c4-895e-c52f111994d5");
			//Ping URL and open connection
			HttpURLConnection connection = (HttpURLConnection)oracle.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			//Checking response Code
			int code = connection.getResponseCode();
			System.out.println("Game Data Recovered - code :"+code);
			//All Good
			if(code == 200){
				BufferedReader in = new BufferedReader(
		        new InputStreamReader(oracle.openStream()));
		        //Storing JSON Data
		        String RAW_JSON_DATA = in.readLine();
		        JSONObject GamesObj = new JSONObject(RAW_JSON_DATA);
		        for(int i = 0 ; i < 10 ; i++){
		        	
		        	//Check if the game i is in JSON File
		        	try{
		        		GamesObj.getJSONArray("games").getJSONObject(i);
		        	}catch (Exception e){	
						break;
					}
		        	
		        	//Game data available, add game reference to the history
		        	currentSum.FScore.game.add(new Game());
			        JSONObject Stats;
					Stats = GamesObj.getJSONArray("games").getJSONObject(i).getJSONObject("stats");
				
			     
					try{
						currentSum.FScore.game.get(i).Minions = Stats.getInt("minionsKilled");
					}catch (Exception e){
					}
					try{
						currentSum.FScore.game.get(i).TripleKills = Stats.getInt("tripleKills");
					}catch (Exception e){	
					}
					try{
						currentSum.FScore.game.get(i).QuadraKills = Stats.getInt("quadraKills");
					}catch (Exception e){	
					}
					try{
						currentSum.FScore.game.get(i).PentaKills = Stats.getInt("pentaKills");
					}catch (Exception e){	
					}
					try{
						currentSum.FScore.game.get(i).Kills = Stats.getInt("championsKilled");
					}catch (Exception e){	
					}
					try{
						currentSum.FScore.game.get(i).Deaths = Stats.getInt("numDeaths");
					}catch (Exception e){	
					}
					try{
						currentSum.FScore.game.get(i).Assits = Stats.getInt("assists");
					}catch (Exception e){	
					}
					try{
						currentSum.FScore.game.get(i).Victory = Stats.getBoolean("win");
					}catch (Exception e){	
					}
					try{
						currentSum.FScore.game.get(i).gameMode = GamesObj.getJSONArray("games").getJSONObject(i).getString("gameMode");
					}catch (Exception e){	
					}
					try{
						currentSum.FScore.game.get(i).subType = GamesObj.getJSONArray("games").getJSONObject(i).getString("subType");
					}catch (Exception e){	
					}
					
					/** Recovering Champion name */
					//this part is taking some time maybe better load the whole champion list at the start */
					try{
						oracle = new URL("https://global.api.pvp.net/api/lol/static-data/"+currentSum.Region+"/v1.2/champion/"+GamesObj.getJSONArray("games").getJSONObject(i).getInt("championId")+"?api_key=ff35712f-5c41-43c4-895e-c52f111994d5");
						 in = new BufferedReader(new InputStreamReader(oracle.openStream()));
				        //Storing JSON Data
				        RAW_JSON_DATA = in.readLine();
				        JSONObject champ = new JSONObject(RAW_JSON_DATA);
				        currentSum.FScore.game.get(i).championName =  champ.getString("name");
		
					}
					catch (Exception e){	
					}
			
					if( currentSum.FScore.game.get(i).Assits >= 10 || currentSum.FScore.game.get(i).Kills >= 10)
						currentSum.FScore.game.get(i).AssistOrKillMaster = 1;
					
					// update the progress
					setProgress((i+1)*10);
		        }
	
			}
			else{
				
				System.out.println("Error !");
				if(code == 400)
					System.out.println("Bad request ");
				else if(code == 401)
					System.out.println("Unauthorized");
				else if(code == 404)
					System.out.println("Game data not found, no recent games found!");
				else if(code == 429)
					System.out.println("Rate limit exceeded");
				else if(code == 500)
					System.out.println("Internal server error");
				else if(code == 503)
					System.out.println("Service unavailable, try gain later");
				
			}
			
			return 0;
		 
		  }
		  
		  
		  @Override
	    public void done() {
	        setCursor(null); // turn off the wait cursor
		    
			//Add contents to the window.
			for(int i = 0; i < 10 ; i++){
				parentRef.content.add(new FantasyScoreBoard(currentSum.FScore.game.get(i)));
			}
			
			parentRef.spane = new JScrollPane(parentRef.content);
			parentRef.spane.setPreferredSize(new Dimension(640,230));		
			parentRef.add(parentRef.spane);
			parentRef.setBackground(new Color(0,0,0,50));
			
			
			MainParentRef.FantasyManager = parentRef;			
			MainParentRef.add(MainParentRef.FantasyManager,BorderLayout.SOUTH);
			 
			System.out.println(" History Loaded !");
		    javax.swing.SwingUtilities.getWindowAncestor(parentRef).revalidate();
		  	javax.swing.SwingUtilities.getWindowAncestor(parentRef).repaint();
		  	

	    }
		
	}
	
	/** Constructor */
	public SearchForHistory(SummonerInfo sum, FantasyManagerPanel parentRef ,MainPanel MainParent) {
			  
		super(new BorderLayout());
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		task = new SearchForHistoryTask(sum , parentRef, MainParent);
		task.addPropertyChangeListener(this);
		task.execute();
		
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		
		JPanel panel = new JPanel();
		panel.add(progressBar);
		panel.setBackground(Color.ORANGE);
		Border raisedbevel = BorderFactory.createRaisedBevelBorder();
		Border loweredbevel = BorderFactory.createLoweredBevelBorder();
		Border thinBorder = BorderFactory.createCompoundBorder(raisedbevel, loweredbevel);
		panel.setBorder(thinBorder);
		add(panel, BorderLayout.PAGE_START);
		setBorder(thinBorder);
	}
	

	/** Invoked when task's progress property changes. */
	public void propertyChange(PropertyChangeEvent evt) {
	  if ("progress" == evt.getPropertyName()) {
	    int progress = (Integer) evt.getNewValue();
	    progressBar.setValue(progress);
	    if(progress == 100)
	  	  javax.swing.SwingUtilities.getWindowAncestor(this).dispose();
	  }
	}
	
	
}