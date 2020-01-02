package ����;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.sql.SQLException;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Search extends BaseFrame{
	JComboBox combo[]= {
			combo("��ü,�����,���ı�,������,�߱�,������,���ʱ�,��������,���α�".split(",")),
			combo(wedding.toArray(String[]::new)),
			combo(meal.toArray(String[]::new))
	};
	JTextField txt[]= {
			new JTextField(),
			new JTextField(),
			new JTextField(),
			new JTextField()
	};
	DefaultTableModel model=model("�̸�,�ּ�,��������,�Ļ�����,�����ο�,Ȧ����,asdf".split(","));
	JTable table=table(model);
	
	public Search() {
		super("�˻�", 900, 400);
		JPanel sp=new JPanel(), cp=new JPanel(new GridLayout(0, 1));
		String cap[]="����,��������,�Ļ�����,�����ο�,Ȧ����".split(",");
		for(int i=0; i<4; i++) size(txt[i], 70, 25);
		for(int i=0; i<cap.length; i++) {
			var tmp=new JPanel();
			tmp.add(size(new JLabel(cap[i], JLabel.RIGHT), 60, 25));
			if(i<3) tmp.add(size(combo[i], 140, 25));
			else {
				int idx=(i==3?0:2);
				tmp.add(txt[idx]);
				tmp.add(new JLabel("~"));
				tmp.add(txt[idx+1]);
			}
			cp.add(tmp);
		}
		
		sp.add(btn("�ʱ�ȭ", e->setDef()));
		sp.add(btn("�˻�", e->search()));
		
		cp.add(sp, "South");
		cp.setBorder(new EmptyBorder(80, 0, 80, 0));
		size(cp, 230, 30);
		
		table.getColumnModel().getColumn(6).setMinWidth(0);
		table.getColumnModel().getColumn(6).setMaxWidth(0);
		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent e) {
				new Reservation(table).addWindowListener(new Before(Search.this));
			};
		});
		add(new JScrollPane(table));
		add(cp, "West");
		setVisible(true);
	}

	private void search() {
		try {
			var pst=con.prepareStatement("select weddinghall_name, weddinghall_address, weddinghall_index, weddinghall_index, weddinghall_accommodate, concat(format(weddinghall_fee, 0), '��'), weddinghall_index from weddinghall where "
					+ "weddinghall_accommodate between ? and ? and weddinghall_fee between ? and ? and weddinghall_address like ?");
			int val[]=new int[4];
			for(int i=0; i<4; i++) {
				if(txt[i].getText().matches(pattern)) {
					err_msg("�����ο��� Ȧ����� ���ڸ� �Է� �����մϴ�");
					return;
				}
				val[i]=toint(txt[i].getText());
				if(i%2==1 && txt[i].getText().equals("")) val[i]=10000000;
				pst.setInt(i+1, val[i]);
			}
			if(val[0]>val[1] || val[2]>val[3]) {
				err_msg("���ڸ� �ùٸ��� �Է����ּ���.");
				return;
			}
			String sql[]= {"", "", ""};
			for(int i=0; i<3; i++) if(combo[i].getSelectedIndex()!=0) sql[i]=combo[i].getSelectedItem()+"";
			pst.setString(5, "%"+sql[0]+"%");
			addRow(model, pst.executeQuery());
			for(int j=model.getRowCount()-1; j>=0; j--) {
				String wed=String.join(",", getType(model.getValueAt(j, 2)+"", "weddingtype"));
				String me=String.join(",", getType(model.getValueAt(j, 2)+"", "mealtype"));
				if(!wed.contains(sql[1]) || !me.contains(sql[2])) {
					model.removeRow(j);
					continue;
				}
				model.setValueAt(wed, j, 2);
				model.setValueAt(me, j, 3);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void setDef() {
		for(int i=0; i<3; i++) combo[i].setSelectedIndex(0);
		for(int i=0; i<4; i++) txt[i].setText("");
		model.setRowCount(0);
	}

	@Override
	void def() {
		// TODO Auto-generated method stub
		
	}

}
