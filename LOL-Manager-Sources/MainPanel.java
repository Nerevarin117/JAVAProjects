import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.jdesktop.xswingx.PromptSupport;
import org.json.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
/**
 * @author Maxime GILLET
 *
 */


public class MainPanel extends JPanel implements ActionListener,MouseListener {
	
    /**
	 * UID
	 */
	private static final long serialVersionUID = 1L;
	
	//User reference
	private UserInfo currentUser;
		
	//Reference to main JFrame
	private LoginWindow windowRef;
	
	//Search panel
	private JTextField searchField;
    private JButton SeekButton;
    private JComboBox<?> RegionSelector;

    //Search Result
    private	JButton AddToFavorite;
    private	JButton LoadHistory;
    private JPanel searchResultPanel = null;
    private JPanel SearchPanel = null;
    
    static final String RegionList[] = {"br", "eune", "euw", "kr", "lan", "las", "na", "oce", "ru", "tr"};
    
    //Reference of childs
    public SummonerInfo currentSummonerDisplayed;
    public FavoriteManagerPanel favoriteManager;
    public FantasyManagerPanel FantasyManager;
    
    //Create Seek Bar to find a summoner in RIOT API DataBase
    void CreateSeeker() throws IOException{
    	
    	SearchPanel = new JPanel();
    	
    	//General Guideline
        SearchPanel.add(new JLabel("Look for a Player ! "));
        SearchPanel.add(new JLabel("Region : "));
        
        //To select the region of the summoner
        RegionSelector = new JComboBox<String>(RegionList);
        RegionSelector.addActionListener(this);
        RegionSelector.addMouseListener(this);
        SearchPanel.addMouseListener(this);
        SearchPanel.add(RegionSelector);
        SearchPanel.setBackground(Color.ORANGE);
        
        //To enter the name of summoner you want to seek 
        SearchPanel.add(new JLabel("Player : "));
        searchField = new JTextField(20);
        searchField.setDocument(new JTextFieldLimit(30));
        PromptSupport.setPrompt("Summoner Name...", searchField);
		PromptSupport.setFontStyle(2, searchField);
        searchField.addActionListener(this);
        searchField.setToolTipText(null);
        SearchPanel.add(searchField);
        
        //To launch seeking
        SeekButton = new JButton("SEARCH!");
        SeekButton.addActionListener(this);
        SearchPanel.add(SeekButton);
        
        add(SearchPanel,BorderLayout.NORTH);
 
    }
    
