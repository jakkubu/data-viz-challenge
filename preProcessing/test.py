import json
from sys import getsizeof


print ("getsizeof(json.load(open(\"preProcessed2.json\")))): " +
       str(getsizeof(json.load(open("preProcessed2.json")))))
