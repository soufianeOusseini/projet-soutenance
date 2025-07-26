package com.transi.flex.tripSchedule.controller;

import com.transi.flex.tripSchedule.dto.ScheduleDTO;
import com.transi.flex.tripSchedule.dto.TripScheduleDTO;
import com.transi.flex.tripSchedule.model.TripSchedule;
import com.transi.flex.tripSchedule.service.TripScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("trip-schedules")
@RequiredArgsConstructor
public class TripScheduleController {
    private final TripScheduleService tripScheduleService;

    @GetMapping
    public ResponseEntity<List<ScheduleDTO>> getAllSchedules() {
        try {
            List<ScheduleDTO> schedules = tripScheduleService.getAllSchedules();
            return new ResponseEntity<>(schedules, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripSchedule> getScheduleById(@PathVariable Long id) {
        Optional<TripSchedule> schedule = tripScheduleService.getScheduleById(id);
        if (schedule.isPresent()) {
            return new ResponseEntity<>(schedule.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<TripSchedule>> getSchedulesByDate(@PathVariable String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            List<TripSchedule> schedules = tripScheduleService.getSchedulesByDate(localDate);
            return new ResponseEntity<>(schedules, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<ScheduleDTO> schedules = tripScheduleService.getSchedulesByDateRange(start, end);
            return new ResponseEntity<>(schedules, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<TripSchedule> createSchedule(@RequestBody TripScheduleDTO scheduleDTO) {
        try {
            TripSchedule schedule = tripScheduleService.createSchedule(scheduleDTO);
            return new ResponseEntity<>(schedule, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripSchedule> updateSchedule(@PathVariable Long id, @RequestBody TripScheduleDTO scheduleDTO) {
        try {
            TripSchedule schedule = tripScheduleService.updateSchedule(id, scheduleDTO);
            return new ResponseEntity<>(schedule, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        try {
            tripScheduleService.deleteSchedule(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}