package restaurant;

import helper.*;
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
import javax.swing.JCheckBox;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.beans.PropertyChangeEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.table.TableModel;

import com.mysql.cj.xdevapi.Table;

/*
 * 주문 화면
 */

public class OrderFrame {

	private JFrame frame;

	// 프레임 구성 요소들
	private JLabel lable_logInState;
	private JLabel label_sum;
	private JTable table_mealList;
	private JTable table_orderList;
	private JScrollPane scrollPane_mealList;

	// 모든 화면에서 뜨게할 라벨 구성 요소들 (창이 바뀌어도 유지)
	private boolean isLogIn;
	private String userID;
	private String position;

	// JTable에서 필요함
	private DefaultTableModel defaultTableModel_mealList;
	private DefaultTableModel defaultTableModel_orderList;
	private String[] header_mealList = { "메뉴", "가격", "추가" };
	private String[] header_orderList = { "메뉴", "가격", "수량", "삭제" };

	// 저장해둘 것
	private String confirmation = null;
	private String stringData = null; // 해당 확인번호의 레스토랑 data 전체
	private int thisRow = -1; // orderList의 바꿀 열 저장

	// 확인 역할
	private boolean alreadyMenu = false; // 이미 menu 있는지

	/**
	 * Create the application.
	 */
	public OrderFrame() {
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

		JLabel label = new JLabel("\uBA54\uB274");
		label.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
		label.setBounds(67, 155, 57, 23);
		frame.getContentPane().add(label);

		JLabel lblNewLabel_1 = new JLabel("\uCD94\uAC00 \uBAA9\uB85D");
		lblNewLabel_1.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
		lblNewLabel_1.setBounds(541, 152, 74, 23);
		frame.getContentPane().add(lblNewLabel_1);

		// 로그인상태 라벨
		lable_logInState = new JLabel(" \uB85C\uADF8\uC544\uC6C3 \uC0C1\uD0DC");
		lable_logInState.setForeground(Color.RED);
		lable_logInState.setBounds(12, 561, 174, 42);
		frame.getContentPane().add(lable_logInState);

		// 이전 버튼
		JButton button_Back = new JButton("이전");
		button_Back.setBounds(928, 22, 85, 23);
		button_Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SetUserForOrderFrame setUserForOrderFrame = new SetUserForOrderFrame();
				closeOrderFrame();
				setUserForOrderFrame.openSetUserForOrderFrame();
			}
		});
		frame.getContentPane().add(button_Back);

		// 청구 금액 표시
		label_sum = new JLabel("\uCCAD\uAD6C \uAE08\uC561 : 0\uC6D0");
		label_sum.setBounds(823, 460, 121, 48);
		frame.getContentPane().add(label_sum);

		/*
		 * 테이블 관련
		 */
		// mealList
		defaultTableModel_mealList = new DefaultTableModel(null, header_mealList);
		table_mealList = new JTable(defaultTableModel_mealList);
		table_mealList.addMouseListener(new MouseAdapter() {
			@Override
			// 추가 버튼
			public void mouseClicked(MouseEvent e) { // 클릭되면
				if (table_mealList.getSelectedColumn() == 2 && table_mealList.getSelectedRow() != -1) {
					checkDB();
					if (alreadyMenu == true)
						modifyDB();
					else
						createDB();
					calPriceSum();// 가격합계 구하기
				}
			}
		});
		scrollPane_mealList = new JScrollPane();
		scrollPane_mealList.setBounds(67, 182, 403, 262);
		frame.getContentPane().add(scrollPane_mealList);

		// 버튼
		table_mealList.getColumn("추가").setCellRenderer(new ButtonRenderer());
		table_mealList.getColumn("추가").setCellEditor(new ButtonEditor(new JCheckBox()));

		table_mealList.getTableHeader().setReorderingAllowed(false);// 이동 불가하게 설정
		table_mealList.setAutoCreateRowSorter(true);// 헤더를 클릭하면 행을 자동정렬
		scrollPane_mealList.setViewportView(table_mealList);

		// orderList
		defaultTableModel_orderList = new DefaultTableModel(null, header_orderList);
		table_orderList = new JTable(defaultTableModel_orderList);
		// 삭제 버튼
		table_orderList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (table_orderList.getSelectedColumn() == 3 && table_orderList.getSelectedRow() != -1) {
					deleteDB();
					calPriceSum();// 가격합계 구하기
				}
			}
		});
		JScrollPane scrollPane_orderList = new JScrollPane();
		scrollPane_orderList.setBounds(541, 182, 403, 262);
		frame.getContentPane().add(scrollPane_orderList);

		// 버튼
		table_orderList.getColumn("삭제").setCellRenderer(new ButtonRenderer());
		table_orderList.getColumn("삭제").setCellEditor(new ButtonEditor(new JCheckBox()));

		table_orderList.getTableHeader().setReorderingAllowed(false);// 이동 불가하게 설정
		table_orderList.setAutoCreateRowSorter(true);// 헤더를 클릭하면 행을 자동정렬
		scrollPane_orderList.setViewportView(table_orderList);

		// 배경 이미지
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(".\\img\\HTuserset.PNG"));
		lblNewLabel.setBounds(0, 0, 1036, 641);
		frame.getContentPane().add(lblNewLabel);
	}

	public void loadMealDB() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			// mealList 불러오기
			String sql = "select * from 음식메뉴;";
			rs = stmt.executeQuery(sql);

			Object data[] = new Object[3];
			while (rs.next()) {
				for (int i = 0; i < 2; i++)
					data[i] = rs.getString(i + 1);
				data[2] = "추가";
				defaultTableModel_mealList.addRow(data);
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

	public void loadOrderDB() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			// orderList 불러오기
			String sql = "select * from 주문 where 확인번호 = '" + confirmation + "';";
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				stringData = rs.getString(2);

				while (stringData.length() != 0) {
					/*
					 * 데이터 하나 추출하기
					 */

					Object data2[] = new Object[4];

					// 메뉴 추출
					int endIndex = stringData.indexOf("-");
					data2[0] = stringData.substring(0, endIndex);
					stringData = stringData.substring(endIndex + 1); // 추출한거 제외하기

					// 가격 추출
					endIndex = stringData.indexOf("*");
					data2[1] = stringData.substring(0, endIndex);
					stringData = stringData.substring(endIndex + 1); // 추출한거 제외하기

					// 수량 추출
					endIndex = stringData.indexOf("/");
					data2[2] = stringData.substring(0, endIndex);
					stringData = stringData.substring(endIndex + 1); // 추출한거 제외하기

					data2[3] = "삭제";

					// table에 넣기
					defaultTableModel_orderList.addRow(data2);
				}

				stringData = rs.getString(2); // 다시 원래대로 초기화하기
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

	public void calPriceSum() {
		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MainFrame.getURL(), "root", "ej117917");

			stmt = conn.createStatement();
			String useHMS = "use hms";
			stmt.executeUpdate(useHMS);

			int sumPrice = 0;

			// 합계 구하기
			for (int i = 0; i < table_orderList.getRowCount(); i++)
				sumPrice += Integer.parseInt(table_orderList.getValueAt(i, 1).toString());
			label_sum.setText("청구금액 : " + sumPrice + " 원"); // 변경하여 표시

			// 고객정보 table DB에 추가
			String sql = "update 고객정보 set 청구금액 = '" + sumPrice + "' where 확인번호 = '" + confirmation + "' limit 1;";
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

	public void checkDB() {
		for (int i = 0; i < table_orderList.getRowCount(); i++) {
			if (table_orderList.getValueAt(i, 0)
					.equals(table_mealList.getValueAt(table_mealList.getSelectedRow(), 0))) {
				alreadyMenu = true;
				thisRow = i;
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

			// 테이블 수정하기
			int EA = Integer.parseInt((table_orderList.getValueAt(thisRow, 2).toString())); // 원래 개수
			EA++; // 하나 추가
			defaultTableModel_orderList.setValueAt(EA, thisRow, 2);

			int price = Integer.parseInt((table_orderList.getValueAt(thisRow, 1).toString())); // 원래 가격
			price = (price / (EA - 1)) * EA;
			defaultTableModel_orderList.setValueAt(price, thisRow, 1);

			// stringData 새로 리셋
			stringData = "";
			for (int i = 0; i < table_orderList.getRowCount(); i++) {
				stringData += table_orderList.getValueAt(i, 0);
				stringData += "-";
				stringData += table_orderList.getValueAt(i, 1);
				stringData += "*";
				stringData += table_orderList.getValueAt(i, 2);
				stringData += "/";
			}

			// DB에 추가
			String sql = "update 주문 set 레스토랑" + " = '" + stringData + "' where 확인번호 = '" + confirmation + "' limit 1;";
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

			// stringData에 추가하고
			String target = table_mealList.getValueAt(table_mealList.getSelectedRow(), 0) + "-"
					+ table_mealList.getValueAt(table_mealList.getSelectedRow(), 1) + "*1/"; // 추가할 주문내역(+1)
			if (stringData == null)
				stringData = target;
			else
				stringData += target;

			// DB에도 추가
			String sql;
			if (table_orderList.getRowCount() == 0) // 주문내역 없으면 새로 만들기
				sql = "insert into 주문 values('" + confirmation + "', '" + stringData + "');";
			else // 주문내역 하나라도 있으면 변경만
				sql = "update 주문 set 레스토랑" + " = '" + stringData + "' where 확인번호 = '" + confirmation + "' limit 1;";
			stmt.executeUpdate(sql);

			// jtable도 추가
			Object data[] = new Object[] { table_mealList.getValueAt(table_mealList.getSelectedRow(), 0),
					table_mealList.getValueAt(table_mealList.getSelectedRow(), 1), 1, "삭제" };
			defaultTableModel_orderList.addRow(data);

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

			// table에서 삭제하고
			defaultTableModel_orderList.removeRow(table_orderList.getSelectedRow());// 해당 열 삭제

			// stringData 새로 리셋
			stringData = "";
			for (int i = 0; i < table_orderList.getRowCount(); i++) {
				stringData += table_orderList.getValueAt(i, 0);
				stringData += "-";
				stringData += table_orderList.getValueAt(i, 1);
				stringData += "*";
				stringData += table_orderList.getValueAt(i, 2);
				stringData += "/";
			}

			// DB에 추가
			String sql = "update 주문 set 레스토랑" + " = '" + stringData + "' where 확인번호 = '" + confirmation + "' limit 1;";
			stmt.executeUpdate(sql);

			// 만약 stringData가 0이 되면은, DB에서 해당 확인번호 아예 삭제하기
			if (stringData.length() == 0) {
				sql = "delete from 주문 where 확인번호 = '" + confirmation + "' limit 1";
				stmt.executeUpdate(sql);
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
		} // 예외처리 끝
	}

	public void setConfirmation(String cfm) {
		confirmation = cfm;
	}

	public void openOrderFrame() {
		loadMealDB(); // open과 동시에 meal 리스트 띄우기
		loadOrderDB(); // open과 동시에 order 리스트 띄우기
		calPriceSum();// 가격합계 구하고 변경하여 표시

		frame.setVisible(true);

		this.isLogIn = MainFrame.getIsLogIn();
		this.userID = MainFrame.getUserID();
		this.position = MainFrame.getPosition();

		if (isLogIn) // 모든 화면에서 뜨게 할 로그인상태 라벨 생성
			lable_logInState.setText("로그인 user : " + userID); // 변경하여 표시
		frame.setVisible(true);
	}

	public void closeOrderFrame() {
		frame.setVisible(false);
	}
}
