package ru.shestakov_a.bidding_telegram_bot.service;

import real_estate.dto.bot.AuctionInformationDTO;
import ru.shestakov_a.bidding_telegram_bot.dto.MessageIdDTO;
import ru.shestakov_a.bidding_telegram_bot.dto.LocationType;
import ru.shestakov_a.bidding_telegram_bot.entity.db.*;
import ru.shestakov_a.bidding_telegram_bot.exception.DatabaseEntityTimeOverException;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.grizzly.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shestakov_a.bidding_telegram_bot.repository.*;

import java.time.Instant;

/**
 * Класс реализации интерфейса сервисного слоя
 * @author Shestakov Artem
 * */
@Service
@Transactional
@Slf4j
public class TemporaryDataServiceImpl implements TemporaryDataService {
    @Autowired
    private CadastralNumberRepository cadastralNumberRepository;
    @Autowired
    private LocationTypeRepository locationTypeRepository;
    @Autowired
    private QuantityRepository quantityRepository;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private CursorRepository cursorRepository;
    @Autowired
    private AuctionInformationRepository auctionInformationRepository;

    @Override
    public String getCadastralNumber(MessageIdDTO messageIdDto) throws DatabaseEntityTimeOverException {
        try {
            return cadastralNumberRepository.findFirstByChatIdAndMessageIdOrderById(messageIdDto.getChatId(),
                    messageIdDto.getMessageId()).getCadastralNumber();
        } catch (NullPointerException e) {
            DatabaseEntityTimeOverException databaseEntityTimeOverException = new DatabaseEntityTimeOverException();
            databaseEntityTimeOverException.setChatId(messageIdDto.getChatId());
            throw databaseEntityTimeOverException;
        }
    }

    @Override
    public void setCadastralNumber(MessageIdDTO messageIdDto, String cadastralNumber) {
        cadastralNumberRepository.save(CadastralNumberEntity.builder()
                .chatId(messageIdDto.getChatId())
                .messageId(messageIdDto.getMessageId())
                .cadastralNumber(cadastralNumber)
                .creationTime(Instant.now().toEpochMilli())
                .build());
    }

    @Override
    public Pair<String, String> getRegionAndCity(MessageIdDTO messageIdDto) {
        try {
            RegionEntity regionEntity = regionRepository.findFirstByChatIdAndMessageIdOrderById(messageIdDto.getChatId(),
                    messageIdDto.getMessageId());
            return new Pair<>(regionEntity.getRegion(), regionEntity.getCity());
        } catch (NullPointerException e) {
            DatabaseEntityTimeOverException databaseEntityTimeOverException = new DatabaseEntityTimeOverException();
            databaseEntityTimeOverException.setChatId(messageIdDto.getChatId());
            throw databaseEntityTimeOverException;
        }
    }

    @Override
    public void setRegionAndCity(MessageIdDTO messageIdDto, String region, String city) {
        regionRepository.save(RegionEntity.builder()
                .chatId(messageIdDto.getChatId())
                .messageId(messageIdDto.getMessageId())
                .region(region)
                .city(city)
                .creationTime(Instant.now().toEpochMilli())
                .build());
    }

    @Override
    public Integer getQuantity(MessageIdDTO messageIdDto) throws DatabaseEntityTimeOverException {
        try {
            return quantityRepository.findFirstByChatIdAndMessageIdOrderById(messageIdDto.getChatId(),
                            messageIdDto.getMessageId())
                    .getQuantity();
        } catch (NullPointerException e) {
            DatabaseEntityTimeOverException databaseEntityTimeOverException = new DatabaseEntityTimeOverException();
            databaseEntityTimeOverException.setChatId(messageIdDto.getChatId());
            throw databaseEntityTimeOverException;
        }
    }

    @Override
    public void setQuantity(MessageIdDTO messageIdDto, int quantity) {
        quantityRepository.save(QuantityEntity.builder()
                .chatId(messageIdDto.getChatId())
                .messageId(messageIdDto.getMessageId())
                .quantity(quantity)
                .creationTime(Instant.now().toEpochMilli())
                .build());
    }

    @Override
    public Integer getLocation(MessageIdDTO messageIdDto) throws DatabaseEntityTimeOverException {
        try {
            return locationTypeRepository.findFirstByChatIdAndMessageIdOrderById(
                    messageIdDto.getChatId(),
                    messageIdDto.getMessageId()
            ).getLocationType();
        } catch (NullPointerException e) {
            DatabaseEntityTimeOverException databaseEntityTimeOverException = new DatabaseEntityTimeOverException();
            databaseEntityTimeOverException.setChatId(messageIdDto.getChatId());
            throw databaseEntityTimeOverException;
        }
    }

