
drop table if exists users;
create table users (
    id int auto_increment primary key,
    username varchar(64) not null,
    password varchar(64) not null,
    email varchar(64) not null,
    phone varchar(64),
    role char(1),
    status char(1) not null,
    create_time timestamp default current_timestamp,
    update_time timestamp default current_timestamp on update current_timestamp
);

insert into users (username, password, email, phone, role, status)
values ('admin', '$2a$10$FVUxA.1Y7x2N/ggwslNpPO15uPQXM/a24mRx7WRH0Ac4tWX5mv7p6', '', '13800138000', '0', '0');

