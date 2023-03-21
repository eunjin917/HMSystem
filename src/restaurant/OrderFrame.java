package restaurant;

import helper.*;
import main.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.awt.Font;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.beans.PropertyChangeEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.table.TableModel;

import com.mysql.cj.xdevapi.Table;

/*
 * �ֹ� ȭ��
 */

public class OrderFrame {

	private JFrame frame;

	// ������ ���� ��ҵ�
	private JLabel lable_logInState;
	private JLabel label_sum;
	private JTable table_mealList;
	private JTable table_orderList;
	private JScrollPane scrollPane_mealList;

	// ��� ȭ�鿡�� �߰��� �� ���� ��ҵ� (â�� �ٲ� ����)
	private boolean isLogIn;
	private String userID;
	private String position;

	// JTable���� �ʿ���
	private DefaultTableModel defaultTableModel_mealList;
	private DefaultTableModel defaultTableModel_orderList;
	private String[] header_mealList = { "�޴�", "����", "�߰�" };
	private String[] header_orderList = { "�޴�", "����", "����", "����" };

	// �����ص� ��
	private String confirmation = null;
	private String stringData = null; // �ش� Ȯ�ι�ȣ�� ������� data ��ü
	private int thisRow = -1; // orderList�� �ٲ� �� ����

	// Ȯ�� ����
	private boolean alreadyMenu = false; // �̹� menu �ִ���

