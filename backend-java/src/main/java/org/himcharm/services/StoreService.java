package org.himcharm.services;

import org.himcharm.entities.Store;

import java.util.List;

public interface StoreService {

    Store createStore(Store store);

    List<Store> getAllStores();

    Store getStoreById(Long id);

    Store updateStore(Long id, Store updatedStore);

    void deleteStore(Long id);
}
