import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.io.*; 
import java.util.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class parallel extends PApplet {

int INIT_WIDTH = 1200;
int INIT_HEIGHT = 800;

DataGroup[] data;
DataManager dataManager;
// Controller controller;
ParallelCoordsManager graphManager;
// Canvas all_canvas;

MapManager mapManager;
// State[] usaStates;




public void setup()
{
  frame.setResizable(true);
  size(INIT_WIDTH, INIT_HEIGHT, P2D);
  ArrayList<DataGroup> arrayData = getData("../data/preProcessedData.json");
  data = new DataGroup[arrayData.size()];
  data = arrayData.toArray(data);

// String[] categoryNames = getCategoryNames("iris.csv");
// controller = new Controller(data, categoryNames);
  Canvas parallel_canvas = new Canvas(0, height*.4f, width, height*.58f);
  Canvas map_canvas = new Canvas(0, 0, width*.6f, height*.6f);
  graphManager = new ParallelCoordsManager("../data/barsValues.json", parallel_canvas, data, dataManager);
  mapManager = new MapManager("usa-wikipedia.svg", "../data/barsValues.json", map_canvas);
  dataManager = new DataManager(data, graphManager, mapManager);
  dataManager.start();
  graphManager.dataManager = dataManager;
  dataManager.setNewColorSource(6);

}



public void draw()
{
  background(245);
  graphManager.display();
  fill(0, 102, 153);
  text(str(floor(frameRate)), 10, 30);
  text(str(mouseX), 10, 50);
  this.mapManager.display();
}

public ArrayList<DataGroup> getData(String file)
{
  ArrayList<DataGroup> datagroups = new ArrayList<DataGroup>();
  JSONArray values = loadJSONArray(file);
  int len = values.size();
  for (int i=0; i<len; ++i) {
    datagroups.add(new DataGroup(values.getJSONObject(i)));
  }
return datagroups;
}


public void mousePressed() {
  graphManager.mousePressed();
}

public void mouseMoved() {
  graphManager.mouseMoved();
}

// void initStates() {
//   usa = loadShape("usa-wikipedia.svg");
// }




public class Bar  {
    float x;
    float y;
    float h;
    float minV;
    float maxV;
    // Field categoryField;
    String label;
    Point[] points;

    boolean isColorSrc = false;




    public Bar (float x, float y, float h, String label) {
        this.x = x;
        this.y = y;
        this.h = h;
        this.label = label;
    }

    public void update(float x, float y, float h){
        this.x = x;
        this.y = y;
        this.h = h;
        this.isColorSrc = false;
    }

    public void setIsColorSrc(boolean isNew){
        this.isColorSrc = isNew;
    }

    public void display() {
        fill(0);
        stroke(0);
        textAlign(CENTER, TOP);
        text(this.label, this.x, this.y+this.h+2); 
        line(this.x, this.y, this.x, this.h + this.y);
        fill(100, 130, 150);
        if (this.isColorSrc){
            fill(100,200);
        }
        rect(this.x - 5, this.y, 10, this.h);
    }

    public boolean isMouseOver() {
        if (mouseX < this.x+5 && mouseX > this.x-5 &&
            mouseY > this.y   && mouseY < this.y+this.h) {
            return true;
        }
        return false;
    }

    public Point getPoint(String category) {
    // float[] getPoint(String category) throws Exception{
        // throw new Exception("not implemented");
        // throw new RemoteException();
        // return new float[] {.0, .0};
        return null;
    }

    public Point getPoint(float val) {
    // float[] getPoint(float val) throws Exception{
        // throw new Exception("not implemented");
        // throw new RemoteException();
        // return new float[] {.0, .0};
        return null;
    }
    
    public String getLabel(float val) {
        return null;
    }

}

public class ValueBar extends Bar{

    float minV;
    float maxV;
    int pointLen = 20;

    public ValueBar (float x, float y, float h, 
                          String categoryName, float minV, 
                          float maxV) {
        super(x, y, h, categoryName);
        this.update(x, y, h, minV, maxV);
    }

    public void update(float x, float y, float h, float minV, float maxV){
        super.update(x, y, h);
        this.minV = minV;
        this.maxV = maxV;
        this.points = new Point[this.pointLen];
        colorMode(HSB, 255);
        for (int i = 0; i < this.pointLen; ++i) {
            float c = i*180/(this.pointLen-1);
            float y_point = map(i, 0, this.pointLen-1, .95f*this.h, .05f*this.h) + this.y;
            String pointLabel = str(floor(map(i,  0, this.pointLen-1, this.minV, this.maxV)));
            points[i] = new Point(x, y_point, color(c, 200, 255), pointLabel);
        }
        colorMode(RGB, 255);
    }

    public Point getPoint(float val) {
        int index = floor(map(val, this.minV, this.maxV, 0, this.pointLen-1));
        return this.points[index];
    }

    public String getLabel(float val) {
        Point p = this.getPoint(val);
        return this.label + p.label;
    }

}

public class CategoryBar extends Bar {

    IntDict categories;

    public CategoryBar (float x, float y, float h, String categoryName, JSONArray categories) {
        super(x, y, h, categoryName);
        this.categories = new IntDict();
        update(x, y, h, categories);
    }

    public void update(float x, float y, float h, JSONArray categories) {
        super.update(x, y, h);
        int len = categories.size();
        float distance = this.h*.9f/(len - 1);
        this.points = new Point[len];
        colorMode(HSB, 255);
        for (int i = 0; i < len; ++i) {
            this.categories.set(categories.getString(i), i);
            float c = (i)*180/(len);
            float y_point = .05f*h + i*distance + this.y;
            points[i] = new Point(x, y_point, color(c, 200, 255), categories.getString(i));
        }
        colorMode(RGB, 255);
    }

    public Point getPoint(String category) {
        int index = this.categories.get(category);
        return this.points[index];
    }
    
    // void draw() {
    //     super.draw();
    //     fill(0);
    //     textAlign(CENTER, TOP);
    //     for (int i = 0; i < this.categories.size(); ++i) {
            
    //     }
    //     for (String label : this.categories.keys()) {
    //         text(label, this.x, this.categories.get(label));
    //     }
    // }

}
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
  
  public void update(float x, float y, float w, float h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }
  
  public void drawRect(int val)
  {
    stroke(0);
    fill(val);
    strokeWeight(1);
    rect(x, y, w, h); 
  }
  
  public void drawRect(float v1, float v2, float v3)
  {
    stroke(0);
    fill(v1, v2, v3);
    strokeWeight(1);
    rect(x, y, w, h); 
  }
  
  public boolean mouseOver()
  {
    return covers(mouseX, mouseY); 
  }
  
  public boolean covers(float px, float py)
  {
    return (px > x && px < x + w && py > y && py < y + h);
  }
}

