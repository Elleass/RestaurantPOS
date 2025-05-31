-- Create a test user with admin role
INSERT INTO users (id_user, username, password, is_locked, id_role)
VALUES (1, 'test_user', 'password', false, 1);