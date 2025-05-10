# Lambda 表达式

Lambda 表达式，也可称为闭包，它是推动 Java 8 发布的最重要新特性。

Lambda 允许把函数作为一个方法的参数（函数作为参数传递进方法中）。

使用 Lambda 表达式可以使代码变的更加简洁紧凑。

## 语法

lambda 表达式的语法格式如下：

```shell
(parameters) -> expression
或
(parameters) ->{ statements; }
```

parameters 是参数列表，expression 或 { statements; } 是Lambda 表达式的主体。如果只有一个参数，可以省略括号；如果没有参数，也需要空括号。

下面是一个简单的例子，展示了使用 Lambda 表达式计算两个数的和：

```java
// 使用 Lambda 表达式计算两个数的和
MathOperation addition = (a, b) -> a + b;

// 调用 Lambda 表达式
int result = addition.operation(5, 3);
System.out.println("5 + 3 = " + result);
```

在上面的例子中，MathOperation 是一个函数式接口，它包含一个抽象方法 operation，Lambda 表达式 (a, b) -> a + b 实现了这个抽象方法，表示对两个参数进行相加操作。

Lambda 表达式可以用于各种场景，包括集合操作、事件处理等，下面是一个使用 Lambda 表达式对列表进行遍历的例子：

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

// 使用 Lambda 表达式遍历列表
names.forEach(name -> System.out.println(name));
```

Lambda 表达式在 Java 中引入了更函数式编程的风格，使得代码更加简洁和易读，它是 Java 8 中对函数式编程的一次重要改进。

# 重要特征

## 简洁性

Lambda 表达式提供了一种更为简洁的语法，尤其适用于函数式接口。相比于传统的匿名内部类，Lambda 表达式使得代码更为紧凑，减少了样板代码的编写。

```java
// 传统的匿名内部类
Runnable runnable1 = new Runnable() {
    @Override
    public void run() {
        System.out.println("Hello World!");
    }
};

// 使用 Lambda 表达式
Runnable runnable2 = () -> System.out.println("Hello World!");
```

## 函数式编程支持

Lambda 表达式是函数式编程的一种体现，它允许将函数当作参数传递给方法，或者将函数作为返回值，这种支持使得 Java 在函数式编程方面更为灵活，能够更好地处理集合操作、并行计算等任务。

```java
// 使用 Lambda 表达式作为参数传递给方法
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
names.forEach(name -> System.out.println(name));
```

## 变量捕获

Lambda 表达式可以访问外部作用域的变量，这种特性称为变量捕获，Lambda 表达式可以隐式地捕获 final 或事实上是 final 的局部变量。

```java
// 变量捕获
int x = 10;
MyFunction myFunction = y -> System.out.println(x + y);
myFunction.doSomething(5); // 输出 15
```

## 方法引用

Lambda 表达式可以通过方法引用进一步简化，方法引用允许你直接引用现有类或对象的方法，而不用编写冗余的代码。

```java
// 使用方法引用
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
names.forEach(System.out::println);
```

## 可并行性

Lambda 表达式能够更方便地实现并行操作，通过使用 Stream API 结合 Lambda 表达式，可以更容易地实现并行计算，提高程序性能。

```java
// 使用 Lambda 表达式和 Stream API 进行并行计算
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
int sum = numbers.parallelStream().mapToInt(Integer::intValue).sum();
```

Lambda 表达式的引入使得 Java 编程更加灵活、简洁，并推动了函数式编程的发展。

# Lambda 表达式实例

Lambda 表达式的简单例子:

```java
// 1. 不需要参数,返回值为 5  
() -> 5  
  
// 2. 接收一个参数(数字类型),返回其2倍的值  
x -> 2 * x  
  
// 3. 接受2个参数(数字),并返回他们的差值  
(x, y) -> x – y  
  
// 4. 接收2个int型整数,返回他们的和  
(int x, int y) -> x + y  
  
