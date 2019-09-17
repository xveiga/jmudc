package mudc.gui;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import mudc.core.Locales;
import mudc.core.MetadataStorage;
import mudc.core.ProgressBinding;
import mudc.core.ProgressUpdater;
import mudc.core.UpdateComparator;
import mudc.core.dataelements.MoodleCategory;
import mudc.core.dataelements.MoodleCourse;
import mudc.core.dataelements.MoodleElement;
import mudc.core.dataelements.MoodleModule;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class PanelFiles extends JPanel {

	private TabCourses courseTab = null;
	private GUI gui = null;
	private JTree courseContentsTree = null;
	private MoodleCourse rootCourse = null;
	private Locales strings = null;
	private boolean shadowsEnabled = false;
	private ToolbarFiles toolbarPanel = null;
	private PanelFiles thisInstance = null;
	private DefaultTreeModel courseContentsTreeModel = null;
	private FileTreeCellRenderer renderer = null;

	public PanelFiles(GUI g, Locales str, TabCourses tab, MoodleCourse course) {

		thisInstance = this;
		gui = g;
		courseTab = tab;

		rootCourse = course;
		strings = str;

		shadowsEnabled = g.getShadowsEnabled();

		setLayout(null);

		setBackground(new Color(255, 255, 255, 255));

		toolbarPanel = new ToolbarFiles(gui, strings, this, course);

		add(toolbarPanel);

		JScrollPane scrollPanel = new JScrollPane();
		scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPanel.setBackground(new Color(255, 255, 255, 255));
		scrollPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		scrollPanel.getVerticalScrollBar().setUnitIncrement(24); // TODO: Save in settings
		scrollPanel.getVerticalScrollBar().setUI(new CustomScrollBar());
		scrollPanel.getVerticalScrollBar().setPreferredSize(new Dimension(16, -1));
		
		courseContentsTree = new JTree();
		courseContentsTree.setShowsRootHandles(true);
		courseContentsTree.setRootVisible(false);
		renderer = new FileTreeCellRenderer(gui, strings);
		renderer.setShadowsEnabled(shadowsEnabled);
		courseContentsTree.setCellRenderer(renderer);

		JPopupMenu filePopup = new JPopupMenu();
		filePopup.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuCanceled(PopupMenuEvent arg0) {
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
				filePopup.removeAll();
			}

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				if (courseContentsTree.getSelectionCount() > 0) {
					int status = 0x00000000; // Four first bits represent status. First bit: element present. Second
												// bit: module present. Third bit: category present. Fourth bit: more
												// than one item selected.

					if (courseContentsTree.getSelectionPaths().length > 1) {
						status = status | (1 << 3);
					}

					List<DefaultMutableTreeNode> selectedNodesList = new ArrayList<DefaultMutableTreeNode>();
					for (TreePath path : courseContentsTree.getSelectionPaths()) {
						DefaultMutableTreeNode elementNode = (DefaultMutableTreeNode) path.getLastPathComponent();
						selectedNodesList.add(elementNode);

						if (elementNode.getUserObject() instanceof MoodleElement) {
							status = status | 1;
						} else if (elementNode.getUserObject() instanceof MoodleModule) {
							status = status | (1 << 1);
						} else if (elementNode.getUserObject() instanceof MoodleCategory) {
							status = status | (1 << 2);
						}
					}

					// Construct proper popup for each selection combination
					switch (status) {

					case 0x00000001:

						DefaultMutableTreeNode node = selectedNodesList.get(0);
						MoodleElement element = (MoodleElement) node.getUserObject();

						if (element.type.equals("file")) { // TODO: Check if file is being downloaded, show "stop" option.
							if (element.isLocal) {
								JMenuItem openItem = new JMenuItem(strings.getString("open"));
								openItem.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent arg0) {
										if (Desktop.isDesktopSupported()) {
											try {
												// TODO: do this properly, check if file exists.
												Desktop.getDesktop().open(new File(element.localPath));
											} catch (IOException e) {
												e.printStackTrace();
											}
										}
									}
								});
								filePopup.add(openItem);

								JMenuItem elementItem = new JMenuItem(strings.getString("redownload")); 
								elementItem.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent arg0) {
										DefaultMutableTreeNode moduleNode = (DefaultMutableTreeNode) node.getParent();
										DefaultMutableTreeNode categoryNode = (DefaultMutableTreeNode) moduleNode
												.getParent();
										Runnable onFinishSuccessful = new Runnable() {
											@Override
											public void run() {
												element.progress = -1;
												element.isLocal = true;
												element.status = UpdateComparator.STATUS_NORMAL;
												courseContentsTreeModel.nodeChanged(node);
											}
										};
										Runnable onFinishFail = new Runnable() {
											@Override
											public void run() {
												element.progress = -1;
												element.isLocal = false;
												courseContentsTreeModel.nodeChanged(node);
											}
										};
										ProgressUpdater updater = gui.downloadFile(false, onFinishSuccessful, onFinishFail, rootCourse,
												(MoodleCategory) categoryNode.getUserObject(),
												(MoodleModule) moduleNode.getUserObject(), element);
										ProgressBinding progress = new ProgressBinding(new Runnable() {
											@Override
											public void run() {
												element.progress = updater.getProgress();
												courseContentsTreeModel.nodeChanged(node);
											}
										});
										updater.registerBinding(progress);
									}
								});
								filePopup.add(elementItem);

								filePopup.addSeparator();

								JMenuItem folderItem = new JMenuItem(strings.getString("openfolder"));
								folderItem.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent arg0) {
										if (Desktop.isDesktopSupported()) {
											try {
												Desktop.getDesktop().open(MetadataStorage
														.getRelativeFile(element.localPath).getParentFile());
												element.status = UpdateComparator.STATUS_NORMAL;
											} catch (IOException | URISyntaxException e) {
												e.printStackTrace();
											}
										} else {
											// TODO: Throw error, desktop not supported
										}
									}
								});
								filePopup.add(folderItem);

								JMenuItem checksumItem = new JMenuItem(strings.getString("checkintegrity"));
								checksumItem.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent arg0) {
										DefaultMutableTreeNode moduleNode = (DefaultMutableTreeNode) node.getParent();
										DefaultMutableTreeNode categoryNode = (DefaultMutableTreeNode) moduleNode
												.getParent();
										Runnable onFinishSuccessful = new Runnable() {
											@Override
											public void run() {
												element.progress = -1;
												element.status = UpdateComparator.STATUS_NORMAL;
												courseContentsTreeModel.nodeChanged(node);
												/*JDialog checkDialog = new JDialog();
												checkDialog.setVisible(true);*/
											}
										};
										Runnable onFinishFail = new Runnable() {
											@Override
											public void run() {
												element.progress = -1;
												element.status = UpdateComparator.STATUS_UPDATED;
												courseContentsTreeModel.nodeChanged(node);
												/*JDialog checkDialog = new JDialog();
												checkDialog.setVisible(true);*/
											}
										};
										ProgressUpdater updater = gui.checkIntegrity(onFinishSuccessful, onFinishFail, rootCourse,
												(MoodleCategory) categoryNode.getUserObject(),
												(MoodleModule) moduleNode.getUserObject(), element);
										ProgressBinding progress = new ProgressBinding(new Runnable() {
											@Override
											public void run() {
												element.progress = updater.getProgress()+1;
												courseContentsTreeModel.nodeChanged(node);
											}
										});
										updater.registerBinding(progress);
									}
								});
								filePopup.add(checksumItem);
								filePopup.addSeparator();

								JMenuItem elementCopyPathItem = new JMenuItem(strings.getString("copypath"));
								elementCopyPathItem.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent arg0) {
										try {
											StringSelection stringSelection = new StringSelection(
													MetadataStorage.getRelativeFile(element.localPath).toString());
											Clipboard systemClipboard = Toolkit.getDefaultToolkit()
													.getSystemClipboard();
											systemClipboard.setContents(stringSelection, null);
										} catch (URISyntaxException e) {
											e.printStackTrace();
										}
									}
								});
								filePopup.add(elementCopyPathItem);

							} else {

								JMenuItem downloadopenItem = new JMenuItem(strings.getString("downloadandopen"));
								downloadopenItem.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent arg0) {
										DefaultMutableTreeNode moduleNode = (DefaultMutableTreeNode) node.getParent();
										DefaultMutableTreeNode categoryNode = (DefaultMutableTreeNode) moduleNode
												.getParent();
										Runnable onFinishSuccessful = new Runnable() {
											@Override
											public void run() {
												element.progress = -1;
												element.isLocal = true;
												element.status = UpdateComparator.STATUS_NORMAL;
												courseContentsTreeModel.nodeChanged(node);
											}
										};
										Runnable onFinishFail = new Runnable() {
											@Override
											public void run() {
												element.progress = -1;
												element.isLocal = false;
												courseContentsTreeModel.nodeChanged(node);
											}
										};
										ProgressUpdater updater = gui.downloadFile(true, onFinishSuccessful, onFinishFail, rootCourse,
												(MoodleCategory) categoryNode.getUserObject(),
												(MoodleModule) moduleNode.getUserObject(), element);
										ProgressBinding progress = new ProgressBinding(new Runnable() {
											@Override
											public void run() {
												element.progress = updater.getProgress();
												courseContentsTreeModel.nodeChanged(node);
											}
										});
										updater.registerBinding(progress);
									}
								});
								filePopup.add(downloadopenItem);

								JMenuItem downloadItem = new JMenuItem(strings.getString("download"));
								downloadItem.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent arg0) {
										DefaultMutableTreeNode moduleNode = (DefaultMutableTreeNode) node.getParent();
										DefaultMutableTreeNode categoryNode = (DefaultMutableTreeNode) moduleNode
												.getParent();
										Runnable onFinishSuccessful = new Runnable() {
											@Override
											public void run() {
												element.progress = -1;
												element.isLocal = true;
												element.status = UpdateComparator.STATUS_NORMAL;
												courseContentsTreeModel.nodeChanged(node);
											}
										};
										Runnable onFinishFail = new Runnable() {
											@Override
											public void run() {
												element.progress = -1;
												element.isLocal = false;
												courseContentsTreeModel.nodeChanged(node);
											}
										};
										ProgressUpdater updater = gui.downloadFile(false, onFinishSuccessful, onFinishFail, rootCourse,
												(MoodleCategory) categoryNode.getUserObject(),
												(MoodleModule) moduleNode.getUserObject(), element);
										ProgressBinding progress = new ProgressBinding(new Runnable() {
											@Override
											public void run() {
												element.progress = updater.getProgress();
												courseContentsTreeModel.nodeChanged(node);
											}
										});
										updater.registerBinding(progress);
									}
								});
								filePopup.add(downloadItem);

								filePopup.add(new JSeparator());
							}

						} else if (element.type.equals("url")) {
							JMenuItem elementItem = new JMenuItem(strings.getString("openurl"));
							elementItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent arg0) {
									if (Desktop.isDesktopSupported()) {
										try {
											Desktop.getDesktop().browse(new URI(element.fileurl));
											element.status = UpdateComparator.STATUS_NORMAL;
											courseContentsTreeModel.nodeChanged(node);
										} catch (IOException | URISyntaxException e1) {
											e1.printStackTrace();
										}
									} else {
										// TODO: Throw error, desktop not supported
									}
								}
							});
							filePopup.add(elementItem);
							filePopup.addSeparator();

							JMenuItem elementCopyURLItem = new JMenuItem(strings.getString("copyurl"));
							elementCopyURLItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent arg0) {
									StringSelection stringSelection = new StringSelection(element.fileurl);
									Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
									systemClipboard.setContents(stringSelection, null);
									element.status = UpdateComparator.STATUS_NORMAL;
									courseContentsTreeModel.nodeChanged(node);
								}
							});
							filePopup.add(elementCopyURLItem);
						}

						JMenuItem elementCopyItem = new JMenuItem(strings.getString("copyname"));
						elementCopyItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								StringSelection stringSelection = new StringSelection(element.filename);
								Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
								systemClipboard.setContents(stringSelection, null);
							}
						});
						filePopup.add(elementCopyItem);
						filePopup.addSeparator();

						JMenuItem elementDetailsItem = new JMenuItem(strings.getString("details"));
						elementDetailsItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								// TODO: Open details window
								DetailWindowFile details = new DetailWindowFile(element);
								details.setVisible(true);
							}
						});
						filePopup.add(elementDetailsItem);

						break;

					case 0x00000002:

						MoodleModule module = (MoodleModule) selectedNodesList.get(0).getUserObject();

						JMenuItem moduleItem = new JMenuItem(strings.getString("copyname"));
						moduleItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								StringSelection stringSelection = new StringSelection(module.name);
								Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
								systemClipboard.setContents(stringSelection, null);
							}
						});
						filePopup.add(moduleItem);

						JMenuItem moduleDescItem = new JMenuItem(strings.getString("copydeschtml"));
						moduleDescItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								StringSelection stringSelection = new StringSelection(module.description);
								Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
								systemClipboard.setContents(stringSelection, null);
							}
						});
						filePopup.add(moduleDescItem);

						filePopup.add(new JSeparator());

						JMenuItem moduleDetailsItem = new JMenuItem(strings.getString("details"));
						moduleDetailsItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								// TODO: Open details window
								DetailWindowModule details = new DetailWindowModule(module);
								details.setVisible(true);
							}
						});
						filePopup.add(moduleDetailsItem);

						break;

					case 0x000004:

						MoodleCategory category = (MoodleCategory) selectedNodesList.get(0).getUserObject();

						JMenuItem categoryItem = new JMenuItem(strings.getString("copyname"));
						categoryItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								StringSelection stringSelection = new StringSelection(category.name);
								Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
								systemClipboard.setContents(stringSelection, null);
							}
						});
						filePopup.add(categoryItem);

						if (category.summary != null && !category.summary.equals("")) {

							String categorySummaryItemText = "";
							if (category.summaryformat == 1) {
								categorySummaryItemText = strings.getString("copysummaryhtml");
							} else if (category.summaryformat == 0) {
								categorySummaryItemText = strings.getString("copysummarytxt");
							} else {
								categorySummaryItemText = strings.getString("copysummaryunknown");
							}

							JMenuItem categorySummaryItem = new JMenuItem(categorySummaryItemText);
							categorySummaryItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent arg0) {
									StringSelection stringSelection = new StringSelection(category.summary);
									Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
									systemClipboard.setContents(stringSelection, null);
								}
							});
							filePopup.add(categorySummaryItem);
						}

						filePopup.add(new JSeparator());

						JMenuItem categoryDetailsItem = new JMenuItem(strings.getString("details"));
						categoryDetailsItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								// TODO: Open details window
								DetailWindowCategory details = new DetailWindowCategory(category);
								details.setVisible(true);
							}
						});
						filePopup.add(categoryDetailsItem);

						break;

					case 0x00000009:

						JMenuItem elementMultipleItem = new JMenuItem(strings.getString("downloadfiles"));
						elementMultipleItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								// TODO: Add files to download queue
							}
						});
						filePopup.add(elementMultipleItem);

						break;

					case 0x0000000C:
						JMenuItem multipleFileDownloadItem = new JMenuItem(strings.getString("downloadselected"));
						multipleFileDownloadItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {

							}
						});
						filePopup.add(multipleFileDownloadItem);
						break;

					case 0x0000000B:
					case 0x0000000D:
					case 0x0000000F: {
						JMenuItem mixedFileDownloadItem = new JMenuItem(strings.getString("downloadselected"));
						mixedFileDownloadItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {

							}
						});
						filePopup.add(mixedFileDownloadItem);

						filePopup.add(new JSeparator());

						JMenuItem mixedMultipleSelectItem = new JMenuItem(strings.getString("selectfilesonly"));
						mixedMultipleSelectItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								// TODO: Select only MoodleElement objects whose type is "file"
							}
						});
						filePopup.add(mixedMultipleSelectItem);

						break;
					}

					default:
						JMenuItem noactionItem = new JMenuItem(strings.getString("noaction"));
						noactionItem.setEnabled(false);
						filePopup.add(noactionItem);
					}
				}
			}
		});
		courseContentsTree.setComponentPopupMenu(filePopup);
		courseContentsTree.setBackground(Colors.panelFilesBackground);

		scrollPanel.setViewportView(courseContentsTree);

		add(scrollPanel);

		ComponentAdapter componentAdapter = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				toolbarPanel.setBounds(4, 4, getWidth() - 8, 32);
				scrollPanel.setBounds(0, 36, getWidth(), getHeight() - toolbarPanel.getHeight() - 4);
				toolbarPanel.validate();
				scrollPanel.validate();
			}
		};
		addComponentListener(componentAdapter);
		componentAdapter.componentResized(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));

		gui.registerCourseContentsPanel(thisInstance);

		List<MoodleCategory> categories = gui.getCourseContents(rootCourse);
		onRefresh(categories);
	}

	public void onRefresh(List<MoodleCategory> categories) {
		DefaultMutableTreeNode courseNode = new DefaultMutableTreeNode(rootCourse);
		if (categories != null && categories.size() > 0) {
			for (MoodleCategory category : categories) {
				DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(category);
				if (category.moduleList != null) {
					for (MoodleModule module : category.moduleList) {
						DefaultMutableTreeNode moduleNode = new DefaultMutableTreeNode(module);
						if (module.elementList != null) {
							for (MoodleElement element : module.elementList) {
								DefaultMutableTreeNode elementNode = new DefaultMutableTreeNode(element);
								elementNode.setAllowsChildren(false);
								moduleNode.add(elementNode);
							}
						}
						categoryNode.add(moduleNode);
					}
				}
				courseNode.add(categoryNode);
			}
		} else {
			DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(
					new String(strings.getString("nocoursecontents")));
			courseNode.add(categoryNode);
		}
		courseContentsTreeModel = new DefaultTreeModel(courseNode, true);
		courseContentsTree.setModel(courseContentsTreeModel);

		for (int i = 0; i < courseContentsTree.getRowCount(); i++) {
			courseContentsTree.expandRow(i);
		}
	}

	public void expandAllNodes() {
		for (int i = 0; i < courseContentsTree.getRowCount(); i++) {
			courseContentsTree.expandRow(i);
		}
	}

	public void collapseAllNodes() {
		for (int i = 0; i < courseContentsTree.getRowCount(); i++) {
			courseContentsTree.collapseRow(i);
		}
	}

	public void focus() {
		courseContentsTree.requestFocusInWindow();
	}

	public void backCourses() {
		courseTab.changeToCoursesPanel();
		gui.unregisterComponent(thisInstance);
	}

	public void onPropertiesChanged() {
		shadowsEnabled = gui.getShadowsEnabled();
		renderer.setShadowsEnabled(shadowsEnabled);
		toolbarPanel.onPropertiesChanged();
		courseContentsTreeModel.reload();
		expandAllNodes();
	}
	
	public boolean getShadowsEnabled() {
		return shadowsEnabled;
	}

	public MoodleCourse getCourse() {
		return rootCourse;
	}
}
