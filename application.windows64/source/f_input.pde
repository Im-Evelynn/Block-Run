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
