package real_estate.response;

import lombok.experimental.UtilityClass;
import real_estate.dto.bot.AuctionInformationDTO;
import real_estate.dto.controller.AddressDTO;
import real_estate.dto.controller.RealEstateDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Утилитарный клас с методами для вывода ответа с информацией по объекту недвижимости
 * @author Shestakov Artem
 * */
@UtilityClass
public class ResponseUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"); //  dd MM yyyy HH:mm

    public String getHtml(RealEstateDTO realEstateDTO, int page, int quantity) {
        return new StringBuilder()
                .append(page).append(" из ").append(quantity).append(System.lineSeparator())
                .append(System.lineSeparator())
                .append("<b>Тип помещения:</b>").append(System.lineSeparator())
                .append(realEstateDTO.getRealEstateType()).append(System.lineSeparator())
                .append("<b>Адрес:</b>").append(System.lineSeparator())
                .append(
                        getAddressInformation(realEstateDTO.getAddress())
                ).append(System.lineSeparator())
                .append("<b>Начальная цена:</b>").append(System.lineSeparator())
                .append(realEstateDTO.getMinPrice()).append(System.lineSeparator())
                .append("<b>Кадастровый номер:</b> ").append(System.lineSeparator())
                .append(realEstateDTO.getCadastralNumber()).append(System.lineSeparator())
                .append("Ссылка на лот:").append(System.lineSeparator())
                .append(realEstateDTO.getLink())
                .toString();
    }

    public String getAuctionInformationText(AuctionInformationDTO auctionInformationDTO) {
        String auctionStartDate = getFormattedDate(auctionInformationDTO.getAuctionStartDate());
        String biddingStartTime = getFormattedDate(auctionInformationDTO.getBiddingStartTime());
        String biddingEndTime = getFormattedDate(auctionInformationDTO.getBiddingEndTime());
        return new StringBuilder()
                .append("Дата начала аукциона:").append(System.lineSeparator())
                .append(auctionStartDate).append(System.lineSeparator())
                .append("Дата начала подачи заявок: ").append(System.lineSeparator())
                .append(biddingStartTime).append(System.lineSeparator())
                .append("Дата окончания подачи заявок:").append(System.lineSeparator())
                .append(biddingEndTime).append(System.lineSeparator())
                .toString();
    }

    private String getFormattedDate(LocalDateTime inputDate) {
        return inputDate.format(FORMATTER);
    }

    private String getAddressInformation(AddressDTO addressDTO) {
        return new ResponseAddressStringBuilder().getTextAddress(addressDTO);
    }
}