public class DataGroup  {
    String status;
    String gender;
    String age;
    String device;
    SpecificUser[] users;

    public DataGroup (JSONObject jsonGroup) {
      this.gender = jsonGroup.getString("gender");
      this.status = jsonGroup.getString("status");
      this.device = jsonGroup.getString("device");
      this.age = jsonGroup.getString("age");

      JSONArray jsonUsers = jsonGroup.getJSONArray("specificUsers");
      this.users = new SpecificUser[jsonUsers.size()];
      for (int i = 0; i < jsonUsers.size(); ++i) {
        this.users[i] = new SpecificUser(jsonUsers.getJSONObject(i));
      }
    }

}

public class SpecificUser  {
    String[] viewed;
    String[] founded;
    int time;
    float amount;
    String location;

    public SpecificUser (JSONObject jsonUser) {
        JSONObject userInteraction = jsonUser.getJSONObject("interaction");
        JSONArray viewed = userInteraction.getJSONArray("viewed");
        this.viewed = new String[viewed.size()];
        for (int i = 0; i < viewed.size(); ++i) {
            this.viewed[i] = viewed.getString(i);
        }
        JSONArray founded = userInteraction.getJSONArray("founded");
        this.founded = new String[founded.size()];
        for (int i = 0; i < founded.size(); ++i) {
            this.founded[i] = founded.getString(i);
        }
        this.time = userInteraction.getInt("time");
        this.amount = userInteraction.getInt("amount");
        this.location = jsonUser.getString("location");
    }

}

