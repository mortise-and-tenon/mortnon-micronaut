package fun.mortnon.framework.log;


import ch.qos.logback.core.rolling.RollingFileAppender;

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
        if (!inGraalImageBuildtimeCode()) {
            super.start();
            this.started.set(true);
        }
    }

    /**
     * This method is synchronized to avoid double start from doAppender().
     */
    protected void maybeStart() {
        lock.lock();
        try {
            if (!this.started.get()) {
                this.start();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void doAppend(E eventObject) {
        if (!inGraalImageBuildtimeCode()) {
            if (!this.started.get()) {
                maybeStart();
            }
            super.doAppend(eventObject);
        }
    }

    private static final String PROPERTY_IMAGE_CODE_VALUE_BUILDTIME = "buildtime";
    private static final String PROPERTY_IMAGE_CODE_KEY = "org.graalvm.nativeimage.imagecode";

    private static boolean inGraalImageBuildtimeCode() {
        return PROPERTY_IMAGE_CODE_VALUE_BUILDTIME.equals(System.getProperty(PROPERTY_IMAGE_CODE_KEY));
    }

}
