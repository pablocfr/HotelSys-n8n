import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComprobanteList } from './comprobante-list';

describe('ComprobanteList', () => {
  let component: ComprobanteList;
  let fixture: ComponentFixture<ComprobanteList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComprobanteList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ComprobanteList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