public class DataManager implements Runnable {
    private Thread t;
    boolean doRun = true;
    boolean doUpdate = false;
    DataGroup[] data;
    ParallelCoordsManager graphManager;
    MapManager mapManager;
    int selectedBar = 0;
    IntDict filter;

    DataManager(DataGroup[] data, ParallelCoordsManager graphManager, MapManager mapManager){
        this.data = data;
        this.graphManager = graphManager;
        this.mapManager = mapManager;
        this.filter = new IntDict();
    }
    public void run() {
        try {
            Thread.sleep(5);
            this.init();
            while (this.doRun) {
                if (this.doUpdate) {
                    this.update();
                }
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
        }
    }

    public void init() {
        String key;
        Line l;
        for (DataGroup dataGroup : this.data) {
            for (SpecificUser user : dataGroup.users) {
                String[] keys = {
                        "status" + dataGroup.status,
                        "gender" + dataGroup.gender,
                        "device" + dataGroup.device,
                        "age" + dataGroup.age,
                    };
                for (int i = 0; i < 3; ++i) {
                    key = keys[i] + keys[i+1];
                    l = this.graphManager.getLine(key);
                    l.addSize(1);
                }
                for (String founded : user.founded) {
                    key = keys[3] + "categoriesfounded " + founded;
                    l = this.graphManager.getLine(key);
                    l.addSize(1);
                    key = "categoriesfounded " + founded + this.graphManager.bars[5].getLabel(user.time);
                    l = this.graphManager.getLine(key);
                    l.addSize(1);
                }
                for (String viewed : user.viewed) {
                    key = keys[3] + "categoriesviewed " + viewed;
                    l = this.graphManager.getLine(key);
                    l.addSize(1);
                    key = "categoriesviewed " + viewed + this.graphManager.bars[5].getLabel(user.time);
                    l = this.graphManager.getLine(key);
                    l.addSize(1);
                }
                key = this.graphManager.bars[5].getLabel(user.time) + this.graphManager.bars[6].getLabel(user.amount);
                l = this.graphManager.getLine(key);
                l.addSize(1);
                this.mapManager.states.get(user.location).addSize(1);

            }
        }
        println("done init");
    }


    public void update() {
        for (Line line : graphManager.lines) {
            line.removeColors();
        }
        for (State state : this.mapManager.states.values()) {
            state.resetSize();
        }
        // String key;
        float m_avr = 0.0f;
        int start_millis = millis();
        int i_millis = 0;
        for (DataGroup dataGroup : this.data) {
            // String[] keys = {
            //         "status" + dataGroup.status,
            //         "gender" + dataGroup.gender,
            //         "device" + dataGroup.device,
            //         "age" + dataGroup.age,
            //     };
            int[] colors = new int[1];
            int[] filteredColor = new int[1];
            if (this.selectedBar <= 3) {
                colors = this.getDataGroupColor(dataGroup);
            }
            start_millis = millis();
            for (SpecificUser user : dataGroup.users) {
                if (this.selectedBar > 3) {
                    colors = this.getUserColor(user);
                }
                // filteredColor = colors;
                filteredColor = this.filterColors(user, dataGroup, colors);
                Line[] simpleLines = this.getSimpleLines(dataGroup, user);
                Line[] categoryLines = this.getCategoryLines(dataGroup, user);

                State state = this.mapManager.states.get(user.location);

                for (int col : filteredColor) {
                    if (col != color(170, 100)) {
                        state.addSize(1.0f/filteredColor.length);
                        // state.addColor(1.0/filteredColor.length, col);
                    }
                }


                for (Line l : simpleLines) {
                    for (int i = 0; i < filteredColor.length; ++i) {
                        float f = 1.0f/filteredColor.length;
                        l.addColor(f, filteredColor[i]);
                        // l.addColor(1.0/filteredColor.length, filteredColor[i]);
                    }
                    // for (int col : colors) {
                    //     l.addColor(1.0/colors.length, col);
                    // }
                }
                if (filteredColor.length == 1) {
                    for (Line l : categoryLines) {
                        l.addColor(1, filteredColor[0]);
                    }
                } else {
                    for (int i = 0; i < filteredColor.length; ++i) {
                        try {
                            
                            
                        categoryLines[2*i].addColor(1,filteredColor[i]);
                        categoryLines[2*i+1].addColor(1,filteredColor[i]);
                        } catch (ArrayIndexOutOfBoundsException e) {
                        }
                    }
                }
            }
            // int userMillis = millis() - start_millis;
            // if (userMillis < 15) {
            //     try {
            //         Thread.sleep(15 - userMillis);
            //     } catch (InterruptedException e) {}
            // }
            // m_avr = (m_avr*i_millis + millis() - start_millis)/(++i_millis);
        }
        this.doUpdate = false;
        this.graphManager.updatePointStats();
        Bar colorSrcBar = this.graphManager.bars[this.selectedBar];
    }

    public int[] filterColors(SpecificUser user, DataGroup dataGroup, int[] colors_src) {
        int[] colors = new int[colors_src.length];
        System.arraycopy(colors_src, 0, colors, 0, colors.length );
        if (filter.size() == 0) {
            return colors;
        } else {
            IntDict checkDict = new IntDict();
            Point[] points = this.getFullUserPoints(dataGroup, user);
            for (int i : filter.values()) {
                checkDict.set(str(i), 0);
            }
            for (Point p : points) {
                if (filter.hasKey(p.label)) {
                    String checkKey = str(filter.get(p.label));
                    checkDict.set(checkKey, 1);
                }
            }
            for (int val : checkDict.values()) {
                if (val == 0) {
                    return new int[] {color(170, 100)}; 
                }
            }
            StringList categories = new StringList();
            for (String barCategory : filter.keys()) {
                if (filter.get(barCategory) == 4) {
                    categories.append(barCategory);
                }
            }
            if (categories.size() > 0) {
                int len = user.founded.length + user.viewed.length;
                int col = colors[0];
                if (colors.length <= 1) {
                    colors = new int[len];
                    for (int i = 0; i < len; ++i) {
                        colors[i] = col;
                    }
                }
                int i = 0;
                for (String viewed : user.viewed) {
                    String key = "viewed " + viewed;
                    if (!categories.hasValue(key)) {
                        colors[i] = color(170, 100);
                    }
                    i++;
                }
                for (String founded : user.founded) {
                    String key = "founded " + founded;
                    if (!categories.hasValue(key)) {
                        colors[i] = color(170, 100);
                    }
                    i++;
                }
            }
            return colors;
        }
    }


    public void removeFilter(String category) {
        this.filter.remove(category);
    }

    public void removeAllFilters() {
        this.filter = new IntDict();
    }

    public Line[] getSimpleLines(DataGroup dataGroup, SpecificUser user) {  
        String key;  
        Line[] lines = new Line[4];
        String[] keys = {
                    "status" + dataGroup.status,
                    "gender" + dataGroup.gender,
                    "device" + dataGroup.device,
                    "age" + dataGroup.age,
                };
        for (int i = 0; i < 3; ++i) {
            key = keys[i] + keys[i+1];
            lines[i] = this.graphManager.getLine(key);
        }
        key = this.graphManager.bars[5].getLabel(user.time) + this.graphManager.bars[6].getLabel(user.amount);
        lines[3] = this.graphManager.getLine(key);
        return lines;
    }

    public Line[] getCategoryLines(DataGroup dataGroup, SpecificUser user) {
        int len = user.founded.length + user.viewed.length;
        Line[] lines = new Line[2*len];
        int i = 0;
        String key;
        for (String viewed : user.viewed) {
            key = "age" + dataGroup.age + "categoriesviewed " + viewed;
            lines[i] = this.graphManager.getLine(key);
            i++;
            key = "categoriesviewed " + viewed + this.graphManager.bars[5].getLabel(user.time);
            lines[i] = this.graphManager.getLine(key);
            i++;
        }
        for (String founded : user.founded) {
            key = "age" + dataGroup.age + "categoriesfounded " + founded;
            lines[i] = this.graphManager.getLine(key);
            i++;
            key = "categoriesfounded " + founded + this.graphManager.bars[5].getLabel(user.time);
            lines[i] = this.graphManager.getLine(key);
            i++;
        }
        return lines;
    }

    public int[] getDataGroupColor(DataGroup dataGroup) {
        int[] colors = new int[1];
        colors[0] = getDataGroupPoints(dataGroup)[this.selectedBar].basicColor;
        return colors;
    }

    public int[] getUserColor(SpecificUser user) {
        int[] colors = new int[1];;
        if (this.selectedBar > 4) {
            colors[0] = getUserValuePoints(user)[this.selectedBar-5].basicColor;
        } else if (this.selectedBar == 4) {
            Point[] categoryPoints = this.getUserCategoryPoints(user);
            int len = categoryPoints.length;
            colors = new int[categoryPoints.length];
            for (int i = 0; i < len; ++i) {
                colors[i] = categoryPoints[i].basicColor;
                // println("categoryPoints[i].label: "+categoryPoints[i].label);
            }
            
        }
        return colors;
    }

    public Point[] getFullUserPoints(DataGroup dataGroup, SpecificUser user) {
        Point[] p1 = getDataGroupPoints(dataGroup);
        Point[] p2 = getUserCategoryPoints(user);
        Point[] p3 = getUserValuePoints(user);
        int len = p1.length + p2.length + p3.length;
        Point[] p = new Point[len];
        System.arraycopy(p1, 0, p, 0, p1.length);
        System.arraycopy(p2, 0, p, p1.length, p2.length);
        System.arraycopy(p3, 0, p, p1.length + p2.length, p3.length);
        return p;
    }

    public Point[] getDataGroupPoints(DataGroup dataGroup) {
        Point[] points = new Point[4];
        String[] keys = {dataGroup.status,
                         dataGroup.gender,
                         dataGroup.device,
                         dataGroup.age};

        for (int i = 0; i < 4; ++i) {
            points[i] = this.graphManager.bars[i].getPoint(keys[i]);
        }
        return points;
    }

    public Point[] getUserCategoryPoints(SpecificUser user) {
        int len = user.viewed.length + user.founded.length;
        Point[] points = new Point[len];
        int i = 0;
        for (String viewed : user.viewed) {
            String key = "viewed " + viewed;
            points[i] = this.graphManager.bars[4].getPoint(key);
            i++;
        }
        for (String founded : user.founded) {
            String key = "founded " + founded;
            points[i] = this.graphManager.bars[4].getPoint(key);
            i++;
        }
        return points;
    }

    public Point[] getUserValuePoints(SpecificUser user) {
        Point[] points = new Point[2];
        points[0] = this.graphManager.bars[5].getPoint(user.time);
        points[1] = this.graphManager.bars[6].getPoint(user.amount);
        return points;
    }

    public void setNewColorSource(int selectedBar) {
        if (!this.doUpdate) {
            for (Line line : graphManager.lines) {
                line.removeColors();
            }
            this.selectedBar = selectedBar;
            this.doUpdate = true;
        }
    }

    public boolean addFilter(String category, int bar) {
        println("this.doUpdate: "+this.doUpdate);
        if (!this.doUpdate) {
            this.setNewColorSource(bar);
            if (this.filter.hasKey(category)) {
                this.removeFilter(category);
                return false;
            } else {
                this.filter.add(category, bar);
                return true;
            }
        } else {
            return this.filter.hasKey(category);
        }
    }

    public void start ()
    {
        if (t == null)
        {
            t = new Thread (this, "Data Manager");
            t.start ();
        }
    }

}
public class Line    {
    Point p1;
    Point p2;
    int size;
    int colorSize;
    FloatDict colors;
    float thikness;
    boolean updateBothPoints;

