package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Утилитарный класс со вспомогательными методами для загрузки файлов
 * @author Shestakov Artem
 * */
@UtilityClass
@Slf4j
public class DownloaderJsonsHelper {

    public void downloadFiles(List<String> links, String folderPath) {
        links.forEach(
                link -> {
                    try (InputStream inputStream = ConnectionHelper.getUrlConnection(link).getInputStream()) {
                        Path path = Paths.get(folderPath + link.substring(link.lastIndexOf("/")));
                        File file = path.toFile();
                        try (OutputStream outputStream = new FileOutputStream(file)) {
                            IOUtils.copy(inputStream, outputStream);
                        }
                    } catch (Exception e) {
                        log.warn("Ошибка {} во время скачивания файла: {}", e.getClass().getName(), e.getMessage());
                    }
                });
    }

    public List<String> getFileNameList(String directoryName) {
        try {
            return Files.walk(Paths.get(directoryName))
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.warn("Ошибка {} доступа к директории: {}", e.getClass().getName(), e.getMessage());
        }
        return null;
    }

    public List<String> getDailyDownloadLinkByObjects(String link) throws NoSuchAlgorithmException, IOException, KeyManagementException /*throws Exception*/ {
        List<String> linksByObjects = new ArrayList<>();
        try (InputStream inputStream = ConnectionHelper.getUrlConnection(link).getInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(inputStream);
            JsonNode listObjectsNode = rootNode.path("listObjects");

            Iterator<JsonNode> iterator = listObjectsNode.elements();
            while (iterator.hasNext()) {
                linksByObjects.add(iterator.next().get("href").textValue());
            }
        }
        return linksByObjects;
    }

    public List<String> getDownloadLinksByDay(String href) throws Exception {
        List<String> biddingLinksByDay = new ArrayList<>();
        try (InputStream inputStream = ConnectionHelper.getUrlConnection(href).getInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(inputStream);

            Iterator<JsonNode> data = rootNode.get("data").elements();
            while (data.hasNext()) {
                biddingLinksByDay.add(data.next().get("source").textValue());
            }
        }
        return biddingLinksByDay;
    }
}