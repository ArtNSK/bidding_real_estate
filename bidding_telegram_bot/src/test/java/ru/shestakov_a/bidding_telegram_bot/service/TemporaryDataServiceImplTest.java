package ru.shestakov_a.bidding_telegram_bot.service;

import ru.shestakov_a.bidding_telegram_bot.dto.MessageIdDTO;
import org.glassfish.grizzly.utils.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import real_estate.dto.bot.AuctionInformationDTO;
import ru.shestakov_a.bidding_telegram_bot.entity.db.*;
import ru.shestakov_a.bidding_telegram_bot.repository.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase
class TemporaryDataServiceImplTest {

    @Mock
    CadastralNumberRepository cadastralNumberRepository;
    @Mock
    LocationTypeRepository locationTypeRepository;
    @Mock
    QuantityRepository quantityRepository;
    @Mock
    RegionRepository regionRepository;
    @Mock
    CursorRepository cursorRepository;
    @Mock
    AuctionInformationRepository auctionInformationRepository;
    @InjectMocks
    TemporaryDataServiceImpl temporaryDataService = new TemporaryDataServiceImpl();

    private final List<AuctionInformationEntity> auctionInformationEntityList = new ArrayList<>();
    private final List<AuctionInformationDTO> auctionInformationDTOList = new ArrayList<>();
    private final List<CadastralNumberEntity> cadastralNumberEntityList = new ArrayList<>();
    private final List<CursorEntity> cursorEntityList = new ArrayList<>();
    private final List<LocationTypeEntity> locationTypeEntityList = new ArrayList<>();
    private final List<QuantityEntity> quantityEntityList = new ArrayList<>();
    private final List<RegionEntity> regionEntityList = new ArrayList<>();


    @BeforeAll
    void setup() {
        MockitoAnnotations.openMocks(this);
        Long chatId = 1L;
        String region = "California";
        String city = "Los Angeles";

        for (int i = 1; i < 5 ; i++) {
            Integer cursor = i;
            Integer previousCursor = i-1;

            LocalDateTime currentTime = LocalDateTime.now(ZoneId.ofOffset("UTC", ZoneOffset.of("+03")));
            Long currentTimeMilli = Instant.now().toEpochMilli();

            MessageIdDTO messageIdDTO = new MessageIdDTO(chatId, i);
            AuctionInformationEntity auctionInformationEntity = AuctionInformationEntity.builder()
                    .id(i)
                    .chatId(messageIdDTO.getChatId())
                    .messageId(messageIdDTO.getMessageId())
                    .auctionStartDate(currentTime)
                    .biddingEndTime(currentTime)
                    .biddingStartTime(currentTime)
                    .cursor(cursor)
                    .creationTime(currentTimeMilli)
                    .build();
            auctionInformationEntityList.add(auctionInformationEntity);
            auctionInformationDTOList.add(mapAuctionInformationToDto(auctionInformationEntity));

            String cadastralNumber = "00:00:0000000:0" + i;
            CadastralNumberEntity cadastralNumberEntity = CadastralNumberEntity.builder()
                    .id(i)
                    .chatId(messageIdDTO.getChatId())
                    .messageId(messageIdDTO.getMessageId())
                    .cadastralNumber(cadastralNumber)
                    .creationTime(currentTimeMilli)
                    .build();
            cadastralNumberEntityList.add(cadastralNumberEntity);

            CursorEntity cursorEntity = CursorEntity.builder()
                    .id(i)
                    .chatId(messageIdDTO.getChatId())
                    .messageId(messageIdDTO.getMessageId())
                    .cursor(cursor)
                    .previousCursor(previousCursor)
                    .creationTime(currentTimeMilli)
                    .build();
            cursorEntityList.add(cursorEntity);

            LocationTypeEntity locationTypeEntity = LocationTypeEntity.builder()
                    .id(i)
                    .chatId(messageIdDTO.getChatId())
                    .messageId(messageIdDTO.getMessageId())
                    .locationType(i%2)
                    .creationTime(currentTimeMilli)
                    .build();
            locationTypeEntityList.add(locationTypeEntity);

            QuantityEntity quantityEntity = QuantityEntity.builder()
                    .id(i)
                    .chatId(messageIdDTO.getChatId())
                    .messageId(messageIdDTO.getMessageId())
                    .quantity(i)
                    .creationTime(currentTimeMilli)
                    .build();
            quantityEntityList.add(quantityEntity);

            RegionEntity regionEntity = RegionEntity.builder()
                    .id(i)
                    .chatId(messageIdDTO.getChatId())
                    .messageId(messageIdDTO.getMessageId())
                    .region(region)
                    .city(city)
                    .creationTime(currentTimeMilli)
                    .build();
            regionEntityList.add(regionEntity);
        }
    }