	/**
	 * Create the application.
	 */
	public OrderFrame() {
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

		JLabel label = new JLabel("\uBA54\uB274");
		label.setFont(new Font("���� ���", Font.PLAIN, 15));
		label.setBounds(67, 155, 57, 23);
		frame.getContentPane().add(label);

		JLabel lblNewLabel_1 = new JLabel("\uCD94\uAC00 \uBAA9\uB85D");
		lblNewLabel_1.setFont(new Font("���� ���", Font.PLAIN, 15));
		lblNewLabel_1.setBounds(541, 152, 74, 23);
		frame.getContentPane().add(lblNewLabel_1);

		// �α��λ��� ��
		lable_logInState = new JLabel(" \uB85C\uADF8\uC544\uC6C3 \uC0C1\uD0DC");
		lable_logInState.setForeground(Color.RED);
		lable_logInState.setBounds(12, 561, 174, 42);
		frame.getContentPane().add(lable_logInState);

		// ���� ��ư
		JButton button_Back = new JButton("����");
		button_Back.setBounds(928, 22, 85, 23);
		button_Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SetUserForOrderFrame setUserForOrderFrame = new SetUserForOrderFrame();
				closeOrderFrame();
				setUserForOrderFrame.openSetUserForOrderFrame();
			}
		});
		frame.getContentPane().add(button_Back);

		// û�� �ݾ� ǥ��
		label_sum = new JLabel("\uCCAD\uAD6C \uAE08\uC561 : 0\uC6D0");
		label_sum.setBounds(823, 460, 121, 48);
		frame.getContentPane().add(label_sum);

		/*
		 * ���̺� ����
		 */
		// mealList
		defaultTableModel_mealList = new DefaultTableModel(null, header_mealList);
		table_mealList = new JTable(defaultTableModel_mealList);
		table_mealList.addMouseListener(new MouseAdapter() {
			@Override
			// �߰� ��ư
			public void mouseClicked(MouseEvent e) { // Ŭ���Ǹ�
				if (table_mealList.getSelectedColumn() == 2 && table_mealList.getSelectedRow() != -1) {
					checkDB();
					if (alreadyMenu == true)
						modifyDB();
					else
						createDB();
					calPriceSum();// �����հ� ���ϱ�
				}
			}
		});
		scrollPane_mealList = new JScrollPane();
		scrollPane_mealList.setBounds(67, 182, 403, 262);
		frame.getContentPane().add(scrollPane_mealList);

		// ��ư
		table_mealList.getColumn("�߰�").setCellRenderer(new ButtonRenderer());
		table_mealList.getColumn("�߰�").setCellEditor(new ButtonEditor(new JCheckBox()));

		table_mealList.getTableHeader().setReorderingAllowed(false);// �̵� �Ұ��ϰ� ����
		table_mealList.setAutoCreateRowSorter(true);// ����� Ŭ���ϸ� ���� �ڵ�����
		scrollPane_mealList.setViewportView(table_mealList);

		// orderList
		defaultTableModel_orderList = new DefaultTableModel(null, header_orderList);
		table_orderList = new JTable(defaultTableModel_orderList);
		// ���� ��ư
		table_orderList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (table_orderList.getSelectedColumn() == 3 && table_orderList.getSelectedRow() != -1) {
					deleteDB();
					calPriceSum();// �����հ� ���ϱ�
				}
			}
		});
		JScrollPane scrollPane_orderList = new JScrollPane();
		scrollPane_orderList.setBounds(541, 182, 403, 262);
		frame.getContentPane().add(scrollPane_orderList);

		// ��ư
		table_orderList.getColumn("����").setCellRenderer(new ButtonRenderer());
		table_orderList.getColumn("����").setCellEditor(new ButtonEditor(new JCheckBox()));

		table_orderList.getTableHeader().setReorderingAllowed(false);// �̵� �Ұ��ϰ� ����
		table_orderList.setAutoCreateRowSorter(true);// ����� Ŭ���ϸ� ���� �ڵ�����
		scrollPane_orderList.setViewportView(table_orderList);

		// ��� �̹���
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(".\\img\\HTuserset.PNG"));
		lblNewLabel.setBounds(0, 0, 1036, 641);
		frame.getContentPane().add(lblNewLabel);
	}

	public void loadMealDB() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			// mealList �ҷ�����
			String sql = "select * from ���ĸ޴�;";
			rs = stmt.executeQuery(sql);

			Object data[] = new Object[3];
			while (rs.next()) {
				for (int i = 0; i < 2; i++)
					data[i] = rs.getString(i + 1);
				data[2] = "�߰�";
				defaultTableModel_mealList.addRow(data);
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

	public void loadOrderDB() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			// orderList �ҷ�����
			String sql = "select * from �ֹ� where Ȯ�ι�ȣ = '" + confirmation + "';";
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				stringData = rs.getString(2);

				while (stringData.length() != 0) {
					/*
					 * ������ �ϳ� �����ϱ�
					 */

					Object data2[] = new Object[4];

					// �޴� ����
					int endIndex = stringData.indexOf("-");
					data2[0] = stringData.substring(0, endIndex);
					stringData = stringData.substring(endIndex + 1); // �����Ѱ� �����ϱ�

					// ���� ����
					endIndex = stringData.indexOf("*");
					data2[1] = stringData.substring(0, endIndex);
					stringData = stringData.substring(endIndex + 1); // �����Ѱ� �����ϱ�

					// ���� ����
					endIndex = stringData.indexOf("/");
					data2[2] = stringData.substring(0, endIndex);
					stringData = stringData.substring(endIndex + 1); // �����Ѱ� �����ϱ�

					data2[3] = "����";

					// table�� �ֱ�
					defaultTableModel_orderList.addRow(data2);
				}

				stringData = rs.getString(2); // �ٽ� ������� �ʱ�ȭ�ϱ�
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

	public void calPriceSum() {
		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			int sumPrice = 0;

			// �հ� ���ϱ�
			for (int i = 0; i < table_orderList.getRowCount(); i++)
				sumPrice += Integer.parseInt(table_orderList.getValueAt(i, 1).toString());
			label_sum.setText("û���ݾ� : " + sumPrice + " ��"); // �����Ͽ� ǥ��

			// ������ table DB�� �߰�
			String sql = "update ������ set û���ݾ� = '" + sumPrice + "' where Ȯ�ι�ȣ = '" + confirmation + "' limit 1;";
			stmt.executeUpdate(sql);

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
		} // ����ó�� ��
	}

	public void checkDB() {
		for (int i = 0; i < table_orderList.getRowCount(); i++) {
			if (table_orderList.getValueAt(i, 0)
					.equals(table_mealList.getValueAt(table_mealList.getSelectedRow(), 0))) {
				alreadyMenu = true;
				thisRow = i;
				break;
			} else
				alreadyMenu = false;
		}
	}

	public void modifyDB() {
		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			// ���̺� �����ϱ�
			int EA = Integer.parseInt((table_orderList.getValueAt(thisRow, 2).toString())); // ���� ����
			EA++; // �ϳ� �߰�
			defaultTableModel_orderList.setValueAt(EA, thisRow, 2);

			int price = Integer.parseInt((table_orderList.getValueAt(thisRow, 1).toString())); // ���� ����
			price = (price / (EA - 1)) * EA;
			defaultTableModel_orderList.setValueAt(price, thisRow, 1);

			// stringData ���� ����
			stringData = "";
			for (int i = 0; i < table_orderList.getRowCount(); i++) {
				stringData += table_orderList.getValueAt(i, 0);
				stringData += "-";
				stringData += table_orderList.getValueAt(i, 1);
				stringData += "*";
				stringData += table_orderList.getValueAt(i, 2);
				stringData += "/";
			}

			// DB�� �߰�
			String sql = "update �ֹ� set �������" + " = '" + stringData + "' where Ȯ�ι�ȣ = '" + confirmation + "' limit 1;";
			stmt.executeUpdate(sql);

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
		} // ����ó�� ��
	}

	public void createDB() {
		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			// stringData�� �߰��ϰ�
			String target = table_mealList.getValueAt(table_mealList.getSelectedRow(), 0) + "-"
					+ table_mealList.getValueAt(table_mealList.getSelectedRow(), 1) + "*1/"; // �߰��� �ֹ�����(+1)
			if (stringData == null)
				stringData = target;
			else
				stringData += target;

			// DB���� �߰�
			String sql;
			if (table_orderList.getRowCount() == 0) // �ֹ����� ������ ���� �����
				sql = "insert into �ֹ� values('" + confirmation + "', '" + stringData + "');";
			else // �ֹ����� �ϳ��� ������ ���游
				sql = "update �ֹ� set �������" + " = '" + stringData + "' where Ȯ�ι�ȣ = '" + confirmation + "' limit 1;";
			stmt.executeUpdate(sql);

			// jtable�� �߰�
			Object data[] = new Object[] { table_mealList.getValueAt(table_mealList.getSelectedRow(), 0),
					table_mealList.getValueAt(table_mealList.getSelectedRow(), 1), 1, "����" };
			defaultTableModel_orderList.addRow(data);

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
		} // ����ó�� ��
	}

	public void deleteDB() {
		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			// table���� �����ϰ�
			defaultTableModel_orderList.removeRow(table_orderList.getSelectedRow());// �ش� �� ����

			// stringData ���� ����
			stringData = "";
			for (int i = 0; i < table_orderList.getRowCount(); i++) {
				stringData += table_orderList.getValueAt(i, 0);
				stringData += "-";
				stringData += table_orderList.getValueAt(i, 1);
				stringData += "*";
				stringData += table_orderList.getValueAt(i, 2);
				stringData += "/";
			}

			// DB�� �߰�
			String sql = "update �ֹ� set �������" + " = '" + stringData + "' where Ȯ�ι�ȣ = '" + confirmation + "' limit 1;";
			stmt.executeUpdate(sql);

			// ���� stringData�� 0�� �Ǹ���, DB���� �ش� Ȯ�ι�ȣ �ƿ� �����ϱ�
			if (stringData.length() == 0) {
				sql = "delete from �ֹ� where Ȯ�ι�ȣ = '" + confirmation + "' limit 1";
				stmt.executeUpdate(sql);
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
		} // ����ó�� ��
	}

	public void setConfirmation(String cfm) {
		confirmation = cfm;
	}

	public void openOrderFrame() {
		loadMealDB(); // open�� ���ÿ� meal ����Ʈ ����
		loadOrderDB(); // open�� ���ÿ� order ����Ʈ ����
		calPriceSum();// �����հ� ���ϰ� �����Ͽ� ǥ��

		frame.setVisible(true);

		this.isLogIn = MainFrame.getIsLogIn();
		this.userID = MainFrame.getUserID();
		this.position = MainFrame.getPosition();

		if (isLogIn) // ��� ȭ�鿡�� �߰� �� �α��λ��� �� ����
			lable_logInState.setText("�α��� user : " + userID); // �����Ͽ� ǥ��
		frame.setVisible(true);
	}

	public void closeOrderFrame() {
		frame.setVisible(false);
	}
}
