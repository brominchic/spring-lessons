create table users
(
    id            bigint not null
        constraint users_pkey
            primary key,
    fullname      varchar(255),
    total_balance bigint
);


create table account_types
(
    id   bigint not null
        constraint account_types_pkey
            primary key,
    name varchar
);

create table accounts
(
    number  bigint not null
        constraint accounts_pkey
            primary key,
    balance numeric(255, 2),
    type    bigint
        constraint fk_type
            references account_types
            on delete restrict,
    user_id bigint
        constraint fk_user_id
            references users
            on delete restrict
);


create table settings
(
    id   bigint not null
        constraint settings_pkey
            primary key,
    name varchar
);

create table users_settings
(
    user_id    bigint
        constraint fk_user_id
            references users
            on delete restrict,
    setting_id bigint
        constraint fk_settings
            references settings
            on delete restrict
);

create table operations
(
    from_account          bigint not null
        constraint fk_account_from
            references accounts
            on delete restrict,
    to_account            bigint
        constraint fk_account_to
            references accounts
            on delete restrict,
    sum                   numeric(255, 2),
    comment_for_operation varchar(255),
    id                    bigint not null
        constraint operations_pkey
            primary key
);

create table operations_applied_settings
(
    operation_id bigint
        constraint fk_operation_id
            references operations
            on delete restrict,
    setting_id   bigint
        constraint fk_setting_id
            references settings
            on delete restrict
);