// 5. 接受一个 string 对象,并在控制台打印,不返回任何值(看起来像是返回void)  
(String s) -> System.out.print(s)
```

在 Java8Tester.java 文件输入以下代码：

```java
public class Java8Tester {
   public static void main(String args[]){
      Java8Tester tester = new Java8Tester();
        
      // 类型声明
      MathOperation addition = (int a, int b) -> a + b;
        
      // 不用类型声明
      MathOperation subtraction = (a, b) -> a - b;
        
      // 大括号中的返回语句
      MathOperation multiplication = (int a, int b) -> { return a * b; };
        
      // 没有大括号及返回语句
      MathOperation division = (int a, int b) -> a / b;
        
      System.out.println("10 + 5 = " + tester.operate(10, 5, addition));
      System.out.println("10 - 5 = " + tester.operate(10, 5, subtraction));
      System.out.println("10 x 5 = " + tester.operate(10, 5, multiplication));
      System.out.println("10 / 5 = " + tester.operate(10, 5, division));
        
      // 不用括号
      GreetingService greetService1 = message ->
      System.out.println("Hello " + message);
        
      // 用括号
      GreetingService greetService2 = (message) ->
      System.out.println("Hello " + message);
        
      greetService1.sayMessage("Runoob");
      greetService2.sayMessage("Google");
   }
    
   interface MathOperation {
      int operation(int a, int b);
   }
    
   interface GreetingService {
      void sayMessage(String message);
   }
    
   private int operate(int a, int b, MathOperation mathOperation){
      return mathOperation.operation(a, b);
   }
}
```

执行以上脚本，输出结果为：

```shell
$ javac Java8Tester.java 
$ java Java8Tester
10 + 5 = 15
10 - 5 = 5
10 x 5 = 50
10 / 5 = 2
Hello Runoob
Hello Google
```

使用 Lambda 表达式需要注意以下两点：

Lambda 表达式主要用来定义行内执行的方法类型接口（例如，一个简单方法接口）。在上面例子中，我们使用各种类型的 Lambda 表达式来定义 MathOperation 接口的方法，然后我们定义了 operation 的执行。

Lambda 表达式免去了使用匿名方法的麻烦，并且给予 Java 简单但是强大的函数化的编程能力。

# 变量作用域

lambda 表达式只能引用标记了 final 的外层局部变量，这就是说不能在 lambda 内部修改定义在域外的局部变量，否则会编译错误。

在 Java8Tester.java 文件输入以下代码：

```java
public class Java8Tester {
 
   final static String salutation = "Hello! ";
   
   public static void main(String args[]){
      GreetingService greetService1 = message -> 
      System.out.println(salutation + message);
      greetService1.sayMessage("Runoob");
   }
    
   interface GreetingService {
      void sayMessage(String message);
   }
}
```

执行以上脚本，输出结果为：

```shell
$ javac Java8Tester.java 
$ java Java8Tester
Hello! Runoob

```

我们也可以直接在 lambda 表达式中访问外层的局部变量：

```java
public class Java8Tester {
    public static void main(String args[]) {
        final int num = 1;
        Converter<Integer, String> s = (param) -> System.out.println(String.valueOf(param + num));
        s.convert(2);  // 输出结果为 3
    }
 
    public interface Converter<T1, T2> {
        void convert(int i);
    }
}
```

lambda 表达式的局部变量可以不用声明为 final，但是必须不可被后面的代码修改（即隐性的具有 final 的语义）

```java
int num = 1;  
Converter<Integer, String> s = (param) -> System.out.println(String.valueOf(param + num));
s.convert(2);
num = 5;  
//报错信息：Local variable num defined in an enclosing scope must be final or effectively 
 final
```

在 Lambda 表达式当中不允许声明一个与局部变量同名的参数或者局部变量。

```java
String first = "";  
Comparator<String> comparator = (first, second) -> Integer.compare(first.length(), second.length());  //编译会出错 
```






