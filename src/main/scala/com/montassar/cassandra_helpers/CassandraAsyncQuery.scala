package com.montassar.cassandra_helpers

import com.datastax.driver.core._
import com.github.blemale.scaffeine.{AsyncLoadingCache, Scaffeine}
import com.google.common.util.concurrent.{FutureCallback, Futures, ListenableFuture}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

object CassandraAsyncQuery {

  val cassandraSession: Session = CassandraSessionProvider.cassandraConn
  val PreparedStatementCache: AsyncLoadingCache[String, PreparedStatement] =
    Scaffeine()
      .buildAsyncFuture((key: String) => {
        val prepared_statement: Future[PreparedStatement] =
          cassandraSession.prepareAsync(key)
        prepared_statement
      })

  /**
   * Value Class to allow creation of CQL statements in the following manner:
   * cql"SELECT * FROM table WHERE key = ?"
   * @param context
   */
  implicit class CqlStrings(val context: StringContext) extends AnyVal {

    /**
     * Create a Cassandra PreparedStatement to run asynchronously
     * @param args
     * @param session
     * @return Future[PreparedStatement]
     */
    def cql(args: Any*)(
      implicit session: Session = cassandraSession
    ): Future[PreparedStatement] = {
      val key = context.raw(args: _*)
      PreparedStatementCache.get(key)
    }
  }

  /**
   * Convert Guava's ListenableFuture to Scala Future
   * This function is implicit meaning that any ListenableFuture will be automatically
   * converted into a Scala Future
   * @param listenableFuture
   * @tparam T
   * @return Future[T]
   */
  implicit def listenableFutureToFuture[T](listenableFuture: ListenableFuture[T]): Future[T] = {
    val promise = Promise[T]()
    Futures.addCallback(listenableFuture, new FutureCallback[T] {
      def onFailure(t: Throwable): Unit = promise failure t
      def onSuccess(result: T): Unit    = promise success result
    })
    promise.future
  }

  /**
   * Execute the Future PreparedStatement asynchronously
   * @param statement
   * @param params
   * @param executionContext
   * @param session
   * @return Future[ResultSet]
   */
  def execute(statement: Future[PreparedStatement], params: Any*)(
    implicit executionContext: ExecutionContext,
    session: Session = cassandraSession
  ): Future[ResultSet] = {
    Try {
      val parameters = params.map(_.asInstanceOf[Object])
      val bound: Future[BoundStatement] = statement.map(_.bind(parameters: _*))
      bound.flatMap(session.executeAsync(_))
    } match {
      case Success(f) => f
      case Failure(e) => Future.failed(e)
    }
  }
}
