package ru.skillbox.skillfitbox.mapper.row_mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.skillbox.skillfitbox.entity.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class TrainerRowMapper implements RowMapper<Trainer> {
    @Override
    public Trainer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Trainer trainer = new Trainer();
        trainer.setId(UUID.fromString(rs.getString("id")));
        trainer.setSurname(rs.getString("surname"));
        trainer.setName(rs.getString("name"));
        trainer.setPatronymic(rs.getString("patronymic"));
        trainer.setPhone(rs.getString("phone"));
        trainer.setStatus(TrainerStatus.valueOf(rs.getString("status")));
        trainer.setCreatedDatetime(rs.getObject("created_datetime", LocalDateTime.class));
        trainer.setUpdatedDatetime(rs.getObject("updated_datetime", LocalDateTime.class));
        return trainer;
    }
}
