package org.himcharm.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.himcharm.dtos.ApiResponse;
import org.himcharm.dtos.StoreRequestDTO;
import org.himcharm.dtos.StoreResponseDTO;
import org.himcharm.services.StoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<ApiResponse> createStore(@Valid @RequestBody StoreRequestDTO request) {
        StoreResponseDTO store = storeService.createStore(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(HttpStatus.CREATED.value(), "Store created successfully", store)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllStores() {
        List<StoreResponseDTO> stores = storeService.getAllStores();
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Stores fetched successfully", stores)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getStoreById(@PathVariable Long id) {
        StoreResponseDTO store = storeService.getStoreById(id);
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Store fetched successfully", store)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateStore(
            @PathVariable Long id,
            @Valid @RequestBody StoreRequestDTO request
    ) {
        StoreResponseDTO store = storeService.updateStore(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Store updated successfully", store)
        );
    }

    /*
    *  No capability to delete for now...
    * */
//    @DeleteMapping("/{id}")
//    public ResponseEntity<ApiResponse> deleteStore(@PathVariable Long id) {
//        storeService.deleteStore(id);
//        return ResponseEntity.ok(
//                ApiResponse.success(HttpStatus.OK.value(), "Store deleted successfully", null)
//        );
//    }


}