    public Line (Point p1, Point p2, boolean updateBothPoints) {
        this.p1 = p1;
        this.p2 = p2;
        this.size = 0;
        this.colorSize = 0;

        this.colors = new FloatDict();
        this.thikness = 0;
        this.updateBothPoints = updateBothPoints;
    }

    public void addSize(int size) {
        this.size += size;
        this.thikness = ceil((float)this.size/300);
        this.p2.addSize(size);
        if (this.updateBothPoints) {
            this.p1.addSize(size);
        }
    }

    public void addColor(float strength, int col) {
        if (this.colors.hasKey(str(col))) {
            this.colors.add(str(col), strength);
        }else {
            this.colors.set(str(col), strength);
        }
        // if (this.colors.get(str(col)) <= 0) {
        //     this.colors.remove(str(col));
        //     print("something wrong during color subtraction!");
        // }
        this.colorSize += strength;
        this.p2.addColor(strength, col);
        if (this.updateBothPoints) {
            this.p1.addColor(strength, col);
        }
    }

    public void delColor(int col) {
        if (this.colors.hasKey(str(col))) {
            this.colors.remove(str(col));
        }
        this.p2.delColor(col);
        if (this.updateBothPoints) {
            this.p1.delColor(col);
        }
    }

    public void removeColors() {
        this.colors = new FloatDict();
        this.p2.removeColors();
        if (this.updateBothPoints) {
            this.p1.removeColors();
        }
    }

