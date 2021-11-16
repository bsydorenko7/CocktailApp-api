CREATE TABLE ROLE (
    ID BIGINT PRIMARY KEY,
    ROLENAME TEXT
);

INSERT INTO
    ROLE (ID, ROLENAME)
VALUES
    (1, 'Unconfirmed');

INSERT INTO
    ROLE (ID, ROLENAME)
VALUES
    (2, 'Confirmed');
	
INSERT INTO
    ROLE (ID, ROLENAME)
VALUES
    (3, 'Admin');
	
INSERT INTO
    ROLE (ID, ROLENAME)
VALUES
    (4, 'Moderator');

CREATE TABLE USERS (
    USERID SERIAL PRIMARY KEY,
    EMAIL TEXT UNIQUE,
    NICKNAME TEXT,
    PASSWORD TEXT,
    ROLEID BIGINT REFERENCES ROLE(ID),
    ACTIVATION TEXT,
    ISACTIVE BOOLEAN
);

CREATE TABLE FRIENDSSTATUS (
    ID BIGINT PRIMARY KEY,
    STATUSNAME TEXT
);

CREATE TABLE FRIENDLIST (
    ID BIGINT PRIMARY KEY,
    OWNERID BIGINT REFERENCES USERS(USERID),
    FRIENDID BIGINT REFERENCES USERS(USERID),
    STATUSID BIGINT REFERENCES FRIENDSSTATUS(ID)
);

CREATE TABLE INGREDIENTS (
    ID BIGINT PRIMARY KEY,
    INGRNAME TEXT
);

CREATE TABLE USERTOSTOCK (
    ID BIGINT PRIMARY KEY,
    USERID BIGINT REFERENCES USERS(USERID),
    INGREDIENTID BIGINT REFERENCES INGREDIENTS(ID),
    QUANTITY BIGINT
);

CREATE TABLE RECIPES (
    ID BIGINT PRIMARY KEY,
    RECIPE TEXT,
    RATING BIGINT
);

CREATE TABLE RECIPESTOINGR (
    ID BIGINT PRIMARY KEY,
    RECIPEID BIGINT REFERENCES RECIPES(ID),
    INGREDIENTID BIGINT REFERENCES INGREDIENTS(ID)
);

CREATE TABLE USERTORECIPES (
    ID BIGINT PRIMARY KEY,
    USERID BIGINT REFERENCES USERS(USERID),
    RECIPEID BIGINT REFERENCES RECIPES(ID)
);

CREATE TABLE EVENTS (
    ID BIGINT PRIMARY KEY,
    CREATEID BIGINT REFERENCES USERS(USERID),
    EVENTTIME TIMESTAMP,
    EVENTNAME TEXT
);

CREATE TABLE EVENTSTORECIPES (
    ID BIGINT PRIMARY KEY,
    EVENTID BIGINT REFERENCES EVENTS(ID),
    RECIPEID BIGINT REFERENCES RECIPES(ID)
);

CREATE TABLE EVENTSTOUSERS (
    ID BIGINT PRIMARY KEY,
    EVENTID BIGINT REFERENCES EVENTS(ID),
    USERID BIGINT REFERENCES USERS(USERID)
);

CREATE TABLE KITCHENWARE (
    ID BIGINT PRIMARY KEY,
    KITCHNAME TEXT
);

CREATE TABLE KITCHENWARETORECIPES (
    ID BIGINT PRIMARY KEY,
    KITCHENWAREID BIGINT REFERENCES KITCHENWARE(ID),
    RECIPEID BIGINT REFERENCES RECIPES(ID)
);