
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.*;
/**
 * @author Maxime GILLET
 *
 */
public class SummonerInfo implements Serializable{
	
	/**
	 * Default ID
	 */
	private static final long serialVersionUID = 1L;
	
	public String Name ="";
	public String Region;
	public int ID;
	public int LVL = 0;
	public URL IconUrl = null;
	public String iconVersion = "4.16.1";
	public int iconID ;
	public String League = "unranked";
	public String Division = "none";
	public int LP = 0;
	public int UnrankedWins = 0;
	public int RankedWins = 0;
	public int RankedLosses = 0;
	
	public FantasyScore FScore = new FantasyScore();
		
	//Update the Icon URL for the icon summoner image
	void UpdateIconURLVersion() throws Exception{
		
		//Get DDragon Version Icon path
		URL oracle = new URL("http://ddragon.leagueoflegends.com/realms/na.json");
        BufferedReader in = new BufferedReader(
        new InputStreamReader(oracle.openStream()));
        //Storing JSON Data
        String RAW_JSON_DATA = in.readLine();
        JSONObject SummonerObj = new JSONObject(RAW_JSON_DATA);
        iconVersion = SummonerObj.getJSONObject("n").getString("profileicon");
        IconUrl = new URL("http://ddragon.leagueoflegends.com/cdn/"+iconVersion+"/img/profileicon/"+iconID+".png");
	}
	
	//Load Ranked League Informations ( League, Divison and LP)
	void LoadLeagueInfos() throws Exception{

		//Riot API Call
		URL oracle = new URL("https://"+Region+".api.pvp.net/api/lol/"+Region+"/v2.5/league/by-summoner/"+ID+"/entry?api_key=ff35712f-5c41-43c4-895e-c52f111994d5");
		//Ping URL and open connection
		HttpURLConnection connection = (HttpURLConnection)oracle.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();
		//Checking response Code
		int code = connection.getResponseCode();
		System.out.println("Load League infos - code : "+ code);
		//All Good
		if(code == 200){
			BufferedReader in = new BufferedReader(
	        new InputStreamReader(oracle.openStream()));
	        //Storing JSON Data
	        String RAW_JSON_DATA = in.readLine();
	        JSONObject SummonerObj = new JSONObject(RAW_JSON_DATA);
	        League = SummonerObj.getJSONArray(""+ID).getJSONObject(0).getString("tier");
	        LP = SummonerObj.getJSONArray(""+ID).getJSONObject(0).getJSONArray("entries").getJSONObject(0).getInt("leaguePoints");
	        Division = SummonerObj.getJSONArray(""+ID).getJSONObject(0).getJSONArray("entries").getJSONObject(0).getString("division");		
		}
		else{
			
			System.out.println("Error !");
			if(code == 400)
				System.out.println("Bad request ");
			else if(code == 401)
				System.out.println("Unauthorized");
			else if(code == 404)
				System.out.println("League not found, this summoner is Unranked!");
			else if(code == 429)
				System.out.println("Rate limit exceeded");
			else if(code == 500)
				System.out.println("Internal server error");
			else if(code == 503)
				System.out.println("Service unavailable, try again later");
			
		}
	}
	
	void LoadStatsSummary() throws Exception{
		
		//Riot API Call
		URL oracle = new URL("https://"+Region+".api.pvp.net/api/lol/"+Region+"/v1.3/stats/by-summoner/"+ID+"/summary?season=SEASON4&api_key=ff35712f-5c41-43c4-895e-c52f111994d5");
		//Ping URL and open connection
		HttpURLConnection connection = (HttpURLConnection)oracle.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();
		//Checking response Code
		int code = connection.getResponseCode();
		System.out.println("Load Stats summary infos - code : "+ code);
		//All Good
		if(code == 200){
			BufferedReader in = new BufferedReader(
	        new InputStreamReader(oracle.openStream()));
	        //Storing JSON Data
	        String RAW_JSON_DATA = in.readLine();
	        JSONObject SummonerObj = new JSONObject(RAW_JSON_DATA);
	        
	        boolean EOJSON = false; //End of JSON file
	        int index = 0;

	        
	        while (!EOJSON){
	        	
	        	try{
	    	        if(SummonerObj.getJSONArray("playerStatSummaries").getJSONObject(index).getString("playerStatSummaryType").equals("Unranked") )
	    	        	UnrankedWins = SummonerObj.getJSONArray("playerStatSummaries").getJSONObject(index).getInt("wins");
	    	        else if(SummonerObj.getJSONArray("playerStatSummaries").getJSONObject(index).getString("playerStatSummaryType").equals("RankedSolo5x5")){
	    	        	RankedLosses = SummonerObj.getJSONArray("playerStatSummaries").getJSONObject(index).getInt("losses");
	    	        	RankedWins = SummonerObj.getJSONArray("playerStatSummaries").getJSONObject(index).getInt("wins");
	    	        }
	    	        	
	        	}catch(Exception e){
	        		
	        		EOJSON = true; //reached end
	        	}
	        	
	        	index ++;
	        	
	        }
	       
		}
		else{
			
			System.out.println("Error !");
			if(code == 400)
				System.out.println("Bad request ");
			else if(code == 401)
				System.out.println("Unauthorized");
			else if(code == 404)
				System.out.println("Stats data not found");
			else if(code == 429)
				System.out.println("Rate limit exceeded");
			else if(code == 500)
				System.out.println("Internal server error");
			else if(code == 503)
				System.out.println("Service unavailable, try again later");
			
		}
	}
	
}