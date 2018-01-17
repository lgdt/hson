package com.cellwize.hson.hsonmain;

import com.cellwize.hson.filewatcher.PathWatcher;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;

public class FileWatcherTest {

    @Test
    public void testFileWatcher() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        PathWatcher pathWatcher = context.getBean("pathWatcher", PathWatcher.class);
        File dir = new File("C:/Liya/hson");
        pathWatcher.watchDirectoryPath(dir.toPath());
    }
}
