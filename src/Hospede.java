import java.util.Random;

// Define a classe Hóspede que herda de Thread, permitindo que instâncias desta classe sejam executadas como threads separadas.
public class Hospede extends Thread {
    private int id;  // Identificador único para o hóspede.
    private Hotel hotel;  // Referência ao hotel onde o hóspede está hospedado.
    private Quarto quarto;  // Referência ao quarto alocado para o hóspede.

    // Construtor da classe Hóspede.
    public Hospede(int id, Hotel hotel) {
        this.id = id;
        this.hotel = hotel;
    }

    // Método run que é executado quando a thread é iniciada.
    @Override
    public void run() {
        int tentativas = 0;
        while (tentativas < 2) {  // Tenta até duas vezes alocar um quarto.
            if (hotel.alocarQuarto(this)) {  // Tenta alocar um quarto no hotel.
                System.out.println("Hóspede " + id + " alocado no quarto " + quarto.getNumero());
                
                Random rand = new Random();
                if (rand.nextInt(2) == 0) {  // 50% de chance de decidir sair para passear.
                    sairParaPassear();
                }

                // Simula a estadia do hóspede no hotel.
                try {
                    Thread.sleep((long) (Math.random() * 5000));  // Espera por um tempo aleatório (representando a duração da estadia).
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();  // Interrompe a thread se for interrompida.
                }
                hotel.liberarQuarto(this.quarto);  // Libera o quarto no hotel.
                break;
            } else {
                tentativas++;
                System.out.println("Hóspede " + id + " não conseguiu quarto na tentativa " + tentativas);
                try {
                    Thread.sleep(1000);  // Espera por um tempo antes de tentar novamente.
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
        if (tentativas >= 2) {
            System.out.println("Hóspede " + id + " deixou uma reclamação e foi embora.");  // Saída do hóspede após falhas na alocação de quarto.
        }
    }

    // Método para simular o hóspede saindo para passear e deixando a chave na recepção.
    public void sairParaPassear() {
        this.quarto.setChaveNaRecepcao(true);  // Marca que a chave foi deixada na recepção.
        hotel.getRecepcionista().receberChave(this.quarto);  // Recepcionista recebe a chave.
        hotel.registrarQuartoParaLimpeza(this.quarto);  // Registra o quarto para limpeza.
        System.out.println("Hóspede " + id + " deixou a chave do quarto " + quarto.getNumero() + " na recepção e foi passear.");
    }

    // Setter para definir o quarto do hóspede.
    public void setQuarto(Quarto quarto) {
        this.quarto = quarto;
    }
}
