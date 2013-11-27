# --- First database schema

# --- !Ups

set ignorecase true;

create table member (
  id                        bigint not null,
  name						varchar(255) not null,
  email						varchar(255) not null,
  password                  varchar(255) not null,
  acl						varchar(20) not null,
  reportOn					timestamp,
  constraint pk_member primary key (id)
);

create table input (
  id                        bigint not null,
  name                      varchar(255) not null,
  createOn	                timestamp not null,
  amount					double not null,  
  memberId                  bigint not null,
  constraint pk_input primary key (id)
);

create sequence member_seq start with 1000;

create sequence input_seq start with 1000;

alter table member add constraint unq_email unique(email);

alter table input add constraint fk_input_member_1 foreign key (member_id) references member (id) on delete restrict on update restrict;
create index ix_input_member_1 on input (member_id);

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists member;

drop table if exists input;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists member_seq;

drop sequence if exists input_seq;

