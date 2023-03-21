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
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.awt.Font;
import javax.swing.ImageIcon;

/*
 * 체크인 화면
 */
public class CheckInFrame {

	private JFrame frame;

	// 프레임 구성 요소들
	private JLabel lable_logInState;
	private JButton button_back;
	private JButton button_checkIn;
	private JTextField textField_request;

	// 모든 화면에서 뜨게할 라벨 구성 요소들 (창이 바뀌어도 유지)
	private boolean isLogIn;
	private String userID;
	private String position;

	// 저장해둘 것
	private String thisConfirmationNumber; // 바꿀 확인번호 저장
	private String request = null; // 요청사항

	/**
	 * Create the application.
	 */
	public CheckInFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 567, 443);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		// 로그인 상태 라벨
		lable_logInState = new JLabel(" \uB85C\uADF8\uC544\uC6C3 \uC0C1\uD0DC");
		lable_logInState.setForeground(Color.RED);
		lable_logInState.setBounds(12, 362, 173, 42);
		frame.getContentPane().add(lable_logInState);

		// 체크인 버튼
		button_checkIn = new JButton("\uCCB4\uD06C\uC778");
		button_checkIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setInfo(); // request 초기화
				modifyDB(); // DB에 체크인했다고 수정하기
				JOptionPane.showMessageDialog(null, "체크인 되었습니다.");

				ReservationFrame reservationFrame = new ReservationFrame();
				closeCheckInFrame();
				reservationFrame.openReservationFrame();

			}
		});
		button_checkIn.setBounds(417, 227, 91, 25);
		frame.getContentPane().add(button_checkIn);

		// 이전 버튼
		button_back = new JButton("\uC774\uC804");
		button_back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ReservationFrame reservationFrame = new ReservationFrame();
				closeCheckInFrame();
				reservationFrame.openReservationFrame();
			}
		});
		button_back.setBounds(448, 10, 91, 23);
		frame.getContentPane().add(button_back);

		textField_request = new JTextField();
		textField_request.setBounds(102, 152, 300, 100);
		frame.getContentPane().add(textField_request);
		textField_request.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("\uC694\uCCAD \uC0AC\uD56D");
		lblNewLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		lblNewLabel.setBounds(101, 119, 74, 33);
		frame.getContentPane().add(lblNewLabel);
		//배경 이미지
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setIcon(new ImageIcon(".\\img\\HTcheckin.PNG"));
		lblNewLabel_1.setBounds(0, 0, 551, 425);
		frame.getContentPane().add(lblNewLabel_1);
	}

	public void setConfirmation(String cfmn) {
		thisConfirmationNumber = cfmn;
	}

	public void setInfo() {
		request = textField_request.getText();

		if (request.length() == 0)
			request = "요청사항 없음";
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

			String sql2 = "update 고객정보 set 현재상태 = '체크인', 요청사항 = '" + request + "' where 확인번호= '"
					+ thisConfirmationNumber + "' limit 1;";
			stmt.executeUpdate(sql2);

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

	public void openCheckInFrame() {
		this.isLogIn = MainFrame.getIsLogIn();
		this.userID = MainFrame.getUserID();
		this.position = MainFrame.getPosition();

		if (isLogIn) // 모든 화면에서 뜨게 할 로그인상태 라벨 생성
			lable_logInState.setText("로그인 user : " + userID); // 변경하여 표시
		frame.setVisible(true);
	}

	public void closeCheckInFrame() {
		frame.setVisible(false);
	}
}