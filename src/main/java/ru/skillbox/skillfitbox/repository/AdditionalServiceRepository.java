package ru.skillbox.skillfitbox.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.skillbox.skillfitbox.entity.AdditionalService;
import ru.skillbox.skillfitbox.mapper.row_mapper.AdditionalServiceRowMapper;

import java.util.List;
import java.util.UUID;

@Repository
@Slf4j
@RequiredArgsConstructor
public class AdditionalServiceRepository implements AdditionalServiceRepositoryInterface<AdditionalService> {
    private final JdbcTemplate jdbcTemplate;
    private static final AdditionalServiceRowMapper ADDITIONAL_SERVICE_MAPPER = new AdditionalServiceRowMapper();

    @Override
    public AdditionalService findById(String id) {
        String sql = "SELECT * FROM services WHERE id = ?";
        log.info("Find by id " + id);

        return jdbcTemplate.queryForObject(sql, ADDITIONAL_SERVICE_MAPPER, id);
    }

    @Override
    public List<AdditionalService> findAll() {
        String sql = "SELECT * FROM services ORDER BY name";
        log.info("Find all additional services");

        return jdbcTemplate.query(sql, ADDITIONAL_SERVICE_MAPPER);
    }

    @Override
    public void addServiceToClient(UUID clientId, String serviceId) {
        String sql = "INSERT INTO client_services (client_id, service_id)" +
                "VALUES (?, ?) ON CONFLICT (client_id, service_id) DO NOTHING";
        log.info("Add service to client");

        jdbcTemplate.update(sql, clientId, serviceId);
    }

    @Override
    public List<String> findClientNamesByServiceId(String serviceId) {
        String sql = "SELECT CONCAT(c.surname, ' ', c.name, ' ', COALESCE(c.patronymic, '')) as client_name " +
                "FROM clients c " +
                "INNER JOIN client_services cs ON c.id = cs.client_id " +
                "WHERE cs.service_id = ? ORDER BY client_name";
        log.info("Find client names by service id");

        return jdbcTemplate.query(sql, (rs, rowNum)
                -> rs.getString("client_name"), serviceId);
    }
}
