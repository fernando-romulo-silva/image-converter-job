
DELETE FROM IMAGE_BATCH;

--insert into IMAGE_TYPE (IMT_ID, IMT_EXTENSION, IMT_NAME, IMT_DESC, IMT_CREATED) values (1000, 'png', 'PNG', 'Portable Network Graphics', CURRENT_TIMESTAMP);
            
--insert into IMAGE_TYPE (IMT_ID, IMT_EXTENSION, IMT_NAME, IMT_DESC, IMT_CREATED) values (1001, 'jpg', 'JPG', 'Joint Photographics Experts Group', CURRENT_TIMESTAMP);
            
--insert into IMAGE_CONVERTION (IMGC_ID, IMGC_NAME       , IMT_ID, IMGC_SIZE, IMGC_CREATED     , IMGC_TYPE, IMGC_TEXT) 
--                      values (1000   , 'image_test.jpg', 1001  , 4000     , CURRENT_TIMESTAMP, 'BATCH'  , '02325678908110000003556752101015176230000023560');