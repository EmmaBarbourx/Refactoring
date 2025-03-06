package src;

import src.Employee;
import src.RandomFile;
import java.io.File;
import java.util.Vector;

public class EmployeeManager {
    private RandomFile randomFile;
    private File file;
    
    public EmployeeManager(File file) {
        this.file = file;
        this.randomFile = new RandomFile();
    }
    
    // File I/O Methods
    public void openFileForRead() {
        randomFile.openReadFile(file.getAbsolutePath());
    }
    
    public void closeReadFile() {
        randomFile.closeReadFile();
    }
    
    public void openFileForWrite() {
        randomFile.openWriteFile(file.getAbsolutePath());
    }
    
    public void closeWriteFile() {
        randomFile.closeWriteFile();
    }
    
    // Record Navigation Methods
    public Employee getFirstEmployee() {
        openFileForRead();
        long byteStart = randomFile.getFirst();
        Employee emp = randomFile.readRecords(byteStart);
        closeReadFile();
        return emp;
    }
    
    public Employee getNextEmployee(long currentByteStart) {
        openFileForRead();
        long nextByte = randomFile.getNext(currentByteStart);
        Employee emp = randomFile.readRecords(nextByte);
        closeReadFile();
        return emp;
    }
    
    public Employee getPreviousEmployee(long currentByteStart) {
        openFileForRead();
        long prevByte = randomFile.getPrevious(currentByteStart);
        Employee emp = randomFile.readRecords(prevByte);
        closeReadFile();
        return emp;
    }
    
    public Employee getLastEmployee() {
        openFileForRead();
        long lastByte = randomFile.getLast();
        Employee emp = randomFile.readRecords(lastByte);
        closeReadFile();
        return emp;
    }
    
    public int getNextFreeId() {
        if (file.length() == 0 || !isSomeoneToDisplay())
            return 1;
        Employee last = getLastEmployee();
        return last.getEmployeeId() + 1;
    }
    
    // Record Manipulation Methods
    public void addEmployee(Employee newEmployee) {
        openFileForWrite();
        randomFile.addRecords(newEmployee);
        closeWriteFile();
    }
    
    public void updateEmployee(Employee updatedEmployee, long bytePosition) {
        openFileForWrite();
        randomFile.changeRecords(updatedEmployee, bytePosition);
        closeWriteFile();
    }
    
    public void deleteEmployee(long bytePosition) {
        openFileForWrite();
        randomFile.deleteRecords(bytePosition);
        closeWriteFile();
    }
    
    // Get All Employees (for summary table) 
    public Vector<Object> getAllEmployees() {
        Vector<Object> allEmployees = new Vector<>();
        openFileForRead();
        long startByte = randomFile.getFirst();
        Employee firstEmp = randomFile.readRecords(startByte);
        int firstId = firstEmp.getEmployeeId();
        long bytePos = startByte;
        do {
            Vector<Object> empDetails = new Vector<>();
            Employee current = randomFile.readRecords(bytePos);
            empDetails.add(current.getEmployeeId());
            empDetails.add(current.getPps());
            empDetails.add(current.getSurname());
            empDetails.add(current.getFirstName());
            empDetails.add(current.getGender());
            empDetails.add(current.getDepartment());
            empDetails.add(current.getSalary());
            empDetails.add(current.getFullTime());
            allEmployees.add(empDetails);
            bytePos = randomFile.getNext(bytePos);
        } while (firstId != randomFile.readRecords(bytePos).getEmployeeId());
        closeReadFile();
        return allEmployees;
    }
    
    // Search Methods
    public Employee searchById(int id) {
        openFileForRead();
        long startByte = randomFile.getFirst();
        Employee emp = randomFile.readRecords(startByte);
        int firstId = emp.getEmployeeId();
        while (true) {
            if (emp.getEmployeeId() == id) {
                closeReadFile();
                return emp;
            }
            startByte = randomFile.getNext(startByte);
            emp = randomFile.readRecords(startByte);
            if (emp.getEmployeeId() == firstId)
                break;
        }
        closeReadFile();
        return null;
    }
    
    public Employee searchBySurname(String surname) {
        openFileForRead();
        long startByte = randomFile.getFirst();
        Employee emp = randomFile.readRecords(startByte);
        String firstSurname = emp.getSurname().trim();
        while (true) {
            if (emp.getSurname().trim().equalsIgnoreCase(surname.trim())) {
                closeReadFile();
                return emp;
            }
            startByte = randomFile.getNext(startByte);
            emp = randomFile.readRecords(startByte);
            if (firstSurname.equalsIgnoreCase(emp.getSurname().trim()))
                break;
        }
        closeReadFile();
        return null;
    }
    
    public boolean isSomeoneToDisplay() {
        openFileForRead();
        boolean result = randomFile.isSomeoneToDisplay();
        closeReadFile();
        return result;
    }
}
