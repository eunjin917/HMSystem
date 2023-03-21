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
 * 예약 화면
 */

public class ReservationFrame {

	private JFrame frame;

	// 프레임 구성 요소들
	private JLabel lable_logInState;
	private JTable table;
	private JScrollPane scrollPane;
	private JTextField textField_search;
	private JRadioButton radio_lastName;
	private JRadioButton radio_roomNumber;

	// 모든 화면에서 뜨게할 라벨 구성 요소들 (창이 바뀌어도 유지)
	private boolean isLogIn;
	private String userID;
	private String position;

	// JTable에서 필요함
	private DefaultTableModel defaultTableModel;
	private String[] header;

	// 저장해둘 것
	private String guess = null;
	private String type = "이름";
	private String confirmationNumber = null;
	private String roomNumber = null;
	private String item = null;
	private String origin = null;
	private int thisRow = 0;

	// 날짜 계산
	private String startDate;
	private String endDate;

	// 확인 목적
	private boolean isNull = false;
	private boolean[] roomIsAble = null; // 체크인~체크아웃 사이에 해당 객실이 사용가능한지
	private ArrayList<String> ableRoomArray = null; // 이용 가능한 객실만 저장
	private String[] roomArray = null; // 일반 객실번호 모두 저장
	private int count = 0; // 클릭 오류 방지
	private boolean isAble = false; // DB에 바꿔도 되는지

