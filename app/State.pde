class State{
    PShape stateShape;
    String stateName;
    int basicColor;
    float size;
    int maxSize;
    boolean isSelected = false;
    FloatDict colors;
    FloatDict stats;






  // JSONObject stateJSON;
  // String stateCode;
  // PShape stateShape;
  // color stateColor = color(255, 204, 100);
  // int xCirc = -1;
  // int yCirc = -1;
  // int stateNum;
 
  State(PShape stateShape, int basicColor, int maxSize) {
    this.stateShape = stateShape;
    this.stateName = stateShape.getName();
    this.basicColor = basicColor;
    this.size = 0;
    this.maxSize = maxSize;
    this.colors = new FloatDict();
    this.stats = new FloatDict();


    // stateJSON = stateData;
    // stateCode = stateJSON.getString("stateCode");
    // stateShape = usa.getChild(stateCode);
    // stateNum = num;
    // xCirc = 38 + 44*(num%24);
    // if (num<24) {
    //   yCirc = 616;
    // } else {
    //   yCirc = 660;
    // }
  }

  void addSize(float size) {
    this.size += size;
  }

  void resetSize() {
      this.size = 0;
  }

  void removeColors() {
    this.colors = new FloatDict();
  }

  void addColor(float strength, int col) {
      if (this.colors.hasKey(str(col))) {
          this.colors.add(str(col), strength);
      }else {
          this.colors.set(str(col), strength);
      }
      if (this.colors.get(str(col)) <= 0) {
          this.colors.remove(str(col));
      }
      this.colors.sortValues();
  }

  void updateStat(Bar bar) {
        float selected = 0.0;
        this.stats = new FloatDict();
        for (String colStr : this.colors.keys()) {
            int thisColor = Integer.parseInt(colStr);
            for (Point p : bar.points) {
                int barColor = p.basicColor;
                if (barColor == thisColor) {
                    float val = this.colors.get(colStr)/this.size;
                    this.stats.set(p.label, val);
                    selected += val;
                }
            }
        }
        this.stats.set("selected part", selected);
        this.stats.sortValuesReverse();
    }

  void display() {
    stateShape.disableStyle();
    fill(50, 50 + 205*this.size/this.maxSize);
    noStroke();
    // stroke(0);
    // smooth(8);
    shape(stateShape);
    this.dispayStats();
  }

    void dispayStats() {
        if (this.isSelected) {
            String textToDisplay = "";
            for (String name : this.stats.keys()) {
                textToDisplay += name + ": " + str(round(this.stats.get(name)*100)) + "%\r\n";
            }
            text(textToDisplay, mouseX, mouseY, 150, 150);
        }
    }   


  void setSelected(boolean newSelected) {
    this.isSelected = newSelected;      
  }
 
  // void drawCircle(){
  //   int radius = int(saturation(stateColor)/6);
  //   if (stateNum==pointedDataState) {
  //     fill(#1FDE45);
  //   }
  //   ellipse(xCirc, yCirc, radius, radius);
  // }

  // boolean overCircle() {
  //   float disX = xCirc - mouseX;
  //   float disY = yCirc - mouseY;
  //   if(sqrt(sq(disX) + sq(disY)) < 40/2 ) {
  //     return true;
  //   } else {
  //     return false;
  //   }
  // }

  // int getAbsoluteData(String date, String crimeType, String crimeWeapon){
  //   JSONObject jsonYear = stateJSON.getJSONObject(date);
  //   JSONObject jsonCrime = jsonYear.getJSONObject(crimeType);
  //   int val = jsonCrime.getInt(crimeWeapon);
  //   return val;
  // }

  // float getDensityData(String date, String crimeType, String crimeWeapon){
  //   int absValue = getAbsoluteData(date, crimeType, crimeWeapon);
  //   JSONObject jsonYear = stateJSON.getJSONObject(date);
  //   int population = jsonYear.getInt("population");
  //   float densityVal = float(absValue)/(float(population)/1000000);
  //   return densityVal;
  // }


  // float getPopulation(String date){
  //   JSONObject jsonYear = stateJSON.getJSONObject(date);
  //   int population = jsonYear.getInt("population");
  //   float val = float(population)/1000000;
  //   return val;
  // }



}