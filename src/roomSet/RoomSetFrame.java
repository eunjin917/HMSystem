package roomSet;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import com.toedter.calendar.JCalendar;

import javax.swing.JTable;
import java.awt.BorderLayout;

/*
 * 객실관리 화면
 */

public class RoomSetFrame {

	private JFrame frame;

	// 프레임 구성 요소들
	private JLabel lable_logInState;
	private JTable table;
	private JScrollPane scrollPane;
	private JTextField textField_changePrice;
	private JTextField textField_reason;
	private JCalendar calendar_start;
	private JCalendar calendar_end;

	// 모든 화면에서 뜨게할 라벨 구성 요소들 (창이 바뀌어도 유지)
	private boolean isLogIn;
	private String userID;
	private String position;

	// JTable에서 필요함
	private DefaultTableModel defaultTableModel;
	private String[] header = { "날짜", "201", "202", "203", "204", "205", "206", "207", "208", "209", "210", "301",
			"302", "303", "304", "305", "306", "307", "308", "309", "310", "401", "402", "403", "404", "405", "406",
			"407", "408", "409", "410", "501", "502", "503", "504", "505", "506", "507", "508", "509", "510", "601",
			"602", "603", "604", "605", "606", "607", "608", "609", "610", "701", "702", "703", "704", "705", "706",
			"707", "708", "709", "710", "801", "802", "803", "804", "805", "806", "807", "808", "809", "810", "901",
			"902", "903", "904", "905", "906", "907", "908", "909", "910", "1001", "1002", "1003", "1004", "1005",
			"1006", "1007", "1008", "1009", "1010", "1101", "1102", "1103", "1104", "1105", "1106", "1107", "1108",
			"1109", "1110", "객실가격", "가격변동이유" };
	private String[] typeArray = { "ID", "PW", "직급" };

	// 날짜 선택에서 필요
	private String todate;
	private int toyear;
	private int tomonth;
	private int today;

	// 저장해둘 것
	private String startDay = null;
	private String endDay = null;
	private String changePrice = null;
	private String reason = null;

