public class Lane
{
  public class Obstacle
  {
    Lane linkedLane;
    int zoffset;
    int yoffset;
    int height;
    PVector size;
    boolean deadly;
    String tag = "NONE";

    public Obstacle(Lane pLinkedLane, int pZoffset, int pYoffset, int pHeight, boolean pDeadly)
    {
      this.linkedLane = pLinkedLane;
      this.zoffset = pZoffset;
      this.yoffset = pYoffset;
      this.height = pHeight;
      this.size = new PVector ( // DEFAULT SIZE
        floor(this.linkedLane.size.x),
        this.height,
        floor(this.linkedLane.size.x/2)
      );
      this.deadly = pDeadly;
    }

    public void render()
    {
      push();
        if (this.deadly) { fill(255, 0, 0); }
        else { fill(255, 255, 0); }
        translate(linkedLane.pos.x, linkedLane.pos.y+this.yoffset, linkedLane.pos.z+this.zoffset);
        box(this.size.x, this.height, this.size.z);
      pop();
    }
  }

  public PVector pos;
  public PVector size;

  public color fillcol = GLOBAL_LANE_COLOUR;

  public ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();

  public Lane(PVector pos, PVector size) { this(pos, size, 0); }
  public Lane(PVector pos, PVector size, int pOPreset)
  {
    this.pos = pos;
    this.size = size;
    
    switch(pOPreset)
    {
      case 0:
        break;
      case 1:
        this.obstacles.add(
          obstacle_basicJump()
        );
        break;
      case 2:
        this.obstacles.add(
          obstacle_basicWall()
        );
        break;
      case 3:
        this.obstacles.add(
          obstacle_basicCoin()
        );
        break;
      case 4:
        this.obstacles.addAll(
          obstacle_tripleCoin()
        );
        break;
    }
  }

  public void render()
  {
    push();
      fill(fillcol); stroke(0);
      translate(this.pos.x, this.pos.y, this.pos.z);
      box(this.size.x, this.size.y, this.size.z);
    pop();
    for (Obstacle o : this.obstacles)
    {
      o.render();
    }
  }

  public void move(float pOffset)
  {
    this.pos.z += pOffset;
  }

  public void checkcollide(Player p)
  {
    // check player X/Y/Z for collision
    for (int i = this.obstacles.size()-1; i >= 0; i--)
    {
      Obstacle o = this.obstacles.get(i);
      // linkedLane.pos.x, linkedLane.pos.y+this.yoffset, linkedLane.pos.z+this.zoffset // POS
      // linkedLane.size.x, this.height, linkedLane.size.x/2                            // SIZE

      // Basic collision : IF CENTER HITS THEN COLLIDE
      // Ill implement complex coloision later coz IDC
      println(p.pos + "->" + this.pos);
      println(this.pos.z-o.size.z/2+o.zoffset + " : " + this.pos.z+o.size.z/2+o.zoffset + "\n-----");
      if (p.pos.x >= this.pos.x-this.size.x/2 && p.pos.x <= this.pos.x+this.size.x/2
      && (p.pos.y >= this.pos.y-o.height+o.yoffset && p.pos.y <= this.pos.y+o.height+o.yoffset)
      && (p.pos.z >= this.pos.z-o.size.z/2+o.zoffset && p.pos.z <= this.pos.z+o.size.z/2+o.zoffset))
      {
        println("COLLIDE: " + this);
        if (o.deadly) { gameOver(); }
        else
        {
          if (o.tag == "COIN")
          {
            GLOBAL_COIN_COUNT += GLOBAL_COIN_VALUE;
            this.obstacles.remove(i);
          }
        }
      }
    }
  }



  // Obstacle Gen
  private Obstacle obstacle_basicJump()
  {
    return new Obstacle(
      this,
      0,
      0,
      floor(this.size.x/2),
      true
    );
  }
  private Obstacle obstacle_basicWall()
  {
    return new Obstacle(
      this,
      0,
      0,
      floor(this.size.x)*2,
      true
    );
  }
  private Obstacle obstacle_basicCoin()
  {
    Obstacle o = new Obstacle(
      this,
      0,
      GLOBAL_COIN_HEIGHT,
      75,
      false
    );
    o.size.x = floor(o.size.z/2);
    o.size.y = floor(o.size.z/2);
    o.height = floor(o.size.z/2);
    o.tag = "COIN";
    return o;
  }
  private ArrayList<Obstacle> obstacle_tripleCoin()
  {
    ArrayList<Obstacle> olist = new ArrayList();
    Obstacle o1 = new Obstacle(
      this,
      -75*2,
      GLOBAL_COIN_HEIGHT,
      75,
      false
    );
    o1.size.x = floor(o1.size.z/2);
    o1.size.y = floor(o1.size.z/2);
    o1.height = floor(o1.size.z/2);
    o1.tag = "COIN";
    Obstacle o2 = new Obstacle(
      this,
      0,
      GLOBAL_COIN_HEIGHT,
      75,
      false
    );
    o2.size.x = floor(o2.size.z/2);
    o2.size.y = floor(o2.size.z/2);
    o2.height = floor(o2.size.z/2);
    o2.tag = "COIN";
    Obstacle o3 = new Obstacle(
      this,
      75*2,
      GLOBAL_COIN_HEIGHT,
      75,
      false
    );
    o3.size.x = floor(o3.size.z/2);
    o3.size.y = floor(o3.size.z/2);
    o3.height = floor(o3.size.z/2);
    o3.tag = "COIN";

    olist.add(o1);
    olist.add(o2);
    olist.add(o3);
    return olist;
  }
}