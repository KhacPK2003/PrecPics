create table if not exists "user"
(
    id            varchar(450) not null
        primary key,
    user_name     varchar(255),
    full_name     varchar(255),
    email         varchar(255),
    password_hash varchar(450),
    description   varchar(1000),
    address       varchar(255),
    website_url   varchar(255),
    twitter_url   varchar(255),
    instagram_url varchar(255),
    role_id       integer,
    is_active     boolean,
    is_admin      boolean,
    avatar_url    bytea
);

alter table "user"
    owner to postgres;

create table if not exists tag
(
    id   serial
        primary key,
    name varchar(255)
);

alter table tag
    owner to postgres;

create table if not exists content
(
    id          varchar(450) not null
        constraint gallery_pkey
            primary key,
    name        varchar,
    date_upload bigint,
    liked       integer,
    downloads   integer,
    views       integer,
    height      integer,
    width       integer,
    data_url    varchar,
    is_public   boolean,
    user_id     varchar(450)
        constraint gallery_user_id_fkey
            references "user"
            on delete cascade,
    type        boolean,
    asset_id    varchar
);

alter table content
    owner to postgres;

create table if not exists collection
(
    id          serial
        primary key,
    name        varchar(255),
    user_id     varchar(450)
        references "user"
            on delete cascade,
    date_create bigint,
    is_public   boolean
);

alter table collection
    owner to postgres;

create table if not exists incols
(
    id            serial
        primary key,
    content_id    varchar(450)
        references content
            on delete cascade,
    collection_id bigint
        references collection
            on delete cascade
);

alter table incols
    owner to postgres;

create table if not exists gottags
(
    id         serial
        primary key,
    content_id varchar(450)
        constraint gottags_gallery_id_fkey
            references content
            on delete cascade,
    tag_id     bigint
        references tag
            on delete cascade
);

alter table gottags
    owner to postgres;

create table if not exists follows
(
    id          serial
        primary key,
    follower_id varchar(450) not null
        references "user"
            on delete cascade,
    followee_id varchar(450) not null
        references "user"
            on delete cascade,
    unique (follower_id, followee_id)
);

alter table follows
    owner to postgres;

