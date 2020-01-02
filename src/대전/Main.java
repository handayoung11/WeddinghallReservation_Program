package 대전;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Main extends BaseFrame{
	JLabel rank=new JLabel("", 0), img[]=new JLabel[5];
	String cap[]="웨딩홀 검색,예약확인,인기 웨딩홀,관리,종료".split(","), name[]=new String[5];
	int w=435;

	public Main() {
		super("메인", 450, 400);
		JPanel np=new JPanel(), cp=new JPanel(null);
		add(np, "North");
		add(cp);
		add(rank, "South");
		for(var c:cap) np.add(btn(c, e->click(e)));
		addWindowListener(new WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent e) {
				new Thread(()->changeImg()).start();
			};
		});
		try {
			var rs=stmt.executeQuery("select weddinghall_name, count(*) as cnt from reservation r inner join weddinghall w on w.weddinghall_index=r.weddinghall_index where pay=1 group by w.weddinghall_index order by cnt desc limit 5");
			int i=0;
			while(rs.next()) {
				name[i]=rs.getString(1);
				cp.add(bound(img[i]=new JLabel(img(hotel+name[i]+"/"+name[i]+" 1.jpg", w, 270)), i*w, 10, w, 270));
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		setDefaultCloseOperation(3);
		setVisible(true);
	}

	void changeImg() {
		int idx=1;
		while(true) {
			rank.setText("예약 "+idx+"위 : "+name[idx-1]);
			try {
				Thread.sleep(2000);
				for(int i=0; i<w; i++) {
					for(int j=0; j<5; j++) {
						img[j].setBounds((int)(img[j].getBounds().getX()-1), 10, w, 270);
						if(img[j].getBounds().getX()<=-1*w) img[j].setBounds(w*4, 10, w, 270);
					}
					Thread.sleep(2);
				}
				idx++;
				if(idx==6) idx=1;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	void click(ActionEvent e) {
		if(e.getActionCommand()==cap[0]) new Search().addWindowListener(new Before(this));
		if(e.getActionCommand()==cap[1]) {
			ReservationChk chk=new ReservationChk();
			if(chk.chk()) chk.addWindowListener(new Before(this));
		}
		if(e.getActionCommand()==cap[2]) new Chart().addWindowListener(new Before(this));
		if(e.getActionCommand()==cap[3]) new Admin().addWindowListener(new Before(this));
		if(e.getActionCommand()==cap[4]) System.exit(0);
//		if(e.getActionCommand()==cap[2]) 
	}

	@Override
	void def() {
		// TODO Auto-generated method stub

	}
	
	public static void main(String[] args) {
		new Main();
	}
}
