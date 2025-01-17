package view.panel.manager;

import bean.Course;
import dao.CourseDAO;
import dao.ScheduleDAO;
import dao.WorkDAO;
import dao.impl.CourseDAOImpl;
import dao.impl.ScheduleDAOImpl;
import dao.impl.WorkDAOImpl;
import util.CheckDigitUtil;
import view.minorframe.AddCourseFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

/**
 * User operating panel. Welcome to ManageCoursePanel class, this class extends from Jpael and should be able to show the functions that admin will use for managing courses
 */
public class ManageCoursePanel extends JPanel {

    //fields for course panel
    private JTable table;
    private DefaultTableModel tdm;
    private JTextField jtf1, jtf2, jtf3;
    private JTextArea jta;
    private final CourseDAO courseDAO = new CourseDAOImpl();
    private final ScheduleDAO scheduleDAO = new ScheduleDAOImpl();
    private final WorkDAO workDAO = new WorkDAOImpl();
    private JButton addCourse;
    private JButton modify;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawLine(0, 0, 0, 650);
        g.drawLine(618, 0, 618, 650);
        g.drawLine(0, 320, 100, 320);
        g.drawLine(100, 290, 100, 320);
    }

    public ManageCoursePanel() {
        setLayout(null);
        initPanel();
    }

    // fields in panel
    private void initPanel() {
        initTable();

        addCourse = new JButton("Add Course");
        modify = new JButton("modify");
        JButton delete = new JButton("delete");
        //Course Information
        //Information column
        Label jl10 = new Label("Info Bar");
        jl10.setFont(new Font("", Font.BOLD, 15));

        JScrollPane jsp = new JScrollPane(table);
        JScrollPane jsp0 = new JScrollPane();

        Label jl11 = new Label("ID:");
        Label jl12 = new Label("courseName:");
        Label jl13 = new Label("price:");
        Label jl14 = new Label("benefit:");
        jtf1 = new JTextField(); //ID
        jtf2 = new JTextField(); //courseName
        jtf3 = new JTextField(); //price
        jta = new JTextArea(); //benefit

        jtf1.setEnabled(false);
        jtf2.setEnabled(false);
        jtf3.setEnabled(false);
        jta.setEnabled(false);

        jsp.setBounds(0, 0, 620, 290);

        addCourse.setBounds(50, 530, 120, 30);
        modify.setBounds(250, 530, 120, 30);
        delete.setBounds(450, 530, 120, 30);

        jl10.setBounds(20, 290, 80, 30);
        jl11.setBounds(20, 330, 40, 30);
        jl12.setBounds(170, 330, 80, 30);
        jl13.setBounds(420, 330, 60, 30);
        jl14.setBounds(20, 380, 60, 30);
        jtf1.setBounds(60, 332, 60, 30);
        jtf2.setBounds(270, 332, 100, 30);
        jtf3.setBounds(480, 332, 100, 30);
        jsp0.setBounds(20, 410, 560, 100);

        jsp0.setViewportView(jta);

        add(jsp);
        add(addCourse);
        add(modify);
        add(delete);
        add(jl10);
        add(jl11);
        add(jl12);
        add(jl13);
        add(jl14);
        add(jtf1);
        add(jtf2);
        add(jtf3);
        add(jsp0);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                MouseReleased();
            }
        });

        addCourse.addActionListener(this::addCourseAction);
        modify.addActionListener(this::modifyAction);
        delete.addActionListener(this::deleteAction);
    }

    //add course button
    private void addCourseAction(ActionEvent e) {
        addCourse.setEnabled(false);
        modify.setEnabled(false);
        JFrame frame = new AddCourseFrame();
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                addCourse.setEnabled(true);
                modify.setEnabled(true);
                Object[][] stu = courseDAO.getCourseArrayList();
                String[] tableHeader = {"ID", "courseName", "price", "benefit"};
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setDataVector(stu, tableHeader);
                table.getColumnModel().getColumn(0).setPreferredWidth(20);
                table.updateUI();
            }
        });
    }

    //Delete button
    private void deleteAction(ActionEvent e) {
        if (table.getSelectedRowCount() > 1) {
            JOptionPane.showMessageDialog(null, "Too many selected!", "warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        //Get selected row
        int row = table.getSelectedRow();
        if (row == -1) { //No selection
            JOptionPane.showMessageDialog(null, "Not selected!", "warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int isFlag = JOptionPane.showConfirmDialog(null, "Please confirm");
        if (isFlag > 0) {
            return;
        }

        //search course
        if (scheduleDAO.courseExist((String) table.getValueAt(row, 1))) {
            JOptionPane.showMessageDialog(null, "Delete not supported!", "warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        courseDAO.deleteCourseById((int) table.getValueAt(row, 0));
        tdm.removeRow(row);
        resetValue(); //清空文本框
        jtf2.setEnabled(false);
        jtf3.setEnabled(false);
        jta.setEnabled(false);
        JOptionPane.showMessageDialog(null, "Delete succeeded!");
    }

    //Modify button
    private void modifyAction(ActionEvent e) {
        if (table.getSelectedRowCount() > 1) {
            JOptionPane.showMessageDialog(null, "Too many selected!", "warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        //Get selected row
        int row = table.getSelectedRow();
        if (row == -1) { //No selection
            JOptionPane.showMessageDialog(null, "Not selected!", "warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = jtf1.getText().trim();
        String name = jtf2.getText().trim();
        String price = jtf3.getText().trim();
        String benefit = jta.getText().trim();
        if ("".equals(name) || "".equals(price)) {
            JOptionPane.showMessageDialog(null, "Not filled in correctly!", "warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(!CheckDigitUtil.check(price)) { //digit
            JOptionPane.showMessageDialog(null, "Not filled in correctly!", "warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int isFlag = JOptionPane.showConfirmDialog(null, "Please confirm");
        if (isFlag > 0) {
            return;
        }

        Course newCourse = new Course(Integer.parseInt(id), name, Double.parseDouble(price), benefit);
        courseDAO.changeCourse(newCourse);
        //courseName
        scheduleDAO.changeCourseName(newCourse.getCourseName(), (String) table.getValueAt(row, 1));
        //coach work
        workDAO.changeWorkName(newCourse.getCourseName(), (String) table.getValueAt(row, 1));

        table.setValueAt(name, row, 1);
        table.setValueAt(price, row, 2);
        table.setValueAt(benefit, row, 3);
        JOptionPane.showMessageDialog(null, "Modified successfully!");
    }

    //Listening for mouse click events
    private void MouseReleased() {
        int row = table.getSelectedRow();
        jtf1.setText(String.valueOf(table.getValueAt(row, 0)));
        jtf2.setText(String.valueOf(table.getValueAt(row, 1)));
        jtf3.setText(String.valueOf(table.getValueAt(row, 2)));
        jta.setText(String.valueOf(table.getValueAt(row, 3)));

        jtf2.setEnabled(true);
        jtf3.setEnabled(true);
        jta.setEnabled(true);
    }

    //Initialization Form
    private void initTable() {
        //Create a form
        String[] columnNames = {"ID", "courseName", "price", "benefit"};
        Object[][] rowType = courseDAO.getCourseArrayList();
        tdm = new DefaultTableModel(rowType, columnNames);
        table = new JTable(tdm) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; //Not editable, but optional
            }
        };
        //
        DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
        dtcr.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, dtcr);

        table.getColumnModel().getColumn(0).setPreferredWidth(20);
        table.getColumnModel().getColumn(1).setPreferredWidth(70);
        table.getColumnModel().getColumn(2).setPreferredWidth(70);
        table.getColumnModel().getColumn(3).setPreferredWidth(70);

        table.getTableHeader().setReorderingAllowed(false); //Cannot change the position of a column
    }

    //reset button
    private void resetValue() {
        jtf1.setText("");
        jtf2.setText("");
        jtf3.setText("");
        jta.setText("");
    }
}
