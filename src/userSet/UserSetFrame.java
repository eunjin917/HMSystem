package userSet;

import main.*;
import helper.*;
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
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/*
 * 사용자관리 화면
 */

public class UserSetFrame {

	private JFrame frame;

	// 프레임 구성 요소들
	private JLabel lable_logInState;
	private JTextField textField_ID;
	private JTextField textField_PW;
	private JRadioButton radio_CSR;
	private JRadioButton radio_manager;
	private JTable table;
	private JScrollPane scrollPane;

	// 모든 화면에서 뜨게할 라벨 구성 요소들 (창이 바뀌어도 유지)
	private boolean isLogIn;
	private String userID;
	private String position;

	// JTable에서 필요함
	private DefaultTableModel defaultTableModel;
	private String[] header = { "ID", "PW", "직급", "선택" };
	private String[] typeArray = { "ID", "PW", "직급" };

	// setInfo() 필요 요소들
	private String ID = null;
	private String PW = null;
	private String pos = "CSR"; // 기본값으로넣기

	// 저장해둘 것
	private String thisID = null;
	private String item = null;
	private String origin = null;

	// 확인 역할
	private boolean alreadyID = false; // 이미 ID 있는지
	private boolean isAble = false; // DB에 바꿔도 되는지
	private int count = 0; // 클릭 오류 방지
	private boolean checkIsEmpty = false; // 체크박스 1도 없으면

