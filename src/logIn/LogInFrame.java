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
 * �α��� ȭ��
 */

public class LogInFrame {

	private JFrame frame;

	// ������ ���� ��ҵ�
	private JTextField textField_ID;
	private JTextField textField_PW;

	// DB���� �޾ƿͼ�, mainFrame���� �ѱ� ��ҵ�
	private boolean isLogIn = false;
	private String userID = null;
	private String position = null;

	// setInfo() �ʿ� ��ҵ�
	private String guessID = null;
	private String guessPW = null;

	// Ȯ�� ����
	private boolean isIDRight = false;
	private boolean isPWRight = false;

	/**
	 * Create the application.
	 */
	public LogInFrame() {
		initialize();

		// �α���ȭ������ ������, ������ �α׾ƿ���!
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

		// �α��� ��ư
		JButton button_LogIn = new JButton("�α���");
		button_LogIn.setBounds(426, 316, 85, 69);
		button_LogIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setInfo(); // �ʱ�ȭ
				if (guessID.length() == 0 || guessPW.length() == 0)
					JOptionPane.showMessageDialog(null, "������ �Է��ϼ���.");
				else {
					logInDB(); // �α��� ����

					if (isIDRight == false) // ID �ٸ����
						JOptionPane.showMessageDialog(null, "ID�� ��ġ���� �ʽ��ϴ�.");
					else if (isPWRight == false) // PW �ٸ����
						JOptionPane.showMessageDialog(null, "PassWord�� ��ġ���� �ʽ��ϴ�.");
					else if (isLogIn) { // �Ѵ� ��ġ
						JOptionPane.showMessageDialog(null, "�α��� ����");

						MainFrame mainFrame = new MainFrame();
						mainFrame.setInfo(isLogIn, userID, position); // ���� �ѱ��
						closeLogInFrame();
						mainFrame.openMainFrame();
					}
				}

			}
		});
		frame.getContentPane().add(button_LogIn);

		/*
		 * ������� ���� ��ҵ�
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
		lblId.setFont(new Font("����", Font.BOLD, 15));
		lblId.setForeground(Color.BLACK);
		lblId.setBounds(259, 316, 30, 18);
		frame.getContentPane().add(lblId);

		JLabel lblPw = new JLabel("PW :");
		lblPw.setForeground(Color.BLACK);
		lblPw.setFont(new Font("����", Font.BOLD, 15));
		lblPw.setBounds(248, 364, 36, 18);
		frame.getContentPane().add(lblPw);

		// ����̹���
		JLabel backgroundIMG = new JLabel("");
		backgroundIMG.setIcon(new ImageIcon(".\\img\\HT.PNG")); // �����!!
		backgroundIMG.setBounds(0, 0, 766, 468);
		frame.getContentPane().add(backgroundIMG);
	}

	// ������� �ʱ�ȭ
	public void setInfo() {
		guessID = textField_ID.getText();
		guessPW = textField_PW.getText();
	}

	// �α��� DB �ҷ�����
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
			String sql = "select * from �α���"; // �α��� ���̺� ����
			rs = stmt.executeQuery(sql);

			// �α��� ����
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

	public void openLogInFrame() {
		frame.setVisible(true);
	}

	public void closeLogInFrame() {
		frame.setVisible(false);
	}
}