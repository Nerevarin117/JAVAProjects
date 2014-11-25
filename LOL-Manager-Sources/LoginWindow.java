import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;

/**
 * @author Maxime GILLET (projet de plus de 2500 lignes au 16/10/2014)
 *
 */

public final class LoginWindow extends JFrame implements ActionListener{
	
	/**
	 * Default ID
	 */
	
    private static volatile LoginWindow instance = null;
    
	private static final long serialVersionUID = 1L;
	
	  /** Utility components */
	  private JLabel Login;
	  private JLabel Password;
	  private JTextField loginField;
	  private JPasswordField pwField;
	  private JButton LogButton;
	  private JButton createAccount;
	  
	  /** Container of the other components */
	  private JPanel pan;
	  /** Background image and first level panel */
	  private ImagePanel bckg = null;
	  /** Client */
	  public Socket echoSocket;
	  
	  /** Default Constructor */
	  private LoginWindow(){
		  super();
		  createAndShowGUI();
	  }
	  
	  /** return LoginPanel Instance */
	  public final static LoginWindow getInstance() { return instance; }
	  
	  /** Set up the window */
	  public void createAndShowGUI(){
		  
		  //pan setting
		  pan = new JPanel();
		  pan.setLayout(new FlowLayout());
		  pan.setBackground(new Color(0,0,0,0));
		  pan.setPreferredSize(new Dimension (225,200));
		  
		  //Component Settings
		  Login = new JLabel("Login : ");
		  loginField = new JTextField(20);
		  Password = new JLabel("Password : ");
		  pwField = new JPasswordField(20);
		  pwField.addActionListener(this);
		  LogButton = new JButton("Login !");
		  LogButton.addActionListener(this);
		  createAccount = new JButton("Create Account");
		  createAccount.addActionListener(this);
		  
		  setResizable(false);
		  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		  
		  //import background image and set content
		  bckg = new ImagePanel("Resources/riven2.jpg");
		  Insets insets = getInsets();
		  Dimension size = pan.getPreferredSize();
		  pan.add(Login);
		  pan.add(loginField);
		  pan.add(Password);
		  pan.add(pwField);
		  pan.add(LogButton);
		  pan.add(createAccount);
		  pan.setBounds(700 + insets.left, 200 + insets.top,  size.width, size.height);
		  bckg.add(pan);
		  
		  //setup and Display the window.  

		  if(Toolkit.getDefaultToolkit().getScreenSize().width < 1230 && Toolkit.getDefaultToolkit().getScreenSize().height < 730){
			  setSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width-30, Toolkit.getDefaultToolkit().getScreenSize().height-30));
			  
		  }else{
			  setSize(new Dimension(1215, 717));
		  }
		  
		  add(bckg);
		  setTitle("LOL-Manager-Launcher");
		  setIconImage(new ImageIcon(this.getClass().getResource("Resources/icon.png")).getImage());
		  setVisible(true);
	  }
	  
	  public static void main(String[] args) {
	        //Schedule a job for the event dispatch thread:
	        //creating and showing this application's GUI.
	        javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	            	instance = new LoginWindow();

	            }
	        });
	   }

	//Load main Panel component
	private void LoadMainPanel(){
	
		bckg = new ImagePanel("Resources/yasuo.jpg");
	    bckg.setLayout(new FlowLayout());
	    
	    bckg.add(new MainPanel(this));
	    setTitle("League-Of-Legend-Manager-Client-version : beta");

        setContentPane(bckg);
		revalidate();
		repaint();
	}
	
	//Load Create Account Panel
	private void LoadCreateAccount(){
		
		bckg = new ImagePanel("Resources/cutRyze.jpg");
	    bckg.setLayout(new BorderLayout());
	    
	    bckg.add(new CreateAccountPanel(this),BorderLayout.WEST);

        setContentPane(bckg);
		setSize(new Dimension(626, 717));
		revalidate();
		repaint();
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent source) {
		// TODO Auto-generated method stub
		if(source.getSource() == LogButton || source.getSource() == pwField){
			
			LogButton.setEnabled(false);
			createAccount.setEnabled(false);
			
			String name = loginField.getText(); 
			String pw = String.valueOf(pwField.getPassword());
			String response =  null;
			boolean serverDown = false;
			if(!name.isEmpty() && !pw.isEmpty()){
				
				//open socket
				 try {
						echoSocket = new Socket(Server.HOSTNAME, Server.PORT);
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (IOException e) {
						JOptionPane.showMessageDialog(new JFrame(), "Server failed to answer !");
						serverDown = true;
					}
				if(!serverDown){
					try {
						//open streams
					    PrintWriter output = new PrintWriter(echoSocket.getOutputStream(), true);
					    BufferedReader input = new BufferedReader( new InputStreamReader(echoSocket.getInputStream()));
					    
					    //Notify action type
					    output.println(Server.Action.LOGIN.name());
					    
					    //Send login info to server
					    output.println(name);
					    output.println(pw);
					    
					    //Waiting for server response
					    do{
					    	try{
					    		response = input.readLine();
					    	}catch(IOException e){
					    		e.printStackTrace();
					    		
					    	}
					    	
					    }while (response == null); // response received
					    
					    //Checking response value
					    if(response.equals("ok")){
					    	System.out.println("Welcome !");
						    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
							LoadMainPanel();
							
					    }else{
					    	JOptionPane.showMessageDialog(new JFrame(), "Invalid Username and/or password !");
					    }
					    
					    output.flush();
			
					}catch(IOException e){
						e.printStackTrace();
					}
				}
				
				
			}
			
	    	loginField.setText("");
	    	pwField.setText("");
			LogButton.setEnabled(true);		
			createAccount.setEnabled(true);	

		}
		if(source.getSource() == createAccount){
			
			LoadCreateAccount();
			
		}
		
	}

}
