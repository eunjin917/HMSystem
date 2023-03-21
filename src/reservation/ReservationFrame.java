package reservation;

import main.*;
import helper.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
 * ���� ȭ��
 */

public class ReservationFrame {

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
	private String confirmationNumber = null;
	private String roomNumber = null;
	private String item = null;
	private String origin = null;
	private int thisRow = 0;

	// ��¥ ���
	private String startDate;
	private String endDate;

	// Ȯ�� ����
	private boolean isNull = false;
	private boolean[] roomIsAble = null; // üũ��~üũ�ƿ� ���̿� �ش� ������ ��밡������
	private ArrayList<String> ableRoomArray = null; // �̿� ������ ���Ǹ� ����
	private String[] roomArray = null; // �Ϲ� ���ǹ�ȣ ��� ����
	private int count = 0; // Ŭ�� ���� ����
	private boolean isAble = false; // DB�� �ٲ㵵 �Ǵ���

	/**
	 * Create the application.
	 */
	public ReservationFrame() {
		String[] typeName = MainFrame.getTypeName();
		header = new String[typeName.length + 1];
		header[0] = "����";
		for (int i = 1; i < typeName.length + 1; i++)
			header[i] = typeName[i - 1];

		roomIsAble = new boolean[100]; // ��� false�������
		for (int i = 0; i < 100; i++) // �ϴ� ���� ó���� true�� �ʱ�ȭ���Ѿ��� (���� ��� ����)
			roomIsAble[i] = true;

		ableRoomArray = new ArrayList<String>();
		roomArray = new String[] { "201", "202", "203", "204", "205", "206", "207", "208", "209", "210", "301", "302",
				"303", "304", "305", "306", "307", "308", "309", "310", "401", "402", "403", "404", "405", "406", "407",
				"408", "409", "410", "501", "502", "503", "504", "505", "506", "507", "508", "509", "510", "601", "602",
				"603", "604", "605", "606", "607", "608", "609", "610", "701", "702", "703", "704", "705", "706", "707",
				"708", "709", "710", "801", "802", "803", "804", "805", "806", "807", "808", "809", "810", "901", "902",
				"903", "904", "905", "906", "907", "908", "909", "910", "1001", "1002", "1003", "1004", "1005", "1006",
				"1007", "1008", "1009", "1010", "1101", "1102", "1103", "1104", "1105", "1106", "1107", "1108", "1109",
				"1110" };

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
		button_search.setBounds(579, 26, 91, 23);
		frame.getContentPane().add(button_search);

		// �����ϱ� ��ư
		JButton button_update = new JButton("\uC608\uC57D\uD558\uAE30");
		button_update.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UpdateFrame updateFrame = new UpdateFrame();
				closeReservationFrame();
				updateFrame.openUpdateFrame();
			}
		});
		button_update.setBounds(417, 545, 115, 42);
		frame.getContentPane().add(button_update);

		// ���� ��ư
		JButton button_delete = new JButton("\uC608\uC57D \uC0AD\uC81C");
		button_delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int checkNum = 0;
					for (int i = 0; i < table.getRowCount(); i++)
						if ((boolean) table.getValueAt(i, 0) == true) {
							checkNum++;

							confirmationNumber = (String) table.getValueAt(i, 9);
							roomNumber = (String) table.getValueAt(i, 4);
							startDate = (String) table.getValueAt(i, 10);
							endDate = (String) table.getValueAt(i, 11);

							deleteDB(); // �����ϱ�
							waitToConfirm();// �����ϸ鼭 �� �ڸ��� ������� ���� �� �ִ���

							defaultTableModel.setNumRows(0);// ���̺� �������
							loadDB();// �ٽ� ���
						}

					if (checkNum == 0)
						JOptionPane.showMessageDialog(null, "������ ������ �����ϴ�. �ٽ� �����ϼ���.");
					else {
						JOptionPane.showMessageDialog(null, "�����Ǿ����ϴ�.");
					}
					checkNum = 0;

				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		});
		button_delete.setBounds(925, 545, 91, 42);
		frame.getContentPane().add(button_delete);

		// ���ǰ��� ��ư
		JButton button_roomPay = new JButton("\uAC1D\uC2E4 \uACB0\uC81C\uD558\uAE30");
		button_roomPay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int checkNum = 0;
				for (int i = 0; i < table.getRowCount(); i++) {
					if (table.getValueAt(i, 0).equals(true)) {
						checkNum++;
						thisRow = i;
					}
				}

				if (checkNum == 0)
					JOptionPane.showMessageDialog(null, "���ǰ����� ���������� �����ϼ���.");
				else if (checkNum >= 2)
					JOptionPane.showMessageDialog(null, "���ǰ����� ���������� �ϳ��� �����ϼ���.");
				else if (checkNum == 1) { // üũ�� ������ ����
					if (!table.getValueAt(table.getSelectedRow(), 7).equals("����Ϸ�")) // ������°� ����Ϸ� �ƴϸ�
						JOptionPane.showMessageDialog(null, "���� ������ Ȯ������ �ʾҰų�, �̹� ������ �����Դϴ�.");
					else {
						PaymentFrame paymentFrame = new PaymentFrame();
						paymentFrame.setConfirmation((String) table.getValueAt(thisRow, 9));
						closeReservationFrame();
						paymentFrame.openPaymentFrame();

						defaultTableModel.setNumRows(0);// ���̺� �������
						loadDB();// �ٽ� ���
					}
				}
				checkNum = 0;
			}
		});
		button_roomPay.setBounds(544, 545, 115, 42);
		frame.getContentPane().add(button_roomPay);

		// üũ�� ��ư
		JButton button_checkIn = new JButton("\uCCB4\uD06C\uC778");
		button_checkIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int checkNum = 0;
				for (int i = 0; i < table.getRowCount(); i++)
					if (table.getValueAt(i, 0).equals(true)) {
						checkNum++;
						thisRow = i;
					}

				if (checkNum == 0)
					JOptionPane.showMessageDialog(null, "üũ���� ���������� �����ϼ���.");
				else if (checkNum >= 2)
					JOptionPane.showMessageDialog(null, "üũ���� ���������� �ϳ��� �����ϼ���.");
				else if (checkNum == 1) { // üũ�� ������ ����
					if (((String) table.getValueAt(table.getSelectedRow(), 7)).equals("���ǰ����Ϸ�") == false) // ������°�
																											// �����Ϸ�ƴϸ�
						JOptionPane.showMessageDialog(null, "���� �������� �ʾҰų�, �̹� üũ�ε� �����Դϴ�.");
					else {
						CheckInFrame checkInFrame = new CheckInFrame();
						checkInFrame.setConfirmation((String) table.getValueAt(thisRow, 9));
						closeReservationFrame();
						checkInFrame.openCheckInFrame();

						defaultTableModel.setNumRows(0);// ���̺� �������
						loadDB();// �ٽ� ���
					}
				}
				checkNum = 0;
			}

		});
		button_checkIn.setBounds(671, 545, 115, 42);
		frame.getContentPane().add(button_checkIn);

		// üũ�ƿ� ��ư
		JButton button_checkOut = new JButton("\uCCB4\uD06C\uC544\uC6C3");
		button_checkOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int checkNum = 0;
				for (int i = 0; i < table.getRowCount(); i++) {
					if (table.getValueAt(i, 0).equals(true)) {
						checkNum++;
						thisRow = i;
					}
				}

				if (checkNum == 0)
					JOptionPane.showMessageDialog(null, "üũ�ƿ��� ���������� �����ϼ���.");
				else if (checkNum >= 2)
					JOptionPane.showMessageDialog(null, "üũ�ƿ��� ���������� �ϳ��� �����ϼ���.");
				else if (checkNum == 1) { // üũ�� ������ ����
					if (((String) table.getValueAt(table.getSelectedRow(), 7)).equals("üũ��") == false) // ������°� üũ�ξƴϸ�
						JOptionPane.showMessageDialog(null, "���� üũ�ε��� �ʾҰų�, �̹� üũ�ƿ��� �����Դϴ�.");
					else {
						CheckOutFrame checkOutFrame = new CheckOutFrame();
						checkOutFrame.setConfirmation((String) table.getValueAt(thisRow, 9));
						closeReservationFrame();
						checkOutFrame.openCheckOutFrame();

						defaultTableModel.setNumRows(0);// ���̺� �������
						loadDB();// �ٽ� ���
					}
				}
				checkNum = 0;
			}
		});
		button_checkOut.setBounds(798, 545, 115, 42);
		frame.getContentPane().add(button_checkOut);

		// ���� ��ư
		JButton button_Back = new JButton("����");
		button_Back.setBounds(939, 23, 85, 23);
		button_Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame mainFrame = new MainFrame();
				closeReservationFrame();
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
		radio_lastName.setBounds(352, 26, 73, 23);
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
		radio_roomNumber.setBounds(352, 51, 85, 23);
		frame.getContentPane().add(radio_roomNumber);

		/*
		 * ���̺� ����
		 */
		defaultTableModel = new DefaultTableModel(null, header);
		textField_search = new JTextField();
		textField_search.setColumns(10);
		textField_search.setBounds(445, 27, 122, 22);
		frame.getContentPane().add(textField_search);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 80, 1004, 450);
		frame.getContentPane().add(scrollPane);

		table = new JTable(defaultTableModel);
		table.setForeground(Color.BLACK);
		table.setBackground(Color.WHITE);

		scrollPane.setViewportView(table);
		table.getTableHeader().setReorderingAllowed(false);// �̵� �Ұ��ϰ� ����
		table.setAutoCreateRowSorter(true); // ���İ���

		// üũ�ڽ�
		table.getColumnModel().getColumn(0).setCellEditor(new CheckBoxCellEditor());
		table.getColumnModel().getColumn(0).setCellRenderer(new CWCheckBoxRenderer());

		// Ŭ���ϸ�
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1) { // Ŭ��
					if (table.getSelectedColumn() != -1 && table.getSelectedColumn() != 0
							&& table.getSelectedColumn() != 4 && table.getSelectedColumn() != 5
							&& table.getSelectedColumn() != 7 && table.getSelectedColumn() != 8
							&& table.getSelectedColumn() != 9 && table.getSelectedColumn() != 10
							&& table.getSelectedColumn() != 11 && table.getSelectedColumn() != 12
							&& table.getSelectedColumn() != 13 && table.getSelectedColumn() != 14
							&& table.getSelectedColumn() != 15) {
						origin = (String) table.getValueAt(table.getSelectedRow(), table.getSelectedColumn()); // ���õȰſ�����
						confirmationNumber = (String) table.getValueAt(table.getSelectedRow(), 9); // ���õȰ� �ش��ϴ� Ȯ�ι�ȣ
						// �޾ƿ���
						count = 1;
					}
				}
			}
		});

		// ���� ���
		table.addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) { // �����Ǹ�
				if (count == 2) {
					if (table.getSelectedColumn() != -1 && table.getSelectedColumn() != 0
							&& table.getSelectedColumn() != 4 && table.getSelectedColumn() != 5
							&& table.getSelectedColumn() != 7 && table.getSelectedColumn() != 8
							&& table.getSelectedColumn() != 9 && table.getSelectedColumn() != 10
							&& table.getSelectedColumn() != 11 && table.getSelectedColumn() != 12
							&& table.getSelectedColumn() != 13 && table.getSelectedColumn() != 14
							&& table.getSelectedColumn() != 15) {
						item = (String) table.getValueAt(table.getSelectedRow(), table.getSelectedColumn()); // ���õȰ� �ٲܰ�
						// �ʱ�ȭ

						if (item.length() == 0) {
							JOptionPane.showMessageDialog(null, "������ ��� �Է��ϼ���.");
							isAble = false;
						} else {// �Ⱥ��������
							modifyDB();// DB�� ����
							isAble = true;
						}
					}

					if (isAble == false)
						table.setValueAt(origin, table.getSelectedRow(), table.getSelectedColumn());

					count--;
				} else if (count == 1)
					count++;
			}
		});
		scrollPane.setViewportView(table);

		// ũ�� ����
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

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

		// ��� �̹���
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(".\\img\\HTreservation.PNG"));
		lblNewLabel.setBounds(0, 0, 1036, 622);
		frame.getContentPane().add(lblNewLabel);
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
				data[0] = new Boolean(false);
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

			sql = "update ������ set " + header[table.getSelectedColumn()] + " = '" + item + "' where Ȯ�ι�ȣ = '"
					+ confirmationNumber + "' limit 1;";
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

	public void deleteDB() throws ParseException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			// ������ table�� ����
			String sql = "delete from ������ where Ȯ�ι�ȣ = '" + confirmationNumber + "' limit 1;";
			stmt.executeUpdate(sql);

			// ȣ�ڰ��� table�� ����
			if (!roomNumber.equals("���� ����")) { // �ڸ� ���� ��쿡�� ���� �Ƚ�Ŵ
				String thisDate; // for���� ���� ��¥(String)
				SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
				Calendar cal = Calendar.getInstance();
				Date inDate = fm.parse(startDate); // üũ�� ��¥
				Date outDate = fm.parse(endDate); // üũ�ƿ� ��¥
				for (cal.setTime(inDate); cal.getTime().compareTo(outDate) < 0; cal.add(Calendar.DATE, 1)) {
					thisDate = fm.format(cal.getTime());
					sql = "update ȣ�ڰ��� set " + roomNumber + "ȣ = 1 where ��¥ = '" + thisDate + "';";
					stmt.executeUpdate(sql);
				}
			}

			// �ֹ� table���� ���� �ִٸ�, ����
			sql = "select * from �ֹ� where Ȯ�ι�ȣ = '" + confirmationNumber + "' limit 1;";
			rs = stmt.executeQuery(sql);
			while (rs.next()) { // ���� �ִٸ�
				sql = "delete from �ֹ� where Ȯ�ι�ȣ = '" + confirmationNumber + "' limit 1;"; // �ֹ����� ����
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
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException se) {
				}
		} // ����ó�� ��
	}

	public void waitToConfirm() throws ParseException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			String sql;
			String thisDate; // for���� ���� ��¥(String)
			SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
			Calendar cal = Calendar.getInstance();
			Date inDate = fm.parse(startDate); // üũ�� ��¥
			Date outDate = fm.parse(endDate); // üũ�ƿ� ��¥

			/*
			 * ������ ������ üũ�γ�¥~üũ�ƿ���¥ ���� ������ �ִ��� Ȯ��
			 */

			for (cal.setTime(inDate); cal.getTime().compareTo(outDate) < 0; cal.add(Calendar.DATE, 1)) {
				thisDate = fm.format(cal.getTime());
				sql = "select * from ������ where ������� = '������' AND üũ�γ�¥ = '" + thisDate + "';"; // �������ΰ� ã��
				rs = stmt.executeQuery(sql);

				/*
				 * �ش� ��¥��, ������ �ش��ϴ°� �ִٸ�, �� ������ üũ��~üũ�ƿ� ��¥ �� �� �̿밡���Ѱ��� �ִ���
				 */
				while (rs.next()) {
					count++;
					String cfmn = rs.getString("Ȯ�ι�ȣ"); // Ȯ�ι�ȣ ����

					String td; // for���� ���� ��¥(String)
					Calendar calendar = Calendar.getInstance();
					Date in = fm.parse(rs.getString("üũ�γ�¥")); // üũ�� ��¥
					Date out = fm.parse(rs.getString("üũ�ƿ���¥")); // üũ�ƿ� ��¥
					for (calendar.setTime(in); calendar.getTime().compareTo(out) < 0; calendar.add(Calendar.DATE, 1)) {
						td = fm.format(calendar.getTime());
						sql = "select * from ȣ�ڰ��� where ��¥ = '" + td + "';"; // �ش� ��¥�� ȣ�ڰ��� table �ҷ�����
						rs2 = stmt.executeQuery(sql);
						while (rs2.next()) {
							for (int i = 2; i <= 101; i++) {
								if (rs2.getString(i).equals("1") && roomIsAble[i - 2] == true) // �ش� ������ ���Ͽ� �̿밡���ϰ� ��������
									// �̿밡���ߴٸ�
									roomIsAble[i - 2] = true; // �̿� ���� ��Ͽ� true�� �߰�
								else
									roomIsAble[i - 2] = false; // false�־�, �� ������ �̿� �Ұ��ϹǷ� ��� ���ϰ�
							}
						}

						for (int i = 0; i < 100; i++) // ���������� ������ rooIsAble �迭����,
							if (roomIsAble[i] == true) // roomIsAble�� true�� ���� ��� ����
								ableRoomArray.add(roomArray[i]); // �ش� �� ȣ���� ��� ����

						// ���� ������ �ִٸ�! ����Ȯ������ ����
						if (ableRoomArray.isEmpty() == false) {
							int random = (int) Math.random() * ableRoomArray.size(); // ���ǹ�ȣ �������� �ϳ� ����

							int price = 0;
							rs2 = stmt.executeQuery(sql); // select * from ȣ�ڰ��� where ��¥ = '" + td + "';
							while (rs2.next())
								price += Integer.parseInt(rs2.getString("���ǰ���")); // ���� ��� �߰��ϸ鼭 ���

							sql = "update ������ set ���ǹ�ȣ = '" + ableRoomArray.get(random) + "', ���ǰ��� = '" + price
									+ "', ������� = '����Ϸ�' where Ȯ�ι�ȣ = '" + cfmn + "';";
							stmt.executeUpdate(sql);

							// ȣ�ڰ��� table���� update (�̿� �Ұ��ϰ�)
							for (cal.setTime(in); cal.getTime().compareTo(out) < 0; cal.add(Calendar.DATE, 1)) {
								thisDate = fm.format(cal.getTime());
								sql = "update ȣ�ڰ��� set " + roomNumber + "ȣ = 0 where ��¥ = '" + thisDate + "';";
								stmt.executeUpdate(sql);
							}
						}
					}
					// rs �����Ƿ� �ٽ� �־��ֱ�
					sql = "select * from ������ where ������� = '������' AND üũ�γ�¥ = '" + thisDate + "';"; // �������ΰ� ã��
					rs = stmt.executeQuery(sql);
					for (int i = 0; i < count; i++)
						rs.next();
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

	public void openReservationFrame() {
		loadDB();// ���°� ���ÿ� ��ü ���;

		this.isLogIn = MainFrame.getIsLogIn();
		this.userID = MainFrame.getUserID();
		this.position = MainFrame.getPosition();

		if (isLogIn) // ��� ȭ�鿡�� �߰� �� �α��λ��� �� ����
			lable_logInState.setText("�α��� user : " + userID); // �����Ͽ� ǥ��
		frame.setVisible(true);
	}

	public void closeReservationFrame() {
		frame.setVisible(false);
	}
}