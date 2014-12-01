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

    void addSize(int size) {
        this.size += size;
        this.thikness = ceil((float)this.size/300);
        this.p2.addSize(size);
        if (this.updateBothPoints) {
            this.p1.addSize(size);
        }
    }

    void addColor(float strength, int col) {
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

    void delColor(int col) {
        if (this.colors.hasKey(str(col))) {
            this.colors.remove(str(col));
        }
        this.p2.delColor(col);
        if (this.updateBothPoints) {
            this.p1.delColor(col);
        }
    }

    void removeColors() {
        this.colors = new FloatDict();
        this.p2.removeColors();
        if (this.updateBothPoints) {
            this.p1.removeColors();
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
        this.p2.changeColor(startColor, endColor);
        if (this.updateBothPoints) {
            this.p1.changeColor(startColor, endColor);
        }
    }

    void display() {
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