import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class main extends PApplet {

ArrayList<Lane> vis_lanes = new ArrayList<Lane>();
ArrayList<Lane> menu_lanes = new ArrayList<Lane>();
ArrayList<Lane> shop_lanes = new ArrayList<Lane>();
ArrayList<TextHolder> onScreenTexts = new ArrayList<TextHolder>();

Menu menu;
Shop shop;
Player player;

PImage GLOBAL_IMAGE_BACK_ARROW;

TextHolder scoreboard_runscore;
TextHolder scoreboard_highscore;
TextHolder scoreboard_coincount;
TextHolder scoreboard_fps;

boolean GLOBAL_GAME_RUNNING = false;
boolean GLOBAL_MENU_ACTIVE = true;
boolean GLOBAL_SHOP_ACTIVE = false;

static int GLOBAL_LANELENGTH = 500;
static int GLOBAL_SPEED = 20;
static int GLOBAL_LANE_MAX_ZPOS = 450;
static int GLOBAL_LANE_MIN_ZPOS = -2500;
static int GLOBAL_LANE_DIST = 150;
static int GLOBAL_LANEWIDTH = 150;
static int GLOBAL_LANEHEIGHT = 25;

int GLOBAL_LANE_FOCUS = 1;
int GLOBAL_SPEEDCLOCK = 0;

PVector GLOBAL_CAMERA;
int GLOBAL_CAMERA_DIR = 0;
int GLOBAL_CAMERA_SPEED = 10;
int GLOBAL_CAMERA_TARGET = 0;
boolean GLOBAL_CAMERA_ANI = false;

static int GLOBAL_PLAYER_MIN_HEIGHT = -50;
static int GLOBAL_PLAYER_JUMPVEL = -12;

PVector GLOBAL_BUTTON_SIZE = new PVector(100, 100);

static String GLOBAL_GAME_TITLE = "Box Run"; // Please change you dippy girl
int GLOBAL_RUN_SCORE = 0;
int GLOBAL_HIGH_SCORE = 0;

int GLOBAL_COIN_COUNT = 0;
static int GLOBAL_COIN_VALUE = 1;
static int GLOBAL_COIN_HEIGHT = -75;
static int GLOBAL_COIN_JUMP_HEIGHT = -150;

static int GLOBAL_WIDTH_BUFFER = 25;
static int GLOBAL_HEIGHT_BUFFER = 25;
static int GLOBAL_FRAMERATE = 60;

int GLOBAL_PURCHASE_COUNT = 0;
PVector GLOBAL_BUYLOCKICON_SIZE = new PVector(50, 50);

int GLOBAL_LANE_COLOUR = color(255);
int GLOBAL_LANE_COLOUR_SELECTED = 0;

static PVector GLOBAL_TOAST_SIZE = new PVector(300, 100);

int GLOBAL_GAME_COUNT = 0;

int GLOBAL_SHOPLANE_ROT = 0;

IntDict AList = new IntDict( // Achievements
  new String[] {"FIRSTGAME", "10COINS", "50COINS", "1PURCHASE"},
  new int[]    {0          ,  0       , 0        , 0          }
);
IntDict PList = new IntDict(
  new String[] {"LANEBLUE", "LANEGREEN", "LANEPURPLE"},
  new int[]    {0          ,  0       , 0            }
);

public void setup()
{
   frameRate(GLOBAL_FRAMERATE);
  gameload();

  menu = new Menu();
  shop = new Shop();

  player = new Player(
    new PVector(0, GLOBAL_PLAYER_MIN_HEIGHT, 0),
    new PVector(50, 50, 50)
  );

  GLOBAL_CAMERA = new PVector(width/2, (height/4)*3, 0);
  
  scoreboard_runscore = new TextHolder(
    "Score - " + GLOBAL_RUN_SCORE,
    new PVector(GLOBAL_WIDTH_BUFFER, GLOBAL_HEIGHT_BUFFER*2),
    32,
    "LEFT"
  );
  scoreboard_highscore = new TextHolder(
    GLOBAL_HIGH_SCORE + " - Best",
    new PVector(width-GLOBAL_WIDTH_BUFFER, GLOBAL_HEIGHT_BUFFER*2),
    32,
    "RIGHT"
  );
  scoreboard_coincount = new TextHolder(
    "Coins - " + GLOBAL_COIN_COUNT,
    new PVector(GLOBAL_WIDTH_BUFFER, GLOBAL_HEIGHT_BUFFER*2+32),
    32,
    "LEFT"
  );
  scoreboard_fps = new TextHolder(
    floor(frameRate) + "FPS",
    new PVector(width, GLOBAL_HEIGHT_BUFFER/2),
    16,
    "RIGHT"
  );

  GLOBAL_IMAGE_BACK_ARROW = loadImage("./img/back_arrow.png");
  
  // Populate Menu Lanes
  for (int z = GLOBAL_LANE_MIN_ZPOS; z <= GLOBAL_LANE_MAX_ZPOS; z += GLOBAL_LANELENGTH)
  {
    spawnMenuLanes(z);
  }
  spawnShopLanes();
}

public void draw()
{
  background(0);
  renderGlobalTexts();

  if (GLOBAL_MENU_ACTIVE)
  {
    //render lanes
    push();
      translate(width/2, (height/4)*3, 0); rotateX(radians(-20));
      renderMenuLanes();
    pop();
    menu.render();
  }
  else if (GLOBAL_SHOP_ACTIVE)
  {
      push();
        translate(width/2, (height/4)*3, -50); rotateX(radians(-20));
        renderShopLanes();
      pop();
      shop.render();
  }
  else if (GLOBAL_GAME_RUNNING)
  {
    push();
      translate(GLOBAL_CAMERA.x, GLOBAL_CAMERA.y, GLOBAL_CAMERA.z); rotateX(radians(-20));
      updateCamera();
      renderLanes();
      cleanLanes();

      player.move();
      player.render();
    pop();
  }

  checkAchivements();
  renderToasts();
}

// Basic Render
public void renderLanes()
{
  for (Lane l : vis_lanes)
  {
    l.move(GLOBAL_SPEED);
    l.checkcollide(player);
    l.render();
  }
  GLOBAL_SPEEDCLOCK += GLOBAL_SPEED;
  if (GLOBAL_SPEEDCLOCK == GLOBAL_LANELENGTH)
  {
    GLOBAL_SPEEDCLOCK = 0;
    spawnLanes();
  }
}
public void renderMenuLanes()
{
  for (Lane l : menu_lanes)
  {
    l.render();
  }
}
public void renderShopLanes()
{
  push();
    GLOBAL_SHOPLANE_ROT -= 1;
    if (GLOBAL_SHOPLANE_ROT == -360) GLOBAL_SHOPLANE_ROT = 0;
    rotateY(radians(GLOBAL_SHOPLANE_ROT));
    for (Lane l : shop_lanes)
    {
      l.render();
    }
    player.render();
  pop();
}

// Stops lag in long games
public void cleanLanes()
{
  // println("Vis Lanes: " + vis_lanes.size());
  for(int i = vis_lanes.size()-1; i >= 0; i--)
  {
    if (vis_lanes.get(i).pos.z > GLOBAL_LANE_MAX_ZPOS)
    {
      vis_lanes.remove(i);
    }
  }
}

// Infinite gen
public void spawnLanes() { spawnLanes(GLOBAL_LANE_MIN_ZPOS); }
public void spawnLanes(int pSpawnZ)
{
  Lane laneL, laneM, laneR;
  // Default Spawn Lanes
  int laneSpawn = floor(random(0, 10));
  if (laneSpawn >= 0 && laneSpawn <= 3) // 30%
  {
    laneL = new Lane(
      new PVector(-GLOBAL_LANE_DIST, 0, pSpawnZ),
      new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH)
    );
    laneM = new Lane(
      new PVector(0, 0, pSpawnZ),
      new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH)
    );
    laneR = new Lane(
      new PVector(GLOBAL_LANE_DIST, 0, pSpawnZ),
      new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH)
    );
  }
  else if (laneSpawn == 4) // 10%
  {
    laneL = new Lane(
      new PVector(-GLOBAL_LANE_DIST, 0, pSpawnZ),
      new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH),
      3
    );
    laneM = new Lane(
      new PVector(0, 0, pSpawnZ),
      new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH),
      3
    );
    laneR = new Lane(
      new PVector(GLOBAL_LANE_DIST, 0, pSpawnZ),
      new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH),
      3
    );
  }
  else if (laneSpawn == 8) // 10%
  {
    laneL = new Lane(
      new PVector(-GLOBAL_LANE_DIST, 0, pSpawnZ),
      new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH),
      4
    );
    laneM = new Lane(
      new PVector(0, 0, pSpawnZ),
      new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH),
      4
    );
    laneR = new Lane(
      new PVector(GLOBAL_LANE_DIST, 0, pSpawnZ),
      new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH),
      4
    );
  }
  else if (laneSpawn >= 5 && laneSpawn <= 7) // 30%
  {
    laneL = new Lane(
      new PVector(-GLOBAL_LANE_DIST, 0, pSpawnZ),
      new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH),
      1
    );
    laneM = new Lane(
      new PVector(0, 0, pSpawnZ),
      new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH),
      1
    );
    laneR = new Lane(
      new PVector(GLOBAL_LANE_DIST, 0, pSpawnZ),
      new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH),
      1
    );
  }
  else if (laneSpawn == 9) // 10%
  {
    int wallLane1 = floor(random(0, 3));
    int wallLane2 = floor(random(0, 3));
    laneL = new Lane(
      new PVector(-GLOBAL_LANE_DIST, 0, pSpawnZ),
      new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH),
      wallLane1 == 0 || wallLane2 == 0 ? 2 : 1
    );
    laneM = new Lane(
      new PVector(0, 0, pSpawnZ),
      new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH),
      wallLane1 == 1 || wallLane2 == 1 ? 2 : 1
    );
    laneR = new Lane(
      new PVector(GLOBAL_LANE_DIST, 0, pSpawnZ),
      new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH),
      wallLane1 == 2 || wallLane2 == 2 ? 2 : 1
    );
  }
  else
  {
    laneL = new Lane(
      new PVector(-GLOBAL_LANE_DIST, 0, pSpawnZ),
      new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH)
    );
    laneM = new Lane(
      new PVector(0, 0, pSpawnZ),
      new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH)
    );
    laneR = new Lane(
      new PVector(GLOBAL_LANE_DIST, 0, pSpawnZ),
      new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH)
    );
  }

  vis_lanes.add(laneL);
  vis_lanes.add(laneM);
  vis_lanes.add(laneR);
  GLOBAL_RUN_SCORE += 1;
}
public void spawnDLanes(int pSpawnZ)
{
  Lane laneL, laneM, laneR;
  laneL = new Lane(
    new PVector(-GLOBAL_LANE_DIST, 0, pSpawnZ),
    new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH),
    0
  );
  laneM = new Lane(
    new PVector(0, 0, pSpawnZ),
    new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH),
    0
  );
  laneR = new Lane(
    new PVector(GLOBAL_LANE_DIST, 0, pSpawnZ),
    new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH),
    0
  );
  vis_lanes.add(laneL);
  vis_lanes.add(laneM);
  vis_lanes.add(laneR);
}
public void spawnMenuLanes(int pSpawnZ)
{
  Lane laneL, laneM, laneR;
  // Default Spawn Lanes
  laneL = new Lane(
    new PVector(-GLOBAL_LANE_DIST, 0, pSpawnZ),
    new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH)
  );
  laneM = new Lane(
    new PVector(0, 0, pSpawnZ),
    new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH)
  );
  laneR = new Lane(
    new PVector(GLOBAL_LANE_DIST, 0, pSpawnZ),
    new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH)
  );
  menu_lanes.add(laneL);
  menu_lanes.add(laneM);
  menu_lanes.add(laneR);
  // Spawn Gaps

}
public void spawnShopLanes()
{
  Lane laneL, laneM, laneR;
  // Default Spawn Lanes
  laneL = new Lane(
    new PVector(-GLOBAL_LANE_DIST, 0, 0),
    new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH)
  );
  laneM = new Lane(
    new PVector(0, 0, 0),
    new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH)
  );
  laneR = new Lane(
    new PVector(GLOBAL_LANE_DIST, 0, 0),
    new PVector(GLOBAL_LANEWIDTH, GLOBAL_LANEHEIGHT, GLOBAL_LANELENGTH)
  );
  shop_lanes.add(laneL);
  shop_lanes.add(laneM);
  shop_lanes.add(laneR);
}

