import { Component, EventEmitter, OnInit} from '@angular/core';
import { FormBuilder, FormGroup, FormControl, Validators } from '@angular/forms';
import { Observable, Subject } from 'rxjs';
import { HttpService } from '../http.service';
import { map, startWith, takeUntil } from 'rxjs/operators';
import { Output } from '@angular/core';

@Component({
  selector: 'search-form',
  templateUrl: './search-form.component.html',
  styleUrls: ['./search-form.component.css']
})
export class SearchFormComponent implements OnInit {


  @Output() emitter = new EventEmitter<string>();

	submitted = false;
  userForm: FormGroup;

  keywordControl = new FormControl();
  locationControl = new FormControl({value: '', disabled: true});
  options: string[] = [];
  filteredOptions: string[] = [];
  lat: string;
  lng: string;
  destroy$: Subject<boolean> = new Subject<boolean>();

  
  
  constructor(private formBuilder: FormBuilder, private httpService: HttpService)
  {
  }

  ngOnInit()
  {
    this.userForm = this.formBuilder.group({
  		keyword: ['',  Validators.required, Validators.minLength(1)],
      location: ['', {value: '', disabled: true}, Validators.required, Validators.minLength(1)]
  	});

    // Get lat and long of user location          
    this.httpService.getIPinfo().pipe(takeUntil(this.destroy$)).subscribe(data => {
      var result = JSON.stringify(data);
      var jsonObject = JSON.parse(result);
      var string = jsonObject.loc.split(",");
      this.lat = string[0];
      this.lng = string[1];
    });
  }
    getLatLng(keywordVal:string, event:string, distance:string, units:string){
    var latitude = "";
    var longitude = "";
    	
    var address = "";
    
    var bar = <HTMLDivElement>document.getElementById("changeProgress");
    bar.style.width = "60%"
    // Using current location, so use already computed lat and long and skip call to geo api
    var currLocationCheckbox = <HTMLInputElement>document.getElementById('radio1');
    if(currLocationCheckbox.checked == true){
    		latitude = this.lat;
    		longitude = this.lng;
        var coords = [latitude, longitude];
        bar.style.width = "80%";
        this.httpService.retrieveData(keywordVal, event, distance, units, coords).pipe(takeUntil(this.destroy$)).subscribe(data => {
          var result = JSON.stringify(data);
          var jsonObject = JSON.parse(result);
          bar.style.width = "100%";
          var actualBar = <HTMLDivElement>document.getElementById("bar");
          actualBar.style.display = "none";
          this.emitter.emit(jsonObject);
       });
    	}
    	
    	else{
        var locationBox = <HTMLInputElement>document.getElementById("location");
			  address = locationBox.value;
			  var url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=AIzaSyBUVIzR6tIwztkSldSmGFP7X6C_-9QmQtw";          
		  
        // Call HTTP Service to get Lat and Lng from inputted location
        // Try with promise here: 
	    	this.httpService.getIPinfoCustom(address).pipe(takeUntil(this.destroy$)).subscribe(data => {
          var result = JSON.stringify(data);
          var jsonObject = JSON.parse(result);
          var latitude = (jsonObject.results[0].geometry.location.lat);
          var longitude = (jsonObject.results[0].geometry.location.lng);
          var coords = [latitude, longitude];
          bar.style.width = "80%";


          this.httpService.retrieveData(keywordVal, event, distance, units, coords).pipe(takeUntil(this.destroy$)).subscribe(data => {
            var result = JSON.stringify(data);
            var jsonObject = JSON.parse(result);
            bar.style.width = "100%";
            var actualBar = <HTMLDivElement>document.getElementById("bar");
            actualBar.style.display = "none";
            this.emitter.emit(jsonObject);
         });
        });          
    	} 
  }

  checkEmptyKeyword(){
    var field = <HTMLInputElement> document.getElementById('keyword');
    if(field.value == ""){
      this.keywordError()
    }
    else{
      return;
    }
  }

  // Make invalid inputs red
  keywordError(){
    var keyword = <HTMLInputElement> document.getElementById('keyword');
    keyword.style.border = "3px solid red";
    // Make div visible here
    var error = <HTMLDivElement> document.getElementById("keywordErrorMSG")
    error.innerHTML = "Please Enter a Keyword";
    error.style.color = "red";
  }

  checkEmptyLocation(){
    var field = <HTMLInputElement> document.getElementById('location');
    if(field.value == ""){
      this.locationError()
    }
    else{
      return;
    }
  }
  locationError(){
    var inputBox = <HTMLInputElement> document.getElementById("location")
    inputBox.style.border = "3px solid red";
    var error2 = <HTMLDivElement> document.getElementById("distanceErrorMSG")
    error2.innerHTML = "Please Enter a Location";
    error2.style.color = "red";
  }

  // Runs every time keyword is changed
  inputChanged(){
    var field = <HTMLInputElement> document.getElementById('keyword');
    var value = field.value;
    
    if(value == ""){
      this.filteredOptions = [];
      // Make search button available
      this.disableSubmit();
    }
    else{
      this.enableSubmit()
      // Get rid of red
      var keyword = <HTMLInputElement> document.getElementById('keyword');
      keyword.style.border = "";
    
      var error = <HTMLDivElement> document.getElementById("keywordErrorMSG")
      error.innerHTML = "";
    }
  

    // Make call to 
    //this.options = ['Four', 'Five', 'Six'];
    // var callResults = this.httpService.getAutocomplete(value);
    this.httpService.getAutocomplete(value).pipe(takeUntil(this.destroy$)).subscribe(data => {
      var result = JSON.stringify(data);
      var jsonObject = JSON.parse(result);
      this.filteredOptions = [];
      for(var i = 0; i < 5 && jsonObject.length; i++){
        if(jsonObject[i] != ""){
          this.filteredOptions.push(jsonObject[i].name)
        }
      }
    });
  }

