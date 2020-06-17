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
