package mealSet;

import main.*;
import helper.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/*
 * 음식메뉴관리 화면
 */

public class MealSetFrame {

	private JFrame frame;

	// 프레임 구성 요소들
	private JLabel lable_logInState;
	private JTextField textField_menu;
	private JTextField textField_price;
	private JTable table;
	private JScrollPane scrollPane;

	// 모든 화면에서 뜨게할 라벨 구성 요소들 (창이 바뀌어도 유지)
	private boolean isLogIn;
	private String userID;
	private String position;

	// JTable에서 필요함
	private DefaultTableModel defaultTableModel;
	private String[] header = { "메뉴", "가격", "선택" };
	private String[] typeArray = { "메뉴", "가격" };

	// setInfo() 필요 요소들
	private String menu = null;
	private String price = null;

	// 저장해둘 것
	private String thisMenu = null;
	private String item = null;
	private String origin = null;

	// 확인 역할
	private boolean alreadyMenu = false; // 이미 메뉴 있는지
	private boolean isAble = false; // DB에 바꿔도 되는지
	private int count = 0; // 클릭 오류 방지
	private boolean checkIsEmpty = false; // 체크박스 1도 없으면

	/**
	 * Create the application.
	 */
	public MealSetFrame() {
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

		JLabel label = new JLabel("\uBA54\uB274 :");
		label.setFont(new Font("굴림", Font.PLAIN, 15));
		label.setBounds(675, 431, 38, 18);
		frame.getContentPane().add(label);

		JLabel lblNewLabel_1 = new JLabel("\uAC00\uACA9 :");
		lblNewLabel_1.setFont(new Font("굴림", Font.PLAIN, 15));
		lblNewLabel_1.setBounds(675, 463, 38, 18);
		frame.getContentPane().add(lblNewLabel_1);

		// 로그인상태 라벨
		lable_logInState = new JLabel(" \uB85C\uADF8\uC544\uC6C3 \uC0C1\uD0DC");
		lable_logInState.setForeground(Color.RED);
		lable_logInState.setBounds(12, 561, 174, 42);
		frame.getContentPane().add(lable_logInState);

		// 추가 버튼
		JButton button_create = new JButton("\uCD94\uAC00");
		button_create.setBounds(843, 431, 85, 50);
		button_create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setInfo();
				checkDB(menu); // 해당 메뉴 이미 있는지 확인

				if (menu.length() == 0 || menu.length() == 0)
					JOptionPane.showMessageDialog(null, "내용을 모두 입력하세요.");
				else if (alreadyMenu == true)
					JOptionPane.showMessageDialog(null, "이미 존재하는 메뉴입니다. 다시 입력하세요.");
				else {
					createDB();// DB에 추가
					JOptionPane.showMessageDialog(null, "추가되었습니다.");
					defaultTableModel.addRow(new Object[] { menu, price, (boolean) false }); // 화면에 추가
				}
			}
		});
		frame.getContentPane().add(button_create);

		// 삭제 버튼
		JButton button_delete = new JButton("\uC0AD\uC81C");
		button_delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < table.getRowCount(); i++)
					if ((boolean) table.getValueAt(i, 2) == true) {
						thisMenu = (String) table.getValueAt(i, 0);
						deleteDB();
						defaultTableModel.removeRow(i--);
						checkIsEmpty = false;
					}

				if (checkIsEmpty == true)
					JOptionPane.showMessageDialog(null, "삭제할 내용이 없습니다. 다시 선택하세요.");
				else {
					JOptionPane.showMessageDialog(null, "삭제되었습니다.");
					checkIsEmpty = true;
				}
			}
		});
		button_delete.setBounds(843, 379, 85, 23);
		frame.getContentPane().add(button_delete);

		// 이전 버튼
		JButton button_Back = new JButton("이전");
		button_Back.setBounds(939, 22, 85, 23);
		button_Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame mainFrame = new MainFrame();
				closeMealSetFrame();
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
		scrollPane.setBounds(221, 104, 610, 298);
		frame.getContentPane().add(scrollPane);

		// 체크박스
		table.getColumnModel().getColumn(2).setCellEditor(new CheckBoxCellEditor());
		table.getColumnModel().getColumn(2).setCellRenderer(new CWCheckBoxRenderer());

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
						thisMenu = (String) table.getValueAt(table.getSelectedRow(), 0); // 선택된거 해당하는 ID 받아오기
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
						} else if (alreadyMenu == true) {
							JOptionPane.showMessageDialog(null, "이미 존재하는 메뉴입니다. 다시 입력하세요.");
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

		/*
		 * 여기부터 구성 요소들
		 */
		textField_menu = new JTextField();
		textField_menu.setBounds(715, 431, 116, 21);
		textField_menu.setColumns(10);
		frame.getContentPane().add(textField_menu);

		textField_price = new JTextField();
		textField_price.setBounds(715, 462, 116, 21);
		textField_price.setColumns(10);
		frame.getContentPane().add(textField_price);
		// 배경이미지
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(".\\img\\HTmealset.PNG"));
		lblNewLabel.setBounds(0, 0, 1036, 622);
		frame.getContentPane().add(lblNewLabel);
	}

	// 구성요소 초기화
	public void setInfo() {
		menu = textField_menu.getText();
		price = textField_price.getText();
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

			// 검색
			String sql = "select * from 음식메뉴;";
			rs = stmt.executeQuery(sql);

			Object data[] = new Object[3];
			while (rs.next()) {
				for (int i = 0; i < 2; i++)
					data[i] = rs.getString(i + 1);
				data[2] = new Boolean(false);
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

	// 음식메뉴 DB 불러오기
	public void checkDB(String checkMenu) {
		for (int i = 0; i < table.getRowCount(); i++) {
			if (table.getValueAt(i, 0).equals(checkMenu) && !(i == table.getSelectedRow())) {
				alreadyMenu = true;
				break;
			} else
				alreadyMenu = false;
		}
	}

	public void modifyDB() {
		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			String sql;

			item = (String) table.getValueAt(table.getSelectedRow(), table.getSelectedColumn());

			sql = "update 음식메뉴 set " + typeArray[table.getSelectedColumn()] + " = '" + item + "' where 메뉴 = '"
					+ thisMenu + "' limit 1;";
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

		MainFrame mainFrame = new MainFrame();

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(mainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			String sql = "insert into 음식메뉴 values('" + menu + "', '" + price + "');";
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

		MainFrame mainFrame = new MainFrame();

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(mainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			String sql = "delete from 음식메뉴 where 메뉴 = '" + thisMenu + "' limit 1;";
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

	public void openMealSetFrame() {
		loadDB(); // open과 동시에 리스트 띄우기

		this.isLogIn = MainFrame.getIsLogIn();
		this.userID = MainFrame.getUserID();
		this.position = MainFrame.getPosition();

		if (isLogIn) // 모든 화면에서 뜨게 할 로그인상태 라벨 생성
			lable_logInState.setText("로그인 user : " + userID); // 변경하여 표시
		frame.setVisible(true);
	}

	public void closeMealSetFrame() {
		frame.setVisible(false);
	}
}