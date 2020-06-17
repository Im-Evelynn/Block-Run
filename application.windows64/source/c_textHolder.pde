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
