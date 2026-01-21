-- Migration: Alter accounts id column to support UUID
ALTER TABLE accounts 
ALTER COLUMN id TYPE VARCHAR(36);
