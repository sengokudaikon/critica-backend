create table if not exists user_verification_codes (
    id uuid primary key,
    code varchar(6) not null,
    user_id uuid not null,
    expires_at bigint not null,
    foreign key (user_id) references users (id) ON DELETE CASCADE ON UPDATE CASCADE
);