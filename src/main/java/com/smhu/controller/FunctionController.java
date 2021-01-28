package com.smhu.controller;

import com.smhu.helper.SyncHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class FunctionController {
    
    @GetMapping("/{address}")
    public ResponseEntity<?> convertPhysicalAddressToGeocoding(@PathVariable("address") String address) {
        SyncHelper sync = new SyncHelper();
        return new ResponseEntity<>(sync.convertAddressToMap(address, 0), HttpStatus.OK);
    }
}
