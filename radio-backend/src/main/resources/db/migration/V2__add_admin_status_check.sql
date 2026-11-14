ALTER TABLE admin_users
ADD CONSTRAINT chk_admin_users_status
CHECK (status IN ('ACTIVE', 'INACTIVE'));

