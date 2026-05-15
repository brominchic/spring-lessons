create table if not exists farmers
(
    id          bigint primary key,
    shop_name   varchar(255) not null,
    description text         not null,
    region      varchar(120) not null,
    url_farmer  text         not null
);

insert into farmers (id, shop_name, description, region, url_farmer)
select distinct on (organization_id)
       organization_id,
       shop_name,
       farmer_description,
       region,
       url_farmer
from assortment_items
where organization_id is not null
order by organization_id, id
on conflict (id) do update
set shop_name = excluded.shop_name,
    description = excluded.description,
    region = excluded.region,
    url_farmer = excluded.url_farmer;

alter table assortment_items drop constraint if exists fk_assortment_items_farmer;
alter table assortment_items add constraint fk_assortment_items_farmer foreign key (organization_id) references farmers (id);

drop index if exists idx_assortment_items_organization;
create index if not exists idx_assortment_items_farmer on assortment_items (organization_id);

alter table assortment_items drop column if exists shop_name;
alter table assortment_items drop column if exists farmer_description;
alter table assortment_items drop column if exists region;
alter table assortment_items drop column if exists url_farmer;
