package ru.skillbox.skillfitbox.repository;

import java.util.Collection;
import java.util.UUID;

public interface LockerRepositoryInterface<T> {
    T findById(UUID id);
    Collection<T> findAllWithClientInfo();
    void update(T locker);
}
