# Base64

在Java 8中，Base64编码已经成为Java类库的标准。

Java 8 内置了 Base64 编码的编码器和解码器。

Base64工具类提供了一套静态方法获取下面三种BASE64编解码器：

基本：输出被映射到一组字符A-Za-z0-9+/，编码不添加任何行标，输出的解码仅支持A-Za-z0-9+/。

URL：输出映射到一组字符A-Za-z0-9+_，输出是URL和文件。

MIME：输出隐射到MIME友好格式。输出每行不超过76字符，并且使用'\r'并跟随'\n'作为分割。编码输出最后没有行分割。

## 内嵌类

|序号 |   内嵌类 & 描述|
|------|-----------|
|1|   static class Base64.Decoder 该类实现一个解码器用于，使用 Base64 编码来解码字节数据。|
|2|   static class Base64.Encoder 该类实现一个编码器，使用 Base64 编码来编码字节数据。|

## 方法
|序号|    方法名 & 描述|
|-----|------------|
|1|   static Base64.Decoder getDecoder() 返回一个 Base64.Decoder ，解码使用基本型 base64 编码方案。|
|2|   static Base64.Encoder getEncoder() 返回一个 Base64.Encoder ，编码使用基本型 base64 编码方案。|
|3|   static Base64.Decoder getMimeDecoder() 返回一个 Base64.Decoder ，解码使用 MIME 型 base64 编码方案。|
|4|   static Base64.Encoder getMimeEncoder() 返回一个 Base64.Encoder ，编码使用 MIME 型 base64 编码方案。|
|5|   static Base64.Encoder getMimeEncoder(int lineLength, byte[] lineSeparator) 返回一个 Base64.Encoder ，编码使用 MIME 型 base64 编码方案，可以通过参数指定每行的长度及行的分隔符。|
|6|   static Base64.Decoder getUrlDecoder() 返回一个 Base64.Decoder ，解码使用 URL 和文件名安全型 base64 编码方案。|
|7|   static Base64.Encoder getUrlEncoder() 返回一个 Base64.Encoder ，编码使用 URL 和文件名安全型 base64 编码方案。|

注意：Base64 类的很多方法从 java.lang.Object 类继承。

## Base64 实例

以下实例演示了 Base64 的使用:

```java
import java.util.Base64;
import java.util.UUID;
import java.io.UnsupportedEncodingException;
 
public class Java8Tester {
   public static void main(String args[]){
      try {
        
         // 使用基本编码
         String base64encodedString = Base64.getEncoder().encodeToString("runoob?java8".getBytes("utf-8"));
         System.out.println("Base64 编码字符串 (基本) :" + base64encodedString);
        
         // 解码
         byte[] base64decodedBytes = Base64.getDecoder().decode(base64encodedString);
        
         System.out.println("原始字符串: " + new String(base64decodedBytes, "utf-8"));
         base64encodedString = Base64.getUrlEncoder().encodeToString("runoob?java8".getBytes("utf-8"));
         System.out.println("Base64 编码字符串 (URL) :" + base64encodedString);
        
         StringBuilder stringBuilder = new StringBuilder();
        
         for (int i = 0; i < 10; ++i) {
            stringBuilder.append(UUID.randomUUID().toString());
         }
        
         byte[] mimeBytes = stringBuilder.toString().getBytes("utf-8");
         String mimeEncodedString = Base64.getMimeEncoder().encodeToString(mimeBytes);
         System.out.println("Base64 编码字符串 (MIME) :" + mimeEncodedString);
         
      }catch(UnsupportedEncodingException e){
         System.out.println("Error :" + e.getMessage());
      }
   }
}
```

执行以上脚本，输出结果为：

```shell
$ javac Java8Tester.java 
$ java Java8Tester
原始字符串: runoob?java8
Base64 编码字符串 (URL) :VHV0b3JpYWxzUG9pbnQ_amF2YTg=
Base64 编码字符串 (MIME) :M2Q4YmUxMTEtYWRkZi00NzBlLTgyZDgtN2MwNjgzOGY2NGFlOTQ3NDYyMWEtZDM4ZS00YWVhLTkz
OTYtY2ZjMzZiMzFhNmZmOGJmOGI2OTYtMzkxZi00OTJiLWEyMTQtMjgwN2RjOGI0MTBmZWUwMGNk
NTktY2ZiZS00MTMxLTgzODctNDRjMjFkYmZmNGM4Njg1NDc3OGItNzNlMC00ZWM4LTgxNzAtNjY3
NTgyMGY3YzVhZWQyMmNiZGItOTIwZi00NGUzLTlkMjAtOTkzZTI1MjUwMDU5ZjdkYjg2M2UtZTJm
YS00Y2Y2LWIwNDYtNWQ2MGRiOWQyZjFiMzJhMzYxOWQtNDE0ZS00MmRiLTk3NDgtNmM4NTczYjMx
ZDIzNGRhOWU4NDAtNTBiMi00ZmE2LWE0M2ItZjU3MWFiNTI2NmQ2NTlmMTFmZjctYjg1NC00NmE1
LWEzMWItYjk3MmEwZTYyNTdk
```