    public void changeColor(int startColor, int endColor) {
        if (this.colors.hasKey(str(startColor))) {
            if (this.colors.hasKey(str(endColor))) {
                this.colors.add(str(endColor), this.colors.get(str(startColor)));
            } else {
                this.colors.set(str(endColor), this.colors.get(str(startColor)));
            }
            this.colors.remove(str(startColor));
        }
        this.p2.changeColor(startColor, endColor);
        if (this.updateBothPoints) {
            this.p1.changeColor(startColor, endColor);
        }
    }

    public void display() {
        fill(100, 100);
        strokeWeight(this.thikness);
        stroke(100, 100);
        line(this.p1.x, this.p1.y, this.p2.x, this.p2.y);
        try {
            for (String colStr : this.colors.keys()) {
                // stroke(Integer.parseInt(colStr), 255*this.colors.get(colStr)/this.colorSize);
                stroke(Integer.parseInt(colStr), 255*this.colors.get(colStr)/this.size);
                line(this.p1.x, this.p1.y, this.p2.x, this.p2.y);
            }
        } catch (NumberFormatException e) {
            println("this.colors: "+this.colors);
        }
        strokeWeight(1);
    }
}
public class MapManager  {

    HashMap<String, State> states;
    Canvas canvas;
    PShape usa;

