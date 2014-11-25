import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.jdesktop.xswingx.PromptSupport;

/**
 * @author Maxime GILLET
 *
 */

public class CreateAccountPanel extends JPanel implements ActionListener , MouseListener{
	
	/**
	 *  Default ID
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel content;
	private JButton submit;
	private JButton cancel;
	
	//Field information
	private JTextField username;
	private JPasswordField pw;
	private JPasswordField pw2;
	private JTextField APIKey;
	
	//Label information
	private JLabel usernameLab;
	private JLabel pwLab;
	private JLabel pw2Lab;
	private JLabel APIKeyLab;
	private JLabel APILink;
	
	//Temporary profile
	private UserInfo tempUser;
	
	//URL Link
	private URI uri = null;
	
	//Frame reference
	private LoginWindow parentRef;
	
	/** Constructor */
	CreateAccountPanel(LoginWindow ref){	
		
		try {
			uri = new URI("https://developer.riotgames.com/sign-in");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parentRef = ref;
		
		new JPanel();
		setLayout(new BorderLayout());
		setBackground(new Color(0,0,0,0));
		
		//Instantiate
		content = new JPanel();
		
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		
		submit = new JButton("Submit !");
		submit.addActionListener(this);
		
		username = new JTextField(20);
		pw = new JPasswordField(20);
		pw2 = new JPasswordField(20);
		APIKey = new JTextField(25);
		
		//Set hints of textfields
		username.setDocument(new JTextFieldLimit(20));
		username.addMouseListener(this);
		PromptSupport.setPrompt("6-20 characters ...", username);
		PromptSupport.setFontStyle(2, username);
		username.setToolTipText(null);
		
		pw.setDocument(new JTextFieldLimit(16));
		pw.addMouseListener(this);
		PromptSupport.setPrompt("6-16 characters ...", pw);
		PromptSupport.setFontStyle(2, pw);
		pw.setToolTipText(null);
		
		pw2.setDocument(new JTextFieldLimit(16));
		pw2.addMouseListener(this);
		PromptSupport.setPrompt("6-16 characters ...", pw2);
		PromptSupport.setFontStyle(2, pw2);
		pw2.setToolTipText(null);

		APIKey.setDocument(new JTextFieldLimit(36));
		
		//Set label values
		usernameLab = new JLabel("<html><font color= white face='Verdana'>Username : </font></html>");
		pwLab = new JLabel("<html><font color= white face='Verdana'>Password : </font></html>");
		pw2Lab = new JLabel("<html><font color= white face='Verdana'>Confirm Password : </font></html>");
		APIKeyLab = new JLabel("<html><font color= white face='Verdana'>Riot API Key : </font></html>");
		APILink = new JLabel("<HTML><FONT color= white ><U>https://developer.riotgames.com/sign-in</U></FONT></HTML>");
		APILink.addMouseListener(this);
        
		//Background Container settings
		JPanel bck = new JPanel();
		bck.setBackground(new Color(121,190,219,100));
		bck.setPreferredSize(new Dimension(619,717));
		Border raisedbevel = BorderFactory.createRaisedBevelBorder();
		Border loweredbevel = BorderFactory.createLoweredBevelBorder();
		Border thinBorder = BorderFactory.createCompoundBorder(raisedbevel, loweredbevel);
		bck.setBorder(thinBorder);
		
		//Content Panel settings
		content.setBackground(new Color(0,0,0,0));
		content.setPreferredSize(new Dimension(300,270));
		content.setLayout(new FlowLayout());

		//Adding components to content
		content.add(usernameLab);
		content.add(username);

		content.add(pwLab);
		content.add(pw);

		content.add(pw2Lab);
		content.add(pw2);

		content.add(APIKeyLab);
		content.add(APIKey);
		content.add(APILink);
		
		content.add(cancel);
		content.add(submit);
		
		//Layout setting and constraint
		bck.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.PAGE_END;
		bck.add(content, c);
		add(bck,BorderLayout.CENTER);

	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		// TODO Auto-generated method stub
		if(ev.getSource() == submit){
			
			String message = checkInfos();
			System.out.println("infos check !");
			if(message.equals("ok")){
				message = newUserUpdate(tempUser);
				System.out.println("user data check and registered!");
				if(message.equals("ok")){
					System.out.println("back to login !");
					parentRef.remove(this);
					parentRef.createAndShowGUI();
					
				}
				else{
					content.add(new JLabel("<HTML><FONT color= #992020 >User not Created : "+message+"</HTML>"));
					System.out.println("User not Created : "+message);
					parentRef.revalidate();
					parentRef.repaint();
				}
				
			}
			else{
				content.add(new JLabel("<HTML><FONT color= #992020 >User not Created : "+message+"</HTML>"));
				System.out.println("User not Created : "+message);
				parentRef.revalidate();
				parentRef.repaint();
			}
			
			
		}
		else if(ev.getSource() == cancel){
			parentRef.remove(this);
			parentRef.createAndShowGUI();
		}
	}
	
	/** Check fields correspondence and verify API Key */
	private String checkInfos(){
		
		String errorCode = null;
		
		errorCode = checkFieldsLength();
		if(!errorCode.equals("ok"))
			return errorCode;
		
		errorCode = checkPassword();
		if(!errorCode.equals("ok"))
			return errorCode;
		
		tempUser = new UserInfo(username.getText(), String.valueOf(pw.getPassword()));
		if(!tempUser.tryAPIKey(APIKey.getText()))
			return "Incorrect API Key";
		
		return "ok";
		
	}
	
	/** Check the field length constraints */
	private String checkFieldsLength(){
		String temp = username.getText();
		if(temp.length() < 6 || temp.length() > 20)
			return "Error with username Length";
		 temp = String.valueOf(pw.getPassword());
		 if(temp.length() < 6 || temp.length() > 16)
			 return "Error with password Length";
		 temp = APIKey.getText();
		 if(temp.length() != 36)
			 return "Error with APIKey Length";	
		 
		 return "ok";
	}
	
	/** Check password coherence */
	private String checkPassword(){
		if(!String.valueOf(pw.getPassword()).equals(String.valueOf(pw2.getPassword()))){
			return "Password does not match";
		}
		
		return "ok";		
	}
	
	/** check username availability and update user list on server side */
	private String newUserUpdate(UserInfo newUser){
		
		boolean serverDown = false;
		String response = null;
		
		//open socket
		 try {
				parentRef.echoSocket = new Socket(Server.HOSTNAME, Server.PORT);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				serverDown = true;
			}
		if(!serverDown){
			try {
				//open streams
			    PrintWriter output = new PrintWriter(parentRef.echoSocket.getOutputStream(), true);
			    BufferedReader input = new BufferedReader( new InputStreamReader(parentRef.echoSocket.getInputStream()));
			    
			    //Notify action type
			    output.println(Server.Action.CREATEUSER.name());
			    
			    //Send new user info to server
			    OutputStream os = null;
				ObjectOutputStream oos = null;
				
				//Open object stream
				try {
					os = parentRef.echoSocket.getOutputStream();
					oos = new ObjectOutputStream(os);
				} catch (IOException e) {
					System.out.println("Error with object streams !");
				}  

				oos.writeObject(newUser);
				System.out.println("new user sent to server !");
			    //Waiting for server response
			    do{
			    	try{
			    		response = input.readLine();
			    	}catch(IOException e){
			    		e.printStackTrace();
			    		
			    	}
			    	
			    }while (response == null); // response received
			    
			    System.out.println("response recieved !");
			    //Checking response value
			    if(response.equals("ok")){
			    	JOptionPane.showMessageDialog(new JFrame(), "Account created successfully!");
					return "ok";
					
			    }else if(response.equals("taken")){
			    	return "Usename is already in use .";
			    }
			    else{
			    	return "Unexpected error, try again later.";
			    	
			    }
			    
	
			}catch(IOException e){
				e.printStackTrace();
			}

		}
		return "Server failed to answer .";

	}
	
	/** Open url in internet navigator */
	private void open(URI uri) {
	    if (Desktop.isDesktopSupported()) {
	      try {
	        Desktop.getDesktop().browse(uri);
	      } catch (IOException e) { /* TODO: error handling */ }
	    } else { /* TODO: error handling */ }
	  }

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == APILink){
			
			open(uri);
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		parentRef.repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		setCursor(null);
		parentRef.repaint();
		
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
