-- Copy me to /docker-entrypoint-initdb.d/

use raptor;

CREATE TABLE cimathtest (
  cimathtest_id INT NOT NULL AUTO_INCREMENT,
  test_number DOUBLE NOT NULL DEFAULT 0,
  test_case INT NOT NULL DEFAULT 0,
  PRIMARY KEY (cimathtest_id))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS ciusers (
  ciusers_id INT NOT NULL,
  first_name VARCHAR(16) NOT NULL,
  last_name VARCHAR(16) NOT NULL,
  email VARCHAR(128) NULL,
  PRIMARY KEY (ciusers_id))
ENGINE = InnoDB;

insert into ciusers (first_name,last_name,email) values
('Thomas','Powell','thomas.powell@nomail.com'),
('Robert','Young','robert.young@nomail.com'),
('Alison','Avery','alison.avery@nomail.com'),
('Michelle','Stewart','michelle.stewart@nomail.com'),
('Melanie','Johnston','melanie.johnston@nomail.com'),
('Emily','McGrath','emily.mcgrath@nomail.com'),
('Justin','Burgess','justin.burgess@nomail.com'),
('Nicola','Parr','nicola.parr@nomail.com'),
('Joe','Chapman','joe.chapman@nomail.com'),
('Yvonne','McLean','yvonne.mclean@nomail.com'),
('Chloe','Cameron','chloe.cameron@nomail.com'),
('Colin','Hardacre','colin.hardacre@nomail.com'),
('Gavin','Clarkson','gavin.clarkson@nomail.com'),
('Neil','Churchill','neil.churchill@nomail.com'),
('Boris','Reid','boris.reid@nomail.com'),
('John','Hodges','john.hodges@nomail.com'),
('Heather','Wilson','heather.wilson@nomail.com'),
('John','Sharp','john.sharp@nomail.com'),
('Max','Graham','max.graham@nomail.com'),
('Melanie','Ince','melanie.ince@nomail.com'),
('Lucas','Simpson','lucas.simpson@nomail.com'),
('Phil','Thomson','phil.thomson@nomail.com'),
('Paul','Butler','paul.butler@nomail.com'),
('Amy','Grant','amy.grant@nomail.com'),
('Simon','Hardacre','simon.hardacre@nomail.com');

insert into cimathtest (test_number,test_case) values
(32, 0),
(72, 0),
(27, 0),
(57, 0),
(90, 0),
(34, 0),
(77, 0),
(87, 0),
(96, 0),
(38, 0),
(18, 0),
(43, 0),
(32, 0),
(56, 0),
(35, 0),
(56, 0),
(55, 0),
(90, 0),
(94, 0),
(6, 0),
(93, 0),
(22, 0),
(1, 0),
(46, 0),
(52, 0),
(79.203, 1),
(64.096, 1),
(20.492, 1),
(9.157, 1),
(19.514, 1),
(22.489, 1),
(72.669, 1),
(65.278, 1),
(4.325, 1),
(4.441, 1),
(17.518, 1),
(43.712, 1),
(91.900, 1),
(32.204, 1),
(79.957, 1),
(96.811, 1),
(63.506, 1),
(91.055, 1),
(91.251, 1),
(4.613, 1),
(52.947, 1),
(25.739, 1),
(49.994, 1),
(56.540, 1),
(67.780, 1);
