# Unworthy-Android

A dark metroidvania game built in Kotlin for Android.

## Grupo

- Bruno Moreira (29561)
- Margarida Sousa (27929)
- Vítor Guerra (27950)

## Introdução
### Descrição do Jogo
*Unworthy* é um jogo no estilo metroidvania. Presentemente, é apenas uma demonstração de um conceito de um jogo bem mais
complexo, que futuramente gostaríamos de desenvolver. Através desta *demo*, apresentamos as mecânicas e a atmosfera que
pretendemos expandir em projetos futuros.
* **Enredo**: No jogo, o protagonista deve enfrentar o estranho mundo dos seus pesadelos, que refletem alguns dos seus
  maiores traumas. Esses pesadelos não são apenas desafios a serem superados, mas também servem como um meio para explorar
  a profundidade emocional e psicológica do personagem principal.

* **Ambiente**: Os mapas do jogo foram criados utilizando o software [Tiled](https://www.mapeditor.org/), garantindo
  uma experiência visual única e um tanto quanto imersiva. A atmosfera mais sombria é usada para refletir os temas de medo
  e a superação inseridas no enredo do jogo.

* **Jogabilidade**: A jogabilidade de *Unworthy* combina elementos clássicos do género metroidvania, com exploração do
  mundo e combates desafiadores. Os jogadores precisam usar estratégia e precisão para derrotar os seus medos e avançar no jogo.

<br/>

## Conceito inicial e ideias

O jogo foi criado de raiz pelo nosso grupo, todo o enredo, *worldbuilding*, e designs foram concebidos por nós
com o intuito de criar um jogo cativante ao olho e visualmente único, que mais tarde pudéssemos dar continuidade.

O estilo visual do jogo é conseguido através de arte completamente desenhada à mão em software de desenho digital,
com todas as animações feitas a 12 frames por segundo.

Inicialmente o nosso objetivo era que o jogo tivesse 4 áreas distintas, com diferentes inimigos e *bosses*, que quando
derrotados dariam ao jogador uma chave com a qual ele conseguiria abrir uma porta e ganhar o jogo.
No decorrer do jogo o jogador também teria de explorar o mundo para adquirir habilidades especiais que o ajudariam a
ficar mais forte, ajudando-o a derrotar os inimigos mais fortes.

Ao começarmos a desenvolver o jogo percebemos que não íamos ter tempo para fazer tudo o que pretendíamos, então, tivemos
de descartar algumas ideias. Nesta versão encurtada, o mapa tem apenas duas áreas mais pequenas e apenas um tipo de
inimigo. Alguns sprites e animações para um *boss* também foram parcialmente trabalhados, mas infelizmente teve de ser
abandonado (a entrada para a sala deste *boss* ainda está presente no mundo do jogo).

### Arte Conceptual
<br/>
<p>
<picture><img alt="ConceptArt" width="400" align="left" src="gh-images/Map.png"></picture>
<picture><img alt="ConceptArt" width="400" align="left" src="gh-images/MobsArt.png"></picture>
<picture><img alt="ConceptArt" width="400" align="left" src="gh-images/Playersketch.jpg"></picture>
<picture><img alt="ConceptArt" width="400" src="gh-images/BossSketch.jpg"></picture>
</p>

<br clear="left"/>

## Como funciona    
O jogador tem 5 vidas, representadas por relógios e para conseguir desbloquear o caminho para a porta de saída no
início do jogo o jogador deverá derrotar todos os inimigos.

### Controlos

#### Menu Principal:
* tocar no ecrã para começar o jogo

#### Dentro do Jogo:
* tocar no lado esquerdo do ecrã para controlar o moviemnto do jogador
* controlos no lado direito do ecrã para saltar e atacar

<br/>

## Criaturas

### Inimigos
* **Fly-e**: Uma criatura misteriosa formada por um olho onde a sua existência serve para garantir que nada passa por
  ele, o que poderá causar problemas ao jogador.
  Diz-se que estas criaturas vivem em grupos de 2 ou 3; o que acontecerá se um grupo possuir mais do que 3?

<p align="center">
<picture><img alt="Fly-e" width="310" src="gh-images/Flye.png"></picture>
<picture><img alt="Fly-e" width="310" src="gh-images/FlyeAnimationDemo.gif"></picture>
</p>

### Player
* **Lie**: o jogador encara este mundo como o Lie, uma criança depressiva que cai misteriosamente num mundo
  desconhecido, transformado numa criatura revestida por algo misterioso. Lie, inevitavelmente explora este mundo
  procurando uma saída e vê-se forçado a enfrentar os seus maiores medos com todos os meios possíveis.

<p align="center">
<picture><img alt="Player" width="240" src="gh-images/Player.png"></picture>
<picture><img alt="Player" width="240" src="gh-images/PlayerAnimationDemo.gif"></picture>
</p>

## Estrutura do Projecto




## Implementação do Projecto




## Modelo de Dados


## Tecnologias utilizadas
* [LibGDX](https://libgdx.com): Framework de desenvolvimento de jogos multiplataforma.
* [KTX](https://libktx.github.io/): Extensão do LibGDX que facilita o desenvolvimento de jogos em Kotlin.
* [Firebase](https://firebase.google.com): Plataforma de armazenamento de dados.
* [SpriteFactory](https://github.com/craftworkgames/SpriteFactory): Ferramenta dos desenvolvedores do MonoGame.Extended
  que permite preparar spritesheets e animações para serem importadas pelo Content Pipeline do MonoGame.
* [Tiled](https://www.mapeditor.org/): Editor de mapas.
* [Krita](https://krita.org): Software de desenho digital.
* [Photoshop](https://www.adobe.com/products/photoshop.html): Software de edição de imagem.
* [Aseprite](https://www.aseprite.org): Software de criação de sprites e animações.

<br/>

## Dificuldades encontradas


<br/>

## Conclusão


<br/>

## Créditos
* Música de fundo do jogo por: [Crow Shade](https://soundcloud.com/crowshade).
