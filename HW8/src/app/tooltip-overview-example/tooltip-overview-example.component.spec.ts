import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TooltipOverviewExampleComponent } from './tooltip-overview-example.component';

describe('TooltipOverviewExampleComponent', () => {
  let component: TooltipOverviewExampleComponent;
  let fixture: ComponentFixture<TooltipOverviewExampleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TooltipOverviewExampleComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TooltipOverviewExampleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