  disableSubmit(){
    var button = <HTMLButtonElement> document.getElementById('submitBtn');
    button.disabled = true;
    return;
  }
  locationInputChanged(){
    var inputBox = <HTMLInputElement> document.getElementById("location")
    var keyword = <HTMLInputElement> document.getElementById("keyword")
    if(inputBox.value != ""){
      inputBox.style.border = "";
      var error = <HTMLDivElement> document.getElementById("distanceErrorMSG")
      error.innerHTML = "";

      // If keyword is not empty, enable submit as both are now not empty
      if(keyword.value != ""){
        this.enableSubmit();
      }
    }
    else{
      this.disableSubmit();
    }
  }

  onSubmit()
  {
    // Make progress Bar reappear
  	var progressBar = <HTMLDivElement> document.getElementById("bar")
    progressBar.style.display = "";
    this.submitted = true;

    var valid = this.validateFields();    
    if(valid){
      this.changeRedElements()

      // Call backend test
      this.retrieveData();
    }
  	else
  	{
      return;
  	}
  }

  retrieveData(){
    var bar = <HTMLDivElement>document.getElementById("changeProgress");
    bar.style.width = "20%";
    // Call httpService function, which then sends data to results child componentS
    var radius = <HTMLInputElement> document.getElementById("distance");
    var distance = "10";
    if(radius.value != ""){
      distance = radius.value
    }
    var select = <HTMLSelectElement>document.getElementById("category");
    var event = select.options[select.selectedIndex].text;

      
            
   var keyword = <HTMLInputElement> document.getElementById('keyword');
   var keywordVal = keyword.value;

   var unit = <HTMLSelectElement> document.getElementById("distanceUnits");
   var units = unit.options[unit.selectedIndex].text;
    
   bar.style.width = "40%";

   this.getLatLng(keywordVal, event, distance, units);
   
  }





  // Validate no fields are empty before submitting
  validateFields(){
    var keyword = <HTMLInputElement> document.getElementById('keyword');
    var radio1 = <HTMLInputElement> document.getElementById('radio1');
    var keywordVal = ""
    // Current location is checked, so other box doesnt need to be red if empty
    if(radio1.checked){
        if(keyword.value == ""){
            keyword.style.border = "3px solid red";
            // Make div visible here
            var error = <HTMLDivElement> document.getElementById("keywordErrorMSG")
            error.innerHTML = "Please Enter a Keyword";
            error.style.color = "red";
            return false;
        }
        else{
            return true;
        }
    }
    else{
        // Need to check for both boxes being undefined
        var isError = false;
        if(keyword.value == ""){
            keyword.style.border = "3px solid red";
            // Make div visible here
            var error = <HTMLDivElement> document.getElementById("keywordErrorMSG")
            error.innerHTML = "Please Enter a Keyword";
            error.style.color = "red";
            isError = true;
        }
        var inputBox = <HTMLInputElement> document.getElementById("location")
        if(inputBox.value == ""){
            inputBox.style.border = "3px solid red";
            var error2 = <HTMLDivElement> document.getElementById("distanceErrorMSG")
            error2.innerHTML = "Please Enter a Location";
            error2.style.color = "red";
            isError = true;
        }
        // No error, call retrieveData
        if(!isError){
            return true;
        }
        else{
            return false;
        }
    }
  }
  changeRedElements(){
    var keyword = <HTMLInputElement> document.getElementById('keyword');
    keyword.style.border = "";
    var inputBox = <HTMLInputElement> document.getElementById("location")
    inputBox.style.border = "";
    var error = <HTMLDivElement> document.getElementById("keywordErrorMSG")
    error.innerHTML = "";
    var error2 = <HTMLDivElement> document.getElementById("distanceErrorMSG")
    error2.innerHTML = "";
  }

  clearResults(){
    // Need to clear results and disable submit button
    this.disableSubmit();

    var results = <HTMLDivElement> document.getElementById("test");
    var noResults = <HTMLDivElement> document.getElementById("noResults");
    noResults.style.display = "none";
    results.style.display = "none";

    var location = <HTMLInputElement> document.getElementById("location");
    location.disabled = true;

  }

  enableSubmit(){
    var button = <HTMLButtonElement> document.getElementById('submitBtn');
    button.disabled = false;
  }

  changeDisabled(){
    var radio1 = <HTMLInputElement> document.getElementById("radio1");
    var locationInput  = <HTMLInputElement> document.getElementById("location");
    var keyword = <HTMLInputElement> document.getElementById("keyword");
    if(radio1.checked){
      locationInput.value = "";
      locationInput.disabled = true;
      
      if(keyword.value != ""){
        this.enableSubmit();
      }
    }
    // Other location box is checked, disable submit
    else{
        locationInput.disabled = false;
        this.disableSubmit();
    }
  }
};