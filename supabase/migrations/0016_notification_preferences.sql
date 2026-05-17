-- Notification preferences
ALTER TABLE vito_users ADD COLUMN IF NOT EXISTS push_enabled BOOLEAN DEFAULT true;
ALTER TABLE vito_users ADD COLUMN IF NOT EXISTS sms_enabled BOOLEAN DEFAULT true;
ALTER TABLE vito_users ADD COLUMN IF NOT EXISTS email_enabled BOOLEAN DEFAULT true;
ALTER TABLE vito_users ADD COLUMN IF NOT EXISTS notification_sound VARCHAR(50) DEFAULT 'default';
