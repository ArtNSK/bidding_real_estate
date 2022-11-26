#bidding_real_estate

Приложение предоставляет информацию о торгах недвижимостью (torgi.gov.ru)
через telegram-бота.

Ссылка на бот: https://t.me/BiddingRealEstateBot
(работает в тестовом режиме)

Приложение состоит из:
- сервера, обработывающего запросы по поиску объектов недвижимости;
- загрузчика базы данных, для первичной загрузки;
- билблиотеки;
- telegram-бота.

#####Конфигурирование
Для работы приложения необходимо получить токен для работы бота, а также токен сервиса 
геолакации.В файле конфигурации для сервера и бота, 
необходимо задать URL-адреса для связи сервисов между собой.