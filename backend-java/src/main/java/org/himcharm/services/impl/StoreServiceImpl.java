package org.himcharm.services.impl;

import lombok.RequiredArgsConstructor;
import org.himcharm.entities.Store;
import org.himcharm.exceptions.DuplicateResourceException;
import org.himcharm.exceptions.ResourceNotFoundException;
import org.himcharm.repositories.StoreRepository;
import org.himcharm.services.StoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    @Override
    @Transactional
    public Store createStore(Store store) {
        if (storeRepository.existsByStoreCode(store.getStoreCode())) {
            throw new DuplicateResourceException("Store code already exists: " + store.getStoreCode());
        }
        return storeRepository.save(store);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Store getStoreById(Long id) {
        return findStore(id);
    }

    @Override
    @Transactional
    public Store updateStore(Long id, Store updatedStore) {
        Store store = findStore(id);
        if (storeRepository.existsByStoreCodeAndIdNot(updatedStore.getStoreCode(), id)) {
            throw new DuplicateResourceException("Store code already exists: " + updatedStore.getStoreCode());
        }

        store.setStoreCode(updatedStore.getStoreCode());
        store.setName(updatedStore.getName());
        store.setPhone(updatedStore.getPhone());
        store.setAddress(updatedStore.getAddress());
        store.setGoogleReviewUrl(updatedStore.getGoogleReviewUrl());
        if (updatedStore.getActive() != null) {
            store.setActive(updatedStore.getActive());
        }

        return storeRepository.save(store);
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

}
