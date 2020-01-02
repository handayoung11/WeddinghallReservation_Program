package 대전;

import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Admin extends BaseFrame{
	DefaultTableModel model=model("이름,주소,예식형태,식사종류,수용인원,홀사용료,asdf".split(","));
	JTable table=table(model);
	JTextField txt=new JTextField(20);
	String sql="";
	PreparedStatement pst;
	
	public Admin() {
		super("관리자", 600, 300);
		JPanel np=new JPanel();
		add(np, "North");
		add(new JScrollPane(table));
		table.getColumnModel().getColumn(6).setMinWidth(0);
		table.getColumnModel().getColumn(6).setMaxWidth(0);
		try {
			pst=con.prepareStatement("select weddinghall_name, weddinghall_address, weddinghall_index, weddinghall_index, weddinghall_accommodate, weddinghall_fee, weddinghall_index from weddinghall where "
					+ "weddinghall_name like ?");
		} catch (SQLException e2) {
			e2.printStackTrace();
		}		
		np.add(txt);
		np.add(btn("검색", e->{
			try {
				pst.setString(1, "%"+txt.getText()+"%");
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			search();
		}));
		np.add(btn("등록", e->new Register().addWindowListener(new Before(this))));
		np.add(btn("닫기", e->dispose()));
		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent e) {
				new Modify(table.getValueAt(table.getSelectedRow(), 6)+"").addWindowListener(new WindowAdapter() {
					public void windowClosed(java.awt.event.WindowEvent e) {
						search();
					};
				});
			};
		});
		setVisible(true);
	}
	
	void search() {
		try {
			addRow(model, pst.executeQuery());
			for(int j=model.getRowCount()-1; j>=0; j--) {
				String wed=String.join(",", getType(model.getValueAt(j, 2)+"", "weddingtype"));
				String me=String.join(",", getType(model.getValueAt(j, 2)+"", "mealtype"));
				model.setValueAt(wed, j, 2);
				model.setValueAt(me, j, 3);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	void def() {
		// TODO Auto-generated method stub
		
	}

}
