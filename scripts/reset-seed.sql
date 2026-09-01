-- ============================================================
-- HelpDesk demo reset script
-- Restores the database to the exact state produced by the
-- Flyway seed migrations (V1 + V2 + V3 + V5 combined).
-- Safe to run repeatedly — fully idempotent.
-- NOTE: the "attachments" table is deliberately left untouched, so any
-- manually-uploaded demo files survive a reset (they only disappear on
-- a fresh Render deploy, since the disk itself is ephemeral).
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE comments;
TRUNCATE TABLE ticket_tags;
TRUNCATE TABLE tickets;
TRUNCATE TABLE tags;
TRUNCATE TABLE categories;
TRUNCATE TABLE users;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- Users (password for every user below is: password123)
-- ============================================================
INSERT INTO users (uuid, username, email, password_hash, full_name, role, active, is_deleted, created_at, updated_at) VALUES
    ('u1111111-1111-1111-1111-111111111111', 'admin.demo',   'admin@corp.internal',    '$2b$10$pbLbD0yr7gAh.ZVPvhnA7ufBALEzrkxv5d8L0bu0hr9xnCA2Fopwa', 'Sarah Chen',   'ADMIN',   TRUE, FALSE, '2026-05-01 09:00:00', '2026-05-01 09:00:00'),
    ('u2222222-2222-2222-2222-222222222222', 'james.support','james@corp.internal',    '$2b$10$pbLbD0yr7gAh.ZVPvhnA7ufBALEzrkxv5d8L0bu0hr9xnCA2Fopwa', 'James Park',   'SUPPORT', TRUE, FALSE, '2026-05-02 09:00:00', '2026-05-02 09:00:00'),
    ('u3333333-3333-3333-3333-333333333333', 'maria.support','maria@corp.internal',    '$2b$10$pbLbD0yr7gAh.ZVPvhnA7ufBALEzrkxv5d8L0bu0hr9xnCA2Fopwa', 'Maria Nikou',  'SUPPORT', TRUE, FALSE, '2026-05-03 09:00:00', '2026-05-03 09:00:00'),
    ('u4444444-4444-4444-4444-444444444444', 'user.demo',    'demo.user@corp.internal','$2b$10$pbLbD0yr7gAh.ZVPvhnA7ufBALEzrkxv5d8L0bu0hr9xnCA2Fopwa', 'Alex Doukas',  'USER',    TRUE, FALSE, '2026-05-04 09:00:00', '2026-05-04 09:00:00'),
    ('u5555555-5555-5555-5555-555555555555', 'nina.user',    'nina@corp.internal',     '$2b$10$pbLbD0yr7gAh.ZVPvhnA7ufBALEzrkxv5d8L0bu0hr9xnCA2Fopwa', 'Nina Karra',   'USER',    TRUE, FALSE, '2026-05-05 09:00:00', '2026-05-05 09:00:00');

-- ============================================================
-- Categories
-- ============================================================
INSERT INTO categories (uuid, name, color, active, is_deleted, created_at, updated_at) VALUES
    ('c1111111-1111-1111-1111-111111111111', 'Hardware',             '#f87171', TRUE, FALSE, NOW(), NOW()),
    ('c2222222-2222-2222-2222-222222222222', 'Software',             '#60a5fa', TRUE, FALSE, NOW(), NOW()),
    ('c3333333-3333-3333-3333-333333333333', 'Network',              '#34d399', TRUE, FALSE, NOW(), NOW()),
    ('c4444444-4444-4444-4444-444444444444', 'Access / Permissions', '#a78bfa', TRUE, FALSE, NOW(), NOW());

