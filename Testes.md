Realizamos dois testes:
  1. EpisodeDetailActivityTest;
  2. MainActivityTest;
  
O teste 1 consistiu em um Teste Unitário utilizando JUnit 4 para avaliar a o horário recebido na descrido de um vídeo (posted on...) foi corretamente transformado no horário equivalente em UTC-3. A função que faz isso foi definida dentro de EpisodeDetailActivity.class e está reproduzida abaixo:

```java 
public String convertPubDate(String date) {
    SimpleDateFormat formatter = new SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
    try {
        TimeZone timeZone = TimeZone.getTimeZone("UTC-3");
        formatter.setTimeZone(timeZone);
        Date result = formatter.parse(date);
        return result.toString();
    } catch (ParseException e) {
        e.printStackTrace();
        return  "ERROR";
    }
} 
```

Para testá-la, foi criado o seguinte teste unitário:

```java 
@Test
public void convertPubDate() throws Exception {
    String input = "Mon, 20 Nov 2017 12:30:46";
    String expected = "Mon Nov 20 09:30:46 GFT 2017";
    EpisodeDetailActivity test = new EpisodeDetailActivity();
    String output = test.convertPubDate(input);
    assertEquals(expected, output);
} 
```

O teste 2 foi um teste instrumental utilizando JUnit 4 e Espresso. Este teste simplesmente simulava o clique de um usuário no botão baixar do primeiro áudio da ListView, e em seguida verifica se o botão mudou sua mensagem para "baixando". Abaixo, o método correspondente, pertecente à classe MainActivityTest:

```java 
@Test
public void testButtonLabelChange() {
    Espresso.onData(anything())
        .inAdapterView(withId(R.id.items))
        .atPosition(0)
        .onChildView(withId(R.id.item_action))
        .perform(click());
    Espresso.onData(anything())
        .inAdapterView(withId(R.id.items))
        .atPosition(0)
        .onChildView(withId(R.id.item_action))
        .check(matches(withText("baixando")));
} 
```
