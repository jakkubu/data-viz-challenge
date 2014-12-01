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
        this.r = 0.0;
        this.label = label;
        this.filtered = false;
    }

    void addSize(int size) {
        this.size += size;
        this.r = max(5, log(this.size/10)*10);
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

    void delColor(int col) {
        if (this.colors.hasKey(str(col))) {
            this.colors.remove(str(col));
        }
    }

    void changeColor(int startColor, int endColor) {
        if (this.colors.hasKey(str(startColor))) {
            if (this.colors.hasKey(str(endColor))) {
                this.colors.add(str(endColor), this.colors.get(str(startColor)));
            } else {
                this.colors.set(str(endColor), this.colors.get(str(startColor)));
            }
            this.colors.remove(str(startColor));
        }
    }

    void removeColors() {
        this.colors = new FloatDict();
    }

    void setFiltered(boolean filtered){
        this.filtered = filtered;
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

    void dispayLabel() {
        fill(0);
        textAlign(CENTER, TOP);
        text(this.label, this.x, this.y); 
        this.dispayStats();
    }

    void dispayStats() {
        if (this.isMouseOver()) {
            String textToDisplay = "";
            for (String name : this.stats.keys()) {
                textToDisplay += name + ": " + str(round(this.stats.get(name)*100)) + "%\r\n";
            }
            text(textToDisplay, mouseX, mouseY, 150, 150);
        }
    }

    boolean isMouseOver() {
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