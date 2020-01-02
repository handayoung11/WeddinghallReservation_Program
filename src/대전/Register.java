package ����;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Register extends BaseFrame{
	JTextField txt[]= {
			new JTextField(),
			new JTextField(),
			new JTextField(),
			new JTextField()
	};
	JCheckBox cwedding[]=new JCheckBox[10], cmeal[]=new JCheckBox[3];
	JLabel img[]=new JLabel[5];
	BufferedImage image[]=new BufferedImage[5];
	
	public Register() {
		super("���", 850, 550);
		JPanel wp=new JPanel(new GridLayout(0, 1)), sp=new JPanel(), cp=new JPanel(new GridLayout(0, 1));
		
		add(wp, "West");
		add(sp, "South");
		add(cp);
		
		for(int i=0; i<5; i++) {
			wp.add(img[i]=new JLabel());
			img[i].setBorder(new LineBorder(Color.black));
			final int idx=i;
			img[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					JFileChooser file=new JFileChooser();
					file.setFileFilter(new FileNameExtensionFilter("JPG ����", "jpg"));
					if(file.showOpenDialog(null)!=file.APPROVE_OPTION) return;
					try {
						image[idx]=ImageIO.read(file.getSelectedFile());
						img[idx].setIcon(img(file.getSelectedFile().getAbsolutePath(), 180, 90));
						if(idx!=4) img[idx+1].setVisible(true);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
			if(i!=0) img[i].setVisible(false);
		}
		((JPanel)getContentPane()).setBorder(new EmptyBorder(0, 0, 10, 0));
		size(wp, 180, 55);
		
		cp.setBorder(new EmptyBorder(80, 0, 80, 0));
		design(cp);
		sp.add(size(btn("���", e->sign()), 120, 30));
		sp.add(size(btn("���", e->dispose()), 120, 30));
		setVisible(true);
	}
	
	void design(JPanel cen) {
		String cap[]="����Ȧ��,�ּ�,�����ο�,Ȧ����,��������,�Ļ�����".split(",");
		JPanel cp=new JPanel(new GridLayout(0, 1));
		cen.add(cp);
		for(var t:txt) size(t, 530, 25);
		for(int i=0; i<cap.length; i++) {
			JPanel tmp=new JPanel();
			tmp.add(size(new JLabel(cap[i]), 60, 25));
			if(i<=3) tmp.add(txt[i]);
			else if(i==4) {
				int cnt=0;
				JPanel cn=new JPanel(new GridLayout(0, 6));
				for(int j=0; j<wedding.size(); j++) {
					if(j==0) continue;
					cn.add(size(cwedding[cnt]=new JCheckBox(wedding.get(j)), 80, 12));
					tmp.add(cn);
					cnt++;
				}
			}
			else if(i==5) {
				JPanel cn=new JPanel(new FlowLayout(0, 0, 0));
				int cnt=0;
				for(int j=0; j<meal.size(); j++) {
					if(j==0) continue;
					cn.add(size(cmeal[cnt]=new JCheckBox(meal.get(j)), 80, 12));
					cnt++;
				}
				cn.add(size(new JLabel(), 240, 25));
				tmp.add(cn);
			}
			cp.add(tmp);
		}
	}
	
	void sign() {
		for(int i=0; i<4; i++) {
			if(txt[i].getText().equals("")) {
				err_msg("��ĭ�� �Է����ּ���");
				return;
			}
		}
		if(img[0].getIcon()==null) {
			err_msg("���� �Է�");
			return;
		}
		int cnt=0, cnt2=0;
		for(var c:cwedding) if(c.isSelected()) cnt++;
		for(var c:cmeal) if(c.isSelected()) cnt2++;
		if(cnt==0 || cnt2==0) {
			err_msg("üũ�ڽ� ����");
			return;
		}
		if(txt[2].getText().matches(pattern)) {
			err_msg("�����ο��� �ٸ��� �Է����ּ���");
			return;
		}
		if(txt[3].getText().matches(pattern)) {
			err_msg("Ȧ���Ḧ �ٸ��� �Է����ּ���");
			return;
		}
		try {
			var rs=stmt.executeQuery("select * from weddinghall where weddinghall_name='"+txt[0].getText()+"'");
			if(rs.next()) {
				err_msg("�ߺ��� �̸�");
				return;
			}
			stmt.execute("insert into weddinghall values(0, '"+txt[0].getText()+"', '"+txt[1].getText()+"', "+txt[2].getText()+", "+txt[3].getText()+")");
			rs=stmt.executeQuery("select count(*) from weddinghall");
			rs.next();
			int idx=rs.getInt(1), wt=0, mt=0;
			for(var c:cwedding) {
				wt++;
				if(c.isSelected()) stmt.execute("insert into weddinghall_weddingtype values("+idx+", "+wt+")");
			}
			for(var c:cmeal) {
				mt++;
				if(c.isSelected()) stmt.execute("insert into weddinghall_mealtype values("+idx+", "+mt+")");
			}
			File file=new File(hotel+txt[0].getText());
			file.mkdir();
			for(int i=0; i<5; i++) {
				if(image[i]==null) break;
				ImageIO.write(image[i], "jpg", new File(hotel+txt[0].getText()+"/"+txt[0].getText()+" "+(i+1)+".jpg"));
			}
			msg("����� �Ϸ�Ǿ����ϴ�");
			dispose();
		} catch (Exception e) {
			e.printStackTrace();
			err_msg("�ùٸ��� ���� �Է�");
		}
	}

	@Override
	void def() {
		// TODO Auto-generated method stub
		
	}
}
