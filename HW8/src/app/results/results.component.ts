import { THIS_EXPR } from '@angular/compiler/src/output/output_ast';
import { Component, Input, OnInit } from '@angular/core';
import { ViewEncapsulation} from '@angular/core';



@Component({
  selector: 'results',
  templateUrl: './results.component.html',
  styleUrls: ['./results.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class ResultsComponent implements OnInit {

  constructor() { }

  @Input() JsonObject: Object;

  localJsonObject: any;

  

  // Declare array of objects events, this will be what the ngFor loops through to output the results
  eventsArray: Array<any>;
  categoryArray: Array<any>;
  favoritesArray: Array<any>;
  myMap: any;
  
  ngOnInit(): void {
    if(!localStorage.getItem("favoritesMap")){
      localStorage.setItem("favoritesMap", JSON.stringify(this.myMap))
    }
  }
  // Runs whenever change is detected, so this runs ALL the time lol
  ngDoCheck(){
    if(this.JsonObject == "undefined" || JSON.stringify(this.JsonObject) == JSON.stringify(this.localJsonObject)){
      // Nothing to update
      return
    }
    else{
      // Update 
      this.localJsonObject = this.JsonObject;
      var result = JSON.stringify(this.localJsonObject);
      var dataObject = JSON.parse(result);

      // If list is empty, then display the noResultsFound
      if(dataObject.page.totalElements == 0){
        this.showNoResults();
        return;
      }
      else{
        this.eventsArray = dataObject._embedded.events;
        this.updateTable()
      }
    }
  }

  updateTable(){
    
    var resultsTable = <HTMLDivElement> document.getElementById("test");
    resultsTable.style.display = "";
    var noResultsDiv = <HTMLDivElement> document.getElementById("noResults");
    noResultsDiv.style.backgroundColor = 	""
    noResultsDiv.innerHTML = ""
    noResultsDiv.style.width = ""
    noResultsDiv.style.height = ""
    noResultsDiv.style.marginTop = ""
    noResultsDiv.style.fontSize = ""
    noResultsDiv.style.textAlign = ""
    noResultsDiv.style.verticalAlign = ""

    // Need to update categories in category array
    this.categoryArray = [];
    for(var i=0; i < this.eventsArray.length; i++){
      var sc = this.eventsArray[i].classifications[0];
      var genres = [];
      if(sc.genre != undefined){
        if(sc.genre.name != "Undefined"){
          genres.push(sc.genre.name);
        }
      }
      if(sc.segment != undefined){
        if(sc.segment.name != "Undefined"){
          genres.push(sc.segment.name);
        }
      }
      if(sc.subGenre != undefined){
        if(sc.subGenre.name != "Undefined"){
          genres.push(sc.subGenre.name);
        }
      }
      if(sc.subType != undefined){
        if(sc.subType.name != "Undefined"){
          genres.push(sc.subType.name);
        }
      }
      if(sc.type != undefined){
        if(sc.type.name != "Undefined"){
          genres.push(sc.type.name);
        }
      }
      var genreString = genres.join(" | ");
      this.categoryArray.push(genreString);
    }
  }

  showNoResults(){
    var noResultsDiv = <HTMLDivElement> document.getElementById("noResults");
    var resultsTable = <HTMLDivElement> document.getElementById("test");
    resultsTable.style.display = "none";


    noResultsDiv.style.backgroundColor = 	"#FFFF99"
    noResultsDiv.innerHTML = "No Records."
    noResultsDiv.style.width = "100%"
    noResultsDiv.style.height = "60px"
    noResultsDiv.style.marginTop = "20px"
    noResultsDiv.style.fontSize = "30px"
    noResultsDiv.style.textAlign = "center"
    noResultsDiv.style.verticalAlign = "center"

  }
  
  tabChanged(event: any){
    console.log(event);
    this.displayFavorites();
  }

  displayFavorites(){
    // Need to get most current array from local storage and set it to local variable

    // Update local variable
    //localStorage.clear();
    if(localStorage.getItem("favoritesMap") == "undefined"){

      // FavoritesMap doesn't exist yet in local storage, so no results
      var noFavorites = <HTMLDivElement> document.getElementById("noFavorites");
      var favoritesTable = <HTMLDivElement> document.getElementById("favoritesTable");
      favoritesTable.style.display = "none";

      noFavorites.style.backgroundColor = 	"#FFFF99"
      noFavorites.innerHTML = "No Records."
      noFavorites.style.width = "100%"
      noFavorites.style.height = "60px"
      noFavorites.style.marginTop = "20px"
      noFavorites.style.fontSize = "30px"
      noFavorites.style.textAlign = "center"
      noFavorites.style.verticalAlign = "center"
      return;
    }


    this.myMap = JSON.parse(localStorage.getItem("favoritesMap")!);
    console.log(this.myMap);
    

    var size = Object.keys(this.myMap).length;
    console.log("size: " + size)
    if(size == 0){
      console.log("Displaying 0 favorites")
      // Code for displaying noFavorite Div here
      var noFavorites = <HTMLDivElement> document.getElementById("noFavorites");
      var favoritesTable = <HTMLDivElement> document.getElementById("favoritesTable");
      favoritesTable.style.display = "none";

      noFavorites.style.backgroundColor = 	"#FFFF99"
      noFavorites.innerHTML = "No Records."
      noFavorites.style.width = "100%"
      noFavorites.style.height = "60px"
      noFavorites.style.marginTop = "20px"
      noFavorites.style.fontSize = "30px"
      noFavorites.style.textAlign = "center"
      noFavorites.style.verticalAlign = "center"
    }
    else{
      // Code for removing noFavorite Div here

      console.log("Displaying favorites")
      console.log("MyMap:");
      console.log(this.myMap);
      var noFavorites = <HTMLDivElement> document.getElementById("noFavorites");
      var favoritesTable = <HTMLDivElement> document.getElementById("favoritesTable");
      favoritesTable.style.display = "";

      noFavorites.style.backgroundColor = 	""
      noFavorites.innerHTML = ""
      noFavorites.style.width = ""
      noFavorites.style.height = ""
      noFavorites.style.marginTop = ""
      noFavorites.style.fontSize = ""
      noFavorites.style.textAlign = ""
      noFavorites.style.verticalAlign = ""
      // Iterate through myMap and add values to array
      this.favoritesArray = [];
      Object.keys(this.myMap).forEach((key) => {
        var value = this.myMap[key];
        this.favoritesArray.push(value)
    });
    }  
  }

  favoriteButton(event: any, category: any, el: any){
    // REMOVE from favorites    
    if(el.style.backgroundColor == "yellow"){
      el.style.color = "";

    }
    // ADD to favorites
    else{
      el.style.backgroundColor = "yellow";

      // Add to local storage

      // Get whole map from local storage, if null then create new, if not null then add new event
      // and put map back to local storage
      if(localStorage.getItem("favoritesMap") == "undefined"){
        console.log("Nothing added yet to local storage, so adding first event")
        this.myMap = {};
        this.myMap[1] =  event;
      }
      else{
        this.myMap = JSON.parse(localStorage.getItem("favoritesMap")!);
        var length = Object.keys(this.myMap).length;
        console.log("Length of map is: " + length)
        this.myMap[length + 1] =  event;
      }
      localStorage.setItem("favoritesMap", JSON.stringify(this.myMap));
      console.log(JSON.parse(localStorage.getItem("favoritesMap")!));
    }
  }

  removeFavoriteButton(event: any,  el: any, mapIndex: any){
    console.log("removing event from favorites")
    // Remove from map, then update local storage again

    // Need to find the key of the event, so iterate through myMap and find the key
    var key = Object.keys(this.myMap).filter((key) => {return this.myMap[key] === event})[0];
    console.log(key);
    delete this.myMap[key];
    console.log(this.myMap)
    localStorage.setItem("favoritesMap", JSON.stringify(this.myMap));

    this.displayFavorites();
  }

  goToEventURL(event: any){
    window.open(event.url, '_blank');
  }
}
