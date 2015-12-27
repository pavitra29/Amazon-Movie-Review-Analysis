create database mr_project;

use mr_project;

create table finalMR(
id int primary key auto_increment,
title text,
genre text,
amazon_avg_score double,
awards text,
imagelink text ,
imdbrating double,
lang varchar(100),
yr varchar(8),
oscars int,
wins int,
nomin int
);

select count(*) from finalMR;

select * from finalMR;

set sql_mode="";

select distinct(title),genre, amazon_avg_score, awards, imagelink, imdbrating, lang, yr, oscars,wins,nomin
from finalMR;
