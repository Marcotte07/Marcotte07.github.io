# Copyright 2018 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# [START gae_python38_app]
# [START gae_python3_app]
from flask import Flask, current_app
from flask import request
import json
import sys
from geolib import geohash
import requests
import urllib.request


# If `entrypoint` is not defined in app.yaml, App Engine will look for an app
# called `app` in `main.py`.
app = Flask(__name__)


@app.route('/')
def send_page():
    return current_app.send_static_file('HW6.html')

@app.route('/JSONTest',  methods = ['POST'])
def returnConfirmation():
    print(request.get_json())

    keyword = request.get_json()['keyword']
    event = request.get_json()['event']
    radius = request.get_json()['radius']
    latitude = request.get_json()['lat']
    longitude = request.get_json()['lng']

    # Get GeoHash from lat and long
    geoHash = getGeoHash(latitude, longitude)

    JSONdata = getTicketMasterJson(keyword, event, radius, geoHash)
    # Return JSON of ticketmaster data
    return JSONdata


# My ticketmaster API Key is: f2DRraG6ARgAnKE2Vc70MT18I7mGrOAp
@app.route('/getEventInfo',  methods = ['POST'])
def returnEventInfo():
    print(request.get_json())
    eventID = request.get_json()['eventID']
    urlstr = "https://app.ticketmaster.com/discovery/v2/events/" + eventID + "?apikey=f2DRraG6ARgAnKE2Vc70MT18I7mGrOAp"
    print(urlstr)
    with urllib.request.urlopen(urlstr) as url:
        data = json.loads(url.read().decode())
        return data

def getGeoHash(latitude, longitude):
    geoCode = geohash.encode(latitude, longitude, 7)
    print(geoCode)
    return geoCode

# My ticketmaster API Key is: f2DRraG6ARgAnKE2Vc70MT18I7mGrOAp
def getTicketMasterJson(keyword, event, radius, geoHash):
    eventCode = "";

    # May need to split keyword if more than one word, as no spaces allowed

    x = keyword.split()
    # Now combine them all together with + inbetween them

    keywordStr = "+".join(x);
    print(keywordStr);

    if(event == "music"):
        eventCode = "KZFzniwnSyZfZ7v7nJ"
    elif(event == "sports"):
        eventCode = "KZFzniwnSyZfZ7v7nE"
    elif(event == "arts&theatre"):
        eventCode = "KZFzniwnSyZfZ7v7na"
    elif(event == "film"):
        eventCode = "KZFzniwnSyZfZ7v7nn"
    elif(event == "miscellaneous"):
        eventCode = "KZFzniwnSyZfZ7v7n1"
    else:
        # DO NOTHING
        eventCode = ""

    print("geoPoint is " + geoHash)

    urlstr = ("https://app.ticketmaster.com/discovery/v2/events.json?apikey=f2DRraG6ARgAnKE2Vc70MT18I7mGrOAp" +
        "&keyword=" + keywordStr + "&segmentId=" +
        eventCode + "&radius=" + radius + "&unit=miles&geoPoint=" + geoHash)

    print(urlstr)
    # headers = {'Content-Type': 'application/json'}
    with urllib.request.urlopen(urlstr) as url:
        data = json.loads(url.read().decode())
        return data

if __name__ == '__main__':
    # This is used when running locally only. When deploying to Google App
    # Engine, a webserver process such as Gunicorn will serve the app. This
    # can be configured by adding an `entrypoint` to app.yaml.
    app.run(host='127.0.0.1', port=8080, debug=True)
# [END gae_python3_app]
# [END gae_python38_app]
