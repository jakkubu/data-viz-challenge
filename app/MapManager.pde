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

    void display() {
        // this.canvas.drawRect(color(250, 100));
        // rect(this.canvas.x, this.canvas.y, this.usa.width, this.usa.height);
        // shape(this.usa, 0, 0);
        for (State state : this.states.values()) {
            state.display();
        }
        noFill();
    }


}