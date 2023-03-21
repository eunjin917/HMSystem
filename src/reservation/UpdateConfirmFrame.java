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
 * ���� ������Ʈ Ȯ�� ȭ��
 */

public class UpdateConfirmFrame {

	private JFrame frame;

	// ������ ���� ��ҵ�
	private JLabel lable_logInState;
	private JButton button_save;
	private JButton button_ok;
	private JList list_result;
	private JList list_reason;

	// ��� ȭ�鿡�� �߰��� �� ���� ��ҵ� (â�� �ٲ� ����)
	private boolean isLogIn;
	private String userID;
	private String position;

	// JList���� �ʿ���
	private DefaultListModel listModel;
	private DefaultListModel listModel2;

	// �����ص� ��
	private String[] typeName; // ������ ���� ����
	private ArrayList<String> priceReason = null; // ���� �������� ����

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

		// �α��� ���� ��
		lable_logInState = new JLabel(" \uB85C\uADF8\uC544\uC6C3 \uC0C1\uD0DC");
		lable_logInState.setForeground(Color.RED);
		lable_logInState.setBounds(12, 435, 224, 42);
		frame.getContentPane().add(lable_logInState);

		// Ȯ�� ��ư
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
		 * ������� ���� ��ҵ�
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
		lblNewLabel.setFont(new Font("���� ���", Font.PLAIN, 14));
		lblNewLabel.setBounds(34, 38, 70, 23);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("\uC694\uAE08\uBCC0\uACBD\uC0AC\uC720");
		lblNewLabel_1.setFont(new Font("���� ���", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(543, 57, 91, 23);
		frame.getContentPane().add(lblNewLabel_1);
		//��� �̹���
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

			// Ȯ�ι�ȣ�� �ش� ������ �˻�
			String sql = "select * from ������ where Ȯ�ι�ȣ = '" + confirmationNumber + "' limit 1;";
			rs = stmt.executeQuery(sql);
			String str;

			while (rs.next()) {
				listModel.addElement("---------" + "������ �Ϸ�Ǿ����ϴ�. " + "---------");
				for (int i = 1; i <= 15; i++) { // 15�� �׸�
					str = typeName[i - 1] + " | " + rs.getString(i);
					listModel.addElement(str);
				}
			}

			list_result.setModel(listModel);

			// ���ǿ�� ���� ���� ����
			if (priceReason.size() == 0)
				listModel2.addElement("���ǿ�� ���� ����");
			else {
				listModel2.addElement("----------���ǿ�� ���� ���� ----------");
				for (int i = 0; i < priceReason.size(); i++)
					listModel2.addElement(priceReason.get(i));
			}

			list_reason.setModel(listModel2);

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

	public void openUpdateConfirmFrame(ArrayList<String> priceReason) { // �ش� Ȯ�ι�ȣ, ���ǹ�ȣ �޾ƾ���
		this.priceReason = priceReason; // ���� ���� ����

		this.isLogIn = MainFrame.getIsLogIn();
		this.userID = MainFrame.getUserID();
		this.position = MainFrame.getPosition();

		if (isLogIn) // ��� ȭ�鿡�� �߰� �� �α��λ��� �� ����
			lable_logInState.setText("�α��� user : " + userID); // �����Ͽ� ǥ��

		loadDB(); // â open�� ���ÿ�, DB������ ����ȭ�� ����

		frame.setVisible(true);
	}

	public void closeUpdateConfirmFrame() {
		frame.setVisible(false);
	}
}