	/**
	 * Create the application.
	 */
	public ReservationFrame() {
		String[] typeName = MainFrame.getTypeName();
		header = new String[typeName.length + 1];
		header[0] = "선택";
		for (int i = 1; i < typeName.length + 1; i++)
			header[i] = typeName[i - 1];

		roomIsAble = new boolean[100]; // 모두 false들어있음
		for (int i = 0; i < 100; i++) // 일단 제일 처음에 true로 초기화시켜야함 (추후 계산 위함)
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

		// 로그인 상태 라벨
		lable_logInState = new JLabel(" \uB85C\uADF8\uC544\uC6C3 \uC0C1\uD0DC");
		lable_logInState.setForeground(Color.RED);
		lable_logInState.setBounds(12, 561, 174, 42);
		frame.getContentPane().add(lable_logInState);

		// 검색 버튼
		JButton button_search = new JButton("\uAC80\uC0C9");
		button_search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setInfo(); // guess 초기화

				loadDB(); // 검색하기
				if (isNull == true)
					JOptionPane.showMessageDialog(null, "검색 결과가 없습니다.");

			}
		});
		button_search.setBounds(579, 26, 91, 23);
		frame.getContentPane().add(button_search);

		// 예약하기 버튼
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

		// 삭제 버튼
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

							deleteDB(); // 삭제하기
							waitToConfirm();// 삭제하면서 빈 자리에 예약대기고객 넣을 수 있는지

							defaultTableModel.setNumRows(0);// 테이블 다지우고
							loadDB();// 다시 출력
						}

					if (checkNum == 0)
						JOptionPane.showMessageDialog(null, "삭제할 내용이 없습니다. 다시 선택하세요.");
					else {
						JOptionPane.showMessageDialog(null, "삭제되었습니다.");
					}
					checkNum = 0;

				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		});
		button_delete.setBounds(925, 545, 91, 42);
		frame.getContentPane().add(button_delete);

		// 객실결제 버튼
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
					JOptionPane.showMessageDialog(null, "객실결제할 예약정보를 선택하세요.");
				else if (checkNum >= 2)
					JOptionPane.showMessageDialog(null, "객실결제할 예약정보를 하나만 선택하세요.");
				else if (checkNum == 1) { // 체크인 프레임 열기
					if (!table.getValueAt(table.getSelectedRow(), 7).equals("예약완료")) // 현재상태가 예약완료 아니면
						JOptionPane.showMessageDialog(null, "아직 예약이 확정되지 않았거나, 이미 결제된 예약입니다.");
					else {
						PaymentFrame paymentFrame = new PaymentFrame();
						paymentFrame.setConfirmation((String) table.getValueAt(thisRow, 9));
						closeReservationFrame();
						paymentFrame.openPaymentFrame();

						defaultTableModel.setNumRows(0);// 테이블 다지우고
						loadDB();// 다시 출력
					}
				}
				checkNum = 0;
			}
		});
		button_roomPay.setBounds(544, 545, 115, 42);
		frame.getContentPane().add(button_roomPay);

		// 체크인 버튼
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
					JOptionPane.showMessageDialog(null, "체크인할 예약정보를 선택하세요.");
				else if (checkNum >= 2)
					JOptionPane.showMessageDialog(null, "체크인할 예약정보를 하나만 선택하세요.");
				else if (checkNum == 1) { // 체크인 프레임 열기
					if (((String) table.getValueAt(table.getSelectedRow(), 7)).equals("객실결제완료") == false) // 현재상태가
																											// 결제완료아니면
						JOptionPane.showMessageDialog(null, "아직 결제되지 않았거나, 이미 체크인된 예약입니다.");
					else {
						CheckInFrame checkInFrame = new CheckInFrame();
						checkInFrame.setConfirmation((String) table.getValueAt(thisRow, 9));
						closeReservationFrame();
						checkInFrame.openCheckInFrame();

						defaultTableModel.setNumRows(0);// 테이블 다지우고
						loadDB();// 다시 출력
					}
				}
				checkNum = 0;
			}

		});
		button_checkIn.setBounds(671, 545, 115, 42);
		frame.getContentPane().add(button_checkIn);

		// 체크아웃 버튼
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
					JOptionPane.showMessageDialog(null, "체크아웃할 예약정보를 선택하세요.");
				else if (checkNum >= 2)
					JOptionPane.showMessageDialog(null, "체크아웃할 예약정보를 하나만 선택하세요.");
				else if (checkNum == 1) { // 체크인 프레임 열기
					if (((String) table.getValueAt(table.getSelectedRow(), 7)).equals("체크인") == false) // 현재상태가 체크인아니면
						JOptionPane.showMessageDialog(null, "아직 체크인되지 않았거나, 이미 체크아웃된 예약입니다.");
					else {
						CheckOutFrame checkOutFrame = new CheckOutFrame();
						checkOutFrame.setConfirmation((String) table.getValueAt(thisRow, 9));
						closeReservationFrame();
						checkOutFrame.openCheckOutFrame();

						defaultTableModel.setNumRows(0);// 테이블 다지우고
						loadDB();// 다시 출력
					}
				}
				checkNum = 0;
			}
		});
		button_checkOut.setBounds(798, 545, 115, 42);
		frame.getContentPane().add(button_checkOut);

		// 이전 버튼
		JButton button_Back = new JButton("이전");
		button_Back.setBounds(939, 23, 85, 23);
		button_Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame mainFrame = new MainFrame();
				closeReservationFrame();
				mainFrame.openMainFrame();
			}
		});
		frame.getContentPane().add(button_Back);

		// 이름으로 라디오버튼
		radio_lastName = new JRadioButton("\uC774\uB984\uC73C\uB85C");
		radio_lastName.setSelected(true);
		radio_lastName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				radio_lastName.setSelected(true);
				radio_roomNumber.setSelected(false);
				type = "이름";
			}
		});
		radio_lastName.setBounds(352, 26, 73, 23);
		frame.getContentPane().add(radio_lastName);

		// 객실번호로 라디오버튼
		radio_roomNumber = new JRadioButton("\uAC1D\uC2E4\uBC88\uD638\uB85C");
		radio_roomNumber.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				radio_lastName.setSelected(false);
				radio_roomNumber.setSelected(true);
				type = "객실번호";
			}
		});
		radio_roomNumber.setBounds(352, 51, 85, 23);
		frame.getContentPane().add(radio_roomNumber);

		/*
		 * 테이블 관련
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
		table.getTableHeader().setReorderingAllowed(false);// 이동 불가하게 설정
		table.setAutoCreateRowSorter(true); // 정렬가능

		// 체크박스
		table.getColumnModel().getColumn(0).setCellEditor(new CheckBoxCellEditor());
		table.getColumnModel().getColumn(0).setCellRenderer(new CWCheckBoxRenderer());

		// 클릭하면
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1) { // 클릭
					if (table.getSelectedColumn() != -1 && table.getSelectedColumn() != 0
							&& table.getSelectedColumn() != 4 && table.getSelectedColumn() != 5
							&& table.getSelectedColumn() != 7 && table.getSelectedColumn() != 8
							&& table.getSelectedColumn() != 9 && table.getSelectedColumn() != 10
							&& table.getSelectedColumn() != 11 && table.getSelectedColumn() != 12
							&& table.getSelectedColumn() != 13 && table.getSelectedColumn() != 14
							&& table.getSelectedColumn() != 15) {
						origin = (String) table.getValueAt(table.getSelectedRow(), table.getSelectedColumn()); // 선택된거원래꺼
						confirmationNumber = (String) table.getValueAt(table.getSelectedRow(), 9); // 선택된거 해당하는 확인번호
						// 받아오기
						count = 1;
					}
				}
			}
		});

		// 수정 기능
		table.addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) { // 수정되면
				if (count == 2) {
					if (table.getSelectedColumn() != -1 && table.getSelectedColumn() != 0
							&& table.getSelectedColumn() != 4 && table.getSelectedColumn() != 5
							&& table.getSelectedColumn() != 7 && table.getSelectedColumn() != 8
							&& table.getSelectedColumn() != 9 && table.getSelectedColumn() != 10
							&& table.getSelectedColumn() != 11 && table.getSelectedColumn() != 12
							&& table.getSelectedColumn() != 13 && table.getSelectedColumn() != 14
							&& table.getSelectedColumn() != 15) {
						item = (String) table.getValueAt(table.getSelectedRow(), table.getSelectedColumn()); // 선택된거 바꿀거
						// 초기화

						if (item.length() == 0) {
							JOptionPane.showMessageDialog(null, "내용을 모두 입력하세요.");
							isAble = false;
						} else {// 안비어있으면
							modifyDB();// DB에 수정
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

		// 크기 조정
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

		// 배경 이미지
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(".\\img\\HTreservation.PNG"));
		lblNewLabel.setBounds(0, 0, 1036, 622);
		frame.getContentPane().add(lblNewLabel);
	}

	// 구성요소 초기화
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

			defaultTableModel.setNumRows(0); // 테이블 초기화

			// 검색
			String sql;
			if (guess == null)// guess가 null이면
				sql = "select * from 고객정보;";
			else
				sql = "select * from 고객정보 where " + type + " = '" + guess + "';";
			rs = stmt.executeQuery(sql);

			if (rs.next() == false) // 검색결과 없으면
				isNull = true;
			else { // 검색결과 있으면
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

			// 여기부터 예외처리
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
		} // 예외처리 끝
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

			sql = "update 고객정보 set " + header[table.getSelectedColumn()] + " = '" + item + "' where 확인번호 = '"
					+ confirmationNumber + "' limit 1;";
			stmt.executeUpdate(sql);

			// 여기부터 예외처리
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
		} // 예외처리 끝
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

			// 고객정보 table에 삭제
			String sql = "delete from 고객정보 where 확인번호 = '" + confirmationNumber + "' limit 1;";
			stmt.executeUpdate(sql);

			// 호텔객실 table에 삭제
			if (!roomNumber.equals("추후 배정")) { // 자리 없을 경우에는 변경 안시킴
				String thisDate; // for문에 넣을 날짜(String)
				SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
				Calendar cal = Calendar.getInstance();
				Date inDate = fm.parse(startDate); // 체크인 날짜
				Date outDate = fm.parse(endDate); // 체크아웃 날짜
				for (cal.setTime(inDate); cal.getTime().compareTo(outDate) < 0; cal.add(Calendar.DATE, 1)) {
					thisDate = fm.format(cal.getTime());
					sql = "update 호텔객실 set " + roomNumber + "호 = 1 where 날짜 = '" + thisDate + "';";
					stmt.executeUpdate(sql);
				}
			}

			// 주문 table에도 내역 있다면, 삭제
			sql = "select * from 주문 where 확인번호 = '" + confirmationNumber + "' limit 1;";
			rs = stmt.executeQuery(sql);
			while (rs.next()) { // 내역 있다면
				sql = "delete from 주문 where 확인번호 = '" + confirmationNumber + "' limit 1;"; // 주문내역 삭제
				stmt.executeUpdate(sql);
			}

			// 여기부터 예외처리
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
		} // 예외처리 끝
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
			String thisDate; // for문에 넣을 날짜(String)
			SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
			Calendar cal = Calendar.getInstance();
			Date inDate = fm.parse(startDate); // 체크인 날짜
			Date outDate = fm.parse(endDate); // 체크아웃 날짜

			/*
			 * 삭제한 예약의 체크인날짜~체크아웃날짜 사이 예약대기 있는지 확인
			 */

			for (cal.setTime(inDate); cal.getTime().compareTo(outDate) < 0; cal.add(Calendar.DATE, 1)) {
				thisDate = fm.format(cal.getTime());
				sql = "select * from 고객정보 where 현재상태 = '예약대기' AND 체크인날짜 = '" + thisDate + "';"; // 예약대기인것 찾기
				rs = stmt.executeQuery(sql);

				/*
				 * 해당 날짜에, 예약대기 해당하는게 있다면, 그 예약의 체크인~체크아웃 날짜 중 쭉 이용가능한객실 있는지
				 */
				while (rs.next()) {
					count++;
					String cfmn = rs.getString("확인번호"); // 확인번호 저장

					String td; // for문에 넣을 날짜(String)
					Calendar calendar = Calendar.getInstance();
					Date in = fm.parse(rs.getString("체크인날짜")); // 체크인 날짜
					Date out = fm.parse(rs.getString("체크아웃날짜")); // 체크아웃 날짜
					for (calendar.setTime(in); calendar.getTime().compareTo(out) < 0; calendar.add(Calendar.DATE, 1)) {
						td = fm.format(calendar.getTime());
						sql = "select * from 호텔객실 where 날짜 = '" + td + "';"; // 해당 날짜의 호텔객실 table 불러오기
						rs2 = stmt.executeQuery(sql);
						while (rs2.next()) {
							for (int i = 2; i <= 101; i++) {
								if (rs2.getString(i).equals("1") && roomIsAble[i - 2] == true) // 해당 객실이 당일에 이용가능하고 전날에도
									// 이용가능했다면
									roomIsAble[i - 2] = true; // 이용 가능 목록에 true로 추가
								else
									roomIsAble[i - 2] = false; // false주어, 그 객실은 이용 불가하므로 계산 안하게
							}
						}

						for (int i = 0; i < 100; i++) // 최종적으로 구해진 rooIsAble 배열에서,
							if (roomIsAble[i] == true) // roomIsAble이 true인 방을 모두 뽑음
								ableRoomArray.add(roomArray[i]); // 해당 방 호수를 모두 저장

						// 객실 남은거 있다면! 예약확정으로 변경
						if (ableRoomArray.isEmpty() == false) {
							int random = (int) Math.random() * ableRoomArray.size(); // 객실번호 랜덤으로 하나 생성

							int price = 0;
							rs2 = stmt.executeQuery(sql); // select * from 호텔객실 where 날짜 = '" + td + "';
							while (rs2.next())
								price += Integer.parseInt(rs2.getString("객실가격")); // 가격 계속 추가하면서 계산

							sql = "update 고객정보 set 객실번호 = '" + ableRoomArray.get(random) + "', 객실가격 = '" + price
									+ "', 현재상태 = '예약완료' where 확인번호 = '" + cfmn + "';";
							stmt.executeUpdate(sql);

							// 호텔객실 table에도 update (이용 불가하게)
							for (cal.setTime(in); cal.getTime().compareTo(out) < 0; cal.add(Calendar.DATE, 1)) {
								thisDate = fm.format(cal.getTime());
								sql = "update 호텔객실 set " + roomNumber + "호 = 0 where 날짜 = '" + thisDate + "';";
								stmt.executeUpdate(sql);
							}
						}
					}
					// rs 닫히므로 다시 넣어주기
					sql = "select * from 고객정보 where 현재상태 = '예약대기' AND 체크인날짜 = '" + thisDate + "';"; // 예약대기인것 찾기
					rs = stmt.executeQuery(sql);
					for (int i = 0; i < count; i++)
						rs.next();
				}
			}

			// 여기부터 예외처리
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
		} // 예외처리 끝
	}

	public void openReservationFrame() {
		loadDB();// 오픈과 동시에 전체 출력;

		this.isLogIn = MainFrame.getIsLogIn();
		this.userID = MainFrame.getUserID();
		this.position = MainFrame.getPosition();

		if (isLogIn) // 모든 화면에서 뜨게 할 로그인상태 라벨 생성
			lable_logInState.setText("로그인 user : " + userID); // 변경하여 표시
		frame.setVisible(true);
	}

	public void closeReservationFrame() {
		frame.setVisible(false);
	}
}