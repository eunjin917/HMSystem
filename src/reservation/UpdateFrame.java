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
 * 예약 업데이트 화면
 */

public class UpdateFrame {

	private JFrame frame;

	// 프레임 구성 요소들
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

	// 모든 화면에서 뜨게할 라벨 구성 요소들 (창이 바뀌어도 유지)
	private boolean isLogIn;
	private String userID;
	private String position;

	// 고객정보 table
	// 성, 이름, 인원, 객실번호, 객실가격, 연락처, 결제여부, 신용카드번호, 확인번호, 체크인날짜, 체크인시간, 체크아웃날짜, 체크아웃시간,
	// 자동취소일, 청구금액, 피드백, 특별요청
	private String firstName = null;
	private String lastName = null;
	private String occupantsNum = null;
	private String roomNumber = null;
	private String roomPrice = null;
	private String phone = null;
	private String cardNum = null;
	private String state = null;
	private static String confirmationNumber = null; // 게터메소드에서도, 동일한 값이 가야되므로
	private String checkInDate = null;
	private String checkOutDate = null;
	private String cancelDate = null;
	private String payment = null;
	private String feedback = null;
	private String request = null;

	// 가격변동이유 저장 필요, 확인페이지에서도 사용 가능하도록 게터메소드 만들자
	private ArrayList<String> priceReason = null;

	// 날짜 선택에서 필요
	private String todate;
	private String toyear;
	private String tomonth;
	private String today;

	// 확인 역할
	private boolean isOccupantsNumSuccessed = false; // 인원수 이상한지
	private boolean isReservationSuccessed = false; // 예약 성공적인지(대기 없이 잘됐는지)

	// 저장해둘 것
	private boolean[] roomIsAble = null; // 체크인~체크아웃 사이에 해당 객실이 사용가능한지
	private ArrayList<String> ableRoomArray = null; // 이용 가능한 객실만 저장
	private String[] roomArray = null; // 일반 객실번호 모두 저장
	private String recentConfirmationNum = null; // 최근 확인번호 저장

