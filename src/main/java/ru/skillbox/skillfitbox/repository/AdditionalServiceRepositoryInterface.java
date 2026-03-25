package ru.skillbox.skillfitbox.repository;

import java.util.Collection;
import java.util.UUID;

public interface AdditionalServiceRepositoryInterface<T>{
    T findById(String id);
    Collection<T> findAll();
    void addServiceToClient(UUID clientId, String serviceId);
    Collection<String> findClientNamesByServiceId(String serviceId);
}
