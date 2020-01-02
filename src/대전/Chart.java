package 대전;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Chart extends BaseFrame{
	JComboBox com=combo("인기 웨딩 종류,인기 식사 종류".split(","));
	ArrayList<Double> slideStart=new ArrayList<>(), slideAngle=new ArrayList<>();
	ArrayList<Color> color=new ArrayList<>();
	int colorIdx=-1;
	DefaultTableModel model=model("이름,주소,홀사용료".split(","));
	JTable table=table(model);
	ArrayList<String >info=new ArrayList<>();
	JPanel cp=new JPanel(new BorderLayout()),ep=new JPanel(new GridBagLayout());
	
	public Chart() {
		super("차트", 300, 550);
		add(cp);
		add(ep, "East");
		
		com.addActionListener(it->{
			colorIdx=-1;
			setSize(300, 550);
			size(ep, 0,  0);
			new SlideChart().start();
			setLocationRelativeTo(null);
		});
		
		cp.add(size(com, 25, 30), "North");
		ep.add(size(new JScrollPane(table), 330, 400));
		size(ep, 0, 0);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				new SlideChart().start();
			}
		});
		color.add(Color.BLACK);
		color.add(Color.red);
		color.add(Color.blue);
		color.add(Color.yellow);
		color.add(Color.cyan);
		color.add(Color.pink);
		color.add(Color.ORANGE);
		color.add(Color.GRAY);
		color.add(Color.green);
		color.add(Color.DARK_GRAY);
		color.add(Color.LIGHT_GRAY);
		drawChart();
		setVisible(true);
	}
	
	void drawChart() {
		JPanel chart=new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				for(int i=0; i<slideAngle.size(); i++) {
					g.setColor(color.get(i));
					if(i==colorIdx) g.setColor(Color.magenta);
					g.fillArc(20, 10, 250, 250, (int)Math.ceil(slideStart.get(i)), (int)Math.ceil(slideAngle.get(i)));
					g.fillRect(70, 270+i*20, 15, 15);
					g.setColor(Color.BLACK);
					g.drawString(info.get(i), 120, 280+i*20);
				}
				g.setColor(Color.white);
				g.fillOval(95, 85, 100, 100);
			}
		};
		
		chart.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				for(int i=0; i<slideAngle.size(); i++)
					if(e.getX()>=70 && e.getX()<=85 && e.getY()>=270+i*20 && e.getY()<=285+i*20) {
						colorIdx=i;
						size(ep, 350, 550);
						setSize(650, 550);
						String t="mealtype";
						if(com.getSelectedIndex()==0) t="weddingtype";
						int tidx=0;
						for(var wed:t=="mealtype"?meal:wedding) {
							if(wed.contentEquals(info.get(colorIdx).split(" : ")[0])) break;
							tidx++;
						}
 						addRow(model, "select weddinghall_name, weddinghall_address, concat(format(weddinghall_fee, 0), '원') from weddinghall w inner join weddinghall_"+t+" wt on wt.weddinghall_index=w.weddinghall_index inner join "+t+" t on t."+t+"_index=wt."+t+"_index where t."+t+"_index="+tidx);
						repaint();
						revalidate();
					}
			}
			
		});
		cp.add(chart);
	}
	
	class SlideChart extends Thread{
		@Override
		public void run() {
			Collections.shuffle(color);
			info.clear();
			slideAngle.clear();
			slideStart.clear();
			ArrayList<Double> steps=new ArrayList<>();
			slideStart.add(0.0);
			try {
				String t="weddingtype";
				if(com.getSelectedIndex()==1) t="mealtype";
				var rs=con.createStatement().executeQuery("select weddinghall_name, count(*) as cnt from reservation r inner join weddinghall w on w.weddinghall_index=r.weddinghall_index inner join weddinghall_"+t+" t on t.weddinghall_index=r.weddinghall_index inner join "+t+" tt on t."+t+"_index=tt."+t+"_index where pay=1");
				rs.next();
				double p=360.0/rs.getInt(2);
				rs=con.createStatement().executeQuery("select "+t+"_name, count(*) as cnt from reservation r inner join weddinghall w on w.weddinghall_index=r.weddinghall_index inner join weddinghall_"+t+" t on t.weddinghall_index=r.weddinghall_index inner join "+t+" tt on t."+t+"_index=tt."+t+"_index where pay=1 group by t."+t+"_index order by cnt desc");
				while(rs.next()) {
					steps.add(p*rs.getInt(2));
					slideStart.add(slideStart.get(slideStart.size()-1)+steps.get(steps.size()-1));
					info.add(String.format("%s : %d개", rs.getString(1), rs.getInt(2)));
					slideAngle.add(0.0);
				}
				for(int j=1; j<11; j++) {
					for(int i=0; i<slideAngle.size(); i++) { 
						slideAngle.set(i, j*0.1*steps.get(i));
						Thread.sleep(10);
					}
					repaint();
					revalidate();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	void def() {
		// TODO Auto-generated method stub
		
	}
}
