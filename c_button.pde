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