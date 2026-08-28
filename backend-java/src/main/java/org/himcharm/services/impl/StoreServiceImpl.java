package org.himcharm.services.impl;

import lombok.RequiredArgsConstructor;
import org.himcharm.dtos.StoreRequestDTO;
import org.himcharm.dtos.StoreResponseDTO;
import org.himcharm.entities.Store;
import org.himcharm.exceptions.DuplicateResourceException;
import org.himcharm.exceptions.ResourceNotFoundException;
import org.himcharm.repositories.StoreRepository;
import org.himcharm.services.StoreService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public StoreResponseDTO createStore(StoreRequestDTO request) {
        if (storeRepository.existsByStoreCode(request.getStoreCode())) {
            throw new DuplicateResourceException("Store code already exists: " + request.getStoreCode());
        }

        Store store = modelMapper.map(request, Store.class);
        return toResponse(storeRepository.save(store));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreResponseDTO> getAllStores() {
        return storeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StoreResponseDTO getStoreById(Long id) {
        return toResponse(findStore(id));
    }

    @Override
    @Transactional
    public StoreResponseDTO updateStore(Long id, StoreRequestDTO request) {
        Store store = findStore(id);
        if (storeRepository.existsByStoreCodeAndIdNot(request.getStoreCode(), id)) {
            throw new DuplicateResourceException("Store code already exists: " + request.getStoreCode());
        }

        store.setStoreCode(request.getStoreCode());
        store.setName(request.getName());
        store.setPhone(request.getPhone());
        store.setAddress(request.getAddress());
        store.setGoogleReviewUrl(request.getGoogleReviewUrl());
        if (request.getActive() != null) {
            store.setActive(request.getActive());
        }

        return toResponse(storeRepository.save(store));
    }

    @Override
    @Transactional
    public void deleteStore(Long id) {
        storeRepository.delete(findStore(id));
    }

    private Store findStore(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + id));
    }

    private StoreResponseDTO toResponse(Store store) {
        return modelMapper.map(store, StoreResponseDTO.class);
    }
}
