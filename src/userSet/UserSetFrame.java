package userSet;

import main.*;
import helper.*;
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
import java.beans.PropertyChangeEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/*
 * ����ڰ��� ȭ��
 */

public class UserSetFrame {

	private JFrame frame;

	// ������ ���� ��ҵ�
	private JLabel lable_logInState;
	private JTextField textField_ID;
	private JTextField textField_PW;
	private JRadioButton radio_CSR;
	private JRadioButton radio_manager;
	private JTable table;
	private JScrollPane scrollPane;

	// ��� ȭ�鿡�� �߰��� �� ���� ��ҵ� (â�� �ٲ� ����)
	private boolean isLogIn;
	private String userID;
	private String position;

	// JTable���� �ʿ���
	private DefaultTableModel defaultTableModel;
	private String[] header = { "ID", "PW", "����", "����" };
	private String[] typeArray = { "ID", "PW", "����" };

	// setInfo() �ʿ� ��ҵ�
	private String ID = null;
	private String PW = null;
	private String pos = "CSR"; // �⺻�����γֱ�

	// �����ص� ��
	private String thisID = null;
	private String item = null;
	private String origin = null;

	// Ȯ�� ����
	private boolean alreadyID = false; // �̹� ID �ִ���
	private boolean isAble = false; // DB�� �ٲ㵵 �Ǵ���
	private int count = 0; // Ŭ�� ���� ����
	private boolean checkIsEmpty = false; // üũ�ڽ� 1�� ������

	/**
	 * Create the application.
	 */
	public UserSetFrame() {
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

		JLabel lblNewLabel_1 = new JLabel("\uBAA9\uB85D");
		lblNewLabel_1.setBounds(261, 134, 33, 22);
		frame.getContentPane().add(lblNewLabel_1);

		// �α��λ��� ��
		lable_logInState = new JLabel(" \uB85C\uADF8\uC544\uC6C3 \uC0C1\uD0DC");
		lable_logInState.setForeground(Color.RED);
		lable_logInState.setBounds(12, 561, 174, 42);
		frame.getContentPane().add(lable_logInState);

		// �߰� ��ư
		JButton button_create = new JButton("\uCD94\uAC00");
		button_create.setBounds(711, 472, 85, 57);
		button_create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setInfo();
				checkDB(ID); // �ش� ID �̹� �ִ��� Ȯ��

				if (ID.length() == 0 || PW.length() == 0)
					JOptionPane.showMessageDialog(null, "������ ��� �Է��ϼ���.");
				else if (ID.length() > 20 || PW.length() > 20)
					JOptionPane.showMessageDialog(null, "ID�� PW�� �ʹ� ��ϴ�. 20�� ���Ϸ� �ٽ� �Է��ϼ���.");
				else if (alreadyID == true)
					JOptionPane.showMessageDialog(null, "�̹� �����ϴ� ID�Դϴ�. �ٽ� �Է��ϼ���.");
				else {
					createDB();// DB�� �߰�
					JOptionPane.showMessageDialog(null, "�߰��Ǿ����ϴ�.");
					defaultTableModel.addRow(new Object[] { ID, PW, pos, (boolean) false }); // ȭ�鿡 �߰�
				}
			}
		});
		frame.getContentPane().add(button_create);

		// ���� ��ư

		JButton button_delete = new JButton("\uC0AD\uC81C");
		button_delete.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int i;
				for (i = 0; i < table.getRowCount(); i++) {
					if ((boolean) table.getValueAt(i, 3) == true) {
						checkIsEmpty = false;
						thisID = (String) table.getValueAt(i, 0);
						deleteDB();
						defaultTableModel.removeRow(i--);
					} else
						checkIsEmpty = true;
				}

				if (checkIsEmpty == true)
					JOptionPane.showMessageDialog(null, "������ ������ �����ϴ�. �ٽ� �����ϼ���.");
				else {
					JOptionPane.showMessageDialog(null, "�����Ǿ����ϴ�.");
				}
			}
		});
		button_delete.setBounds(711, 373, 85, 23);
		frame.getContentPane().add(button_delete);

		// ���� ��ư
		JButton button_Back = new JButton("����");
		button_Back.setBounds(928, 22, 85, 23);
		button_Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame mainFrame = new MainFrame();
				closeUserSetFrame();
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
		scrollPane.setBounds(296, 134, 403, 262);
		frame.getContentPane().add(scrollPane);

		// üũ�ڽ�
		table.getColumnModel().getColumn(3).setCellEditor(new CheckBoxCellEditor());
		table.getColumnModel().getColumn(3).setCellRenderer(new CWCheckBoxRenderer());

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
						thisID = (String) table.getValueAt(table.getSelectedRow(), 0); // ���õȰ� �ش��ϴ� ID �޾ƿ���
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
						} else if ((table.getSelectedColumn() == 0 || table.getSelectedColumn() == 1)
								&& item.length() > 20) {
							JOptionPane.showMessageDialog(null, "ID�� PW�� �ʹ� ��ϴ�. 20�� ���Ϸ� �ٽ� �Է��ϼ���.");
							isAble = false;
						} else if (table.getSelectedColumn() == 2 && !(item.equals("CSR") || item.equals("�Ŵ���"))) {
							JOptionPane.showMessageDialog(null, "������ CSR �Ǵ� �Ŵ����θ� ������ �� �ֽ��ϴ�.");
							isAble = false;
						} else if (alreadyID == true) {
							JOptionPane.showMessageDialog(null, "�̹� �����ϴ� ID�Դϴ�. �ٽ� �Է��ϼ���.");
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

		// CSR ������ư
		radio_CSR = new JRadioButton("CSR");
		radio_CSR.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				radio_CSR.setSelected(true);
				radio_manager.setSelected(false);
				pos = "CSR"; // CSR ����
			}
		});
		radio_CSR.setSelected(true);
		radio_CSR.setBounds(479, 472, 51, 23);
		frame.getContentPane().add(radio_CSR);

		// �Ŵ��� ������ư
		radio_manager = new JRadioButton("\uB9E4\uB2C8\uC800");
		radio_manager.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				radio_CSR.setSelected(false);
				radio_manager.setSelected(true);
				pos = "�Ŵ���"; // �Ŵ��� ����
			}
		});
		radio_manager.setBounds(479, 506, 61, 23);
		frame.getContentPane().add(radio_manager);

		/*
		 * ������� ���� ��ҵ�
		 */
		textField_ID = new JTextField();
		textField_ID.setBounds(583, 473, 116, 21);
		textField_ID.setColumns(10);
		frame.getContentPane().add(textField_ID);

		textField_PW = new JTextField();
		textField_PW.setBounds(583, 507, 116, 21);
		textField_PW.setColumns(10);
		frame.getContentPane().add(textField_PW);

		JLabel label_ID = new JLabel("ID");
		label_ID.setBackground(Color.WHITE);
		label_ID.setFont(new Font("����", Font.BOLD, 15));
		label_ID.setForeground(Color.BLACK);
		label_ID.setBounds(548, 476, 23, 15);
		frame.getContentPane().add(label_ID);

		JLabel label_PW = new JLabel("PW");
		label_PW.setForeground(Color.BLACK);
		label_PW.setFont(new Font("����", Font.BOLD, 15));
		label_PW.setBounds(548, 510, 23, 15);
		frame.getContentPane().add(label_PW);

		// ��� �̹���
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(".\\img\\HTuserset.PNG"));
		lblNewLabel.setBounds(0, 0, 1036, 641);
		frame.getContentPane().add(lblNewLabel);
	}

	// ������� �ʱ�ȭ
	public void setInfo() {
		ID = textField_ID.getText();
		PW = textField_PW.getText();
	}

	public void loadDB() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		MainFrame mainFrame = new MainFrame();

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(mainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			// �˻�
			String sql = "select * from �α��� where ���� != '���������';";
			rs = stmt.executeQuery(sql);

			Object data[] = new Object[4];
			while (rs.next()) {
				for (int i = 0; i < 3; i++)
					data[i] = rs.getString(i + 1);
				data[3] = new Boolean(false);
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

	public void checkDB(String checkID) {
		for (int i = 0; i < table.getRowCount(); i++) {
			if (table.getValueAt(i, 0).equals(checkID) && !(i == table.getSelectedRow())) {
				alreadyID = true;
				break;
			} else
				alreadyID = false;
		}

		if (checkID.equals("master"))
			alreadyID = true;
	}

	public void modifyDB() {
		Connection conn = null;
		Statement stmt = null;

		MainFrame mainFrame = new MainFrame();

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(mainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			String sql = "update �α��� set " + typeArray[table.getSelectedColumn()] + " = '" + item + "' where ID = '"
					+ thisID + "' limit 1;";
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

			String sql = "insert into �α��� values('" + ID + "', '" + PW + "', '" + pos + "');";
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

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			String sql = "delete from �α��� where ID = '" + thisID + "' limit 1;";
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

	public void openUserSetFrame() {
		loadDB(); // open�� ���ÿ� ����Ʈ ����

		this.isLogIn = MainFrame.getIsLogIn();
		this.userID = MainFrame.getUserID();
		this.position = MainFrame.getPosition();

		if (isLogIn) // ��� ȭ�鿡�� �߰� �� �α��λ��� �� ����
			lable_logInState.setText("�α��� user : " + userID); // �����Ͽ� ǥ��
		frame.setVisible(true);
	}

	public void closeUserSetFrame() {
		frame.setVisible(false);
	}
}