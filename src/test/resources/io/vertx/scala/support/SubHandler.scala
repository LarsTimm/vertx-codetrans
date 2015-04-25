package io.vertx.scala.support

import io.vertx.codetrans.MethodExpressionTest
import io.vertx.core.Handler

class SubHandler extends Handler[String] {

  def handle(String event): Unit = {
    MethodExpressionTest.event = event
  }

  def instanceHandler(handler: String => Unit): Unit = {
    handler("hello_instance")
  }
}

object SubHandler {

  def classHandler(handler: String => Unit): Unit = {
    handler("hello_class")
  }

  def create(): SubHandler = {
    new SubHandler()
  }
}
