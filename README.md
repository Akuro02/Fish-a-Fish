# **Fish a Fish: um jogo sério de pesca controlado por cicloergômetro para apoiar a reabilitação de membros**

Marco Antonio de Camargo

## **Resumo**

A reabilitação física é um processo gradual e cansativo, o que frequentemente leva à desmotivação e ao abandono do tratamento. Jogos sérios surgem como uma estratégia para tornar esse processo mais engajante. Este projeto apresenta o *Fish a Fish*, um jogo sério desenvolvido em Java com a biblioteca libGDX, voltado à reabilitação de membros (braços ou pernas) por meio de pedaladas constantes em um cicloergômetro. No jogo, o paciente controla um pescador que lança o anzol, aguarda o peixe morder e, então, pedala para puxá-lo até a margem. O jogo possui menu, três fases (lago pequeno, lago grande e mar) e registra o tempo gasto em cada fase e a cadência de acionamentos do controle em apertos por segundo. A entrada do jogo é a tecla espaço, que pode ser pressionada no teclado ou gerada por um ESP32 conectado via Bluetooth a um dispositivo de rotação acoplado ao cicloergômetro.

## **1\. Descrição do problema e contexto de reabilitação**

A reabilitação é um processo cansativo e gradual. Os resultados aparecem lentamente e dependem de sessões repetitivas, realizadas ao longo de semanas ou meses, nas quais o paciente executa os mesmos movimentos muitas vezes. Essas características favorecem a desmotivação, e muitos pacientes acabam abandonando o tratamento antes de alcançar os ganhos esperados.

Jogos sérios, isto é, jogos concebidos com um propósito além do entretenimento, são introduzidos como uma forma de mitigar esse problema. Ao transformar o exercício em uma atividade com objetivo, desafio e retorno imediato, eles buscam tornar a repetição mais agradável e sustentar a participação do paciente.

O jogo apresentado neste trabalho foca na reabilitação de membros (braços ou pernas) por meio de pedaladas constantes em um cicloergômetro e o movimento cíclico do equipamento é utilizado como forma de controle do jogo.

## **2\. Justificativa**

A reabilitação é um processo cansativo e gradual. Como o progresso é lento e as sessões são repetitivas, o paciente tem dificuldade de perceber sua evolução, o que reduz a motivação e aumenta o risco de abandono do tratamento. Mantê-lo engajado é, portanto, parte do problema clínico, e não apenas uma questão de conforto.

O *Fish a Fish* responde a esse cenário ao organizar práticas e tarefas curtas com um final claro (pescar o peixe), distribuídas em fases de dificuldade crescente. O jogo também registra medidas (como tempo por fase e apertos/pedaladas por segundo), que podem ser mostradas ao paciente como retorno sobre o desempenho e comparadas entre sessões por quem acompanha o tratamento.

## **3\. Descrição do jogo**

### **3.1 Visão geral**

O *Fish a Fish* é um jogo 2D desenvolvido em Java com a biblioteca libGDX, para desktop. O jogador controla um pescador posicionado em um píer. O objetivo de cada fase é pescar um peixe: o jogador lança o anzol, espera o peixe morder e pedala para puxá-lo até o píer.

### **3.2 Mecânica principal**

### **Cada fase segue um ciclo de cinco estados:**

* ### **Preparação: o** pescador aguarda com a linha pendurada. O jogador pressiona a tecla ENTER para lançar o anzol.

* ### **Lançamento:** o anzol descreve um arco até um ponto da água que depende da fase.

* ### **Espera:** a boia flutua na água por um tempo aleatório, dentro de um intervalo definido para cada fase. Nesse intervalo, os acionamentos do controle são ignorados.

* ### **Puxada:** quando o peixe morde, a tela indica "Peixe fisgado\!". Cada pressionamento da tecla espaço aproxima o peixe do píer, com uma barra de progresso. Cada fase tem uma quantidade de pressionamentos necessários para concluir. Esse valor é incremental conforme o jogador avança no jogo.

* ### **Peixe pescado:** o peixe é içado e o jogo exibe o resultado da fase. O jogador pode avançar, repetir a fase ou voltar ao menu.

A tecla espaço funciona como abstração do pedal: cada pressionamento representa um acionamento do dispositivo de rotação. O jogo pode ser jogado no teclado ou no pedal, sem diferença para a lógica do jogo.

### **3.3 Fases**

O jogo possui três fases, que podem ser escolhidas diretamente no menu ou jogadas em sequência:

* **Lago pequeno:** anzol lançado a curta distância, peixe pequeno e menor tempo de espera e quantidade de pressionamentos.  
* **Lago grande:** anzol lançado mais longe, peixe maior, espera mais longa e uma quantidade maior quantidade de pressionamentos.  
* **Mar:** maior distância, peixe maior, maior tempo de espera e pressionamentos, com cenário em tons mais azulados.

### **3.4 Registro de desempenho**

Durante a partida, o jogo mostra em tempo real o tempo decorrido na fase, o número de acionamentos e a cadência. Ao concluir cada fase, são registrados:

* **Tempo na fase:** do início da fase até a captura do peixe.  
* **Tempo de puxada:** do primeiro ao último acionamento.  
* **Total de acionamentos.**  
* **Apertos por segundo:** calculado como o número de intervalos entre acionamentos dividido pelo tempo de puxada.

Após a última fase, uma tela de resumo reúne tempo, tempo de puxada e cadência de cada fase. Esses dados são mantidos apenas durante a execução do jogo.

### **3.5 Identidade visual**

O jogo adota uma estética em pixel art, com um conjunto reduzido de imagens: o personagem do jogador (um pescador de chapéu de palha), o peixe, o cenário (céu, colinas, água e píer), o menu e o botão.

### **3.6 Integração com o ESP32 e o cicloergômetro**

A próxima etapa do projeto é a integração do jogo com um cicloergômetro por meio de um ESP32. O microcontrolador será conectado a um dispositivo de rotação acoplado ao equipamento. Ao detectar a rotação, o ESP32 envia, via Bluetooth, sinais equivalentes ao pressionamento da tecla espaço ao computador que executa o jogo.

Como o jogo já usa a tecla espaço como entrada, essa integração não exige alterações na lógica do jogo: o mesmo software pode ser jogado no teclado, para testes e demonstrações, ou no pedal, com cliques de espaço simulados pela rotação.

## **4\. Considerações finais e conclusão**

Este trabalho apresentou o *Fish a Fish*, um jogo sério que relaciona a pedalada em um cicloergômetro à tarefa de pescar, com o objetivo de tornar a reabilitação de membros mais motivadora. A versão atual é funcional com entrada de teclado e registra tempo por fase e apertos por segundo.

Em conclusão, o *Fish a Fish* reúne exercício físico e jogo em uma mesma atividade: cada pedalada tem efeito direto na tela, e o desempenho do paciente é registrado de forma objetiva. Por usar a tecla espaço como única entrada, o jogo pode ser operado tanto no teclado quanto no pedal, o que o torna uma proposta simples de apoio à motivação na reabilitação de membros por meio de pedaladas.


