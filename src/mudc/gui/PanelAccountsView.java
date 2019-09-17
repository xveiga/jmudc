package mudc.gui;

import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import mudc.core.Locales;
import mudc.core.casudc.MoodleToken;
import mudc.core.casudc.MoodleTokenData;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class PanelAccountsView extends JPanel {

	private static final long serialVersionUID = 573959158202390198L;
	
	private JList<MoodleToken> tokenList = null;

	public PanelAccountsView(PanelAccounts p, GUI g, Window w, Locales str) {

		setLayout(new BorderLayout(Dimensions.accountsPanelMargin, Dimensions.accountsPanelMargin));
		setBackground(new Color(255, 255, 255, 255));
		
		
		JPanel contentPanel = new JPanel();
		contentPanel.setBackground(new Color(255, 255, 255, 255));
		contentPanel.setLayout(null);
		add(contentPanel, BorderLayout.CENTER);
		
		JLabel titleText = new JLabel();
		titleText.setFont(new Font("Tahoma", Font.PLAIN, 20));
		titleText.setText("Choose security key");
		titleText.setHorizontalAlignment(JLabel.CENTER);
		titleText.setVerticalAlignment(JLabel.CENTER);
		contentPanel.add(titleText);
		
		JLabel descriptionText = new JLabel();
		descriptionText.setFont(new Font("Tahoma", Font.PLAIN, 12));
		descriptionText.setText("<html>We've found those Moodle security keys within your account. Select which one you want to use. If you are unsure, just try the first one and see if it works.</html>");
		descriptionText.setHorizontalAlignment(JLabel.CENTER);
		descriptionText.setVerticalAlignment(JLabel.CENTER);
		contentPanel.add(descriptionText);
		
		JLabel footerText = new JLabel();
		footerText.setFont(new Font("Tahoma", Font.PLAIN, 10));
		footerText.setText("<html>The security key allows this application to work without ever having to store your password on this computer.</html>");
		footerText.setHorizontalAlignment(JLabel.CENTER);
		footerText.setVerticalAlignment(JLabel.CENTER);
		contentPanel.add(footerText);
		
		tokenList = new JList<MoodleToken>();
		tokenList.setCellRenderer(new ListRendererToken());
		tokenList.setFixedCellHeight(48);
		tokenList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		contentPanel.add(tokenList);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(new Color(255, 255, 255, 255));
		FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		add(buttonPanel, BorderLayout.SOUTH);
		
		JButton btnPrev = new CustomJButton();
		btnPrev.setText("Cancel");
		buttonPanel.add(btnPrev);
		
		JButton btnNext = new CustomJButton();
		btnNext.setText("Save");
		buttonPanel.add(btnNext);
		
		ComponentAdapter componentAdapter = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				titleText.setBounds(0, 0, contentPanel.getWidth(), 24);
				descriptionText.setBounds(16, 28, contentPanel.getWidth()-32, 32);
				footerText.setBounds(8, contentPanel.getHeight()-24, contentPanel.getWidth()-16, 24);
				tokenList.setBounds(16, 64, getWidth()-32, getHeight()-92);
			}
		};
		addComponentListener(componentAdapter);
		
		btnNext.addActionListener(new ActionListener () {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				p.setToken(tokenList.getSelectedValue());
			}
			
		});
		btnPrev.addActionListener(new ActionListener () {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				w.dispose();
			}
			
		});
		componentAdapter.componentResized(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
	}
	
	public void requestFocus() {
		tokenList.requestFocusInWindow();
	}
	
	public void setTokenData(MoodleTokenData data) {
		List<MoodleToken> tokens = data.getTokens();
		DefaultListModel<MoodleToken> model = new DefaultListModel<MoodleToken>();
		if (tokens != null && tokens.size() != 0) {
			for (MoodleToken token : tokens) {
				model.addElement(token);
			}
		}
		tokenList.setModel(model);
		tokenList.validate();
		if (tokens.size() > 0) {
			tokenList.setSelectedIndex(0);
		}
		tokenList.repaint();
	}
}
