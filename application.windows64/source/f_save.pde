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
