
CREATE TABLE public.songs (
	id int8 NOT NULL,
	album varchar(255) NOT NULL,
	artist varchar(255) NOT NULL,
	duration_ms int4 NOT NULL,
	"name" varchar(255) NOT NULL,
	"year" int4 NOT NULL,
	CONSTRAINT songs_pkey PRIMARY KEY (id)
);