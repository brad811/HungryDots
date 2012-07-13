import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.text.DecimalFormat;
import java.util.Random;

public class main extends Applet implements Runnable
{
	private static final long serialVersionUID = 1L;
	DecimalFormat df = new DecimalFormat("###.##");
	int stageSize = 400,
			delay = 10,
			foodSize = 8,
			numFoods = 10,
			numRobots = 2,
			maxRobots = 100,
			births = 0,
			deaths = 0,
			foodTime = 1,
			foodSpawn = 1,
			variance = 200,
			fertility = 1000,
			foodChance = 500,
			startSize = 16;
	
	static Random rand = new Random(System.currentTimeMillis());

	long start = System.currentTimeMillis();
	Graphics bufferGraphics;
	Dimension dim;
	Image offscreen;
	robot[] myRobot = new robot[this.maxRobots];
	food[] myFood = new food[20];

	public void start()
	{
		setSize(this.stageSize + 180, this.stageSize);
		this.dim = getSize();
		this.offscreen = createImage(this.dim.width, this.dim.height);
		this.bufferGraphics = this.offscreen.getGraphics();
		Thread th = new Thread(this);
		th.start();
	}

	public void run()
	{
		int startVelocity = 1;
		double startRadius = 1.0D;
		for (int i = 0; i < this.numRobots; i++)
		{
			int x = rand.nextInt(this.stageSize - this.startSize * 2) + this.startSize;
			int y = rand.nextInt(this.stageSize - this.startSize * 2) + this.startSize;
			this.myRobot[i] = new robot(this.stageSize, this.startSize, x, y, startRadius,
					startVelocity);
		}

		for (int i = 0; i < this.numFoods; i++)
		{
			int x = rand.nextInt(this.stageSize - this.foodSize * 2) + this.foodSize;
			int y = rand.nextInt(this.stageSize - this.foodSize * 2) + this.foodSize;
			this.myFood[i] = new food(x, y);
		}

		while(true)
		{
			step();
			feed();
			try
			{
				Thread.sleep(this.delay);
			} catch (InterruptedException localInterruptedException)
			{
			}
		}
	}

	public void feed()
	{
		for (int j = 0; j < this.myFood.length; j++)
		{
			if(this.myFood[j] != null)
				continue;
			
			if(rand.nextInt(this.foodChance) == 4)
			{
				int nx = rand.nextInt(this.stageSize - this.startSize * 2) + this.startSize;
				int ny = rand.nextInt(this.stageSize - this.startSize * 2) + this.startSize;
	
				if(nx > this.stageSize - this.foodSize)
					nx = this.stageSize - this.foodSize;
				else if(nx < this.foodSize)
				{
					nx = this.foodSize;
				}
				if(ny > this.stageSize - this.foodSize)
					ny = this.stageSize - this.foodSize;
				else if(ny < this.foodSize)
				{
					ny = this.foodSize;
				}
	
				this.myFood[j] = new food(nx, ny);
				this.numFoods += 1;
				break;
			}
		}
	}

	public void step()
	{
		for (int i = 0; i < this.myRobot.length; i++)
		{
			if(this.myRobot[i] == null)
			{
				continue;
			}
			get_smell(this.myRobot[i], this.myRobot[i].x, this.myRobot[i].y);
			this.myRobot[i].step(this.myFood);

			if(this.myRobot[i].score >= 10)
			{
				if(rand.nextInt(this.fertility) != 4)
					continue;
				for (int j = 0; j < this.maxRobots; j++)
				{
					if(this.myRobot[j] != null)
						continue;
					this.myRobot[j] = new robot(this.stageSize, this.myRobot[i].size,
							(int)this.myRobot[i].x, (int)this.myRobot[i].y,
							this.myRobot[i].turnRadius, this.myRobot[i].velocity);

					this.births += 1;
					break;
				}
			}
			else
			{
				if(this.myRobot[i].score >= 0)
					continue;
				this.myRobot[i] = null;
				this.deaths += 1;
			}
		}

		repaint();
	}

	public void get_smell(robot myBot, double x, double y)
	{
		for (int i = 0; i < this.myFood.length; i++)
		{
			if(this.myFood[i] == null)
			{
				continue;
			}
			double distance = Math.sqrt(Math.pow(Math.abs(myBot.x - this.myFood[i].x), 2.0D)
					+ Math.pow(Math.abs(myBot.y - this.myFood[i].y), 2.0D));

			if(distance > myBot.size / 2.0D + this.foodSize / 2)
				continue;
			myBot.setScore(myBot.score + 1);
			this.myFood[i] = null;
			this.myFood[i] = null;
			this.numFoods -= 1;
		}

		if(this.numFoods <= 1)
		{
			this.foodSpawn += 1;
		}
		if(this.foodSpawn >= 100)
		{
			int nx = rand.nextInt(this.stageSize - this.foodSize * 2) + this.foodSize;
			int ny = rand.nextInt(this.stageSize - this.foodSize * 2) + this.foodSize;

			if(nx > this.stageSize - this.foodSize)
				nx = this.stageSize - this.foodSize;
			else if(nx < this.foodSize)
			{
				nx = this.foodSize;
			}
			if(ny > this.stageSize - this.foodSize)
				ny = this.stageSize - this.foodSize;
			else if(ny < this.foodSize)
			{
				ny = this.foodSize;
			}
			this.myFood[0] = new food(nx, ny);
			this.numFoods += 1;
			this.foodSpawn = 0;
		}
	}