    @Override
    public void setLocation(MessageIdDTO messageIdDto, LocationType locationType) {
        locationTypeRepository.save(LocationTypeEntity.builder()
                .chatId(messageIdDto.getChatId())
                .messageId(messageIdDto.getMessageId())
                .locationType(locationType.getIndex())
                .creationTime(Instant.now().toEpochMilli())
                .build());
    }

    @Override
    public Integer getCursor(MessageIdDTO messageIdDto, Integer cursor) {
        try {
            return cursorRepository.findFirstByChatIdAndMessageIdAndCursorOrderById(
                            messageIdDto.getChatId(),
                            messageIdDto.getMessageId(),
                            cursor)
                    .getPreviousCursor();
        } catch (NullPointerException e) {
            DatabaseEntityTimeOverException databaseEntityTimeOverException = new DatabaseEntityTimeOverException();
            databaseEntityTimeOverException.setChatId(messageIdDto.getChatId());
            throw databaseEntityTimeOverException;
        }
    }

    @Override
    public void setCursor(MessageIdDTO messageIdDto, Integer cursor, Integer previousCursor) {
        cursorRepository.save(CursorEntity.builder()
                .chatId(messageIdDto.getChatId())
                .messageId(messageIdDto.getMessageId())
                .cursor(cursor)
                .previousCursor(previousCursor)
                .creationTime(Instant.now().toEpochMilli())
                .build());
    }

    @Override
    public AuctionInformationDTO getAuctionInformation(MessageIdDTO messageIdDTO, Integer cursor) {
        try {
            return mapAuctionInformationToDto(auctionInformationRepository.findFirstByChatIdAndMessageIdAndCursorOrderById(
                    messageIdDTO.getChatId(),
                    messageIdDTO.getMessageId(),
                    cursor));

        } catch (NullPointerException e) {
            DatabaseEntityTimeOverException databaseEntityTimeOverException = new DatabaseEntityTimeOverException();
            databaseEntityTimeOverException.setChatId(messageIdDTO.getChatId());
            throw databaseEntityTimeOverException;
        }
    }

    @Override
    public void setAuctionInformation(MessageIdDTO messageIdDTO, Integer cursor, AuctionInformationDTO auctionInformationDTO) {
        auctionInformationRepository.save(AuctionInformationEntity.builder()
                .chatId(messageIdDTO.getChatId())
                .messageId(messageIdDTO.getMessageId())
                .auctionStartDate(auctionInformationDTO.getAuctionStartDate())
                .biddingEndTime(auctionInformationDTO.getBiddingEndTime())
                .biddingStartTime(auctionInformationDTO.getBiddingStartTime())
                .cursor(cursor)
                .creationTime(Instant.now().toEpochMilli())
                .build());
    }

    @Override
    public void deleteCursor(Long creationTime) {
        try {
            cursorRepository.deleteByCreationTimeLessThan(creationTime);
        } catch (Exception e) {
            log.warn("Ошибка {} при удалении сущности: {}", e.getClass().getName(), e.getMessage());
        }
    }

    @Transactional
    @Override
    public void deleteCadastralNumber(Long creationTime) {
        try {
            cadastralNumberRepository.deleteByCreationTimeLessThan(creationTime);
        } catch (Exception e) {
            log.warn("Ошибка {} при удалении сущности: {}", e.getClass().getName(), e.getMessage());
        }
    }

    @Transactional
    @Override
    public void deleteQuantity(Long creationTime) {
        try {
            quantityRepository.deleteByCreationTimeLessThan(creationTime);
        } catch (Exception e) {
            log.warn("Ошибка {} при удалении сущности: {}", e.getClass().getName(), e.getMessage());
        }
    }

    @Transactional
    @Override
    public void deleteRegionCity(Long creationTime) {
        try {
            regionRepository.deleteByCreationTimeLessThan(creationTime);
        } catch (Exception e) {
            log.warn("Ошибка {} при удалении сущности: {}", e.getClass().getName(), e.getMessage());
        }
    }

    @Transactional
    @Override
    public void deleteLocation(Long creationTime) {
        try {
            locationTypeRepository.deleteByCreationTimeLessThan(creationTime);
        } catch (Exception e) {
            log.warn("Ошибка {} при удалении сущности: {}", e.getClass().getName(), e.getMessage());
        }
    }

    @Override
    public void deleteAuctionInformation(Long creationTime) {
        try {
            auctionInformationRepository.deleteByCreationTimeLessThan(creationTime);
        } catch (Exception e) {
            log.warn("Ошибка {} при удалении сущности: {}", e.getClass().getName(), e.getMessage());
        }
    }

    private AuctionInformationDTO mapAuctionInformationToDto(AuctionInformationEntity auctionInformationEntity) {
        return AuctionInformationDTO.builder()
                .auctionStartDate(auctionInformationEntity.getAuctionStartDate())
                .biddingEndTime(auctionInformationEntity.getBiddingEndTime())
                .biddingStartTime(auctionInformationEntity.getBiddingStartTime())
                .build();
    }
}