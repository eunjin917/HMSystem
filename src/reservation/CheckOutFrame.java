package reservation;

import main.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import java.awt.Font;
import javax.swing.ImageIcon;

/*
 * üũ�ƿ� ȭ��
 */
public class CheckOutFrame {

	private JFrame frame;

	// ������ ���� ��ҵ�
	private JLabel lable_logInState;
	private JLabel label_sum;
	private JButton button_back;
	private JButton button_checkOut;
	private JTable table_orderList;
	private JTextField textField_feedback;

	// ��� ȭ�鿡�� �߰��� �� ���� ��ҵ� (â�� �ٲ� ����)
	private boolean isLogIn;
	private String userID;
	private String position;

	// JTable���� �ʿ���
	private DefaultTableModel defaultTableModel_orderList;
	private String[] header_orderList = { "�޴�", "����", "����" };

	// �����ص� ��
	private String confirmation; // �ش� Ȯ�ι�ȣ ����
	private String feedback = null;
	private int sumPrice = 0;

	/**
	 * Create the application.
	 */
	public CheckOutFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 567, 448);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel label = new JLabel("\uD53C\uB4DC\uBC31");
		label.setFont(new Font("���� ���", Font.PLAIN, 15));
		label.setBounds(12, 58, 57, 23);
		frame.getContentPane().add(label);

		// �α��� ���� ��
		lable_logInState = new JLabel(" \uB85C\uADF8\uC544\uC6C3 \uC0C1\uD0DC");
		lable_logInState.setForeground(Color.RED);
		lable_logInState.setBounds(12, 362, 173, 42);
		frame.getContentPane().add(lable_logInState);

		// üũ�ƿ� ��ư ��ư
		button_checkOut = new JButton("\uCD94\uAC00\uACB0\uC81C \uBC0F \uCCB4\uD06C\uC544\uC6C3\uD558\uAE30");
		button_checkOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// �ֹ��� ��� -> �����ϵ���
				setInfo(); // feedback �ʱ�ȭ
				modifyDB(); // DB�� üũ�ƿ��ߴٰ� �����ϱ�
				JOptionPane.showMessageDialog(null, "üũ�ƿ� �Ǿ����ϴ�.");

				ReservationFrame reservationFrame = new ReservationFrame();
				closeCheckOutFrame();
				reservationFrame.openReservationFrame();
			}

		});
		button_checkOut.setBounds(347, 343, 192, 34);
		frame.getContentPane().add(button_checkOut);

		// ���� ��ư
		button_back = new JButton("\uC774\uC804");
		button_back.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				MainFrame mainFrame = new MainFrame();
				closeCheckOutFrame();
				mainFrame.openMainFrame();
			}

		});
		button_back.setBounds(452, 10, 91, 23);
		frame.getContentPane().add(button_back);

		/*
		 * ���� ���
		 */
		textField_feedback = new JTextField();
		textField_feedback.setBounds(12, 81, 403, 48);
		frame.getContentPane().add(textField_feedback);
		textField_feedback.setColumns(10);

		// û�� �ݾ� ǥ��
		label_sum = new JLabel("\uCCAD\uAD6C \uAE08\uC561 : 0\uC6D0");
		label_sum.setBounds(161, 314, 160, 48);
		frame.getContentPane().add(label_sum);

		/*
		 * ���̺� ����
		 */
		// orderList
		defaultTableModel_orderList = new DefaultTableModel(null, header_orderList);
		table_orderList = new JTable(defaultTableModel_orderList);

		JScrollPane scrollPane_orderList = new JScrollPane();
		scrollPane_orderList.setBounds(12, 139, 403, 175);
		frame.getContentPane().add(scrollPane_orderList);

		table_orderList.setEnabled(false); // �̿���ϰ�
		table_orderList.getTableHeader().setReorderingAllowed(false);// �̵� �Ұ��ϰ� ����
		table_orderList.setAutoCreateRowSorter(true);// ����� Ŭ���ϸ� ���� �ڵ�����
		scrollPane_orderList.setViewportView(table_orderList);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(".\\img\\HTcheckout.PNG"));
		lblNewLabel.setBounds(0, 0, 551, 423);
		frame.getContentPane().add(lblNewLabel);
	}

	public void setConfirmation(String cfmn) {
		confirmation = cfmn;
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

			if (rs.next() == false) // �˻���� ������
				JOptionPane.showMessageDialog(null, "������� �̿� ���� ���� ���Դϴ�.");

			else {
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					String stringData = rs.getString(2);

					while (stringData.length() != 0) {
						/*
						 * ������ �ϳ� �����ϱ�
						 */

						Object data2[] = new Object[3];

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

						// table�� �ֱ�
						defaultTableModel_orderList.addRow(data2);
					}
					stringData = rs.getString(2); // �ٽ� ������� �ʱ�ȭ�ϱ�
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

	public void calPriceSum() {
		for (int i = 0; i < table_orderList.getRowCount(); i++)
			sumPrice += Integer.parseInt(table_orderList.getValueAt(i, 1).toString());
	}

	public void setInfo() {
		feedback = textField_feedback.getText();

		if (feedback.length() == 0)
			feedback = "�ǵ�� ����";
	}

	public void modifyDB() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			// ������ table �ٲٱ�
			String sql = "update ������ set ������� = 'üũ�ƿ�', �ǵ�� = '" + feedback + "' where Ȯ�ι�ȣ= '" + confirmation
					+ "' limit 1;";
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
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException se) {
				}
		} // ����ó�� ��
	}

	public void openCheckOutFrame() {
		loadOrderDB();// open���ڸ��� opderlist����
		calPriceSum();// �����հ� ���ϱ�
		label_sum.setText("û���ݾ� : " + sumPrice + " ��"); // �����Ͽ� ǥ��

		this.isLogIn = MainFrame.getIsLogIn();
		this.userID = MainFrame.getUserID();
		this.position = MainFrame.getPosition();

		if (isLogIn) // ��� ȭ�鿡�� �߰� �� �α��λ��� �� ����
			lable_logInState.setText("�α��� user : " + userID); // �����Ͽ� ǥ��
		frame.setVisible(true);
	}

	public void closeCheckOutFrame() {
		frame.setVisible(false);
	}
}