// Smooth Camera
public void updateCamera()
{
  // println("Current: " + GLOBAL_CAMERA.x);
  // println("Target: " + GLOBAL_CAMERA_TARGET);
  if (GLOBAL_CAMERA_ANI == true)
  {
    GLOBAL_CAMERA.x += GLOBAL_CAMERA_DIR * GLOBAL_CAMERA_SPEED;
    if (GLOBAL_CAMERA.x == GLOBAL_CAMERA_TARGET)
    {
      GLOBAL_CAMERA_ANI = false;
      GLOBAL_CAMERA_DIR = 0;
    }
    else
    {
      return;
    }
  }
}

public void newGame()
{
  // Refresh lanes
  vis_lanes = new ArrayList<Lane>();
  for (int z = GLOBAL_LANE_MIN_ZPOS; z <= GLOBAL_LANE_MAX_ZPOS; z += GLOBAL_LANELENGTH)
  {
    spawnDLanes(z);
  }

  // Refresh player
  player = new Player(
    new PVector(0, GLOBAL_PLAYER_MIN_HEIGHT, 0),
    new PVector(50, 50, 50)
  );

  // Refresh camera
  GLOBAL_CAMERA = new PVector(width/2, (height/4)*3, 0);
  GLOBAL_CAMERA_TARGET = 0;
  GLOBAL_CAMERA_ANI = false;
  GLOBAL_CAMERA_DIR = 0;
  GLOBAL_LANE_FOCUS = 1;
  GLOBAL_SPEEDCLOCK = 0;

  GLOBAL_RUN_SCORE = 0;
  GLOBAL_GAME_COUNT += 1;
}

