package ru.effective.mobile.tasks_dashboard.config;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.effective.mobile.tasks_dashboard.util.CommentMapper;
import ru.effective.mobile.tasks_dashboard.util.TaskMapper;
import ru.effective.mobile.tasks_dashboard.util.UserMapper;

@Configuration
public class AppConfig {
    @Bean
    public TaskMapper taskMapper() {
        return Mappers.getMapper(TaskMapper.class);
    }

    @Bean
    public CommentMapper commentMapper() {
        return Mappers.getMapper(CommentMapper.class);
    }

    @Bean
    public UserMapper userMapper() {
        return Mappers.getMapper(UserMapper.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