	/**
	 * Create the application.
	 */
	public UserSetFrame() {
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

		JLabel lblNewLabel_1 = new JLabel("\uBAA9\uB85D");
		lblNewLabel_1.setBounds(261, 134, 33, 22);
		frame.getContentPane().add(lblNewLabel_1);

		// 로그인상태 라벨
		lable_logInState = new JLabel(" \uB85C\uADF8\uC544\uC6C3 \uC0C1\uD0DC");
		lable_logInState.setForeground(Color.RED);
		lable_logInState.setBounds(12, 561, 174, 42);
		frame.getContentPane().add(lable_logInState);

		// 추가 버튼
		JButton button_create = new JButton("\uCD94\uAC00");
		button_create.setBounds(711, 472, 85, 57);
		button_create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setInfo();
				checkDB(ID); // 해당 ID 이미 있는지 확인

				if (ID.length() == 0 || PW.length() == 0)
					JOptionPane.showMessageDialog(null, "내용을 모두 입력하세요.");
				else if (ID.length() > 20 || PW.length() > 20)
					JOptionPane.showMessageDialog(null, "ID나 PW가 너무 깁니다. 20자 이하로 다시 입력하세요.");
				else if (alreadyID == true)
					JOptionPane.showMessageDialog(null, "이미 존재하는 ID입니다. 다시 입력하세요.");
				else {
					createDB();// DB에 추가
					JOptionPane.showMessageDialog(null, "추가되었습니다.");
					defaultTableModel.addRow(new Object[] { ID, PW, pos, (boolean) false }); // 화면에 추가
				}
			}
		});
		frame.getContentPane().add(button_create);

		// 삭제 버튼

		JButton button_delete = new JButton("\uC0AD\uC81C");
		button_delete.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int i;
				for (i = 0; i < table.getRowCount(); i++) {
					if ((boolean) table.getValueAt(i, 3) == true) {
						checkIsEmpty = false;
						thisID = (String) table.getValueAt(i, 0);
						deleteDB();
						defaultTableModel.removeRow(i--);
					} else
						checkIsEmpty = true;
				}

				if (checkIsEmpty == true)
					JOptionPane.showMessageDialog(null, "삭제할 내용이 없습니다. 다시 선택하세요.");
				else {
					JOptionPane.showMessageDialog(null, "삭제되었습니다.");
				}
			}
		});
		button_delete.setBounds(711, 373, 85, 23);
		frame.getContentPane().add(button_delete);

		// 이전 버튼
		JButton button_Back = new JButton("이전");
		button_Back.setBounds(928, 22, 85, 23);
		button_Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame mainFrame = new MainFrame();
				closeUserSetFrame();
				mainFrame.openMainFrame();
			}
		});
		frame.getContentPane().add(button_Back);

		/*
		 * 테이블 관련
		 */
		defaultTableModel = new DefaultTableModel(null, header);
		table = new JTable(defaultTableModel);
		scrollPane = new JScrollPane();
		scrollPane.setBounds(296, 134, 403, 262);
		frame.getContentPane().add(scrollPane);

		// 체크박스
		table.getColumnModel().getColumn(3).setCellEditor(new CheckBoxCellEditor());
		table.getColumnModel().getColumn(3).setCellRenderer(new CWCheckBoxRenderer());

		table.getTableHeader().setReorderingAllowed(false);// 이동 불가하게 설정
		table.setAutoCreateRowSorter(true);// 헤더를 클릭하면 행을 자동정렬

		// 클릭하면
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1) { // 클릭
					if (table.getSelectedRow() != -1 && table.getSelectedColumn() != 3) {
						origin = (String) table.getValueAt(table.getSelectedRow(), table.getSelectedColumn()); // 선택된거
						// 원래꺼
						thisID = (String) table.getValueAt(table.getSelectedRow(), 0); // 선택된거 해당하는 ID 받아오기
						count = 1;
					}
				}
			}
		});

		// 수정 기능
		table.addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) { // 수정되면
				if (count == 2) {
					if (table.getSelectedRow() != -1 && table.getSelectedColumn() != 3) {
						item = (String) table.getValueAt(table.getSelectedRow(), table.getSelectedColumn()); // 선택된거 바꿀거
						// 초기화

						if (table.getSelectedColumn() == 0) // ID를 바꾸는거면
							checkDB(item);// 바꾼 메뉴가 겹치는지 확인

						if (item.length() == 0) {
							JOptionPane.showMessageDialog(null, "내용을 모두 입력하세요.");
							isAble = false;
						} else if ((table.getSelectedColumn() == 0 || table.getSelectedColumn() == 1)
								&& item.length() > 20) {
							JOptionPane.showMessageDialog(null, "ID나 PW가 너무 깁니다. 20자 이하로 다시 입력하세요.");
							isAble = false;
						} else if (table.getSelectedColumn() == 2 && !(item.equals("CSR") || item.equals("매니저"))) {
							JOptionPane.showMessageDialog(null, "직급은 CSR 또는 매니저로만 설정할 수 있습니다.");
							isAble = false;
						} else if (alreadyID == true) {
							JOptionPane.showMessageDialog(null, "이미 존재하는 ID입니다. 다시 입력하세요.");
							isAble = false;
						} else { // ID 바꾸는거 아니면
							modifyDB();// DB에 수정
							isAble = true;
						}

						if (isAble == false)
							table.setValueAt(origin, table.getSelectedRow(), table.getSelectedColumn());
					}
					count--;
				} else if (count == 1)
					count++;
			}
		});
		scrollPane.setViewportView(table);

		// CSR 라디오버튼
		radio_CSR = new JRadioButton("CSR");
		radio_CSR.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				radio_CSR.setSelected(true);
				radio_manager.setSelected(false);
				pos = "CSR"; // CSR 직위
			}
		});
		radio_CSR.setSelected(true);
		radio_CSR.setBounds(479, 472, 51, 23);
		frame.getContentPane().add(radio_CSR);

		// 매니저 라디오버튼
		radio_manager = new JRadioButton("\uB9E4\uB2C8\uC800");
		radio_manager.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				radio_CSR.setSelected(false);
				radio_manager.setSelected(true);
				pos = "매니저"; // 매니저 직위
			}
		});
		radio_manager.setBounds(479, 506, 61, 23);
		frame.getContentPane().add(radio_manager);

		/*
		 * 여기부터 구성 요소들
		 */
		textField_ID = new JTextField();
		textField_ID.setBounds(583, 473, 116, 21);
		textField_ID.setColumns(10);
		frame.getContentPane().add(textField_ID);

		textField_PW = new JTextField();
		textField_PW.setBounds(583, 507, 116, 21);
		textField_PW.setColumns(10);
		frame.getContentPane().add(textField_PW);

		JLabel label_ID = new JLabel("ID");
		label_ID.setBackground(Color.WHITE);
		label_ID.setFont(new Font("굴림", Font.BOLD, 15));
		label_ID.setForeground(Color.BLACK);
		label_ID.setBounds(548, 476, 23, 15);
		frame.getContentPane().add(label_ID);

		JLabel label_PW = new JLabel("PW");
		label_PW.setForeground(Color.BLACK);
		label_PW.setFont(new Font("굴림", Font.BOLD, 15));
		label_PW.setBounds(548, 510, 23, 15);
		frame.getContentPane().add(label_PW);

		// 배경 이미지
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(".\\img\\HTuserset.PNG"));
		lblNewLabel.setBounds(0, 0, 1036, 641);
		frame.getContentPane().add(lblNewLabel);
	}

	// 구성요소 초기화
	public void setInfo() {
		ID = textField_ID.getText();
		PW = textField_PW.getText();
	}

	public void loadDB() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		MainFrame mainFrame = new MainFrame();

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(mainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			// 검색
			String sql = "select * from 로그인 where 직급 != '최종사용자';";
			rs = stmt.executeQuery(sql);

			Object data[] = new Object[4];
			while (rs.next()) {
				for (int i = 0; i < 3; i++)
					data[i] = rs.getString(i + 1);
				data[3] = new Boolean(false);
				defaultTableModel.addRow(data);
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

	public void checkDB(String checkID) {
		for (int i = 0; i < table.getRowCount(); i++) {
			if (table.getValueAt(i, 0).equals(checkID) && !(i == table.getSelectedRow())) {
				alreadyID = true;
				break;
			} else
				alreadyID = false;
		}

		if (checkID.equals("master"))
			alreadyID = true;
	}

	public void modifyDB() {
		Connection conn = null;
		Statement stmt = null;

		MainFrame mainFrame = new MainFrame();

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(mainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			String sql = "update 로그인 set " + typeArray[table.getSelectedColumn()] + " = '" + item + "' where ID = '"
					+ thisID + "' limit 1;";
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
		} // 예외처리 끝
	}

	public void createDB() {
		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			String sql = "insert into 로그인 values('" + ID + "', '" + PW + "', '" + pos + "');";
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
		} // 예외처리 끝
	}

	public void deleteDB() {
		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			String sql = "delete from 로그인 where ID = '" + thisID + "' limit 1;";
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
		} // 예외처리 끝
	}

	public void openUserSetFrame() {
		loadDB(); // open과 동시에 리스트 띄우기

		this.isLogIn = MainFrame.getIsLogIn();
		this.userID = MainFrame.getUserID();
		this.position = MainFrame.getPosition();

		if (isLogIn) // 모든 화면에서 뜨게 할 로그인상태 라벨 생성
			lable_logInState.setText("로그인 user : " + userID); // 변경하여 표시
		frame.setVisible(true);
	}

	public void closeUserSetFrame() {
		frame.setVisible(false);
	}
}