public void renderGlobalTexts()
{
  for (TextHolder t : onScreenTexts)
  {
    t.render();
  }
  
  scoreboard_runscore.text = "Score - " + GLOBAL_RUN_SCORE;
  scoreboard_runscore.render();
  if (GLOBAL_RUN_SCORE > GLOBAL_HIGH_SCORE)
  {
    GLOBAL_HIGH_SCORE = GLOBAL_RUN_SCORE;
    scoreboard_highscore.text = GLOBAL_HIGH_SCORE + " - Best";
  }
  scoreboard_highscore.render();

  scoreboard_coincount.text = "Coins - " + GLOBAL_COIN_COUNT;
  scoreboard_coincount.render();

  scoreboard_fps.text = floor(frameRate) + "FPS";
  scoreboard_fps.render();
}

public void gameOver()
{
  GLOBAL_MENU_ACTIVE = true;
  GLOBAL_GAME_RUNNING = false;
  gamesave();
}

public void checkAchivements()
{
  if (GLOBAL_GAME_COUNT >= 1 && AList.get("FIRSTGAME") == 0)
  {
    showToast("First Game!", "Thank you for playing " + GLOBAL_GAME_TITLE + ".");
    AList.set("FIRSTGAME", 1);
  }

  if (GLOBAL_COIN_COUNT >= 10 && AList.get("10COINS") == 0) // Unlock Shop
  {
    menu.buttons.get(1).active = true; // Unlock Shop Button
    showToast("Time To Go Shopping!", "Collect 10 Coins.");
    AList.set("10COINS", 1);
  }

  if (GLOBAL_COIN_COUNT >= 50 && AList.get("50COINS") == 0) // Unlock Secret
  {
    showToast("Spare Change.", "Collect 50 Coins.");
    AList.set("50COINS", 1);
  }

  if (GLOBAL_PURCHASE_COUNT >= 1 && AList.get("1PURCHASE") == 0) // Unlock Secret
  {
    showToast("Paint Job.", "Buy 1 Item.");
    AList.set("1PURCHASE", 1);
  }
}

