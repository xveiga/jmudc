package mudc.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import mudc.core.dataelements.MoodleModule;

import javax.swing.JLabel;
import javax.swing.JTextPane;
import java.awt.Font;
import javax.swing.SpringLayout;

@SuppressWarnings("serial")
public class DetailWindowModule extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblDetails;

	public DetailWindowModule(MoodleModule module) {
		setBounds(0, 0, 480, 320);
		getContentPane().setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setBackground(new Color(255, 255, 255, 255));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);
		{
			lblDetails = new JLabel("Details");
			sl_contentPanel.putConstraint(SpringLayout.NORTH, lblDetails, 0, SpringLayout.NORTH, contentPanel);
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblDetails, 0, SpringLayout.WEST, contentPanel);
			sl_contentPanel.putConstraint(SpringLayout.EAST, lblDetails, 0, SpringLayout.EAST, contentPanel);
			lblDetails.setFont(new Font("Dialog", Font.BOLD, 14));
			lblDetails.setForeground(new Color(32, 32, 32, 255));
			contentPanel.add(lblDetails);
		}
		{
			JTextPane textPane = new JTextPane();
			sl_contentPanel.putConstraint(SpringLayout.NORTH, textPane, 0, SpringLayout.SOUTH, lblDetails);
			sl_contentPanel.putConstraint(SpringLayout.WEST, textPane, 0, SpringLayout.WEST, contentPanel);
			sl_contentPanel.putConstraint(SpringLayout.SOUTH, textPane, 0, SpringLayout.SOUTH, contentPanel);
			sl_contentPanel.putConstraint(SpringLayout.EAST, textPane, 0, SpringLayout.EAST, contentPanel);
			textPane.setEditable(false);
			textPane.setBackground(new Color(255, 255, 255, 255));
			textPane.setForeground(new Color(32, 32, 32, 255));
			textPane.setText("Module name: " + module.name +
					"\nVisible: " + module.visible +
					"\nID: " + module.id +
					"\nInstance: " + module.instance +
					"\nIndent: " + module.indent +
					"\nMod Icon: " + module.modicon +
					"\nMod Name: " + module.modname +
					"\nMod Plural: " + module.modplural +
					"\nURL: " + module.url
					);
			contentPanel.add(textPane);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBackground(new Color(255, 255, 255, 255));
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton closeButton = new JButton("Close");
				closeButton.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e) {
		            	dispose();
		            }
				});
				buttonPane.add(closeButton);
			}
		}
	}

}