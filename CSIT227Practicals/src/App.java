import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class App extends JFrame {
    private JPanel pnlMain;
    private JRadioButton rbCustomer;
    private JRadioButton rbClerk;
    private JRadioButton rbManager;
    private JTextField tfName;
    private JTextArea taPersons;
    private JButton btnSave;
    private JTextField tfAge;
    private JTextField tfMonths;
    private JTextField tfSalary;
    private JButton btnClear;
    private JTextField tfLoad;
    private JButton btnLoad;
    private JButton btnSayHi;
    private JButton btnSavePerson;
    private JButton btnLoadPerson;
    private JButton btnReward;

    private List<Person> persons;

    // customized fields by me
    private JRadioButton[] rbPersons = { rbCustomer, rbClerk, rbManager };
    ButtonGroup btnGPersons = new ButtonGroup();


    public App() {
        persons = new ArrayList<>();
        btnGPersons.add(rbCustomer);
        btnGPersons.add(rbClerk);
        btnGPersons.add(rbManager);

        // TODO add implementations for all milestones here

        rbCustomer.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (rbCustomer.isSelected()) {
                    tfMonths.setEditable(false);
                    tfSalary.setEditable(false);
                }
                else {
                    tfMonths.setEditable(true);
                    tfSalary.setEditable(true);
                }
            }
        });

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // create person
                for (int i = 0; i < rbPersons.length; i++) {
                    if (rbPersons[i].isSelected()) {
                        Person p = (createPerson(i));
                        if (p == null) break;

                        // add person to list
                        persons.add(p);

                        // show created person
                        taPersons.append(persons.size() + ". " + p.getClass().getSimpleName() + " - " + p.getName() + " (" + p.getAge() + ")\n");

                        // reset fields
                        resetFields();
                    }
                }
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetFields();
                taPersons.setText("");
            }
        });

        btnLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    int n = Integer.parseInt(tfLoad.getText());
                    Person p = persons.get(n -1);

                    tfName.setText(p.getName());
                    tfAge.setText(String.valueOf(p.getAge()));
                    tfMonths.setText("");
                    tfSalary.setText("");

                    if (p instanceof Employee) {
                        tfMonths.setText(String.valueOf(((Employee)p).getMonths_worked()));
                        tfSalary.setText(String.valueOf(((Employee)p).getSalary()));
                    }

                    if (p instanceof Customer) rbCustomer.setSelected(true);
                    else if (p instanceof Clerk) rbClerk.setSelected(true);
                    else if (p instanceof Manager) rbManager.setSelected(true);

                } catch (IndexOutOfBoundsException | NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid load input");
                }
                finally {
                    tfLoad.setText("");
                }
            }
        });

        btnSayHi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                for (Person p : persons) {
                    System.out.println(p);
                }
            }
        });

        btnReward.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    int n = Integer.parseInt(tfLoad.getText());
                    Person p = persons.get(n -1);

                    if (p instanceof Employee) {
                        Double bonus = ((Employee)p).thirteenthMonth();
                        if (bonus < 1) throw new IllegalArgumentException(p.getName() + " has not yet worked");

                        JOptionPane.showMessageDialog(null, "13th Month for " + p.getName() + ": " + String.format("%.2f", bonus));
                    }
                    else throw new IllegalArgumentException(p.getName() + " is not an employee");

                } catch (IndexOutOfBoundsException | NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid load input");
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
                finally {
                    tfLoad.setText("");
                }
            }
        });

        btnSavePerson.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // CREATE FILE
                try (BufferedWriter bw = new BufferedWriter(new FileWriter("database.txt", true))) {

                    if (persons.isEmpty()) throw new IOException();

                    // WRITE TO FILE
                    for (Person p : persons) {
                        bw.write(p.getClass().getSimpleName());
                        bw.write(" ");
                        bw.write(p.getName());
                        bw.write(" ");
                        bw.write(String.valueOf(p.getAge()));
                        bw.write(" ");

                        if (p instanceof Employee) {
                            bw.write(String.valueOf(((Employee) p).getMonths_worked()));
                            bw.write(" ");
                            bw.write(String.valueOf(((Employee) p).getSalary()));
                        }

                        bw.newLine();
                    }
                    JOptionPane.showMessageDialog(null, "List written to file successfully!");
                }
                catch (IOException ex) {
                    System.err.println("Error writing into file");
                }
            }
        });

        btnLoadPerson.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try (BufferedReader br = new BufferedReader(new FileReader("database.txt"))) {

                    Person p = null;
                    String line;

                    while ((line = br.readLine()) != null) {
                        // check what class
                        String[] words = line.split(" ");

                        switch (words[0]) {
                            case "Customer":
                                p = new Customer(words[1], Integer.parseInt(words[2]));
                                break;
                            case "Clerk":
                                p = new Clerk(words[1], Integer.parseInt(words[2]), Integer.parseInt(words[3]), Double.parseDouble(words[4]));
                                break;
                            case "Manager":
                                p = new Manager(words[1], Integer.parseInt(words[2]), Integer.parseInt(words[3]), Double.parseDouble(words[4]));
                                break;
                        }

                        JOptionPane.showMessageDialog(null, "Database imported successfully!");
                        persons.add(p);
                    }

                } catch (IOException ex) {
                    System.err.println("Error reading from file");
                }

                // show on taPersons
                int n = 1;
                for (Person p : persons) {
                    taPersons.append(n + ". " + p.getClass().getSimpleName() + " - " + p.getName() + " (" + p.getAge() + ")\n");
                    n++;
                }
            }
        });
    }

    public static void main(String[] args) {
        // add here how to make GUI visible
        App app = new App();
        app.setContentPane(app.pnlMain);
        app.setSize(500, 600);
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        app.setResizable(false);
        app.setLocationRelativeTo(null);
        app.setVisible(true);
        app.setTitle("Person Database");
    }

    static void giveReward(int n) {

    }

    // customized methods by me
    private Person createPerson(int i) {

        // obtain input fields
        String name;
        int age;
        int months;
        double salary;

        name = validateName();
        age = validateAge();

        if (i == 0) {
            if (name.isBlank() || age == -1) return null;

            return new Customer(name, age);
        }
        else {
            months = validateMonths();
            salary = validateSalary();

            if (name.isBlank() || age == -1 || months == -1 || salary == -1) return null;

            if (i == 1) {
                return new Clerk(name, age, months, salary);
            }
            else if (i == 2) {
                return new Manager(name, age, months, salary);
            }
        }

        return null;
    }

    private boolean errorCheck(String var, String input) {

        if (input.isBlank()) {
            JOptionPane.showMessageDialog(null, var + " field is empty");
            return true;
        }

        for (char c : input.toCharArray()) {
            if (c == '-') {
                JOptionPane.showMessageDialog(null, var + " is a negative integer");
                return true;
            }

            if (!Character.isDigit(c)) {
                JOptionPane.showMessageDialog(null, var + " is a String input");
                return true;
            }
        }
        return false;
    }
    private String validateName() {

        if (tfName.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Name field is blank");
            return "";
        }

        return tfName.getText();
    }
    private int validateAge() {
        if (errorCheck("Age", tfAge.getText())) {
            tfAge.setText("");
            return -1;
        }

        return Integer.parseInt(tfAge.getText());
    }
    private int validateMonths() {
        if (errorCheck("Months", tfMonths.getText())) {
            tfMonths.setText("");
            return -1;
        }

        return Integer.parseInt(tfMonths.getText());
    }
    private double validateSalary() {
        if (errorCheck("Salary", tfSalary.getText())) {
            tfSalary.setText("");
            return -1;
        }

        return Double.parseDouble(tfSalary.getText());
    }

    private void resetFields() {
        btnGPersons.clearSelection();
        tfName.setText("");
        tfAge.setText("");
        tfMonths.setText("");
        tfSalary.setText("");
        tfLoad.setText("");
    }

}