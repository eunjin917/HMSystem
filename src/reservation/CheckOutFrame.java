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
 * 체크아웃 화면
 */
public class CheckOutFrame {

	private JFrame frame;

	// 프레임 구성 요소들
	private JLabel lable_logInState;
	private JLabel label_sum;
	private JButton button_back;
	private JButton button_checkOut;
	private JTable table_orderList;
	private JTextField textField_feedback;

	// 모든 화면에서 뜨게할 라벨 구성 요소들 (창이 바뀌어도 유지)
	private boolean isLogIn;
	private String userID;
	private String position;

	// JTable에서 필요함
	private DefaultTableModel defaultTableModel_orderList;
	private String[] header_orderList = { "메뉴", "가격", "수량" };

	// 저장해둘 것
	private String confirmation; // 해당 확인번호 저장
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
		label.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
		label.setBounds(12, 58, 57, 23);
		frame.getContentPane().add(label);

		// 로그인 상태 라벨
		lable_logInState = new JLabel(" \uB85C\uADF8\uC544\uC6C3 \uC0C1\uD0DC");
		lable_logInState.setForeground(Color.RED);
		lable_logInState.setBounds(12, 362, 173, 42);
		frame.getContentPane().add(lable_logInState);

		// 체크아웃 버튼 버튼
		button_checkOut = new JButton("\uCD94\uAC00\uACB0\uC81C \uBC0F \uCCB4\uD06C\uC544\uC6C3\uD558\uAE30");
		button_checkOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 주문서 출력 -> 결제하도록
				setInfo(); // feedback 초기화
				modifyDB(); // DB에 체크아웃했다고 수정하기
				JOptionPane.showMessageDialog(null, "체크아웃 되었습니다.");

				ReservationFrame reservationFrame = new ReservationFrame();
				closeCheckOutFrame();
				reservationFrame.openReservationFrame();
			}

		});
		button_checkOut.setBounds(347, 343, 192, 34);
		frame.getContentPane().add(button_checkOut);

		// 이전 버튼
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
		 * 구성 요소
		 */
		textField_feedback = new JTextField();
		textField_feedback.setBounds(12, 81, 403, 48);
		frame.getContentPane().add(textField_feedback);
		textField_feedback.setColumns(10);

		// 청구 금액 표시
		label_sum = new JLabel("\uCCAD\uAD6C \uAE08\uC561 : 0\uC6D0");
		label_sum.setBounds(161, 314, 160, 48);
		frame.getContentPane().add(label_sum);

		/*
		 * 테이블 관련
		 */
		// orderList
		defaultTableModel_orderList = new DefaultTableModel(null, header_orderList);
		table_orderList = new JTable(defaultTableModel_orderList);

		JScrollPane scrollPane_orderList = new JScrollPane();
		scrollPane_orderList.setBounds(12, 139, 403, 175);
		frame.getContentPane().add(scrollPane_orderList);

		table_orderList.setEnabled(false); // 이용못하게
		table_orderList.getTableHeader().setReorderingAllowed(false);// 이동 불가하게 설정
		table_orderList.setAutoCreateRowSorter(true);// 헤더를 클릭하면 행을 자동정렬
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

			// orderList 불러오기
			String sql = "select * from 주문 where 확인번호 = '" + confirmation + "';";
			rs = stmt.executeQuery(sql);

			if (rs.next() == false) // 검색결과 없으면
				JOptionPane.showMessageDialog(null, "레스토랑 이용 하지 않은 고객입니다.");

			else {
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					String stringData = rs.getString(2);

					while (stringData.length() != 0) {
						/*
						 * 데이터 하나 추출하기
						 */

						Object data2[] = new Object[3];

						// 메뉴 추출
						int endIndex = stringData.indexOf("-");
						data2[0] = stringData.substring(0, endIndex);
						stringData = stringData.substring(endIndex + 1); // 추출한거 제외하기

						// 가격 추출
						endIndex = stringData.indexOf("*");
						data2[1] = stringData.substring(0, endIndex);
						stringData = stringData.substring(endIndex + 1); // 추출한거 제외하기

						// 수량 추출
						endIndex = stringData.indexOf("/");
						data2[2] = stringData.substring(0, endIndex);
						stringData = stringData.substring(endIndex + 1); // 추출한거 제외하기

						// table에 넣기
						defaultTableModel_orderList.addRow(data2);
					}
					stringData = rs.getString(2); // 다시 원래대로 초기화하기
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

	public void calPriceSum() {
		for (int i = 0; i < table_orderList.getRowCount(); i++)
			sumPrice += Integer.parseInt(table_orderList.getValueAt(i, 1).toString());
	}

	public void setInfo() {
		feedback = textField_feedback.getText();

		if (feedback.length() == 0)
			feedback = "피드백 없음";
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

			// 고객정보 table 바꾸기
			String sql = "update 고객정보 set 현재상태 = '체크아웃', 피드백 = '" + feedback + "' where 확인번호= '" + confirmation
					+ "' limit 1;";
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
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException se) {
				}
		} // 예외처리 끝
	}

	public void openCheckOutFrame() {
		loadOrderDB();// open하자마자 opderlist띄우기
		calPriceSum();// 가격합계 구하기
		label_sum.setText("청구금액 : " + sumPrice + " 원"); // 변경하여 표시

		this.isLogIn = MainFrame.getIsLogIn();
		this.userID = MainFrame.getUserID();
		this.position = MainFrame.getPosition();

		if (isLogIn) // 모든 화면에서 뜨게 할 로그인상태 라벨 생성
			lable_logInState.setText("로그인 user : " + userID); // 변경하여 표시
		frame.setVisible(true);
	}

	public void closeCheckOutFrame() {
		frame.setVisible(false);
	}
}
