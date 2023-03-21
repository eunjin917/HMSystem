package restaurant;

import main.*;
import helper.*;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.JTable;
import javax.swing.JRadioButton;

/*
 * �뼭��/������� ȭ��
 */

public class SetUserForOrderFrame {

	private JFrame frame;

	// ������ ���� ��ҵ�
	private JLabel lable_logInState;
	private JTable table;
	private JScrollPane scrollPane;
	private JTextField textField_search;
	private JRadioButton radio_lastName;
	private JRadioButton radio_roomNumber;

	// ��� ȭ�鿡�� �߰��� �� ���� ��ҵ� (â�� �ٲ� ����)
	private boolean isLogIn;
	private String userID;
	private String position;

	// JTable���� �ʿ���
	private DefaultTableModel defaultTableModel;
	private String[] header;

	// �����ص� ��
	private String guess = null;
	private String type = "�̸�";
	private String confirmationNumber;

	// Ȯ�� ����
	private boolean isNull = false;

	/**
	 * Create the application.
	 */
	public SetUserForOrderFrame() {
		String[] typeName = MainFrame.getTypeName();
		header = new String[typeName.length + 1];
		header[0] = "����";
		for (int i = 1; i < typeName.length + 1; i++)
			header[i] = typeName[i - 1];

		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1052, 642);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		// �α��� ���� ��
		lable_logInState = new JLabel(" \uB85C\uADF8\uC544\uC6C3 \uC0C1\uD0DC");
		lable_logInState.setForeground(Color.RED);
		lable_logInState.setBounds(12, 561, 174, 42);
		frame.getContentPane().add(lable_logInState);

		// �˻� ��ư
		JButton button_search = new JButton("\uAC80\uC0C9");
		button_search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setInfo(); // guess �ʱ�ȭ

				loadDB(); // �˻��ϱ�
				if (isNull == true)
					JOptionPane.showMessageDialog(null, "�˻� ����� �����ϴ�.");
			}
		});
		button_search.setBounds(609, 520, 91, 23);
		frame.getContentPane().add(button_search);

		// ���� ��ư
		JButton button_Back = new JButton("����");
		button_Back.setBounds(939, 23, 85, 23);
		button_Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame mainFrame = new MainFrame();
				closeSetUserForOrderFrame();
				mainFrame.openMainFrame();
			}
		});
		frame.getContentPane().add(button_Back);

		// �̸����� ������ư
		radio_lastName = new JRadioButton("\uC774\uB984\uC73C\uB85C");
		radio_lastName.setSelected(true);
		radio_lastName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				radio_lastName.setSelected(true);
				radio_roomNumber.setSelected(false);
				type = "�̸�";
			}
		});
		radio_lastName.setBounds(382, 520, 73, 23);
		frame.getContentPane().add(radio_lastName);

		// ���ǹ�ȣ�� ������ư
		radio_roomNumber = new JRadioButton("\uAC1D\uC2E4\uBC88\uD638\uB85C");
		radio_roomNumber.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				radio_lastName.setSelected(false);
				radio_roomNumber.setSelected(true);
				type = "���ǹ�ȣ";
			}
		});
		radio_roomNumber.setBounds(382, 545, 85, 23);
		frame.getContentPane().add(radio_roomNumber);

		/*
		 * ���̺� ����
		 */
		defaultTableModel = new DefaultTableModel(null, header);
		textField_search = new JTextField();
		textField_search.setColumns(10);
		textField_search.setBounds(475, 521, 122, 22);
		frame.getContentPane().add(textField_search);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 64, 1004, 450);
		frame.getContentPane().add(scrollPane);

		table = new JTable(defaultTableModel);
		table.setForeground(Color.BLACK);
		table.setBackground(Color.WHITE);

		scrollPane.setViewportView(table);
		table.getTableHeader().setReorderingAllowed(false);// �̵� �Ұ��ϰ� ����
		table.setAutoCreateRowSorter(true); // ���İ���

		// ��ư Ŭ���Ǹ�
		table.addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				if (table.getSelectedColumn() == 0) {
					if (((String) table.getValueAt(table.getSelectedRow(), 7)).equals("üũ��") == false) // ������°� üũ�ξƴϸ�
						JOptionPane.showMessageDialog(null, "���� üũ�ε��� �ʾҰų� �̹� üũ�ƿ��� �����̹Ƿ�, ������� �ֹ��� �Ұ��մϴ�.");
					else {
						confirmationNumber = (String) table.getValueAt(table.getSelectedRow(), 9);

						OrderFrame orderFrame = new OrderFrame();
						closeSetUserForOrderFrame();
						orderFrame.setConfirmation(confirmationNumber);
						orderFrame.openOrderFrame();
					}
				}
			}
		});

		// ��ư
		table.getColumn("����").setCellRenderer(new ButtonRenderer());
		table.getColumn("����").setCellEditor(new ButtonEditor(new JCheckBox()));

		// ũ�� ����
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// ��� �̹���
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(".\\img\\HTsetuser.PNG"));
		lblNewLabel.setBounds(0, 0, 1036, 623);
		frame.getContentPane().add(lblNewLabel);

		TableColumn column = table.getColumnModel().getColumn(0);
		column.setPreferredWidth(60);
		column = table.getColumnModel().getColumn(1);
		column.setPreferredWidth(30);
		column = table.getColumnModel().getColumn(2);
		column.setPreferredWidth(30);
		column = table.getColumnModel().getColumn(3);
		column.setPreferredWidth(30);
		column = table.getColumnModel().getColumn(5);
		column.setPreferredWidth(80);
		column = table.getColumnModel().getColumn(8);
		column.setPreferredWidth(150);
		column = table.getColumnModel().getColumn(13);
		column.setPreferredWidth(110);
		column = table.getColumnModel().getColumn(14);
		column.setPreferredWidth(150);
		column = table.getColumnModel().getColumn(15);
		column.setPreferredWidth(150);
	}

	// ������� �ʱ�ȭ
	public void setInfo() {
		if (textField_search.getText().length() == 0)
			guess = null;
		else
			guess = textField_search.getText();
	}

	public void loadDB() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			defaultTableModel.setNumRows(0); // ���̺� �ʱ�ȭ

			// �˻�
			String sql;
			if (guess == null)// guess�� null�̸�
				sql = "select * from ������;";
			else
				sql = "select * from ������ where " + type + " = '" + guess + "';";
			rs = stmt.executeQuery(sql);

			if (rs.next() == false) // �˻���� ������
				isNull = true;
			else { // �˻���� ������
				rs = stmt.executeQuery(sql);
				isNull = false;

				Object data[] = new Object[header.length];
				data[0] = "����";
				while (rs.next()) {
					for (int i = 1; i < header.length; i++)
						data[i] = rs.getString(i);
					defaultTableModel.addRow(data);
				}
			}

			// ������� ����ó��
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} catch (SQLException se) {
			se.printStackTrace();
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException se) {
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException se) {
				}
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException se) {
				}
		} // ����ó�� ��
	}

	public void openSetUserForOrderFrame() {
		loadDB();// ���°� ���ÿ� ��ü ���;

		this.isLogIn = MainFrame.getIsLogIn();
		this.userID = MainFrame.getUserID();
		this.position = MainFrame.getPosition();

		if (isLogIn) // ��� ȭ�鿡�� �߰� �� �α��λ��� �� ����
			lable_logInState.setText("�α��� user : " + userID); // �����Ͽ� ǥ��
		frame.setVisible(true);
	}

	public void closeSetUserForOrderFrame() {
		frame.setVisible(false);
	}
}