public void renderToasts()
{
  if (toasts.size() >= 1)
  {
    Toast t = toasts.get(0);
    t.render(height/5);
    if (t.time <= 0) toasts.remove(t);
  }
}

public void changeLaneColours(int pColour)
{
  for (Lane l : menu_lanes)
  {
    l.fillcol = pColour;
  }
  for (Lane l : shop_lanes)
  {
    l.fillcol = pColour;
  }
}

public void changeShopColour(int pColour)
{
  for (Lane l : shop_lanes)
  {
    l.fillcol = pColour;
  }
}
public class Button
{
  public PVector pos, size;
  public String code;
  public boolean active = true;

  public StringDict mods;

  public int cost = 0;
  public boolean bought = false;

  public PVector price_buffer = new PVector(5, 5);

  public Button(PVector pos, PVector size, String pCode, StringDict pMods)
  {
    this.pos = pos;
    this.size = size;

    this.code = pCode;
    this.mods = pMods;

    if (this.mods.hasKey("is_active"))
    {
      String val = this.mods.get("is_active");
      if (val == "true") this.active = true;
      else if (val == "false") this.active = false;
    }
    if (this.mods.hasKey("cost"))
    {
      try
      {
        int COST = Integer.parseInt(this.mods.get("cost"));
        this.cost = COST;
      }
      catch(Exception ex)
      { this.cost = 0; }
    }
  }

  public void render()
  {
    push();
      rectMode(CENTER);
      rect(this.pos.x, this.pos.y, this.size.x, this.size.y);
      if (!this.active)
      {
        push();
          imageMode(CENTER);
          image(menu.image_locked_icon, this.pos.x, this.pos.y,
                GLOBAL_BUTTON_SIZE.x, GLOBAL_BUTTON_SIZE.y);
        pop();
      }
      else
      {
        if (this.mods.hasKey("center_shape"))
        {
          switch(this.mods.get("center_shape"))
          {
            case "play_tri":
              push();
                // fill(0, 255, 0); stroke(0); // Prototype 1
                // triangle(this.pos.x-this.size.x/3, this.pos.y-this.size.y/3,
                //          this.pos.x-this.size.x/3, this.pos.y+this.size.y/3,
                //          this.pos.x+this.size.x/3, this.pos.y);
                imageMode(CENTER);
                image(menu.image_play_icon, this.pos.x, this.pos.y,
                      GLOBAL_BUTTON_SIZE.x, GLOBAL_BUTTON_SIZE.y);
              pop();
              break;
            case "item_shop":
              push();
              imageMode(CENTER);
                image(menu.image_shop_icon, this.pos.x, this.pos.y,
                      GLOBAL_BUTTON_SIZE.x, GLOBAL_BUTTON_SIZE.y);
              pop();
              break;
            case "back_arrow":
              push();
              imageMode(CENTER);
                image(GLOBAL_IMAGE_BACK_ARROW, this.pos.x, this.pos.y,
                      GLOBAL_BUTTON_SIZE.x/2, GLOBAL_BUTTON_SIZE.y/2);
              pop();
              break;
            case "sq_blue":
              push();
                rectMode(CENTER); fill(0, 0, 255); stroke(0);
                rect(this.pos.x, this.pos.y, GLOBAL_BUTTON_SIZE.x/2, GLOBAL_BUTTON_SIZE.y/2);
              pop();
              break;
            case "sq_green":
              push();
                rectMode(CENTER); fill(0, 255, 0); stroke(0);
                rect(this.pos.x, this.pos.y, GLOBAL_BUTTON_SIZE.x/2, GLOBAL_BUTTON_SIZE.y/2);
              pop();
              break;
            case "sq_purple":
              push();
                rectMode(CENTER); fill(128, 0, 128); stroke(0);
                rect(this.pos.x, this.pos.y, GLOBAL_BUTTON_SIZE.x/2, GLOBAL_BUTTON_SIZE.y/2);
              pop();
              break;
            case "icon_eye":
              push();
              imageMode(CENTER);
                image(shop.image_eye_icon, this.pos.x, this.pos.y,
                      GLOBAL_BUTTON_SIZE.x/2, GLOBAL_BUTTON_SIZE.y/2);
              pop();
              break;
            default:
              println("Undefined Custom Mod-Key");
              break;
          }
        }
        if (this.cost > 0)
        {
          push();
            image(shop.image_locked_icon,
                  this.pos.x-GLOBAL_BUTTON_SIZE.x/2, this.pos.y+GLOBAL_BUTTON_SIZE.y/2-GLOBAL_BUYLOCKICON_SIZE.y,
                  GLOBAL_BUYLOCKICON_SIZE.x, GLOBAL_BUYLOCKICON_SIZE.y);
            textAlign(RIGHT); stroke(0); fill(0); textSize(24);
            text("$" + this.cost,
                 this.pos.x+GLOBAL_BUTTON_SIZE.x/2-this.price_buffer.x,
                 this.pos.y+GLOBAL_BUTTON_SIZE.y/2-this.price_buffer.y);
          pop();
        }
      }
    pop();
  }

