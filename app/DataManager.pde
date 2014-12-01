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

    void init() {
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


    void update() {
        for (Line line : graphManager.lines) {
            line.removeColors();
        }
        for (State state : this.mapManager.states.values()) {
            state.resetSize();
        }
        // String key;
        float m_avr = 0.0;
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
                        state.addSize(1.0/filteredColor.length);
                        // state.addColor(1.0/filteredColor.length, col);
                    }
                }


                for (Line l : simpleLines) {
                    for (int i = 0; i < filteredColor.length; ++i) {
                        float f = 1.0/filteredColor.length;
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

    int[] filterColors(SpecificUser user, DataGroup dataGroup, int[] colors_src) {
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


    void removeFilter(String category) {
        this.filter.remove(category);
    }

    void removeAllFilters() {
        this.filter = new IntDict();
    }

    Line[] getSimpleLines(DataGroup dataGroup, SpecificUser user) {  
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

    Line[] getCategoryLines(DataGroup dataGroup, SpecificUser user) {
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

    int[] getDataGroupColor(DataGroup dataGroup) {
        int[] colors = new int[1];
        colors[0] = getDataGroupPoints(dataGroup)[this.selectedBar].basicColor;
        return colors;
    }

    int[] getUserColor(SpecificUser user) {
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

    Point[] getFullUserPoints(DataGroup dataGroup, SpecificUser user) {
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

    Point[] getDataGroupPoints(DataGroup dataGroup) {
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

    Point[] getUserCategoryPoints(SpecificUser user) {
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

    Point[] getUserValuePoints(SpecificUser user) {
        Point[] points = new Point[2];
        points[0] = this.graphManager.bars[5].getPoint(user.time);
        points[1] = this.graphManager.bars[6].getPoint(user.amount);
        return points;
    }

    void setNewColorSource(int selectedBar) {
        if (!this.doUpdate) {
            for (Line line : graphManager.lines) {
                line.removeColors();
            }
            this.selectedBar = selectedBar;
            this.doUpdate = true;
        }
    }

    boolean addFilter(String category, int bar) {
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