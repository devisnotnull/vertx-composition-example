# Where to find the expected sources / Information

 * Had been edited in NetBeans, so if you expect to find eclipse project files, you should run ``mvn eclipse:eclipse` or `./gradlew eclipse` first
 * The handlers itself are `com.mycompany.myproject.CompositionHandler` and ``com.mycompany.myproject.RxCompositionHandler`
 * Helper-Classes are `com.mycompany.myproject.util.event.ChainableProcess`
 * Tests are `com.mycompany.myproject.test.integration.java.CompositionHandlerIntegrationTest`, `com.mycompany.myproject.test.integration.java.RxCompositionHandlerIntegrationTest`, `com.mycompany.myproject.test.integration.java.RxCompositionHandlerTest`, `com.mycompany.myproject.util.event.ChainableProcessTest`
 * Can be tested manually with `curl -d "{\"composition\" : [\"range\",\"square\",\"inc\",\"sum\"], \"argument\" : 10}" http://localhost:8080/comp.rx.configurable`, `curl -d "{\"composition\" : [\"range\",\"square\",\"inc\",\"sum\"], \"argument\" : 10}" http://localhost:8080/comp.configurable`, `curl -d "[1,2,3]" http://localhost:8080/comp`, `curl -d "[1,2,3]" http://localhost:8080/comp.rx`
