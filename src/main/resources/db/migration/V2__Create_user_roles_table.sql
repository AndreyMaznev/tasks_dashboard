CREATE TABLE user_roles (
                            user_user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
                            roles VARCHAR(50) NOT NULL,
                            PRIMARY KEY (user_user_id, roles),
                            CONSTRAINT chk_roles_valid CHECK (roles IN ('ROLE_ADMIN', 'ROLE_USER'))
);