import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { vi } from 'vitest';

import { RoomAvailability } from './room-availability';
import { RoomControllerService } from '../../core/api/services/roomController.service';
import { RoomAvailabilityControllerService } from '../../core/api/services/roomAvailabilityController.service';
import { RoomDto } from '../../core/api/models/roomDto';
import { RoomAvailabilityDto } from '../../core/api/models/roomAvailabilityDto';

describe('RoomAvailability', () => {
  let component: RoomAvailability;
  let fixture: ComponentFixture<RoomAvailability>;

  const roomService = {
    getAllRooms: vi.fn(),
    createRoom: vi.fn(),
    updateRoom: vi.fn(),
    deleteRoom: vi.fn()
  };

  const availabilityService = {
    getAllRoomAvailabilities: vi.fn(),
    createRoomAvailability: vi.fn(),
    deleteRoomAvailability: vi.fn()
  };

  const room: RoomDto = {
    id: '11111111-1111-1111-1111-111111111111',
    roomName: 'A101',
    zone: 'A'
  };

  const availability: RoomAvailabilityDto = {
    id: '22222222-2222-2222-2222-222222222222',
    roomId: '11111111-1111-1111-1111-111111111111',
    dayOfWeek: RoomAvailabilityDto.DayOfWeekEnum.Monday,
    startTime: '08:00:00',
    endTime: '10:00:00'
  };

  beforeEach(async () => {
    vi.clearAllMocks();

    roomService.getAllRooms.mockReturnValue(of([]));
    roomService.createRoom.mockReturnValue(of(room));
    roomService.updateRoom.mockReturnValue(of(room));
    roomService.deleteRoom.mockReturnValue(of(undefined));

    availabilityService.getAllRoomAvailabilities.mockReturnValue(of([]));
    availabilityService.createRoomAvailability.mockReturnValue(of(availability));
    availabilityService.deleteRoomAvailability.mockReturnValue(of(undefined));

    await TestBed.configureTestingModule({
      imports: [RoomAvailability],
      providers: [
        {
          provide: RoomControllerService,
          useValue: roomService
        },
        {
          provide: RoomAvailabilityControllerService,
          useValue: availabilityService
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RoomAvailability);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load rooms and availabilities on init', () => {
    fixture.detectChanges();

    expect(roomService.getAllRooms).toHaveBeenCalled();
    expect(
      availabilityService.getAllRoomAvailabilities
    ).toHaveBeenCalled();
  });

  it('should create a room', () => {
    const componentAccess = component as any;

    componentAccess.roomForm.setValue({
      roomName: 'A101',
      zone: 'A'
    });

    componentAccess.saveRoom();

    expect(roomService.createRoom).toHaveBeenCalledWith({
      roomName: 'A101',
      zone: 'A'
    });
  });

  it('should not create a room when the form is invalid', () => {
    const componentAccess = component as any;

    componentAccess.roomForm.setValue({
      roomName: '',
      zone: ''
    });

    componentAccess.saveRoom();

    expect(roomService.createRoom).not.toHaveBeenCalled();
  });

  it('should open the availability dialog for a room', () => {
    const componentAccess = component as any;

    componentAccess.openAvailabilityDialog(room);

    expect(componentAccess.selectedRoom()).toEqual(room);
    expect(componentAccess.availabilityDialogVisible()).toBe(true);
  });

  it('should create room availability', () => {
    const componentAccess = component as any;

    componentAccess.selectedRoom.set(room);

    componentAccess.availabilityForm.setValue({
      dayOfWeek: RoomAvailabilityDto.DayOfWeekEnum.Monday,
      startTime: '08:00',
      endTime: '10:00'
    });

    componentAccess.saveAvailability();

    expect(
      availabilityService.createRoomAvailability
    ).toHaveBeenCalledWith({
      roomId: room.id,
      dayOfWeek: RoomAvailabilityDto.DayOfWeekEnum.Monday,
      startTime: '08:00',
      endTime: '10:00'
    });
  });

  it('should not create availability when end time is earlier than start time', () => {
    const componentAccess = component as any;

    componentAccess.selectedRoom.set(room);

    componentAccess.availabilityForm.setValue({
      dayOfWeek: RoomAvailabilityDto.DayOfWeekEnum.Monday,
      startTime: '12:00',
      endTime: '10:00'
    });

    componentAccess.saveAvailability();

    expect(
      availabilityService.createRoomAvailability
    ).not.toHaveBeenCalled();
  });

  it('should format the day correctly', () => {
    const componentAccess = component as any;

    expect(componentAccess.formatDay('MONDAY')).toBe('Monday');
  });

  it('should format the time correctly', () => {
    const componentAccess = component as any;

    expect(componentAccess.formatTime('08:30:00')).toBe('08:30');
  });
});
