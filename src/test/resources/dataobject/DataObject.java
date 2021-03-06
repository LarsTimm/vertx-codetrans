package dataobject;

import io.vertx.core.http.HttpServerOptions;
import io.vertx.codetrans.annotations.CodeTranslate;
import io.vertx.codetrans.DataObjectTest;
import io.vertx.core.net.JksOptions;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class DataObject {

  @CodeTranslate
  public void empty() throws Exception {
    DataObjectTest.o = new HttpServerOptions();
  }

  @CodeTranslate
  public void nested() throws Exception {
    DataObjectTest.o = new HttpServerOptions().setKeyStoreOptions(new JksOptions().setPath("/mystore.jks").setPassword("secret"));
  }

  @CodeTranslate
  public void add() throws Exception {
    DataObjectTest.o = new HttpServerOptions().addEnabledCipherSuite("foo").addEnabledCipherSuite("bar");
  }

  @CodeTranslate
  public void setFromConstructor() throws Exception {
    DataObjectTest.o = new HttpServerOptions().setPort(8080).setHost("localhost");
  }

  @CodeTranslate
  public void setFromIdentifier() throws Exception {
    HttpServerOptions obj = new HttpServerOptions();
    obj.setPort(8080);
    obj.setHost("localhost");
    DataObjectTest.o = obj;
  }

  @CodeTranslate
  public void getFromIdentifier() throws Exception {
    HttpServerOptions obj = new HttpServerOptions().setHost("localhost");
    DataObjectTest.o = obj.getHost();
  }
}