	public void update(Graphics g)
	{
		paint(g);
	}

	public void paint(Graphics g)
	{
		this.bufferGraphics.clearRect(0, 0, this.dim.width, this.dim.width);

		this.bufferGraphics.setColor(new Color(200, 200, 200));
		this.bufferGraphics.fillRect(0, 0, this.stageSize, this.stageSize);

		int countRobots = 0;
		for (int i = 0; i < this.myRobot.length; i++)
		{
			try {
				if(this.myRobot[i] == null)
					continue;
				double blue = 255.0D * (this.myRobot[i].score / 10.0D);
				if(blue > 255.0D)
					blue = 255.0D;
				double red = 255.0D - 255.0D * (this.myRobot[i].score / 10.0D);
				if(red < 0.0D)
					red = 0.0D;
				this.bufferGraphics.setColor(new Color((int)red, 0, (int)blue));
				this.bufferGraphics.fillOval((int)(this.myRobot[i].x - this.myRobot[i].size / 2.0D),
						(int)(this.myRobot[i].y - this.myRobot[i].size / 2.0D),
						(int)this.myRobot[i].size, (int)this.myRobot[i].size);
	
				this.bufferGraphics.setColor(Color.WHITE);
				this.bufferGraphics.drawLine((int)this.myRobot[i].x, (int)this.myRobot[i].y,
						(int)(this.myRobot[i].x + this.myRobot[i].vx * 4.0D),
						(int)(this.myRobot[i].y + this.myRobot[i].vy * 4.0D));
	
				countRobots++;
			} catch(NullPointerException e)
			{
				
			}
		}

		this.bufferGraphics.setColor(Color.BLACK);
		int row = 14;
		this.bufferGraphics.drawString("Runtime: " + getTime(), this.stageSize + 10, row);
		row += 14;
		this.bufferGraphics.drawString("Births: " + this.births, this.stageSize + 10, row);
		row += 14;
		this.bufferGraphics.drawString("Deaths: " + this.deaths, this.stageSize + 10, row);
		row += 14;
		this.bufferGraphics.drawString("Robots: " + countRobots, this.stageSize + 10, row);
		row += 14;

		this.bufferGraphics.drawString("Stats: ", this.stageSize + 10, row);
		row += 14;

		this.bufferGraphics.drawString("Full", this.stageSize + 10, row);
		this.bufferGraphics.drawString("Size", this.stageSize + 40, row);
		this.bufferGraphics.drawString("Vel", this.stageSize + 90, row);
		this.bufferGraphics.drawString("Rad", this.stageSize + 130, row);
		row += 14;

		countRobots = 0;
		for (int i = 0; i < this.myRobot.length; i++)
		{
			try {
				if(this.myRobot[i] == null)
					continue;
				countRobots++;
				this.bufferGraphics.drawString(Integer.toString(this.myRobot[i].score),
						this.stageSize + 10, row);
				this.bufferGraphics.drawString(this.df.format(this.myRobot[i].size),
						this.stageSize + 40, row);
				this.bufferGraphics.drawString(this.df.format(this.myRobot[i].velocity),
						this.stageSize + 90, row);
				this.bufferGraphics.drawString(this.df.format(this.myRobot[i].turnRadius),
						this.stageSize + 130, row);
				row += 14;
			}
			catch(NullPointerException e)
			{
				
			}
		}

		this.bufferGraphics.setColor(new Color(0, 180, 0));
		for (int i = 0; i < this.myFood.length; i++)
		{
			if(this.myFood[i] == null)
				continue;
			this.bufferGraphics.fillOval(this.myFood[i].x - this.foodSize / 2, this.myFood[i].y
					- this.foodSize / 2, this.foodSize, this.foodSize);
		}

		g.drawImage(this.offscreen, 0, 0, this);
	}

	private String getTime()
	{
		int runTime = (int)(System.currentTimeMillis() - this.start) / 1000;
		String myTime = "";
		int s1 = 0;
		int s10 = 0;
		int m1 = 0;
		int m10 = 0;
		int h1 = 0;
		int h10 = 0;

		h10 = runTime / 36000;
		runTime -= h10 * 36000;
		h1 = runTime / 3600;
		runTime -= h1 * 3600;
		m10 = runTime / 600;
		runTime -= m10 * 600;
		m1 = runTime / 60;
		runTime -= m1 * 60;
		s10 = runTime / 10;
		s1 = runTime % 10;

		myTime = " " + h10 + h1 + ":" + m10 + m1 + ":" + s10 + s1;

		return myTime;
	}
}