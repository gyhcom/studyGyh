#### 의존성 주입(Dependency Injection)의 종류
프로젝트를 진행하다 보니 소스 젤 상단에
의존성을 주입하는 부분에 있어서

```java
private final EmailService emailService;
```
저 부분에서 final을 빼니 동작하지 않았다.. 왜인지 기본개념적인 부분이 부족한거 같아
다시 한번 의존성 주입에 대해 정리를 진행한다.

---

의존성 주입 종류는 다음과 같다.

1. 생성자 주입방식(Constructor Injection)
   - 생성자를 통해 의존성을 주입하는 방식이다.
   - 총 3가지 주입 방식중 가장 권장되는 주입 방식이다.
   ```java
    //final을 사용 할수 잇다.
    private final ExampleClass exampleClass;
        
    @Autowired
    public ExampleClass(Example example) {
        this.example = example;
        }
    }
    ```
2. 필드 주입 방식(Field Injection)
    - @Autowired 어노테이션을 통해 의존성을 주입하는 방식이다.
    - 사용법이 매우 간단하다.
    ```java
    @Autowired
    public ExampleClass example;
    ```
3. 수정자 주입 방식(Setter Injection)
   - 수정자(setXX) 를 통해 의존성을 주입하는 방식이다.

   ```java
    public class ExampleClass {

        private Example example;
        //함수 이름이 setXX일 필요는 없지만 일관성을 위해 setXX를 사용하는 것이 좋다.

        @Autowired
        public void setExample(Example example) {
            this.example = example;
        }
    } 
    
   ```
   
### 생성자 주입방식 권장된다.
의존성을 주입하는 방법은 3가지인데, 왜 생성자 주입 방식이 권장되냐?

1.순환참조 방지   
예제는 검색해서 찾아보고 이유만 적겠다.   

필드 주입방식과 생성자 주입 방식의 결과가 다르다.
- 이는 의존성을 주입하는 시점이(생성자) - (필드, 수정자) 방식이 다르기 때문이다.
- 필드, 수정자 주입 방식의 경우, 빈 객체를 먼저 생성한 뒤에 의존성을 주입하기 때문에 빈 객체를 생성하는   
  시점에는 순환참조가 발생하는지 알 수 있는 방법이 없다.(생성 -> 주입)
- 생성자 주입 방식의 경우, 빈 객체를 생성하는 시점에 생성자의 파라미터 빈 객체를 찾아 먼저 주입한 뒤에 주입받은 빈 객체를 이용하여   
  생성한다.(주입 -> 생성)
- 이 때문에 런타임 시점이 아닌 애플리케이션 구동 시점에 순환 참조를 발견할 수 있다.
```
정리하면.. 필드주입 방식은 빈 객체를 생성하고 의존성을 주입하기 때문에 빈 객체를 생성하는 시점(런타임중에)   
에러가 발생해 문제가 되는데, 생성자 주입 방식의 경우, 빈 객체를 생성하는 시점에서 생성자의 파라미터 빈 객체를 찾아
먼저 주입한 뒤에 주입받은 빈 객체를 이용하여 생성하기 때문에 런타임 전 구동시점에 에러가 발생하기 때문에 디버깅 시점이 훨씬
이르다.

또한 final 키워드 사용으로 불변성을 통한 오류 방지 및 Lombok 활용해 객체 생성 시점에 빈 객체 초기화를
수행해야 하기 때문에 null이 아님을 보장할수 있고 초기화된 객체는 변경될 수 없다.
```

필드주입
수정자주입(메서드주입)
생성자 주입
@Resource도 있다.. 1.9에서 사라짐

