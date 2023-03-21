package mealSet;

import main.*;
import helper.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/*
 * ���ĸ޴����� ȭ��
 */

public class MealSetFrame {

	private JFrame frame;

	// ������ ���� ��ҵ�
	private JLabel lable_logInState;
	private JTextField textField_menu;
	private JTextField textField_price;
	private JTable table;
	private JScrollPane scrollPane;

	// ��� ȭ�鿡�� �߰��� �� ���� ��ҵ� (â�� �ٲ� ����)
	private boolean isLogIn;
	private String userID;
	private String position;

	// JTable���� �ʿ���
	private DefaultTableModel defaultTableModel;
	private String[] header = { "�޴�", "����", "����" };
	private String[] typeArray = { "�޴�", "����" };

	// setInfo() �ʿ� ��ҵ�
	private String menu = null;
	private String price = null;

	// �����ص� ��
	private String thisMenu = null;
	private String item = null;
	private String origin = null;

	// Ȯ�� ����
	private boolean alreadyMenu = false; // �̹� �޴� �ִ���
	private boolean isAble = false; // DB�� �ٲ㵵 �Ǵ���
	private int count = 0; // Ŭ�� ���� ����
	private boolean checkIsEmpty = false; // üũ�ڽ� 1�� ������

	/**
	 * Create the application.
	 */
	public MealSetFrame() {
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

		JLabel label = new JLabel("\uBA54\uB274 :");
		label.setFont(new Font("����", Font.PLAIN, 15));
		label.setBounds(675, 431, 38, 18);
		frame.getContentPane().add(label);

		JLabel lblNewLabel_1 = new JLabel("\uAC00\uACA9 :");
		lblNewLabel_1.setFont(new Font("����", Font.PLAIN, 15));
		lblNewLabel_1.setBounds(675, 463, 38, 18);
		frame.getContentPane().add(lblNewLabel_1);

		// �α��λ��� ��
		lable_logInState = new JLabel(" \uB85C\uADF8\uC544\uC6C3 \uC0C1\uD0DC");
		lable_logInState.setForeground(Color.RED);
		lable_logInState.setBounds(12, 561, 174, 42);
		frame.getContentPane().add(lable_logInState);

		// �߰� ��ư
		JButton button_create = new JButton("\uCD94\uAC00");
		button_create.setBounds(843, 431, 85, 50);
		button_create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setInfo();
				checkDB(menu); // �ش� �޴� �̹� �ִ��� Ȯ��

				if (menu.length() == 0 || menu.length() == 0)
					JOptionPane.showMessageDialog(null, "������ ��� �Է��ϼ���.");
				else if (alreadyMenu == true)
					JOptionPane.showMessageDialog(null, "�̹� �����ϴ� �޴��Դϴ�. �ٽ� �Է��ϼ���.");
				else {
					createDB();// DB�� �߰�
					JOptionPane.showMessageDialog(null, "�߰��Ǿ����ϴ�.");
					defaultTableModel.addRow(new Object[] { menu, price, (boolean) false }); // ȭ�鿡 �߰�
				}
			}
		});
		frame.getContentPane().add(button_create);

		// ���� ��ư
		JButton button_delete = new JButton("\uC0AD\uC81C");
		button_delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < table.getRowCount(); i++)
					if ((boolean) table.getValueAt(i, 2) == true) {
						thisMenu = (String) table.getValueAt(i, 0);
						deleteDB();
						defaultTableModel.removeRow(i--);
						checkIsEmpty = false;
					}

				if (checkIsEmpty == true)
					JOptionPane.showMessageDialog(null, "������ ������ �����ϴ�. �ٽ� �����ϼ���.");
				else {
					JOptionPane.showMessageDialog(null, "�����Ǿ����ϴ�.");
					checkIsEmpty = true;
				}
			}
		});
		button_delete.setBounds(843, 379, 85, 23);
		frame.getContentPane().add(button_delete);

		// ���� ��ư
		JButton button_Back = new JButton("����");
		button_Back.setBounds(939, 22, 85, 23);
		button_Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame mainFrame = new MainFrame();
				closeMealSetFrame();
				mainFrame.openMainFrame();
			}
		});
		frame.getContentPane().add(button_Back);

		/*
		 * ���̺� ����
		 */
		defaultTableModel = new DefaultTableModel(null, header);
		table = new JTable(defaultTableModel);
		scrollPane = new JScrollPane();
		scrollPane.setBounds(221, 104, 610, 298);
		frame.getContentPane().add(scrollPane);

		// üũ�ڽ�
		table.getColumnModel().getColumn(2).setCellEditor(new CheckBoxCellEditor());
		table.getColumnModel().getColumn(2).setCellRenderer(new CWCheckBoxRenderer());

		table.getTableHeader().setReorderingAllowed(false);// �̵� �Ұ��ϰ� ����
		table.setAutoCreateRowSorter(true);// ����� Ŭ���ϸ� ���� �ڵ�����

		// Ŭ���ϸ�
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1) { // Ŭ��
					if (table.getSelectedRow() != -1 && table.getSelectedColumn() != 3) {
						origin = (String) table.getValueAt(table.getSelectedRow(), table.getSelectedColumn()); // ���õȰ�
						// ������
						thisMenu = (String) table.getValueAt(table.getSelectedRow(), 0); // ���õȰ� �ش��ϴ� ID �޾ƿ���
						count = 1;
					}
				}
			}
		});

		// ���� ���
		table.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) { // �����Ǹ�
				if (count == 2) {
					if (table.getSelectedRow() != -1 && table.getSelectedColumn() != 3) {
						item = (String) table.getValueAt(table.getSelectedRow(), table.getSelectedColumn()); // ���õȰ� �ٲܰ�
																												// �ʱ�ȭ

						if (table.getSelectedColumn() == 0) // ID�� �ٲٴ°Ÿ�
							checkDB(item);// �ٲ� �޴��� ��ġ���� Ȯ��

						if (item.length() == 0) {
							JOptionPane.showMessageDialog(null, "������ ��� �Է��ϼ���.");
							isAble = false;
						} else if (alreadyMenu == true) {
							JOptionPane.showMessageDialog(null, "�̹� �����ϴ� �޴��Դϴ�. �ٽ� �Է��ϼ���.");
							isAble = false;
						} else { // ID �ٲٴ°� �ƴϸ�
							modifyDB();// DB�� ����
							isAble = true;
						}

						if (isAble == false)
							table.setValueAt(origin, table.getSelectedRow(), table.getSelectedColumn());
					}
					count--;
				} else if (count == 1)
					count++;
			}
		});
		scrollPane.setViewportView(table);

		/*
		 * ������� ���� ��ҵ�
		 */
		textField_menu = new JTextField();
		textField_menu.setBounds(715, 431, 116, 21);
		textField_menu.setColumns(10);
		frame.getContentPane().add(textField_menu);

		textField_price = new JTextField();
		textField_price.setBounds(715, 462, 116, 21);
		textField_price.setColumns(10);
		frame.getContentPane().add(textField_price);
		// ����̹���
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(".\\img\\HTmealset.PNG"));
		lblNewLabel.setBounds(0, 0, 1036, 622);
		frame.getContentPane().add(lblNewLabel);
	}

	// ������� �ʱ�ȭ
	public void setInfo() {
		menu = textField_menu.getText();
		price = textField_price.getText();
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

			// �˻�
			String sql = "select * from ���ĸ޴�;";
			rs = stmt.executeQuery(sql);

			Object data[] = new Object[3];
			while (rs.next()) {
				for (int i = 0; i < 2; i++)
					data[i] = rs.getString(i + 1);
				data[2] = new Boolean(false);
				defaultTableModel.addRow(data);
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

	// ���ĸ޴� DB �ҷ�����
	public void checkDB(String checkMenu) {
		for (int i = 0; i < table.getRowCount(); i++) {
			if (table.getValueAt(i, 0).equals(checkMenu) && !(i == table.getSelectedRow())) {
				alreadyMenu = true;
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

			String sql;

			item = (String) table.getValueAt(table.getSelectedRow(), table.getSelectedColumn());

			sql = "update ���ĸ޴� set " + typeArray[table.getSelectedColumn()] + " = '" + item + "' where �޴� = '"
					+ thisMenu + "' limit 1;";
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

		MainFrame mainFrame = new MainFrame();

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(mainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			String sql = "insert into ���ĸ޴� values('" + menu + "', '" + price + "');";
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

	public void deleteDB() {
		Connection conn = null;
		Statement stmt = null;

		MainFrame mainFrame = new MainFrame();

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(mainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			String sql = "delete from ���ĸ޴� where �޴� = '" + thisMenu + "' limit 1;";
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

	public void openMealSetFrame() {
		loadDB(); // open�� ���ÿ� ����Ʈ ����

		this.isLogIn = MainFrame.getIsLogIn();
		this.userID = MainFrame.getUserID();
		this.position = MainFrame.getPosition();

		if (isLogIn) // ��� ȭ�鿡�� �߰� �� �α��λ��� �� ����
			lable_logInState.setText("�α��� user : " + userID); // �����Ͽ� ǥ��
		frame.setVisible(true);
	}

	public void closeMealSetFrame() {
		frame.setVisible(false);
	}
}