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
        new PVector(width/2-GLOBAL_BUTTON_SIZE.x*1.5-GLOBAL_WIDTH_BUFFER, height/2-GLOBAL_BUTTON_SIZE.y*1.6 + 5),
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
