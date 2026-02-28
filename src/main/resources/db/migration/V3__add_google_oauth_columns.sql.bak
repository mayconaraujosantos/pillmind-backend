-- Migration: Add OAuth fields to accounts
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS google_id VARCHAR(255) UNIQUE;
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS picture_url TEXT;
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS last_login_at TIMESTAMP;
