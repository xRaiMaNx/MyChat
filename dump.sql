--
-- PostgreSQL database dump
--

-- Dumped from database version 11.3
-- Dumped by pg_dump version 11.3

-- Started on 2021-05-06 21:11:15

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 196 (class 1259 OID 16395)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    users_login character varying(20) NOT NULL,
    users_password character varying(20) NOT NULL,
    users_nick character varying(20) NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 2804 (class 0 OID 16395)
-- Dependencies: 196
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.users (users_login, users_password, users_nick) VALUES ('login1', 'pass1', 'nick1');
INSERT INTO public.users (users_login, users_password, users_nick) VALUES ('RaiMaN', '123456', 'L');


-- Completed on 2021-05-06 21:11:15

--
-- PostgreSQL database dump complete
--