  public boolean testClicked(PVector mpos)
  {
    // println(String.format("Clicking -> " + mpos + "[%s, %s, %s][%s, %s, %s]", this.pos.x-this.size.x/2, this.pos.y-this.size.y/2, this.pos.z-this.size.z/2, this.pos.x+this.size.x/2, this.pos.y+this.size.y/2, this.pos.z+this.size.z/2));
    if (mpos.x >= this.pos.x-this.size.x/2 && mpos.x <= this.pos.x+this.size.x/2
    &&  mpos.y >= this.pos.y-this.size.y/2 && mpos.y <= this.pos.y+this.size.y/2
    &&  this.active)
    {
      // println("TRUE");
      return true;
    }
    else
    {
      // println("FALSE");
      return false;
    }
  }

  public void click()
  {
    if (this.active && GLOBAL_COIN_COUNT >= this.cost) {
      switch(this.code)
      {
        case "START_GAME":
          GLOBAL_MENU_ACTIVE = false;
          GLOBAL_GAME_RUNNING = true;
          newGame();
          break;
        case "START_SHOP":
          player = new Player(
            new PVector(0, GLOBAL_PLAYER_MIN_HEIGHT, 0),
            new PVector(50, 50, 50)
          );
          GLOBAL_MENU_ACTIVE = false;
          GLOBAL_SHOP_ACTIVE = true;
          changeShopColour(GLOBAL_LANE_COLOUR);
          break;
        case "BACK_MENU":
          GLOBAL_MENU_ACTIVE = true;
          GLOBAL_SHOP_ACTIVE = false;
          break;
        case "BUY_LANE_BLUE":
          if (GLOBAL_LANE_COLOUR_SELECTED != 1) {
            if (this.cost > 0) GLOBAL_PURCHASE_COUNT += 1;
            PList.set("LANEBLUE", 1);
            GLOBAL_LANE_COLOUR_SELECTED = 1;
            GLOBAL_LANE_COLOUR = color(0, 0, 255);
            changeLaneColours(color(0, 0, 255));
          } else {
            GLOBAL_LANE_COLOUR_SELECTED = 0;
            GLOBAL_LANE_COLOUR = color(255);
            changeLaneColours(color(255));
          }
          break;
        case "BUY_LANE_GREEN":
          if (GLOBAL_LANE_COLOUR_SELECTED != 2) {
            if (this.cost > 0) GLOBAL_PURCHASE_COUNT += 1;
            PList.set("LANEGREEN", 1);
            GLOBAL_LANE_COLOUR_SELECTED = 2;
            GLOBAL_LANE_COLOUR = color(0, 255, 0);
            changeLaneColours(color(0, 255, 0));
          } else {
            GLOBAL_LANE_COLOUR_SELECTED = 0;
            GLOBAL_LANE_COLOUR = color(255);
            changeLaneColours(color(255));
          }
          break;
        case "BUY_LANE_PURPLE":
          if (GLOBAL_LANE_COLOUR_SELECTED != 3) {
            if (this.cost > 0) GLOBAL_PURCHASE_COUNT += 1;
            PList.set("LANEPURPLE", 1);
            GLOBAL_LANE_COLOUR_SELECTED = 3;
            GLOBAL_LANE_COLOUR = color(128,0,128);
            changeLaneColours(color(128,0,128));
          } else {
            GLOBAL_LANE_COLOUR_SELECTED = 0;
            GLOBAL_LANE_COLOUR = color(255);
            changeLaneColours(color(255));
          }
          break;
        case "PREV_LANE_BLUE":
          changeShopColour(color(0, 0, 255));
          break;
        case "PREV_LANE_GREEN":
          changeShopColour(color(0, 255, 0));
          break;
        case "PREV_LANE_PURPLE":
          changeShopColour(color(128, 0, 128));
          break;
      }
      GLOBAL_COIN_COUNT -= this.cost;
      this.cost = 0;
    }
  }
}
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

  public int fillcol = GLOBAL_LANE_COLOUR;

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
public class Menu
{
  ArrayList<Button> buttons = new ArrayList<Button>();
  ArrayList<TextHolder> texts = new ArrayList<TextHolder>();

