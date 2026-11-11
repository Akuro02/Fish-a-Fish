# Codigo de ESP32 para o jogo Fish-a-Fish
O ESP32 utiliza a biblioteca ESP32-BLE-Keyboard (https://github.com/T-vK/ESP32-BLE-Keyboard) para criar uma conexão bluetooth com o computador e atuar como um teclado que envia apenas as teclas necessarias para o jogo como espaço (para puxar o peixe) e enter (confirma para menus). 
Graças a isso, o jogo pode ser jogado pelo hardware específico mas também pode manter o teclado como alternativa de controle, apesar do teclado não oferecer uma experiencia apropriada e, muito menos, serve de jogo sério.



### Como configurar o ESP32 com arduino CLI
- Dependencia: biblioteca ESP32-BLE-Keyboard
- em `Fish-a-Fish/hardware/01` use
    - Compilar o arquivo `.ino`: `arduino-cli compile --fqbn esp32:esp32:esp32-poe-iso --libraries ../libs .`
    - Fazer upload para o esp32: `arduino-cli upload -p {PORT} --fqbn esp32:esp32:esp32-poe-iso`
