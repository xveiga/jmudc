package mudc.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import mudc.core.Locales;
import mudc.core.UpdateComparator;
import mudc.core.dataelements.MoodleCategory;
import mudc.core.dataelements.MoodleElement;
import mudc.core.dataelements.MoodleModule;

public class FileTreeCellRenderer implements TreeCellRenderer {

	private GUI core;
	private JPanel treePanel;
	private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
	private Color backgroundSelectionColor;
	private Color backgroundNonSelectionColor;
	private Locales strings = null;
	private boolean drawShadows = false;

	public FileTreeCellRenderer(GUI coreSystem, Locales str) {
		core = coreSystem;
		strings = str;
		backgroundSelectionColor = new Color(200, 218, 235, 255);
		backgroundNonSelectionColor = Colors.cardElementBackground;
	}

	public void setShadowsEnabled(boolean state) {
		drawShadows = state;
	}

	public boolean getShadowsEnabled() {
		return drawShadows;
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {

		Component returnValue = null;

		if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
			Object userObject = ((DefaultMutableTreeNode) value).getUserObject();

			if (userObject instanceof MoodleElement) {

				MoodleElement element = (MoodleElement) userObject;
				CardElement cardElement = new CardElement(core, strings, element);
				cardElement.setDrawShadow(drawShadows);
				cardElement.setBottomSpacing(Dimensions.cardElementBottomSpacing);
				treePanel = (JPanel) cardElement;

				if (selected) {
					treePanel.setBackground(backgroundSelectionColor);
					treePanel.setForeground(new Color(128, 128, 128, 255));
				} else {
					treePanel.setForeground(new Color(224, 224, 224, 255));
					if (element.status == UpdateComparator.STATUS_DELETED) {
						treePanel.setBackground(Colors.cardElementDeletedBackground);
					}
				}
				treePanel.repaint();
				treePanel.setEnabled(tree.isEnabled());
				treePanel
						.setPreferredSize(new Dimension(600/* tree.getWidth() */, cardElement.getTotalContentHeight()));
				returnValue = treePanel;
			} else if (userObject instanceof MoodleModule) {

				MoodleModule element = (MoodleModule) userObject;
				CardModule cardModule = new CardModule(element);
				// CardModule castPanel = (CardModule) treePanel;
				cardModule.setDrawShadow(drawShadows);
				cardModule.setBottomSpacing(Dimensions.cardModuleBottomSpacing);
				treePanel = (JPanel) cardModule;

				if (selected) {
					treePanel.setForeground(new Color(128, 128, 128, 255));
					treePanel.setBackground(backgroundSelectionColor);
				} else {
					treePanel.setForeground(new Color(224, 224, 224, 255));
					treePanel.setBackground(backgroundNonSelectionColor);
				}
				treePanel.setEnabled(tree.isEnabled());
				treePanel.setPreferredSize(new Dimension(600/* tree.getWidth() */, cardModule.getTotalContentHeight()));
				// TODO: Find ways to scale width to window as it is resized.

				returnValue = treePanel;
			} else if (userObject instanceof MoodleCategory) {

				MoodleCategory category = (MoodleCategory) userObject;
				treePanel = new CardCategory(category);
				CardCategory cardCategory = (CardCategory) treePanel;
				cardCategory.setDrawShadow(drawShadows);
				cardCategory.setBottomSpacing(Dimensions.cardCategoryBottomSpacing);

				if (selected) {
					treePanel.setForeground(new Color(128, 128, 128, 255));
					treePanel.setBackground(backgroundSelectionColor);
				} else {
					treePanel.setForeground(new Color(224, 224, 224, 255));
					treePanel.setBackground(backgroundNonSelectionColor);
				}
				treePanel.setEnabled(tree.isEnabled());
				treePanel.setPreferredSize(
						new Dimension(600/* tree.getWidth() */, cardCategory.getTotalContentHeight()));

				returnValue = treePanel;
			} else if (userObject instanceof String) {
				String str = (String) userObject;
				CardString card = new CardString(str);
				treePanel = (CardString) card;
				if (selected) {
					treePanel.setBackground(backgroundSelectionColor);
				} else {
					treePanel.setBackground(backgroundNonSelectionColor);
				}
				treePanel.setEnabled(tree.isEnabled());
				treePanel.setPreferredSize(new Dimension(600/* tree.getWidth() */, card.getTotalContentHeight()));
				returnValue = treePanel;
			}
		}

		if (returnValue == null) { // Use default renderer if object is not defined here.
			returnValue = defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row,
					hasFocus);
		}
		return returnValue;
	}
}
