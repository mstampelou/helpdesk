-- ============================================================
-- V3: Fix tag wording — "recurring" is the natural English term
-- for a repeat/periodic issue, "recurrent" reads as medical jargon.
-- ============================================================

UPDATE tags SET name = 'recurring' WHERE name = 'recurrent';