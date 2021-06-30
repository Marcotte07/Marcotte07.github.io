// Copyright 2017 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


'use strict';

// [START gae_node_request_example]
const express = require('express');

const app = express();
const path = require('path');
app.use(express.static(__dirname + '/dist/hw8-nick-marcotte'));

var cors = require('cors');
app.use(cors());

const axios = require('axios');

var geohash = require('ngeohash');

app.get('/', (req, res) => {
  //res.status(200).send('Hello, world!').end();
  res.sendFile(path.join(__dirname, '/index.html'));
});


// My ticketmaster API Key is: f2DRraG6ARgAnKE2Vc70MT18I7mGrOAp

app.get('/retrieveSearchResults', (req, res) => {
  console.log(req.query)

  var JSONInput = req.query
  getSearchResults(JSONInput.keyword, JSONInput.event, JSONInput.distance, JSONInput.units, JSONInput.lat, JSONInput.lng, res);
});


function getSearchResults(keyword, eventType, distance, units, lat, lng, res){
  var x = keyword.split(" ")
  // Now combine them all together with + inbetween them
  console.log("x is " + x)
  var keywordStr = x.join("+");
  console.log(keywordStr);
  var eventCode = "";
  if(eventType == "music"){
      eventCode = "KZFzniwnSyZfZ7v7nJ"
  }
  else if(eventType == "sports"){
      eventCode = "KZFzniwnSyZfZ7v7nE"
  }
  else if(eventType == "arts&theatre"){
      eventCode = "KZFzniwnSyZfZ7v7na"
  }
  else if(eventType == "film"){
      eventCode = "KZFzniwnSyZfZ7v7nn"
  }
  else if(eventType == "miscellaneous"){
      eventCode = "KZFzniwnSyZfZ7v7n1"
  }
  else{
      eventCode = ""
  }

  if(units == "Miles"){
    units = "miles"
  }
  else{
    units = 'km'
  }
  const geoHash = geohash.encode(lat, lng);
  console.log(geoHash);
  var urlstr = ("https://app.ticketmaster.com/discovery/v2/events.json?apikey=f2DRraG6ARgAnKE2Vc70MT18I7mGrOAp" +
      "&keyword=" + keywordStr + "&segmentId=" +
      eventCode + "&radius=" + distance + "&unit=" + units + "&geoPoint=" + geoHash +
      "&sort=date,asc");
  console.log(urlstr);
  axios.get(urlstr).then(resp => {
    res.send(resp.data);
  });

}


// My ticketmaster API Key is: f2DRraG6ARgAnKE2Vc70MT18I7mGrOAp

app.get('/getAutocomplete', (req, res) => {
  var value = req.query.value;

  // Make call to ticketMaster here
  var url = "https://app.ticketmaster.com/discovery/v2/suggest?apikey=f2DRraG6ARgAnKE2Vc70MT18I7mGrOAp&keyword=" + value;
  axios.get(url).then(resp => {
    res.send(resp.data._embedded.attractions);
  });

});

// Start the server
const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`App listening on port ${PORT}`);
  console.log('Press Ctrl+C to quit.');
});
// [END gae_node_request_example]

module.exports = app;
