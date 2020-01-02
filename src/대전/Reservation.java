package 대전;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.LineBorder;

public class Reservation extends BaseFrame{
	JTable table;
	JLabel img[]=new JLabel[6];
	JTextField txt[]= {
			new JTextField(),
			new JTextField(),
			new JTextField(),
			new JTextField(),
			new JTextField(),
			new JTextField()
	};
	JComboBox com[]= {
			new JComboBox(),
			new JComboBox()
	};
	LocalDate date=LocalDate.now();
	int row, idx;
	JPanel cal[]= {
			new JPanel(new BorderLayout()),
			new JPanel(new BorderLayout())
	};
	ArrayList<JToggleButton> toggles=new ArrayList<>();
	
	public Reservation(JTable t) {
		super("예약", 800, 900);
		table=t;
		row=t.getSelectedRow();
		JPanel cen=new JPanel(new BorderLayout()), np=new JPanel(new BorderLayout()), n_e=new JPanel(new FlowLayout(0, 0, 0)), sp=new JPanel(new BorderLayout()), sp_n=new JPanel(new GridLayout(1, 0)), sp_c=new JPanel(new GridLayout(1, 0));
		
		for(int i=0; i<6; i++) {
			img[i]=size(img[i]=new JLabel(), 100, 55);
			int cnt=i==0?1:i;
			if(i==0) np.add(img[i]);
			else {
				n_e.add(img[i]);
				img[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
				final int idx=i;
				img[i].addMouseListener(new MouseAdapter() {
					public void mousePressed(java.awt.event.MouseEvent e) {
						img[0].setIcon(img(hotel+txt[0].getText()+"/"+txt[0].getText()+" "+idx+".jpg", 590, 55*5));
					};
				});
			}
			img[i].setBorder(new LineBorder(Color.BLACK));
		}
		np.add(n_e, "East");
		size(n_e, 100, 55*5);
		
		sp.add(sp_n, "North");
		sp.add(sp_c);
		sp_c.add(cal[0]);
		sp_c.add(cal[1]);
		sp_n.add(btn("◁", e->plusMonth(-1)));
		sp_n.add(btn("▷", e->plusMonth(1)));
		plusMonth(0);
		cal[0].setBorder(new LineBorder(Color.black));
		cal[1].setBorder(new LineBorder(Color.black));
		
		cen.add(np, "North");
		cen.add(sp, "South");
		design(cen);
		
		setRow(0);
		add(btn("◀", e->setRow(-1)), "West");
		add(btn("▶", e->setRow(1)), "East");
		add(cen);
		add(btn("예약하기", e->reservation()), "South");

		setVisible(true);
	}
	
	void setInfo(String id) {
		try {
			pst.setInt(1, idx=toint(id));
			var rs=pst.executeQuery();
			rs.next();
			for(int i=0; i<txt.length-2; i++) txt[i].setText(rs.getString(i+2));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		com[0]=combo(com[0], getType(id, "weddingtype"));
		com[1]=combo(com[1], getType(id, "mealtype"));
		
		try {
			var rs=stmt.executeQuery("select * from mealtype where mealtype_name like '%"+com[1].getSelectedItem()+"%'");
			rs.next();
			txt[4].setText(rs.getInt(3)+"");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for(int i=1; i<6; i++) {
			img[i].setIcon(img(hotel+txt[0].getText()+"/"+txt[0].getText()+" "+i+".jpg", 100, 55));
			if(i==1) img[0].setIcon(img(hotel+txt[0].getText()+"/"+txt[0].getText()+" "+i+".jpg", 590, 55*5));
		}
	}
	
	void drawCal(int idx) {
		cal[idx].removeAll();
		LocalDate local=date.plusMonths(idx);
		JPanel cp=new JPanel(new GridLayout(0, 7));
		cal[idx].add(new JLabel(local.format(DateTimeFormatter.ofPattern("yyyy년 M월")), 0), "North");
		cal[idx].add(cp);
		String l[]= "일,월,화,수,목,금,토".split(",");
		for(var la : l) cp.add(new JLabel(la, 0));
		int offset=local.with(TemporalAdjusters.firstDayOfMonth()).getDayOfWeek().getValue(), last=local.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth(), day=1;
		if(offset==7) offset=0;
		for(int i=0; i<42; i++) {
			if(day>last || i<offset) {
				cp.add(new JLabel());
				continue;
			}
			JToggleButton tog=new JToggleButton(day+"");
			toggles.add(tog);
			tog.addActionListener(it -> {
				int cnt=0;
				JToggleButton s=(JToggleButton)it.getSource();
				LocalDate sec=local.withDayOfMonth(toint(s.getText()));
				if(!s.isSelected()) {
					se=null;
					return;
				}
				
				for(var item:toggles) if(item.isSelected()) cnt++;
				if(cnt>1) {
					s.setSelected(false);
					return;
				}
				if(sec.isBefore(LocalDate.now()) || sec.isEqual(LocalDate.now())) {
					err_msg("이미 지난 날짜와 당일 예약은 불가능합니다.");
					s.setSelected(false);
					return;
				}
				se=sec;
			});
			try {
				var rs=stmt.executeQuery("select * from reservation where date='"+local.withDayOfMonth(day)+"'");
				if(rs.next()) tog.setEnabled(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			cp.add(tog);
			day++;
		}
		repaint();
		revalidate();
	}
	
	void setRow(int p) {
		row+=p;
		if(row==-1) row=table.getRowCount()-1;
		if(row==table.getRowCount()) row=0;
		setInfo(table.getValueAt(row, 6)+"");
	}
	
	void plusMonth(int idx) {
		se=null;
		date=date.plusMonths(idx);
		toggles.clear();
		drawCal(0);
		drawCal(1);
	}
	LocalDate se=null;
	
	
void reservation() {
		if(txt[5].getText().equals("") || txt[5].getText().matches(pattern) || toint(txt[5].getText())>toint(txt[2].getText())) {
			err_msg("인원수를 바르게 입력해주세요");
			return;
		}
		if(se==null) {
			err_msg("날짜를 선택해주세요");
			return;
		}
		String num=se.format(DateTimeFormatter.ofPattern("yyMMdd"))+new DecimalFormat("00").format(idx);
		int wt=0, mt=0;
		for(var wed:wedding) {
			if(com[0].getSelectedItem().toString().equals(wed)) break;
			wt++;
		}
		for(var wed:meal) {
			if(com[1].getSelectedItem().toString().equals(wed)) break;
			mt++;
		}
		try {
			stmt.execute("insert into reservation values("+num+", "+idx+", "+txt[5].getText()+", "+wt+", "+mt+", 0, 0, 0, '"+se+"', 0)");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int yes=JOptionPane.showOptionDialog(null, "예약이 완료되었습니다.\n예약번호는 "+num+" 입니다.", "예약확인", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, "클립보드에 복사,확인".split(","), null);
		if(yes==0) {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(num), null);
			msg("복사가 완료되었습니다.");
		}
		chk=true;
		dispose();
	}
	
	void design(JPanel cen) {
		String cap[]="웨딩홀명,주소,수용인원,홀사용료,예식형태,식사종류,식사비용,인원수".split(",");
		JPanel cp=new JPanel(new GridLayout(0, 1));
		cen.add(cp);
		for(var t:txt) {
			size(t, 550, 25);
			t.setEnabled(false);
		}
		for(int i=0; i<cap.length; i++) {
			JPanel tmp=new JPanel();
			tmp.add(size(new JLabel(cap[i]), 60, 25));
			if(i<=3) tmp.add(txt[i]);
			else if(i<=5) tmp.add(size(com[i-4], 530, 25));
			else tmp.add(txt[i-2]);
			cp.add(tmp);
		}
		txt[5].setEnabled(true);
	}
	
	@Override
	void def() {
		// TODO Auto-generated method stub
		
	}

	
}
