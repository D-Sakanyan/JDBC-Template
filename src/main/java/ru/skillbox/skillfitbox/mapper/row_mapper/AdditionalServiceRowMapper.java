package ru.skillbox.skillfitbox.mapper.row_mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.skillbox.skillfitbox.entity.AdditionalService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AdditionalServiceRowMapper implements RowMapper<AdditionalService> {
    @Override
    public AdditionalService mapRow(ResultSet rs, int rowNum) throws SQLException {
        AdditionalService additionalService = new AdditionalService();
        additionalService.setId(rs.getString("id"));
        additionalService.setName(rs.getString("name"));
        additionalService.setCreatedDatetime(rs.getObject("created_datetime", LocalDateTime.class));
        additionalService.setUpdatedDatetime(rs.getObject("updated_datetime", LocalDateTime.class));
        additionalService.setPrice(rs.getInt("price"));
        return additionalService;
    }
}
