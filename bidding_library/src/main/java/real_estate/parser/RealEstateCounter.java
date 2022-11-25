package real_estate.parser;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Класс счетчика, используется для указания id при сохранении в документ Excel
 * @author Shestakov Artem
 * */
public class RealEstateCounter {
    private RealEstateCounter() {
    }

    private static volatile RealEstateCounter instance;
    public AtomicInteger realEstateId = new AtomicInteger();


    public static RealEstateCounter getInstance() {
        RealEstateCounter localInstance = instance;
        if (localInstance == null) {
            synchronized (RealEstateCounter.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new RealEstateCounter();
                }
            }
        }
        return localInstance;
    }
}