    //Create The info Panel Displaying general information
    public void CreateInfoPanel(int version){
    	
    	boolean MissingIcon = false;
    	if(searchResultPanel != null)
    		remove(searchResultPanel);
    	searchResultPanel = new JPanel();
    	JLabel title = new JLabel("<html><font color='white' face='Verdana'>Research Result : </font></html>");
    	Border raisedbevel = BorderFactory.createRaisedBevelBorder();
		Border loweredbevel = BorderFactory.createLoweredBevelBorder();
		Border thinBorder = BorderFactory.createCompoundBorder(raisedbevel, loweredbevel);
		searchResultPanel.setBorder(thinBorder);
    	searchResultPanel.setBackground(new Color(121,190,219,50));
    	//searchResultPanel.setLayout(new BoxLayout(searchResultPanel, BoxLayout.Y_AXIS));
    	searchResultPanel.setLayout(new BorderLayout());
    	searchResultPanel.add(title,BorderLayout.NORTH);
    	
    	JPanel infoPanel = new JPanel();
    	infoPanel.setBackground(new Color(0,0,0,0));
    	JPanel CurrentGridInfoPanel = new JPanel();
        CurrentGridInfoPanel.setBackground(new Color(0,0,0,0));
    	 //Display the summoner Icon
        Image image = null;
        try {
            URL url = new URL("http://ddragon.leagueoflegends.com/cdn/"+currentSummonerDisplayed.iconVersion+"/img/profileicon/"+currentSummonerDisplayed.iconID+".png");
            image = ImageIO.read(url);
        } catch (IOException e) {
        	 setCursor(null);
        	 MissingIcon = true;
        }
        
        if(version == 0){
            AddToFavorite = new JButton("Add To Favorites");
            AddToFavorite.addActionListener(this);
            AddToFavorite.setToolTipText("Add this summoner to your favorites");
        }else{
        	LoadHistory = new JButton("Load History");
        	LoadHistory.addActionListener(this);
        	LoadHistory.addMouseListener(this);
        	LoadHistory.setToolTipText("Load Game History data calculated as fantasy LCS points");
        	LoadHistory.setToolTipText("Show the last 10 games played");
        }

        
        GridLayout experimentLayout = new GridLayout(0,1);
        experimentLayout.layoutContainer(CurrentGridInfoPanel);
        CurrentGridInfoPanel.setLayout(experimentLayout);

        CurrentGridInfoPanel.add(new JLabel("<html><font color='white' face='Comic Sans MS'>" +" " + currentSummonerDisplayed.Name+" </font></html>"));
        CurrentGridInfoPanel.add(new JLabel("<html><font color='white' face='Comic Sans MS'>" +" LVL : "+currentSummonerDisplayed.LVL+" </font></html>"));
        CurrentGridInfoPanel.add(new JLabel("<html><font color='white' face='Comic Sans MS'>" +" Unranked Wins : "+currentSummonerDisplayed.UnrankedWins+" </font></html>"));
        CurrentGridInfoPanel.add(new JLabel("<html><font color='white' face='Comic Sans MS'>" +" ------------------" +"</font></html>"));
        CurrentGridInfoPanel.add(new JLabel("<html><font color='white' face='Comic Sans MS'>" +" League : "+currentSummonerDisplayed.League+" </font></html>"));
        CurrentGridInfoPanel.add(new JLabel("<html><font color='white' face='Comic Sans MS'>" +" Division : "+currentSummonerDisplayed.Division+" </font></html>"));
        CurrentGridInfoPanel.add(new JLabel("<html><font color='white' face='Comic Sans MS'>" +" LP : "+currentSummonerDisplayed.LP+" </font></html>"));
        CurrentGridInfoPanel.add(new JLabel("<html><font color='white' face='Comic Sans MS'>" +" ------------------" +"</font></html>"));
        CurrentGridInfoPanel.add(new JLabel("<html><font color='white' face='Comic Sans MS'>" +" Ranked Wins : "+currentSummonerDisplayed.RankedWins+" </font></html>"));
        CurrentGridInfoPanel.add(new JLabel("<html><font color='white' face='Comic Sans MS'>" +" Ranked Losses : "+currentSummonerDisplayed.RankedLosses+" </font></html>")); 
        if(currentSummonerDisplayed.RankedLosses != 0)
        CurrentGridInfoPanel.add(new JLabel("<html><font color='white' face='Comic Sans MS'>" +" Win Ratio : "+String.format("%.2f",(float)currentSummonerDisplayed.RankedWins/((float)currentSummonerDisplayed.RankedLosses + currentSummonerDisplayed.RankedWins)*100)+" %"+"</font></html>")); 
        infoPanel.add(CurrentGridInfoPanel);
        if(!MissingIcon)
        	infoPanel.add(new JLabel(new ImageIcon(image)));   
        searchResultPanel.add(infoPanel,BorderLayout.CENTER);
        if(version == 0)
        	searchResultPanel.add(AddToFavorite,BorderLayout.SOUTH);
        else searchResultPanel.add(LoadHistory,BorderLayout.SOUTH);
        add(searchResultPanel,BorderLayout.EAST);
    	
    }
    
    
    //Default Constructor
    public MainPanel(LoginWindow ref) {
    	
    	  windowRef = ref;
    	  windowRef.addWindowListener(new WindowAdapter() {
		        public void windowClosing(WindowEvent e) {
		        	SaveUserDataOnServer();
		        	windowRef.dispose();
				}
		   });

    	setLayout(new BorderLayout());
    	
    	//Create Search component
    	try {
			CreateSeeker();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	
    	//Create Favorite Manager component
    	try {
    		favoriteManager = new FavoriteManagerPanel(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	favoriteManager.setBackground(new Color(0,0,0,150));
        add(favoriteManager,BorderLayout.WEST);
	    this.setBackground(new Color(0,0,0,150));
	    
	    LoadUserDataFromServer();
    }
 
    //Action Performer
    public void actionPerformed(ActionEvent evt) {
    	    	
    	if(evt.getSource() == searchField || evt.getSource() == SeekButton){
    		String name = searchField.getText();
        	String Region = (String)RegionSelector.getSelectedItem();
        	if(name != "" && name != null && !name.isEmpty()){
        		try {
        			
        	    	
        	    	if(searchResultPanel != null)
        	    		remove(searchResultPanel);
        	    	if(SearchSummoner(name, Region))
        	    		CreateInfoPanel(0);
        	    	setCursor(null);

        		} catch (Exception e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
    	}
    	else if(evt.getSource() == AddToFavorite){
    		
    		favoriteManager.AddFavorite(currentSummonerDisplayed);
    		
    	}
    	else if(evt.getSource() == LoadHistory){
    		
			try {
				FantasyManager = new FantasyManagerPanel(currentSummonerDisplayed , this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
    	
    	getParent().revalidate();
    	getParent().repaint();
    	

    }
    
    //Search Summoner in API Riot DataBase
    boolean SearchSummoner(String name,String Region)throws Exception{
    	
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
    	currentSummonerDisplayed = new SummonerInfo();
    	currentSummonerDisplayed.UpdateIconURLVersion();
    	currentSummonerDisplayed.Region = Region;
    	
    	//Search summoner in riot API
    	URL oracle = new URL("https://"+currentSummonerDisplayed.Region+".api.pvp.net/api/lol/"+currentSummonerDisplayed.Region+"/v1.4/summoner/by-name/"+URLEncoder.encode(NoSpaceAndLowerCase(name),"UTF-8")+"?api_key=ff35712f-5c41-43c4-895e-c52f111994d5");
    	HttpURLConnection connection = (HttpURLConnection)oracle.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();
		//Checking response Code
		int code = connection.getResponseCode();
		System.out.println("Search Summoner - code : "+code);
		//All Good
		if(code == 200){    	
	    	BufferedReader in = new BufferedReader(
	        new InputStreamReader(oracle.openStream(),"UTF-8"));
	       
	        String RAW_JSON_DATA = in.readLine();
	        JSONObject SummonerObj = new JSONObject(RAW_JSON_DATA);
	        name = NoSpaceAndLowerCase(name);
	        currentSummonerDisplayed.Name = SummonerObj.getJSONObject(name).getString("name");
	        currentSummonerDisplayed.iconID = SummonerObj.getJSONObject(name).getInt("profileIconId");
	        currentSummonerDisplayed.LVL = SummonerObj.getJSONObject(name).getInt("summonerLevel");
	        currentSummonerDisplayed.ID = SummonerObj.getJSONObject(name).getInt("id");
	        
	        currentSummonerDisplayed.LoadLeagueInfos();
	        currentSummonerDisplayed.LoadStatsSummary();
	
	        in.close();
	        return true;
		}
		else{
			System.out.println("Error !");
			if(code == 400)
				System.out.println("Bad request ");
			else if(code == 401)
				System.out.println("Unauthorized");
			else if(code == 404)
				System.out.println("No summoner data found for any specified inputs");
			else if(code == 429)
				System.out.println("Rate limit exceeded");
			else if(code == 500)
				System.out.println("Internal server error");
			else if(code == 503)
				System.out.println("Service unavailable, try gain later");
			
			JOptionPane.showMessageDialog(new JFrame(), "Summoner not found !");
			return false;
		}
    }
    
    //Format the string to fit the RIOT API Syntax
    @SuppressWarnings("unused")
	private String FormatStringForRequest(String str) {
    	str = str.toLowerCase();
    	str = str.replace(" ", "%20");
    	
    	return str;
		
	}
    
    //Format the string to fit the JSON Object Syntax
    private String NoSpaceAndLowerCase(String str){
    	str = str.toLowerCase();
    	str = str.replace(" ", "");
    	
    	return str;
    	
    }
    
    
    /** Function handling the user data loading from server */
    private void LoadUserDataFromServer(){
    	
    	//open streams
	    PrintWriter output = null;
		try {
			output = new PrintWriter(windowRef.echoSocket.getOutputStream(), true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		 //Notify action type
		if(output != null)
			output.println(Server.Action.LOADINFO.name());
    	
	    InputStream is = null;
		ObjectInputStream ois = null;
		
		//Open object stream
		try {
			is = windowRef.echoSocket.getInputStream();
			ois = new ObjectInputStream(is);
		} catch (IOException e) {
			System.out.println("Error with object streams !");
		}  
		
		//Receiving Object
		try {
			currentUser = (UserInfo)ois.readObject();
		} catch (ClassNotFoundException e1) {
			System.out.println("Error with object received !");
		} catch (IOException e1) {
		}
		
		if(currentUser != null){
			for(int i = 0 ; i < currentUser.favoriteList.size(); i++)
				favoriteManager.AddFavorite(currentUser.favoriteList.get(i));
		}
    	
    }
    
    /** Function handling the user data saving on server */
    private void SaveUserDataOnServer(){
    	
		try {
			//open streams
		    PrintWriter output = new PrintWriter(windowRef.echoSocket.getOutputStream(), true);
		    
		    //Notify action type
		    output.println(Server.Action.SAVE.name());
		    
		    OutputStream os = null;
			ObjectOutputStream oos = null;
			
			//Open object stream
			try {
				os = windowRef.echoSocket.getOutputStream();
				oos = new ObjectOutputStream(os);
			} catch (IOException e) {
				System.out.println("Error with object streams !");
			}  

			//Update favorite
			currentUser.favoriteList = favoriteManager.FavoritePlayers;
			
		    //Send favorite list to server
			try {
				oos.writeObject(currentUser);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error with object sent !");
			} 
			System.out.println("Data sent to server !");
			
		    //LOGOUT
			output = new PrintWriter(windowRef.echoSocket.getOutputStream(), true);
		    output.println(Server.Action.LOGOUT.name());
		
		    //End
		    output.close();
		    windowRef.echoSocket.close();

		}catch(IOException e1){
			e1.printStackTrace();
		}
		
		System.out.println("See you next time !");
    }

	@Override
	public void mouseClicked(MouseEvent e) {
		getParent().revalidate();
    	getParent().repaint();
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		getParent().revalidate();
    	getParent().repaint();
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