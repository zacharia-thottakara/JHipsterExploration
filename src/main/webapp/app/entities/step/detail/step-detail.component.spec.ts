import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { StepDetailComponent } from './step-detail.component';

describe('Step Management Detail Component', () => {
  let comp: StepDetailComponent;
  let fixture: ComponentFixture<StepDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StepDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ step: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(StepDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(StepDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load step on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.step).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
