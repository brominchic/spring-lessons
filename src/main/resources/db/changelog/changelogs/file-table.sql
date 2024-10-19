CREATE TABLE files (
    id BIGINT PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    file_data BYTEA NOT NULL,
    user_id bigint
            constraint fk_user_id
                references users
                on delete restrict
);

