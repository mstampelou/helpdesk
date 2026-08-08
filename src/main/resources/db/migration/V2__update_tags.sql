-- ============================================================
-- V2: Replace initial demo tags with a more curated,
-- IT-helpdesk-appropriate set.
-- ============================================================

-- Remove existing ticket <-> tag associations first (FK constraint),
-- then the old tags themselves.
DELETE FROM ticket_tags;
DELETE FROM tags;

INSERT INTO tags (uuid, name, color, is_deleted, created_at, updated_at) VALUES
    ('t6666666-6666-6666-6666-666666666666', 'hardware-repair', '#f87171', FALSE, NOW(), NOW()),
    ('t7777777-7777-7777-7777-777777777777', 'network-wifi',    '#60a5fa', FALSE, NOW(), NOW()),
    ('t8888888-8888-8888-8888-888888888888', 'password-reset',  '#fbbf24', FALSE, NOW(), NOW()),
    ('t9999999-9999-9999-9999-999999999999', 'access-request',  '#a78bfa', FALSE, NOW(), NOW()),
    ('taaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'os-update',        '#34d399', FALSE, NOW(), NOW()),
    ('tbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'onboarding',       '#f472b6', FALSE, NOW(), NOW()),
    ('tccccccc-cccc-cccc-cccc-cccccccccccc', 'recurrent',        '#94a3b8', FALSE, NOW(), NOW());

-- Re-tag a few existing demo tickets with the new set, for a realistic-looking demo.
-- ticket_id 1 = "VPN client fails after Windows update"
-- ticket_id 3 = "Printer MFD-3F-01 showing offline"
-- ticket_id 4 = "Need access to shared Finance drive"
INSERT INTO ticket_tags (ticket_id, tag_id)
SELECT 1, id FROM tags WHERE name = 'network-wifi'
UNION ALL
SELECT 1, id FROM tags WHERE name = 'os-update'
UNION ALL
SELECT 3, id FROM tags WHERE name = 'hardware-repair'
UNION ALL
SELECT 4, id FROM tags WHERE name = 'access-request';