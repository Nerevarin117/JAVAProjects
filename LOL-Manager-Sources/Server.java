import java.io.*;
import java.net.*;
import java.util.ArrayList;


/**
 * @author Maxime GILLET
 *
 */
public class Server implements Runnable{
	
	public final static int PORT = 5000 ;
	public final static String HOSTNAME = "localhost" ;
	public static enum Action {LOGIN, CREATEUSER , LOADINFO, SAVE , LOGOUT};
	public ArrayList<UserInfo> user;
	private boolean UserListcorrupted = false;
	private ServerPanel panelRef;
	ServerSocket serveur ;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Server();
	}

	public Server() {
		
		System.out.println("Start Main Server...");

		try {
			serveur = new ServerSocket(PORT) ;
			new Thread(this).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		LoadUserListFromFile();
		
		if(UserListcorrupted)
			user = new ArrayList<UserInfo>();
		
		int index = checkUserInfo("admin");
		if(index == -1)
			user.add(new UserInfo("admin","admin"));
		
		panelRef = new ServerPanel(this);
		panelRef.resetStatus();
	}
	

	@Override
	public void run() {
		Socket sock;
		String threadName = "default";
		while(true){
			try {
				sock = serveur.accept();
				new Thread(this).start();
				System.out.println("<start new thread socket>");
				PrintWriter output = null;
				BufferedReader input = null;
				Action act = null;

				do{
					//open streams
					output = new PrintWriter(sock.getOutputStream(), true);
					input = new BufferedReader( new InputStreamReader(sock.getInputStream()));
					
					
					//Read action 
					try{
						act = Action.valueOf(input.readLine()) ; //Read request type
					}catch(IOException e){
						e.printStackTrace();
					}
					
					switch (act) {
					
						case LOGIN :
							threadName = checkLogin(input,output);
							break ;
						case CREATEUSER :
							registerNewUser(sock, output);
							break;
						case LOADINFO :
							LoadUserData(sock , threadName);
							break;
						case SAVE :
							SaveUserData(sock);
							break ;
						case LOGOUT :
							break ;
					}
				} while (act!= Action.LOGOUT) ;

				//End
		        output.close();
			    input.close();
				sock.close();
				System.out.println("<end thread -"+threadName+"- socket>");


			} catch (IOException e) {
				System.out.println("Error with socket");
				e.printStackTrace();		
			}
			
		}

	}
	
	/** Add new User */
	private void registerNewUser(Socket sock, PrintWriter output){
		
		System.out.println("Registering new user...");
	    InputStream is = null;
		ObjectInputStream ois = null;
		
		//Open object stream
		try {
			is = sock.getInputStream();
			ois = new ObjectInputStream(is);
		} catch (IOException e) {
			System.out.println("Error with object streams !");
		}  
		UserInfo temp = null;
		try {
			temp = (UserInfo)ois.readObject();
		} catch (ClassNotFoundException e) {
			System.out.println("Error with object received !");
		} catch (IOException e) {
		}
		//Check if user is already in list and get its position
		if(temp != null){
			int index = checkUserInfo(temp);
			if(index != -1){
				output.println("taken");
			}
			else{
				user.add(temp);
				output.println("ok");
				panelRef.updateServerPanel();
			}
		}
		else output.println("other error");
		
		
		
	}
	
	/** Check Login Request */
	private String checkLogin(BufferedReader in , PrintWriter out){
		
		 System.out.println("check login ...");
		
		 //Read in socket
	     String username = null;
		 String password = null;
		 
		try {
			username = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			password = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		 //check log info
		 if (username != null && password != null && CheckUser(username,password)) {
		      out.println("ok");
		      System.out.println(username+" has logged in.");
		      user.get(checkUserInfo(username)).online = true;
		      panelRef.updateStatus();
		      
		 } else {
		      out.println(0);
		 }
		 
		 if(username != null)
			 return username;
		 else return "default";
		
	}
	
	/** Check User correct info */
	private boolean CheckUser(String name, String pw ){
		
		for(int i = 0 ; i < user.size(); i++){
			
			if(user.get(i).getUsername().equals(name))
				if(user.get(i).getPassword().equals(pw))
					return true;
		}
		
		return false;
	}
	
	/** Load user data to its session */
	private void LoadUserData(Socket sock, String threadName){
		
		System.out.println("sending user info ...");
	    OutputStream os = null;
		ObjectOutputStream oos = null;
		
		//Open object stream
		try {
			os = sock.getOutputStream();
			oos = new ObjectOutputStream(os);
		} catch (IOException e) {
			System.out.println("Error with object streams !");
		}  

		//Check if user is already in list and get its position
		int index = checkUserInfo(threadName);
		if(index != -1){
			 //Send favorite list to client
			try {
				oos.writeObject(user.get(index));
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error with object sent !");
			} 
			System.out.println("Data sent to client !");
		}
		else System.out.println("Data is missing !");		
		
	}
	
	/** Save user data from its session */
	private void SaveUserData(Socket sock){
		
		System.out.println("receiving data ...");
		
		InputStream is = null;
		ObjectInputStream ois = null;
		
		//Open object stream
		try {
			is = sock.getInputStream();  
			ois = new ObjectInputStream(is);
		} catch (IOException e) {
			System.out.println("Error with object stream !");
		}  

		//Receiving Object
		UserInfo temp = null;
		try {
			temp = (UserInfo)ois.readObject();
		} catch (ClassNotFoundException e1) {
			System.out.println("Error with object received !");
		} catch (IOException e1) {
		}
		
		if(temp != null){
			
			//Check if user is already in list and get its position
			int index = checkUserInfo(temp);
			if(index != -1)
				user.remove(index);
			//Add the new info
			user.add(index,temp);
			user.get(index).online = false;
			panelRef.updateStatus();
			System.out.println("data saved !");
			
		}
	
	}
	
	/** Check if username is in the list of users and return its index */
	private int checkUserInfo(UserInfo temp){
		
		for(int i = 0 ; i < user.size() ; i++){
			
			if(user.get(i).getUsername().equals(temp.getUsername()))
				return i;
			
		}
		
		return -1;
	}
	
	/** Overload previous function*/
	private int checkUserInfo(String username){
		
		for(int i = 0 ; i < user.size() ; i++){
			
			if(user.get(i).getUsername().equals(username))
				return i;
			
		}
		
		return -1;
	}
	
	/** Save the user info list in a local file */
	public void SaveUserListInFile(){
		
		try{
		  //use buffering
		  OutputStream file = new FileOutputStream("users.ser");
		  OutputStream buffer = new BufferedOutputStream(file);
		  ObjectOutput output = new ObjectOutputStream(buffer);
		  try{
			//serialize the list
		    output.writeObject(user);
		  }
		  finally{
		    output.close();
		  }
		}  
		catch(IOException ex){
		  ex.printStackTrace();
		}
		
		System.out.println("User data list saved in a local file !");
	}
	
	/** Load the user info list from a local file */
	@SuppressWarnings("unchecked")
	private void LoadUserListFromFile(){
		
	  
		InputStream file = null;
		try {
			file = new FileInputStream("users.ser");
		} catch (FileNotFoundException e) {
			System.out.println("File users.ser is missing !");
			try {
				new FileOutputStream("users.ser");
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	  
		
	  try{
	      //use buffering
	      file = new FileInputStream("users.ser");
	      InputStream buffer = new BufferedInputStream(file);
	      ObjectInput input = new ObjectInputStream (buffer);
	      try{
	        //deserialize the List
	        user = (ArrayList<UserInfo>)input.readObject();
	      }
	      finally{
	        input.close();
	      }
	    }
	    catch(ClassNotFoundException ex){
	    	UserListcorrupted = true;
	    }
	    catch(IOException ex){
	    	System.out.println("File users.ser is empty !");
	    	UserListcorrupted = true;
	    }
	  
	  System.out.println("User data list loaded from local file !");
	  

	}
}