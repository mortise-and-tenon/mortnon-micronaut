package fun.mortnon;

import io.micronaut.runtime.Micronaut;

/**
 * 应用启动类
 *
 * @author dev2007
 * @date 2023/2/3
 */
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}