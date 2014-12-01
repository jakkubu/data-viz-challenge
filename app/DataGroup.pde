
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

