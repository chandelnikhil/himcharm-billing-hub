package org.himcharm.repositories;

import org.himcharm.entities.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    boolean existsByStoreCode(String storeCode);

    boolean existsByStoreCodeAndIdNot(String storeCode, Long id);
}
