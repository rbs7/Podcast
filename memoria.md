# Memória

----
## Android Profiler
A avaliação de uso de memória com o Android Profiler foi realizada da seguinte maneira:

1. O app foi inicializado;
2. Foi feita a rolagem do ListView rapidamente até o final e de volta até o começo;
3. O processo de rolagem foi repetido continuamente por 1,5 minuto.

A figura abaixo mostra os resultados deste teste com o Android Profiler

![Imagem do Android Profiler - Memória](https://github.com/rbs7/Podcast/blob/master/relatorios_assets/AndroidProfilerMem1.png?raw=true)

A inicialização do app possui um pico de aproximadamente 38MB de memória, mas logo depois o app passa a consumir ~27MB de memória. No teste de estresse com a rolagem contínua do ListView nota-se o aumento gradual do uso de memória a partir de um pequeno pico de ~30MB, indo gradualmente até ~45MB, e finalmente ocorre a primeira liberação de memória.

O problema observado aqui é que há a recriação dos itens do ListView durante a rolagem, e isso custa tempo de CPU e também memória.

### Boas práticas adotadas

TODO: Para evitar esse desperdício de CPU e memória, adotou-se um RecyclerView no lugar da ListView. As vantagem do RecyclerView são [documentadas aqui](https://developer.android.com/guide/topics/ui/layout/recyclerview.html).

> If the user switches the direction they were scrolling, the view holders which were scrolled off the screen can be brought right back. On the other hand, if the user keeps scrolling in the same direction, the view holders which have been off-screen the longest can be rebound to new data. The view holder does not need to be created or have its view inflated; instead, the app just updates the view's contents to match the new item it was bound to.

![Imagem do Android Profiler - RecyclerView](https://github.com/rbs7/Podcast/blob/master/relatorios_assets/AndroidProfilerMem2.png?raw=true)

O Android Profiler mostra agora o consumo de memória após o uso do RecyclerView.

----
## LeakCanary
A avaliação de desempenho com o LeakCanary foi realizada da seguinte maneira:

1. O app foi inicializado;
2. Executou-se várias funcionalidades do app, dentre elas: rolar a lista de episódios, abrir e fechar activities, rotacionar a tela, baixar um episódio etc

Nenhum memory leak foi encontrado.
