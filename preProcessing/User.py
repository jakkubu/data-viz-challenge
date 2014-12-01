import json


class User(object):

    def __init__(self, user_id, gender, status, device, age, location):
        super(User, self).__init__()
        self.viewed = []
        self.founded = []
        self.time = []
        self.amount = 0

        self.user_id = user_id
        self.gender = gender
        self.status = status
        self.device = device
        self.age = age
        self.state = location["state"]
        # self.latitude = location["latitude"]
        # self.longitude = location["longitude"]

    def checkUserData(self, user_id, gender, status, device, age, location):
        if (self.user_id != user_id or
                self.gender != gender or
                self.status != status or
                self.device != device or
                self.age != age):
            if (self.gender != gender):
                gender = self.gender if gender == "U" else gender
                self.gender = gender if self.gender == "U" else self.gender
                if (self.gender != gender):
                    print ("self.gender: " + str(self.gender) +
                           " gender: " + str(gender))
                    raise Warning("user is both M and F!")
                    self.gender = gender
            else:
                raise Exception("User data has changed!")

        # if (self.state != location["state"] or
        #         self.latitude != location["latitude"] or
        #         self.longitude != location["longitude"]):
        #     raise Exception("location has changed!")

    def addEvent(self, time, founded, category, location, amount=0):
        if founded:
            if amount == 0:
                raise Exception("Project was founded with 0$ !?!")
            self.founded.append(category)
        else:
            self.viewed.append(category)
        self.time.append(time)
        self.amount += amount

    def prepare4json(self):
        self.time = max(self.time) - min(self.time)
        del self.user_id


class UserEncoder(json.JSONEncoder):

    def default(self, obj):
        if not isinstance(obj, User):
            return super(UserEncoder, self).default(obj)

        obj.prepare4json()
        return obj.__dict__
