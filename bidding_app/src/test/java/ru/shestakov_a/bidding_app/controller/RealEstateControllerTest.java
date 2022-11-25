package ru.shestakov_a.bidding_app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import real_estate.dto.controller.*;
import real_estate.entity.AddressEntity;
import real_estate.entity.RealEstateEntity;
import ru.shestakov_a.bidding_app.model.error.DefaultErrorResponse;
import ru.shestakov_a.bidding_app.repository.AddressRepository;
import ru.shestakov_a.bidding_app.repository.RealEstateRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase*/
public class RealEstateControllerTest {
   /* @Autowired
    private MockMvc mockMvc;

    @Autowired
    RealEstateRepository realEstateRepository;

    @Autowired
    AddressRepository addressRepository;



    private List<AddressEntity> addressEntityList = new ArrayList<>();
    private List<RealEstateEntity> realEstateEntityList = new ArrayList<>();

    private ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @BeforeAll
    void setup() {
        clearDb();
        initTestData();
    }

    @AfterAll
    void cleanup() {
        clearDb();
    }

    private void clearDb() {
        realEstateRepository.deleteAll();
        addressRepository.deleteAll();
    }

    private void initTestData() {
        for (int i = 0; i < 10; i++) {
            AddressEntity address = AddressEntity.builder()
                    .region("California")
                    .district("Pacoima")
                    .city("Los Angeles")
                    .microdistrict("")
                    .street("Roslyndale Avenue")
                    .building("9303")
                    .apartment(String.valueOf(1885 + i))
                    .room(String.valueOf(2015 + i))
                    .housing(String.valueOf(1985 + i))
                    .build();
            addressEntityList.add(address);

            RealEstateEntity realEstateEntity = RealEstateEntity.builder()
                    .address(address)
                    .cadastralNumber("00:00:0000000:0" + i)
                    .link("http://127.0.0.1")
                    .minPrice("1")
                    .realEstateType("Комната")
                    .lotNumber(0)
                    .lotName("Комната в доме")
                    .publishDate(LocalDateTime.of(1985, Month.OCTOBER, 26, 1, 21))
                    .biddingStartTime(LocalDateTime.of(1885, Month.SEPTEMBER, 2, 8, 0))
                    .biddingEndTime(LocalDateTime.of(2015, Month.OCTOBER, 21, 7, 28))
                    .auctionStartDate(LocalDateTime.of(2015, Month.OCTOBER, 21, 7, 28))
                    .build();
            realEstateEntityList.add(realEstateEntity);
        }
        realEstateEntityList = realEstateRepository.saveAll(realEstateEntityList);

    }

    private List<String> getCadastralNumbers(long limit) throws JsonProcessingException {
        AtomicInteger counter = new AtomicInteger();
        return Stream
                .generate(() -> "00:00:0000000:0" + counter.getAndIncrement())
                .limit(limit)
                .collect(Collectors.toList());

    }

    @Test
    void getRealEstateList() throws Exception {
        int limit = 5;
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/api/real-estate/list")
                        .param("limit", String.valueOf(limit)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        RealEstateResponseDTO<RealEstateDTO> realEstateResponseDTO = objectMapper
                .readValue(mockHttpServletResponse.getContentAsString(), new TypeReference<RealEstateResponseDTO<RealEstateDTO>>() {
                });
        assertEquals(limit, realEstateResponseDTO.getItems().size());
        assertEquals(realEstateResponseDTO.getItems().get(realEstateResponseDTO.getItems().size() - 1).getId(), realEstateResponseDTO.getCursor());

        mockHttpServletResponse = mockMvc.perform(get("/api/real-estate/list")
                        .param("limit", String.valueOf(limit))
                        .param("cadastralNumber", "name doesn't exist"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();

        DefaultErrorResponse defaultErrorResponse = objectMapper
                .readValue(mockHttpServletResponse.getContentAsString(), new TypeReference<DefaultErrorResponse>() {
                });
        assertEquals("Entity not found", defaultErrorResponse.getMessage());

    }

    @Test
    void getRealEstateListByCadastralNumbers() throws Exception {
        int limit = 5;

        List<String> cadastralNumbers = getCadastralNumbers(limit);
        CadastralNumberDTO cadastralNumberDTO = new CadastralNumberDTO(cadastralNumbers);
        String request = objectMapper.writeValueAsString(cadastralNumberDTO);

        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(post("/api/real-estate/list/cadastral-numbers")
                        .param("limit", String.valueOf(limit))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        RealEstateResponseDTO<RealEstateDTO> response = objectMapper
                .readValue(mockHttpServletResponse.getContentAsString(), new TypeReference<RealEstateResponseDTO<RealEstateDTO>>() {
                });

        assertEquals(cadastralNumbers.size(), response.getItems().size());
        assertEquals(cadastralNumbers.get(cadastralNumbers.size() - 1), response.getItems().get(response.getItems().size() - 1).getCadastralNumber());
    }

    @Test
    void getRealEstateListByCity() throws Exception {
        int limit = 5;

        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/api/real-estate/list/city")
                        .param("limit", String.valueOf(limit))
                        .param("region", "California")
                        .param("city", "Los Angeles"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        RealEstateResponseDTO<RealEstateDTO> response = objectMapper
                .readValue(mockHttpServletResponse.getContentAsString(), new TypeReference<RealEstateResponseDTO<RealEstateDTO>>() {
                });

        assertEquals(limit, response.getItems().size());
        assertEquals(String.valueOf(limit), response.getCursor());
    }

    @Test
    void getRealEstateListByRegion() throws Exception {
        int limit = 5;

        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/api/real-estate/list/region")
                        .param("limit", String.valueOf(limit))
                        .param("region", "California"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        RealEstateResponseDTO<RealEstateDTO> response = objectMapper
                .readValue(mockHttpServletResponse.getContentAsString(), new TypeReference<RealEstateResponseDTO<RealEstateDTO>>() {
                });

        assertEquals(limit, response.getItems().size());
        assertEquals(String.valueOf(limit), response.getCursor());
    }

    @Test
    void getRealEstateById() throws Exception {
        int limit = 5;

        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(
                        get("/api/real-estate/" + realEstateEntityList.get(0).getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        RealEstateDTO response = objectMapper
                .readValue(mockHttpServletResponse.getContentAsString(), new TypeReference<RealEstateDTO>() {
                });

        assertEquals(String.valueOf(realEstateEntityList.get(0).getId()), response.getId());
    }

    @Test
    void getRealEstateQuantityByCadastralNumber() throws Exception {
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(
                        get("/api/real-estate/quantity/cadastral-number")
                                .param("cadastralNumber", realEstateEntityList.get(0).getCadastralNumber()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        QuantityRealEstateCadastralNumberDTO response = objectMapper
                .readValue(mockHttpServletResponse.getContentAsString(), new TypeReference<QuantityRealEstateCadastralNumberDTO>() {
                });

        assertEquals(1, response.getQuantity());
        assertEquals(realEstateEntityList.get(0).getCadastralNumber(), response.getCadastralNumber());


    }

    @Test
    void getRealEstateQuantityByRegion() throws Exception {
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(
                        get("/api/real-estate/quantity/region")
                                .param("region", "California")
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        QuantityRealEstateRegionDTO response = objectMapper
                .readValue(mockHttpServletResponse.getContentAsString(), new TypeReference<QuantityRealEstateRegionDTO>() {
                });

        assertEquals(realEstateEntityList.size(), response.getQuantity());
        assertEquals(realEstateEntityList.get(0).getAddress().getRegion(), response.getRegion());
    }

    @Test
    void getRealEstateQuantityByCity() throws Exception {
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(
                        get("/api/real-estate/quantity/city")
                                .param("region", "California")
                                .param("city", "Los Angeles")
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        QuantityRealEstateCityDTO response = objectMapper
                .readValue(mockHttpServletResponse.getContentAsString(), new TypeReference<QuantityRealEstateCityDTO>() {
                });

        assertEquals(realEstateEntityList.size(), response.getQuantity());
        assertEquals(realEstateEntityList.get(0).getAddress().getRegion(), response.getRegion());
        assertEquals(realEstateEntityList.get(0).getAddress().getCity(), response.getCity());
    }

    @Test
    public void getRealEstateBadRequest() throws Exception {
        int limit = 5;

        List<String> cadastralNumbers = getCadastralNumbers(limit);
        CadastralNumberDTO cadastralNumberDTO = new CadastralNumberDTO(cadastralNumbers);
        String request = objectMapper.writeValueAsString(cadastralNumberDTO);

        mockMvc.perform(get("/api/real-estate/list"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(notNullValue())));

        mockMvc.perform(get("/api/real-estate/list")
                        .param("limit", "-1"))
                .andExpect(jsonPath("message", is(notNullValue())));

        mockMvc.perform(get("/api/real-estate/list")
                        .param("limit", "someString"))
                .andExpect(jsonPath("message", is(notNullValue())));

        mockMvc.perform(post("/api/real-estate/list/cadastral-numbers")
                        .param("limit", "smth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(notNullValue())));

        mockMvc.perform(get("/api/real-estate/list/city")
                .param("limit", "smth")
                .param("region", "California")
                .param("city", "Los Angeles"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(notNullValue())));

        mockMvc.perform(get("/api/real-estate/list/city")
                        .param("limit", String.valueOf(limit))
                        .param("region", "")
                        .param("city", "Los Angeles"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(notNullValue())));

        mockMvc.perform(get("/api/real-estate/list/city")
                        .param("limit", String.valueOf(limit))
                        .param("region", "California")
                        .param("city", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(notNullValue())));

        mockMvc.perform(get("/api/real-estate/list/region")
                        .param("limit", "smth")
                        .param("region", "California")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(notNullValue())));

        mockMvc.perform(get("/api/real-estate/list/region")
                        .param("limit", String.valueOf(limit))
                        .param("region", "")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(notNullValue())));

        mockMvc.perform(get("/api/real-estate/quantity/cadastral-number")
                .param("cadastralNumber", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(notNullValue())));

        mockMvc.perform(get("/api/real-estate/quantity/region")
                        .param("region", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(notNullValue())));

        mockMvc.perform(get("/api/real-estate/quantity/city")
                        .param("city", "Los Angeles")
                        .param("region", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(notNullValue())));

        mockMvc.perform(get("/api/real-estate/quantity/city")
                        .param("city", "")
                        .param("region", "California"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(notNullValue())));
    }*/
}