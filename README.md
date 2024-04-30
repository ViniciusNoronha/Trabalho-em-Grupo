# Trabalho-em-Grupo: AT3/N1 - Atividade prática coletiva - Bimestre N1
<h3>Crie um projeto (em Java 17) que simule um sistema de reserva e controle de quartos em um hotel, utilizando threads em Java.</h3>
<p>O sistema deve representar as seguintes entidades, no mínimo:</p>
<ul>
  <li>Quarto
    <ul>
      <li>No mínimo, devem existir 10 quartos;</li>  
    </ul> 
  </li>
  <li>Hóspede
    <ul>
      <li>Cada hóspede deve ser representado por uma thread;</li>  
      <li>No mínimo, devem existir 10 camareiras;</li>
    </ul> 
  </li>
  <li>Recepcionista
    <ul>
      <li>Cada camareira deve ser representado por uma thread;</li>  
      <li>No mínimo, devem existir 10 camareiras;</li>
    </ul> 
  </li>
    <li>Camareira
    <ul>
      <li>Cada recepcionista deve ser representado por uma thread;</li>  
      <li>No mínimo, devem existir 5 recepcionistas;</li>
    </ul> 
  </li>
    <li>
    <ul>
      <li>Cada recepcionista deve ser representado por uma thread;</li>  
      <li>No mínimo, devem existir 5 recepcionistas;</li>
    </ul> 
  </li>
</ul>
<p>E deve se basear na seguintes regras:</p>
<ul>
  <li>O hotel deve contar com vários recepcionistas, que trabalham juntos e que <b>devem alocar hóspedes apenas em quartos vagos;</b></li>
  <li>O hotel deve contar com várias camareiras;</li>
  <li>Cada quarto possui capacidade para até 4 hóspedes e uma única chave;</li>
  <li>Caso um grupo ou uma família possua mais do que 4 membros, eles devem ser divididos em vários
    quartos;</li>
  <li>Quando os hóspedes de um quarto saem do hotel para passear, devem deixar a chave na recepção;</li>
  <li>Uma camareira só pode entrar em um quarto caso ele esteja <b>vago</b> ou os hóspedes não estejam nele, ou seja, a chave esteja na recepção;</li>
  <li>A limpeza dos quartos é feita sempre após a passagem dos hóspedes pelo quarto. Isso significa que toda vez que os hóspedes saem do quarto (para passear ou terminando sua estadia),       deve haver a entrada de uma camareira para limpeza do quarto e <b>os hóspedes só podem retornar após o fim da limpeza</b>;</li>
  <li>Um quarto vago que passa por limpeza não pode ser alocado para um hóspede novo;</li>
  <li>Caso uma pessoa chegue e não tenha quartos vagos, <b>ele deve esperar em uma fila</b> até algum quarto ficar vago. Caso a espera demore muito, ele passeia pela cidade e retorna         após um tempo para tentar alugar um quarto novamente;
  </li>
  <li>Caso a pessoa tente duas vezes alugar um quarto e não consiga, ele deixa uma reclamação e vai embora.</li>
</ul>

<h3>Observações:</h3>

<ul>
  <li>
    Não há a possibilidade de, para um mesmo quarto, somente parte dos hóspedes saírem para passear. Ou saem todos ou nenhum;
  </li>
    <li>
    <b>A implementação deve ser abrangente e simular várias situações:</b> número diferentes de hóspedes chegando, grupos com mais de 4 pessoas, todos os quartos lotados, etc.
  </li>
    <li>
    <b>Atentem-se para a descrição de cada regra!! Deve haver sincronia e coordenação entre as entidades.</b>
  </li>
</ul>
