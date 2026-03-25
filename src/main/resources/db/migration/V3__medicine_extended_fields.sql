-- Campos alinhados ao mockup: tipo, prescrito para, quantidade, lembrete ao acabar

ALTER TABLE medicines ADD COLUMN medicine_type VARCHAR(32) NOT NULL DEFAULT 'capsule';

ALTER TABLE medicines ADD COLUMN prescribed_for TEXT;

ALTER TABLE medicines ADD COLUMN quantity INTEGER NOT NULL DEFAULT 1;

ALTER TABLE medicines ADD COLUMN reminder_on_empty INTEGER NOT NULL DEFAULT 1;