    public MapManager (String mapFileName, String barFileName, Canvas canvas) {
        this.usa = loadShape(mapFileName);
        this.canvas = canvas;
        float w = this.usa.width;
        float h = this.usa.height;
        float scale = min(canvas.w/w, canvas.h/h);
        this.usa.scale(scale);

        JSONObject values = loadJSONObject(barFileName);
        int maxStateVal = values.getInt("maxStateVal");

        this.states = new HashMap<String, State>();
        for (int i = 0; i < 51; ++i) {
            PShape state = usa.getChild(i);
            this.states.put(state.getName(), new State(state, color(100,50,170), maxStateVal));
        }
    }

    public void display() {
        // this.canvas.drawRect(color(250, 100));
        // rect(this.canvas.x, this.canvas.y, this.usa.width, this.usa.height);
        // shape(this.usa, 0, 0);
        for (State state : this.states.values()) {
            state.display();
        }
        noFill();
    }


}
public class ParallelCoordsManager  {
    Canvas canvas;
    Bar[] bars;
    Point[] points;
    Line[] lines;
    IntDict lineIndexDict;
    DataManager dataManager;
    int currentColorSrc = 0;
    int currentStatPoint = 0;


    public ParallelCoordsManager (String barFile, Canvas canvas, DataGroup[] data, DataManager dataManager) {
        this.canvas = canvas;
        this.initBars(barFile);
        this.initPoints();
        this.initLines();
        this.dataManager = dataManager;
    }

