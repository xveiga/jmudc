package mudc.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import mudc.core.Locales;
import mudc.core.UpdateComparator;
import mudc.core.dataelements.MoodleElement;

@SuppressWarnings("serial")
public class CardElement extends JPanel {

	private JLabel nameLabel;
	private JLabel detailsLabel;
	private JPanel icon;
	private int iconPack = 0;
	private final static String moodleIconPath = "/mudc/icons/moodle/filetypes/"; // TODO: Change to get from GUI
	private final static String defaultIconPath = "/mudc/icons/basic/filetypes/";
	private Locales strings = null;
	private static String noSizeReported = "";
	private Color backgroundColor = Colors.transparency;
	private Color foregroundColor = Colors.black;
	private boolean drawShadow = false;
	private int shadowSize = 0;
	private int bottomSpacing = 0;
	private MoodleElement element = null;
	private boolean drawDot = false;
	private Color dotColor = new Color(0, 0, 0, 0);
	private float progress = -1;

	public CardElement(GUI core, Locales str, MoodleElement elem) {
		strings = str;
		element = elem;
		noSizeReported = strings.getString("nosizereported");
		iconPack = core.getIconPack();
		progress = element.progress;

		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		// setBorder(new MatteBorder(1, 1, 1, 1, new Color(192, 192, 192, 255)));

		icon = new JPanel();

		if (element.type.equals("url")) {
			loadAppropiateIconForFile(element.fileurl, false);
		} else {
			loadAppropiateIconForFile(element.filename, true);
		}

		springLayout.putConstraint(SpringLayout.NORTH, icon, 4, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.SOUTH, icon, 36, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, icon, 4, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, icon, 36, SpringLayout.WEST, this);
		add(icon);

		nameLabel = new JLabel(element.filename);
		nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		springLayout.putConstraint(SpringLayout.NORTH, nameLabel, 4, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.SOUTH, nameLabel, 20, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, nameLabel, 4, SpringLayout.EAST, icon);
		springLayout.putConstraint(SpringLayout.EAST, nameLabel, -4, SpringLayout.EAST, this);
		add(nameLabel);

		SimpleDateFormat sdf = new SimpleDateFormat(strings.getString("dateformat"));

		Date time = null;
		String timestampText = "";
		String detailsLabelText = "";
		String authorText = "";

		if (element.timecreated > element.timemodified) {
			time = new Date(element.timecreated * 1000L);
			timestampText = strings.getString("created") + " " + sdf.format(time);
		} else {
			time = new Date(element.timemodified * 1000L);
			timestampText = strings.getString("modified") + " " + sdf.format(time);
		}

		/*
		 * if (element.type.equals("file")) { if (element.author != null &&
		 * !element.author.equals("null")) { authorText = "     " +
		 * strings.getString("author") + " " + element.author; } else { authorText =
		 * "     " + strings.getString("noauthor"); } detailsLabelText =
		 * readableFileSize((int) element.filesize) + "     " + timestampText +
		 * authorText; } else
		 */if (element.type.equals("url")) {
			detailsLabelText = element.fileurl + "      " + timestampText;
		} else {
			if (element.author != null && !element.author.equals("null")) {
				authorText = "     " + strings.getString("author") + " " + element.author;
			} else {
				authorText = "     " + strings.getString("noauthor");
			}
			detailsLabelText = readableFileSize((int) element.filesize) + "     " + timestampText + authorText;
		}
		detailsLabel = new JLabel();
		detailsLabel.setText(detailsLabelText);
		detailsLabel.setFont(new Font("Tahoma", Font.PLAIN, 9));
		springLayout.putConstraint(SpringLayout.NORTH, detailsLabel, 24, SpringLayout.NORTH, this);
		// springLayout.putConstraint(SpringLayout.SOUTH, detailsLabel, 34,
		// SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, detailsLabel, 4, SpringLayout.EAST, icon);
		springLayout.putConstraint(SpringLayout.EAST, detailsLabel, -4, SpringLayout.EAST, this);
		add(detailsLabel);

		setStatus();
	}

