-- ============================================================
-- Schema
-- ============================================================

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL UNIQUE,
    color VARCHAR(7),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL UNIQUE,
    color VARCHAR(7),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    category_id BIGINT,
    created_by BIGINT NOT NULL,
    assigned_to BIGINT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_ticket_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    CONSTRAINT fk_ticket_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_ticket_assigned_to FOREIGN KEY (assigned_to) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE ticket_tags (
    ticket_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (ticket_id, tag_id),
    CONSTRAINT fk_tt_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
    CONSTRAINT fk_tt_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    ticket_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    body TEXT NOT NULL,
    internal_note BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_comment_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE RESTRICT
);

CREATE INDEX idx_tickets_deleted_status   ON tickets (is_deleted, status);
CREATE INDEX idx_tickets_deleted_priority ON tickets (is_deleted, priority);

-- ============================================================
-- Demo data — password for every user below is: password123
-- (real BCrypt hash, login actually works)
-- ============================================================

INSERT INTO users (uuid, username, email, password_hash, full_name, role, active, is_deleted, created_at, updated_at) VALUES
    ('u1111111-1111-1111-1111-111111111111', 'admin.demo',   'admin@corp.internal',   '$2b$10$pbLbD0yr7gAh.ZVPvhnA7ufBALEzrkxv5d8L0bu0hr9xnCA2Fopwa', 'Sarah Chen',      'ADMIN',   TRUE, FALSE, '2026-05-01 09:00:00', '2026-05-01 09:00:00'),
    ('u2222222-2222-2222-2222-222222222222', 'james.support','james@corp.internal',   '$2b$10$pbLbD0yr7gAh.ZVPvhnA7ufBALEzrkxv5d8L0bu0hr9xnCA2Fopwa', 'James Park',      'SUPPORT', TRUE, FALSE, '2026-05-02 09:00:00', '2026-05-02 09:00:00'),
    ('u3333333-3333-3333-3333-333333333333', 'maria.support','maria@corp.internal',   '$2b$10$pbLbD0yr7gAh.ZVPvhnA7ufBALEzrkxv5d8L0bu0hr9xnCA2Fopwa', 'Maria Nikou',     'SUPPORT', TRUE, FALSE, '2026-05-03 09:00:00', '2026-05-03 09:00:00'),
    ('u4444444-4444-4444-4444-444444444444', 'user.demo',    'demo.user@corp.internal','$2b$10$pbLbD0yr7gAh.ZVPvhnA7ufBALEzrkxv5d8L0bu0hr9xnCA2Fopwa', 'Alex Doukas',     'USER',    TRUE, FALSE, '2026-05-04 09:00:00', '2026-05-04 09:00:00'),
    ('u5555555-5555-5555-5555-555555555555', 'nina.user',    'nina@corp.internal',    '$2b$10$pbLbD0yr7gAh.ZVPvhnA7ufBALEzrkxv5d8L0bu0hr9xnCA2Fopwa', 'Nina Karra',      'USER',    TRUE, FALSE, '2026-05-05 09:00:00', '2026-05-05 09:00:00');

INSERT INTO categories (uuid, name, color, active, is_deleted, created_at, updated_at) VALUES
    ('c1111111-1111-1111-1111-111111111111', 'Hardware',             '#f87171', TRUE, FALSE, NOW(), NOW()),
    ('c2222222-2222-2222-2222-222222222222', 'Software',             '#60a5fa', TRUE, FALSE, NOW(), NOW()),
    ('c3333333-3333-3333-3333-333333333333', 'Network',              '#34d399', TRUE, FALSE, NOW(), NOW()),
    ('c4444444-4444-4444-4444-444444444444', 'Access / Permissions', '#a78bfa', TRUE, FALSE, NOW(), NOW());

INSERT INTO tags (uuid, name, color, is_deleted, created_at, updated_at) VALUES
    ('t1111111-1111-1111-1111-111111111111', 'vpn',            '#60a5fa', FALSE, NOW(), NOW()),
    ('t2222222-2222-2222-2222-222222222222', 'windows-update', '#fbbf24', FALSE, NOW(), NOW()),
    ('t3333333-3333-3333-3333-333333333333', 'urgent',         '#f87171', FALSE, NOW(), NOW()),
    ('t4444444-4444-4444-4444-444444444444', 'finance',        '#34d399', FALSE, NOW(), NOW()),
    ('t5555555-5555-5555-5555-555555555555', 'printer',        '#a78bfa', FALSE, NOW(), NOW());

