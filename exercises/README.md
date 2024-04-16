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

#### Example: [SealedExample](https://github.com/macknever/demoRepo/blob/master/exercises/src/main/java/com/lawrence/corejava/inheritaqnce/sealed)


