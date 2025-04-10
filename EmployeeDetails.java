package src;


/* * 
 * This is a menu driven system that will allow users to define a data structure representing a collection of 
 * records that can be displayed both by means of a dialog that can be scrolled through and by means of a table
 * to give an overall view of the collection contents.
 * 
 * */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import src.ValidationUtils;
import src.EmployeeManager;  
import src.AppConstants;



import net.miginfocom.swing.MigLayout;

public class EmployeeDetails extends JFrame implements ActionListener, ItemListener, DocumentListener, WindowListener {
	// decimal format for inactive currency text field
	private static final DecimalFormat format = new DecimalFormat("\u20ac ###,###,##0.00");
	// decimal format for active currency text field
	private static final DecimalFormat fieldFormat = new DecimalFormat("0.00");
	// hold object start position in file
	private long currentByteStart = 0;
	private RandomFile application = new RandomFile();
	// display files in File Chooser only with extension .dat
	private FileNameExtensionFilter datfilter = new FileNameExtensionFilter("dat files (*.dat)", "dat");
	// hold file name and path for current file in use
	private File file;
	// holds true or false if any changes are made for text fields
	private boolean change = false;
	// holds true or false if any changes are made for file content
	boolean changesMade = false;
	private JMenuItem open, save, saveAs, create, modify, delete, firstItem, lastItem, nextItem, prevItem, searchById,
			searchBySurname, listAll, closeApp;
	private JButton first, previous, next, last, add, edit, deleteButton, displayAll, searchId, searchSurname,
			saveChange, cancelChange;
	private JComboBox<String> genderCombo, departmentCombo, fullTimeCombo;
	private JTextField idField, ppsField, surnameField, firstNameField, salaryField;
	private static EmployeeDetails frame = new EmployeeDetails();
	// font for labels, text fields and combo boxes
	 Font font1 = AppConstants.DEFAULT_FONT; 
	// holds automatically generated file name
	String generatedFileName;
	// holds current Employee object
	Employee currentEmployee;
	JTextField searchByIdField, searchBySurnameField;
	// gender combo box values
	String[] gender = { "", "M", "F" };
	// department combo box values
	String[] department = { "", "Administration", "Production", "Transport", "Management" };
	// full time combo box values
	String[] fullTime = { "", "Yes", "No" };
	
	private EmployeeManager employeeManager;

	// initialize menu bar
	private JMenuBar menuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu, recordMenu, navigateMenu, closeMenu;

		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		recordMenu = new JMenu("Records");
		recordMenu.setMnemonic(KeyEvent.VK_R);
		navigateMenu = new JMenu("Navigate");
		navigateMenu.setMnemonic(KeyEvent.VK_N);
		closeMenu = new JMenu("Exit");
		closeMenu.setMnemonic(KeyEvent.VK_E);

		menuBar.add(fileMenu);
		menuBar.add(recordMenu);
		menuBar.add(navigateMenu);
		menuBar.add(closeMenu);

