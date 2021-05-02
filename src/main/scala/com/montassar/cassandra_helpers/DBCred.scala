package com.montassar.cassandra_helpers

class DBCred {
  // Database connection related env variables
  var DB_HOST: String = sys.env.getOrElse("DB_HOST", "")
  var DB_PORT: String = sys.env.getOrElse("DB_PORT", "")
  var DB_USER: String = sys.env.getOrElse("DB_USER", "")
  var DB_PASS: String = sys.env.getOrElse("DB_PASS", "")
  val KEYSPACE: String = sys.env.getOrElse("KEYSPACE", "")
}
