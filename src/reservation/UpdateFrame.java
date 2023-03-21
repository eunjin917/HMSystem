package reservation;

import main.*;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
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
import java.util.Locale;
import java.awt.event.ActionEvent;
import com.toedter.calendar.JCalendar;
import java.awt.Font;
import javax.swing.ImageIcon;

/*
 * ���� ������Ʈ ȭ��
 */

public class UpdateFrame {

	private JFrame frame;

	// ������ ���� ��ҵ�
	private JLabel lable_logInState;
	private JTextField textField_firstName;
	private JTextField textField_lastName;
	private JButton button_save;
	private JButton button_back;
	private JTextField textField_occupantsNum;
	private JTextField textField_phone;
	private JLabel label_expectedCheckOut;
	JCalendar calendar_checkIn;
	JCalendar calendar_checkOut;

	// ��� ȭ�鿡�� �߰��� �� ���� ��ҵ� (â�� �ٲ� ����)
	private boolean isLogIn;
	private String userID;
	private String position;

	// ������ table
	// ��, �̸�, �ο�, ���ǹ�ȣ, ���ǰ���, ����ó, ��������, �ſ�ī���ȣ, Ȯ�ι�ȣ, üũ�γ�¥, üũ�νð�, üũ�ƿ���¥, üũ�ƿ��ð�,
	// �ڵ������, û���ݾ�, �ǵ��, Ư����û
	private String firstName = null;
	private String lastName = null;
	private String occupantsNum = null;
	private String roomNumber = null;
	private String roomPrice = null;
	private String phone = null;
	private String cardNum = null;
	private String state = null;
	private static String confirmationNumber = null; // ���͸޼ҵ忡����, ������ ���� ���ߵǹǷ�
	private String checkInDate = null;
	private String checkOutDate = null;
	private String cancelDate = null;
	private String payment = null;
	private String feedback = null;
	private String request = null;

	// ���ݺ������� ���� �ʿ�, Ȯ�������������� ��� �����ϵ��� ���͸޼ҵ� ������
	private ArrayList<String> priceReason = null;

	// ��¥ ���ÿ��� �ʿ�
	private String todate;
	private String toyear;
	private String tomonth;
	private String today;

	// Ȯ�� ����
	private boolean isOccupantsNumSuccessed = false; // �ο��� �̻�����
	private boolean isReservationSuccessed = false; // ���� ����������(��� ���� �ߵƴ���)

	// �����ص� ��
	private boolean[] roomIsAble = null; // üũ��~üũ�ƿ� ���̿� �ش� ������ ��밡������
	private ArrayList<String> ableRoomArray = null; // �̿� ������ ���Ǹ� ����
	private String[] roomArray = null; // �Ϲ� ���ǹ�ȣ ��� ����
	private String recentConfirmationNum = null; // �ֱ� Ȯ�ι�ȣ ����

	/**
	 * Create the application.
	 */
	public UpdateFrame() {
		// roomIsAble �ʱ�ȭ
		roomIsAble = new boolean[100]; // ��� false�������
		for (int i = 0; i < 100; i++) // �ϴ� ���� ó���� true�� �ʱ�ȭ���Ѿ��� (���� ��� ����)
			roomIsAble[i] = true;

		ableRoomArray = new ArrayList<String>();
		priceReason = new ArrayList<String>();
		roomArray = new String[] { "201", "202", "203", "204", "205", "206", "207", "208", "209", "210", "301", "302",
				"303", "304", "305", "306", "307", "308", "309", "310", "401", "402", "403", "404", "405", "406", "407",
				"408", "409", "410", "501", "502", "503", "504", "505", "506", "507", "508", "509", "510", "601", "602",
				"603", "604", "605", "606", "607", "608", "609", "610", "701", "702", "703", "704", "705", "706", "707",
				"708", "709", "710", "801", "802", "803", "804", "805", "806", "807", "808", "809", "810", "901", "902",
				"903", "904", "905", "906", "907", "908", "909", "910", "1001", "1002", "1003", "1004", "1005", "1006",
				"1007", "1008", "1009", "1010", "1101", "1102", "1103", "1104", "1105", "1106", "1107", "1108", "1109",
				"1110" };

		setTodate(); // ���� ��¥ ���� �ʱ�ȭ
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
		lable_logInState.setBounds(12, 561, 173, 42);
		frame.getContentPane().add(lable_logInState);

		// ���� ��ư
		button_save = new JButton("\uC800\uC7A5");
		button_save.setBounds(886, 528, 98, 29);
		button_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					setInfo(); // ������� �ʱ�ȭ
					// �ϳ��� �Է� X
					if (firstName.length() == 0 || lastName.length() == 0 || occupantsNum.length() == 0
							|| phone.length() == 0 || checkInDate.length() != 8 || checkOutDate.length() == 0)
						JOptionPane.showMessageDialog(null, "������ ��� �Է��Ͽ� �ּ���.");
					else if (isOccupantsNumSuccessed == false)// �ο��� �߸��Ȱ��
						JOptionPane.showMessageDialog(null, "�ο� ���� ��Ȯ�ϰ� �Է��ϼ���.");
					else if (checkInDate.compareTo(todate) <= 0) // üũ�� ��¥ < ���� => -1,0
						JOptionPane.showMessageDialog(null, "üũ�� ��¥�� ���� ���ķ� �����ϼ���.");
					else if (checkInDate.compareTo(checkOutDate) >= 0) // üũ�� ��¥ >= üũ�ƿ� ��¥ => 1, 0
						JOptionPane.showMessageDialog(null, "üũ�ƿ� ��¥�� üũ�� ���ķ� �����ϼ���.");
					else {
						loadDB();// DB�κ��� ��������(�迭), �ֱ�Ȯ�ι�ȣ ��������
						createInfo(); // ���ǹ�ȣ, Ȯ�ι�ȣ �� ���� set
						updateDB(); // DB�� �ø���

						if (isReservationSuccessed == true) // ����Ϸ� �����̸�
							JOptionPane.showMessageDialog(null, "������ �Ϸ�Ǿ����ϴ�.");
						else
							JOptionPane.showMessageDialog(null, "�̿� ������ ������ �����ϴ�. ���� ����ܿ� ����Ǿ����ϴ�.");

						UpdateConfirmFrame updateConfirmFrame = new UpdateConfirmFrame();
						closeUpdateFrame();
						updateConfirmFrame.openUpdateConfirmFrame(priceReason); // Ȯ�� �������� �Ѿ��
					}

				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		});
		frame.getContentPane().add(button_save);

		// ���� ��ư
		button_back = new JButton("\uC774\uC804");
		button_back.setBounds(933, 21, 91, 23);
		button_back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ReservationFrame reservationSetFrame = new ReservationFrame();
				closeUpdateFrame();
				reservationSetFrame.openReservationFrame();
			}
		});
		frame.getContentPane().add(button_back);

		/*
		 * ������� ���� ��ҵ�
		 */
		textField_firstName = new JTextField();
		textField_firstName.setBounds(740, 389, 116, 21);
		frame.getContentPane().add(textField_firstName);
		textField_firstName.setColumns(10);

		textField_lastName = new JTextField();
		textField_lastName.setBounds(740, 420, 116, 21);
		textField_lastName.setColumns(10);
		frame.getContentPane().add(textField_lastName);

		JLabel label_firstName = new JLabel("\uC131 :");
		label_firstName.setFont(new Font("���� ���", Font.PLAIN, 14));
		label_firstName.setBounds(709, 389, 28, 22);
		frame.getContentPane().add(label_firstName);

		JLabel label_lastName = new JLabel("\uC774\uB984 :");
		label_lastName.setFont(new Font("���� ���", Font.PLAIN, 14));
		label_lastName.setBounds(692, 421, 45, 15);
		frame.getContentPane().add(label_lastName);

		textField_occupantsNum = new JTextField();
		textField_occupantsNum.setBounds(740, 451, 116, 21);
		textField_occupantsNum.setColumns(10);
		frame.getContentPane().add(textField_occupantsNum);

		JLabel label_occupantsNum = new JLabel("\uC778\uC6D0 :");
		label_occupantsNum.setFont(new Font("���� ���", Font.PLAIN, 14));
		label_occupantsNum.setBounds(692, 452, 50, 15);
		frame.getContentPane().add(label_occupantsNum);

		JLabel label_phone = new JLabel("\uC5F0\uB77D\uCC98 :");
		label_phone.setFont(new Font("���� ���", Font.PLAIN, 14));
		label_phone.setBounds(678, 485, 50, 15);
		frame.getContentPane().add(label_phone);

		textField_phone = new JTextField();
		textField_phone.setBounds(740, 482, 116, 21);
		textField_phone.setColumns(10);
		frame.getContentPane().add(textField_phone);

		JLabel label_expectedCheckIn = new JLabel("\uC608\uC0C1\uCCB4\uD06C\uC778");
		label_expectedCheckIn.setBounds(299, 111, 91, 15);
		frame.getContentPane().add(label_expectedCheckIn);

		label_expectedCheckOut = new JLabel("\uC608\uC0C1\uCCB4\uD06C\uC544\uC6C3");
		label_expectedCheckOut.setBounds(553, 111, 85, 15);
		frame.getContentPane().add(label_expectedCheckOut);

		/*
		 * ��¥ ���� ����
		 */
		calendar_checkIn = new JCalendar();
		calendar_checkIn.getDayChooser().getDayPanel().setBackground(Color.WHITE);
		calendar_checkIn.setBounds(299, 136, 219, 236);
		frame.getContentPane().add(calendar_checkIn);

		calendar_checkOut = new JCalendar();
		calendar_checkOut.getDayChooser().getDayPanel().setBackground(Color.WHITE);
		calendar_checkOut.setBounds(553, 136, 219, 236);
		frame.getContentPane().add(calendar_checkOut);
		// ����̹���
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(".\\img\\HTupdate.PNG"));
		lblNewLabel.setBounds(0, 0, 1036, 625);
		frame.getContentPane().add(lblNewLabel);
	}

	// ������� �ʱ�ȭ
	public void setInfo() {
		firstName = textField_firstName.getText();
		lastName = textField_lastName.getText();
		occupantsNum = textField_occupantsNum.getText();
		phone = textField_phone.getText();

		SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
		checkInDate = fm.format(calendar_checkIn.getDate());// üũ�� ��¥
		checkOutDate = fm.format(calendar_checkOut.getDate());// üũ�ƿ� ��¥

		// �ο��� int �ƴ� ���
		try {
			Integer.parseInt(occupantsNum);
			isOccupantsNumSuccessed = true;
		} catch (NumberFormatException e) {
			isOccupantsNumSuccessed = false;
		}
	}

	public void setTodate() {
		// ���� ��¥/�ð� ���
		Calendar calendar = Calendar.getInstance();
		toyear = Integer.toString(calendar.get(Calendar.YEAR));
		tomonth = Integer.toString(calendar.get(Calendar.MONTH) + 1);
		if (tomonth.length() == 1) // ���ڸ� �����,
			tomonth = "0" + tomonth; // �տ� 0���̱�
		today = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
		if (today.length() == 1) // ���ڸ� �����,
			today = "0" + today; // �տ� 0���̱�

		todate = toyear + tomonth + today;
	}

	public void loadDB() throws ParseException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			String sql;

			// �̿� ������ ���ǹ�ȣ �迭 �����ϱ�
			SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
			Calendar cal = Calendar.getInstance();

			Date indate = fm.parse(checkInDate);
			Date outdate = fm.parse(checkOutDate);

			for (cal.setTime(indate); cal.getTime().compareTo(outdate) < 0; cal.add(Calendar.DATE, 1)) {
				String thisDate = fm.format(cal.getTime());

				sql = "select * from ȣ�ڰ��� where ��¥ = '" + thisDate + "';"; // �ش� ��¥�� ȣ�ڰ��� table �ҷ�����
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					for (int i = 2; i <= 101; i++) {
						if (rs.getString(i).equals("1") && roomIsAble[i - 2] == true) // �ش� ������ ���Ͽ� �̿밡���ϰ� �������� �̿밡���ߴٸ�
							roomIsAble[i - 2] = true; // �̿� ���� ��Ͽ� true�� �߰�
						else
							roomIsAble[i - 2] = false; // false�־�, �� ������ �̿� �Ұ��ϹǷ� ��� ���ϰ�
					}
				}
			}

			for (int i = 0; i < 100; i++) // ���������� ������ rooIsAble �迭����,
				if (roomIsAble[i] == true) {// roomIsAble�� true�� ���� ��� ����
					ableRoomArray.add(roomArray[i]); // �ش� �� ȣ���� ��� ����
				}

			// ���� ���� ��� ��, ���� ���� �迭 �����ϱ�
			int price = 0;

			for (cal.setTime(indate); cal.getTime().compareTo(outdate) < 0; cal.add(Calendar.DATE, 1)) {
				String thisDate = fm.format(cal.getTime());
				sql = "select * from ȣ�ڰ��� where ��¥ = '" + thisDate + "';"; // �ش� ��¥�� ȣ�ڰ��� table �ҷ�����
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					price += Integer.parseInt(rs.getString("���ǰ���")); // ���� ��� �߰��ϸ鼭 ���
					if (!rs.getString("���ݺ�������").equals("��������")) // ���� ��������
						priceReason.add(thisDate + " : " + rs.getString("���ݺ�������")); // ���� �迭�� ����
				}
			}
			roomPrice = Integer.toString(price); // ���� ���� ���� ����!!

			// �ֱ� Ȯ�ι�ȣ �����ϱ�
			sql = "select * from ������";
			rs = stmt.executeQuery(sql);
			if (!rs.next()) // �������� �ƹ��͵� ���� ���, ����Ʈ��
				recentConfirmationNum = "10000";
			else {
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					recentConfirmationNum = rs.getString(9); // 9��°�� ������

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

	// ���� �Է��� �� ����, ����Ʈ������ �ְų� ���������� ���� �� set
	public void createInfo() {

		// ���ǹ�ȣ, ���ǰ��� ����
		if (ableRoomArray.isEmpty() == true) { // ���� ������ ������
			isReservationSuccessed = false;
			roomNumber = "���� ����";
			roomPrice = "���� Ȯ��";
		} else {
			isReservationSuccessed = true;
			int random = (int) Math.random() * ableRoomArray.size(); // �������� �ϳ� ����
			roomNumber = ableRoomArray.get(random); // ���ǹ�ȣ �����ϱ�
		}

		// Ȯ�ι�ȣ ����
		int num = Integer.parseInt(recentConfirmationNum) + 1;
		confirmationNumber = Integer.toString(num);

		// �ڵ������
		try {
			SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
			Calendar cal = Calendar.getInstance(); // �ڵ������ Ķ����
			Date inDate = fm.parse(checkInDate); // üũ�� ��¥

			cal.setTime(inDate);
			cal.add(Calendar.DATE, -7); // üũ�� ��¥ -7
			cancelDate = fm.format(cal.getTime());

			if (todate.compareTo(cancelDate) > 0) // ���� >= �ڵ������ ��¥
				cancelDate = todate; // ������ �ڵ������
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		// �������
		if (ableRoomArray.isEmpty() == true) // ���� ������ ������
			state = "������";
		else
			state = "����Ϸ�";

		// �ſ�ī���ȣ
		cardNum = "�̰���";

		// û���ݾ�
		payment = "���� û����������"; // ���� �ʿ�

		// �ǵ��, Ư����û
		feedback = "�������";
		request = "�������";
	}

	public void updateDB() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			// ������ table�� update
			String sql = "insert into ������ values(";
			sql += " '" + firstName + "', ";
			sql += " '" + lastName + "', ";
			sql += " '" + occupantsNum + "', ";
			sql += " '" + roomNumber + "', ";
			sql += " '" + roomPrice + "', ";
			sql += " '" + phone + "', ";
			sql += " '" + state + "', ";
			sql += " '" + cardNum + "', ";
			sql += " '" + confirmationNumber + "', ";
			sql += " '" + checkInDate + "', ";
			sql += " '" + checkOutDate + "', ";
			sql += " '" + cancelDate + "', ";
			sql += " '" + payment + "', ";
			sql += " '" + feedback + "', ";
			sql += " '" + request + "');";

			stmt.executeUpdate(sql);

			// ȣ�ڰ��� table�� update (�̿� �Ұ��ϰ�)
			if (!roomNumber.equals("���� ����")) { // �ڸ� ���� ��쿡�� ���� �Ƚ�Ŵ
				String thisDate; // for���� ���� ��¥(String)
				SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
				Calendar cal = Calendar.getInstance();
				Date indate = fm.parse(checkInDate); // üũ�� ��¥
				Date outdate = fm.parse(checkOutDate); // üũ�ƿ� ��¥
				for (cal.setTime(indate); cal.getTime().compareTo(outdate) < 0; cal.add(Calendar.DATE, 1)) {
					thisDate = fm.format(cal.getTime());
					sql = "update ȣ�ڰ��� set " + roomNumber + "ȣ = 0 where ��¥ = '" + thisDate + "';";
					stmt.executeUpdate(sql);
				}
			}

			// ���⼭���� ����ó��
		} catch (ParseException e1) {
			e1.printStackTrace();
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

	public void openUpdateFrame() {
		this.isLogIn = MainFrame.getIsLogIn();
		this.userID = MainFrame.getUserID();
		this.position = MainFrame.getPosition();

		if (isLogIn) // ��� ȭ�鿡�� �߰� �� �α��λ��� �� ����
			lable_logInState.setText("�α��� user : " + userID); // �����Ͽ� ǥ��
		frame.setVisible(true);
	}

	public void closeUpdateFrame() {
		frame.setVisible(false);
	}

	/*
	 * ���� �޼ҵ�
	 */
	public String getConfirmationNumber() {
		return confirmationNumber;
	}

	public ArrayList<String> getPriceReason() {
		return priceReason;
	}
}