package ru.shestakov_a.bidding_db_loader.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.shestakov_a.bidding_db_loader.BiddingDbLoader;
import ru.shestakov_a.bidding_db_loader.service.DownloadService;
import util.Downloader;
import util.DownloaderJsonsHelper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Класс реализации интерфейса сервисного слоя для загрузки JSON объектов
 * @author Shestakov Artem
 * */
@Slf4j
@Service
public class DownloadServiceImpl implements DownloadService {
    @Value("${json.download.link}")
    private String downloadLink;

    @Value("${downloader.folder}")
    private String downloadFolder;

    @Override
    public void download() throws Exception {
        List<String> linksByDay = DownloaderJsonsHelper.getDownloadLinksByDay(downloadLink);
        linksByDay =linksByDay.stream().skip(327).collect(Collectors.toList());
        downloadJsons(linksByDay);
    }

    private void downloadJsons(List<String> links) {
        Path source = getSourcePath();
        ExecutorService service = Executors.newFixedThreadPool(20);
        long start = new Date().getTime();
        for (String link : links) {
            Runnable downloader = new Downloader(link, source);
            service.execute(downloader);
        }
        service.shutdown();
        long duration = (new Date().getTime() - start) / 1000;
        log.info("Врем загрузки составило {} секунд", duration);
    }

    private Path getSourcePath() {
        return !downloadFolder.isBlank() ? Path.of(downloadFolder) :
                Paths.get(Objects.requireNonNull(BiddingDbLoader.class.getResource("/")).getPath());
    }
}