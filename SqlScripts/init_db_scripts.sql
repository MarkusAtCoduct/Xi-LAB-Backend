
CREATE SEQUENCE roles_id_seq;
CREATE SEQUENCE users_id_seq;
CREATE SEQUENCE refreshtoken_id_seq;

CREATE SEQUENCE method_id_seq;
CREATE SEQUENCE method_rating_id_seq;
CREATE SEQUENCE method_comment_id_seq;

CREATE SEQUENCE method_set_details_id_seq;
CREATE SEQUENCE method_set_id_seq;

CREATE SEQUENCE process_set_id_seq;
CREATE SEQUENCE process_set_details_id_seq;

CREATE SEQUENCE user_badge_id_seq;


-- nextval('refreshtoken_id_seq'::regclass)

-- -- Shift List
-- DROP SEQUENCE IF EXISTS shift_id_seq;
-- CREATE SEQUENCE shift_id_seq;

-- DROP TABLE IF EXISTS shift;
-- CREATE TABLE shift (
--                        "id" int8 NOT NULL DEFAULT nextval('shift_id_seq'),
--                        "code" varchar(200) COLLATE "pg_catalog"."default",
--                        "name" varchar(200) COLLATE "pg_catalog"."default"
-- );
-- ALTER TABLE shift ADD CONSTRAINT shift_pkey PRIMARY KEY ("id");


-- -- Line List
-- DROP SEQUENCE IF EXISTS line_id_seq;
-- CREATE SEQUENCE line_id_seq;

-- DROP TABLE IF EXISTS line;
-- CREATE TABLE line (
--                       "id" int8 NOT NULL DEFAULT nextval('line_id_seq'),
--                       "code" varchar(200) COLLATE "pg_catalog"."default",
--                       "name" varchar(200) COLLATE "pg_catalog"."default"
-- );
-- ALTER TABLE line ADD CONSTRAINT line_pkey PRIMARY KEY ("id");


-- -- Branch List
-- DROP SEQUENCE IF EXISTS branch_id_seq;
-- CREATE SEQUENCE branch_id_seq;

-- DROP TABLE IF EXISTS branch;
-- CREATE TABLE branch (
--                         "id" int8 NOT NULL DEFAULT nextval('branch_id_seq'),
--                         "code" varchar(200) COLLATE "pg_catalog"."default",
--                         "name" varchar(1000) COLLATE "pg_catalog"."default"
-- );
-- ALTER TABLE branch ADD CONSTRAINT branch_pkey PRIMARY KEY ("id");



-- -- Scan Session
-- DROP SEQUENCE IF EXISTS scan_session_id_seq;
-- CREATE SEQUENCE scan_session_id_seq;

-- DROP TABLE IF EXISTS scan_session;
-- CREATE TABLE scan_session (
--                               "id" int8 NOT NULL DEFAULT nextval('scan_session_id_seq'),
--                               "user_id" int8 NOT NULL,
--                               "line_code" varchar(200) COLLATE "pg_catalog"."default",
--                               "shift_code" varchar(200) COLLATE "pg_catalog"."default",
--                               "branch_code" varchar(200) COLLATE "pg_catalog"."default",
--                               "product_code" varchar(200) COLLATE "pg_catalog"."default",
--                               "device_id" varchar(200) COLLATE "pg_catalog"."default",
--                               "fiif" varchar(1000) COLLATE "pg_catalog"."default",
--                               "created_on" timestamp(6),
--                               "expired_date" timestamp(6),
--                               "total_scan" int4
-- );
-- ALTER TABLE scan_session ADD CONSTRAINT scan_session_pkey PRIMARY KEY ("id");


-- -- Scan Item
-- DROP SEQUENCE IF EXISTS scan_item_id_seq;
-- CREATE SEQUENCE scan_item_id_seq;

-- DROP TABLE IF EXISTS scan_item;
-- CREATE TABLE scan_item (
--                            "id" int8 NOT NULL DEFAULT nextval('scan_item_id_seq'),
--                            "session_id" int8 NOT NULL,
--                            "qr_code" varchar(2000) COLLATE "pg_catalog"."default",
--                            "scan_time" timestamp(6),
--                            "total_scan" int4
-- );
-- ALTER TABLE scan_item ADD CONSTRAINT scan_item_pkey PRIMARY KEY ("id");









/*
Host: host.docker.internal
Port: 55001
Username: postgres
Password: postgrespw
*/