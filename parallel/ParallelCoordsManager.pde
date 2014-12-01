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

    void initBars(String barFile) {
        ArrayList<Bar> barsList;
        barsList = new ArrayList<Bar>();
        JSONObject values = loadJSONObject(barFile);

        float len = 7;
        String[] shortCategoryBars = {"status", "gender", "device", "age"};
        String longCategoryBar = "categories";
        String[] valueBars  = {"time", "amount"};

        float w = 0.9*canvas.w;
        float x = canvas.x + 0.05*canvas.w;
        float y = canvas.y + 0.05*canvas.h;

        for (String barName : shortCategoryBars) {
            barsList.add(new CategoryBar(x, y + canvas.h*.3, .3*canvas.h, barName, values.getJSONArray(barName)));
            x = x + w/(len-1); 
        }
        barsList.add(new CategoryBar(x, y, 0.9*canvas.h, longCategoryBar, values.getJSONArray(longCategoryBar)));
        x = x + w/(len-1); 

        for (String barName : valueBars) {
            barsList.add(new ValueBar(x, y, 0.9*canvas.h, barName, 0, values.getFloat(barName)));
            x = x + w/(len-1); 
        }
        this.bars = new Bar[barsList.size()];
        this.bars = barsList.toArray(this.bars);
    }

    void initPoints() {
        ArrayList<Point> pointList = new ArrayList<Point>();
        for (Bar bar : this.bars) {
            for (Point p : bar.points) {
                pointList.add(p);
            }
        }
        this.points = new Point[pointList.size()];
        this.points = pointList.toArray(this.points);
    }

    void initLines() {
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

    Line getLine(String key) {
        if (!this.lineIndexDict.hasKey(key)) {
            println("No Key: "+key);
        }
        return this.lines[this.lineIndexDict.get(key)];
    }

    void updatePointStats() {
        for (Point p : this.points) {
            p.updateStat(this.bars[this.currentColorSrc]);
        }
    }


    void display() {
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

    boolean mousePressed() {
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


    boolean mouseMoved() {
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