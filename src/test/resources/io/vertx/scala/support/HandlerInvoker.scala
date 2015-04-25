package io.vertx.scala.support

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import scala.util.Try

object HandlerInvoker {

  def invokeStringHandler(handler: String => Unit): Unit = {
    handler("callback_value");
  }

  def invokeStringHandlerFirstParam(handler: String => Unit, other: String): Unit = {
    handler(other);
  }

  def invokeStringHandlerLastParam(other: String, handler: String => Unit): Unit = {
    handler(other);
  }

  def invokeAsyncResultHandlerSuccess(callback: Try[String] => Unit): Unit = {
    callback(Try(Future.succeededFuture("hello").result));
  }

  def invokeAsyncResultHandlerFailure(callback: Try[String] => Unit): Unit = {
    callback(Try(Future.failedFuture("oh no").result));
  }
}
