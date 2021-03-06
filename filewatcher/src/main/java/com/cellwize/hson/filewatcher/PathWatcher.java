package com.cellwize.hson.filewatcher;

import com.cellwize.hson.eventbroker.api.KafkaMeasEventPublisher;
import com.cellwize.hson.parsers.ParserException;
import com.cellwize.hson.parsers.nokiaxml.NokiaPMXmlParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

@Component
public class PathWatcher {

    @Autowired
    KafkaMeasEventPublisher publisher;

    public void watchDirectoryPath(Path path) {
        // Sanity check - Check if path is a folder
        try {
            Boolean isFolder = (Boolean) Files.getAttribute(path, "basic:isDirectory", NOFOLLOW_LINKS);
            if (!isFolder) {
                throw new IllegalArgumentException("Path: " + path + " is not a folder");
            }
        } catch (IOException ioe) {
            // Folder does not exists
            ioe.printStackTrace();
        }
        // We obtain the file system of the Path
        FileSystem fs = path.getFileSystem();
        // We create the new WatchService using the new try() block
        try (WatchService service = fs.newWatchService()) {
            // We register the path to the service
            // We watch for creation events
            path.register(service, ENTRY_CREATE);
            // Start the infinite polling loop
            WatchKey key = null;
            while (true) {
                key = service.take();
                // Dequeueing events
                Kind<?> kind = null;
                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    // Get the type of the event
                    kind = watchEvent.kind();
                    if (ENTRY_CREATE == kind) {
                        // A new Path was created
                        Path dir = (Path)key.watchable();
                        Path newPath =  dir.resolve(((WatchEvent<Path>) watchEvent).context());
                        Thread.sleep(1000);
                        // Output
                        Thread thread = new Thread(() -> {
                            NokiaPMXmlParser nokiaPMXmlParser = new NokiaPMXmlParser("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                            nokiaPMXmlParser.setResultHandler(publisher);
                            try {
                                nokiaPMXmlParser.parse(newPath.toUri(), new FileInputStream(newPath.toFile()));
                            } catch (ParserException | IOException e) {
                                e.printStackTrace();
                            }

                        });
                        thread.start();
                    }
                }
                if (!key.reset()) {
                    break; // loop
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
