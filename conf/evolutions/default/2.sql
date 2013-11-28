# --- Sample dataset

# --- !Ups

insert into member (id,name,email,password,acl,reportOn) values (1,'Jenkins Lopez','admin1@mailinator.com','$2a$10$dvk718ZjcDdjIAbEptmm0.KUpHsrDq8OTnizBzVqWDvIu7vvAzA8W','admin',null);
insert into member (id,name,email,password,acl,reportOn) values (2,'Jack Leaning','member1@mailinator.com','$2a$10$dvk718ZjcDdjIAbEptmm0.KUpHsrDq8OTnizBzVqWDvIu7vvAzA8W','member',null);

# --- !Downs

delete from member;
delete from input;
