create table if not exists clubs (
    id uuid primary key,
    name text not null,
    description text not null,
    country text not null,
    city text not null,
    address text not null,
    logo text not null,
    created_at date default now(),
    updated_at date default now(),
    rule_set varchar(255) not null
)