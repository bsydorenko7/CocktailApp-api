package com.netcracker.coctail.dao;

import com.netcracker.coctail.dto.AuthenticationRequestDto;
import com.netcracker.coctail.model.User;
import com.netcracker.coctail.services.MailSender;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Data
@Slf4j
@PropertySource("classpath:SQLscripts.properties")
public class ForgotPasswordDaoImpl implements ForgotPasswordDao {
    private final MailSender mailSender;
    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${updateActivation}")
    private String updateActivation;
    @Value("${findByActivation}")
    private String findByActivation;
    @Value("${updatePassword}")
    private String updatePassword;


    @Async
    public void send(String email, String code) {
        String message = "Hello! To finish restore your password visit http://localhost:8080/api/auth/restore-password/change-password/'%s'";
        mailSender.send(email, "verification", String.format(message, code));

    }

    @Override
    public String sendCode(User user) {
        String activation = UUID.randomUUID().toString();
        jdbcTemplate.update(updateActivation, activation, user.getEmail());
        send(user.getEmail(), activation);
        return "Mail send";
    }

    @Override
    public AuthenticationRequestDto findByActivationCode(String code) {
        RowMapper<AuthenticationRequestDto> rowMapper = (rs, rowNum) ->
                new AuthenticationRequestDto(
                        rs.getString("email"),
                        rs.getString("password"));
        return jdbcTemplate.query(String.format(findByActivation, code), rowMapper).get(0);
    }

    public int changePassword(User user, String password) {
        return jdbcTemplate.update(updatePassword, passwordEncoder.encode(password), user.getEmail());
    }
}