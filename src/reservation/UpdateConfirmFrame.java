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

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import main.MainFrame;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import java.awt.Font;
import javax.swing.ImageIcon;

/*
 * 예약 업데이트 확인 화면
 */

public class UpdateConfirmFrame {

	private JFrame frame;

	// 프레임 구성 요소들
	private JLabel lable_logInState;
	private JButton button_save;
	private JButton button_ok;
	private JList list_result;
	private JList list_reason;

	// 모든 화면에서 뜨게할 라벨 구성 요소들 (창이 바뀌어도 유지)
	private boolean isLogIn;
	private String userID;
	private String position;

	// JList에서 필요함
	private DefaultListModel listModel;
	private DefaultListModel listModel2;

	// 저장해둘 것
	private String[] typeName; // 고객정보 종류 저장
	private ArrayList<String> priceReason = null; // 가격 변동이유 저장

	/**
	 * Create the application.
	 */
	public UpdateConfirmFrame() {
		initialize();

		typeName = MainFrame.getTypeName();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 825, 516);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		// 로그인 상태 라벨
		lable_logInState = new JLabel(" \uB85C\uADF8\uC544\uC6C3 \uC0C1\uD0DC");
		lable_logInState.setForeground(Color.RED);
		lable_logInState.setBounds(12, 435, 224, 42);
		frame.getContentPane().add(lable_logInState);

		// 확인 버튼
		button_ok = new JButton("\uD655\uC778");
		button_ok.setBounds(706, 10, 91, 23);
		button_ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ReservationFrame reservationFrame = new ReservationFrame();
				closeUpdateConfirmFrame();
				reservationFrame.openReservationFrame();
			}
		});
		frame.getContentPane().add(button_ok);

		/*
		 * 여기부터 구성 요소들
		 */
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(32, 61, 499, 341);
		frame.getContentPane().add(scrollPane);

		list_result = new JList();
		scrollPane.setViewportView(list_result);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(543, 81, 224, 61);
		frame.getContentPane().add(scrollPane_1);
		list_reason = new JList();
		scrollPane_1.setViewportView(list_reason);
		
		JLabel lblNewLabel = new JLabel("\uC608\uC57D\uD655\uC778");
		lblNewLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		lblNewLabel.setBounds(34, 38, 70, 23);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("\uC694\uAE08\uBCC0\uACBD\uC0AC\uC720");
		lblNewLabel_1.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(543, 57, 91, 23);
		frame.getContentPane().add(lblNewLabel_1);
		//배경 이미지
		JLabel lblNewLabel_2 = new JLabel("");
		lblNewLabel_2.setIcon(new ImageIcon(".\\img\\HTupdatecon.PNG"));
		lblNewLabel_2.setBounds(0, 0, 809, 511);
		frame.getContentPane().add(lblNewLabel_2);

	}

	public void loadDB() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		UpdateFrame updateFrame = new UpdateFrame();

		String confirmationNumber = updateFrame.getConfirmationNumber();

		listModel = new DefaultListModel();
		listModel2 = new DefaultListModel();

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			// 확인번호로 해당 고객정보 검색
			String sql = "select * from 고객정보 where 확인번호 = '" + confirmationNumber + "' limit 1;";
			rs = stmt.executeQuery(sql);
			String str;

			while (rs.next()) {
				listModel.addElement("---------" + "예약이 완료되었습니다. " + "---------");
				for (int i = 1; i <= 15; i++) { // 15개 항목
					str = typeName[i - 1] + " | " + rs.getString(i);
					listModel.addElement(str);
				}
			}

			list_result.setModel(listModel);

			// 객실요금 변동 이유 변경
			if (priceReason.size() == 0)
				listModel2.addElement("객실요금 변동 없음");
			else {
				listModel2.addElement("----------객실요금 변동 이유 ----------");
				for (int i = 0; i < priceReason.size(); i++)
					listModel2.addElement(priceReason.get(i));
			}

			list_reason.setModel(listModel2);

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

	public void openUpdateConfirmFrame(ArrayList<String> priceReason) { // 해당 확인번호, 객실번호 받아야함
		this.priceReason = priceReason; // 가격 이유 저장

		this.isLogIn = MainFrame.getIsLogIn();
		this.userID = MainFrame.getUserID();
		this.position = MainFrame.getPosition();

		if (isLogIn) // 모든 화면에서 뜨게 할 로그인상태 라벨 생성
			lable_logInState.setText("로그인 user : " + userID); // 변경하여 표시

		loadDB(); // 창 open과 동시에, DB연동된 예약화면 띄우기

		frame.setVisible(true);
	}

	public void closeUpdateConfirmFrame() {
		frame.setVisible(false);
	}
}
