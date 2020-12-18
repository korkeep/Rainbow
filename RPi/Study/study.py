import requests
import json

with open('/home/pi/School/Project/Study/today.json') as json_file:
    json_data = json.load(json_file)
    response = requests.post('https://r89kbtj8x9.execute-api.us-east-1.amazonaws.com/dev/study-update', json=json_data)

with open('/home/pi/School/Project/Study/tomorrow.json') as json_file:
    json_data = json.load(json_file)
    response = requests.post('https://r89kbtj8x9.execute-api.us-east-1.amazonaws.com/dev/study-init', json=json_data)
