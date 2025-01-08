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

<br/>

## Estrutura do Projeto
Para desenvolver o nosso jogo decidimos usar o libGDX, uma framework para desenvolvimento de jogos para diversas plataformas.
Ao configuramos o projeto libGDX uma pasta core é criada, nesta encontra-se  o código comum a todas as plataformas a também maior 
parte da lógica do jogo, para além da pasta core é também criada uma pasta para cada uma das outras plataformas, neste caso só nos interessa, o Android. 
Na pasta Android, é onde estão localizadas classes que utilizamos para a gestão de dados e inicialização do jogo em android estas classes são responsáveis pelo bom funcionamento do na plataforma Android. 
Dentro da pasta Core criamos seis subpastas, core, input, models, objects, e utils, para além destas temos a classe UnworthyApp, 
o objeto constantes e um repositório com a PlayerData que não pertencem a nenhum pasta em específico. 
Escolhemos este método de organização para facilitar a navegação do projeto, tornando-a mais intuitiva. 

* **core** : Nesta pasta core temos classes que gerem as cenas, animações, objetos do jogo e a renderização do mesmo, para além destas classes temos também interfaces que contêm funções responsãveis pelo desenho de sprites, colisão e pela lógica do loop do jogo. 
* **input** : Dentro da pasta imput temos classes que gerem o toque do jogador no ecrã e criam os controles do jogo.
* **models** : Esta pasta contém o nosso modelo de dados.
* **objects** : A pasta objects guarda os gameobjects como o background, plataformas, chão, assets da UI e as entidades.
* **scenes** : Nesta pasta temos as classes Level e MainMenu que lidam com os diferentes estados do jogo e definem o que acontece durante estes.
* **utils** : Esta pasta contém funções e extensões para várias classes que auxiliam diversas operações ao longo do projeto.
Fora das subpastas temos ainda o objecto Constants que armazena toda a informação fixa do jogo, a interface PlayerDataRepository
que gerencia o progresso do jogador e a classe UnworthyApp que é a classe principal do jogo, responsável por inicializa-lo e conectar todas as suas partes.

<br/>

## Implementação do Projecto
O jogo Unworthy foi originalmente desenvolvido em Monogame, portanto quando decidimos criar uma versão Android do mesmo tivemos que recorrer à framework libGTX que já foi referida na estrutura do projeto. Desta forma conseguimos ter um jogo de boa qualidade e bem mais organizado, assim como o que pertendiamos inicialmente.
## Implementação do Projeto

No desenvolvimento para o projeto foi necessário implementar várias classes para permitir o funcionamento do jogo.
A classe **UnworthyApp** é uma subclasse de *KtxGame*, que é a classe principal do jogo, responsável 
por inicializar o jogo e conectar todas as suas partes, trazendo o clico de vida como o game loop.
Esta gere os "screens", que tipicamente representam diferentes níveis de um jogo mas no nosso caso 
implementámos uma classe chamada **Scene**, subclasse de KtxScreen,  à qual adicionamos outras funcionalidades, 
como a capacidade de detetar o input do jogador implementando a interface *KtxInputAdapter*.
**Scene** é também subclasse de **GameObjectList**, outra classe que implementámos para gerir
os vários Game Objects de uma cena, delegando-lhes eventos do ciclo do jogo, estes sendo o *update* e o *draw*.
**GameObjectList** é uma subclasse de **GameObject**, que é a classe onde controlamos os objetos do jogo, sendo
remover, adicionar, atualizar e desenhar os objetos. Assim chegando a classe base do nosso projeto, o
**GameObject**. Nesta classe implementamos as interfaces usadas no nosso projeto, **IGameLoop** e **Disposable**.



<br/>

## Modelo de Dados
Em relação a modelo de dados decidimos criar um ficheiro **AndroidPlayerData** onde recolhemos informação sobre
o id do utilizador, como a informação do jogador fornecida pelo ficheiro **PlayerData**, onde recolhemos o tempo de jogo,
a quantidade de inimigos derrotados, como a quantidade de vezes que o jogador morreu. 
Desta informação recolhida, manda-mos esta informação para a nossa base de dados que criamos utilizando a Firebase, 
de forma quando o jogador abre o jogo, acede a informação recolhida, a partir do id recolhido, e consegue
desfrutar do jogo sem perda de informação de sessão por sessão.

<br/>

## Tecnologias utilizadas
### Ferramentas de Desenvolvimento
* [LibGDX](https://libgdx.com): Framework de desenvolvimento de jogos multiplataforma.
* [KTX](https://libktx.github.io/): Extensão do LibGDX que facilita o desenvolvimento de jogos em Kotlin.
* [Firebase](https://firebase.google.com): Plataforma de armazenamento de dados.

### Software 
* [SpriteFactory](https://github.com/craftworkgames/SpriteFactory): Ferramenta dos desenvolvedores do MonoGame.Extended
  que permite preparar spritesheets e animações para serem importadas pelo Content Pipeline do MonoGame.
* [Tiled](https://www.mapeditor.org/): Editor de mapas.
* [Krita](https://krita.org): Software de desenho digital.
* [Photoshop](https://www.adobe.com/products/photoshop.html): Software de edição de imagem.
* [Aseprite](https://www.aseprite.org): Software de criação de sprites e animações.

<br/>

## Dificuldades encontradas
Ao longo do desenvolvimento do projeto, *Unworthy*, deparamo-nos com algumas dificuldades, mas a que destacou-se mais
foi a utilização do LibGDX e os vários sistemas de coordenadas que utiliza. libGDX, ao contrário do canvas do Android,
que usa o sistema *y-down*, libGDX usa ambos sistemas de coordenadas, *y-down* e *y-up*, dependendo da situação.
Em relação ao toque no ecrã, onde se deteta onde o utilizador tocou, usa um sistema y-down, onde o centro de ecrã, (0,0),
encontrasse no canto superior esquerdo do ecrã, enquanto em relação ao carregar sprites e imagens no ecrã,
usa um sistema y-up, onde o centro do ecrã, encontra-se no canto inferior esquerdo do ecrã. 
Desta forma tornou a implementação de certas funcionalidades necessárias para a funcionalidade da aplicação
mais complicadas devido a conversões das coordenadas necessárias. 

<br/>

## Conclusão
Devido ao tempo limitado e hás dificuldades anteriormente referidas, não fomos capazes de implementar certas mecânicas e contéudo como pretendiamos inicialmente. Mesmo que não tenhamos conseguido implementar tudo o que querias, ainda fomos capazes de implementar novas funcionalidades ao jogo que tornaram a jogabilidade mais aplativa, por exemplo o sistema de notificações, a contagem das mortes e enimigos derrotados e a quantidade de tempo jogado, que são guardadas no id do utilizador na base de dados. Apesar das dificuldades, ficámos muito felizes com o resultado e estamos bastante motivados para continuar com o desenvolvimento deste jogo para que futuramente possamos publicá-lo em diversas plataformas.


<br/>

## Créditos
* Música de fundo do jogo por: [Crow Shade](https://soundcloud.com/crowshade).
