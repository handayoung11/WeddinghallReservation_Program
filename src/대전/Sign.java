package 대전;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Sign extends BaseFrame{
	int w=400, h=300;
	BufferedImage img=new BufferedImage(w, h, Image.SCALE_SMOOTH);
	JLabel paint=new JLabel(new ImageIcon(img));
	Brush brush=new Brush();
	
	public Sign(JLabel sign) {
		super("사인", 450, 350);
		var sp=new JPanel();
		var cp=new JPanel(null);
		
		add(cp);
		add(sp, "South");
		
		sp.add(btn("확인", e->{
			sign.setIcon(new ImageIcon(img.getScaledInstance(320, 200, Image.SCALE_SMOOTH)));
			dispose();
		}));
		((JPanel)getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
		paint.setBackground(Color.black);
		cp.add(bound(paint, 0, 0, w, h));
		cp.add(bound(brush, 0, 0, w, h));
		brush.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				brush.x=e.getX();
				brush.y=e.getY();
				brush.repaint();
				brush.paintAll(img.getGraphics());
			}
		});
		
		setVisible(true);
	}
	
	void def(){
		
	}
}

class Brush extends JLabel{
	int x, y;
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.red);
		g.fillOval(x-10, y-10, 20, 20);
	}
}