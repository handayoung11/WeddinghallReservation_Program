package 대전;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ReservationChk extends BaseFrame{
	JCheckBox chk[]= {
			new JCheckBox("앨범제작"),
			new JCheckBox("청첩장"),
			new JCheckBox("드레스")
	};
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
	int row, idx;
	JPanel cal[]= {
			new JPanel(new BorderLayout()),
			new JPanel(new BorderLayout())
	};
	JButton jB[]=new JButton[5];
	String cap[]="청첩장 수정,예약 변경,예약 취소,결제하기,닫기".split(",");
	
	public ReservationChk() {
		super("예약 확인", 650, 400);
		JPanel cp=new JPanel(), sp=new JPanel(new GridLayout(1, 0, 20, 0));
		sp.setBorder(new EmptyBorder(0, 0, 10, 0));
		design(cp);
		add(cp);
		for(int i=0; i<5; i++) {
			sp.add(jB[i]=btn(cap[i], e->click(e)));
		}
		add(sp, "South");
	}
	
	void click(ActionEvent e) {
		if(e.getActionCommand()==cap[0]) new Select(txt[1].getText(), date).addWindowListener(new Before(this));
		if(e.getActionCommand()==cap[1]) {
			if(txt[5].getText().matches(pattern) || txt[5].getText().equals("") || toint(txt[5].getText())>toint(txt[2].getText())) {
				err_msg("인원수를 바르게 입력해주세요.");
				return;
			}
			int val[]= {0, 0, 0}, wt=0, mt=0;
			for(int i=0; i<3; i++) if(chk[i].isSelected()) val[i]=1;
			for(var we:wedding) {
				if(we.equals(com[0].getSelectedItem())) break;
				wt++;
			}
			for(var we:meal) {
				if(we.equals(com[1].getSelectedItem())) break;
				mt++;
			}
			execute("update reservation set reservation_personnel="+txt[5].getText()+", album="+val[0]+", letter="+val[1]+", dress="+val[2]+", weddingtype_index="+wt+", mealtype_index="+mt+" where reservation_code="+yes);
			msg("변경되었습니다.");
		}if(e.getActionCommand()==cap[2]) {
			execute("delete from reservation where reservation_code="+yes);
			msg("취소되었습니다.");
			dispose();
		}if(e.getActionCommand()==cap[3]) {
			Object info[]=new Object[9];
			info[0]=txt[0].getText();
			info[1]=txt[3].getText();
			info[2]=txt[4].getText();
			try {
				var rs=pre.executeQuery();
				int val[]= {100000, 150000, 200000};
				if(rs.next()) {
					info[3]=rs.getString("reservation_personnel");
					for(int i=4; i<7; i++) {
						info[i]=(rs.getInt(i+2)==1?"신청":"신청안함");
						val[i-4]=rs.getInt(i+2)*val[i-4];
					}
				}
				info[7]=toint(txt[2].getText())*toint(info[3]+"")+Arrays.stream(val).sum();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			info[8]=yes;
			new Pay(info).addWindowListener(new Before(this));
		}
		if(e.getActionCommand()==cap[4]) dispose();
	}
	PreparedStatement pre;
	
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
		com[0].setSelectedItem(wed);
		com[1].setSelectedItem(mea);
		try {
			var rs=stmt.executeQuery("select * from mealtype where mealtype_name like '%"+com[1].getSelectedItem()+"%'");
			rs.next();
			txt[4].setText(rs.getInt(3)+"");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void design(JPanel cen) {
		String cap[]="웨딩홀명,주소,수용인원,홀사용료,예식형태,식사종류,식사비용,인원수".split(",");
		JPanel cp=new JPanel(new GridLayout(0, 1));
		cen.add(cp);
		for(var t:txt) {
			size(t, 530, 25);
			t.setEnabled(false);
		}
		for(int i=0; i<cap.length; i++) {
			JPanel tmp=new JPanel();
			tmp.add(size(new JLabel(cap[i]), 60, 25));
			if(i<=3) tmp.add(txt[i]);
			else if(i<=5) tmp.add(size(com[i-4], 500, 25));
			else tmp.add(txt[i-2]);
			if(i==7) for(var c:chk) tmp.add(c);
			cp.add(tmp);
		}
		size(txt[5], 80, 25);
		txt[5].setEnabled(true);
	}
	
	String yes, wed, mea, date;
	
	boolean chk() {
		yes=JOptionPane.showInputDialog(null, "예약번호를 입력하세요.", "입력", JOptionPane.QUESTION_MESSAGE);
		if(yes==null) return false;
		try {
			pre=con.prepareStatement("select * from reservation where reservation_code=?");
			pre.setString(1, yes);
			var rs=pre.executeQuery();
			if(rs.next()) {
				date=rs.getString("date");
				idx=rs.getInt(2);
				if(rs.getInt("pay")==1) for(int j=1; j<=3; j++) jB[j].setEnabled(false);
				for(int j=6; j<=8; j++) if(rs.getInt(j)==1) chk[j-6].setSelected(true);
				wed=wedding.get(rs.getInt(4));
				mea=meal.get(rs.getInt(5));
				txt[5].setText(rs.getString(3));
				setInfo(idx+"");
				setVisible(true);
				return true;
			}
		} catch (SQLException e) {
//			e.printStackTrace();
		}
		err_msg("예약번호가 일치하지 않습니다.");
		return false;
	}
	
	@Override
	void def() {
		// TODO Auto-generated method stub
		
	}

}
