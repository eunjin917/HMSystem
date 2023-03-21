package main;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import logIn.LogInFrame;
import mealSet.MealSetFrame;
import reservation.*;
import restaurant.SetUserForOrderFrame;
import roomSet.RoomSetFrame;
import userSet.UserSetFrame;

import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Font;

/*
 * 메인 화면
 */

public class MainFrame {
	private JFrame frame;

	// 프레임 구성 요소들
	private JLabel lable_logInState;
	private JButton button_reservation;
	private JButton button_logIn;
	private JButton button_userSet;
	private JButton button_order;
	private JButton button_roomSet;
	private JButton button_mealSet;

	// 모든 화면에서 뜨게할 라벨 구성 요소들 (창이 바뀌어도 유지)
	private static boolean isLogIn = false;
	private static String userID = null;
	private static String position = null;

	/**
	 * Create the application.
	 */
	public MainFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 498, 640);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);

		// 로그인상태 라벨
		lable_logInState = new JLabel(" \uB85C\uADF8\uC544\uC6C3 \uC0C1\uD0DC");
		lable_logInState.setForeground(Color.RED);
		lable_logInState.setBounds(12, 569, 174, 42);
		frame.getContentPane().add(lable_logInState);

		// 로그인 버튼
		button_logIn = new JButton("\uB85C\uADF8\uC544\uC6C3");
		button_logIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isLogIn == true) // 이미 로그인된 경우
					JOptionPane.showMessageDialog(null, "ID : " + userID + " 로그아웃 합니다.");

				LogInFrame logInFrame = new LogInFrame();
				closeMainFrame();
				logInFrame.openLogInFrame();
			}
		});
		button_logIn.setBounds(352, 182, 103, 69);
		frame.getContentPane().add(button_logIn);

		// 예약 버튼
		button_reservation = new JButton("\uC608\uC57D");
		button_reservation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				ReservationFrame reservationSetFrame = new ReservationFrame();
				closeMainFrame();
				reservationSetFrame.openReservationFrame();
			}
		});
		button_reservation.setBounds(26, 182, 103, 69);
		frame.getContentPane().add(button_reservation);

		// 사용자관리 버튼
		button_userSet = new JButton("\uC0AC\uC6A9\uC790 \uAD00\uB9AC");
		button_userSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UserSetFrame userSetFrame = new UserSetFrame();
				closeMainFrame();
				userSetFrame.openUserSetFrame();
			}
		});
		button_userSet.setBounds(352, 344, 103, 69);
		frame.getContentPane().add(button_userSet);

		// 객실관리 버튼
		button_roomSet = new JButton("\uAC1D\uC2E4 \uAD00\uB9AC");
		button_roomSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					RoomSetFrame roomSetFrame = new RoomSetFrame();
					closeMainFrame();
					roomSetFrame.openRoomSetFrame();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});
		button_roomSet.setBounds(185, 344, 103, 69);
		frame.getContentPane().add(button_roomSet);

		// 음식관리 버튼
		button_mealSet = new JButton("\uC74C\uC2DD \uAD00\uB9AC");
		button_mealSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MealSetFrame mealSetFrame = new MealSetFrame();
				closeMainFrame();
				mealSetFrame.openMealSetFrame();
			}
		});
		button_mealSet.setBounds(26, 344, 103, 69);
		frame.getContentPane().add(button_mealSet);

		// 룸서비스/레스토랑 버튼
		button_order = new JButton("\uB808\uC2A4\uD1A0\uB791");
		button_order.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SetUserForOrderFrame setUserForOrderFrame = new SetUserForOrderFrame();
				closeMainFrame();
				setUserForOrderFrame.openSetUserForOrderFrame();
			}
		});
		button_order.setBounds(185, 182, 103, 69);
		frame.getContentPane().add(button_order);

		JLabel lblNewLabel_2 = new JLabel("Choi eunjin");
		lblNewLabel_2.setFont(new Font("나눔스퀘어라운드 ExtraBold", Font.PLAIN, 12));
		lblNewLabel_2.setBounds(389, 515, 75, 15);
		frame.getContentPane().add(lblNewLabel_2);

		JLabel lblNewLabel_3 = new JLabel(" Jang jiuk");
		lblNewLabel_3.setFont(new Font("나눔스퀘어라운드 ExtraBold", Font.PLAIN, 12));
		lblNewLabel_3.setBounds(389, 528, 77, 21);
		frame.getContentPane().add(lblNewLabel_3);

		JLabel lblNewLabel_4 = new JLabel("Kim kwang min");
		lblNewLabel_4.setFont(new Font("나눔스퀘어라운드 ExtraBold", Font.PLAIN, 12));
		lblNewLabel_4.setBounds(383, 548, 97, 15);
		frame.getContentPane().add(lblNewLabel_4);

		JLabel lblNewLabel_1 = new JLabel("By");
		lblNewLabel_1.setFont(new Font("나눔스퀘어라운드 ExtraBold", Font.PLAIN, 15));
		lblNewLabel_1.setBounds(370, 496, 57, 18);
		frame.getContentPane().add(lblNewLabel_1);

		// 배경이미지
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(".\\img\\HTmain.PNG"));
		lblNewLabel.setBounds(0, 0, 492, 611);
		frame.getContentPane().add(lblNewLabel);
	}

	// 라벨 만들 때 필요한 정보 초기화
	public void setInfo(boolean isLogIn, String userID, String position) {
		this.isLogIn = isLogIn;
		this.userID = userID;
		this.position = position;
	}

	public void openMainFrame() {
		checkPosition(); // position에 따라, 버튼 접근이 다르게
		if (isLogIn) // 모든 화면에서 뜨게 할 로그인상태 라벨 생성
			lable_logInState.setText("로그인 user : " + userID); // 변경하여 표시
		frame.setVisible(true);
	}

	public void closeMainFrame() {
		frame.setVisible(false);
	}

	// position 확인
	public void checkPosition() {
		if (position.equals("최종사용자")) { // 최종사용자는 제한없음
		} else if (position.equals("매니저")) { // 매니저 이상
			button_userSet.setEnabled(false);
		} else if (position.equals("CSR")) { // CSR 이상
			button_roomSet.setEnabled(false);
			button_mealSet.setEnabled(false);
			button_userSet.setEnabled(false);
		}
	}

	/*
	 * 여기서부터 게터 메소드
	 */
	public static String getURL() {
		return "jdbc:mysql://192.168.43.62: 3306/hms?characterEncoding=UTF-8&serverTimezone=UTC"; // IP주소 바꾸기
	}

	public static String[] getTypeName() {
		String[] typeName = new String[] { "성", "이름", "인원", "객실번호", "객실가격", "연락처", "현재상태", "신용카드번호", "확인번호", "체크인날짜",
				"체크아웃날짜", "자동취소일", "청구금액", "피드백", "요청사항" };
		return typeName;
	}

	public static boolean getIsLogIn() {
		return isLogIn;
	}

	public static String getUserID() {
		return userID;
	}

	public static String getPosition() {
		return position;
	}
}