	private void setStatus() {
		if (!element.isLocal && element.type.equals("file")) {
			drawDot = true;
			dotColor = Colors.cardElementNewDot;
		} else if (element.status == UpdateComparator.STATUS_UPDATED) {
			drawDot = true;
			dotColor = Colors.cardElementUpdatedDot;
		} else if (element.status == UpdateComparator.STATUS_NEW) {
			drawDot = true;
			dotColor = Colors.cardElementNewDot;
		} else {
			drawDot = false;
		}
	}

	private void loadAppropiateIconForFile(String filename, boolean isFile) {
		String[] fileNameParts = filename.split("\\.");
		String fileExtension = fileNameParts[fileNameParts.length - 1];
		switch (iconPack) {
		case 1:

			// MOODLE ICONS
			FileImagePanel imgIcon = new FileImagePanel();

			switch (fileExtension) {

			case "zip":
				imgIcon.loadImage(moodleIconPath + "archive-32.png");
				break;

			case "7z":
				imgIcon.loadImage(moodleIconPath + "archive-32.png");
				break;

			case "gz":
				imgIcon.loadImage(moodleIconPath + "archive-32.png");
				break;

			case "tar":
				imgIcon.loadImage(moodleIconPath + "archive-32.png");
				break;

			case "bz2":
				imgIcon.loadImage(moodleIconPath + "archive-32.png");
				break;

			case "z":
				imgIcon.loadImage(moodleIconPath + "archive-32.png");
				break;

			case "xz":
				imgIcon.loadImage(moodleIconPath + "archive-32.png");
				break;

			case "iso":
				imgIcon.loadImage(moodleIconPath + "archive-32.png");
				break;

			case "rar":
				imgIcon.loadImage(moodleIconPath + "archive-32.png");
				break;

			case "apk":
				imgIcon.loadImage(moodleIconPath + "oth-32.png");
				break;

			case "aac":
				imgIcon.loadImage(moodleIconPath + "audio-32.png");
				break;

			case "ogg":
				imgIcon.loadImage(moodleIconPath + "audio-32.png");
				break;

			case "wma":
				imgIcon.loadImage(moodleIconPath + "audio-32.png");
				break;

			case "flac":
				imgIcon.loadImage(moodleIconPath + "audio-32.png");
				break;

			case "opus":
				imgIcon.loadImage(moodleIconPath + "audio-32.png");
				break;

			case "3gp":
				imgIcon.loadImage(moodleIconPath + "audio-32.png");
				break;

			case "aiff":
				imgIcon.loadImage(moodleIconPath + "audio-32.png");
				break;

			case "avi":
				imgIcon.loadImage(moodleIconPath + "avi-32.png");
				break;

			case "db":
				imgIcon.loadImage(moodleIconPath + "base-32.png");
				break;

			case "dbs":
				imgIcon.loadImage(moodleIconPath + "base-32.png");
				break;

			case "dbf":
				imgIcon.loadImage(moodleIconPath + "base-32.png");
				break;

			case "accdb":
				imgIcon.loadImage(moodleIconPath + "base-32.png");
				break;

			case "mdb":
				imgIcon.loadImage(moodleIconPath + "base-32.png");
				break;

			case "odb":
				imgIcon.loadImage(moodleIconPath + "base-32.png");
				break;

			case "sqlite":
				imgIcon.loadImage(moodleIconPath + "base-32.png");
				break;

			case "sqlite3":
				imgIcon.loadImage(moodleIconPath + "base-32.png");
				break;

			case "sqlitedb":
				imgIcon.loadImage(moodleIconPath + "base-32.png");
				break;

			case "bmp":
				imgIcon.loadImage(moodleIconPath + "bmp-32.png");
				break;

			case "csv":
				imgIcon.loadImage(moodleIconPath + "calc-32.png");
				break;

			case "tsv":
				imgIcon.loadImage(moodleIconPath + "calc-32.png");
				break;

			case "ods":
				imgIcon.loadImage(moodleIconPath + "calc-32.png");
				break;

			case "xl":
				imgIcon.loadImage(moodleIconPath + "calc-32.png");
				break;

			case "xls":
				imgIcon.loadImage(moodleIconPath + "calc-32.png");
				break;

			case "xlsx":
				imgIcon.loadImage(moodleIconPath + "calc-32.png");
				break;

			case "dex":
				imgIcon.loadImage(moodleIconPath + "calc-32.png");
				break;

			case "gsheet":
				imgIcon.loadImage(moodleIconPath + "calc-32.png");
				break;

			case "numbers":
				imgIcon.loadImage(moodleIconPath + "calc-32.png");
				break;

			case "dmg":
				imgIcon.loadImage(moodleIconPath + "dmg-32.png");
				break;

			case "flash":
				imgIcon.loadImage(moodleIconPath + "flash-32.png");
				break;

			case "flv":
				imgIcon.loadImage(moodleIconPath + "flash-32.png");
				break;

			case "swf":
				imgIcon.loadImage(moodleIconPath + "flash-32.png");
				break;

			case "html":
				imgIcon.loadImage(moodleIconPath + "html-32.png");
				break;

			case "gif":
				imgIcon.loadImage(moodleIconPath + "gif-32.png");
				break;

			case "jpeg":
				imgIcon.loadImage(moodleIconPath + "jpeg-32.png");
				break;

			case "xml":
				imgIcon.loadImage(moodleIconPath + "markup-32.png");
				break;

			case "moodle":
				imgIcon.loadImage(moodleIconPath + "moodle-32.png");
				break;

			case "mp3":
				imgIcon.loadImage(moodleIconPath + "mp3-32.png");
				break;

			case "mpeg":
				imgIcon.loadImage(moodleIconPath + "mpeg-32.png");
				break;

			case "mp4":
				imgIcon.loadImage(moodleIconPath + "mpeg-32.png");
				break;

			case "pdf":
				imgIcon.loadImage(moodleIconPath + "pdf-32.png");
				break;

			case "png":
				imgIcon.loadImage(moodleIconPath + "png-32.png");
				break;

			case "pps":
				imgIcon.loadImage(moodleIconPath + "powerpoint-32.png");
				break;

			case "ppsx":
				imgIcon.loadImage(moodleIconPath + "powerpoint-32.png");
				break;

			case "ppt":
				imgIcon.loadImage(moodleIconPath + "powerpoint-32.png");
				break;

			case "pptm":
				imgIcon.loadImage(moodleIconPath + "powerpoint-32.png");
				break;

			case "pptx":
				imgIcon.loadImage(moodleIconPath + "powerpoint-32.png");
				break;

			case "psd":
				imgIcon.loadImage(moodleIconPath + "psd-32.png");
				break;

			case "c":
				imgIcon.loadImage(moodleIconPath + "sourcecode-32.png");
				break;

			case "cc":
				imgIcon.loadImage(moodleIconPath + "sourcecode-32.png");
				break;

			case "cs":
				imgIcon.loadImage(moodleIconPath + "sourcecode-32.png");
				break;

			case "cpp":
				imgIcon.loadImage(moodleIconPath + "sourcecode-32.png");
				break;

			case "cxx":
				imgIcon.loadImage(moodleIconPath + "sourcecode-32.png");
				break;

			case "h":
				imgIcon.loadImage(moodleIconPath + "sourcecode-32.png");
				break;

			case "hpp":
				imgIcon.loadImage(moodleIconPath + "sourcecode-32.png");
				break;

			case "py":
				imgIcon.loadImage(moodleIconPath + "sourcecode-32.png");
				break;

			case "java":
				imgIcon.loadImage(moodleIconPath + "sourcecode-32.png");
				break;

			case "js":
				imgIcon.loadImage(moodleIconPath + "sourcecode-32.png");
				break;

			case "ino":
				imgIcon.loadImage(moodleIconPath + "sourcecode-32.png");
				break;

			case "tex":
				imgIcon.loadImage(moodleIconPath + "sourcecode-32.png");
				break;

			case "bas":
				imgIcon.loadImage(moodleIconPath + "sourcecode-32.png");
				break;

			case "pl":
				imgIcon.loadImage(moodleIconPath + "sourcecode-32.png");
				break;

			case "m":
				imgIcon.loadImage(moodleIconPath + "markup-32.png");
				break;

			case "txt":
				imgIcon.loadImage(moodleIconPath + "text-32.png");
				break;

			default:
				if (isFile) {
					imgIcon.loadImage(moodleIconPath + "unknown-32.png");
				}
				else {
					//TODO: load moodle url icon
					imgIcon.loadImage(moodleIconPath + "oth-32.png");
				}

			}
			icon = (JPanel) imgIcon;
			icon.setVisible(true);
			break;

		default:
			// DEFAULT ICONS (+ AUTOMATICALLY GENERATED ICONS)
			if (fileExtension.equals("pdf")) {
				FileImagePanel imgPanel = new FileImagePanel();
				imgPanel.loadImage(defaultIconPath + "pdf-32.png");
				icon = (JPanel) imgPanel;
			} else if (fileExtension.equals("txt")) {
				FileImagePanel imgPanel = new FileImagePanel();
				imgPanel.loadImage(defaultIconPath + "txt-32.png");
				icon = (JPanel) imgPanel;
			} else if (fileExtension.equals("html")) {
				FileImagePanel imgPanel = new FileImagePanel();
				imgPanel.loadImage(defaultIconPath + "html-32.png");
				icon = (JPanel) imgPanel;
			} else if (fileNameParts.length == 1 && isFile) {
				FileImagePanelDynamicIcon dynamicIcon = new FileImagePanelDynamicIcon();
				dynamicIcon.loadImage(defaultIconPath + "template-32.png", "UNKNOWN", new Color(255, 128, 0, 255));
				icon = (JPanel) dynamicIcon;
			} else if (isFile) {
				FileImagePanelDynamicIcon dynamicIcon = new FileImagePanelDynamicIcon();
				dynamicIcon.loadImage(defaultIconPath + "template-32.png", fileExtension, new Color(0, 32, 192, 255));
				icon = (JPanel) dynamicIcon;
			}
			else {
				FileImagePanelDynamicIcon dynamicIcon = new FileImagePanelDynamicIcon();
				dynamicIcon.loadImage(defaultIconPath + "template-32.png", "UNKNOWN", new Color(0, 192, 0, 255));
				icon = (JPanel) dynamicIcon;
			}
		}
	}

