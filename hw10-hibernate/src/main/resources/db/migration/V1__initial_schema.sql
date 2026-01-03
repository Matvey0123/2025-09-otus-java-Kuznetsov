create table address
(
    id bigserial,
    street varchar(255),
    primary key (id)
);

create table client
(
    id bigserial,
    name varchar(255),
    address_id bigint,
    foreign key (address_id) references address(id),
    primary key (id)
);

create table phone
(
    id bigserial,
    number varchar(255),
    client_id bigint,
    foreign key (client_id) references client(id),
    primary key (id)
);
