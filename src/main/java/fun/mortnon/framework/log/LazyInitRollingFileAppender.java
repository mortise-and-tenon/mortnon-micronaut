package fun.mortnon.framework.log;


import ch.qos.logback.core.rolling.RollingFileAppender;
import org.graalvm.nativeimage.ImageInfo;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 自定义的日志文件 appender
 * 避免 GraalVM Native Image 编译时 logback 进程占用日志文件导致编译失败
 *
 * @author dev2007
 * @date 2024/3/13
 */
public class LazyInitRollingFileAppender<E> extends RollingFileAppender<E> {
    private AtomicBoolean started = new AtomicBoolean(false);

    @Override
    public void start() {
        if (!ImageInfo.inImageBuildtimeCode()) {
            super.start();
            this.started.set((true));
        }
    }

    @Override
    public void doAppend(E eventObject) {
        if (!ImageInfo.inImageBuildtimeCode()) {
            if (!this.started.get()) {
                maybeStart();
            }
            super.doAppend(eventObject);
        }
    }

    private void maybeStart() {
        lock.lock();
        try {
            if (!this.started.get()) {
                this.start();
            }
        } finally {

            lock.unlock();
        }
    }
}
