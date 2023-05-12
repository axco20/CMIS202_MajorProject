import java.util.Stack;
import java.util.HashSet;
import java.util.Map;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class FunkoPopCollectionGUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FunkoPopCollection collection;
	private ArrayList<FunkoPop> currentItems;
	private JTable table;
	private DefaultTableModel model;
	private JTextField nameField;
	private JTextField seriesField;
	private JTextField priceField;
	private JTextField imagePathField;
	private Stack<FunkoPop> undoStack;
	private Stack<FunkoPop> redoStack;
	private Map<String, FunkoPop> itemMap;
	private HashSet<FunkoPop> itemSet;
	private BTree btree;
	

	public FunkoPopCollectionGUI() {
		super("Funko Pop Collection");
		collection = new FunkoPopCollection();
		try {
			currentItems = FileIO.readFromFile("collection.txt");
			collection = new FunkoPopCollection();
			for (FunkoPop item : currentItems) {
				collection.addItem(item);
			}
		} catch (Exception e) {
			currentItems = new ArrayList<>();
			System.out.println("error");
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		createTable();
		createForm();
		createButtons();
		undoStack = new Stack<>();
		redoStack = new Stack<>();
		itemMap = new HashMap<>();
		itemSet = new HashSet<>();
		for (FunkoPop item : collection.getItems()) {
			itemMap.put(item.getName() + item.getSeries(), item);
			itemSet.add(item);
		}
		btree = new BTree(4);
		for (FunkoPop item : collection.getItems()) {
			btree.insert(item);
		}
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		System.out.println(collection.getItems());
	}

	void createTable() {
		model = new DefaultTableModel(new Object[] { "Name", "Series", "Price", "Serial Number" }, 0);
		table = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);
		for (FunkoPop item : collection.getItems()) {
			model.addRow(new Object[] { item.getName(), item.getSeries(), item.getPrice(), item.getIndex() });
		}
	}

	private void createForm() {
		JPanel formPanel = new JPanel(new GridLayout(0, 2));
		formPanel.setBorder(BorderFactory.createTitledBorder("Add new item"));
		formPanel.add(new JLabel("Name:"));
		nameField = new JTextField();
		formPanel.add(nameField);
		formPanel.add(new JLabel("Series:"));
		seriesField = new JTextField();
		formPanel.add(seriesField);
		formPanel.add(new JLabel("Price:"));
		priceField = new JTextField();
		formPanel.add(priceField);
		formPanel.add(new JLabel("Serial Number"));
		imagePathField = new JTextField();
		formPanel.add(imagePathField);
		add(formPanel, BorderLayout.NORTH);
	}

	private void createButtons() {
		JPanel buttonPanel = new JPanel(new FlowLayout());
		
		//ADD ITEM BUTTON
		JButton addButton = new JButton("Add item");
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				Runnable task = new Runnable() {
					public void run() {
						try {
							String name = nameField.getText();
							String series = seriesField.getText();
							double price = Double.parseDouble(priceField.getText());
							int imagePath = Integer.parseInt(imagePathField.getText());

							FunkoPop item = new FunkoPop(name, series, price, imagePath);
							collection.addItem(item);
							model.addRow(new Object[] { item.getName(), item.getSeries(), item.getPrice(), item.getIndex() });
							itemMap.put(name+series, item);
			                undoStack.push(item);
			                redoStack.push(item);
			                itemSet.add(item);
			    			btree.insert(item);
							clearForm();
						} catch (NumberFormatException ex) {
							JOptionPane.showMessageDialog(null, "Invalid price");
						}
					}
				};
				
				executeTaskInThread(task);
			}
		});
		buttonPanel.add(addButton);
		
		//REMOVE ITEM BUTTON
		JButton removeButton = new JButton("Remove selected item");
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				Runnable task = new Runnable() {
					public void run() {
						int selectedRow = table.getSelectedRow();
						if (selectedRow == -1) {
							JOptionPane.showMessageDialog(null, "Please select an item to remove");
							return;
						}
						String name = (String) model.getValueAt(selectedRow, 0);
						String series = (String) model.getValueAt(selectedRow, 1);
						double price = (Double) model.getValueAt(selectedRow, 2);
						FunkoPop itemToRemove = null;
						for (FunkoPop item : collection.getItems()) {
							if (item.getName().equals(name) && item.getSeries().equals(series) && item.getPrice() == price) {
								itemToRemove = item;
								break;
							}
						}
						if (itemToRemove != null) {
							collection.removeItem(itemToRemove);
							model.removeRow(selectedRow);
							
						}
						
					}
				};
				
				executeTaskInThread(task);
			}
		});
		
		buttonPanel.add(removeButton);
		
		//SORT ITEM BUTTON
		JButton sortButton = new JButton("Sort");
		sortButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Runnable task = new Runnable() {
					public void run() {
						collection.sortItems();
						model.setRowCount(0);
						for (FunkoPop item : collection.getItems()) {
							model.addRow(new Object[] { item.getName(), item.getSeries(), item.getPrice(), item.getIndex() });
						}
					}
				};
				executeTaskInThread(task);
			}
		});
		buttonPanel.add(sortButton);
		
		//SAVE COLLECTION BUTTON
		JButton saveButton = new JButton("Save collection");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Runnable task = new Runnable() {
					public void run() {
						try {
							FileOutputStream fos = new FileOutputStream("collection.txt");
							ObjectOutputStream oos = new ObjectOutputStream(fos);
							oos.writeObject(new ArrayList<FunkoPop>(collection.getItems()));
							oos.close();
							JOptionPane.showMessageDialog(null, "Collection saved to file");
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(null, "Error saving collection to file");
						}
					}
				};
				executeTaskInThread(task);
			}
		});
		buttonPanel.add(saveButton);
		add(buttonPanel, BorderLayout.SOUTH);
		
		//UNDO BUTTON
		JButton undoButton = new JButton("Undo");
		undoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Runnable task = new Runnable() {
					public void run() {
						if (!undoStack.empty()) {
							FunkoPop item = undoStack.pop();
							collection.removeItem(item);
							model.removeRow(findIndex(item));
							itemMap.remove(item.getName() + item.getSeries());
							itemSet.remove(item);
							btree.delete(item);
						} else {
							JOptionPane.showMessageDialog(null, "No action to undo");
						}
					}
				};
				executeTaskInThread(task);
			}
		});
		buttonPanel.add(undoButton);
		add(buttonPanel, BorderLayout.SOUTH);
		
		//REDO BUTTON
		JButton redoButton = new JButton("Redo");
		redoButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        Runnable task = new Runnable() {
		            public void run() {
		                if (!redoStack.empty()) {
		                    FunkoPop item = redoStack.pop();
		                    collection.addItem(item);
		                    model.addRow(new Object[] { item.getName(), item.getSeries(), item.getPrice(), item.getIndex() });
		                    itemMap.put(item.getName() + item.getSeries(), item);
		                    itemSet.add(item);
		                    btree.insert(item);
		                } else {
		                    JOptionPane.showMessageDialog(null, "No action to redo");
		                }
		            }
		        };
		        executeTaskInThread(task);
		    }
		});
		buttonPanel.add(redoButton);
		
		//SEARCH USING HASHMAP BUTTON
		JButton lookupButton = new JButton("search: HashMap");
	    lookupButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	
	        	Runnable task = new Runnable() {
	        		public void run() {
	        			String name = JOptionPane.showInputDialog("Enter the name of the item:");
	    	            String series = JOptionPane.showInputDialog("Enter the series of the item:");
	    	            String key = name + series;
	    	            
	    	            FunkoPop pop = itemMap.get(key);
	    	            
	    	            if (pop != null) {
	    	                String message = "Name: " + pop.getName() + "\nSeries: " + pop.getSeries() + "\nPrice: " + pop.getPrice() + "\nSerial Number: " + pop.getIndex();
	    	                JOptionPane.showMessageDialog(null, message);
	    	            } else {
	    	                JOptionPane.showMessageDialog(null, "Pop not found.");
	    	            }
	        		}
	        	};
	            executeTaskInThread(task);
	        }
	    });
	    buttonPanel.add(lookupButton);
	    add(buttonPanel, BorderLayout.SOUTH);
	    
	    //SEARCH USING HASHSET BUTTON
	    JButton searchButton = new JButton("search: HashSet");
	    searchButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	
	        	Runnable task = new Runnable() {
	        		public void run() {
	        			String name = JOptionPane.showInputDialog("Enter name of Funko Pop to search:");
	    	            String series = JOptionPane.showInputDialog("Enter series of Funko Pop to search:");
	    	            String price = JOptionPane.showInputDialog("What is the price of the pop: ");
	    	            String ID = JOptionPane.showInputDialog("What is the ID number of the pop: ");
	    	            
	    	            
	    	            FunkoPop item = new FunkoPop(name, series,Double.parseDouble(price) , Integer.parseInt(ID));
	    	            if (!itemSet.contains(item)) {
	    	                JOptionPane.showMessageDialog(null, "Funko Pop not found!");
	    	            } else {
	    	                for (FunkoPop fp : itemSet) {
	    	                    if (fp.equals(item)) {
	    	                        JOptionPane.showMessageDialog(null,
	    	                                "Name: " + fp.getName() + "\nSeries: " + fp.getSeries() + "\nPrice: " + fp.getPrice()
	    	                                        + "\nSerial Number: " + fp.getIndex());
	    	                        break;
	    	                    }
	    	                }
	    	            }
	        		}
	        	};
	            executeTaskInThread(task);
	        }
	    });
	    buttonPanel.add(searchButton);
	    
	    JButton searchButton2 = new JButton("search: B-tree");
	    searchButton2.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            Runnable task = new Runnable() {
	                public void run() {
	                    String name = JOptionPane.showInputDialog("Enter name of Funko Pop to search:");
	                    String series = JOptionPane.showInputDialog("Enter series of Funko Pop to search:");
	                    String price = JOptionPane.showInputDialog("What is the price of the pop: ");
	                    String ID = JOptionPane.showInputDialog("What is the ID number of the pop: ");

	                    FunkoPop item = new FunkoPop(name, series, Double.parseDouble(price), Integer.parseInt(ID));
	                    boolean found = btree.search(item);

	                    if (!found) {
	                        JOptionPane.showMessageDialog(null, "Funko Pop not found!");
	                    } else {
	                        FunkoPop foundItem = btree.retrieve(item);
	                        JOptionPane.showMessageDialog(null,
	                                "Name: " + foundItem.getName() + "\nSeries: " + foundItem.getSeries() + "\nPrice: " + foundItem.getPrice()
	                                        + "\nSerial Number: " + foundItem.getIndex());
	                    }
	                }
	            };
	            executeTaskInThread(task);
	        }
	    });
	    buttonPanel.add(searchButton2);


	}

	private int findIndex(FunkoPop item) {
		for (int i = 0; i < model.getRowCount(); i++) {
			if (model.getValueAt(i, 0).equals(item.getName()) && model.getValueAt(i, 1).equals(item.getSeries())) {
				return i;
			}
		}
		return -1;
	}

	private void clearForm() {
		nameField.setText("");
		seriesField.setText("");
		priceField.setText("");
		imagePathField.setText("");
	}
	
	private void executeTaskInThread(Runnable task) {
		Thread thread = new Thread(task);
		thread.start();
	}

	public static void main(String[] args) {
		new FunkoPopCollectionGUI();
	}
}