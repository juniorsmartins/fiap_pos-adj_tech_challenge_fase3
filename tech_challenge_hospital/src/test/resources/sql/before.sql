INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_MEDICO');
INSERT INTO roles (id, name) VALUES (3, 'ROLE_PACIENTE');
INSERT INTO roles (id, name) VALUES (4, 'ROLE_ENFERMEIRO');

INSERT INTO users (username, password, enabled, role_id) VALUES ('admin', '123', true, 1);

