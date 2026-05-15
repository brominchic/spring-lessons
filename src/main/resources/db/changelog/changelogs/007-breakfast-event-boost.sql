insert into event_category_boosts (id, event_template_id, tag, multiplier, rationale, source_title, source_url)
values (39, 2, 'здоровье', 1.15, 'Полезные завтраки опираются на умеренный спрос на продукты с чистым составом, сухофрукты, мед, чай и натуральные перекусы.', 'X5 Dialog: сезонность продуктовых категорий', 'https://dialog.x5.ru/connect/education/sezonnost-sprosa-kak-menyajutsya-prodazhi-kategorij-v-techenie-goda/')
on conflict (id) do update
set event_template_id = excluded.event_template_id,
    tag = excluded.tag,
    multiplier = excluded.multiplier,
    rationale = excluded.rationale,
    source_title = excluded.source_title,
    source_url = excluded.source_url;
