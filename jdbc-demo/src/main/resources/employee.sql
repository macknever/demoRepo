CREATE TABLE if not exists employee (
                          id identity PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          occupation VARCHAR(100)
);

CREATE TABLE  if not exists manager (
                         id identity PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         occupation VARCHAR(100)
);

INSERT INTO employee ( name, occupation)
VALUES ( 'Lawrence', 'employee');

INSERT INTO manager ( name, occupation)
VALUES ( 'Levon', 'manager');




