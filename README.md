# Hướng dẫn khởi chạy
### FrontEnd
```
cd FrontEnd
npm install
npm rundev
```
### BackEnd
```
cd BackEnd
docker-compose up -d
```
## 🗄️ Kết nối cơ sở dữ liệu (PostgreSQL)
Sau khi chạy lệnh khởi tạo Docker container cho Backend:
```
cd BackEnd
docker-compose up -d
```
Cơ sở dữ liệu PostgreSQL sẽ tự động được khởi tạo trong Docker.
##🔌 Kết nối bằng DataGrip
1. Mở DataGrip
   
2. Open folder BackEnd

3. Click chuột phải vào postgres@localhost -> properties -> General điền các thông tin:
```
Trường-----Giá trị
Host:	localhost
Port:	5432
Database:	postgres
User:	postgres
Password:	postgres
```
### Cơ sở dữ liệu
<details>
<summary>📜 Xem chi tiết cấu trúc database (SQL)</summary>

```sql
create sequence follows_id_seq
    as integer;

alter sequence follows_id_seq owner to postgres;

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
    avatar_url    varchar
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
    width       integer,
    data_url    varchar,
    is_public   boolean,
    asset_id    varchar,
    user_id     varchar(450)
        constraint gallery_user_id_fkey
            references "user"
            on delete cascade,
    type        boolean,
    description varchar,
    data_byte   varchar
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

create table if not exists followers
(
    id          integer default nextval('follows_id_seq'::regclass) not null
        constraint follows_pkey
            primary key,
    follower_id varchar(450)                                        not null
        constraint follows_follower_id_fkey
            references "user"
            on delete cascade,
    user_id     varchar
        constraint followers_user_id_fk
            references "user"
            on delete cascade
);

alter table followers
    owner to postgres;

alter sequence follows_id_seq owned by followers.id;

create table if not exists followees
(
    id          serial
        constraint followees_pk
            primary key,
    user_id     varchar
        constraint followees_user_id_fk
            references "user"
            on delete cascade,
    followee_id varchar
        constraint followees_user_id_fk_2
            references "user"
            on delete cascade
);

alter table followees
    owner to postgres;

create table if not exists comment
(
    id          serial
        constraint comment_pk
            primary key,
    content     varchar not null,
    user_id     varchar
        constraint comment_user_id_fk
            references "user"
            on delete cascade,
    content_id  varchar
        constraint comment_content_id_fk
            references content
            on delete cascade,
    date_create bigint
);

alter table comment
    owner to postgres;
```
</details>

# 🚀 Triển khai
### BackEnd
```
cd BackEnd
run BackendApplication
```
### FrontEnd
```
cd FrontEnd
npm run dev
```
### CheckContentNSLW
```
run app.py
```
