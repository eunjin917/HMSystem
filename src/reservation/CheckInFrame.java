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
 * üũ�� ȭ��
 */
public class CheckInFrame {

	private JFrame frame;

	// ������ ���� ��ҵ�
	private JLabel lable_logInState;
	private JButton button_back;
	private JButton button_checkIn;
	private JTextField textField_request;

	// ��� ȭ�鿡�� �߰��� �� ���� ��ҵ� (â�� �ٲ� ����)
	private boolean isLogIn;
	private String userID;
	private String position;

	// �����ص� ��
	private String thisConfirmationNumber; // �ٲ� Ȯ�ι�ȣ ����
	private String request = null; // ��û����

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

		// �α��� ���� ��
		lable_logInState = new JLabel(" \uB85C\uADF8\uC544\uC6C3 \uC0C1\uD0DC");
		lable_logInState.setForeground(Color.RED);
		lable_logInState.setBounds(12, 362, 173, 42);
		frame.getContentPane().add(lable_logInState);

		// üũ�� ��ư
		button_checkIn = new JButton("\uCCB4\uD06C\uC778");
		button_checkIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setInfo(); // request �ʱ�ȭ
				modifyDB(); // DB�� üũ���ߴٰ� �����ϱ�
				JOptionPane.showMessageDialog(null, "üũ�� �Ǿ����ϴ�.");

				ReservationFrame reservationFrame = new ReservationFrame();
				closeCheckInFrame();
				reservationFrame.openReservationFrame();

			}
		});
		button_checkIn.setBounds(417, 227, 91, 25);
		frame.getContentPane().add(button_checkIn);

		// ���� ��ư
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
		lblNewLabel.setFont(new Font("���� ���", Font.PLAIN, 14));
		lblNewLabel.setBounds(101, 119, 74, 33);
		frame.getContentPane().add(lblNewLabel);
		//��� �̹���
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
			request = "��û���� ����";
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

			String sql2 = "update ������ set ������� = 'üũ��', ��û���� = '" + request + "' where Ȯ�ι�ȣ= '"
					+ thisConfirmationNumber + "' limit 1;";
			stmt.executeUpdate(sql2);

			// ������� ����ó��
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
		} // ����ó�� ��
	}

	public void openCheckInFrame() {
		this.isLogIn = MainFrame.getIsLogIn();
		this.userID = MainFrame.getUserID();
		this.position = MainFrame.getPosition();

		if (isLogIn) // ��� ȭ�鿡�� �߰� �� �α��λ��� �� ����
			lable_logInState.setText("�α��� user : " + userID); // �����Ͽ� ǥ��
		frame.setVisible(true);
	}

	public void closeCheckInFrame() {
		frame.setVisible(false);
	}
}