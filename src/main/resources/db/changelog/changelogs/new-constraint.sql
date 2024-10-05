alter table accounts
   add constraint check_positive check (balance >= 0);