package ru.skillbox.skillfitbox.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.skillbox.skillfitbox.entity.Trainer;
import ru.skillbox.skillfitbox.mapper.row_mapper.TrainerRowMapper;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TrainerRepository implements TrainerRepositoryInterface<Trainer> {
    private final JdbcTemplate jdbcTemplate;
    private static final TrainerRowMapper TRAINER_ROW_MAPPER = new TrainerRowMapper();

    @Override
    public Trainer save(Trainer trainer) {
        String sql = "INSERT INTO trainers (id, surname, name, patronymic, phone," +
                " status, created_datetime, updated_datetime) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        UUID id = trainer.getId() != null ? trainer.getId() : UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        log.info("Insert trainer");

        jdbcTemplate.update(
                sql,
                id,
                trainer.getSurname(),
                trainer.getName(),
                trainer.getPatronymic(),
                trainer.getPhone(),
                trainer.getStatus().name(),
                now,
                now
        );
        trainer.setId(id);
        trainer.setCreatedDatetime(now);
        trainer.setUpdatedDatetime(now);
        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        String sql = "UPDATE trainers SET surname = ?, name = ?, patronymic = ?, phone = ?," +
                " status = ?, updated_datetime = ? WHERE id = ?";
        log.info("Update trainer");

        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update(
                sql,
                trainer.getSurname(),
                trainer.getName(),
                trainer.getPatronymic(),
                trainer.getPhone(),
                trainer.getStatus().name(),
                now,
                trainer.getId()
        );
        trainer.setUpdatedDatetime(now);

        log.info("Trainer id" + trainer.getId());
        return trainer;
    }

    @Override
    public Trainer findById(UUID id) {
        String sql = "SELECT * FROM trainers WHERE id = ?";
        log.info("Find by id");

        return jdbcTemplate.queryForObject(sql, TRAINER_ROW_MAPPER, id);
    }

    @Override
    public List<Trainer> findAll() {
        String sql = "SELECT * FROM trainers ORDER BY created_datetime DESC";
        log.info("Find all trainers");

        return jdbcTemplate.query(sql, TRAINER_ROW_MAPPER);
    }
}
