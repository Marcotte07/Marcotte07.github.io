import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'tooltip-overview-example',
  templateUrl: './tooltip-overview-example.component.html',
  styleUrls: ['./tooltip-overview-example.component.css']
})
export class TooltipOverviewExampleComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
    console.log("Button Initialized")
  }

}
