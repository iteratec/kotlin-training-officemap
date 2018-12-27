ALTER TABLE workplace
    ADD COLUMN equipment text NOT NULL DEFAULT '';

ALTER TABLE workplace
    ALTER COLUMN equipment DROP DEFAULT;
