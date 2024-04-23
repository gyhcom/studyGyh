### 메소드 참조(MethodReference) 

자바의 람다표현식을 통해 코드 정의를 혁신적으로 줄여주었지만 이보다 더 간략하게 줄이는 법
말 그대로 실행하려는 메소드를 참조해서 매개 변수의 정보 및 리턴 타입을 알아내어 람다식에서
굳이 선언이 불필요한 부분을 생략하는 것을 말한다.

메소드 참조는 이중 콜론(::)을 사용하여 클래스 이름과 메서드 이름을 구분하며, 람다식과 달리 인수를 전달할 필요가 없다

예를 들어

```java
@FunctionalInterface
public interface IAdd{
    int add(int x, int y);
}
```
```java
public class MathUtils{
    public static int AddElement(int x, int y) {
        return x +y;
    }
}
```

```java
public class Main{
    public static void main(String args[]) {
        //람다식
        System.out.println("lambda Expression");
        IAdd addLambda = (x, y) -> MathUtils.AddElement(x, y);
        System.out.println(addLambda.add(10, 20));

        //메소드 참조
        System.out.println("method Reference");
        Iadd addMethodRef = MathUtils::AddElement;
        System.out.println(addMethodRef.add(20, 40));
    }
}
```