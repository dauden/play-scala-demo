# --- First database schema

create table member (
  id                        bigint not null auto_increment,
  name                      varchar(255) not null,
  email						varchar(255) not null,
  password					varchar(255) not null,
  constraint pk_member primary key (id))
;

create table input (
  id                        bigint not null auto_increment,  
  inputdate	                timestamp not null,
  amount              		bigint not null,
  member_id                	bigint not null,
  constraint pk_input primary key (id))
;

alter table input add constraint fk_input_member_1 foreign key (member_id) references member (id) on delete restrict on update restrict;
create index ix_input_member_1 on input (member_id);