package 대전;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Modify extends BaseFrame{
	JTextField txt[]= {
			new JTextField(),
			new JTextField(),
			new JTextField(),
			new JTextField()
	};
	JCheckBox cwedding[]=new JCheckBox[10], cmeal[]=new JCheckBox[3];
	JLabel img[]=new JLabel[5];
	BufferedImage image[]=new BufferedImage[5];
	int id;
	
	public Modify(String id) {
		super("등록", 850, 550);
		this.id=toint(id);
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
					file.setFileFilter(new FileNameExtensionFilter("JPG 파일", "jpg"));
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
		sp.add(size(btn("등록", e->sign()), 120, 30));
		sp.add(size(btn("취소", e->dispose()), 120, 30));
		setInfo(id);
		setVisible(true);
	}
	
	void setInfo(String id) {
		try {
			pst.setInt(1, toint(id));
			var rs=pst.executeQuery();
			rs.next();
			for(int i=0; i<txt.length; i++) txt[i].setText(rs.getString(i+2));
			for(var item:getType(id, "weddingtype"))
				for(var w:cwedding) if(w.getText().equals(item)) w.setSelected(true);
			for(var item:getType(id, "mealtype"))
				for(var w:cmeal) if(w.getText().equals(item)) w.setSelected(true);
			for(int i=0; i<5; i++) {
				image[i]=ImageIO.read(new File(hotel+txt[0].getText()+"/"+txt[0].getText()+" "+(i+1)+".jpg"));
				img[i].setIcon(new ImageIcon(image[i].getScaledInstance(180, 90, Image.SCALE_SMOOTH)));
				img[i+1].setVisible(true);
			}
		} catch (Exception e) {
		}
	}
	
	void design(JPanel cen) {
		String cap[]="웨딩홀명,주소,수용인원,홀사용료,예식형태,식사종류".split(",");
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
				err_msg("빈칸을 입력해주세요");
				return;
			}
		}
		int cnt=0, cnt2=0;
		for(var c:cwedding) if(c.isSelected()) cnt++;
		for(var c:cmeal) if(c.isSelected()) cnt2++;
		if(cnt==0 || cnt2==0) {
			err_msg("빈칸을 입력해주세요");
			return;
		}
		if(img[0].getIcon()==null) {
			err_msg("사진 입력");
			return;
		}
		if(txt[2].getText().matches(pattern)) {
			err_msg("수용인원을 바르게 입력해주세요");
			return;
		}
		if(txt[3].getText().matches(pattern)) {
			err_msg("홀사용료를 바르게 입력해주세요");
			return;
		}
		try {
			int idx=id;
			stmt.execute("update weddinghall set weddinghall_name='"+txt[0].getText()+"', weddinghall_address='"+txt[1].getText()+"', weddinghall_accommodate="+txt[2].getText()+", weddinghall_fee="+txt[3].getText()+" where weddinghall_index="+idx);
			var rs=stmt.executeQuery("select count(*) from weddinghall");
			rs.next();
			int wt=0, mt=0;
			execute("delete from weddinghall_weddingtype where weddinghall_index="+idx);
			execute("delete from weddinghall_mealtype where weddinghall_index="+idx);
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
			msg("수정이 완료되었습니다");
			dispose();
		} catch (Exception e) {
			e.printStackTrace();
			err_msg("올바르지 못한 입력");
		}
	}

	@Override
	void def() {
		// TODO Auto-generated method stub
		
	}
}
