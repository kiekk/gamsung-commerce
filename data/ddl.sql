create table brand
(
    created_at datetime(6)                           not null,
    deleted_at datetime(6)                           null,
    id         bigint auto_increment
        primary key,
    updated_at datetime(6)                           not null,
    name       varchar(255)                          null,
    status     enum ('ACTIVE', 'CLOSED', 'INACTIVE') null,
    constraint UKrdxh7tq2xs66r485cc8dkxt77
        unique (name)
);

create table coupon
(
    value      double                       null,
    created_at datetime(6)                  not null,
    deleted_at datetime(6)                  null,
    id         bigint auto_increment
        primary key,
    price      bigint                       null,
    updated_at datetime(6)                  not null,
    name       varchar(255)                 null,
    status     enum ('ACTIVE', 'INACTIVE')  null,
    type       enum ('FIXED', 'PERCENTAGE') null
);

create table event_handled
(
    event_type     tinyint                    null,
    partition_no   int                        not null,
    created_at     datetime(6)                not null,
    deleted_at     datetime(6)                null,
    id             bigint auto_increment
        primary key,
    offset_no      bigint                     not null,
    updated_at     datetime(6)                not null,
    consumer_group varchar(255)               null,
    event_id       varchar(255)               null,
    topic          varchar(255)               null,
    status         enum ('FAILED', 'SUCCEED') null,
    check (`event_type` between 0 and 5)
);

create table event_log
(
    partition_no int                                                                                                                                not null,
    created_at   datetime(6)                                                                                                                        not null,
    deleted_at   datetime(6)                                                                                                                        null,
    id           bigint auto_increment
        primary key,
    offset_no    bigint                                                                                                                             not null,
    updated_at   datetime(6)                                                                                                                        not null,
    event_id     varchar(255)                                                                                                                       null,
    payload      varchar(255)                                                                                                                       null,
    topic        varchar(255)                                                                                                                       null,
    event_type   enum ('PRODUCT_CHANGED', 'PRODUCT_LIKED', 'PRODUCT_STOCK_ADJUSTED', 'PRODUCT_STOCK_SOLD_OUT', 'PRODUCT_UNLIKED', 'PRODUCT_VIEWED') null
);

create table issued_coupon
(
    coupon_id  bigint                  not null,
    created_at datetime(6)             not null,
    deleted_at datetime(6)             null,
    id         bigint auto_increment
        primary key,
    issued_at  datetime(6)             null,
    updated_at datetime(6)             not null,
    used_at    datetime(6)             null,
    user_id    bigint                  not null,
    status     enum ('ACTIVE', 'USED') null
);

create table member
(
    created_at datetime(6)     not null,
    deleted_at datetime(6)     null,
    id         bigint auto_increment
        primary key,
    updated_at datetime(6)     not null,
    birthday   varchar(255)    null,
    email      varchar(255)    not null,
    name       varchar(255)    null,
    username   varchar(255)    not null,
    gender     enum ('F', 'M') null,
    constraint UKgc3jmn7c2abyo3wf6syln5t2i
        unique (username),
    constraint UKmbmcqelty0fbrvxp1q58dn57t
        unique (email)
);

create table orders
(
    amount           bigint                                              null,
    created_at       datetime(6)                                         not null,
    deleted_at       datetime(6)                                         null,
    id               bigint auto_increment
        primary key,
    issued_coupon_id bigint                                              null,
    price            bigint                                              null,
    total_price      bigint                                              null,
    updated_at       datetime(6)                                         not null,
    user_id          bigint                                              not null,
    address          varchar(255)                                        null,
    address_detail   varchar(255)                                        null,
    email            varchar(255)                                        null,
    mobile           varchar(255)                                        null,
    name             varchar(255)                                        null,
    order_key        varchar(255)                                        null,
    zip_code         varchar(255)                                        null,
    order_status     enum ('CANCELED', 'COMPLETED', 'FAILED', 'PENDING') null
);