  public PImage image_play_icon;
  public PImage image_shop_icon;
  public PImage image_locked_icon;

  public Menu()
  {
    image_play_icon   = loadImage("./img/play.png");
    image_shop_icon   = loadImage("./img/shop.png");
    image_locked_icon = loadImage("./img/lock.png");
    
    this.buttons.add(
      new Button(
        new PVector(width/2, height/2-GLOBAL_BUTTON_SIZE.y),
        new PVector(GLOBAL_BUTTON_SIZE.x, GLOBAL_BUTTON_SIZE.y),
        "START_GAME",
        new StringDict(new String[]{"center_shape"}, new String[]{"play_tri"})
      )
    );
    if (GLOBAL_COIN_COUNT < 10) this.buttons.add(
      new Button(
        new PVector(width/2, height/2+GLOBAL_BUTTON_SIZE.y),
        new PVector(GLOBAL_BUTTON_SIZE.x, GLOBAL_BUTTON_SIZE.y),
        "START_SHOP",
        new StringDict(new String[]{"center_shape", "is_active"}, new String[]{"item_shop", "false"})
      )
    );
    else this.buttons.add(
      new Button(
        new PVector(width/2, height/2+GLOBAL_BUTTON_SIZE.y),
        new PVector(GLOBAL_BUTTON_SIZE.x, GLOBAL_BUTTON_SIZE.y),
        "START_SHOP",
        new StringDict(new String[]{"center_shape"}, new String[]{"item_shop"})
      )
    );

    this.texts.add(
      new TextHolder(
        GLOBAL_GAME_TITLE,
        new PVector(width/2, height/5),
        64,
        "CENTER"
      )
    );
  }

  public void render()
  {
    // render buttons
    for (Button b : this.buttons)
    {
      b.render();
    }
    for (TextHolder t : this.texts)
    {
      t.render();
    }
  }
}
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

  public int fillcol = color(0, 255, 0);

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
public class Shop
{
  ArrayList<Button> buttons = new ArrayList<Button>();
  ArrayList<TextHolder> texts = new ArrayList<TextHolder>();

  public PImage image_locked_icon;
  public PImage image_eye_icon;