INSERT INTO tickets (uuid, title, description, status, priority, category_id, created_by, assigned_to, is_deleted, created_at, updated_at) VALUES
    ('a0000001-0000-0000-0000-000000000001', 'VPN client fails after Windows update', 'After the latest Windows update, the VPN client crashes on launch with error code 0x800B0109. Affects the whole finance team.', 'IN_PROGRESS', 'CRITICAL', 3, 4, 2, FALSE, '2026-07-28 08:15:00', '2026-08-01 10:30:00'),
    ('a0000002-0000-0000-0000-000000000002', 'Email signature not rendering in Outlook', 'Company logo shows as a broken image in the signature on desktop Outlook, works fine in webmail.', 'OPEN', 'MEDIUM', 2, 5, 3, FALSE, '2026-07-29 11:00:00', '2026-07-30 09:00:00'),
    ('a0000003-0000-0000-0000-000000000003', 'Printer MFD-3F-01 showing offline', 'The 3rd floor printer has been offline since this morning. Tried power cycle, no change.', 'RESOLVED', 'HIGH', 1, 4, 2, FALSE, '2026-07-27 07:45:00', '2026-07-27 15:20:00'),
    ('a0000004-0000-0000-0000-000000000004', 'Need access to shared Finance drive', 'New hire on the finance team needs read/write access to the shared finance network drive.', 'OPEN', 'LOW', 4, 5, NULL, FALSE, '2026-07-30 13:00:00', '2026-07-30 13:00:00'),
    ('a0000005-0000-0000-0000-000000000005', 'Laptop battery not charging', 'Dell Latitude, battery stuck at 0%, charger light is on but percentage does not move.', 'IN_PROGRESS', 'HIGH', 1, 4, 3, FALSE, '2026-07-31 09:10:00', '2026-08-02 08:00:00'),
    ('a0000006-0000-0000-0000-000000000006', 'Slack notifications not arriving on desktop', 'Push notifications stopped working after macOS update, mobile app is fine.', 'CLOSED', 'LOW', 2, 5, 2, FALSE, '2026-07-20 10:00:00', '2026-07-22 16:00:00'),
    ('a0000007-0000-0000-0000-000000000007', 'Cannot connect to office WiFi', 'Guest WiFi drops connection every few minutes since yesterday.', 'OPEN', 'MEDIUM', 3, 4, NULL, FALSE, '2026-08-01 09:30:00', '2026-08-01 09:30:00'),
    ('a0000008-0000-0000-0000-000000000008', 'Request: second monitor for new desk', 'Moved to hot-desk area, need a second monitor set up like my old desk.', 'RESOLVED', 'LOW', 1, 5, 3, FALSE, '2026-07-25 14:00:00', '2026-07-26 11:00:00'),
    ('a0000009-0000-0000-0000-000000000009', 'Database backup job failed last night', 'Nightly backup for the reporting DB failed with a timeout error around 02:14.', 'IN_PROGRESS', 'CRITICAL', 3, 2, 2, FALSE, '2026-08-02 06:00:00', '2026-08-03 09:00:00'),
    ('a0000010-0000-0000-0000-000000000010', 'Reset password for locked account', 'Account locked after too many failed login attempts.', 'CLOSED', 'MEDIUM', 4, 5, 3, FALSE, '2026-07-15 08:00:00', '2026-07-15 08:40:00');

INSERT INTO ticket_tags (ticket_id, tag_id) VALUES
    (1, 1), (1, 2), (1, 3),
    (3, 5),
    (4, 4),
    (9, 3);

INSERT INTO comments (uuid, ticket_id, author_id, body, internal_note, is_deleted, created_at, updated_at) VALUES
    ('m0000001-0000-0000-0000-000000000001', 1, 2, 'Reproduced locally. Looks like the update replaced a network driver the VPN client depends on. Testing a rollback now.', FALSE, FALSE, '2026-07-28 09:00:00', '2026-07-28 09:00:00'),
    ('m0000002-0000-0000-0000-000000000002', 1, 2, 'Confirmed with vendor: known issue, patch expected this week. Rolling back the driver on affected machines in the meantime.', TRUE, FALSE, '2026-08-01 10:30:00', '2026-08-01 10:30:00'),
    ('m0000003-0000-0000-0000-000000000003', 3, 2, 'Power cycled the printer and cleared the print queue, back online now.', FALSE, FALSE, '2026-07-27 15:20:00', '2026-07-27 15:20:00'),
    ('m0000004-0000-0000-0000-000000000004', 9, 2, 'Checked disk space on the backup target — it was 98% full, that is the timeout cause.', TRUE, FALSE, '2026-08-02 07:30:00', '2026-08-02 07:30:00'),
    ('m0000005-0000-0000-0000-000000000005', 9, 1, 'Please prioritize — reporting DB feeds the exec dashboard, need this fixed before Monday.', FALSE, FALSE, '2026-08-03 09:00:00', '2026-08-03 09:00:00');
