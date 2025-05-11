# Nashorn JavaScript

Nashorn 一个 javascript 引擎。

Nashorn JavaScript Engine 在 Java 15 已经不可用了。

这已经在 Java 11 标记为：

```java
@deprecated (forRemoval = true)
```

从 JDK 1.8 开始，Nashorn取代Rhino(JDK 1.6, JDK1.7) 成为 Java 的嵌入式 JavaScript 引擎。Nashorn 完全支持 ECMAScript 5.1 规范以及一些扩展。它使用基于 JSR 292 的新语言特性，其中包含在 JDK 7 中引入的 invokedynamic，将 JavaScript 编译成 Java 字节码。

与先前的 Rhino 实现相比，这带来了 2 到 10倍的性能提升。

## jjs

jjs是个基于Nashorn引擎的命令行工具。它接受一些JavaScript源代码为参数，并且执行这些源代码。

例如，我们创建一个具有如下内容的sample.js文件：

```java
print('Hello World!');
```

打开控制台，输入以下命令：

```shell
$ jjs sample.js
```

以上程序输出结果为：

```shell
Hello World!
```

## jjs 交互式编程

打开控制台，输入以下命令：

```shell
$ jjs
jjs> print("Hello, World!")
Hello, World!
jjs> quit()
>>
```

## 传递参数

打开控制台，输入以下命令：

```shell
$ jjs -- a b c
jjs> print('字母: ' +arguments.join(", "))
字母: a, b, c
jjs> 
```

## Java 中调用 JavaScript

使用 ScriptEngineManager, JavaScript 代码可以在 Java 中执行，实例如下：

```java
Java8Tester.java 文件
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
 
public class Java8Tester {
   public static void main(String args[]){
   
      ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
      ScriptEngine nashorn = scriptEngineManager.getEngineByName("nashorn");
        
      String name = "Runoob";
      Integer result = null;
      
      try {
         nashorn.eval("print('" + name + "')");
         result = (Integer) nashorn.eval("10 + 2");
         
      }catch(ScriptException e){
         System.out.println("执行脚本错误: "+ e.getMessage());
      }
      
      System.out.println(result.toString());
   }
}
```

执行以上脚本，输出结果为：

```shell
$ javac Java8Tester.java 
$ java Java8Tester
Runoob
12
```

## JavaScript 中调用 Java

以下实例演示了如何在 JavaScript 中引用 Java 类：

```java script
var BigDecimal = Java.type('java.math.BigDecimal');

function calculate(amount, percentage) {

   var result = new BigDecimal(amount).multiply(
   new BigDecimal(percentage)).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN);
   
   return result.toPlainString();
}

var result = calculate(568000000000000000023,13.9);
print(result);
```

我们使用 jjs 命令执行以上脚本，输出结果如下：

```shell
$ jjs sample.js
78952000000000002017.94
```
