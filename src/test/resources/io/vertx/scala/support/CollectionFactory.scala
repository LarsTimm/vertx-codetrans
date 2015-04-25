package io.vertx.scala.support

/**
 * @author <a href="mailto:larsdtimm@gmail.com">LarsTimm</a>
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
object CollectionFactory {
  def createMap: java.util.Map[String, String]  = {
    val map = new java.util.HashMap[String, String]()
    map.put("foo", "foo_value")
    map;
  }
}
