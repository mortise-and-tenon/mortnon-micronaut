package fun.mortnon.framework.message;

import freemarker.cache.TemplateLoader;
import fun.mortnon.dal.sys.entity.SysTemplate;
import fun.mortnon.dal.sys.repository.TemplateRepository;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 用于 freemarker 的数据库模板加载器
 *
 * @author dev2007
 * @date 2024/3/21
 */
@Singleton
public class DbTemplateLoader implements TemplateLoader {
    @Inject
    private TemplateRepository templateRepository;

    public static final ExecutorService CUSTOM_THREAD_POOL = Executors.newFixedThreadPool(4);

    @Override
    public Object findTemplateSource(String s) throws IOException {
        SysTemplate template = templateRepository.existsByName(s)
                .subscribeOn(Schedulers.fromExecutorService(CUSTOM_THREAD_POOL))
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.just(new SysTemplate());
                    }
                    return templateRepository.findByName(s);
                }).block(Duration.ofSeconds(3));
        if (StringUtils.isNotEmpty(template.getName())) {
            return new StringTemplateSource(template.getName(), template.getContent(), template.getGmtCreate().toEpochMilli());
        }

        return null;
    }

    @Override
    public long getLastModified(Object o) {
        if (o instanceof StringTemplateSource) {
            return ((StringTemplateSource) o).lastModified;
        }
        return 0;
    }

    @Override
    public Reader getReader(Object o, String s) throws IOException {
        if (o instanceof StringTemplateSource) {
            return new StringReader(((StringTemplateSource) o).source);
        }
        return null;
    }

    @Override
    public void closeTemplateSource(Object o) throws IOException {

    }

    @Data
    @AllArgsConstructor
    public static class StringTemplateSource {
        private String name;
        private String source;
        private long lastModified;

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof StringTemplateSource) {
                if (obj == this) {
                    return true;
                } else {
                    return ((StringTemplateSource) obj).name.equals(this.name);
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }
    }
}
