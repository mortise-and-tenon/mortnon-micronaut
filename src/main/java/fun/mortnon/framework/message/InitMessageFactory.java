package fun.mortnon.framework.message;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * @author dev2007
 * @date 2024/3/21
 */
@Factory
@Slf4j
public class InitMessageFactory {

    private DbTemplateLoader dbTemplateLoader;

    @Inject
    public InitMessageFactory(DbTemplateLoader dbTemplateLoader) {
        this.dbTemplateLoader = dbTemplateLoader;
    }

    @Bean
    public Configuration configuration() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        File folder = new File("/templates");
        if (!folder.exists()) {
            log.info("Create message template folder.");
            folder.mkdirs();
        }

        try {
            configuration.setDirectoryForTemplateLoading(folder);
        } catch (IOException e) {
            log.warn("Failed to set Freemarker template folder.");
        }

        configuration.setDefaultEncoding("GB2312");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setTemplateLoader(dbTemplateLoader);

        return configuration;
    }
}
