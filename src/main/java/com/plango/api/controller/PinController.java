package com.plango.api.controller;

import com.plango.api.dto.pin.CreatePinDto;
import com.plango.api.dto.pin.GetPinDto;
import com.plango.api.dto.pin.UpdatePinDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/pin")
public interface PinController {
    @GetMapping(path = "/{id}")
    ResponseEntity<GetPinDto> getPinById(@PathVariable Long id);

    @PostMapping(path = "")
    ResponseEntity<String> createPin(@RequestBody CreatePinDto pin);

    @PutMapping(path = "")
    ResponseEntity<String> updatePin(@RequestBody UpdatePinDto pin);

    @DeleteMapping(path = "/{id}")
    ResponseEntity<String> deletePinById(@PathVariable Long id);


}
