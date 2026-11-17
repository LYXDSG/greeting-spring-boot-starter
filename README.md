# greeting-spring-boot-starter

一个**最小可运行的 Spring Boot 自定义 starter**，用来把「自动配置」这件事拆到最小：
四个文件 + 一行清单，演示 `.imports` 机制和条件装配的全部要点。

不是生产库，是**学习/演示用的最小样本**。

---

## 它演示了哪些机制

| 机制 | 在哪 |
|---|---|
| `META-INF/spring/...AutoConfiguration.imports` 清单 | `src/main/resources/META-INF/spring/` |
| `@AutoConfiguration` | `GreetingAutoConfiguration` |
| `@EnableConfigurationProperties` 绑定 yml | `GreetingAutoConfiguration` + `GreetingProperties` |
| `@ConditionalOnProperty` 配置开关 | `GreetingAutoConfiguration` |
| `@ConditionalOnMissingBean` 允许用户覆盖 | `GreetingService` 的 `@Bean` 方法 |
| `@ConfigurationProperties(prefix = "greeting")` | `GreetingProperties` |

---

## 用法

### 1. 安装到本地仓库

```bash
mvn install
```

### 2. 在你的项目里加依赖

```xml
<dependency>
    <groupId>org.example</groupId>
    <artifactId>greeting-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 3. 直接注入使用（不用写任何 `@Bean`）

```java
@RestController
public class DemoController {

    private final GreetingService greetingService;

    public DemoController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping("/greet/{name}")
    public String greet(@PathVariable String name) {
        return greetingService.greet(name);
    }
}
```

### 4. 可选配置

```yaml
greeting:
  enabled: true          # false 则整个功能不装配
  message: 你好          # 问候语前缀
```

---

## 三个可以自己验证的实验

| 改什么 | 预期结果 | 证明了什么 |
|---|---|---|
| yml 里 `greeting.enabled: false` | 启动报 `NoSuchBeanDefinitionException: GreetingService` | `@ConditionalOnProperty` 真的在执行 |
| 在自己项目里定义 `@Bean GreetingService` | 启动不报错，用你的实现 | `@ConditionalOnMissingBean` 会主动让位 |
| 把 `.imports` 文件里的类名删掉 | 启动报找不到 Bean | 清单是「有没有」的开关 |

开启条件评估报告可以看到每个类的判定过程：

```properties
debug=true
```

控制台会打出 `CONDITIONS EVALUATION REPORT`，里面能看到：

```
GreetingAutoConfiguration matched:
   - @ConditionalOnProperty (greeting.enabled=true) matched
GreetingAutoConfiguration#greetingService matched:
   - @ConditionalOnMissingBean (types: org.example.greeting.GreetingService) did not find any beans
```

---

## 为什么这个项目没有 spring-boot-maven-plugin

因为它是**库**，不是**应用**。

带 `spring-boot-maven-plugin` 会打成「可执行 jar」，类被放进 `BOOT-INF/classes/`，
标准 classpath 机制看不到，别的项目依赖它会报 `ClassNotFoundException`。

所以本项目的 pom **刻意不配置**该插件。

---

## 文件说明

```
src/main/java/org/example/greeting/
├── GreetingAutoConfiguration.java   自动配置类，决定装不装、怎么装
├── GreetingProperties.java          yml 配置的容器（靠 setter 绑定）
└── GreetingService.java             真正干活的类（无 Spring 注解）

src/main/resources/META-INF/spring/
└── org.springframework.boot.autoconfigure.AutoConfiguration.imports
                                     清单，一行一个全限定类名
```