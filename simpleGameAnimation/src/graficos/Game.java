package graficos;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;


public class Game extends Canvas implements Runnable{
	
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	private final int WIDTH = 240;
	private final int HEIGHT = 160;
	private final int SCALE = 3;
	
	private BufferedImage image;
	private Spritesheet sheet;
	private BufferedImage[] player;
	private int frames = 0;
	private int maxFrames = 20;
	private int curAnimation = 0, maxAnimation = 2;

	
	
	public Game() {
		sheet = new Spritesheet("/spritesheet.png");
		player = new BufferedImage[2];
		player[0] = sheet.getSprite(0, 0, 16, 16);
		player[1] = sheet.getSprite(16, 0, 16, 16);
		
		setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		initFrame();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	}
	
	public void initFrame() {
		frame = new JFrame("Game #1");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}
	
	public void tick() {
		frames++;
		
		if(frames > maxFrames) {
			frames = 0;
			curAnimation++;
			
			if(curAnimation >= maxAnimation) {
				curAnimation = 0;
			}
		}
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		// background black
		Graphics g = image.getGraphics();
		g.setColor(new Color(0,0,0));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		g.setFont(new Font("Arial", Font.BOLD, 20));
		g.setColor(Color.white);
		g.drawString("Olá Mundo!", 19, 19);
		
		/* Renderizaçao do jogo */
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(player[curAnimation],90,90,null);
		
		
		/* */
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		bs.show();
	}
	
	// game loop profissional
	public void run() {
		long lastTime = System.nanoTime(); // pega o tempo do pc em nanosegundos
		double  amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;  // frames por segundo
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis(); // retorna o tempo em uma medidad menos precisa
		
		
		while(isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			
			if (delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}
			
			// calcula frames por segundo
			if(System.currentTimeMillis() - timer >= 1000) {
				System.out.println("FPS: "+ frames);
				frames = 0;
				timer += 1000;
			}
		}
	}

}
