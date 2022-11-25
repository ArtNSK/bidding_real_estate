package ru.shestakov_a.bidding_telegram_bot.service;

import real_estate.dto.bot.AuctionInformationDTO;
import ru.shestakov_a.bidding_telegram_bot.dto.MessageIdDTO;
import ru.shestakov_a.bidding_telegram_bot.dto.LocationType;
import ru.shestakov_a.bidding_telegram_bot.exception.DatabaseEntityTimeOverException;
import org.glassfish.grizzly.utils.Pair;

/**
 * Интерфейс сервисного слоя
 * @author Shestakov Artem
 * */
public interface TemporaryDataService {
    String getCadastralNumber(MessageIdDTO messageIdDto) throws DatabaseEntityTimeOverException;

    void setCadastralNumber(MessageIdDTO messageIdDto, String cadastralNumber);

    Pair<String, String> getRegionAndCity(MessageIdDTO messageIdDto) throws DatabaseEntityTimeOverException;

    void setRegionAndCity(MessageIdDTO messageIdDto, String region, String city);

    Integer getQuantity(MessageIdDTO messageIdDto) throws DatabaseEntityTimeOverException;

    void setQuantity(MessageIdDTO messageIdDto, int quantity);

    Integer getLocation(MessageIdDTO messageIdDto) throws DatabaseEntityTimeOverException;

    void setLocation(MessageIdDTO messageIdDto, LocationType locationType);

    Integer getCursor(MessageIdDTO messageIdDto, Integer cursor);

    void setCursor(MessageIdDTO messageIdDto, Integer cursor, Integer previousCursor);

    AuctionInformationDTO getAuctionInformation (MessageIdDTO messageIdDTO, Integer cursor);

    void setAuctionInformation(MessageIdDTO messageIdDTO, Integer cursor, AuctionInformationDTO auctionInformationDTO);

    void deleteCursor(Long creationTime);

    void deleteCadastralNumber(Long creationTime);

    void deleteQuantity(Long creationTime);

    void deleteRegionCity(Long creationTime);

    void deleteLocation(Long creationTime);

    void deleteAuctionInformation(Long creationTime);


}
