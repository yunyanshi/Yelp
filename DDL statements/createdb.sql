CREATE TABLE Yelp_User(
    user_id CHAR(22) PRIMARY KEY,
    name VARCHAR(50),
    yelping_since DATE,
    review_count INT,
    friend_count INT,
    avg_stars NUMBER,
    vote_count INT
);


CREATE TABLE Business(
    bid CHAR(22) PRIMARY KEY,
    if_open NUMBER(1,0),
    city VARCHAR(30),
    b_state CHAR(3),
    review_count INT,
    bname VARCHAR(100),
    stars NUMBER  
);

CREATE TABLE Category(
    bid CHAR(22) NOT NULL,
    catg VARCHAR(40),
    PRIMARY KEY (bid, catg),
    FOREIGN KEY (bid) REFERENCES Business (bid) ON DELETE CASCADE
);


CREATE TABLE Subcategory(
    bid CHAR(22) NOT NULL,
    subcatg VARCHAR(40),
    PRIMARY KEY (bid, subcatg),
    FOREIGN KEY (bid) REFERENCES Business (bid) ON DELETE CASCADE
);


CREATE TABLE Attribute(
    bid CHAR(22) NOT NULL, 
    attribute VARCHAR(50),
    PRIMARY KEY (bid, attribute),
    FOREIGN KEY (bid) REFERENCES Business (bid) ON DELETE CASCADE
);

CREATE TABLE Review(
    rid CHAR(22) PRIMARY KEY,
    user_id CHAR(22) NOT NULL, 
    bid CHAR(22) NOT NULL,
    useful_vote_count INT,
    stars NUMBER,
    rdate DATE,
    content NVARCHAR2(500),
    FOREIGN KEY (user_id) REFERENCES Yelp_User (user_id) ON DELETE CASCADE,
    FOREIGN KEY (bid) REFERENCES Business(bid) ON DELETE CASCADE
);


CREATE INDEX category_catg_i on Category (catg);
CREATE INDEX subcategory_subcatg_i on Subcategory (subcatg);
CREATE INDEX attribute_attribute_i on Attribute (attribute);


CREATE INDEX user_stars_i on Yelp_User (avg_stars);
CREATE INDEX user_review_i on Yelp_User (review_count);
CREATE INDEX user_vote_i on Yelp_User (vote_count);
CREATE INDEX user_friend_i on Yelp_User (friend_count);
CREATE INDEX user_date_i on Yelp_User (yelping_since);



CREATE INDEX review_yid_i on Review (user_id);
CREATE INDEX review_bid_i on Review (bid);
CREATE INDEX review_date_i on Review (rdate);
CREATE INDEX review_vote_i on Review (useful_vote_count);
CREATE INDEX review_stars_i on Review (stars);



















