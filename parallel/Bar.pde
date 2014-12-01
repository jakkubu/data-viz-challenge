import java.io.*;
import java.util.*;

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

    void update(float x, float y, float h){
        this.x = x;
        this.y = y;
        this.h = h;
        this.isColorSrc = false;
    }

    void setIsColorSrc(boolean isNew){
        this.isColorSrc = isNew;
    }

    void display() {
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

    boolean isMouseOver() {
        if (mouseX < this.x+5 && mouseX > this.x-5 &&
            mouseY > this.y   && mouseY < this.y+this.h) {
            return true;
        }
        return false;
    }

    Point getPoint(String category) {
    // float[] getPoint(String category) throws Exception{
        // throw new Exception("not implemented");
        // throw new RemoteException();
        // return new float[] {.0, .0};
        return null;
    }

    Point getPoint(float val) {
    // float[] getPoint(float val) throws Exception{
        // throw new Exception("not implemented");
        // throw new RemoteException();
        // return new float[] {.0, .0};
        return null;
    }
    
    String getLabel(float val) {
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

    void update(float x, float y, float h, float minV, float maxV){
        super.update(x, y, h);
        this.minV = minV;
        this.maxV = maxV;
        this.points = new Point[this.pointLen];
        colorMode(HSB, 255);
        for (int i = 0; i < this.pointLen; ++i) {
            float c = i*180/(this.pointLen-1);
            float y_point = map(i, 0, this.pointLen-1, .95*this.h, .05*this.h) + this.y;
            String pointLabel = str(floor(map(i,  0, this.pointLen-1, this.minV, this.maxV)));
            points[i] = new Point(x, y_point, color(c, 200, 255), pointLabel);
        }
        colorMode(RGB, 255);
    }

    Point getPoint(float val) {
        int index = floor(map(val, this.minV, this.maxV, 0, this.pointLen-1));
        return this.points[index];
    }

    String getLabel(float val) {
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

    void update(float x, float y, float h, JSONArray categories) {
        super.update(x, y, h);
        int len = categories.size();
        float distance = this.h*.9/(len - 1);
        this.points = new Point[len];
        colorMode(HSB, 255);
        for (int i = 0; i < len; ++i) {
            this.categories.set(categories.getString(i), i);
            float c = (i)*180/(len);
            float y_point = .05*h + i*distance + this.y;
            points[i] = new Point(x, y_point, color(c, 200, 255), categories.getString(i));
        }
        colorMode(RGB, 255);
    }

    Point getPoint(String category) {
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