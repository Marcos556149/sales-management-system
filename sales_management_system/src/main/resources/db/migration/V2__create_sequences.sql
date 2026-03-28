-- V2__create_sequences.sql
-- Description: Create sequences for primary key generation in core entities

-- Creates sequences used to generate unique identifiers for core tables


CREATE SEQUENCE core.user_seq
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE core.sale_seq
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE core.sale_detail_seq
    START WITH 1
    INCREMENT BY 1;