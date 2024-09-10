# Core Java Book exercises

This module aims to practice with and showcase Java features in [Core Java](https://www.oreilly.com/library/view/core-java-volume/9780137673810/)

Also, I will write notes here for each chapter. I will only focus on those features I was not familiar with or are new
in Java 17.

## Chapter 4 Objects and Classes
### Record
- From Java 16
- Immutable data class(all field is final)
- Not completely immutable if one of the fields is a reference

#### Example: [RecordExample](https://github.com/macknever/demoRepo/blob/master/exercises/src/main/java/com/lawrence/corejava/objectsandclasses/RecordExample.java)

## Chapter 5 Inheritance
### Pattern Matching for instanceof

Since Java 16 there is an update in how to use `instanceof`

- Old way
```java
if (staff[i] instanceof Manager)
{
Manager boss = (Manager) staff[i];
boss.setBonus(5000);
}
```

- New way
```java
if (staff[i] instanceof Manager boss)
{
boss.setBonus(5000);
}
```

Since Java 17/21 switch can be used with type patterns
This is a preview feature in java 17.
```java
String description = switch (e)
{
case Executive exec -> "An executive with a fancy title of " + exec.getTitle();
case Manager m -> "A manager with a bonus of " + m.getBonus();
default -> "A lowly employee with a salary of " + e.getSalary();
}
```

### sealed class

`sealed` is a new modifier of class. Like `abstract`, `final`, `sealed` has a different limit on it. A class with `sealed`
access modifier can only be inherited by designated class. So when declare a `sealed` class, one need to define which classes can inherit from it.

```java
public abstract sealed class JSONPrimitive extends JSONValue
permits JSONString, JSONNumber, JSONBoolean, JSONNull
{
. . .
}
```

For the subclass of a `sealed` class, we can use `non-sealed` to make it be extended arbitrarily. Also, a class which extends a `sealed` class must have 
`final`, `sealed`, or `non-sealed` modifier.

#### Example: [SealedExample](https://github.com/macknever/demoRepo/tree/master/exercises/src/main/java/com/lawrence/corejava/inheritance/sealed)

## Chapter 6 Interfaces

### Abstract class vs. interface
Java does not allow multiple inheritance. One class can only extends single class, but can implement multiple interfaces.

### default method.
Both abstract class and interface can have default method. If ClassA extends ClassB and implement InterfaceB. Both ClassB 
and InterfaceB has a method getName(), which one ClassA will execute? Principle:
- Super Class win
- Interfaces clash
**HAVE TO MANUALLY RESOLVE IN ANY SITUATION**

#### Example [DefaultMethod](https://github.com/macknever/demoRepo/tree/master/exercises/src/main/java/com/lawrence/corejava/interfaces/defaultMethodExample)

### Clone

I will not address why we need clone, just make some notes about how clone in Java work.
A object can make a clone of itself only if the corresponding class implements Cloneable and also override the `clone()`
method which is belong to class`Object`. Or else,  either only implement Cloneable or only override `clone()` would not work

#### Example [clone](https://github.com/macknever/demoRepo/tree/master/exercises/src/main/java/com/lawrence/corejava/interfaces/clone)

## Chapter 7 Exception logging assert

Those topics and tools are the things I use in my daily job.
For exception, here is my former notes: https://mire-cayenne-563.notion.site/Exception-d6ea0594a11e4c90b9422f424e278696

## Chapter 8 Generic
If you need a reusable class or an interface, you need generic program.

### Syntax and usage

Although this is not complex, there are something need to be careful.

#### Generic class and method
```java
class GenericClass <T> {
    // This class has already declared T as a generic type, so the method in this class can use T as a known type
    // ordinary method
    T foo(T a) {
        // do sth 
    }
    
    // Still, one can create a generic method inside a generic class
    // generic method
    <T> T genericFoo(T t) {
        // the syntax seems like correct, but it will confuse compiler and the programmer
        // <T> is not the same T declared in class.
    }
    
    // This is a real generic method
    // this syntax is correct in both generic and ordinary class
    <R> R genericFoo(R r) {
        
    }
    
}
```

#### Bound / Restriction

If you need the generic type not any arbitrary one, you can bound it.

```java
class BoundedGenericClass<T extends Comparable> {
    // maybe you need the generic type you use can be compared
}

```

#### Wild card
Wild card is question mark ?. When need to deal with a GenericClass<T>, say `foo (GenericClass<T>`, this T need to be a 
specific type. If we need some dynamic of this T we need use Wild card.

Wild card can make generic type generic. When you want to read `List<T>` like `void read(List<T>)`, you have to specify
the type of T. If you want to make it generic, you need to use it like `void read(List<?>)`. If it is arbitrary type, that
would make no sense. We can split the operations into two categories, read and write. 

```java
// read
void read(List<? extends Parent> lists) {
    for(var p : lists) {
        p.print();
    }
}

//write
void write(Child[] children, List<? super Child> lists) {
    for(var child : children) {
        lsits.add(child);
    }
}

```

## Collections

### TreeMap and TreeSet
Those are sorted Map and Set. When needs a sorted map, consider to use a TreeMap. In default, TreeMap is sorted by Key 
or a Comparator<? extends K>, here K is the type of the key of this TreeMap. TreeMap can not customize to sort by value.
Also, because this TreeMap is sorted, if you customize the comparator, it has a new way to decide if the key is already there.
For example, if you sort the key by length of the string, same length of the string will be considered to be same Key.

### Example [Customized TreeMap](https://github.com/macknever/demoRepo/tree/master/exercises/src/main/java/com/lawrence/corejava/collections/Worker)


## Concurrency

Concurrency is a mechanism in Java that multithreads can work "At the same time". In this chapter, the main point is not
to showcase how we use Java to do Multithreading, but to prevent from the issues brought from it.

### Race condition
When different threads operating the same set of data, the data may corrupt in between.

There are two solutions:
- lock
- Synchronized methods or block

The idea is simple. We lock the data from being altered --> change it --> unlock.

### Callable & Future

`Callable` is similar to `Runable`. Class implements `Runable` does not bother to return result. `Callable` does. Since 
the class will run asynchronously, the result is wrapped with a `Future`. It can get when the result is ready

### CompletableFuture

CompletableFuture, unless Future which is blocking, can register a callback to handle or do further operation to the result.
`Future` needs to call `get()` to get the result, if you want to do further operation onto it, you need to wait till 
future can get the result.
`CompletableFuture` use fluent manner to compose all the operations that you want to do, and it will do each step when 
the previous is complete.
Also, it is called completableFuture, means you can give it a result, if you like. E.g. You plan to give it 5 seconds to
get some results, if it hasn't been done within 5 seconds, you can call `completableFuture.complete(<result you want>)` 
to set the result. Of course, you can also set it to te an Exception.

#### Chain computation

In terms of non-blocking, `CompletableFuture` can chain computations. If we already have an instance, say completableFuture,
we can call `completableFuture.thenApply(Function)` to do further computation and get a result.

```java
ComletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "hello");
completableFuture.thenApply(s -> s + " world");
completableFuture.thenAccept(s -> System.out.println(s));
```

#### Combine result

We can use `thenCombine()` to combine two CompletableFutures. Say we have two completableFutures, cf1, cf2. It can be call 
like this `cf1.thenCombine(cf2())` Need to notice that, the result of cf1 can be call as an argument of cf2.


## Input and output 

### Input and output streams

For me there are two goals from learning this chapter:
- Now how input and output are working in Java
- How to use them in my daily job

InputStream is to read from a source. A source can be a file, a String, an object etc. The most important concept here 
is, those source are encoded to raw bytes by unicode or any other standards, before being read. And InputStream just 
read the bytes.

## JDBC

A JDBC driver is a JDBC API implementation used for connecting to a particular type of database.

## JPMS Java platform Module System

JPMS is a mechanism to control the accessibility of classes. Like modifiers `public`,  `private`. To create a module, we
need to create a module-info.java on root level. 
```java
module example.module{
    // exports or requires
}
```
This is a boundary of what this module needs and what this module wants to expose to external. If there is nothing, that
 means no class can access any class from this module, no matter if it is `public` or 'private'

### exports and requires

If you want to expose some certain classes, you can specify that in module-info.java. For the classes needed, use requires

```java
module my.module {
    exports com.mycompany.mypackage;
    requires com.another.anotherpackage;
}
```

## Security

### Class loader

Dynamic load classes when they are needed by JVM. There are three types of classloaders:
- Application --> To load your own class
- Extension --> To load extension of java core classes
- Bootstrap --> To load all other classes like ArrayList StringBuilder. The classloaders of them are written by native code


### Signature

A want to send B a message.

- Create fingerprint(message digest) SHA-256
- Sender sign fingerprint using its private key(only know by sender)
- Sender send message along with signature

- B receives message and signature
- B generates fingerprint for message
- B decrypts signature with public key --> should get a fingerprint

generated vs. decrypted


