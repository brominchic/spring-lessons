update assortment_items
set category = 'Мясо и птица'
where id = 180980
  and category = 'Бакалея'
  and lower(name) like '%говядина%';

update assortment_items
set category = 'Мясо и птица'
where category = 'Бакалея'
  and (
      lower(name) like '%говяд%'
      or lower(name) like '%ростбиф%'
      or lower(name) like '%паштет из говядины%'
      or lower(url_product) like '%mjas%'
      or lower(url_product) like '%myas%'
      or lower(url_product) like '%pashtety%'
  );