	/**
	 * Create the application.
	 */
	public UpdateFrame() {
		// roomIsAble 초기화
		roomIsAble = new boolean[100]; // 모두 false들어있음
		for (int i = 0; i < 100; i++) // 일단 제일 처음에 true로 초기화시켜야함 (추후 계산 위함)
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

		setTodate(); // 현재 날짜 먼저 초기화
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
		lable_logInState.setBounds(12, 561, 173, 42);
		frame.getContentPane().add(lable_logInState);

		// 저장 버튼
		button_save = new JButton("\uC800\uC7A5");
		button_save.setBounds(886, 528, 98, 29);
		button_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					setInfo(); // 구성요소 초기화
					// 하나라도 입력 X
					if (firstName.length() == 0 || lastName.length() == 0 || occupantsNum.length() == 0
							|| phone.length() == 0 || checkInDate.length() != 8 || checkOutDate.length() == 0)
						JOptionPane.showMessageDialog(null, "정보를 모두 입력하여 주세요.");
					else if (isOccupantsNumSuccessed == false)// 인원수 잘못된경우
						JOptionPane.showMessageDialog(null, "인원 수를 정확하게 입력하세요.");
					else if (checkInDate.compareTo(todate) <= 0) // 체크인 날짜 < 오늘 => -1,0
						JOptionPane.showMessageDialog(null, "체크인 날짜를 오늘 이후로 선택하세요.");
					else if (checkInDate.compareTo(checkOutDate) >= 0) // 체크인 날짜 >= 체크아웃 날짜 => 1, 0
						JOptionPane.showMessageDialog(null, "체크아웃 날짜를 체크인 이후로 선택하세요.");
					else {
						loadDB();// DB로부터 객실정보(배열), 최근확인번호 가져오기
						createInfo(); // 객실번호, 확인번호 등 정보 set
						updateDB(); // DB에 올리기

						if (isReservationSuccessed == true) // 예약완료 상태이면
							JOptionPane.showMessageDialog(null, "예약이 완료되었습니다.");
						else
							JOptionPane.showMessageDialog(null, "이용 가능한 객실이 없습니다. 예약 대기명단에 저장되었습니다.");

						UpdateConfirmFrame updateConfirmFrame = new UpdateConfirmFrame();
						closeUpdateFrame();
						updateConfirmFrame.openUpdateConfirmFrame(priceReason); // 확인 페이지로 넘어가기
					}

				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		});
		frame.getContentPane().add(button_save);

		// 이전 버튼
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
		 * 여기부터 구성 요소들
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
		label_firstName.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		label_firstName.setBounds(709, 389, 28, 22);
		frame.getContentPane().add(label_firstName);

		JLabel label_lastName = new JLabel("\uC774\uB984 :");
		label_lastName.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		label_lastName.setBounds(692, 421, 45, 15);
		frame.getContentPane().add(label_lastName);

		textField_occupantsNum = new JTextField();
		textField_occupantsNum.setBounds(740, 451, 116, 21);
		textField_occupantsNum.setColumns(10);
		frame.getContentPane().add(textField_occupantsNum);

		JLabel label_occupantsNum = new JLabel("\uC778\uC6D0 :");
		label_occupantsNum.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		label_occupantsNum.setBounds(692, 452, 50, 15);
		frame.getContentPane().add(label_occupantsNum);

		JLabel label_phone = new JLabel("\uC5F0\uB77D\uCC98 :");
		label_phone.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
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
		 * 날짜 관련 시작
		 */
		calendar_checkIn = new JCalendar();
		calendar_checkIn.getDayChooser().getDayPanel().setBackground(Color.WHITE);
		calendar_checkIn.setBounds(299, 136, 219, 236);
		frame.getContentPane().add(calendar_checkIn);

		calendar_checkOut = new JCalendar();
		calendar_checkOut.getDayChooser().getDayPanel().setBackground(Color.WHITE);
		calendar_checkOut.setBounds(553, 136, 219, 236);
		frame.getContentPane().add(calendar_checkOut);
		// 배경이미지
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(".\\img\\HTupdate.PNG"));
		lblNewLabel.setBounds(0, 0, 1036, 625);
		frame.getContentPane().add(lblNewLabel);
	}

	// 구성요소 초기화
	public void setInfo() {
		firstName = textField_firstName.getText();
		lastName = textField_lastName.getText();
		occupantsNum = textField_occupantsNum.getText();
		phone = textField_phone.getText();

		SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
		checkInDate = fm.format(calendar_checkIn.getDate());// 체크인 날짜
		checkOutDate = fm.format(calendar_checkOut.getDate());// 체크아웃 날짜

		// 인원이 int 아닐 경우
		try {
			Integer.parseInt(occupantsNum);
			isOccupantsNumSuccessed = true;
		} catch (NumberFormatException e) {
			isOccupantsNumSuccessed = false;
		}
	}

	public void setTodate() {
		// 현재 날짜/시간 얻기
		Calendar calendar = Calendar.getInstance();
		toyear = Integer.toString(calendar.get(Calendar.YEAR));
		tomonth = Integer.toString(calendar.get(Calendar.MONTH) + 1);
		if (tomonth.length() == 1) // 한자리 수라면,
			tomonth = "0" + tomonth; // 앞에 0붙이기
		today = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
		if (today.length() == 1) // 한자리 수라면,
			today = "0" + today; // 앞에 0붙이기

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

			// 이용 가능한 객실번호 배열 저장하기
			SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
			Calendar cal = Calendar.getInstance();

			Date indate = fm.parse(checkInDate);
			Date outdate = fm.parse(checkOutDate);

			for (cal.setTime(indate); cal.getTime().compareTo(outdate) < 0; cal.add(Calendar.DATE, 1)) {
				String thisDate = fm.format(cal.getTime());

				sql = "select * from 호텔객실 where 날짜 = '" + thisDate + "';"; // 해당 날짜의 호텔객실 table 불러오기
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					for (int i = 2; i <= 101; i++) {
						if (rs.getString(i).equals("1") && roomIsAble[i - 2] == true) // 해당 객실이 당일에 이용가능하고 전날에도 이용가능했다면
							roomIsAble[i - 2] = true; // 이용 가능 목록에 true로 추가
						else
							roomIsAble[i - 2] = false; // false주어, 그 객실은 이용 불가하므로 계산 안하게
					}
				}
			}

			for (int i = 0; i < 100; i++) // 최종적으로 구해진 rooIsAble 배열에서,
				if (roomIsAble[i] == true) {// roomIsAble이 true인 방을 모두 뽑음
					ableRoomArray.add(roomArray[i]); // 해당 방 호수를 모두 저장
				}

			// 객실 가격 계산 및, 변동 이유 배열 저장하기
			int price = 0;

			for (cal.setTime(indate); cal.getTime().compareTo(outdate) < 0; cal.add(Calendar.DATE, 1)) {
				String thisDate = fm.format(cal.getTime());
				sql = "select * from 호텔객실 where 날짜 = '" + thisDate + "';"; // 해당 날짜의 호텔객실 table 불러오기
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					price += Integer.parseInt(rs.getString("객실가격")); // 가격 계속 추가하면서 계산
					if (!rs.getString("가격변동이유").equals("변동없음")) // 변동 있으면은
						priceReason.add(thisDate + " : " + rs.getString("가격변동이유")); // 이유 배열에 저장
				}
			}
			roomPrice = Integer.toString(price); // 객실 가격 저장 했음!!

			// 최근 확인번호 저장하기
			sql = "select * from 고객정보";
			rs = stmt.executeQuery(sql);
			if (!rs.next()) // 고객정보에 아무것도 없을 경우, 디폴트값
				recentConfirmationNum = "10000";
			else {
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					recentConfirmationNum = rs.getString(9); // 9번째의 고객정보

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

	// 직접 입력한 것 말고, 디폴트값으로 넣거나 랜덤값으로 넣을 것 set
	public void createInfo() {

		// 객실번호, 객실가격 저장
		if (ableRoomArray.isEmpty() == true) { // 객실 남은거 없으면
			isReservationSuccessed = false;
			roomNumber = "추후 배정";
			roomPrice = "추후 확정";
		} else {
			isReservationSuccessed = true;
			int random = (int) Math.random() * ableRoomArray.size(); // 랜덤으로 하나 생성
			roomNumber = ableRoomArray.get(random); // 객실번호 저장하기
		}

		// 확인번호 저장
		int num = Integer.parseInt(recentConfirmationNum) + 1;
		confirmationNumber = Integer.toString(num);

		// 자동취소일
		try {
			SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
			Calendar cal = Calendar.getInstance(); // 자동취소일 캘린더
			Date inDate = fm.parse(checkInDate); // 체크인 날짜

			cal.setTime(inDate);
			cal.add(Calendar.DATE, -7); // 체크인 날짜 -7
			cancelDate = fm.format(cal.getTime());

			if (todate.compareTo(cancelDate) > 0) // 오늘 >= 자동취소일 날짜
				cancelDate = todate; // 오늘이 자동취소일
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		// 현재상태
		if (ableRoomArray.isEmpty() == true) // 객실 남은거 없으면
			state = "예약대기";
		else
			state = "예약완료";

		// 신용카드번호
		cardNum = "미결제";

		// 청구금액
		payment = "아직 청구되지않음"; // 수정 필요

		// 피드백, 특별요청
		feedback = "내용없음";
		request = "내용없음";
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

			// 고객정보 table에 update
			String sql = "insert into 고객정보 values(";
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

			// 호텔객실 table에 update (이용 불가하게)
			if (!roomNumber.equals("추후 배정")) { // 자리 없을 경우에는 변경 안시킴
				String thisDate; // for문에 넣을 날짜(String)
				SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
				Calendar cal = Calendar.getInstance();
				Date indate = fm.parse(checkInDate); // 체크인 날짜
				Date outdate = fm.parse(checkOutDate); // 체크아웃 날짜
				for (cal.setTime(indate); cal.getTime().compareTo(outdate) < 0; cal.add(Calendar.DATE, 1)) {
					thisDate = fm.format(cal.getTime());
					sql = "update 호텔객실 set " + roomNumber + "호 = 0 where 날짜 = '" + thisDate + "';";
					stmt.executeUpdate(sql);
				}
			}

			// 여기서부터 예외처리
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
		} // 예외처리 끝
	}

	public void openUpdateFrame() {
		this.isLogIn = MainFrame.getIsLogIn();
		this.userID = MainFrame.getUserID();
		this.position = MainFrame.getPosition();

		if (isLogIn) // 모든 화면에서 뜨게 할 로그인상태 라벨 생성
			lable_logInState.setText("로그인 user : " + userID); // 변경하여 표시
		frame.setVisible(true);
	}

	public void closeUpdateFrame() {
		frame.setVisible(false);
	}

	/*
	 * 게터 메소드
	 */
	public String getConfirmationNumber() {
		return confirmationNumber;
	}

	public ArrayList<String> getPriceReason() {
		return priceReason;
	}
}