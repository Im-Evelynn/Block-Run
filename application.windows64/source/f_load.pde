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
