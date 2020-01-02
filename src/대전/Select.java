package 대전;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Select extends BaseFrame{
	int idx=1;
	JLabel img=bound(new JLabel(), 0, 0, 350, 450), info=new JLabel("", 0);
	JTextField txt[]= {
			new JTextField(),
			new JTextField()
	};
	
	public Select(String addr, String date) {
		super("청첩장 디자인 고르기", 350, 550);
		JPanel cp=new JPanel(null), sp=new JPanel();
		
		cp.add(bound(txt[0], 100, 140, 60, 25));
		cp.add(bound(txt[1], 180, 140, 60, 25));
		cp.add(bound(new JLabel(addr), 120, 270, 200, 30));
		cp.add(bound(new JLabel(date), 120, 300, 200, 30));
		cp.add(img);
		
		sp.add(btn("◀", e->load(idx-1)));
		sp.add(btn("결정", e->{
			if(txt[0].getText().equals("") || txt[1].getText().equals("")) {
				err_msg("이름을 입력해주세요.");
				return;
			}
			msg("디자인 "+idx+"번으로 결정되었습니다.");
			dispose();
		}));
		sp.add(btn("▶", e->load(idx+1)));
		
		add(info, "North");
		add(cp);
		add(sp, "South");
		load(1);
		setVisible(true);
	}
	
	void load(int i) {
		idx=i;
		if(idx==0) idx=3;
		if(idx==4) idx=1;
		info.setText(idx+"번 이미지");
		img.setIcon(img(path+"청첩장/청첩장"+idx+".jpg", 335, 450));
	}

	@Override
	void def() {
		// TODO Auto-generated method stub
		
	}
}