		fileMenu.add(open = new JMenuItem("Open")).addActionListener(this);
		open.setMnemonic(KeyEvent.VK_O);
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		fileMenu.add(save = new JMenuItem("Save")).addActionListener(this);
		save.setMnemonic(KeyEvent.VK_S);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		fileMenu.add(saveAs = new JMenuItem("Save As")).addActionListener(this);
		saveAs.setMnemonic(KeyEvent.VK_F2);
		saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, ActionEvent.CTRL_MASK));

		recordMenu.add(create = new JMenuItem("Create new Record")).addActionListener(this);
		create.setMnemonic(KeyEvent.VK_N);
		create.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		recordMenu.add(modify = new JMenuItem("Modify Record")).addActionListener(this);
		modify.setMnemonic(KeyEvent.VK_E);
		modify.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		recordMenu.add(delete = new JMenuItem("Delete Record")).addActionListener(this);

		navigateMenu.add(firstItem = new JMenuItem("First"));
		firstItem.addActionListener(this);
		navigateMenu.add(prevItem = new JMenuItem("Previous"));
		prevItem.addActionListener(this);
		navigateMenu.add(nextItem = new JMenuItem("Next"));
		nextItem.addActionListener(this);
		navigateMenu.add(lastItem = new JMenuItem("Last"));
		lastItem.addActionListener(this);
		navigateMenu.addSeparator();
		navigateMenu.add(searchById = new JMenuItem("Search by ID")).addActionListener(this);
		navigateMenu.add(searchBySurname = new JMenuItem("Search by Surname")).addActionListener(this);
		navigateMenu.add(listAll = new JMenuItem("List all Records")).addActionListener(this);

		closeMenu.add(closeApp = new JMenuItem("Close")).addActionListener(this);
		closeApp.setMnemonic(KeyEvent.VK_F4);
		closeApp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.CTRL_MASK));

		return menuBar;
	}// end menuBar

	// initialize search panel
	private JPanel searchPanel() {
		JPanel searchPanel = new JPanel(new MigLayout());

		searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
		searchPanel.add(new JLabel("Search by ID:"), "growx, pushx");
		searchPanel.add(searchByIdField = new JTextField(AppConstants.TEXT_FIELD_COLUMNS), "width 200:200:200, growx, pushx");
		searchByIdField.addActionListener(this);
		searchByIdField.setDocument(new JTextFieldLimit(AppConstants.TEXT_FIELD_COLUMNS));
		searchPanel.add(searchId = new JButton("Go"),
				"width 35:35:35, height 20:20:20, growx, pushx, wrap");
		searchId.addActionListener(this);
		searchId.setToolTipText("Search Employee By ID");

		searchPanel.add(new JLabel("Search by Surname:"), "growx, pushx");
		searchPanel.add(searchBySurnameField = new JTextField(AppConstants.TEXT_FIELD_COLUMNS), "width 200:200:200, growx, pushx");
		searchBySurnameField.addActionListener(this);
		searchBySurnameField.setDocument(new JTextFieldLimit(AppConstants.TEXT_FIELD_COLUMNS));
		searchPanel.add(
				searchSurname = new JButton("Go"),"width 35:35:35, height 20:20:20, growx, pushx, wrap");
		searchSurname.addActionListener(this);
		searchSurname.setToolTipText("Search Employee By Surname");

		return searchPanel;
	}// end searchPanel

	// initialize navigation panel
	private JPanel navigPanel() {
		JPanel navigPanel = new JPanel();

		navigPanel.setBorder(BorderFactory.createTitledBorder("Navigate"));
		navigPanel.add(first = new JButton(new ImageIcon(
				new ImageIcon("first.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		first.setPreferredSize(new Dimension(17, 17));
		first.addActionListener(this);
		first.setToolTipText("Display first Record");

		navigPanel.add(previous = new JButton(new ImageIcon(new ImageIcon("prev.png").getImage()
				.getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		previous.setPreferredSize(new Dimension(17, 17));
		previous.addActionListener(this);
		previous.setToolTipText("Display next Record");

		navigPanel.add(next = new JButton(new ImageIcon(
				new ImageIcon("next.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		next.setPreferredSize(new Dimension(17, 17));
		next.addActionListener(this);
		next.setToolTipText("Display previous Record");

		navigPanel.add(last = new JButton(new ImageIcon(
				new ImageIcon("last.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		last.setPreferredSize(new Dimension(17, 17));
		last.addActionListener(this);
		last.setToolTipText("Display last Record");

		return navigPanel;
	}// end naviPanel

	private JPanel buttonPanel() {
		JPanel buttonPanel = new JPanel();

		buttonPanel.add(add = new JButton("Add Record"), "growx, pushx");
		add.addActionListener(this);
		add.setToolTipText("Add new Employee Record");
		buttonPanel.add(edit = new JButton("Edit Record"), "growx, pushx");
		edit.addActionListener(this);
		edit.setToolTipText("Edit current Employee");
		buttonPanel.add(deleteButton = new JButton("Delete Record"), "growx, pushx, wrap");
		deleteButton.addActionListener(this);
		deleteButton.setToolTipText("Delete current Employee");
		buttonPanel.add(displayAll = new JButton("List all Records"), "growx, pushx");
		displayAll.addActionListener(this);
		displayAll.setToolTipText("List all Registered Employees");

		return buttonPanel;
	}

	// initialize main/details panel
	private JPanel detailsPanel() {
		JPanel empDetails = new JPanel(new MigLayout());
		JPanel buttonPanel = new JPanel();
		JTextField field;

		empDetails.setBorder(BorderFactory.createTitledBorder("Employee Details"));

		empDetails.add(new JLabel("ID:"), "growx, pushx");
		empDetails.add(idField = new JTextField(AppConstants.TEXT_FIELD_COLUMNS), "growx, pushx, wrap");
		idField.setEditable(false);

		empDetails.add(new JLabel("PPS Number:"), "growx, pushx");
		empDetails.add(ppsField = new JTextField(AppConstants.TEXT_FIELD_COLUMNS), "growx, pushx, wrap");

		empDetails.add(new JLabel("Surname:"), "growx, pushx");
		empDetails.add(surnameField = new JTextField(AppConstants.TEXT_FIELD_COLUMNS), "growx, pushx, wrap");

		empDetails.add(new JLabel("First Name:"), "growx, pushx");
		empDetails.add(firstNameField = new JTextField(AppConstants.TEXT_FIELD_COLUMNS), "growx, pushx, wrap");

		empDetails.add(new JLabel("Gender:"), "growx, pushx");
		empDetails.add(genderCombo = new JComboBox<String>(gender), "growx, pushx, wrap");

		empDetails.add(new JLabel("Department:"), "growx, pushx");
		empDetails.add(departmentCombo = new JComboBox<String>(department), "growx, pushx, wrap");

		empDetails.add(new JLabel("Salary:"), "growx, pushx");
		empDetails.add(salaryField = new JTextField(AppConstants.TEXT_FIELD_COLUMNS), "growx, pushx, wrap");

		empDetails.add(new JLabel("Full Time:"), "growx, pushx");
		empDetails.add(fullTimeCombo = new JComboBox<String>(fullTime), "growx, pushx, wrap");

		buttonPanel.add(saveChange = new JButton("Save"));
		saveChange.addActionListener(this);
		saveChange.setVisible(false);
		saveChange.setToolTipText("Save changes");
		buttonPanel.add(cancelChange = new JButton("Cancel"));
		cancelChange.addActionListener(this);
		cancelChange.setVisible(false);
		cancelChange.setToolTipText("Cancel edit");

		empDetails.add(buttonPanel, "span 2,growx, pushx,wrap");

		// loop through panel components and add listeners and format
		for (int i = 0; i < empDetails.getComponentCount(); i++) {
			empDetails.getComponent(i).setFont(font1);
			if (empDetails.getComponent(i) instanceof JTextField) {
				field = (JTextField) empDetails.getComponent(i);
				field.setEditable(false);
				if (field == ppsField)
					field.setDocument(new JTextFieldLimit(AppConstants.PPS_FIELD_LIMIT));
				else
					field.setDocument(new JTextFieldLimit(AppConstants.TEXT_FIELD_COLUMNS));
				field.getDocument().addDocumentListener(this);
			} // end if
			else if (empDetails.getComponent(i) instanceof JComboBox) {
				empDetails.getComponent(i).setBackground(Color.WHITE);
				empDetails.getComponent(i).setEnabled(false);
				((JComboBox<String>) empDetails.getComponent(i)).addItemListener(this);
				((JComboBox<String>) empDetails.getComponent(i)).setRenderer(new DefaultListCellRenderer() {
					// set foregroung to combo boxes
					public void paint(Graphics g) {
						setForeground(new Color(65, 65, 65));
						super.paint(g);
					}// end paint
				});
			} // end else if
		} // end for
		return empDetails;
	}// end detailsPanel

	// display current Employee details
	public void displayRecords(Employee thisEmployee) {
		int countGender = 0;
		int countDep = 0;
		boolean found = false;

		searchByIdField.setText("");
		searchBySurnameField.setText("");
		// if Employee is null or ID is 0 do nothing else display Employee
		// details
		if (thisEmployee == null) {
		} else if (thisEmployee.getEmployeeId() == 0) {
		} else {
			// find corresponding gender combo box value to current employee
			while (!found && countGender < gender.length - 1) {
				if (Character.toString(thisEmployee.getGender()).equalsIgnoreCase(gender[countGender]))
					found = true;
				else
					countGender++;
			} // end while
			found = false;
			// find corresponding department combo box value to current employee
			while (!found && countDep < department.length - 1) {
				if (thisEmployee.getDepartment().trim().equalsIgnoreCase(department[countDep]))
					found = true;
				else
					countDep++;
			} // end while
			idField.setText(Integer.toString(thisEmployee.getEmployeeId()));
			ppsField.setText(thisEmployee.getPps().trim());
			surnameField.setText(thisEmployee.getSurname().trim());
			firstNameField.setText(thisEmployee.getFirstName());
			genderCombo.setSelectedIndex(countGender);
			departmentCombo.setSelectedIndex(countDep);
			salaryField.setText(format.format(thisEmployee.getSalary()));
			// set corresponding full time combo box value to current employee
			if (thisEmployee.getFullTime() == true)
				fullTimeCombo.setSelectedIndex(1);
			else
				fullTimeCombo.setSelectedIndex(2);
		}
		change = false;
	}// end display records

	// Delegating display to new class
	
	// display Employee summary dialog
	private void displayEmployeeSummaryDialog() {
		// display Employee summary dialog if these is someone to display
		if (employeeManager.isSomeoneToDisplay())
            new EmployeeSummaryDialog(employeeManager.getAllEmployees());
	}// end displaySummaryDialog

	// display search by ID dialog
	private void displaySearchByIdDialog() {
		 if (employeeManager.isSomeoneToDisplay())
	            new SearchByIdDialog(EmployeeDetails.this);
	}// end displaySearchByIdDialog

	// display search by surname dialog
	private void displaySearchBySurnameDialog() {
		 if (employeeManager.isSomeoneToDisplay())
	            new SearchBySurnameDialog(EmployeeDetails.this);
	}// end displaySearchBySurnameDialog

	// find byte start in file for first active record
		private void firstRecord() {
			// if any active record in file look for first record
			if (isSomeoneToDisplay()) {
				// open file for reading
				application.openReadFile(file.getAbsolutePath());
				// get byte start in file for first record
				currentByteStart = application.getFirst();
				// assign current Employee to first record in file
				currentEmployee = application.readRecords(currentByteStart);
				application.closeReadFile();// close file for reading
				// if first record is inactive look for next record
				if (currentEmployee.getEmployeeId() == 0)
					nextRecord();// look for next record
			} // end if
		}// end firstRecord

		// find byte start in file for previous active record
		private void previousRecord() {
			// if any active record in file look for first record
			if (isSomeoneToDisplay()) {
				// open file for reading
				application.openReadFile(file.getAbsolutePath());
				// get byte start in file for previous record
				currentByteStart = application.getPrevious(currentByteStart);
				// assign current Employee to previous record in file
				currentEmployee = application.readRecords(currentByteStart);
				// loop to previous record until Employee is active - ID is not 0
				while (currentEmployee.getEmployeeId() == 0) {
					// get byte start in file for previous record
					currentByteStart = application.getPrevious(currentByteStart);
					// assign current Employee to previous record in file
					currentEmployee = application.readRecords(currentByteStart);
				} // end while
				application.closeReadFile();// close file for reading
			}
		}// end previousRecord

		// find byte start in file for next active record
		private void nextRecord() {
			// if any active record in file look for first record
			if (isSomeoneToDisplay()) {
				// open file for reading
				application.openReadFile(file.getAbsolutePath());
				// get byte start in file for next record
				currentByteStart = application.getNext(currentByteStart);
				// assign current Employee to record in file
				currentEmployee = application.readRecords(currentByteStart);
				// loop to previous next until Employee is active - ID is not 0
				while (currentEmployee.getEmployeeId() == 0) {
					// get byte start in file for next record
					currentByteStart = application.getNext(currentByteStart);
					// assign current Employee to next record in file
					currentEmployee = application.readRecords(currentByteStart);
				} // end while
				application.closeReadFile();// close file for reading
			} // end if
		}// end nextRecord

		// find byte start in file for last active record
		private void lastRecord() {
			// if any active record in file look for first record
			if (isSomeoneToDisplay()) {
				// open file for reading
				application.openReadFile(file.getAbsolutePath());
				// get byte start in file for last record
				currentByteStart = application.getLast();
				// assign current Employee to first record in file
				currentEmployee = application.readRecords(currentByteStart);
				application.closeReadFile();// close file for reading
				// if last record is inactive look for previous record
				if (currentEmployee.getEmployeeId() == 0)
					previousRecord();// look for previous record
			} // end if
		}// end lastRecord

	// search Employee by ID
	public void searchEmployeeById() {
	    try {
	        int searchId = Integer.parseInt(searchByIdField.getText().trim());
	        Employee emp = employeeManager.searchById(searchId);
	        if (emp != null) {
	            displayRecords(emp);
	        } else {
	            JOptionPane.showMessageDialog(null, "Employee not found!");
	        }
	    } catch (NumberFormatException e) {
	        searchByIdField.setBackground(new Color(255, 150, 150));
	        JOptionPane.showMessageDialog(null, "Wrong ID format!");
	    }
	    searchByIdField.setBackground(Color.WHITE);
	    searchByIdField.setText("");
	
		} // end try
		// end searchEmployeeByID

	// search Employee by surname
	public void searchEmployeeBySurname() {
	    String surname = searchBySurnameField.getText().trim();
	    Employee emp = employeeManager.searchBySurname(surname);
	    if (emp != null) {
	        displayRecords(emp);
	    } else {
	        JOptionPane.showMessageDialog(null, "Employee not found!");
	    }
	    searchBySurnameField.setText("");
	}// end searchEmployeeBySurname

	// get next free ID from Employees in the file
		public int getNextFreeId() {
			int nextFreeId = 0;
			// if file is empty or all records are empty start with ID 1 else look
			// for last active record
			if (file.length() == 0 || !isSomeoneToDisplay())
				nextFreeId++;
			else {
				lastRecord();// look for last active record
				// add 1 to last active records ID to get next ID
				nextFreeId = currentEmployee.getEmployeeId() + 1;
			}
			return nextFreeId;
		}// end getNextFreeId
	

	// get values from text fields and create Employee object
	private Employee getChangedDetails() {
		boolean fullTime = false;
		Employee theEmployee;
		if (((String) fullTimeCombo.getSelectedItem()).equalsIgnoreCase("Yes"))
			fullTime = true;

		theEmployee = new Employee(Integer.parseInt(idField.getText()), ppsField.getText().toUpperCase(),
				surnameField.getText().toUpperCase(), firstNameField.getText().toUpperCase(),
				genderCombo.getSelectedItem().toString().charAt(0), departmentCombo.getSelectedItem().toString(),
				Double.parseDouble(salaryField.getText()), fullTime);

		return theEmployee;
	}// end getChangedDetails

	// add Employee object to fail
	public void addRecord(Employee newEmployee) {
        employeeManager.addEmployee(newEmployee);
    }// end addRecord

	// delete (make inactive - empty) record from file
	private void deleteRecord() {
        if (isSomeoneToDisplay()) {
            int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to delete record?", "Delete",
                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            if (returnVal == JOptionPane.YES_OPTION) {
                employeeManager.deleteEmployee(currentByteStart);
                if (isSomeoneToDisplay()) {
                    nextRecord();
                    displayRecords(currentEmployee);
                }
            }
        }
    }// end deleteDecord

	// create vector of vectors with all Employee details
	private Vector<Object> getAllEmloyees() {
        return employeeManager.getAllEmployees();
	}// end getAllEmployees

	// activate field for editing
	private void editDetails() {
		// activate field for editing if there is records to display
		if (isSomeoneToDisplay()) {
			// remove euro sign from salary text field
			salaryField.setText(fieldFormat.format(currentEmployee.getSalary()));
			change = false;
			setEnabled(true);// enable text fields for editing
		} // end if
	}// end editDetails

	// ignore changes and set text field unenabled
	private void cancelChange() {
		setEnabled(false);
		displayRecords(currentEmployee);
	}// end cancelChange

	// check if any of records in file is active - ID is not 0
	private boolean isSomeoneToDisplay() {
		 boolean someoneToDisplay = employeeManager.isSomeoneToDisplay();
		if (!someoneToDisplay) {
			currentEmployee = null;
			idField.setText("");
			ppsField.setText("");
			surnameField.setText("");
			firstNameField.setText("");
			salaryField.setText("");
			genderCombo.setSelectedIndex(0);
			departmentCombo.setSelectedIndex(0);
			fullTimeCombo.setSelectedIndex(0);
			JOptionPane.showMessageDialog(null, "No Employees registered!");
		}
		return someoneToDisplay;
	}// end isSomeoneToDisplay

	// check for correct PPS format and look if PPS already in use
	public boolean correctPps(String pps, long currentByte) {
		boolean ppsExist = false;
		// check for correct PPS format based on assignment description
		if (pps.length() == 8 || pps.length() == 9) {
			if (Character.isDigit(pps.charAt(0)) && Character.isDigit(pps.charAt(1))
					&& Character.isDigit(pps.charAt(2))	&& Character.isDigit(pps.charAt(3)) 
					&& Character.isDigit(pps.charAt(4))	&& Character.isDigit(pps.charAt(5)) 
					&& Character.isDigit(pps.charAt(6))	&& Character.isLetter(pps.charAt(7))
					&& (pps.length() == 8 || Character.isLetter(pps.charAt(8)))) {
				// open file for reading
				application.openReadFile(file.getAbsolutePath());
				// look in file is PPS already in use
				ppsExist = application.isPpsExist(pps, currentByte);
				application.closeReadFile();// close file for reading
			} // end if
			else
				ppsExist = true;
		} // end if
		else
			ppsExist = true;

		return ppsExist;
	}// end correctPPS

	// check if file name has extension .dat
	private boolean checkFileName(File fileName) {
		boolean checkFile = false;
		int length = fileName.toString().length();

		// check if last characters in file name is .dat
		if (fileName.toString().endsWith(AppConstants.DAT_EXTENSION))
			checkFile = true;
		return checkFile;
	}// end checkFileName

	// check if any changes text field where made
	private boolean checkForChanges() {
		boolean anyChanges = false;
		// if changes where made, allow user to save there changes
		if (change) {
			saveChanges();// save changes
			anyChanges = true;
		} // end if
			// if no changes made, set text fields as unenabled and display
			// current Employee
		else {
			setEnabled(false);
			displayRecords(currentEmployee);
		} // end else

		return anyChanges;
	}// end checkForChanges

	// check for input in text fields
	private boolean checkInput() {
	    boolean valid = true;
	    
	    if (ppsField.isEditable()) {
	        valid &= ValidationUtils.validateTextField(ppsField, text -> !text.isEmpty());
	        // Custom PPS check
	        if (correctPps(ppsField.getText().trim(), currentByteStart)) {
	            ppsField.setBackground(ValidationUtils.ERROR_COLOR);
	            valid = false;
	        }
	    }
	    
	    if (surnameField.isEditable()) {
	        valid &= ValidationUtils.validateTextField(surnameField, text -> !text.isEmpty());
	    }
	    
	    if (firstNameField.isEditable()) {
	        valid &= ValidationUtils.validateTextField(firstNameField, text -> !text.isEmpty());
	    }
	    
	    if (genderCombo.isEnabled()) {
	        valid &= ValidationUtils.validateComboBox(genderCombo, 0);
	    }
	    
	    if (departmentCombo.isEnabled()) {
	        valid &= ValidationUtils.validateComboBox(departmentCombo, 0);
	    }
	    
	    try {
	        double salary = Double.parseDouble(salaryField.getText().trim());
	        if (salary < 0) {
	            salaryField.setBackground(ValidationUtils.ERROR_COLOR);
	            valid = false;
	        } else {
	            salaryField.setBackground(ValidationUtils.DEFAULT_COLOR);
	        }
	    } catch (NumberFormatException ex) {
	        if (salaryField.isEditable()) {
	            salaryField.setBackground(ValidationUtils.ERROR_COLOR);
	            valid = false;
	        }
	    }
	    
	    if (fullTimeCombo.isEnabled()) {
	        valid &= ValidationUtils.validateComboBox(fullTimeCombo, 0);
	    }
	    
	    if (!valid) {
	        JOptionPane.showMessageDialog(null, AppConstants.WRONG_FORMAT_MESSAGE);
	    }
	    
	    if (ppsField.isEditable()) {
	        setToWhite();
	    }
	    
	    return valid;
	}

	// set text field background colour to white
	private void setToWhite() {
		ppsField.setBackground(UIManager.getColor("TextField.background"));
		surnameField.setBackground(UIManager.getColor("TextField.background"));
		firstNameField.setBackground(UIManager.getColor("TextField.background"));
		salaryField.setBackground(UIManager.getColor("TextField.background"));
		genderCombo.setBackground(UIManager.getColor("TextField.background"));
		departmentCombo.setBackground(UIManager.getColor("TextField.background"));
		fullTimeCombo.setBackground(UIManager.getColor("TextField.background"));
	}// end setToWhite

	// enable text fields for editing
	public void setEnabled(boolean booleanValue) {
		boolean search;
		if (booleanValue)
			search = false;
		else
			search = true;
		ppsField.setEditable(booleanValue);
		surnameField.setEditable(booleanValue);
		firstNameField.setEditable(booleanValue);
		genderCombo.setEnabled(booleanValue);
		departmentCombo.setEnabled(booleanValue);
		salaryField.setEditable(booleanValue);
		fullTimeCombo.setEnabled(booleanValue);
		saveChange.setVisible(booleanValue);
		cancelChange.setVisible(booleanValue);
		searchByIdField.setEnabled(search);
		searchBySurnameField.setEnabled(search);
		searchId.setEnabled(search);
		searchSurname.setEnabled(search);
	}// end setEnabled

	// open file
	private void openFile() {
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Open");
		// display files in File Chooser only with extension .dat
		fc.setFileFilter(datfilter);
		File newFile; // holds opened file name and path
		// if old file is not empty or changes has been made, offer user to save
		// old file
		if (file.length() != 0 || change) {
			int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			// if user wants to save file, save it
			if (returnVal == JOptionPane.YES_OPTION) {
				saveFile();// save file
			} // end if
		} // end if

		int returnVal = fc.showOpenDialog(EmployeeDetails.this);
		// if file been chosen, open it
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();
			// if old file wasn't saved and its name is generated file name,
			// delete this file
			if (file.getName().equals(generatedFileName))
				file.delete();// delete file
			file = newFile;// assign opened file to file
			// open file for reading
			application.openReadFile(file.getAbsolutePath());
			firstRecord();// look for first record
			displayRecords(currentEmployee);
			application.closeReadFile();// close file for reading
		} // end if
	}// end openFile

	// save file
	private void saveFile() {
		// if file name is generated file name, save file as 'save as' else save
		// changes to file
		if (file.getName().equals(generatedFileName))
			saveFileAs();// save file as 'save as'
		else {
			// if changes has been made to text field offer user to save these
			// changes
			if (change) {
				int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
						JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
				// save changes if user choose this option
				if (returnVal == JOptionPane.YES_OPTION) {
					// save changes if ID field is not empty
					if (!idField.getText().equals("")) {
						// open file for writing
						application.openWriteFile(file.getAbsolutePath());
						// get changes for current Employee
						currentEmployee = getChangedDetails();
						employeeManager.updateEmployee(currentEmployee, currentByteStart);
					}// end if
				} // end if
			} // end if

			displayRecords(currentEmployee);
			setEnabled(false);
		} // end else
	}// end saveFile

	// save changes to current Employee
	private void saveChanges() {
		int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes to current Employee?", "Save",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
		// if user choose to save changes, save changes
		if (returnVal == JOptionPane.YES_OPTION) {
			currentEmployee = getChangedDetails();
	        employeeManager.updateEmployee(currentEmployee, currentByteStart);
	        changesMade = false;
	    }
	    displayRecords(currentEmployee);
	    setEnabled(false);
	}// end saveChanges

	// save file as 'save as'
	private void saveFileAs() {
		final JFileChooser fc = new JFileChooser();
		File newFile;
		String defaultFileName = AppConstants.DEFAULT_FILENAME;
		fc.setDialogTitle("Save As");
		// display files only with .dat extension
		fc.setFileFilter(datfilter);
		fc.setApproveButtonText("Save");
		fc.setSelectedFile(new File(defaultFileName));

		int returnVal = fc.showSaveDialog(EmployeeDetails.this);
		// if file has chosen or written, save old file in new file
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();
			// check for file name
			if (!checkFileName(newFile)) {
				// add .dat extension if it was not there
				newFile = new File(newFile.getAbsolutePath() + AppConstants.DAT_EXTENSION);
				// create new file
				application.createFile(newFile.getAbsolutePath());
			} // end id
			else
				// create new file
				application.createFile(newFile.getAbsolutePath());

			try {// try to copy old file to new file
				Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				// if old file name was generated file name, delete it
				if (file.getName().equals(generatedFileName))
					file.delete();// delete file
				file = newFile;// assign new file to file
			} // end try
			catch (IOException e) {
			} // end catch
		} // end if
		changesMade = false;
		 employeeManager = new EmployeeManager(file);
	}// end saveFileAs

	// allow to save changes to file when exiting the application
	private void exitApp() {
		// if file is not empty allow to save changes
		if (file.length() != 0) {
			if (changesMade) {
				int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
				// if user chooses to save file, save file
				if (returnVal == JOptionPane.YES_OPTION) {
					saveFile();// save file
					// delete generated file if user saved details to other file
					if (file.getName().equals(generatedFileName))
						file.delete();// delete file
					System.exit(0);// exit application
				} // end if
					// else exit application
				else if (returnVal == JOptionPane.NO_OPTION) {
					// delete generated file if user chooses not to save file
					if (file.getName().equals(generatedFileName))
						file.delete();// delete file
					System.exit(0);// exit application
				} // end else if
			} // end if
			else {
				// delete generated file if user chooses not to save file
				if (file.getName().equals(generatedFileName))
					file.delete();// delete file
				System.exit(0);// exit application
			} // end else
				// else exit application
		} else {
			// delete generated file if user chooses not to save file
			if (file.getName().equals(generatedFileName))
				file.delete();// delete file
			System.exit(0);// exit application
		} // end else
	}// end exitApp

	// generate 20 character long file name
	private String getFileName() {
		String fileNameChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-";
		StringBuilder fileName = new StringBuilder();
		Random rnd = new Random();
		// loop until 20 character long file name is generated
		while (fileName.length() < 20) {
			int index = (int) (rnd.nextFloat() * fileNameChars.length());
			fileName.append(fileNameChars.charAt(index));
		}
		String generatedfileName = fileName.toString();
		return generatedfileName;
	}// end getFileName

	// create file with generated file name when application is opened
	private void createRandomFile() {
		generatedFileName = getFileName() + ".dat";
		// assign generated file name to file
		file = new File(generatedFileName);
		// create file
		application.createFile(file.getName());
		 employeeManager = new EmployeeManager(file);
	}// end createRandomFile

	// action listener for buttons, text field and menu items
	public void actionPerformed(ActionEvent e) {
	    Object source = e.getSource();
	    if (source == closeApp) {
	        handleCloseApp();
	    } else if (source == open) {
	        handleOpenFile();
	    } else if (source == save) {
	        handleSaveFile();
	        change = false;
	    } else if (source == saveAs) {
	        handleSaveFileAs();
	        change = false;
	    } else if (source == searchById) {
	        handleSearchByIdDialog();
	    } else if (source == searchBySurname) {
	        handleSearchBySurnameDialog();
	    } else if (source == searchId || source == searchByIdField) {
	        searchEmployeeById();
	    } else if (source == searchSurname || source == searchBySurnameField) {
	        searchEmployeeBySurname();
	    } else if (source == saveChange) {
	        handleSaveChange();
	    } else if (source == cancelChange) {
	        cancelChange();
	    } else if (source == firstItem || source == first) {
	        handleFirstRecord();
	    } else if (source == prevItem || source == previous) {
	        handlePreviousRecord();
	    } else if (source == nextItem || source == next) {
	        handleNextRecord();
	    } else if (source == lastItem || source == last) {
	        handleLastRecord();
	    } else if (source == listAll || source == displayAll) {
	        handleListAllRecords();
	    } else if (source == create || source == add) {
	        handleAddNewRecord();
	    } else if (source == modify || source == edit) {
	        handleEditRecord();
	    } else if (source == delete || source == deleteButton) {
	        handleDeleteRecord();
	    }
	}

	private void handleCloseApp() {
	    if (checkInput() && !checkForChanges()) {
	        exitApp();
	    }
	}

	private void handleOpenFile() {
	    if (checkInput() && !checkForChanges()) {
	        openFile();
	    }
	}

	private void handleSaveFile() {
	    if (checkInput() && !checkForChanges()) {
	        saveFile();
	    }
	}

	private void handleSaveFileAs() {
	    if (checkInput() && !checkForChanges()) {
	        saveFileAs();
	    }
	}

	private void handleSearchByIdDialog() {
	    if (checkInput() && !checkForChanges()) {
	        displaySearchByIdDialog();
	    }
	}

	private void handleSearchBySurnameDialog() {
	    if (checkInput() && !checkForChanges()) {
	        displaySearchBySurnameDialog();
	    }
	}

	private void handleSaveChange() {
	    if (checkInput() && !checkForChanges()) {
	        saveChanges();
	    }
	}

	private void handleFirstRecord() {
	    if (checkInput() && !checkForChanges()) {
	        firstRecord();
	        displayRecords(currentEmployee);
	    }
	}

	private void handlePreviousRecord() {
	    if (checkInput() && !checkForChanges()) {
	        previousRecord();
	        displayRecords(currentEmployee);
	    }
	}

	private void handleNextRecord() {
	    if (checkInput() && !checkForChanges()) {
	        nextRecord();
	        displayRecords(currentEmployee);
	    }
	}

	private void handleLastRecord() {
	    if (checkInput() && !checkForChanges()) {
	        lastRecord();
	        displayRecords(currentEmployee);
	    }
	}

	private void handleListAllRecords() {
		if (checkInput() && !checkForChanges() && employeeManager.isSomeoneToDisplay()) {
	        displayEmployeeSummaryDialog(); 
	    }
	}

	private void handleAddNewRecord() {
	    if (checkInput() && !checkForChanges()) {
	        new AddRecordDialog(EmployeeDetails.this);
	    }
	}

	private void handleEditRecord() {
	    if (checkInput() && !checkForChanges()) {
	        editDetails();
	    }
	}

	private void handleDeleteRecord() {
	    if (checkInput() && !checkForChanges()) {
	        deleteRecord();
	    }
	}// end actionPerformed

	// content pane for main dialog
	private void createContentPane() {
		setTitle("Employee Details");
		createRandomFile();// create random file name
		JPanel dialog = new JPanel(new MigLayout());

		setJMenuBar(menuBar());// add menu bar to frame
		// add search panel to frame
		dialog.add(searchPanel(), "width 400:400:400, growx, pushx");
		// add navigation panel to frame
		dialog.add(navigPanel(), "width 150:150:150, wrap");
		// add button panel to frame
		dialog.add(buttonPanel(), "growx, pushx, span 2,wrap");
		// add details panel to frame
		dialog.add(detailsPanel(), "gap top 30, gap left 150, center");

		JScrollPane scrollPane = new JScrollPane(dialog);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		addWindowListener(this);
	}// end createContentPane

	// create and show main dialog
	private static void createAndShowGUI() {

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.createContentPane();// add content pane to frame
		frame.setSize(AppConstants.FRAME_WIDTH, AppConstants.FRAME_HEIGHT);
		frame.setLocation(AppConstants.FRAME_X_POSITION, AppConstants.FRAME_Y_POSITION);
		frame.setVisible(true);
	}// end createAndShowGUI

	// main method
	public static void main(String args[]) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}// end main

	// DocumentListener methods
	public void changedUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(AppConstants.TEXT_FIELD_COLUMNS);
	}

	public void insertUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(AppConstants.TEXT_FIELD_COLUMNS);
	}

	public void removeUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(AppConstants.TEXT_FIELD_COLUMNS);
	}

	// ItemListener method
	public void itemStateChanged(ItemEvent e) {
		change = true;
	}

	// WindowsListener methods
	public void windowClosing(WindowEvent e) {
		// exit application
		exitApp();
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}
}// end class EmployeeDetails