    public void initBars(String barFile) {
        ArrayList<Bar> barsList;
        barsList = new ArrayList<Bar>();
        JSONObject values = loadJSONObject(barFile);

        float len = 7;
        String[] shortCategoryBars = {"status", "gender", "device", "age"};
        String longCategoryBar = "categories";
        String[] valueBars  = {"time", "amount"};

        float w = 0.9f*canvas.w;
        float x = canvas.x + 0.05f*canvas.w;
        float y = canvas.y + 0.05f*canvas.h;

        for (String barName : shortCategoryBars) {
            barsList.add(new CategoryBar(x, y + canvas.h*.3f, .3f*canvas.h, barName, values.getJSONArray(barName)));
            x = x + w/(len-1); 
        }
        barsList.add(new CategoryBar(x, y, 0.9f*canvas.h, longCategoryBar, values.getJSONArray(longCategoryBar)));
        x = x + w/(len-1); 

        for (String barName : valueBars) {
            barsList.add(new ValueBar(x, y, 0.9f*canvas.h, barName, 0, values.getFloat(barName)));
            x = x + w/(len-1); 
        }
        this.bars = new Bar[barsList.size()];
        this.bars = barsList.toArray(this.bars);
    }

    public void initPoints() {
        ArrayList<Point> pointList = new ArrayList<Point>();
        for (Bar bar : this.bars) {
            for (Point p : bar.points) {
                pointList.add(p);
            }
        }
        this.points = new Point[pointList.size()];
        this.points = pointList.toArray(this.points);
    }

    public void initLines() {
        ArrayList<Line> lineList = new ArrayList<Line>();
        lineIndexDict = new IntDict();
        int l = 0;
        for (int i = 0; i < this.bars.length - 1; ++i) {
            boolean updateBothPoints = false;
            if (i == 0) {
                updateBothPoints = true;
            }   
            for (Point p1 : this.bars[i].points) {
                for (Point p2 : this.bars[i+1].points) {
                    lineList.add(new Line(p1, p2, updateBothPoints));
                    String key = this.bars[i].label + p1.label + this.bars[i+1].label + p2.label;
                    // println("key: "+key);
                    lineIndexDict.set(key, l);
                    l++;
                }
            }
        }
        this.lines = new Line[lineList.size()];
        this.lines = lineList.toArray(this.lines);
    }

    public Line getLine(String key) {
        if (!this.lineIndexDict.hasKey(key)) {
            println("No Key: "+key);
        }
        return this.lines[this.lineIndexDict.get(key)];
    }

    public void updatePointStats() {
        for (Point p : this.points) {
            p.updateStat(this.bars[this.currentColorSrc]);
        }
    }


    public void display() {
        stroke(30, 20, 55, 10);
        for (Bar b : this.bars) {
            b.display();
        }
        for (Line l : this.lines) {
            l.display();
        }
        for (Point p : this.points) {
            p.display();
        }
        for (Point p : this.points) {
            p.dispayLabel();
        }
    }

    public boolean mousePressed() {
        for (int i = 0; i < bars.length; ++i) {
            Bar bar = bars[i];
            for (Point point : bar.points) {
                if (point.isMouseOver()) {
                    boolean filtered = this.dataManager.addFilter(point.label, i);
                    point.setFiltered(filtered);
                    return true;
                }
            }
        }
        return false;
    }


