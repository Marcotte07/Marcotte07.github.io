import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'hw8-nick-marcotte';

  JsonObject: Object;
  public resultsFound: Event;

  send(JsonObject: Object){
    this.JsonObject = JsonObject;
  }
}
