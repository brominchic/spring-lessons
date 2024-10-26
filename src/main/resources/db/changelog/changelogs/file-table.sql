CREATE TABLE files (
    id BIGINT PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    file_data BYTEA NOT NULL,
    user_id bigint
            constraint fk_user_id
                references users
                on delete restrict
);
CREATE SEQUENCE public.id_seq
    INCREMENT 1
    START 3
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

