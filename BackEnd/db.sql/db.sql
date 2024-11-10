create table if not exists "user"
(
    id            varchar(450) not null
        primary key,
    user_name     varchar(255),
    full_name     varchar(255),
    email         varchar(255),
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
    location    varchar(255),
    date_upload bigint,
    liked       integer,
    downloads   integer,
    views       integer,
    height      integer,
    width        integer,
    data_url    varchar,
    is_public   boolean,
    asset_id    varchar,
    user_id     varchar(450)
        constraint gallery_user_id_fkey
            references "user"
            on delete cascade,
    type        boolean
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

-- temp data

insert into "user" (id, user_name, full_name, email, description, address, website_url, twitter_url, instagram_url, role_id, is_active, is_admin, avatar_url)
values
    ('C4fBR5W2usRdT2FqQWIFoXDoKqC2', 'thienweret', 'John Doe', 'thienweret@gmail.com', 'A passionate photographer', '123 Main St', 'http://johndoe.com', 'http://twitter.com/johndoe', 'http://instagram.com/johndoe', 1, true, true, null);

insert into tag (name)
values
    ('Nature'),
    ('Cityscape'),
    ('Wildlife'),
    ('Portrait'),
    ('Street');

-- Hình ảnh
insert into content (id, name, date_upload, liked, downloads, views, height, width, data_url, is_public, user_id, type, asset_id)
values
    ('two-ladies', 'Sample Image 70', 1731228982, 494, 83, 4578, 1200, 1500, 'https://res.cloudinary.com/drw51zabb/image/upload/v1693321234/two-ladies.jpg', true, 'C4fBR5W2usRdT2FqQWIFoXDoKqC2', true, 'dc3cd01e95724bedbe2d2569ed7e6871'),
    ('shoe', 'Sample Image 67', 1731228982, 465, 350, 8310, 1140, 1000, 'https://res.cloudinary.com/drw51zabb/image/upload/v1693321235/shoe.jpg', true, 'C4fBR5W2usRdT2FqQWIFoXDoKqC2', true, '67c03311d2b8481e7c86008e9097d669');

-- Video
insert into content (id, name, date_upload, liked, downloads, views, height, width, data_url, is_public, user_id, type, asset_id)
values
    ('cld-sample-video', 'Sample Video 60', 1731228982, 87, 440, 438, 2160, 3840, 'https://res.cloudinary.com/drw51zabb/video/upload/v1693321231/cld-sample-video.mp4', true, 'C4fBR5W2usRdT2FqQWIFoXDoKqC2', false, '04b827c545262cbd3bef1767aff93523'),
    ('elephants', 'Sample Video 34', 1731228982, 915, 48, 9628, 1080, 1920, 'https://res.cloudinary.com/drw51zabb/video/upload/v1693321228/elephants.mp4', true, 'C4fBR5W2usRdT2FqQWIFoXDoKqC2', false, '01493549ad5982584b7708a4acde472d');

insert into collection (name, user_id, date_create, is_public)
values
    ('Summer Vacation', 'C4fBR5W2usRdT2FqQWIFoXDoKqC2', 1731228982, true),
    ('Urban Adventures', 'C4fBR5W2usRdT2FqQWIFoXDoKqC2', 1731228982, false);

insert into incols (content_id, collection_id)
values
    ('cld-sample-video', 1),
    ('two-ladies', 1),
    ('shoe', 2);

insert into gottags (content_id, tag_id)
values
    ('shoe', 1),  -- Nature
    ('two-ladies', 2),  -- Cityscape
    ('cld-sample-video', 3),  -- Wildlife
    ('elephants', 4);  -- Wildlife
