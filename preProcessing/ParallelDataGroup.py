import json


class ParallelData(object):

    def __init__(self):
        super(ParallelData, self).__init__()


class ParallelDataGroup(ParallelData):

    def __init__(self, gender, status, device, age):
        super(ParallelDataGroup, self).__init__()
        self.gender = gender
        self.status = status
        self.device = device
        self.age = age
        self.specificUsers = []

    def addSpecificUser(self, user):
        interaction = UserInteraction(viewed=user.viewed,
                                      founded=user.founded,
                                      time=user.time,
                                      amount=user.amount,
                                      )
        location = user.state
        # location = UserLocation(state=user.state,
        #                         latitude=user.latitude,
        #                         longitude=user.longitude,
        #                         )
        self.specificUsers.append(SpecificUser(interaction, location))


class SpecificUser(ParallelData):

    def __init__(self,  interaction, location):
        super(SpecificUser, self).__init__()
        self.interaction = interaction
        self.location = location


class UserInteraction(ParallelData):

    def __init__(self, viewed, founded, time, amount):
        super(UserInteraction, self).__init__()
        self.viewed = viewed
        self.founded = founded
        self.time = time
        self.amount = amount


class UserLocation(ParallelData):

    def __init__(self, state, latitude, longitude):
        super(UserLocation, self).__init__()
        self.state = state
        self.latitude = latitude
        self.longitude = longitude


class ParallelDataEncoder(json.JSONEncoder):

    def default(self, obj):
        if not isinstance(obj, ParallelData):
            return super(ParallelDataEncoder, self).default(obj)

        # obj.prepare4json()
        return obj.__dict__
