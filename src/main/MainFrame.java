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
 * ���� ȭ��
 */

public class MainFrame {
	private JFrame frame;

	// ������ ���� ��ҵ�
	private JLabel lable_logInState;
	private JButton button_reservation;
	private JButton button_logIn;
	private JButton button_userSet;
	private JButton button_order;
	private JButton button_roomSet;
	private JButton button_mealSet;

	// ��� ȭ�鿡�� �߰��� �� ���� ��ҵ� (â�� �ٲ� ����)
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

		// �α��λ��� ��
		lable_logInState = new JLabel(" \uB85C\uADF8\uC544\uC6C3 \uC0C1\uD0DC");
		lable_logInState.setForeground(Color.RED);
		lable_logInState.setBounds(12, 569, 174, 42);
		frame.getContentPane().add(lable_logInState);

		// �α��� ��ư
		button_logIn = new JButton("\uB85C\uADF8\uC544\uC6C3");
		button_logIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isLogIn == true) // �̹� �α��ε� ���
					JOptionPane.showMessageDialog(null, "ID : " + userID + " �α׾ƿ� �մϴ�.");

				LogInFrame logInFrame = new LogInFrame();
				closeMainFrame();
				logInFrame.openLogInFrame();
			}
		});
		button_logIn.setBounds(352, 182, 103, 69);
		frame.getContentPane().add(button_logIn);

		// ���� ��ư
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

		// ����ڰ��� ��ư
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

		// ���ǰ��� ��ư
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

		// ���İ��� ��ư
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

		// �뼭��/������� ��ư
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
		lblNewLabel_2.setFont(new Font("������������� ExtraBold", Font.PLAIN, 12));
		lblNewLabel_2.setBounds(389, 515, 75, 15);
		frame.getContentPane().add(lblNewLabel_2);

		JLabel lblNewLabel_3 = new JLabel(" Jang jiuk");
		lblNewLabel_3.setFont(new Font("������������� ExtraBold", Font.PLAIN, 12));
		lblNewLabel_3.setBounds(389, 528, 77, 21);
		frame.getContentPane().add(lblNewLabel_3);

		JLabel lblNewLabel_4 = new JLabel("Kim kwang min");
		lblNewLabel_4.setFont(new Font("������������� ExtraBold", Font.PLAIN, 12));
		lblNewLabel_4.setBounds(383, 548, 97, 15);
		frame.getContentPane().add(lblNewLabel_4);

		JLabel lblNewLabel_1 = new JLabel("By");
		lblNewLabel_1.setFont(new Font("������������� ExtraBold", Font.PLAIN, 15));
		lblNewLabel_1.setBounds(370, 496, 57, 18);
		frame.getContentPane().add(lblNewLabel_1);

		// ����̹���
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(".\\img\\HTmain.PNG"));
		lblNewLabel.setBounds(0, 0, 492, 611);
		frame.getContentPane().add(lblNewLabel);
	}

	// �� ���� �� �ʿ��� ���� �ʱ�ȭ
	public void setInfo(boolean isLogIn, String userID, String position) {
		this.isLogIn = isLogIn;
		this.userID = userID;
		this.position = position;
	}

	public void openMainFrame() {
		checkPosition(); // position�� ����, ��ư ������ �ٸ���
		if (isLogIn) // ��� ȭ�鿡�� �߰� �� �α��λ��� �� ����
			lable_logInState.setText("�α��� user : " + userID); // �����Ͽ� ǥ��
		frame.setVisible(true);
	}

	public void closeMainFrame() {
		frame.setVisible(false);
	}

	// position Ȯ��
	public void checkPosition() {
		if (position.equals("���������")) { // ��������ڴ� ���Ѿ���
		} else if (position.equals("�Ŵ���")) { // �Ŵ��� �̻�
			button_userSet.setEnabled(false);
		} else if (position.equals("CSR")) { // CSR �̻�
			button_roomSet.setEnabled(false);
			button_mealSet.setEnabled(false);
			button_userSet.setEnabled(false);
		}
	}

	/*
	 * ���⼭���� ���� �޼ҵ�
	 */
	public static String getURL() {
		return "jdbc:mysql://192.168.43.62: 3306/hms?characterEncoding=UTF-8&serverTimezone=UTC"; // IP�ּ� �ٲٱ�
	}

	public static String[] getTypeName() {
		String[] typeName = new String[] { "��", "�̸�", "�ο�", "���ǹ�ȣ", "���ǰ���", "����ó", "�������", "�ſ�ī���ȣ", "Ȯ�ι�ȣ", "üũ�γ�¥",
				"üũ�ƿ���¥", "�ڵ������", "û���ݾ�", "�ǵ��", "��û����" };
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