	/**
	 * Create the application.
	 */
	public RoomSetFrame() throws ClassNotFoundException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() throws ClassNotFoundException {
		frame = new JFrame();
		frame.setBounds(100, 100, 1052, 643);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("\uC608\uC57D \uD604\uD669");
		lblNewLabel.setBounds(22, 27, 57, 15);
		frame.getContentPane().add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("\uC694\uAE08 \uBCC0\uACBD");
		lblNewLabel_1.setBounds(784, 94, 57, 15);
		frame.getContentPane().add(lblNewLabel_1);

		JLabel label = new JLabel("\uBCC0\uACBD \uAE08\uC561 :");
		label.setBounds(740, 515, 70, 15);
		frame.getContentPane().add(label);

		JLabel lblNewLabel_2 = new JLabel("\uBCC0\uACBD \uC0AC\uC720 :");
		lblNewLabel_2.setBounds(740, 545, 66, 15);
		frame.getContentPane().add(lblNewLabel_2);

		// 로그인 상태 라벨
		lable_logInState = new JLabel(" \uB85C\uADF8\uC544\uC6C3 \uC0C1\uD0DC");
		lable_logInState.setForeground(Color.RED);
		lable_logInState.setBounds(12, 545, 264, 42);
		frame.getContentPane().add(lable_logInState);

		// 저장 버튼
		JButton button_Save = new JButton("\uC785\uB825");
		button_Save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setInfo(); // 텍스트필드에서 초기화 진행
				if (changePrice.length() == 0)
					JOptionPane.showMessageDialog(null, "가격을 입력하세요.");
				else if (reason.length() == 0)
					JOptionPane.showMessageDialog(null, "객실 요금 변동 이유를 입력하세요.");
				else if (startDay.compareTo(endDay) >= 0) // 시작 날짜 >= 끝 날짜 => 1, 0
					JOptionPane.showMessageDialog(null, "종료  날짜를 시작 날짜 이후로 선택하세요.");
				else {
					saveDB(); // DB 연동시켜 DB에 수정하기
					JOptionPane.showMessageDialog(null, "저장 완료");

				}
			}
		});
		button_Save.setBounds(939, 511, 85, 23);
		frame.getContentPane().add(button_Save);

		// 이전 버튼
		JButton button_Back = new JButton("이전");
		button_Back.setBounds(939, 20, 85, 23);
		button_Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame mainFrame = new MainFrame();
				closeRoomSetFrame();
				mainFrame.openMainFrame();
			}
		});
		frame.getContentPane().add(button_Back);

		/*
		 * 테이블 관련
		 */
		defaultTableModel = new DefaultTableModel(null, header);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(22, 52, 750, 450);
		frame.getContentPane().add(scrollPane);

		table = new JTable(defaultTableModel);
		table.setForeground(Color.BLACK);
		table.setBackground(Color.WHITE);
		table.setEnabled(false);
		scrollPane.setViewportView(table);
		table.getTableHeader().setReorderingAllowed(false);// 이동 불가하게 설정
		table.setAutoCreateRowSorter(true); // 정렬가능

		TableCellRenderer renderer = new MyTableCellRenderer();
		table.setDefaultRenderer(Class.forName("java.lang.Object"), renderer);

		// 크기 조정
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableColumn column = table.getColumnModel().getColumn(0);
		column.setPreferredWidth(60);
		for (int i = 1; i < 101; i++) {
			column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth(30);
		}
		column = table.getColumnModel().getColumn(101);
		column.setPreferredWidth(50);
		column = table.getColumnModel().getColumn(102);
		column.setPreferredWidth(200);

		/*
		 * 여기부터 구성 요소들
		 */
		textField_changePrice = new JTextField();
		textField_changePrice.setBounds(807, 512, 124, 21);
		frame.getContentPane().add(textField_changePrice);
		textField_changePrice.setColumns(10);

		textField_reason = new JTextField();
		textField_reason.setBounds(807, 543, 217, 47);
		frame.getContentPane().add(textField_reason);
		textField_reason.setColumns(10);

		/*
		 * 날짜 관련 시작
		 */
		calendar_start = new JCalendar();
		calendar_start.setBounds(846, 91, 178, 203);
		frame.getContentPane().add(calendar_start);

		calendar_end = new JCalendar();
		calendar_end.setBounds(846, 299, 178, 203);
		frame.getContentPane().add(calendar_end);

		// 배경이미지
		JLabel lblNewLabel_3 = new JLabel("");
		lblNewLabel_3.setBounds(0, 0, 1036, 617);
		frame.getContentPane().add(lblNewLabel_3);
		lblNewLabel_3.setIcon(new ImageIcon(".\\img\\HTroomset.PNG"));
	}

	// 구성요소 초기화
	public void setInfo() {
		changePrice = textField_changePrice.getText();
		reason = textField_reason.getText();

		Date day;
		SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
		startDay = fm.format(calendar_start.getDate());// 시작 날짜
		endDay = fm.format(calendar_end.getDate());// 끝 날짜
	}

	public void setTodate() {
		// 현재 날짜/시간 얻기
		Calendar calendar = Calendar.getInstance();
		toyear = calendar.get(Calendar.YEAR);
		tomonth = calendar.get(Calendar.MONTH) + 1;
		today = calendar.get(Calendar.DAY_OF_MONTH);

		todate = Integer.toString(toyear) + Integer.toString(tomonth) + Integer.toString(today);
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

			// 검색
			String sql = "select * from 호텔객실;";
			rs = stmt.executeQuery(sql);

			Object data[] = new Object[header.length];
			while (rs.next()) {
				for (int i = 0; i < header.length; i++)
					data[i] = rs.getString(i + 1);
				defaultTableModel.addRow(data);
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

	public void saveDB() {
		Connection conn = null;
		Statement stmt = null;

		MainFrame mainFrame = new MainFrame();

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(mainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			String sql;

			String thisDate; // for문에 넣을 날짜(String)
			SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
			Calendar cal = Calendar.getInstance();
			Date inDate = fm.parse(startDay); // 시작 날짜
			Date outDate = fm.parse(endDay); // 종료 날짜

			for (cal.setTime(inDate); !(cal.getTime().equals(outDate)); cal.add(Calendar.DAY_OF_YEAR, 1)) {
				thisDate = fm.format(cal.getTime());
				sql = "update 호텔객실 set 객실가격 = '" + changePrice + "', 가격변동이유 = '" + reason + "' where 날짜 = '" + thisDate
						+ "';";
				stmt.executeUpdate(sql);
			}

			// 여기부터 예외처리
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
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException se) {
				}
			}
		} // 예외처리 끝
	}

	public void openRoomSetFrame() {
		loadDB(); // 오픈과 동시에 화면에 출력

		this.isLogIn = MainFrame.getIsLogIn();
		this.userID = MainFrame.getUserID();
		this.position = MainFrame.getPosition();

		if (isLogIn) // 모든 화면에서 뜨게 할 로그인상태 라벨 생성
			lable_logInState.setText("로그인 user : " + userID); // 변경하여 표시
		frame.setVisible(true);
	}

	public void closeRoomSetFrame() {
		frame.setVisible(false);
	}
}