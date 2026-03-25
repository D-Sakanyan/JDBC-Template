package ru.skillbox.skillfitbox.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.skillbox.skillfitbox.entity.AdditionalService;
import ru.skillbox.skillfitbox.entity.Client;
import ru.skillbox.skillfitbox.entity.Trainer;
import ru.skillbox.skillfitbox.entity.TrainerStatus;
import ru.skillbox.skillfitbox.entity.Locker;
import ru.skillbox.skillfitbox.mapper.row_mapper.ClientRowMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ClientRepository implements ClientRepositoryInterface<Client> {
    private final JdbcTemplate jdbcTemplate;
    private static final ClientRowMapper CLIENT_ROW_MAPPER = new ClientRowMapper();

    @Override
    public Client save(Client client) {
        String sql = "INSERT INTO clients (id, surname, name, patronymic, birthday, phone, email, " +
                "is_active, locker_id, created_datetime, updated_datetime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        log.info("Save client");

        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update(
                sql,
                id,
                client.getSurname(),
                client.getName(),
                client.getPatronymic(),
                client.getBirthday(),
                client.getPhone(),
                client.getEmail(),
                client.getIsActive(),
                client.getLocker().getId(),
                now,
                now
        );
        client.setId(id);
        client.setCreatedDatetime(now);
        client.setUpdatedDatetime(now);
        return client;
    }

    @Override
    public Client update(Client client) {
        String sql = "UPDATE clients SET surname = ?, name = ?, patronymic = ?, birthday = ?, phone = ?, email = ?, " +
                "is_active = ?, locker_id = ?, trainer_id = ?, updated_datetime = ? WHERE id = ?";
        log.info("Update client");

        LocalDateTime now = LocalDateTime.now();


        jdbcTemplate.update(
                sql,
                client.getSurname(),
                client.getName(),
                client.getPatronymic(),
                client.getBirthday(),
                client.getPhone(),
                client.getEmail(),
                client.getIsActive(),
                client.getLocker() != null ? client.getLocker().getId() : null,
                client.getTrainer() != null ? client.getTrainer().getId() : null,
                now,
                client.getId()
        );
        client.setUpdatedDatetime(now);
        return client;
    }

    @Override
    public Client findById(UUID id) {
        String sql = "SELECT * FROM clients WHERE id = ?";
        log.info("Find by client id");

        return jdbcTemplate.queryForObject(sql, CLIENT_ROW_MAPPER, id);
    }

    @Override
    public List<Client> findAll() {
        String sql = "SELECT * FROM clients ORDER BY created_datetime DESC";
        log.info("Find all clients");

        return jdbcTemplate.query(sql, CLIENT_ROW_MAPPER);
    }

    @Override
    public List<String> findClientNamesByTrainerId(UUID trainerId) {
        String sql = "SELECT CONCAT(surname, ' ', name, ' ', COALESCE(patronymic, ''))" +
                "as client_name FROM clients WHERE trainer_id = ? ORDER BY surname, name";
        log.info("Find client name by trainer id");

        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("client_name"), trainerId);
    }

    @Override
    public Client findClientDetailById(UUID id) {
        String sql = """
                SELECT
                    c.*,
                
                    t.id as trainer_id,
                    t.surname as trainer_surname,
                    t.name as trainer_name,
                    t.patronymic as trainer_patronymic,
                    t.phone as trainer_phone,
                    t.status as trainer_status,
                    t.created_datetime as trainer_created_datetime,
                    t.updated_datetime as trainer_updated_datetime,
                
                    l.id as locker_id,
                    l.number as locker_number,
                    l.created_datetime as locker_created_datetime,
                    l.updated_datetime as locker_updated_datetime,
                
                    s.id as service_id,
                    s.name as service_name,
                    s.created_datetime as service_created_datetime,
                    s.updated_datetime as service_updated_datetime
                FROM clients c
                LEFT JOIN trainers t ON c.trainer_id = t.id
                LEFT JOIN lockers l ON c.locker_id = l.id
                LEFT JOIN client_services cs ON c.id = cs.client_id
                LEFT JOIN services s ON cs.service_id = s.id
                WHERE c.id = ?
                ORDER BY s.name
                """;
        log.info("Find client details by id");

        return jdbcTemplate.query(sql, rs -> {
            Client client = null;
            List<AdditionalService> services = new ArrayList<>();

            while (rs.next()) {
                if (client == null) {
                    client = new Client();
                    client.setId(rs.getObject("id", UUID.class));
                    client.setSurname(rs.getString("surname"));
                    client.setName(rs.getString("name"));
                    client.setPatronymic(rs.getString("patronymic"));
                    client.setBirthday(rs.getObject("birthday", LocalDate.class));
                    client.setPhone(rs.getString("phone"));
                    client.setEmail(rs.getString("email"));
                    client.setIsActive(rs.getBoolean("is_active"));
                    client.setCreatedDatetime(rs.getObject("created_datetime", LocalDateTime.class));
                    client.setUpdatedDatetime(rs.getObject("updated_datetime", LocalDateTime.class));

                    UUID trainerId = rs.getObject("trainer_id", UUID.class);
                    if (trainerId != null) {
                        Trainer trainer = new Trainer();
                        trainer.setId(trainerId);
                        trainer.setSurname(rs.getString("trainer_surname"));
                        trainer.setName(rs.getString("trainer_name"));
                        trainer.setPatronymic(rs.getString("trainer_patronymic"));
                        trainer.setPhone(rs.getString("trainer_phone"));
                        trainer.setStatus(TrainerStatus.valueOf(rs.getString("trainer_status")));
                        trainer.setCreatedDatetime(rs.getObject("trainer_created_datetime", LocalDateTime.class));
                        trainer.setUpdatedDatetime(rs.getObject("trainer_updated_datetime", LocalDateTime.class));
                        client.setTrainer(trainer);
                    }

                    UUID lockerId = rs.getObject("locker_id", UUID.class);
                    if (lockerId != null) {
                        Locker locker = new Locker();
                        locker.setId(lockerId);
                        locker.setNumber(rs.getInt("locker_number"));
                        locker.setCreatedDatetime(rs.getObject("locker_created_datetime", LocalDateTime.class));
                        locker.setUpdatedDatetime(rs.getObject("locker_updated_datetime", LocalDateTime.class));
                        locker.setClient(client);
                        client.setLocker(locker);
                    }
                }

                String sId = rs.getString("service_id");
                if (sId != null) {
                    AdditionalService serviceInfo = new AdditionalService();
                    serviceInfo.setId(rs.getString("service_id"));
                    serviceInfo.setName(rs.getString("service_name"));
                    serviceInfo.setCreatedDatetime(rs.getObject("service_created_datetime", LocalDateTime.class));
                    serviceInfo.setUpdatedDatetime(rs.getObject("service_updated_datetime", LocalDateTime.class));

                    services.add(serviceInfo);
                }
            }

            if (client != null) {
                client.setServices(services);
            }
            return client;
        }, id);
    }
}
