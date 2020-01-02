package ����;

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
		super("ûø�� ������ ����", 350, 550);
		JPanel cp=new JPanel(null), sp=new JPanel();
		
		cp.add(bound(txt[0], 100, 140, 60, 25));
		cp.add(bound(txt[1], 180, 140, 60, 25));
		cp.add(bound(new JLabel(addr), 120, 270, 200, 30));
		cp.add(bound(new JLabel(date), 120, 300, 200, 30));
		cp.add(img);
		
		sp.add(btn("��", e->load(idx-1)));
		sp.add(btn("����", e->{
			if(txt[0].getText().equals("") || txt[1].getText().equals("")) {
				err_msg("�̸��� �Է����ּ���.");
				return;
			}
			msg("������ "+idx+"������ �����Ǿ����ϴ�.");
			dispose();
		}));
		sp.add(btn("��", e->load(idx+1)));
		
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
		info.setText(idx+"�� �̹���");
		img.setIcon(img(path+"ûø��/ûø��"+idx+".jpg", 335, 450));
	}

	@Override
	void def() {
		// TODO Auto-generated method stub
		
	}
}
