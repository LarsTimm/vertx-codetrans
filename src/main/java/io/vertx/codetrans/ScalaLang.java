package io.vertx.codetrans;

import com.sun.source.tree.LambdaExpressionTree;

import scala.tools.nsc.interpreter.IMain;
import scala.tools.nsc.settings.MutableSettings.BooleanSetting;
import scala.tools.nsc.Settings;
import io.vertx.codegen.TypeInfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;


/**
 * @author <a href="mailto:larsdtimm@gmail.com">Lars Timm</a>
 */
public class ScalaLang implements Lang {

  @Override
  public io.vertx.codetrans.Script loadScript(ClassLoader loader, String path) throws Exception {
    IMain engine = new IMain(new Settings());
    ((BooleanSetting)(engine.settings().usejavacp())).value_$eq(true);
    
    InputStream in = loader.getResourceAsStream(path + ".scala");
    if (in == null) {
      throw new Exception("Could not find " + path + ".scala");
    }
    String source = new Scanner(in,"UTF-8").useDelimiter("\\A").next();
    return new io.vertx.codetrans.Script() {
      @Override
      public String getSource() {
        return source;
      }
      @Override
      public void run(Map<String, Object> globals) throws Exception {
        // TODO: handle globals.
        
        List<String> imports = Arrays.asList(
          "io/vertx/scala/core/buffer/Buffer.scala",
          "io/vertx/lang/scala/json/Json.scala",
          "io/vertx/lang/scala/json/package.scala",
          "io/vertx/scala/support/CollectionFactory.scala",
          "io/vertx/scala/support/HandlerInvoker.scala",
          "io/vertx/scala/support/JsonConverter.scala",
          "io/vertx/scala/support/SubHandler.scala"
        );
        for (String imp : imports) {
          InputStream stream = loader.getResourceAsStream(imp);
          String source = new Scanner(stream,"UTF-8").useDelimiter("\\A").next();
          engine.compileString(source);
        }
        
        engine.eval(source);
      }
    };
  }

  static class ScalaRenderer extends CodeWriter {
    LinkedHashSet<TypeInfo.Class> imports = new LinkedHashSet<>();
    LinkedHashSet<String> plainImports = new LinkedHashSet<>();
    ScalaRenderer(Lang lang) {
      super(lang);
    }
  }

  @Override
  public void renderStatement(StatementModel statement, CodeWriter writer) {
    statement.render(writer);
    writer.append("\n");
  }

  @Override
  public void renderMethodInvocation(ExpressionModel expression, String methodName, java.util.List<TypeInfo> parameterTypes,
                                     java.util.List<ExpressionModel> argumentModels, java.util.List<TypeInfo> argumentTypes, CodeWriter writer) {
    expression.render(writer);
    writer.append('.');
    writer.append(methodName);
    
    Boolean inParenthesis = false;
    for (int i = 0; i < argumentModels.size(); i++) {
      Boolean handler = false;

      TypeInfo parameterType = parameterTypes.get(i);
      TypeInfo argumentType = argumentTypes.get(i);
      if (io.vertx.codetrans.Helper.isHandler(parameterType)) {
        handler = true;
      }

      if (i > 0 &&
          inParenthesis &&
          !handler) {
        writer.append(", ");
      }

      // Handlers are put in their own seperate curly brackets.
      if (handler) {
        if (inParenthesis) {
          writer.append(')');
          inParenthesis = false;
        }
        writer.append(" {");
      } else if (!inParenthesis) {
        writer.append('(');
        inParenthesis = true;
      }
      
      argumentModels.get(i).render(writer);
      
      if (handler) {
        writer.append('}');
      }       
    }
    if (inParenthesis) {
      writer.append(')');
    }
  }

  @Override
  public void renderBlock(BlockModel block, CodeWriter writer) {
    if (writer instanceof ScalaRenderer) {
      Lang.super.renderBlock(block, writer);
    } else {
      ScalaRenderer langRenderer = new ScalaRenderer(this);
      Lang.super.renderBlock(block, langRenderer);
      for (TypeInfo.Class importedType : langRenderer.imports) {
        String fqn = importedType.getName();
        if (importedType instanceof TypeInfo.Class.Api) {
          fqn = importedType.getName().replace("io.vertx.", "io.vertx.scala.");
        }
        writer.append("import ").append(fqn).append('\n');
      }
      for (String plainImport : langRenderer.plainImports) {
        writer.append("import ").append(plainImport).append('\n');
      }
      writer.append(langRenderer.getBuffer());
    }
  }

  @Override
  public String getExtension() {
    return "scala";
  }

  @Override
  public void renderLongLiteral(String value, CodeWriter writer) {
    writer.renderChars(value);
  }

  @Override
  public void renderFloatLiteral(String value, CodeWriter writer) {
    writer.renderChars(value);  
  }

  @Override
  public void renderDoubleLiteral(String value, CodeWriter writer) {
	  writer.renderChars(value);
  }

