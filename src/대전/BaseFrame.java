package 대전;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public abstract class BaseFrame extends JFrame{
	static Statement stmt;
	static Connection con;
	static final String path="./지급자료/", hotel=path+"호텔이미지/", pattern=".*\\D.*";
	static final ArrayList<String> meal=new ArrayList<>(), wedding=new ArrayList<>();
	static PreparedStatement pst;
	static boolean chk=false;
	
	abstract void def();
	
	static int toint(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	class Before extends WindowAdapter{
		BaseFrame bef;
		Before(BaseFrame b){
			b.setVisible(false);
			bef=b;
		}
		
		public void windowClosed(java.awt.event.WindowEvent e) {
			bef.def();
			bef.setVisible(true);
			if(chk) bef.dispose();
			chk=false;
		};
	}
	
	static void msg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "MESSAGE", JOptionPane.INFORMATION_MESSAGE);
	}
	
	static void err_msg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "MESSAGE", JOptionPane.ERROR_MESSAGE);
	}
	
	static {
		UIManager.put("OptionPane.yesButtonText", "확인");
		UIManager.put("OptionPane.okButtonText", "확인");
		UIManager.put("OptionPane.cancelButtonText", "취소");
		try {
			con=DriverManager.getConnection("jdbc:mysql://localhost/wedding?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true&allowLoadLocalInfile=true", "user", "1234");
			stmt=con.createStatement();
			pst=con.prepareStatement("select * from weddinghall where weddinghall_index=?");
			var rs=stmt.executeQuery("select * from mealtype");
			meal.add("전체");
			wedding.add("전체");
			while(rs.next()) meal.add(rs.getString(2));
			rs=stmt.executeQuery("select * from weddingtype");
			while(rs.next()) wedding.add(rs.getString(2));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public BaseFrame(String title, int w, int h) {
		super(title);
		setSize(w, h);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(2);
	}
	
	static ImageIcon img(ImageIcon img, int w, int h) {
		return new ImageIcon(img.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}
	
	static DefaultTableModel model(String title[]) {
		DefaultTableModel model=new DefaultTableModel(null, title) {
			public boolean isCellEditable(int row, int column) {
				return false;
			};
		};
		return model;
	}
	
	static JComboBox combo(JComboBox combo, String str[]) {
		combo.setModel(new DefaultComboBoxModel<>(str));
		return combo;
	}
	
	static JComboBox combo(String str[]) {
		JComboBox combo=new JComboBox<>(str);
		return combo;
	}
	
	static String[] getType(String idx, String t) {
		ArrayList<String> arr=new ArrayList<>();
		try {
			var rs=stmt.executeQuery("select "+t+"_name from "+t+" t inner join weddinghall_"+t+" wt on wt."+t+"_index="+"t."+t+"_index where weddinghall_index="+idx+" order by "+t+"_name asc");
			while(rs.next()) arr.add(rs.getString(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return arr.toArray(String[]::new);
	}
	
	void addRow(DefaultTableModel model, String sql) {
		try {
			model.setRowCount(0);
			var rs=stmt.executeQuery(sql);
			Object row[]=new Object[model.getColumnCount()];
			while(rs.next()) {
				for(int i=0; i<model.getColumnCount(); i++) row[i]=rs.getString(i+1);
				model.addRow(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void addRow(DefaultTableModel model, ResultSet rs) {
		try {
			model.setRowCount(0);
			Object row[]=new Object[model.getColumnCount()];
			while(rs.next()) {
				for(int i=0; i<model.getColumnCount(); i++) row[i]=rs.getString(i+1);
				model.addRow(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static JTable table(DefaultTableModel model) {
		JTable table=new JTable(model);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return table;
	}
	
	static ImageIcon img(String path, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}
	
	static JLabel font(JLabel jL, int size) {
		jL.setFont(new Font("맑은 고딕", 1, size));
		return jL;
	}
	
	static <T extends JComponent> T bound(T comp, int x, int y, int w, int h) {
		comp.setBounds(x, y, w, h);
		return comp;
	}
	
	static <T extends JComponent> T size(T comp, int w, int h) {
		comp.setPreferredSize(new Dimension(w, h));
		return comp;
	}
	
	static JLabel font(JLabel jL, int size, Color color) {
		jL.setFont(new Font("맑은 고딕", 1, size));
		jL.setForeground(color);
		return jL;
	}
	
	static JButton btn(String txt, ImageIcon icon, ActionListener a) {
		JButton jB=new JButton(txt, icon);
		jB.addActionListener(a);
		return jB;
	}
	
	static JButton btn(String txt, ActionListener a) {
		JButton jB=new JButton(txt);
		jB.addActionListener(a);
		return jB;
	}
	
	static void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
