alter table event_templates add column if not exists start_date date;
alter table event_templates add column if not exists end_date date;

create index if not exists idx_event_templates_date_range on event_templates (start_date, end_date);
