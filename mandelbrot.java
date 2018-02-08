
package mandelbrot;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;


public class mandelbrot extends Frame
{
	private static final long serialVersionUID = 1L;
	private double rangeReal, rangeImaginary, originReal, originImaginary, aspectRatio, delta;
	private int height, width;
	private BufferedImage bufImage;
	private Insets ins;
	private int Hwindow;			
	private int Wwindow;
	private double xRange, yImaginary;
	private Color c = new Color(255, 255, 255);
	private Color C = new Color(0, 0, 0);
	private Color R;
	private boolean rubberbanding;
	private int x_init, y_init, x_cur, y_cur;
	private int r, g;
	
	
	

	//Constructor
	mandelbrot()
	{
		//Enables the closing of the window.
		addWindowListener(new MyFinishWindow());
		
		addMouseListener(
				new MouseAdapter()
				{
					public void mousePressed(MouseEvent evt)
					{
						// rubberbanding starts with the first mouse move
						rubberbanding = false;
						
						// remember the coordinates
						x_init = evt.getX();
						y_init = evt.getY();
					}

					public void mouseReleased(MouseEvent evt)
					{
						// done with rubberbanding
						rubberbanding = false;
						
						// coordinates at release point
						x_cur = evt.getX();
						y_cur = evt.getY();
						
						int w = (int) Math.abs(x_cur - x_init);
						int h = (int) Math.abs(y_cur - y_init);
						double xmin = Math.min(x_cur, x_init);
						double ymin = (int) Math.min(x_cur, x_init);
						double xmax = Math.min(x_cur, x_init);
						double ymax = (int) Math.min(x_cur, x_init);
						
					}
				}
				);
		addMouseMotionListener(
				new MouseAdapter()
				{
					public void mouseDragged(MouseEvent evt)
					{
						Graphics2D g2d = (Graphics2D)getGraphics();

						// rubber banding with XOR drawing:
						// drawing into the canvas once will display a rectangle
						// drawing over the same location will restore the previous state
						g2d.setXORMode(Color.black);
						g2d.setColor(Color.white);
						// have we already drawn a rectangle? if yes, draw over it to make it disappear
						if(rubberbanding == true)
						{
							drawRect(g2d);
						}
						// update
						x_cur = evt.getX();
						y_cur = evt.getY();
						drawRect(g2d);
						// after we drew at least once
						rubberbanding = true;
					}
				}
				);
	
	}
	
	public void paint(Graphics g)
	{
		//In order to use Java 2D, it is necessary to cast the Graphics object
		//into a Graphics2D object.
		Graphics2D g2d = (Graphics2D) g;
		
		
		
		setupDisplay();
		setupCoordinateSystem(4.0, -0.5, 0);
		
		generateMandelbrot();
		g2d.drawImage(bufImage,ins.left,ins.top,null);
	}
	
	private void setupDisplay()
	{
		Hwindow = getHeight();
		Wwindow = getWidth();
		
		
		ins = getInsets();
		
		width = (Wwindow - ins.left - ins.right);
		height = (Hwindow - ins.top - ins.bottom);
		
		aspectRatio = rangeReal/rangeImaginary;
		bufImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		//bufImage.setRGB(rangeReal, rangeImaginary, c.getRGB());
		
		
	}
	
	private void setupCoordinateSystem(double rangeReal, double originReal, double originImaginary)
	{
		this.rangeReal = rangeReal;
		this.originReal = originReal;
		this.originImaginary = originImaginary;
		rangeImaginary = height * rangeReal / width;
		aspectRatio = rangeReal/rangeImaginary;
		delta =  rangeReal / width;
	
		
	}
	
	private double screenXtoLogical(int x)
	{
		 xRange = originReal - (rangeReal/2) + delta * x;
		 return xRange;
	}
	private double screenYtoLogical(int y)
	{
		yImaginary = originImaginary + (rangeImaginary /2) - delta * y;
		return yImaginary;
	}
	
	public Color redShades(int iter, int maxIter)
	{
		r = (int)(80 + 175*(double) (iter/maxIter));
		
		if(iter > 60)
		{
			g = 155 + 100* iter/maxIter;
		}
		else
		{
			g = 0;
		}
		
		if(r >= 255)
		{
			r = 255;
		}
		else if(r <= 0)
		{
			r = 0;
		}
		
		return new Color(r, g, 0);
		
	}
	private void drawRect(Graphics2D g)
	{
		g.drawRect(Math.min(x_init,  x_cur), Math.min(y_init,  y_cur),
				   Math.abs(x_init - x_cur), Math.abs(y_init - y_cur));
	}
	public void generateMandelbrot()
	{
		for(int i = 0; i < width; i++)
		{
			int max = 100;
			double Cx = screenXtoLogical(i);
			for(int j = 0; j < height; j++)
			{
				double Cy = screenYtoLogical(j);
				double Zx = 0, Zy = 0;
				boolean called = false;
				int s;
				for(s = 0; s < max; s++)
				{
					double XX = Zx * Zx;
					double YY = Zy * Zy;
					if(XX + YY > 4)
					{
						bufImage.setRGB(i, j, (redShades(s, max)).getRGB());
						called = true;
						break;
					}
					Zy = (2*Zx)*Zy + Cy;
					Zx = XX - YY + Cx;
					if(called == false)
					{
						bufImage.setRGB(i, j, C.getRGB());
					}
					
				}
				
				
			}
		}
	
		
		

		
	}

	
   public static void main(String[] argv)
	{
		//Generate the window.
		mandelbrot f = new mandelbrot();
		
		//Define a title for the window.
		f.setTitle("Basis for Java 2D programs");
		//Definition of the window size in pixels
		f.setSize(800, 600);
		//Show the window on the screen.
		f.setVisible(true);
		f.setResizable(false);
	}
}
