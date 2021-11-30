package com.plango.api.controller.impl;

import com.plango.api.common.exception.*;
import com.plango.api.controller.PinController;
import com.plango.api.dto.pin.CreatePinDto;
import com.plango.api.dto.pin.GetPinDto;
import com.plango.api.dto.pin.UpdatePinDto;
import com.plango.api.service.PinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PinControllerImpl implements PinController {
    @Autowired
    PinService pinService;

    @Override
    public ResponseEntity<GetPinDto> getPinById(Long id) {
        try {
            GetPinDto getPinDto = pinService.getPinById(id);
            return ResponseEntity.ok(getPinDto);
        } catch (CurrentUserAuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (PinNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<String> createPin(CreatePinDto pin) {
        try {
            pinService.createPin(pin);
            return ResponseEntity.ok("Pin created");
        } catch (PinAlreadyExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (InvalidRequestDataException | TravelNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (CurrentUserAuthorizationException | UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public ResponseEntity<String> updatePin(UpdatePinDto pin) {
        try {
            pinService.updatePin(pin);
            return ResponseEntity.ok("Pin updated");
        } catch (PinNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    @Override
    public ResponseEntity<String> deletePinById(Long id) {
        try {
            pinService.deletePinById(id);
            return ResponseEntity.ok("Pin deleted");
        } catch (PinNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
