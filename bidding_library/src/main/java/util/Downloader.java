package util;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Класс загрузчик для объектов JSON
 * @author Shestakov Artem
 * */
@Slf4j
@AllArgsConstructor
public class Downloader implements Runnable {
    private final static String JSONS_FOLDER_NAME = "/jsons/";
    private String link;
    private Path source;


    @SneakyThrows
    @Override
    public void run() {
        String folderName = link.substring(link.lastIndexOf("/") + 1, link.lastIndexOf(".json"));
        Path newFolder = createDirectory(source, folderName);
        downloadFiles(newFolder, link);
    }

    private Path createDirectory(Path source, String folderName) throws IOException {
        Path newFolder = Paths.get(source.toAbsolutePath() + JSONS_FOLDER_NAME + folderName + "/");
        Files.createDirectories(newFolder);
        log.info("Создана папка: {}", newFolder);
        return newFolder;
    }

    private void downloadFiles(Path newFolder, String link) throws Exception {
        List<String> linkByObjects = DownloaderJsonsHelper.getDailyDownloadLinkByObjects(link);
        DownloaderJsonsHelper.downloadFiles(linkByObjects, newFolder.toString());
    }
}