CREATE TABLE `customer` (
                            `id` mediumint(8) unsigned NOT NULL auto_increment,
                            `firstName` varchar(255) default NULL,
                            `lastName` varchar(255) default NULL,
                            `birthdate` varchar(255),
                            PRIMARY KEY (`id`)
) AUTO_INCREMENT=1;


CREATE TABLE `member` (
                            `id` mediumint(8) unsigned NOT NULL auto_increment,
                            `username` varchar(255) default NULL,
                            `age` int default NULL,
                            PRIMARY KEY (`id`)
) AUTO_INCREMENT=1;


CREATE TABLE `address` (
                            `id` mediumint(8) unsigned NOT NULL auto_increment,
                            `location` varchar(255) default NULL,
                            `member_id` mediumint(8) default NULL,
                            PRIMARY KEY (`id`)
) AUTO_INCREMENT=1;

