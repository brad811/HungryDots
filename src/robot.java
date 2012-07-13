import java.util.Random;

public class robot
{
	int score,
		round,
		appetite;
	
	double x, y,
		vx, vy,
		minPos, maxPos,
		turnRadius,
		angle,
		velocity,
		size,
		smell,
		lastSmell = 0.0D;
	
	static Random rand = new Random(System.currentTimeMillis());

	robot(double m, double s, int mx, int my, double tr, double vc)
	{
		this.x = mx;
		this.y = my;
		this.turnRadius = ((rand.nextInt(3) - 1) * rand.nextDouble() * (0.2D * tr) + tr);
		if(this.turnRadius == 0.0D)
		{
			this.turnRadius = tr;
		}
		this.angle = 0.0D;

		this.size = ((rand.nextInt(3) - 1) * rand.nextDouble() * (0.2D * s) + s);
		if(this.size == 0.0D)
		{
			this.size = s;
		}

		this.velocity = ((rand.nextInt(3) - 1) * rand.nextDouble() * (0.2D * vc) + vc);
		if(this.velocity == 0.0D)
		{
			this.velocity = vc;
		}

		this.minPos = (this.size / 2.0D);
		this.maxPos = (m - this.size / 2.0D);
		this.score = 5;
		this.round = 0;
		this.appetite = (400 - (int)(this.velocity * 100.0D));
	}

	public void step(food[] myFood)
	{
		this.lastSmell = this.smell;
		this.smell = 0.0D;
		this.vx = 0.0D;
		this.vy = 0.0D;
		double maxAngle = 0.0D;
		double maxSmell = 0.0D;
		for (int i = 0; i < 360; i += 10)
		{
			double tempAngle = this.angle;
			tempAngle += i;

			double tx = Math.cos(tempAngle) * this.velocity;
			double ty = Math.sin(tempAngle) * this.velocity;

			for (int j = 0; j < myFood.length; j++)
			{
				if((myFood[j] == null)
						|| (myFood[j].get_smell((int)(this.x + tx), (int)(this.y + ty)) <= this.smell))
					continue;
				this.smell = myFood[j].get_smell((int)(this.x + tx), (int)(this.y + ty));
			}

			if(this.smell <= maxSmell)
				continue;
			maxAngle = tempAngle;
			maxSmell = this.smell;
			this.vx = tx;
			this.vy = ty;
		}

		this.smell = maxSmell;

		this.round += 1;
		if(this.round >= this.appetite)
		{
			this.score -= 1;
			this.round = 0;
		}

		if(maxSmell == 0.0D)
		{
			wander();
		}
		else if(this.score >= 10)
		{
			wander();
		}
		else
		{
			if(maxAngle > this.angle)
				this.angle += this.turnRadius;
			else if(maxAngle < this.angle)
				this.angle -= this.turnRadius;
			move();
		}
	}

	public void wander()
	{
		int which = rand.nextInt(40);
		switch (which) {
			case 0:
				this.angle += this.turnRadius;
				break;
			case 1:
				this.angle -= this.turnRadius;
				break;
			default:
				this.angle += 0.0D;
		}

		this.vx = (Math.cos(this.angle) * this.velocity);
		this.vy = (Math.sin(this.angle) * this.velocity);

		move();
	}

	public void move()
	{
		while(this.angle >= 360.0D)
			this.angle -= 360.0D;

		if((this.vx == 0.0D) && (this.vy == 0.0D))
		{
			this.vx = 2.0D;
			this.vy = 2.0D;
		}

		this.x += this.vx;
		this.y += this.vy;

		if(this.x > this.maxPos)
		{
			this.x = this.maxPos;
			this.angle += this.turnRadius;
		}
		if(this.y > this.maxPos)
		{
			this.y = this.maxPos;
			this.angle += this.turnRadius;
		}
		if(this.x < this.minPos)
		{
			this.x = this.minPos;
			this.angle += this.turnRadius;
		}
		if(this.y < this.minPos)
		{
			this.y = this.minPos;
			this.angle += this.turnRadius;
		}
	}

	public void setScore(int s)
	{
		this.score = s;
	}
}