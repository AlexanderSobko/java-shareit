MERGE INTO users
    (id,name,email)
VALUES
    (101,'user1','user1@user.com'),
    (102,'user2','user2@user.com'),
    (103,'user3','user3@user.com'),
    (104,'user4','user4@user.com');
MERGE INTO item_requests
    (id, description, user_id, created)
VALUES
    (101,'description1',101,now()),
    (102,'description2',102,now()),
    (103,'description3',103,now()),
    (104,'description4',104,now()),
    (105,'description5',101,now());
MERGE INTO items
    (id, available, description, request_id, name, owner_id, rent_count)
VALUES
    (101,true,'description1',null,'name1',101,0),
    (102,true,'description2',null,'name2',102,0),
    (103,true,'description3',null,'name3',102,0),
    (104,true,'description4',null,'name4',104,0),
    (105,false,'description5',null,'name5',104,0);
MERGE INTO bookings
    (id, start_date, end_date, user_id, item_id, booking_state)
VALUES
    (101,(NOW() - 1 MINUTE),(NOW() + 5 MINUTE),101,103,'WAITING'),
    (102,(NOW() + 10 MINUTE),(NOW() + 20 MINUTE),104,103,'WAITING'),
    (103,(NOW() + 25 MINUTE),(NOW() + 30 MINUTE),104,103,'WAITING'),
    (104,(NOW() - 40 MINUTE),(NOW() - 30 MINUTE),101,102,'APPROVED'),
    (105,(NOW() -20 MINUTE),(NOW() - 5 MINUTE),103,102,'APPROVED'),
    (106,(NOW() + 4 MINUTE),(NOW() + 10 MINUTE),104,102,'APPROVED'),
    (107,(NOW() - 1 MINUTE),(NOW() + 5 MINUTE),102,101,'REJECTED');
--MERGE INTO comments
--    (id, text, author_name, user_id, item_id, created)
--VALUES
--    (101,true,'description1',null,'name1',101,0),
--    (102,true,'description2',null,'name2',102,0),
--    (103,true,'description3',null,'name3',102,0),
--    (104,true,'description4',null,'name4',104,0);
MERGE INTO item_requests
    (id, description, user_id, created)
VALUES
    (101,'description1',101,now()),
    (102,'description2',102,now()),
    (103,'description3',102,now()),
    (104,'description4',104,now());