create table order_item
(
    amount     bigint      null,
    created_at datetime(6) not null,
    deleted_at datetime(6) null,
    id         bigint auto_increment
        primary key,
    order_id   bigint      null,
    product_id bigint      not null,
    updated_at datetime(6) not null,
    constraint FKt4dc2r9nbvbujrljv3e23iibt
        foreign key (order_id) references orders (id)
);

create table payment
(
    card_type       tinyint                                             null,
    created_at      datetime(6)                                         not null,
    deleted_at      datetime(6)                                         null,
    id              bigint auto_increment
        primary key,
    order_id        bigint                                              not null,
    price           bigint                                              null,
    updated_at      datetime(6)                                         not null,
    user_id         bigint                                              not null,
    card_no         varchar(255)                                        null,
    order_key       varchar(255)                                        null,
    reason          varchar(255)                                        null,
    transaction_key varchar(255)                                        null,
    method          enum ('CARD', 'POINT')                              null,
    status          enum ('CANCELED', 'COMPLETED', 'FAILED', 'PENDING') null,
    check (`card_type` between 0 and 2)
);

create table point
(
    created_at datetime(6) not null,
    deleted_at datetime(6) null,
    id         bigint auto_increment
        primary key,
    point      bigint      not null,
    updated_at datetime(6) not null,
    user_id    bigint      not null,
    version    bigint      null,
    constraint UKhq78fkgkdiel9lydwbq8vu9bt
        unique (user_id)
);

create table product
(
    brand_id    bigint                                 not null,
    created_at  datetime(6)                            not null,
    deleted_at  datetime(6)                            null,
    id          bigint auto_increment
        primary key,
    price       bigint                                 null,
    updated_at  datetime(6)                            not null,
    description varchar(255)                           null,
    name        varchar(255)                           null,
    status      enum ('ACTIVE', 'DELETED', 'INACTIVE') null
);

create table product_like
(
    created_at datetime(6) not null,
    deleted_at datetime(6) null,
    id         bigint auto_increment
        primary key,
    product_id bigint      not null,
    updated_at datetime(6) not null,
    user_id    bigint      not null,
    constraint UK37ktf1twpt25939lf23nwwot
        unique (user_id, product_id)
);

create table product_like_count
(
    product_like_count int         not null,
    created_at         datetime(6) not null,
    deleted_at         datetime(6) null,
    product_id         bigint      not null
        primary key,
    updated_at         datetime(6) not null,
    version            bigint      null
);

create table product_metrics
(
    like_count  int         not null,
    metric_date date        null,
    sales_count int         not null,
    view_count  int         not null,
    created_at  datetime(6) not null,
    deleted_at  datetime(6) null,
    id          bigint auto_increment
        primary key,
    product_id  bigint      not null,
    updated_at  datetime(6) not null,
    constraint uk_product_metrics_product_date
        unique (product_id, metric_date)
);

create index idx_product_metrics_metric_date
    on product_metrics (metric_date);

create index idx_product_metrics_product_id
    on product_metrics (product_id);

create table product_rank_daily
(
    rank_date   date         null,
    rank_number int          not null,
    score       double       not null,
    created_at  datetime(6)  not null,
    deleted_at  datetime(6)  null,
    id          bigint auto_increment
        primary key,
    updated_at  datetime(6)  not null,
    product_id  varchar(255) null
);

create table product_rank_hourly
(
    rank_number    int          not null,
    score          double       not null,
    created_at     datetime(6)  not null,
    deleted_at     datetime(6)  null,
    id             bigint auto_increment
        primary key,
    rank_date_time datetime(6)  null,
    updated_at     datetime(6)  not null,
    product_id     varchar(255) null
);

create table stock
(
    quantity   int         not null,
    created_at datetime(6) not null,
    deleted_at datetime(6) null,
    product_id bigint      not null
        primary key,
    updated_at datetime(6) not null
);

