import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;


/**
 * @author Maxime GILLET
 */

/** Class saving simple informations of a user */
public class UserInfo implements Serializable{
	
	/**
	 * Default ID
	 */
	private static final long serialVersionUID = 1L;
	
	private String username;
	private String password;
	private String SummonerName = "unknown";
	private String region = "unknown";
	private String APIKey = "ff35712f-5c41-43c4-895e-c52f111994d5";
	public boolean online = false;
	public ArrayList<SummonerInfo> favoriteList;
	
	/**Default Constructor */
	UserInfo(){

		favoriteList = new ArrayList<SummonerInfo>();
	}
	
	/**Other Constructor */
	UserInfo(String u , String pw){
		username = u ;
		password = pw;
		favoriteList = new ArrayList<SummonerInfo>();
	}
	
	/**
	 ** Getters
	 */
	public String getPassword(){
		
		return password;
	}
	
	public String getUsername(){
		
		return username;
	}
	
	public String getRegion(){
		
		return region;
	}
	
	public String getSummonerName(){
		
		return SummonerName;
	}
	
	public String getAPIKey(){
		
		return APIKey;
	}
	
	/**
	 ** Setters
	 */
	public void setUsername(String s){
		
		 username = s;
	}
	
	public void setRegion(String s){
		
		 region = s;
	}
	
	public void setSummonerName(String s){
		
		 SummonerName = s;
	}
	
	public void setPassword(String s){
		
		password = s;
	}
	public void setAPIKey(String s){
		
		APIKey = s;
	}
	
	/** Test and set APIKEY */
	public boolean tryAPIKey(String s){
		
		//test key
		URL oracle = null;
		try {
			oracle = new URL("https://na.api.pvp.net/api/lol/na/v1.4/summoner/by-name/RiotSchmick?api_key="+APIKey);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	HttpURLConnection connection = null;
    	int code = 0;
    	
		try {
			connection = (HttpURLConnection)oracle.openConnection();
		} catch (IOException e) {
		}
		try {
			connection.setRequestMethod("GET");
		} catch (ProtocolException e) {
		}
		try {
			connection.connect();
		} catch (IOException e) {
		}
		//Checking response Code
		try {
			code = connection.getResponseCode();
		} catch (IOException e) {
		}
			
		//All Good
		if(code == 200){  
			
			APIKey = s;	
	        return true;//API KEY
		}
		else{
			System.out.println("Error !");
			if(code == 400)
				System.out.println("Bad request ");//Wrong API Key
			else
				System.out.println("Error with API !");//General error
			
			return false;
		}

	}
	
	
}