  @Override
  public void renderPrefixIncrement(ExpressionModel expression, CodeWriter writer) {
    expression.render(writer);
    writer.append("+=1");
  }

  @Override
  public void renderPostfixIncrement(ExpressionModel expression, CodeWriter writer) {
    expression.render(writer);
    writer.append("+=1");
  }

  @Override
  public void renderPostfixDecrement(ExpressionModel expression, CodeWriter writer) {
    expression.render(writer);
    writer.append("-=1");
  }

  @Override
  public void renderPrefixDecrement(ExpressionModel expression, CodeWriter writer) {
    expression.render(writer);
    writer.append("-=1");
  }

  @Override
  public ExpressionModel classExpression(TypeInfo.Class type) {
    return ExpressionModel.render(type.getName());
  }

  @Override
  public void renderLambda(LambdaExpressionTree.BodyKind bodyKind, java.util.List<TypeInfo> parameterTypes, java.util.List<String> parameterNames, CodeModel body, CodeWriter writer) {
    //writer.append("{");
    for (int i = 0; i < parameterNames.size(); i++) {
      if (i == 0) {
        writer.append(" ");
      } else {
        writer.append(", ");
      }
      writer.append(parameterNames.get(i));
    }
    
    if (parameterNames.size() > 0) {
      writer.append(" =>\n");
      writer.indent();
    }

    body.render(writer);

    if (parameterNames.size() > 0) {
      writer.unindent();
    }
    //writer.append("}");
  }

  @Override
  public void renderEnumConstant(TypeInfo.Class.Enum type, String constant, CodeWriter writer) {
    ScalaRenderer renderer = (ScalaRenderer) writer;
    renderer.imports.add(type);
    writer.append(type.getSimpleName()).append('.').append(constant);
  }

  @Override
  public void renderThrow(String throwableType, ExpressionModel reason, CodeWriter writer) {
    if (reason == null) {
      writer.append("throw new ").append(throwableType).append("()");
    } else {
      writer.append("throw new ").append(throwableType).append("(");
      reason.render(writer);
      writer.append(")");
    }
  }

  @Override
  public ExpressionModel asyncResult(String identifier) {
    // TODO: If we could somehow rewrite the typical if/else statement to a pattern match
    //       the failure handling could be made much prettier...
    return ExpressionModel.forMethodInvocation((member, args) -> {
      switch (member) {
        case "succeeded":
          return ExpressionModel.render(identifier + ".isSuccess");
        case "result":
          return ExpressionModel.render(identifier + ".get");
        case "cause":
          return ExpressionModel.render(identifier + ".asInstanceOf[scala.util.Failure].exception");
        case "failed":
          return ExpressionModel.render(identifier + ".isFailure");
        default:
          throw new UnsupportedOperationException("Not implemented");
      }
    });

  }

  @Override
  public ExpressionModel asyncResultHandler(LambdaExpressionTree.BodyKind bodyKind, TypeInfo.Parameterized resultType, String resultName, CodeModel body) {
    return new LambdaExpressionModel(bodyKind, Collections.singletonList(resultType), Collections.singletonList(resultName), body);
  }

  @Override
  public ExpressionModel staticFactory(TypeInfo.Class type, String methodName, java.util.List<TypeInfo> parameterTypes, java.util.List<ExpressionModel> arguments, java.util.List<TypeInfo> argumentTypes) {
    return ExpressionModel.render(writer -> {
      ScalaRenderer renderer = (ScalaRenderer) writer;
      renderer.imports.add(type);
      writer.append(type.getSimpleName()).append('.').append(methodName);
      writer.append('(');
      for (int i = 0;i < arguments.size();i++) {
        ExpressionModel argument = arguments.get(i);
        if (i > 0) {
          writer.append(", ");
        }
        argument.render(writer);
      }
      writer.append(')');
    });
  }

  @Override
  public StatementModel variableDecl(TypeInfo type, String name, ExpressionModel initializer) {
    return StatementModel.render(renderer -> {
      if (type instanceof TypeInfo.Class.Api &&
          initializer != null) { 
        renderer.append("val "); 
      } else {
        renderer.append("var ");
      }
      
      renderer.append(name);
      
      if (initializer == null) {
        if (type.getSimpleName().equals("String")) { 
          renderer.append(" = \"\"");
        } else {
          renderer.append("TODO: *" + type.getSimpleName() + "*");
        }
      }
      
      if (initializer != null) {
        renderer.append(" = ");
        initializer.render(renderer);
      }
    });
  }

  @Override
  public StatementModel enhancedForLoop(String variableName, ExpressionModel expression, StatementModel body) {
    return StatementModel.render(renderer -> {
      expression.render(renderer);
      renderer.append(".foreach( ").append(variableName).append(" =>\n");
      renderer.indent();
      body.render(renderer);
      renderer.unindent();
      renderer.append(")");
    });
  }

