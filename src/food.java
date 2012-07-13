public class food
{
	int x;
	int y;
	int value;
	int time;
	int maxSmell = 150;

	food(int mx, int my)
	{
		this.x = mx;
		this.y = my;
		this.value = 100;
		this.time = 0;
	}

	public double get_smell(double rx, double ry)
	{
		double smell = this.maxSmell;

		double distance = Math.sqrt(Math.pow(Math.abs(this.x - rx), 2.0D)
				+ Math.pow(Math.abs(this.y - ry), 2.0D));

		smell -= distance;
		
		if(smell < 0.0D)
		{
			smell = 0.0D;
		}
		
		return smell;
	}

	public void bite()
	{
		this.value -= 5;
	}

	public int getX()
	{
		return this.x;
	}

	public int getY()
	{
		return this.y;
	}
}