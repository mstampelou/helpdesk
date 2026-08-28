-- ============================================================
-- V5: Add more demo tickets
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

INSERT INTO ticket_tags (ticket_id, tag_id)
SELECT t.id, g.id FROM tickets t, tags g WHERE t.title = 'VPN keeps disconnecting on MacBook' AND g.name = 'network-wifi'
UNION ALL
SELECT t.id, g.id FROM tickets t, tags g WHERE t.title = 'New employee onboarding - laptop setup' AND g.name = 'onboarding'
UNION ALL
SELECT t.id, g.id FROM tickets t, tags g WHERE t.title = 'Forgot password, locked out' AND g.name = 'password-reset'
UNION ALL
SELECT t.id, g.id FROM tickets t, tags g WHERE t.title = 'Windows 11 update broke printer drivers' AND g.name = 'os-update'
UNION ALL
SELECT t.id, g.id FROM tickets t, tags g WHERE t.title = 'Windows 11 update broke printer drivers' AND g.name = 'hardware-repair'
UNION ALL
SELECT t.id, g.id FROM tickets t, tags g WHERE t.title = 'Request access to shared HR drive' AND g.name = 'access-request'
UNION ALL
SELECT t.id, g.id FROM tickets t, tags g WHERE t.title = 'Same VPN issue happened again' AND g.name = 'recurring'
UNION ALL
SELECT t.id, g.id FROM tickets t, tags g WHERE t.title = 'Same VPN issue happened again' AND g.name = 'network-wifi';