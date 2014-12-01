public class Canvas {
  float x;
  float y;
  float w;
  float h;
  // ArrayList<Canvas> selections;
  
  Canvas(float x, float y, float w, float h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    // this.selections = new ArrayList<Canvas>();
  } 
  
  // void addSelection(float x, float y, float w, float h)
  // {
  //   if (w < 0) {
  //     x += w;
  //     w *= -1; 
  //   }
  //   if (h < 0) {
  //     y += h;
  //     h *= -1; 
  //   }
  //   selections.add(new Canvas(x, y, w, h));
  //   print("ADDED SELECTION\n");
  // }
  
  // void clearSelections()
  // {
  //   selections = new ArrayList<Canvas>(); 
  // }
  
  // void drawSelections()
  // {
  //   stroke(0, 255, 0);
  //   for (int i = 0; i < selections.size(); i++) {
  //     selections.get(i).drawRect(0, 150, 0);
  //   } 
  // }
  
  void update(float x, float y, float w, float h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }
  
  void drawRect(int val)
  {
    stroke(0);
    fill(val);
    strokeWeight(1);
    rect(x, y, w, h); 
  }
  
  void drawRect(float v1, float v2, float v3)
  {
    stroke(0);
    fill(v1, v2, v3);
    strokeWeight(1);
    rect(x, y, w, h); 
  }
  
  boolean mouseOver()
  {
    return covers(mouseX, mouseY); 
  }
  
  boolean covers(float px, float py)
  {
    return (px > x && px < x + w && py > y && py < y + h);
  }
}