  @Override
  public StatementModel forLoop(StatementModel initializer, ExpressionModel condition, ExpressionModel update, StatementModel body) {
    // TODO: Can we rewrite this as a for comprehension instead?
    return StatementModel.render(writer -> {
      initializer.render(writer);
      writer.append('\n');
      writer.append("while(");
      condition.render(writer);
      writer.append(") {\n");
      writer.indent();
      body.render(writer);
      update.render(writer);
      writer.append('\n');
      writer.unindent();
      writer.append("}");
    });
  }

  public void renderDataObject(DataObjectLiteralModel model, CodeWriter writer) {
    renderJsonObject(model.getMembers(), writer, false);
  }

  public void renderJsonObject(JsonObjectLiteralModel jsonObject, CodeWriter writer) {
    renderJsonObject(jsonObject.getMembers(), writer, true);
  }

  public void renderJsonArray(JsonArrayLiteralModel jsonArray, CodeWriter writer) {
    renderJsonArray(jsonArray.getValues(), writer);
  }

  private void renderJsonObject(java.lang.Iterable<Member> members, CodeWriter writer, boolean unquote) {
    Iterator<Member> iterator = members.iterator();
    if (iterator.hasNext()) {
      ScalaRenderer renderer = (ScalaRenderer) writer;
      renderer.plainImports.add("io.vertx.lang.scala.json._");
            
      writer.append("Json.obj(\n").indent();
      while (iterator.hasNext()) {
        Member member = iterator.next();
        String name = member.name.render(writer.getLang());
        if (unquote) {
          name = Helper.unwrapQuotedString(name);
        }
        writer.append("\"").append(name).append("\" -> ");
        if (member instanceof Member.Single) {
          ((Member.Single) member).value.render(writer);
        } else {
          renderJsonArray(((Member.Array) member).values, writer);
        }
        if (iterator.hasNext()) {
          writer.append(',');
        }
        writer.append('\n');
      }
      writer.unindent().append(")");
    } else {
      writer.append("new io.vertx.core.json.JsonObject()");
    }
  }

  private void renderJsonArray(java.util.List<ExpressionModel> values, CodeWriter writer) {
    ScalaRenderer renderer = (ScalaRenderer) writer;
    renderer.plainImports.add("io.vertx.lang.scala.json._");

    writer.append("Json.arr(\n").indent();
    for (int i = 0;i < values.size();i++) {
      values.get(i).render(writer);
      if (i < values.size() - 1) {
        writer.append(',');
      }
      writer.append('\n');
    }
    writer.unindent().append(')');
  }

  @Override
  public void renderJsonObjectAssign(ExpressionModel expression, ExpressionModel name, ExpressionModel value, CodeWriter writer) {
    expression.render(writer);
    writer.append(".put(");
    name.render(writer);
    writer.append(", ");
    value.render(writer);
    writer.append(")");
  }

  @Override
  public void renderDataObjectAssign(ExpressionModel expression, ExpressionModel name, ExpressionModel value, CodeWriter writer) {
    renderJsonObjectAssign(expression, name, value, writer);
  }

  @Override
  public void renderJsonObjectMemberSelect(ExpressionModel expression, ExpressionModel name, CodeWriter writer, String methodName) {
    expression.render(writer);
    writer.append('.').append(methodName).append('(');
    name.render(writer);
    writer.append(')');
  }

  @Override
  public void renderJsonObjectToString(ExpressionModel expression, CodeWriter writer) {
    expression.render(writer);
    writer.append(".toString()");
  }

  @Override
  public void renderDataObjectMemberSelect(ExpressionModel expression, ExpressionModel name, CodeWriter writer, String methodName) {
    renderJsonObjectMemberSelect(expression, name, writer, methodName);
  }

  @Override
  public ExpressionModel console(ExpressionModel expression) {
    return ExpressionModel.render(renderer -> {
      renderer.append("println(");
      expression.render(renderer);
      renderer.append(")");
    });
  }

  @Override
  public void renderMapGet(ExpressionModel map, ExpressionModel arg, CodeWriter writer) {
    map.render(writer);
    writer.append(".get(");
    arg.render(writer);
    writer.append(").get");
  }

  @Override
  public void renderMapForEach(ExpressionModel map, String keyName, TypeInfo keyType, String valueName, TypeInfo valueType, LambdaExpressionTree.BodyKind bodyKind, CodeModel block, CodeWriter writer) {
    map.render(writer);
    writer.append(".foreach(");
    writer.indent();
    renderLambda(bodyKind, Arrays.asList(keyType, valueType), Arrays.asList(keyName, valueName), block, writer);
    writer.unindent();
    writer.append(')');
  }

  @Override
  public void renderMethodReference(ExpressionModel expression, String methodName, CodeWriter writer) {
    expression.render(writer);
    writer.append(".").append(methodName);
  }
}
