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
 * ���� ȭ��
 */

public class PaymentFrame {

	private JFrame frame;

	// ������ ���� ��ҵ�
	private JLabel lable_logInState;
	private JTextField textField_creditCard1;
	private JTextField textField_creditCard2;
	private JTextField textField_creditCard3;
	private JTextField textField_creditCard4;
	private JButton button_pay;
	private JButton button_back;

	// ��� ȭ�鿡�� �߰��� �� ���� ��ҵ� (â�� �ٲ� ����)
	private boolean isLogIn;
	private String userID;
	private String position;

	// �����ص� ��
	private String thisConfirmationNumber; // �ٲ� Ȯ�ι�ȣ ����
	private String creditCard = null;
	private boolean isRight = false; // card ���� �´���

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

		// �α��� ���� ��
		lable_logInState = new JLabel(" \uB85C\uADF8\uC544\uC6C3 \uC0C1\uD0DC");
		lable_logInState.setForeground(Color.RED);
		lable_logInState.setBounds(12, 363, 151, 42);
		frame.getContentPane().add(lable_logInState);

		// �����ϱ� ��ư
		button_pay = new JButton("\uACB0\uC81C\uD558\uAE30");
		button_pay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setInfo(); // creditcard �ʱ�ȭ
				if (creditCard.length() <= 15)
					JOptionPane.showMessageDialog(null, "������ ��Ȯ�ϰ� �Է��ϼ���.");
				else {
					modifyDB();// DB�� �����ߴٰ� �����ϱ�
					JOptionPane.showMessageDialog(null, "���� �Ǿ����ϴ�.");

					ReservationFrame reservationFrame = new ReservationFrame();
					closePaymentFrame();
					reservationFrame.openReservationFrame();
				}
			}
		});
		button_pay.setBounds(448, 363, 91, 30);
		frame.getContentPane().add(button_pay);

		// ���� ��ư Ŭ�� ��
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
		// ��� �̹���
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

			String sql = "update ������ set ������� = '���ǰ����Ϸ�', �ſ�ī���ȣ = '" + creditCard + "' where Ȯ�ι�ȣ= '"
					+ thisConfirmationNumber + "' limit 1;";
			stmt.executeUpdate(sql);

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

	public void openPaymentFrame() {
		this.isLogIn = MainFrame.getIsLogIn();
		this.userID = MainFrame.getUserID();
		this.position = MainFrame.getPosition();

		if (isLogIn) // ��� ȭ�鿡�� �߰� �� �α��λ��� �� ����
			lable_logInState.setText("�α��� user : " + userID); // �����Ͽ� ǥ��
		frame.setVisible(true);
	}

	public void closePaymentFrame() {
		frame.setVisible(false);
	}
}