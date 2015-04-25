package io.vertx.scala.support

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

object JsonConverter {

  def toJsonObject(obj: JsonObject): JsonObject =  {
    return new JsonObject(obj);
  }

  def toJsonArray(obj: JsonArray): JsonArray = {
    return new JsonArray(obj);
  }
}
