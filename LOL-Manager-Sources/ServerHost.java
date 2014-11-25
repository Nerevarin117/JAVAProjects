import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;


public class ServerHost {
	
	public final static int PORT = 5000 ;
	public final static String HOSTNAME = "localhost" ;
	public static enum Action {LOGIN, LOADINFO, SAVE , LOGOUT};
	private static ArrayList<UserInfo> user;
	private static boolean UserListcorrupted = false;
	private static JFrame serverWindow;
	
	// The server socket.
	private static ServerSocket serverSocket = null;
	// The client socket.
	private static Socket clientSocket = null;

	// This chat server can accept up to maxClientsCount clients' connections.
	private static final int maxClientsCount = 10;
	private static final ServerWorker[] threads = new ServerWorker[maxClientsCount];

	public static void main(String args[]) {

		/*
		 * Open a server socket on the portNumber (default 2222). Note that we can
		 * not choose a port less than 1023 if we are not privileged users (root).
		 */
		try {
		  serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
		  System.out.println(e);
		}
		
		
		LoadUserListFromFile();
		
		if(UserListcorrupted)
			user = new ArrayList<UserInfo>();
		
		int index = checkUserInfo("admin");
		if(index == -1)
			user.add(new UserInfo("admin","admin"));
		
		createAndShowGUI();
	}
	
	/** Overload previous function*/
	private static int checkUserInfo(String username){
		
		for(int i = 0 ; i < user.size() ; i++){
			
			if(user.get(i).getUsername().equals(username))
				return i;
			
		}
		
		return -1;
	}
	
	/** Display server window */
	private static void createAndShowGUI() {
		
		serverWindow = new JFrame();
		serverWindow.setTitle("LOL-Manager-Server-Administration");
		serverWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		JPanel content = new JPanel();
		content.add(new JLabel("User List : "));
		for(int i = 0; i < user.size(); i++){
			
			content.add(new JLabel(user.get(i).getUsername()));
		}
		
		serverWindow.setContentPane(content);
		serverWindow.pack();
		serverWindow.setVisible(true);
		
		serverWindow.addWindowListener(new WindowAdapter() {
		        public void windowClosing(WindowEvent e) {
		        	SaveUserListInFile();
		        	serverWindow.dispose();
		        	System.exit(0);
				}
		 });
		
		
		/*
		 * Create a client socket for each connection and pass it to a new client
		 * thread.
		 */
		while (true) {
		  try {
		    clientSocket = serverSocket.accept();
		    int i = 0;
		    for (i = 0; i < maxClientsCount; i++) {
		      if (threads[i] == null) {
		        (threads[i] = new ServerWorker(clientSocket, threads)).start();
		        break;
		      }
		    }
		    if (i == maxClientsCount) {
		      PrintStream os = new PrintStream(clientSocket.getOutputStream());
		      os.println("Server too busy. Try later.");
		          os.close();
		          clientSocket.close();
		    }
	      } catch (IOException e) {
	        System.out.println(e);
	      }
		}
	}
	
	/** Save the user info list in a local file */
	private static void SaveUserListInFile(){
		
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
	private static void LoadUserListFromFile(){
		
	  
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