    public boolean mouseMoved() {
        for (int i = 0; i < bars.length; ++i) {
            Bar bar = bars[i];
            if(bar.isMouseOver()){
                // this.dataManager.removeAllFilters();
                if (i != this.currentColorSrc) {
                    this.dataManager.setNewColorSource(i);
                    this.currentColorSrc = i;
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }



}
public class Point  {
    int basicColor;
    float x;
    float y;
    int size;
    FloatDict colors;
    float r;
    String label;
    boolean filtered;
    FloatDict stats;
    boolean doStatDisplay = false;

    public Point (float x, float y, int basicColor, String label) {
        this.x = x;
        this.y = y;
        this.basicColor = basicColor;
        this.size = 0;
        this.colors = new FloatDict();
        this.stats = new FloatDict();
        this.r = 0.0f;
        this.label = label;
        this.filtered = false;
    }

    public void addSize(int size) {
        this.size += size;
        this.r = max(5, log(this.size/10)*10);
    }

    public void addColor(float strength, int col) {
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

    public void delColor(int col) {
        if (this.colors.hasKey(str(col))) {
            this.colors.remove(str(col));
        }
    }

    public void changeColor(int startColor, int endColor) {
        if (this.colors.hasKey(str(startColor))) {
            if (this.colors.hasKey(str(endColor))) {
                this.colors.add(str(endColor), this.colors.get(str(startColor)));
            } else {
                this.colors.set(str(endColor), this.colors.get(str(startColor)));
            }
            this.colors.remove(str(startColor));
        }
    }

    public void removeColors() {
        this.colors = new FloatDict();
    }

    public void setFiltered(boolean filtered){
        this.filtered = filtered;
    }

    public void updateStat(Bar bar) {
        float selected = 0.0f;
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

    public void display() {
        fill(170);
        // if (this.colors.size() == 0) {
            
        //     fill(this.basicColor);
        // }
        ellipse(this.x, this.y, this.r, this.r);
        float startAngle = 0;
        float endAngle = 0;
        // if (this.colors.size() > 0) {
        try {
            for (String colStr : this.colors.keys()) {
                fill(Integer.parseInt(colStr));
                noStroke();
                endAngle = startAngle + TWO_PI*this.colors.get(colStr)/this.size;
                arc(this.x, this.y, this.r, this.r, startAngle, endAngle, PIE);
                startAngle = endAngle;
            }
        } catch (NumberFormatException e) {
            println("this.colors: "+this.colors);
        }
        noFill();
        stroke(0);
        if (this.filtered) {
            strokeWeight(5);
        } else {
            strokeWeight(1);
        }
        ellipse(this.x, this.y, this.r, this.r);
        stroke(0);
        strokeWeight(1);

        // }
        // st   roke(0);
    }

    public void dispayLabel() {
        fill(0);
        textAlign(CENTER, TOP);
        text(this.label, this.x, this.y); 
        this.dispayStats();
    }

    public void dispayStats() {
        if (this.isMouseOver()) {
            String textToDisplay = "";
            for (String name : this.stats.keys()) {
                textToDisplay += name + ": " + str(round(this.stats.get(name)*100)) + "%\r\n";
            }
            text(textToDisplay, mouseX, mouseY, 150, 150);
        }
    }

    public boolean isMouseOver() {
        if (mouseX < this.x+5 && mouseX > this.x-5 &&
            mouseY < this.y+5 && mouseY > this.y-5) {
            return true;
        }
        if (sqrt(sq(mouseX - this.x) + sq(mouseY - this.y)) < this.r/2) {
            return true;
        }
        return false;
    }
}
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

  public void addSize(float size) {
    this.size += size;
  }

  public void resetSize() {
      this.size = 0;
  }

  public void removeColors() {
    this.colors = new FloatDict();
  }

  public void addColor(float strength, int col) {
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

  public void updateStat(Bar bar) {
        float selected = 0.0f;
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

  public void display() {
    stateShape.disableStyle();
    fill(50, 50 + 205*this.size/this.maxSize);
    noStroke();
    // stroke(0);
    // smooth(8);
    shape(stateShape);
    this.dispayStats();
  }

    public void dispayStats() {
        if (this.isSelected) {
            String textToDisplay = "";
            for (String name : this.stats.keys()) {
                textToDisplay += name + ": " + str(round(this.stats.get(name)*100)) + "%\r\n";
            }
            text(textToDisplay, mouseX, mouseY, 150, 150);
        }
    }   


  public void setSelected(boolean newSelected) {
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
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "parallel" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
