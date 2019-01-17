ALTER TABLE reservation
    ALTER COLUMN start_date TYPE TIMESTAMP WITH TIME ZONE
        USING to_timestamp(start_date / 1000) AT TIME ZONE 'Europe/Berlin';

ALTER TABLE reservation
    ALTER COLUMN end_date TYPE TIMESTAMP WITH TIME ZONE
        USING to_timestamp(end_date / 1000) AT TIME ZONE 'Europe/Berlin';
