create table assortment_items
(
    id            bigint primary key,
    name          varchar(255)   not null,
    category      varchar(120)   not null,
    price         numeric(12, 2) not null,
    stock         integer        not null check (stock >= 0),
    monthly_sales integer        not null check (monthly_sales >= 0)
);

create table assortment_item_tags
(
    assortment_item_id bigint       not null references assortment_items (id) on delete cascade,
    tag                varchar(120) not null
);

create index idx_assortment_item_tags_item on assortment_item_tags (assortment_item_id);

create table event_templates
(
    id          bigint primary key,
    event_month integer      not null check (event_month between 1 and 12),
    event_day   integer      not null check (event_day between 1 and 31),
    title       varchar(255) not null,
    event_type  varchar(80)  not null,
    roi_percent integer      not null check (roi_percent >= 0)
);

create table event_template_tags
(
    event_template_id bigint       not null references event_templates (id) on delete cascade,
    tag               varchar(120) not null
);

create index idx_event_template_tags_template on event_template_tags (event_template_id);

create table event_template_channels
(
    event_template_id bigint       not null references event_templates (id) on delete cascade,
    channel           varchar(120) not null
);

create index idx_event_template_channels_template on event_template_channels (event_template_id);