	private static String readableFileSize(int size) {
		if (size <= 0) {
			return noSizeReported;
		}
		final String[] units = new String[] { "Bytes", "kB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	@Override
	public void setBackground(Color c) {
		backgroundColor = c;
	}

	@Override
	public void setForeground(Color c) {
		foregroundColor = c;
	}

	public void setBottomSpacing(int size) {
		bottomSpacing = size;
	}

	public int getTotalContentHeight() {
		return (int) 40 + shadowSize + bottomSpacing;
	}

	public void setDrawShadow(boolean state) {
		drawShadow = state;
		if (drawShadow) {
			shadowSize = Dimensions.shadowSize;
		} else {
			shadowSize = 0;
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Dimension arcs = Dimensions.cardElementBorderArc; // Border arc size
		int bWidth = getWidth() - 1;
		int bHeight = getHeight() - 1;
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int shadowHeight = bHeight - bottomSpacing;
		// Drop shadow
		if (drawShadow) {
			for (int i = 0; i < shadowSize; i++) {
				int shadow = i * Dimensions.shadowStrength;
				g2d.setColor(new Color(0, 0, 0, shadow));
				g2d.drawRoundRect(0, 0, bWidth, shadowHeight - i, arcs.width, arcs.height);
			}
		}
		int finalHeight = shadowHeight - shadowSize;
		
		// Draws the panel background
		g2d.setColor(backgroundColor);
		g2d.fillRoundRect(0, 0, bWidth, finalHeight, arcs.width, arcs.height); // paint background
		
		// Draws the background progress bar
		if (progress >= 0 && progress < 1) {
			g2d.setColor(Colors.cardElementDownloadProgressBackground);
			g2d.fillRoundRect(0, 0, Math.round(bWidth*progress), finalHeight, arcs.width, arcs.height);
		}
		if (progress > 1) {
			g2d.setColor(Colors.cardElementIntegrityProgressBackground);
			g2d.fillRoundRect(0, 0, Math.round(bWidth*(progress-1f)), finalHeight, arcs.width, arcs.height);
		}
		
		// Draws the rounded panel with borders.
		g2d.setColor(foregroundColor);
		g2d.drawRoundRect(0, 0, bWidth, finalHeight, arcs.width, arcs.height); // paint border
		// "New" dot
		if (drawDot) {
			g2d.setColor(dotColor);
			g2d.fillOval(36, 4, 8, 8);
		}
	}
}
