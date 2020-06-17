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
