package ru.skillbox.skillfitbox.mapper.row_mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.skillbox.skillfitbox.entity.Client;
import ru.skillbox.skillfitbox.entity.Locker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class LockerRowMapper implements RowMapper<Locker> {
    @Override
    public Locker mapRow(ResultSet rs, int rowNum) throws SQLException {
        Locker locker = new Locker();
        locker.setId(rs.getObject("l_id", UUID.class));
        locker.setNumber(rs.getInt("l_number"));
        locker.setCreatedDatetime(rs.getObject("l_created_datetime", LocalDateTime.class));
        locker.setUpdatedDatetime(rs.getObject("l_updated_datetime", LocalDateTime.class));

        UUID clientId = rs.getObject("id", UUID.class);
        if (clientId != null) {
            Client client = new Client();
            client.setId(clientId);
            client.setName(rs.getString("surname"));
            client.setSurname(rs.getString("name"));
            client.setPatronymic(rs.getString("patronymic"));
            locker.setClient(client);
        }
        return locker;
    }
}