  public Shop()
  {
    image_locked_icon = loadImage("./img/lock.png");
    image_eye_icon = loadImage("./img/eye.png");

    // Back Arrow
    this.buttons.add(
      new Button(
        new PVector(GLOBAL_WIDTH_BUFFER+GLOBAL_BUTTON_SIZE.x/2, height/5-GLOBAL_BUTTON_SIZE.y/4),
        new PVector(GLOBAL_BUTTON_SIZE.x/2, GLOBAL_BUTTON_SIZE.y/2),
        "BACK_MENU",
        new StringDict(new String[]{"center_shape"}, new String[]{"back_arrow"})
      )
    );

    // Lane Paints
    if (PList.get("LANEBLUE") == 0) this.buttons.add(
      new Button(
        new PVector(width/2-GLOBAL_BUTTON_SIZE.x-GLOBAL_WIDTH_BUFFER, height/2-GLOBAL_BUTTON_SIZE.y),
        new PVector(GLOBAL_BUTTON_SIZE.x, GLOBAL_BUTTON_SIZE.y),
        "BUY_LANE_BLUE",
        new StringDict(new String[]{"center_shape", "cost"}, new String[]{"sq_blue", "10"})
      )
    );
    else this.buttons.add(
      new Button(
        new PVector(width/2-GLOBAL_BUTTON_SIZE.x-GLOBAL_WIDTH_BUFFER, height/2-GLOBAL_BUTTON_SIZE.y),
        new PVector(GLOBAL_BUTTON_SIZE.x, GLOBAL_BUTTON_SIZE.y),
        "BUY_LANE_BLUE",
        new StringDict(new String[]{"center_shape"}, new String[]{"sq_blue"})
      )
    );
    if (PList.get("LANEGREEN") == 0) this.buttons.add(
      new Button(
        new PVector(width/2, height/2-GLOBAL_BUTTON_SIZE.y),
        new PVector(GLOBAL_BUTTON_SIZE.x, GLOBAL_BUTTON_SIZE.y),
        "BUY_LANE_GREEN",
        new StringDict(new String[]{"center_shape", "cost"}, new String[]{"sq_green", "25"})
      )
    );
    else this.buttons.add(
      new Button(
        new PVector(width/2, height/2-GLOBAL_BUTTON_SIZE.y),
        new PVector(GLOBAL_BUTTON_SIZE.x, GLOBAL_BUTTON_SIZE.y),
        "BUY_LANE_GREEN",
        new StringDict(new String[]{"center_shape"}, new String[]{"sq_green"})
      )
    );
    if (PList.get("LANEPURPLE") == 0) this.buttons.add(
      new Button(
        new PVector(width/2+GLOBAL_BUTTON_SIZE.x+GLOBAL_WIDTH_BUFFER, height/2-GLOBAL_BUTTON_SIZE.y),
        new PVector(GLOBAL_BUTTON_SIZE.x, GLOBAL_BUTTON_SIZE.y),
        "BUY_LANE_PURPLE",
        new StringDict(new String[]{"center_shape", "cost"}, new String[]{"sq_purple", "50"})
      )
    );
    else this.buttons.add(
      new Button(
        new PVector(width/2+GLOBAL_BUTTON_SIZE.x+GLOBAL_WIDTH_BUFFER, height/2-GLOBAL_BUTTON_SIZE.y),
        new PVector(GLOBAL_BUTTON_SIZE.x, GLOBAL_BUTTON_SIZE.y),
        "BUY_LANE_PURPLE",
        new StringDict(new String[]{"center_shape"}, new String[]{"sq_purple"})
      )
    );

    // Preview Buttons
    this.buttons.add(
      new Button(
        new PVector(width/2-GLOBAL_BUTTON_SIZE.x-GLOBAL_WIDTH_BUFFER, height/2),
        new PVector(GLOBAL_BUTTON_SIZE.x/2, GLOBAL_BUTTON_SIZE.y/2),
        "PREV_LANE_BLUE",
        new StringDict(new String[]{"center_shape"}, new String[]{"icon_eye"})
      )
    );
    this.buttons.add(
      new Button(
        new PVector(width/2, height/2),
        new PVector(GLOBAL_BUTTON_SIZE.x/2, GLOBAL_BUTTON_SIZE.y/2),
        "PREV_LANE_GREEN",
        new StringDict(new String[]{"center_shape"}, new String[]{"icon_eye"})
      )
    );
    this.buttons.add(
      new Button(
        new PVector(width/2+GLOBAL_BUTTON_SIZE.x+GLOBAL_WIDTH_BUFFER, height/2),
        new PVector(GLOBAL_BUTTON_SIZE.x/2, GLOBAL_BUTTON_SIZE.y/2),
        "PREV_LANE_PURPLE",
        new StringDict(new String[]{"center_shape"}, new String[]{"icon_eye"})
      )
    );

    // Texts
    this.texts.add(
      new TextHolder(
        "Item Shop",
        new PVector(width/2, height/5),
        64,
        "CENTER"
      )
    );
    this.texts.add(
      new TextHolder(
        "Lane Paints",
        new PVector(width/2-GLOBAL_BUTTON_SIZE.x*1.5f-GLOBAL_WIDTH_BUFFER, height/2-GLOBAL_BUTTON_SIZE.y*1.6f + 5),
        32,
        "LEFT"
      )
    );
  }

  public void render()
  {
    // render buttons
    for (Button b : this.buttons)
    {
      b.render();
    }
    for (TextHolder t : this.texts)
    {
      t.render();
    }
  }
}
public class TextHolder
{
  String text;
  PVector pos;
  int size;
  String alignMode;

  public TextHolder(String pText, PVector pPos, int pSize, String pAlignMode)
  {
    this.text = pText;
    this.pos = pPos;
    this.size = pSize;
    this.alignMode = pAlignMode;
  }

  public void render()
  {
    push();
      if (this.alignMode == "CENTER") { textAlign(CENTER); }
      else if(this.alignMode == "RIGHT") { textAlign(RIGHT); }
      else if(this.alignMode == "LEFT") { textAlign(LEFT); }
      fill(255); stroke(255); textSize(this.size);
      text(this.text, this.pos.x, this.pos.y);
    pop();
  }
}
ArrayList<Toast> toasts = new ArrayList<Toast>();
public class Toast
{
  String title;
  String desc;
  int time;

  public Toast(String pTitle, String pDesc, int pTime)
  {
    this.title = pTitle;
    this.desc = pDesc;
    this.time = pTime;
  }

