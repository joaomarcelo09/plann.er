CREATE TABLE trips (
    id UUID NOT NULL,
    destination VARCHAR(255) NOT NULL,
    start_at TIMESTAMP NOT NULL,
    ends_at TIMESTAMP NOT NULL,
    is_confirmed BOOLEAN NOT NULL,
    owner_name VARCHAR(255) NOT NULL,
    owner_email VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);