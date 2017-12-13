# Desempenho

----
## Android Profiler
A avaliação de desempenho com o Android Profiler foi realizada da seguinte maneira:

1. O app foi inicializado;
2. Foi feita a rolagem do ListView rapidamente até o final e de volta até o começo;
3. Cinco episódios de poscast foram escolhidos para ter seu detalhamento exibido. Para isso, iniciou-se e encerrou-se a EpisodeDetailActivity para cinco episódios escolhidos aleatoriamente.

A figura abaixo mostra os resultados deste teste com o Android Profiler

![Imagem do Android Profiler](https://github.com/rbs7/Podcast/blob/master/relatorios_assets/AndroidProfilerCPU1.png?raw=true)

Estes três eventos são os que mais consomem tempo de CPU no app. Porém, eles não excederam 30% de uso da CPU. É interessante notar que o uso de CPU é muito alto ao voltar à MainActivity. Isso ocorre porque, da forma que o app está, é sempre feito novamente o download dos episódios e a verificação de cada episódio no BD.

### Boas práticas adotadas

TODO: Para evitar esse desperdício de CPU, o download da lista de episódios agora só é feito se a lista de episódios no servidor tiver sido modificada. Isso é feito observando o campo Last-Modified do cabeçalho HTTP.

![Imagem do Android Profiler - Last-Modified](https://github.com/rbs7/Podcast/blob/master/relatorios_assets/AndroidProfilerCPU2.png?raw=true)

Isso também se reflete em economia de banda. Observar o arquivo bandwidth.md para mais detalhes

----
## AndroidDevMetrics
A avaliação de desempenho com o AndroidDevMetrics foi realizada da seguinte maneira:

1. O app foi inicializado;
2. Dois episódios de poscast foram escolhidos para ter seu detalhamento exibido. Para isso, iniciou-se e encerrou-se a EpisodeDetailActivity para dois episódios escolhidos aleatoriamente;
3. Iniciou-se a SettingsActivity.

A figura abaixo mostra os resultados deste teste com o AndroidDevMetrics

![Imagem do AndroidDevMetrics - MainActivity](https://github.com/rbs7/Podcast/blob/master/relatorios_assets/AndroidDevMetrics1.png?raw=true)
![Imagem do AndroidDevMetrics - EpisodeDetailActivity](https://github.com/rbs7/Podcast/blob/master/relatorios_assets/AndroidDevMetrics2.png?raw=true)
![Imagem do AndroidDevMetrics - SettingsActivity](https://github.com/rbs7/Podcast/blob/master/relatorios_assets/AndroidDevMetrics3.png?raw=true)

O AndroidDevMetrics apresentou tempo negativo no primeiro caso. Há uma [issue aberta](https://github.com/frogermcs/AndroidDevMetrics/issues/36) no repositório oficial sobre esse problema, porém não há respostas.
