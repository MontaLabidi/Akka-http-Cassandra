create keyspace if not exists simple_keyspace WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
use simple_keyspace;
CREATE TABLE IF NOT EXISTS Person (
  id UUID,
  name TEXT,
  age INT,
  birth_date TIMESTAMP,
  job_title TEXT,
  PRIMARY KEY (id)
);
INSERT INTO person (id, name, age, birth_date, job_title)
 VALUES (256fce82-2358-4152-a3ad-990b20bb7c37, 'Monta', 25, '1996-02-12', 'Software Engineer');