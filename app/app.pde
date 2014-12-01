int INIT_WIDTH = 1200;
int INIT_HEIGHT = 800;

DataGroup[] data;
DataManager dataManager;
// Controller controller;
ParallelCoordsManager graphManager;
// Canvas all_canvas;

MapManager mapManager;
// State[] usaStates;




void setup()
{
  frame.setResizable(true);
  size(INIT_WIDTH, INIT_HEIGHT, P2D);
  ArrayList<DataGroup> arrayData = getData("../data/preProcessedData.json");
  data = new DataGroup[arrayData.size()];
  data = arrayData.toArray(data);

// String[] categoryNames = getCategoryNames("iris.csv");
// controller = new Controller(data, categoryNames);
  Canvas parallel_canvas = new Canvas(0, height*.4, width, height*.58);
  Canvas map_canvas = new Canvas(0, 0, width*.6, height*.6);
  graphManager = new ParallelCoordsManager("../data/barsValues.json", parallel_canvas, data, dataManager);
  mapManager = new MapManager("usa-wikipedia.svg", "../data/barsValues.json", map_canvas);
  dataManager = new DataManager(data, graphManager, mapManager);
  dataManager.start();
  graphManager.dataManager = dataManager;
  dataManager.setNewColorSource(6);

}



void draw()
{
  background(245);
  graphManager.display();
  fill(0, 102, 153);
  text(str(floor(frameRate)), 10, 30);
  text(str(mouseX), 10, 50);
  this.mapManager.display();
}

ArrayList<DataGroup> getData(String file)
{
  ArrayList<DataGroup> datagroups = new ArrayList<DataGroup>();
  JSONArray values = loadJSONArray(file);
  int len = values.size();
  for (int i=0; i<len; ++i) {
    datagroups.add(new DataGroup(values.getJSONObject(i)));
  }
return datagroups;
}


void mousePressed() {
  graphManager.mousePressed();
}

void mouseMoved() {
  graphManager.mouseMoved();
}

// void initStates() {
//   usa = loadShape("usa-wikipedia.svg");
// }

