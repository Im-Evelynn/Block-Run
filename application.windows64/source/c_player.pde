public class Player
{
  public PVector pos;
  public PVector size;

  public int speed = 20;
  public int cDir  = 0;
  public int cTarget = 0;
  public int vVel = 0;

  public boolean canMove = true;
  public boolean canJump = true;
  public boolean jumpHeld = false;

  public color fillcol = color(0, 255, 0);

  public Player(PVector pos, PVector size)
  {
    this.pos = pos;
    this.size = size;
  }

  public void render()
  {
    push();
      fill(this.fillcol); stroke(0);
      translate(this.pos.x, this.pos.y, this.pos.z);
      box(this.size.x, this.size.y, this.size.z);
    pop();

    // println(this.pos);
  }

  public void setTarget(int pTarget, int pDir)
  {
    if (this.canMove)
    {
      this.cDir = pDir;
      this.cTarget = pTarget;
      this.canMove = false;
    }
  }

  public void move()
  {
    this.pos.x += this.cDir * this.speed;
    // println(String.format("%s, %s, Hit: %s", this.pos, this.cTarget, this.pos.x == this.cTarget));
    if ((this.pos.x == this.cTarget)
    || (this.cDir == 1 && this.pos.x >= this.cTarget)
    || (this.cDir == -1 && this.pos.x <= this.cTarget))
    {
      this.canMove = true;
      this.cDir = 0;
      this.pos.x = this.cTarget;
    }
    if (this.jumpHeld && this.canJump) { this.jump(); }
    if (this.canJump != true)
    {
      this.pos.y += this.vVel;
      this.vVel  += 1;
      if (this.pos.y >= GLOBAL_PLAYER_MIN_HEIGHT)
      {
        this.pos.y = GLOBAL_PLAYER_MIN_HEIGHT;
        this.canJump = true;
      }
    }
  }

  public void jump()
  {
    if (this.canJump)
    {
      this.canJump = false;
      this.vVel = GLOBAL_PLAYER_JUMPVEL;
    }
  }
}
