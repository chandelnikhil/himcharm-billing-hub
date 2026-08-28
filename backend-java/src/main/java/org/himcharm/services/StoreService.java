package org.himcharm.services;

import org.himcharm.dtos.StoreRequestDTO;
import org.himcharm.dtos.StoreResponseDTO;

import java.util.List;

public interface StoreService {

    StoreResponseDTO createStore(StoreRequestDTO request);

    List<StoreResponseDTO> getAllStores();

    StoreResponseDTO getStoreById(Long id);

    StoreResponseDTO updateStore(Long id, StoreRequestDTO request);

    void deleteStore(Long id);
}
