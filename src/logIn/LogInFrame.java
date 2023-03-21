package logIn;

import main.*;
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

/*
 * 로그인 화면
 */

public class LogInFrame {

	private JFrame frame;

	// 프레임 구성 요소들
	private JTextField textField_ID;
	private JTextField textField_PW;

	// DB에서 받아와서, mainFrame으로 넘길 요소들
	private boolean isLogIn = false;
	private String userID = null;
	private String position = null;

	// setInfo() 필요 요소들
	private String guessID = null;
	private String guessPW = null;

	// 확인 역할
	private boolean isIDRight = false;
	private boolean isPWRight = false;

	/**
	 * Create the application.
	 */
	public LogInFrame() {
		initialize();

		// 로그인화면으로 들어오면, 무조건 로그아웃됨!
		isLogIn = false;
		userID = null;
		position = null;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 782, 507);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		// 로그인 버튼
		JButton button_LogIn = new JButton("로그인");
		button_LogIn.setBounds(426, 316, 85, 69);
		button_LogIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setInfo(); // 초기화
				if (guessID.length() == 0 || guessPW.length() == 0)
					JOptionPane.showMessageDialog(null, "내용을 입력하세요.");
				else {
					logInDB(); // 로그인 진행

					if (isIDRight == false) // ID 다른경우
						JOptionPane.showMessageDialog(null, "ID가 일치하지 않습니다.");
					else if (isPWRight == false) // PW 다른경우
						JOptionPane.showMessageDialog(null, "PassWord가 일치하지 않습니다.");
					else if (isLogIn) { // 둘다 일치
						JOptionPane.showMessageDialog(null, "로그인 성공");

						MainFrame mainFrame = new MainFrame();
						mainFrame.setInfo(isLogIn, userID, position); // 정보 넘기기
						closeLogInFrame();
						mainFrame.openMainFrame();
					}
				}

			}
		});
		frame.getContentPane().add(button_LogIn);

		/*
		 * 여기부터 구성 요소들
		 */
		textField_ID = new JTextField();
		textField_ID.setBounds(294, 316, 116, 21);
		textField_ID.setColumns(10);
		frame.getContentPane().add(textField_ID);

		textField_PW = new JTextField();
		textField_PW.setBounds(294, 363, 116, 21);
		textField_PW.setColumns(10);
		frame.getContentPane().add(textField_PW);

		JLabel lblId = new JLabel("ID : ");
		lblId.setBackground(Color.WHITE);
		lblId.setFont(new Font("굴림", Font.BOLD, 15));
		lblId.setForeground(Color.BLACK);
		lblId.setBounds(259, 316, 30, 18);
		frame.getContentPane().add(lblId);

		JLabel lblPw = new JLabel("PW :");
		lblPw.setForeground(Color.BLACK);
		lblPw.setFont(new Font("굴림", Font.BOLD, 15));
		lblPw.setBounds(248, 364, 36, 18);
		frame.getContentPane().add(lblPw);

		// 배경이미지
		JLabel backgroundIMG = new JLabel("");
		backgroundIMG.setIcon(new ImageIcon(".\\img\\HT.PNG")); // 상대경로!!
		backgroundIMG.setBounds(0, 0, 766, 468);
		frame.getContentPane().add(backgroundIMG);
	}

	// 구성요소 초기화
	public void setInfo() {
		guessID = textField_ID.getText();
		guessPW = textField_PW.getText();
	}

	// 로그인 DB 불러오기
	public void logInDB() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);
			String sql = "select * from 로그인"; // 로그인 테이블 접속
			rs = stmt.executeQuery(sql);

			// 로그인 진행
			while (rs.next()) {
				if (rs.getString(1).equals(guessID)) {
					isIDRight = true;
					if (rs.getString(2).equals(guessPW)) {
						isPWRight = true;
						isLogIn = true;
						userID = rs.getString(1);
						position = rs.getString(3);
						break;
					}
					break;
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

	public void openLogInFrame() {
		frame.setVisible(true);
	}

	public void closeLogInFrame() {
		frame.setVisible(false);
	}
}