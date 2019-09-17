package mudc.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import mudc.core.Locales;
import mudc.core.ProgressBinding;
import mudc.core.Task;

@SuppressWarnings("serial")
public class ListRendererTask extends JPanel implements ListCellRenderer<Task> {
	private JLabel lblName = null;
	private JLabel lblDescription = null;
	private JLabel btnOpen = null;
	private JProgressBar progressBar = null;
	private Locales strings = null;

	public ListRendererTask(Locales str) {

		super();
		
		strings = str;

		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		btnOpen = new JLabel(strings.getString("cancel"), SwingConstants.CENTER);
		springLayout.putConstraint(SpringLayout.NORTH, btnOpen, 8, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, btnOpen, -64, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, btnOpen, -8, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, btnOpen, -8, SpringLayout.EAST, this);
		btnOpen.setForeground(new Color(64, 64, 64, 255));
		btnOpen.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(128, 128, 128, 255)));
		add(btnOpen);

		lblName = new JLabel();
		lblName.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblName.setForeground(new Color(64, 64, 64, 255));
		springLayout.putConstraint(SpringLayout.NORTH, lblName, 4, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblName, 8, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, lblName, -8, SpringLayout.WEST, btnOpen);
		add(lblName);

		lblDescription = new JLabel();
		lblDescription.setFont(new Font("Dialog", Font.PLAIN, 10));
		lblDescription.setForeground(new Color(64, 64, 64, 255));
		springLayout.putConstraint(SpringLayout.NORTH, lblDescription, 4, SpringLayout.SOUTH, lblName);
		springLayout.putConstraint(SpringLayout.WEST, lblDescription, 20, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, lblDescription, -16, SpringLayout.WEST, btnOpen);
		//add(lblDescription);
		
		progressBar = new JProgressBar(0, 100);
		springLayout.putConstraint(SpringLayout.NORTH, progressBar, 4, SpringLayout.SOUTH, lblName);
		springLayout.putConstraint(SpringLayout.WEST, progressBar, 20, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, progressBar, -16, SpringLayout.WEST, btnOpen);
		add(progressBar);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Task> list, Task task, int index,
			boolean isSelected, boolean cellHasFocus) {

		lblName.setText(task.name);
		String text = "";
		if (task.run) {
			text += "Running.    ";
		} else {
			text += "Stopped.    ";
		}
		if (task.progress == null) {
			progressBar.setIndeterminate(true);
			progressBar.setStringPainted(false);
		}
		else {
			progressBar.setIndeterminate(false);
			progressBar.setStringPainted(true);
			task.progress.registerBinding(new ProgressBinding(new Runnable() {
				@Override
				public void run() {
					progressBar.setValue(Math.round(task.progress.getProgress()*100));
					progressBar.repaint();
				}}));
		}
		if (task.stalled) {
			text += "Stalled until other tasks on group " + task.group + " finish.";
		}
		lblDescription.setText(text);

		if (isSelected) {
			this.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(64, 64, 64, 255)));
			this.setBackground(new Color(200, 218, 235, 255));
		} else {
			this.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(64, 64, 64, 255)));
			this.setBackground(new Color(255, 255, 255, 255));
		}
		return this;
	}

}
