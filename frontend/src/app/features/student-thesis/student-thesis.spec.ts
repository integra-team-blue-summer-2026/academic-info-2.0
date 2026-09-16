import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { of, throwError } from 'rxjs';

import { StudentThesis } from './student-thesis';
import { ThesisControllerService } from '../../core/api/services/thesisController.service';
import { ThesisDto } from '../../core/api/models/thesisDto';

describe('StudentThesis', () => {
  let component: StudentThesis;
  let fixture: ComponentFixture<StudentThesis>;
  let thesisService: {
    getThesisByStudent: ReturnType<typeof vi.fn>;
    uploadThesis: ReturnType<typeof vi.fn>;
  };

  const thesis: ThesisDto = {
    id: '11111111-1111-1111-1111-111111111111',
    studentId: '3fa85f64-5717-4562-b3fc-2c963f66afa6',
    fileName: 'thesis.pdf',
    status: 'UNCHECKED',
    rejectionMessage: undefined,
    uploadedAt: '2026-09-16T10:00:00',
    checkedAt: undefined,
  };

  beforeEach(async () => {
    thesisService = {
      getThesisByStudent: vi.fn(),
      uploadThesis: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [StudentThesis],
      providers: [
        {
          provide: ThesisControllerService,
          useValue: thesisService,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(StudentThesis);
    component = fixture.componentInstance;
  });

  describe('initialization', () => {
    it('should create', () => {
      thesisService.getThesisByStudent.mockReturnValue(of(thesis));

      fixture.detectChanges();

      expect(component).toBeTruthy();
    });

    it('should load the student thesis on initialization', () => {
      thesisService.getThesisByStudent.mockReturnValue(of(thesis));

      fixture.detectChanges();

      expect(thesisService.getThesisByStudent).toHaveBeenCalledWith(
        '3fa85f64-5717-4562-b3fc-2c963f66afa6'
      );

      expect(component['thesis']()).toEqual(thesis);
      expect(component['loading']()).toBe(false);
    });

    it('should set thesis to null when no thesis exists', () => {
      const error = new HttpErrorResponse({
        status: 404,
        statusText: 'Not Found',
      });

      thesisService.getThesisByStudent.mockReturnValue(
        throwError(() => error)
      );

      fixture.detectChanges();

      expect(component['thesis']()).toBeNull();
      expect(component['loading']()).toBe(false);
      expect(component['errorMessage']()).toBeNull();
    });

    it('should show an error when loading the thesis fails', () => {
      const error = new HttpErrorResponse({
        status: 500,
        statusText: 'Internal Server Error',
      });

      thesisService.getThesisByStudent.mockReturnValue(
        throwError(() => error)
      );

      fixture.detectChanges();

      expect(component['thesis']()).toBeNull();
      expect(component['loading']()).toBe(false);
      expect(component['errorMessage']()).toBe(
        'The thesis information could not be loaded.'
      );
    });
  });

  describe('file selection', () => {
    it('should select a PDF file', () => {
      const file = new File(
        ['test thesis'],
        'thesis.pdf',
        { type: 'application/pdf' }
      );

      const input = document.createElement('input');
      input.type = 'file';

      Object.defineProperty(input, 'files', {
        value: [file],
      });

      const event = {
        target: input,
      } as unknown as Event;

      component['onFileSelected'](event);

      expect(component['selectedFile']).toBe(file);
      expect(component['errorMessage']()).toBeNull();
    });

    it('should reject a non-PDF file', () => {
      const file = new File(
        ['test document'],
        'document.docx',
        { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' }
      );

      const input = document.createElement('input');
      input.type = 'file';

      Object.defineProperty(input, 'files', {
        value: [file],
      });

      const event = {
        target: input,
      } as unknown as Event;

      component['onFileSelected'](event);

      expect(component['selectedFile']).toBeNull();
      expect(component['errorMessage']()).toBe(
        'Please select a PDF file.'
      );
      expect(input.value).toBe('');
    });

    it('should clear the selected file when no file is selected', () => {
      component['selectedFile'] = new File(
        ['test'],
        'thesis.pdf',
        { type: 'application/pdf' }
      );

      const input = document.createElement('input');
      input.type = 'file';

      Object.defineProperty(input, 'files', {
        value: [],
      });

      const event = {
        target: input,
      } as unknown as Event;

      component['onFileSelected'](event);

      expect(component['selectedFile']).toBeNull();
    });
  });

  describe('upload', () => {
    beforeEach(() => {
      component['selectedFile'] = new File(
        ['test thesis'],
        'thesis.pdf',
        { type: 'application/pdf' }
      );
    });

    it('should not upload when no file is selected', () => {
      component['selectedFile'] = null;

      component['upload']();

      expect(thesisService.uploadThesis).not.toHaveBeenCalled();
      expect(component['errorMessage']()).toBe(
        'Please select a PDF file first.'
      );
    });

    it('should upload the selected thesis', () => {
      thesisService.uploadThesis.mockReturnValue(of(thesis));

      const selectedFile = new File(
        ['test thesis'],
        'thesis.pdf',
        { type: 'application/pdf' }
      );

      component['selectedFile'] = selectedFile;

      component['upload']();

      expect(thesisService.uploadThesis).toHaveBeenCalledWith(
        '3fa85f64-5717-4562-b3fc-2c963f66afa6',
        selectedFile
      );

      expect(component['thesis']()).toEqual(thesis);
      expect(component['selectedFile']).toBeNull();
      expect(component['uploading']()).toBe(false);
      expect(component['errorMessage']()).toBeNull();
    });

    it('should show an error when upload fails', () => {
      const error = new HttpErrorResponse({
        status: 400,
        statusText: 'Bad Request',
      });

      thesisService.uploadThesis.mockReturnValue(
        throwError(() => error)
      );

      component['upload']();

      expect(component['uploading']()).toBe(false);
      expect(component['errorMessage']()).toBe(
        'The thesis could not be uploaded. Please try again.'
      );
    });
  });

  describe('status helpers', () => {
    it('should return the correct label for unchecked status', () => {
      expect(component['getStatusLabel']('UNCHECKED')).toBe('Unchecked');
    });

    it('should return the correct label for checked status', () => {
      expect(component['getStatusLabel']('CHECKED')).toBe('Checked');
    });

    it('should return the correct label for rejected status', () => {
      expect(component['getStatusLabel']('REJECTED')).toBe('Rejected');
    });

    it('should return warn severity for unchecked status', () => {
      expect(component['getStatusSeverity']('UNCHECKED')).toBe('warn');
    });

    it('should return success severity for checked status', () => {
      expect(component['getStatusSeverity']('CHECKED')).toBe('success');
    });

    it('should return danger severity for rejected status', () => {
      expect(component['getStatusSeverity']('REJECTED')).toBe('danger');
    });
  });
});
