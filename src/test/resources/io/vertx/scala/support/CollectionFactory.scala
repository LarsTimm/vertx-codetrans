package io.vertx.scala.support

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
object CollectionFactory {
  def createMap(): Map  = {
    val map = new HashMap<>()
    map.put("foo", "foo_value")
    map;
  }
}