    @ParameterizedTest
    @CsvSource({"1,1"})
    void getCadastralNumber(Long chatId, Integer messageId) {
        when(cadastralNumberRepository.findFirstByChatIdAndMessageIdOrderById(anyLong(), anyInt()))
                .thenReturn(cadastralNumberEntityList.stream()
                        .filter(entity -> entity.getChatId().equals(chatId) &&
                                    entity.getMessageId().equals(messageId)).findFirst().get());
        MessageIdDTO messageIdDTO = new MessageIdDTO(chatId, messageId);

        String expected = cadastralNumberEntityList.stream()
                .filter(entity -> entity.getChatId().equals(chatId) &&
                        entity.getMessageId().equals(messageId)).findFirst().get().getCadastralNumber();
        String actual = temporaryDataService.getCadastralNumber(messageIdDTO);

        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource({"1,1"})
    void getRegionAndCity(Long chatId, Integer messageId) {
        when(regionRepository.findFirstByChatIdAndMessageIdOrderById(anyLong(), anyInt()))
                .thenReturn(regionEntityList.stream()
                        .filter(entity -> entity.getChatId().equals(chatId) &&
                                entity.getMessageId().equals(messageId)).findFirst().get());

        RegionEntity regionEntity = regionEntityList.stream()
                .filter(entity -> entity.getChatId().equals(chatId) &&
                        entity.getMessageId().equals(messageId)).findFirst().get();
        MessageIdDTO messageIdDTO = new MessageIdDTO(chatId, messageId);

        Pair<String, String> expected = new Pair<>(regionEntity.getRegion(), regionEntity.getCity());
        Pair<String, String> actual = temporaryDataService.getRegionAndCity(messageIdDTO);

//        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(expected.getFirst(), actual.getFirst());
        Assertions.assertEquals(expected.getSecond(), actual.getSecond());
    }

    @ParameterizedTest
    @CsvSource({"1,1"})
    void getQuantity(Long chatId, Integer messageId) {
        when(quantityRepository.findFirstByChatIdAndMessageIdOrderById(anyLong(), anyInt()))
                .thenReturn(quantityEntityList.stream()
                        .filter(entity -> entity.getChatId().equals(chatId) &&
                                entity.getMessageId().equals(messageId)).findFirst().get());
        MessageIdDTO messageIdDTO = new MessageIdDTO(chatId, messageId);

        Integer expected = quantityEntityList.stream()
                .filter(entity -> entity.getChatId().equals(chatId) &&
                        entity.getMessageId().equals(messageId)).findFirst().get().getQuantity();

        Integer actual = temporaryDataService.getQuantity(messageIdDTO);

        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource({"1,1"})
    void getLocation(Long chatId, Integer messageId) {
        when(locationTypeRepository.findFirstByChatIdAndMessageIdOrderById(anyLong(), anyInt()))
                .thenReturn(locationTypeEntityList.stream()
                        .filter(entity -> entity.getChatId().equals(chatId) &&
                                entity.getMessageId().equals(messageId)).findFirst().get());
        MessageIdDTO messageIdDTO = new MessageIdDTO(chatId, messageId);

        Integer expected = locationTypeEntityList.stream()
                .filter(entity -> entity.getChatId().equals(chatId) &&
                        entity.getMessageId().equals(messageId)).findFirst().get().getLocationType();
        Integer actual = temporaryDataService.getLocation(messageIdDTO);

        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource({"1,1,1"})
    void getCursor(Long chatId, Integer messageId, Integer cursor) {
        when(cursorRepository.findFirstByChatIdAndMessageIdAndCursorOrderById(anyLong(), anyInt(), anyInt()))
                .thenReturn(cursorEntityList.stream()
                        .filter(entity -> entity.getChatId().equals(chatId) &&
                                entity.getMessageId().equals(messageId) &&
                                entity.getCursor().equals(cursor)).findFirst().get());
        MessageIdDTO messageIdDTO = new MessageIdDTO(chatId, messageId);

        Integer expected = cursorEntityList.stream()
                .filter(entity -> entity.getChatId().equals(chatId) &&
                        entity.getMessageId().equals(messageId) &&
                        entity.getCursor().equals(cursor)).findFirst().get().getPreviousCursor();
        Integer actual = temporaryDataService.getCursor(messageIdDTO, cursor);

        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource({"1,1,1"})
    void getAuctionInformation(Long chatId, Integer messageId, Integer cursor) {
        when(auctionInformationRepository.findFirstByChatIdAndMessageIdAndCursorOrderById(anyLong(), anyInt(), anyInt()))
                .thenReturn(auctionInformationEntityList.stream()
                        .filter(entity -> entity.getChatId().equals(chatId) &&
                                entity.getMessageId().equals(messageId) &&
                                entity.getCursor().equals(cursor)).findFirst().get());
        MessageIdDTO messageIdDTO = new MessageIdDTO(chatId, messageId);

        AuctionInformationDTO expected = mapAuctionInformationToDto(auctionInformationEntityList.stream()
                .filter(entity -> entity.getChatId().equals(chatId) &&
                        entity.getMessageId().equals(messageId) &&
                        entity.getCursor().equals(cursor)).findFirst().get());
        AuctionInformationDTO actual = temporaryDataService.getAuctionInformation(messageIdDTO, cursor);

        Assertions.assertEquals(expected, actual);
    }

    private AuctionInformationDTO mapAuctionInformationToDto(AuctionInformationEntity auctionInformationEntity) {
        return AuctionInformationDTO.builder()
                .auctionStartDate(auctionInformationEntity.getAuctionStartDate())
                .biddingEndTime(auctionInformationEntity.getBiddingEndTime())
                .biddingStartTime(auctionInformationEntity.getBiddingStartTime())
                .build();
    }
}