  public void render(int ypos)
  {
    push();
      rectMode(CENTER);
      stroke(255); fill(0);
      rect(width/2, ypos + GLOBAL_TOAST_SIZE.y/2, GLOBAL_TOAST_SIZE.x, GLOBAL_TOAST_SIZE.y);
      stroke(255); fill(255); textSize(26); textAlign(CENTER);
      text(this.title, width/2, ypos + GLOBAL_TOAST_SIZE.y/3);
      stroke(255); fill(255); textSize(16); textAlign(CENTER);
      text(this.desc, width/2, ypos + GLOBAL_TOAST_SIZE.y/3*2);

    pop();

    this.time -= 1;
  }
}

public void showToast(String pTitle, String pDesc)
{
  toasts.add(
    new Toast(pTitle, pDesc, 3*GLOBAL_FRAMERATE)
  );
}
public void keyPressed()
{ // PC CONTROLS!!
  if (key == '#')
  {
    // Cheats
    GLOBAL_COIN_COUNT = 100;
  }
  else if (key == ESC)
  {
    GLOBAL_MENU_ACTIVE = true;
    GLOBAL_SHOP_ACTIVE = false;
    GLOBAL_GAME_RUNNING = false;

    key = 0;
  }
  else if (GLOBAL_GAME_RUNNING)
  {
    if (key == CODED)
    {
      int keyDir = 0;
      if (keyCode == LEFT && GLOBAL_LANE_FOCUS < 2 && player.canMove)
      {
        GLOBAL_LANE_FOCUS += 1;
        GLOBAL_CAMERA_DIR = 1;

        keyDir = -1;
      }
      else if (keyCode == RIGHT && GLOBAL_LANE_FOCUS > 0 && player.canMove)
      {
        GLOBAL_LANE_FOCUS -= 1;
        GLOBAL_CAMERA_DIR = -1;

        keyDir = 1;
      }
      else if (keyCode == UP)
      {
        player.jumpHeld = true;
      }
      else
      {
        return;
      }

      if (keyDir != 0)
      {
        GLOBAL_CAMERA_ANI = true;
        switch(GLOBAL_LANE_FOCUS)
        {
          case 0:
            GLOBAL_CAMERA_TARGET = (width/2)-GLOBAL_LANE_DIST;
            player.setTarget(GLOBAL_LANE_DIST, keyDir);
            break;
          case 1:
            GLOBAL_CAMERA_TARGET = width/2;
            player.setTarget(0, keyDir);
            break;
          case 2:
            GLOBAL_CAMERA_TARGET = (width/2)+GLOBAL_LANE_DIST;
            player.setTarget(-GLOBAL_LANE_DIST, keyDir);
            break;
        }
      }
    }
  }
}

public void keyReleased()
{
  if (key == CODED)
  {
    if (keyCode == UP)
    {
      player.jumpHeld = false;
    }
  }
}

public void mouseClicked()
{ // MOBILE CONTROLS
  PVector mpos = new PVector(mouseX, mouseY);
  if (mouseButton == LEFT)
  {
    if (GLOBAL_MENU_ACTIVE)
    {
      for (Button b : menu.buttons)
      {
        if (b.testClicked(mpos))
        {
          b.click();
          break;
        }
      }
    }
    else if (GLOBAL_SHOP_ACTIVE)
    {
      for (Button b : shop.buttons)
      {
        if (b.testClicked(mpos))
        {
          b.click();
          break;
        }
      }
    }
  }
}
public void gameload()
{
  try
  {
    // Load File
    String[] lines = loadStrings("./save/local.sav");
    // Set Settings
    GLOBAL_HIGH_SCORE = Integer.parseInt(lines[0]);

    GLOBAL_COIN_COUNT = Integer.parseInt(lines[1]);

    String[] purchases = split(lines[2], ',');
    PList.set("LANEBLUE", Integer.parseInt(purchases[0]));
    PList.set("LANEGREEN", Integer.parseInt(purchases[1]));
    PList.set("LANEPURPLE", Integer.parseInt(purchases[2]));

    String[] achievements = split(lines[3], ',');
    AList.set("FIRSTGAME", Integer.parseInt(achievements[0]));
    AList.set("10COINS", Integer.parseInt(achievements[1]));
    AList.set("50COINS", Integer.parseInt(achievements[2]));
    AList.set("1PURCHASE", Integer.parseInt(achievements[3]));

    GLOBAL_LANE_COLOUR_SELECTED = Integer.parseInt(lines[4]);
  }
  catch(Exception ex)
  {
    println("Load Failed, Factory settings engaged");
  }
}
public void gamesave()
{
  String savefile = "";
  // highscore
  savefile += GLOBAL_HIGH_SCORE + ";";
  // coins
  savefile += GLOBAL_COIN_COUNT + ";";
  
  // Purchases
  for (int i : PList.values())
  { savefile += i + ","; }
  savefile += ";";
  // Achievements
  for (int i : AList.values())
  { savefile += i + ","; }
  savefile += ";";
  //Customisation
  savefile += GLOBAL_LANE_COLOUR_SELECTED +";";

  String[] savefileArr = split(savefile, ';');
  saveStrings("./save/local.sav", savefileArr);
}
  public void settings() {  size(550, 900, P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "main" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
