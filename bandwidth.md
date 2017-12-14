# Bandwidth

----
## Android Profiler
A avaliação de uso de largura de banda com o Android Profiler foi realizada da seguinte maneira:

1. O app foi inicializado;
2. Foi aberta e encerrada a EpisodeDetailActivity para algum episódio aleatoriamente escolhido e depois encerrada pressionando o botão BACK;
3. O passo 2 se repetiu várias vezes.

A figura abaixo mostra os resultados deste teste com o Android Profiler

![Imagem do Android Profiler - Bandwidth](https://github.com/rbs7/Podcast/blob/master/relatorios_assets/AndroidProfilerNet1.png?raw=true)

O primeiro pico corresponde ao carregamento do feed pela primeira vez que o app é aberto. Os outros seis picos correspondem à volta à MainActivity, que executa novamente o download do feed.

Isso é custoso de largura de banda, bateria e CPU, pois gasta mais tempo de rádio e exige também que o feed seja comparado com o banco de dados.

### Boas práticas adotadas

Para evitar esse desperdício de recursos, o download da lista de episódios agora só é feito se a lista de episódios no servidor tiver sido modificada. Isso é feito observando o campo Last-Modified do cabeçalho HTTP. O download do feed e a busca e armazenamento no banco de dados não são executados se não houve atualização no servidor.

![Imagem do Android Profiler - Last-Modified](https://github.com/rbs7/Podcast/blob/master/relatorios_assets/AndroidProfilerNet2.png?raw=true)

Neste novo teste com o Android Profiler, nota-se que houve um pico de uso de banda apenas na primeira inicialização do app. Das outras vezes que a MainActivity é executada, é consumida uma quantidade muito pequena de dados correspondente apenas à verificação se houve atualização no servidor.