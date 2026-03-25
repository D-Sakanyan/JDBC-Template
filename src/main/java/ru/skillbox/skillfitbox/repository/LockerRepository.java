package ru.skillbox.skillfitbox.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.skillbox.skillfitbox.entity.Client;
import ru.skillbox.skillfitbox.entity.Locker;
import ru.skillbox.skillfitbox.mapper.row_mapper.LockerRowMapper;

import java.time.LocalDateTime;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LockerRepository implements LockerRepositoryInterface<Locker> {
    private final JdbcTemplate jdbcTemplate;
    private static final LockerRowMapper LOCKER_ROW_MAPPER = new LockerRowMapper();

    @Override
    public Locker findById(UUID id) {
        String sql = """
                SELECT c.*,
                    l.id as l_id,
                    l.number as l_number,
                    l.created_datetime as l_created_datetime,
                    l.updated_datetime as l_updated_datetime
                FROM lockers l LEFT JOIN clients c ON l.client_id = c.id WHERE l.id = ?
                """;
        log.info("Find by id");

        return jdbcTemplate.queryForObject(sql, LOCKER_ROW_MAPPER, id);
    }

    @Override
    public List<Locker> findAllWithClientInfo() {
        String sql = """
                SELECT c.*,
                    l.id as l_id,
                    l.number as l_number,
                    l.created_datetime as l_created_datetime,
                    l.updated_datetime as l_updated_datetime
                FROM lockers l LEFT JOIN clients c ON l.client_id = c.id ORDER BY l.number
                """;
        log.info("Find all with client info");

        return jdbcTemplate.query(sql, LOCKER_ROW_MAPPER);
    }

    @Override
    public void update(Locker locker) {
        String sql = "UPDATE lockers SET client_id = ?, updated_datetime = ? WHERE id = ?";
        log.info("Update locker " + locker.getId());

        UUID clientId = Optional.ofNullable(locker.getClient())
                .map(Client::getId)
                .orElse(null);
        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update(
                sql,
                clientId,
                now,
                locker.getId()
        );
    }
}
