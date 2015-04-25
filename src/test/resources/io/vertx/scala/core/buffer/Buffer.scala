package io.vertx.scala.core.buffer;

class Buffer(private val _asJava: io.vertx.core.buffer.Buffer) {

  def asJava: java.lang.Object = _asJava

  def appendString(str: String): io.vertx.scala.core.buffer.Buffer = {
    _asJava.appendString(str)
    this
  }

  def toString(enc: String): String = {
    _asJava.toString(enc)
  }
}

object Buffer {

  def apply(_asJava: io.vertx.core.buffer.Buffer): io.vertx.scala.core.buffer.Buffer =
    new io.vertx.scala.core.buffer.Buffer(_asJava)

  def buffer(string: String): io.vertx.scala.core.buffer.Buffer = {
    Buffer.apply(io.vertx.core.buffer.Buffer.buffer(string))
  }
}