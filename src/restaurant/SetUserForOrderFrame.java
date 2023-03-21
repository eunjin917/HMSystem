package restaurant;

import main.*;
import helper.*;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.JTable;
import javax.swing.JRadioButton;

/*
 * 룸서비스/레스토랑 화면
 */

public class SetUserForOrderFrame {

	private JFrame frame;

	// 프레임 구성 요소들
	private JLabel lable_logInState;
	private JTable table;
	private JScrollPane scrollPane;
	private JTextField textField_search;
	private JRadioButton radio_lastName;
	private JRadioButton radio_roomNumber;

	// 모든 화면에서 뜨게할 라벨 구성 요소들 (창이 바뀌어도 유지)
	private boolean isLogIn;
	private String userID;
	private String position;

	// JTable에서 필요함
	private DefaultTableModel defaultTableModel;
	private String[] header;

	// 저장해둘 것
	private String guess = null;
	private String type = "이름";
	private String confirmationNumber;

	// 확인 목적
	private boolean isNull = false;

	/**
	 * Create the application.
	 */
	public SetUserForOrderFrame() {
		String[] typeName = MainFrame.getTypeName();
		header = new String[typeName.length + 1];
		header[0] = "선택";
		for (int i = 1; i < typeName.length + 1; i++)
			header[i] = typeName[i - 1];

		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1052, 642);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		// 로그인 상태 라벨
		lable_logInState = new JLabel(" \uB85C\uADF8\uC544\uC6C3 \uC0C1\uD0DC");
		lable_logInState.setForeground(Color.RED);
		lable_logInState.setBounds(12, 561, 174, 42);
		frame.getContentPane().add(lable_logInState);

		// 검색 버튼
		JButton button_search = new JButton("\uAC80\uC0C9");
		button_search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setInfo(); // guess 초기화

				loadDB(); // 검색하기
				if (isNull == true)
					JOptionPane.showMessageDialog(null, "검색 결과가 없습니다.");
			}
		});
		button_search.setBounds(609, 520, 91, 23);
		frame.getContentPane().add(button_search);

		// 이전 버튼
		JButton button_Back = new JButton("이전");
		button_Back.setBounds(939, 23, 85, 23);
		button_Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame mainFrame = new MainFrame();
				closeSetUserForOrderFrame();
				mainFrame.openMainFrame();
			}
		});
		frame.getContentPane().add(button_Back);

		// 이름으로 라디오버튼
		radio_lastName = new JRadioButton("\uC774\uB984\uC73C\uB85C");
		radio_lastName.setSelected(true);
		radio_lastName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				radio_lastName.setSelected(true);
				radio_roomNumber.setSelected(false);
				type = "이름";
			}
		});
		radio_lastName.setBounds(382, 520, 73, 23);
		frame.getContentPane().add(radio_lastName);

		// 객실번호로 라디오버튼
		radio_roomNumber = new JRadioButton("\uAC1D\uC2E4\uBC88\uD638\uB85C");
		radio_roomNumber.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				radio_lastName.setSelected(false);
				radio_roomNumber.setSelected(true);
				type = "객실번호";
			}
		});
		radio_roomNumber.setBounds(382, 545, 85, 23);
		frame.getContentPane().add(radio_roomNumber);

		/*
		 * 테이블 관련
		 */
		defaultTableModel = new DefaultTableModel(null, header);
		textField_search = new JTextField();
		textField_search.setColumns(10);
		textField_search.setBounds(475, 521, 122, 22);
		frame.getContentPane().add(textField_search);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 64, 1004, 450);
		frame.getContentPane().add(scrollPane);

		table = new JTable(defaultTableModel);
		table.setForeground(Color.BLACK);
		table.setBackground(Color.WHITE);

		scrollPane.setViewportView(table);
		table.getTableHeader().setReorderingAllowed(false);// 이동 불가하게 설정
		table.setAutoCreateRowSorter(true); // 정렬가능

		// 버튼 클릭되면
		table.addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				if (table.getSelectedColumn() == 0) {
					if (((String) table.getValueAt(table.getSelectedRow(), 7)).equals("체크인") == false) // 현재상태가 체크인아니면
						JOptionPane.showMessageDialog(null, "아직 체크인되지 않았거나 이미 체크아웃된 예약이므로, 레스토랑 주문이 불가합니다.");
					else {
						confirmationNumber = (String) table.getValueAt(table.getSelectedRow(), 9);

						OrderFrame orderFrame = new OrderFrame();
						closeSetUserForOrderFrame();
						orderFrame.setConfirmation(confirmationNumber);
						orderFrame.openOrderFrame();
					}
				}
			}
		});

		// 버튼
		table.getColumn("선택").setCellRenderer(new ButtonRenderer());
		table.getColumn("선택").setCellEditor(new ButtonEditor(new JCheckBox()));

		// 크기 조정
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// 배경 이미지
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(".\\img\\HTsetuser.PNG"));
		lblNewLabel.setBounds(0, 0, 1036, 623);
		frame.getContentPane().add(lblNewLabel);

		TableColumn column = table.getColumnModel().getColumn(0);
		column.setPreferredWidth(60);
		column = table.getColumnModel().getColumn(1);
		column.setPreferredWidth(30);
		column = table.getColumnModel().getColumn(2);
		column.setPreferredWidth(30);
		column = table.getColumnModel().getColumn(3);
		column.setPreferredWidth(30);
		column = table.getColumnModel().getColumn(5);
		column.setPreferredWidth(80);
		column = table.getColumnModel().getColumn(8);
		column.setPreferredWidth(150);
		column = table.getColumnModel().getColumn(13);
		column.setPreferredWidth(110);
		column = table.getColumnModel().getColumn(14);
		column.setPreferredWidth(150);
		column = table.getColumnModel().getColumn(15);
		column.setPreferredWidth(150);
	}

	// 구성요소 초기화
	public void setInfo() {
		if (textField_search.getText().length() == 0)
			guess = null;
		else
			guess = textField_search.getText();
	}

	public void loadDB() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			defaultTableModel.setNumRows(0); // 테이블 초기화

			// 검색
			String sql;
			if (guess == null)// guess가 null이면
				sql = "select * from 고객정보;";
			else
				sql = "select * from 고객정보 where " + type + " = '" + guess + "';";
			rs = stmt.executeQuery(sql);

			if (rs.next() == false) // 검색결과 없으면
				isNull = true;
			else { // 검색결과 있으면
				rs = stmt.executeQuery(sql);
				isNull = false;

				Object data[] = new Object[header.length];
				data[0] = "선택";
				while (rs.next()) {
					for (int i = 1; i < header.length; i++)
						data[i] = rs.getString(i);
					defaultTableModel.addRow(data);
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

	public void openSetUserForOrderFrame() {
		loadDB();// 오픈과 동시에 전체 출력;

		this.isLogIn = MainFrame.getIsLogIn();
		this.userID = MainFrame.getUserID();
		this.position = MainFrame.getPosition();

		if (isLogIn) // 모든 화면에서 뜨게 할 로그인상태 라벨 생성
			lable_logInState.setText("로그인 user : " + userID); // 변경하여 표시
		frame.setVisible(true);
	}

	public void closeSetUserForOrderFrame() {
		frame.setVisible(false);
	}
}