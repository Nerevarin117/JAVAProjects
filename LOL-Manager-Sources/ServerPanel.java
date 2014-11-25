import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

/**
 * @author Maxime GILLET
 */

public class ServerPanel extends JFrame implements ActionListener{

	/**
	 *  Default ID
	 */
	private static final long serialVersionUID = 1L;
	private Server servRef;
	public JButton[] deleteUser;
	public JButton[] editUser;
	private JLabel[] status;
	private JScrollPane spane;
	ServerPanel(Server  ref){
		
		new JFrame();
		setTitle("LOL-Manager-Server-Administration");
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		createAndShowGUI(ref) ;
		
		addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent e) {
	        	servRef.SaveUserListInFile();
	        	dispose();
	        	System.exit(0);
			}
		});
		
	}
	
	/** Display server window */
	private void createAndShowGUI(Server ref) {
		
		servRef = ref;
		setIconImage(new ImageIcon(this.getClass().getResource("Resources/server.png")).getImage());
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content,BoxLayout.Y_AXIS));
		JPanel containerTab = new JPanel();
		content.add(new JLabel("User List : "));
		deleteUser = new JButton[servRef.user.size()];
		editUser = new JButton[servRef.user.size()];
		status = new JLabel[servRef.user.size()];
		for(int i = 0; i < servRef.user.size(); i++){
			
			//Instantiation
			deleteUser[i] = new JButton(" Delete User ");
			deleteUser[i].addActionListener(this);
			editUser[i] = new JButton(" Edit User ");
			editUser[i].addActionListener(this);
			status[i] = new JLabel("             •  ");
			if(servRef.user.get(i).online)
				status[i].setForeground(Color.GREEN);
			else status[i].setForeground(Color.RED);
			
			JPanel usermanager = new JPanel(new GridLayout(1,0));
			usermanager.setPreferredSize(new Dimension(300,42));
			usermanager.setMaximumSize(new Dimension(480,42));
			usermanager.setBackground(Color.WHITE);
			//Decoration
			Border raisedbevel = BorderFactory.createRaisedBevelBorder();
			Border loweredbevel = BorderFactory.createLoweredBevelBorder();
			Border thinBorder = BorderFactory.createCompoundBorder(raisedbevel, loweredbevel);
			usermanager.setBorder(thinBorder);
			//Add components
			usermanager.add(new JLabel("  " + servRef.user.get(i).getUsername()));
			usermanager.add(status[i]);
			usermanager.add(editUser[i]);
			usermanager.add(deleteUser[i]);
			content.add(usermanager);
		}
		spane = new JScrollPane(containerTab);
		BoxLayout ColumnLayout = new BoxLayout(containerTab,BoxLayout.Y_AXIS);
		containerTab.setLayout(ColumnLayout);
		containerTab.add(content);
		spane.setPreferredSize(new Dimension(500,300));
		setContentPane(spane);
		pack();
		setVisible(true);
		
	}
	
	/** Remake window */
	public void updateServerPanel(){
		
		createAndShowGUI( servRef);
		
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		// TODO Auto-generated method stub
		for(int i =0; i<deleteUser.length; i++)
			if(ev.getSource() == deleteUser[i]){
				servRef.user.remove(i);
				updateServerPanel();
			}
		
		for(int i =0; i<editUser.length; i++)
			if(ev.getSource() == editUser[i]){
				new UserEditorPanel(servRef.user.get(i),servRef, this , i);
				editUser[i].setEnabled(false);
				deleteUser[i].setEnabled(false);
			}
	}
	
	/** Update User status (Online/Offline) */
	public void updateStatus(){
		for(int i = 0; i < servRef.user.size(); i++){
			if(servRef.user.get(i).online)
				status[i].setForeground(Color.GREEN);
			else status[i].setForeground(Color.RED);
		}
		revalidate();
		repaint();
		
	}
	
	/** Set all status to offline */
	public void resetStatus(){
		for(int i = 0; i < servRef.user.size(); i++){
			status[i].setForeground(Color.RED);
		}
		revalidate();
		repaint();
	}
}
