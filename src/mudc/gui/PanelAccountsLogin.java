package mudc.gui;

import javax.swing.JPanel;

import mudc.core.Locales;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

public class PanelAccountsLogin extends JPanel {

	private static final long serialVersionUID = 573959158202390198L;
	private JTextField usernameField = null;
	private JPasswordField passwordField = null;
	private JLabel lblErrorlabel = null;

	public PanelAccountsLogin(PanelAccounts p, GUI g, Window w, Locales str) {

		setLayout(new BorderLayout(Dimensions.accountsPanelMargin, Dimensions.accountsPanelMargin));
		setBackground(new Color(255, 255, 255, 255));
		
		JPanel contentPanel = new JPanel();
		contentPanel.setBackground(new Color(255, 255, 255, 255));
		contentPanel.setLayout(null);
		add(contentPanel, BorderLayout.CENTER);
		
		JLabel titleText = new JLabel();
		contentPanel.add(titleText);
		titleText.setFont(new Font("Tahoma", Font.BOLD, 20));
		titleText.setText("Enter your username and password to continue");
		titleText.setHorizontalAlignment(JLabel.CENTER);
		titleText.setVerticalAlignment(JLabel.CENTER);
		
		JLabel lblDescription = new JLabel("<html>%appname% needs your login credentials to authenticate with UDC's Central Authentication System</html>");
		lblDescription.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblDescription.setHorizontalAlignment(JLabel.CENTER);
		lblDescription.setVerticalAlignment(JLabel.CENTER);
		contentPanel.add(lblDescription);
		
		lblErrorlabel = new JLabel();
		lblErrorlabel.setForeground(new Color(255, 64, 64, 255));
		lblErrorlabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblErrorlabel.setHorizontalAlignment(JLabel.CENTER);
		lblErrorlabel.setVerticalAlignment(JLabel.CENTER);
		contentPanel.add(lblErrorlabel);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setFont(new Font("Tahoma", Font.BOLD, 14));
		contentPanel.add(lblUsername);
		
		usernameField = new JTextField();
		usernameField.setFont(new Font("Tahoma", Font.PLAIN, 14));
		usernameField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				passwordField.requestFocusInWindow();
			}
		});
		contentPanel.add(usernameField);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Tahoma", Font.BOLD, 14));
		contentPanel.add(lblPassword);
		
		ActionListener nextAction = new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				p.getTokens();
				p.nextSlide();
				passwordField.setText(null);
			}
		};
		
		passwordField = new JPasswordField();
		passwordField.setFont(new Font("Tahoma", Font.PLAIN, 14));
		passwordField.addActionListener(nextAction);
		contentPanel.add(passwordField);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(new Color(255, 255, 255, 255));
		FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		add(buttonPanel, BorderLayout.SOUTH);
		
		JButton btnPrev = new CustomJButton();
		btnPrev.setText("Cancel");
		buttonPanel.add(btnPrev);
		
		JButton btnNext = new CustomJButton();
		btnNext.setText("Next");
		buttonPanel.add(btnNext);
		btnNext.addActionListener(nextAction);
		btnPrev.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				w.dispose();
			}
		});
		
		ComponentAdapter componentAdapter = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				titleText.setBounds(0, 12, contentPanel.getWidth(), 24);
				lblDescription.setBounds(16, 40, contentPanel.getWidth()-32, 32);
				lblErrorlabel.setBounds(16, 76, getWidth()-32, getHeight()/2-96);
				lblUsername.setBounds(getWidth()/4, getHeight()/2-16, getWidth()/4, 24);
				lblPassword.setBounds(getWidth()/4, getHeight()/2+16, getWidth()/4, 24);
				int width = 0;
				if (lblUsername.getPreferredSize().getWidth() > lblPassword.getPreferredSize().getWidth()) {
					width = (int) Math.round(lblUsername.getPreferredSize().getWidth());
				} else {
					width = (int) Math.round(lblPassword.getPreferredSize().getWidth());
				}
				usernameField.setBounds(getWidth()/2-getWidth()/4+width+8, getHeight()/2-16, getWidth()/2-width-8, 24);
				passwordField.setBounds(getWidth()/2-getWidth()/4+width+8, getHeight()/2+16, getWidth()/2-width-8, 24);
				usernameField.validate();
			}
		};
		addComponentListener(componentAdapter);
		componentAdapter.componentResized(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
	}
	
	public void requestFocus() {
		usernameField.requestFocusInWindow();
	}
	
	public void setError(String error) {
		lblErrorlabel.setText("<html>" + error + "</html>");
	}
	
	public String getUsername() {
		return usernameField.getText();
	}
	
	public char[] getPassword() {
		return passwordField.getPassword();
	}
}
