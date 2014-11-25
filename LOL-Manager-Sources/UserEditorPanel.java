import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 * @author Maxime GILLET
 */

public class UserEditorPanel extends JFrame implements ActionListener{

	/**
	 * Default ID
	 */
	private static final long serialVersionUID = 1L;
	private UserInfo editedUser;
	private int userIndex;
	private Server servRef;
	private ServerPanel servPanRef;
	private JPanel content;
	
	private JTextField username;
	private JTextField password;
	private JTextField APIKey;
	private JTextField summoner;
	private JTextField region;
	
	private JLabel usernameLab;
	private JLabel passwordLab;
	private JLabel APIKeyLab;
	private JLabel summonerLab;
	private JLabel regionLab;
	
	private JButton save;
	
	/** Constructor */
	UserEditorPanel(UserInfo user, Server ref, ServerPanel panRef, int i){
		userIndex = i;
		servPanRef = panRef;
		servRef = ref;
		editedUser = user;
		
		new JFrame();
		setTitle("User Editor Panel");
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		createAndShowGUI();
		
		addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent e) {
	        	servPanRef.deleteUser[userIndex].setEnabled(true);
				servPanRef.editUser[userIndex].setEnabled(true);
	        	dispose();
			}
		});
	}

	/** setup and display interface */
	private void createAndShowGUI(){
				
		//Instanciate et set up content	
		content = new JPanel(new GridLayout(0,2));
		
		usernameLab = new JLabel(" Username : ");
		username = new JTextField(20);
		username.setText(editedUser.getUsername());
		
		passwordLab = new JLabel(" Password : ");
		password = new JTextField(16);
		password.setText(editedUser.getPassword());
		
		APIKeyLab = new JLabel(" APIKey : ");
		APIKey = new JTextField(36);
		APIKey.setText(editedUser.getAPIKey());
		
		summonerLab = new JLabel(" Summoner Name : ");
		summoner = new JTextField(20);
		summoner.setText(editedUser.getSummonerName());
		
		regionLab = new JLabel(" Region : ");
		region = new JTextField(5);
		region.setText(editedUser.getRegion());
		
		content.add(usernameLab);
		content.add(username);
		content.add(passwordLab);
		content.add(password);
		content.add(APIKeyLab);
		content.add(APIKey);
		content.add(summonerLab);
		content.add(summoner);
		content.add(regionLab);
		content.add(region);
		
		save = new JButton("Save");
		save.addActionListener(this);
		content.add(save);
		
		//window setup
		setContentPane(content);
		pack();
		setSize(300,150);
		setVisible(true);
		
		
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		if(ev.getSource() == save){
			servRef.user.remove(editedUser);
			
			if(!username.getText().isEmpty())
				editedUser.setUsername(username.getText());
			if(!password.getText().isEmpty())
				editedUser.setPassword(password.getText());
			if(!APIKey.getText().isEmpty())
				editedUser.setAPIKey(APIKey.getText());
			if(!summoner.getText().isEmpty())
				editedUser.setSummonerName(summoner.getText());
			if(!region.getText().isEmpty())
				editedUser.setRegion(region.getText());
			
			servRef.user.add(editedUser);
			servPanRef.deleteUser[userIndex].setEnabled(true);
			servPanRef.editUser[userIndex].setEnabled(true);
			dispose();
			servPanRef.updateServerPanel();
			
		}
	}
}
