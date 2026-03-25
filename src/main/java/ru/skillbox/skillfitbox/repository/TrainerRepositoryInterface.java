package ru.skillbox.skillfitbox.repository;

import java.util.Collection;
import java.util.UUID;

public interface TrainerRepositoryInterface<T> {
    T save(T trainer);
    T update(T trainer);
    T findById(UUID id);
    Collection<T> findAll();
}
