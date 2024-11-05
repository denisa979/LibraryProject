select count(*) from books;

select count(*) from users;

select * from book_borrow
where is_returned = 0;

select name from book_categories;

# name,author ,isbn,desc,year

select name,isbn,year,author,description from books
where name = 'Agile Testing';

select full_name from users
where email = 'librarian55@library';

select status from users where email='anisa.stokes@gmail.com';