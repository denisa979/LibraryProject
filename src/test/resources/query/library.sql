select count(*) from books;

select count(*) from users;

select * from book_borrow
where is_returned = 0;

select * from users
where id=1;

select full_name,email,user_group_id,status,start_date,end_date,address
from users
where id=18635;
