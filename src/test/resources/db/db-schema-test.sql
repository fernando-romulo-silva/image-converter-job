
drop table if exists IMAGE_BATCH;

--------------

create table IMAGE_BATCH (IMG_ID bigint generated by default as identity, IMG_NAME varchar(255), IMG_BATCH_FILE CLOB, IMG_CONVERSION varchar(255), IMG_CREATED date, primary key (IMG_ID));

-- alter table IMAGE_TYPE add constraint UK_IMT_EXTENSION unique (IMT_EXTENSION);

-- alter table IMAGE_CONVERTION add constraint FK_IMT_IMGC foreign key (IMT_ID) references IMAGE_TYPE;