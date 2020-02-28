# BluetoothTest
![Иллюстрация к проекту](https://github.com/esdiar-ru/BluetoothTest/raw/master/BluetoothUtil.png)
Приложение позволяет обмениваться данными с BLE устройством. Для этого создается BluetoothGattServer с одним сервисом, в котором определяется одна публичная характеристика. Общение происходит путем записи и считывания байтов из данной характеристики.
При запуске приложения необходимо принять запрос на включение Bluetooth адаптера. Затем необходимо перейти на экран поиска и подключения BLE устройств. При переходе на данный экран появляется запрос на разрешение считывания текущего местоположения (необходимо для того, чтобы разрешить поиск BLE устройств). Кнопка "Start Search" позволяет начать поиск активных Bluetooth устройств, "Stop Search" прерывает поиск. Нажатие на найденное устройство запускает процесс сопряжения. При завершении процесса соединения автоматически завершается текущее активити и на передний план переходит главный экран. Он состоит из 3 кнопок отправки данных (Command1 - отправляет 10 байт, Command2 - 30 байт, Command3 - 100 байт) и терминала, в котором отображаются все принятые и отправленные пакеты данных.
