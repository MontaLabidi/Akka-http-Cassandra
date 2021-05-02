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