-- ============================================================
-- Tags (final set, post V2 + V3 rename)
-- ============================================================
INSERT INTO tags (uuid, name, color, is_deleted, created_at, updated_at) VALUES
    ('t6666666-6666-6666-6666-666666666666', 'hardware-repair', '#f87171', FALSE, NOW(), NOW()),
    ('t7777777-7777-7777-7777-777777777777', 'network-wifi',    '#60a5fa', FALSE, NOW(), NOW()),
    ('t8888888-8888-8888-8888-888888888888', 'password-reset',  '#fbbf24', FALSE, NOW(), NOW()),
    ('t9999999-9999-9999-9999-999999999999', 'access-request',  '#a78bfa', FALSE, NOW(), NOW()),
    ('taaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'os-update',        '#34d399', FALSE, NOW(), NOW()),
    ('tbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'onboarding',       '#f472b6', FALSE, NOW(), NOW()),
    ('tccccccc-cccc-cccc-cccc-cccccccccccc', 'recurring',        '#94a3b8', FALSE, NOW(), NOW());

-- ============================================================
-- Tickets (V1 originals, ids 1-10)
-- ============================================================
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

-- ============================================================
-- Tickets (V5 additions, ids 11-18)
-- ============================================================
INSERT INTO tickets (uuid, title, description, status, priority, category_id, created_by, assigned_to, is_deleted, created_at, updated_at) VALUES
    ('a0000011-0000-0000-0000-000000000011', 'VPN keeps disconnecting on MacBook', 'VPN drops every 10-15 minutes since switching to the new client version. Happens both on office WiFi and home network.', 'OPEN', 'HIGH', 3, 5, 2, FALSE, '2026-08-11 09:20:00', '2026-08-12 10:00:00'),
    ('a0000012-0000-0000-0000-000000000012', 'Need software license for Adobe Photoshop', 'Marketing team member needs a Photoshop license for the new campaign assets. Manager approval attached separately.', 'OPEN', 'MEDIUM', 2, 4, NULL, FALSE, '2026-08-13 11:05:00', '2026-08-13 11:05:00'),
    ('a0000013-0000-0000-0000-000000000013', 'New employee onboarding - laptop setup', 'New hire starting Monday needs a laptop imaged, accounts created, and standard software installed.', 'IN_PROGRESS', 'MEDIUM', 1, 1, 3, FALSE, '2026-08-14 08:30:00', '2026-08-15 14:20:00'),
    ('a0000014-0000-0000-0000-000000000014', 'Forgot password, locked out', 'Account locked after several failed attempts trying to remember a new password after the policy change.', 'RESOLVED', 'HIGH', 4, 5, 2, FALSE, '2026-08-16 07:50:00', '2026-08-16 08:15:00'),
    ('a0000015-0000-0000-0000-000000000015', 'Windows 11 update broke printer drivers', 'After the mandatory update rolled out, none of the floor printers are recognized anymore. Affects the whole 2nd floor.', 'OPEN', 'CRITICAL', 1, 4, 3, FALSE, '2026-08-18 09:00:00', '2026-08-19 09:40:00'),
    ('a0000016-0000-0000-0000-000000000016', 'Request access to shared HR drive', 'Need read access to the shared HR drive for the quarterly headcount report.', 'CLOSED', 'LOW', 4, 5, 2, FALSE, '2026-08-05 13:10:00', '2026-08-06 09:00:00'),
    ('a0000017-0000-0000-0000-000000000017', 'Same VPN issue happened again', 'Reporting the same VPN disconnect issue as last week — did not fully resolve after the driver rollback.', 'IN_PROGRESS', 'MEDIUM', 3, 4, NULL, FALSE, '2026-08-20 10:15:00', '2026-08-21 11:00:00'),
    ('a0000018-0000-0000-0000-000000000018', 'Monitor flickering intermittently', 'Second monitor flickers on and off a few times a day, seems to happen more when the laptop is charging.', 'OPEN', 'LOW', 1, 5, NULL, FALSE, '2026-08-22 15:40:00', '2026-08-22 15:40:00');

-- ============================================================
-- Ticket <-> tag associations (final combined state)
-- ============================================================
INSERT INTO ticket_tags (ticket_id, tag_id)
SELECT 1, id FROM tags WHERE name = 'network-wifi'
UNION ALL SELECT 1, id FROM tags WHERE name = 'os-update'
UNION ALL SELECT 3, id FROM tags WHERE name = 'hardware-repair'
UNION ALL SELECT 4, id FROM tags WHERE name = 'access-request'
UNION ALL SELECT t.id, g.id FROM tickets t, tags g WHERE t.title = 'VPN keeps disconnecting on MacBook' AND g.name = 'network-wifi'
UNION ALL SELECT t.id, g.id FROM tickets t, tags g WHERE t.title = 'New employee onboarding - laptop setup' AND g.name = 'onboarding'
UNION ALL SELECT t.id, g.id FROM tickets t, tags g WHERE t.title = 'Forgot password, locked out' AND g.name = 'password-reset'
UNION ALL SELECT t.id, g.id FROM tickets t, tags g WHERE t.title = 'Windows 11 update broke printer drivers' AND g.name = 'os-update'
UNION ALL SELECT t.id, g.id FROM tickets t, tags g WHERE t.title = 'Windows 11 update broke printer drivers' AND g.name = 'hardware-repair'
UNION ALL SELECT t.id, g.id FROM tickets t, tags g WHERE t.title = 'Request access to shared HR drive' AND g.name = 'access-request'
UNION ALL SELECT t.id, g.id FROM tickets t, tags g WHERE t.title = 'Same VPN issue happened again' AND g.name = 'recurring'
UNION ALL SELECT t.id, g.id FROM tickets t, tags g WHERE t.title = 'Same VPN issue happened again' AND g.name = 'network-wifi';

-- ============================================================
-- Comments
-- ============================================================
INSERT INTO comments (uuid, ticket_id, author_id, body, internal_note, is_deleted, created_at, updated_at) VALUES
    ('m0000001-0000-0000-0000-000000000001', 1, 2, 'Reproduced locally. Looks like the update replaced a network driver the VPN client depends on. Testing a rollback now.', FALSE, FALSE, '2026-07-28 09:00:00', '2026-07-28 09:00:00'),
    ('m0000002-0000-0000-0000-000000000002', 1, 2, 'Confirmed with vendor: known issue, patch expected this week. Rolling back the driver on affected machines in the meantime.', TRUE, FALSE, '2026-08-01 10:30:00', '2026-08-01 10:30:00'),
    ('m0000003-0000-0000-0000-000000000003', 3, 2, 'Power cycled the printer and cleared the print queue, back online now.', FALSE, FALSE, '2026-07-27 15:20:00', '2026-07-27 15:20:00'),
    ('m0000004-0000-0000-0000-000000000004', 9, 2, 'Checked disk space on the backup target — it was 98% full, that is the timeout cause.', TRUE, FALSE, '2026-08-02 07:30:00', '2026-08-02 07:30:00'),
    ('m0000005-0000-0000-0000-000000000005', 9, 1, 'Please prioritize — reporting DB feeds the exec dashboard, need this fixed before Monday.', FALSE, FALSE, '2026-08-03 09:00:00', '2026-08-03 09:00:00');
