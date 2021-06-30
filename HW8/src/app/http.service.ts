import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class HttpService {
  constructor(private http: HttpClient) { }

  
  getIPinfo() {
    var url = "https://ipinfo.io/json?token=b8767809c41bba"          
    return this.http.get(url);
  }
  getIPinfoCustom(address: string){
    var url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=AIzaSyBUVIzR6tIwztkSldSmGFP7X6C_-9QmQtw";          
    console.log("URL for getting custom ip info is:" + url);
    return this.http.get(url);
  }

  //rootURL = 'http://localhost:8080'
  
  // Deployed URL
  rootURL = "https://csci-571-hw8-nick-marcotte.uc.r.appspot.com"
  retrieveData(keywordVal: string, event: string, distance: string, units: string, coords: string[]){
    var url = this.rootURL + "/retrieveSearchResults?keyword=" + keywordVal + "&distance=" + distance +
    "&units=" + units + "&lat=" + coords[0] + "&lng=" + coords[1];
    // Need to send JSON somehow to result component, for now just print out json in form component
    console.log(url);
    var JSONData = this.http.get(url);
    return JSONData;
  }


  getAutocomplete(value: string) {
    var url = this.rootURL + "/getAutocomplete?value=" + value;
    return this.http.get(url);
  }
}
