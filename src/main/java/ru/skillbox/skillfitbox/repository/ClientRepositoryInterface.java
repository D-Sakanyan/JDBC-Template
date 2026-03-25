package ru.skillbox.skillfitbox.repository;

import java.util.Collection;
import java.util.UUID;

public interface ClientRepositoryInterface<T> {
    T save(T client);
    T update(T client);
    T findById(UUID id);
    Collection<T> findAll();
    Collection<String> findClientNamesByTrainerId(UUID trainerId);
    T findClientDetailById(UUID id);
}
