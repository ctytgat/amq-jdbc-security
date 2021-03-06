CREATE TABLE users (
username VARCHAR(100) NOT NULL,
password VARCHAR(100) NOT NULL,
enabled CHAR(1) DEFAULT '1' NOT NULL
);
ALTER TABLE users ADD CONSTRAINT pk_users primary key (username);
CREATE TABLE authorities (
username VARCHAR(100) NOT NULL,
authority VARCHAR(100) NOT NULL
);
ALTER TABLE authorities ADD CONSTRAINT pk_authorities primary key (username, authority);
ALTER TABLE authorities ADD CONSTRAINT fk_authorities_users foreign key (username) REFERENCES users(username);