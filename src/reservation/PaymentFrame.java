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
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;

/*
 * 결제 화면
 */

public class PaymentFrame {

	private JFrame frame;

	// 프레임 구성 요소들
	private JLabel lable_logInState;
	private JTextField textField_creditCard1;
	private JTextField textField_creditCard2;
	private JTextField textField_creditCard3;
	private JTextField textField_creditCard4;
	private JButton button_pay;
	private JButton button_back;

	// 모든 화면에서 뜨게할 라벨 구성 요소들 (창이 바뀌어도 유지)
	private boolean isLogIn;
	private String userID;
	private String position;

	// 저장해둘 것
	private String thisConfirmationNumber; // 바꿀 확인번호 저장
	private String creditCard = null;
	private boolean isRight = false; // card 길이 맞는지

	/**
	 * Create the application.
	 */
	public PaymentFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setBounds(100, 100, 567, 444);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		// 로그인 상태 라벨
		lable_logInState = new JLabel(" \uB85C\uADF8\uC544\uC6C3 \uC0C1\uD0DC");
		lable_logInState.setForeground(Color.RED);
		lable_logInState.setBounds(12, 363, 151, 42);
		frame.getContentPane().add(lable_logInState);

		// 결제하기 버튼
		button_pay = new JButton("\uACB0\uC81C\uD558\uAE30");
		button_pay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setInfo(); // creditcard 초기화
				if (creditCard.length() <= 15)
					JOptionPane.showMessageDialog(null, "내용을 정확하게 입력하세요.");
				else {
					modifyDB();// DB에 결제했다고 수정하기
					JOptionPane.showMessageDialog(null, "결제 되었습니다.");

					ReservationFrame reservationFrame = new ReservationFrame();
					closePaymentFrame();
					reservationFrame.openReservationFrame();
				}
			}
		});
		button_pay.setBounds(448, 363, 91, 30);
		frame.getContentPane().add(button_pay);

		// 이전 버튼 클릭 시
		button_back = new JButton("\uC774\uC804");
		button_back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ReservationFrame reservationFrame = new ReservationFrame();
				closePaymentFrame();
				reservationFrame.openReservationFrame();
			}
		});
		button_back.setBounds(448, 10, 91, 23);
		frame.getContentPane().add(button_back);

		textField_creditCard1 = new JTextField();
		textField_creditCard1.setBounds(102, 318, 84, 21);
		frame.getContentPane().add(textField_creditCard1);
		textField_creditCard1.setColumns(10);

		textField_creditCard2 = new JTextField();
		textField_creditCard2.setColumns(10);
		textField_creditCard2.setBounds(198, 318, 84, 21);
		frame.getContentPane().add(textField_creditCard2);

		textField_creditCard3 = new JTextField();
		textField_creditCard3.setColumns(10);
		textField_creditCard3.setBounds(294, 318, 84, 21);
		frame.getContentPane().add(textField_creditCard3);

		textField_creditCard4 = new JTextField();
		textField_creditCard4.setColumns(10);
		textField_creditCard4.setBounds(391, 318, 84, 21);
		frame.getContentPane().add(textField_creditCard4);
		// 배경 이미지
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(".\\img\\HTpayment.PNG"));
		lblNewLabel.setBounds(0, 10, 551, 432);
		frame.getContentPane().add(lblNewLabel);

	}

	public void setConfirmation(String cfmn) {
		thisConfirmationNumber = cfmn;
	}

	public void setInfo() {
		creditCard = textField_creditCard1.getText() + textField_creditCard2.getText() + textField_creditCard3.getText()
				+ textField_creditCard4.getText();
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

			String sql = "update 고객정보 set 현재상태 = '객실결제완료', 신용카드번호 = '" + creditCard + "' where 확인번호= '"
					+ thisConfirmationNumber + "' limit 1;";
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

	public void openPaymentFrame() {
		this.isLogIn = MainFrame.getIsLogIn();
		this.userID = MainFrame.getUserID();
		this.position = MainFrame.getPosition();

		if (isLogIn) // 모든 화면에서 뜨게 할 로그인상태 라벨 생성
			lable_logInState.setText("로그인 user : " + userID); // 변경하여 표시
		frame.setVisible(true);
	}

	public void closePaymentFrame() {
		frame.setVisible(false);
	}
}