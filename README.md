# RunMyProcess Json Library

## Why another Json library ?

The RunMyProcess Json Library has been written in order to optimize the serialization/deserialization of the json structures used by the RunMyProcess process engine. The performances of the other available libraries were becoming a bottleneck for our use case, so that we had to write our own, performance oriented, library.

Other libraries performances are mitigated by the fact they offer a lot of functionnalities, like data binding, data mapping, that the RunMyProcess engine does not need. So when you just need to manipulate raw json structures, our library may be a good solution. 

## Getting Started

First, clone the repository, build the jar file using a maven command, for example :

`git clone git@github.com:runmyprocess/json.git`  
`mvn clean package`

Then you will find a `json-x.y.z.jar` file, that you should add to the classpath of your own project.

## Examples

There are two ways to construct a Json object : either by deserializing it, or by creating an empty object and then populating it.

To deserialize an json object, use the `fromObject()`/`fromString()` methods :

```java
String serializedJson = "{'a':1,'b':2,'c':'foo'}";
JSONObject json = JSONObject.fromString(q);
```

You can also deserialize a Json from a stream :

```java
InpuStream inputStream = ...
JSONObject json = JSONObject.parse( new StreamParser( inputStream, "UTF-8") );
```

To construct a json object from scratch :

```java
JSONObject json = new JSONObject();
json.put("a", 1);
json.put("b", 2);
json.put("c", "foo");
```

Since a JSONObject is based on a Java Map, it can be used as a Map once created. But be careful, if you try to use an unknown type in a JSONObject, an exception will be thrown. Here are the supported types for a JSONObject value:

`String.class, Number.class, Boolean.class, JSON.class, Map.class, Collection.class, int[].class, long[].class`

To serialize an object, use the `toString()` methods :

```java
String serializedJson = "{'a':1,'b':2,'c':'foo'}";
JSONObject json = JSONObject.fromString(q);
System.out.println(json.toString());
```

Do not hesitate to have a look at the test classes to learn about other usages.

## Non official extensions

Since the RunMyProcess engine needs to manipulate sets, we have added a non official Json type, JSONSet. Basically a JSONArray implements List, and a JSONSet implements Set. A JSONSet serialization will look like this :

`<2,3,4,5>`

## Advanced usages

If you need to parse some unofficial Json syntax, you may easily override the default parser to add your own logic to it. Then you can use your customized parser in a `parse()` call :

```java
String serializedJson = ...;
JSONObject json = JSONObject.parse( new MyOwnJsonParser( serializedJson ) );
```

## See also

For those not familiar with the json, format, you should have a look at the [official json site](http://json.org).
     
