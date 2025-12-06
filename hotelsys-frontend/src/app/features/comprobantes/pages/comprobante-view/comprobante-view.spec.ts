import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComprobanteView } from './comprobante-view';

describe('ComprobanteView', () => {
  let component: ComprobanteView;
  let fixture: ComponentFixture<ComprobanteView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComprobanteView]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ComprobanteView);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
