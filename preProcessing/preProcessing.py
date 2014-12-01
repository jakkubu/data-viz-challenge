import json
# from sys import getsizeof
from User import *
from ParallelDataGroup import *

bars = {
    "gender": [],
    "status": [],
    "device": [],
    "age": [],
    "categories": [],
    "time": 0,
    "amount": 0,
    "maxStateVal": 0
}


def getUsers(data):
    users = []
    usersObjects = {}
    stateDict = {}
    for u in data:
        user = u["session_id"]
        if user not in users:
            users.append(user)
            userObj = User(
                user_id=u["session_id"],
                gender=u["gender"],
                status=u["marital_status"],
                device=u["device"],
                location=u["location"],
                age=u["age"])
            num = stateDict.get(u["location"]["state"], 0) + 1
            stateDict[u["location"]["state"]] = num
            usersObjects[user] = userObj
        else:
            usersObjects[user].checkUserData(
                user_id=u["session_id"],
                gender=u["gender"],
                status=u["marital_status"],
                device=u["device"],
                location=u["location"],
                age=u["age"])

        founded = u["event_name"] == "Fund Project"

        #        " " + str(len(u["location"])))
        usersObjects[user].addEvent(
            time=u["client_time"],
            founded=founded,
            category=u["category"],
            location=u["location"],
            amount=u.get("amount", 0)
        )
        if u["gender"] not in bars["gender"]:
            bars["gender"].append(u["gender"])
        if u["marital_status"] not in bars["status"]:
            bars["status"].append(u["marital_status"])
        if u["device"] not in bars["device"]:
            bars["device"].append(u["device"])
        if u["age"] not in bars["age"]:
            bars["age"].append(u["age"])
        if u["category"] not in bars["categories"]:
            bars["categories"].append(u["category"])
    bars["age"].sort(reverse=True)
    bars["maxStateVal"] = max(stateDict.values())
    return usersObjects.values()


def getParallelData(users):
    dataGroup = {}
    for u in users:
        u.prepare4json()
        key = u.age + u.device + u.gender + u.status
        if key not in dataGroup:
            dataGroup[key] = ParallelDataGroup(
                gender=u.gender,
                status=u.status,
                device=u.device,
                age=u.age
            )
        dataGroup[key].addSpecificUser(u)
        if u.time > bars["time"]:
            bars["time"] = u.time
        if u.amount > bars["amount"]:
            bars["amount"] = u.amount

    print ("len(dataGroup): " + str(len(dataGroup)))
    return dataGroup


if __name__ == '__main__':
    json_data = open("../data/data.json")
    data = json.load(json_data)["data"]
    users = getUsers(data)
    dataGroup = getParallelData(users)
    with open('../data/preProcessedData.json', 'w') as outfile:
        json.dump(dataGroup.values(), outfile, cls=ParallelDataEncoder,
                  separators=(',', ':'))
    with open('../data/preProcessedData_redeable.json', 'w') as outfile:
        json.dump(dataGroup.values(), outfile, cls=ParallelDataEncoder,
                  separators=(',', ':'), indent=2)

    categories = bars["categories"]
    newCategories = []
    for category in categories:
        newCategories.append("viewed " + category)
    for category in categories:
        newCategories.append("founded " + category)
    bars["categories"] = newCategories

    with open('../data/barsValues.json', 'w') as outfile:
        json.dump(bars, outfile, cls=ParallelDataEncoder,
                  separators=(',', ':'), indent=2)
    json_data.close()
