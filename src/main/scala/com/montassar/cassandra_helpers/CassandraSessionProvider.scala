package com.montassar.cassandra_helpers

import com.datastax.driver.core._

/**
 * Provides a Cassandra session to be used in the system
 */
object CassandraSessionProvider extends DBCred {
  private var _session: Session = _

  def setSession(s: Session): Unit = { _session = s }

  def cassandraConn: Session = {
    if (_session == null) {
      val cluster_builder =
        new Cluster.Builder().withClusterName("Promo Decision Support")
      var cluster: Cluster = null

      cluster_builder
        .addContactPoint(DB_HOST)
        .withPort(DB_PORT.toInt)
        .withCredentials(DB_USER, DB_PASS)

      cluster = cluster_builder.build()


      val queryLogger: QueryLogger = QueryLogger.builder().build()

      cluster.register(queryLogger)

      _session = cluster.connect(KEYSPACE)
    }

    _session
  }
}
