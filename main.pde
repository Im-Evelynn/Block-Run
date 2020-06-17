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

color GLOBAL_LANE_COLOUR = color(255);
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
  size(550, 900, P3D); frameRate(GLOBAL_FRAMERATE);
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

public void changeLaneColours(color pColour)
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

public void changeShopColour(color pColour)
{
  for (Lane l : shop_lanes)
  {
    l.fillcol = pColour;
  }
}