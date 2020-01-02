package 대전;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Pay extends BaseFrame{
	JLabel sign=new JLabel();
	Object info[];
	public Pay(Object info[]) {
		super("결제하기", 350, 650);
		this.info=info;
		setLayout(new BorderLayout(0, 10));
		JPanel np=new JPanel(), cp=new JPanel(new BorderLayout(0, 10)), sp=new JPanel(), c_c=new JPanel(new GridLayout(0, 1)), c_s=new JPanel(new BorderLayout());
		cp.setBorder(new EmptyBorder(0, 10, 0, 10));
		np.add(font(new JLabel("결제 서비스"), 30, Color.white));
		np.setBackground(Color.red);
		
		String cap[]="웨딩홀명,홀사용료,식사비용,인원수,앨범제작,청첩장,드레스,총 금액".split(",");
		for(int i=0; i<cap.length; i++) {
			JPanel tmp=new JPanel(new FlowLayout(0));
			tmp.setPreferredSize(new Dimension(20, 40));
			tmp.add(size(new JLabel(cap[i]), 80, 25));
			tmp.add(new JLabel(info[i]+""));
			if(i%2==0) tmp.setBackground(Color.white);
			tmp.setBorder(new LineBorder(Color.BLACK));
			c_c.add(tmp);
		}
		
		c_s.add(new JLabel("사인란", 0), "North");
		c_s.add(sign);
		sign.setBorder(new LineBorder(Color.BLACK));
		
		sign.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new Sign(sign).addWindowListener(new Before(Pay.this));
			}
		});
		size(sign, 200, 200);
		cp.add(c_c);
		cp.add(c_s, "South");
		
		sp.add(btn("결제", e->pay()));
		sp.add(btn("취소", e->dispose()));
		
		add(cp);
		add(np, "North");
		add(sp, "South");
		setVisible(true);
	}
	
	void pay() {
		try {
			stmt.execute("update reservation set pay=1 where reservation_code="+info[8]);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		msg("결제가 완료되었습니다.");
		chk=true;
		dispose();
	}

	@Override
	void def() {
		// TODO Auto-generated method stub
		
	}

}
