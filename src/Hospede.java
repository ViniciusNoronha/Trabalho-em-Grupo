import java.util.Random;

public class Hospede extends Thread {
    private int id;
    private Hotel hotel;
    private Quarto quarto;

    public Hospede(int id, Hotel hotel) {
        this.id = id;
        this.hotel = hotel;
    }

    @Override
    public void run() {
        int tentativas = 0;
        while (tentativas < 2) {
            if (hotel.alocarQuarto(this)) {
                System.out.println("Hóspede " + id + " alocado no quarto " + quarto.getNumero());
                
                Random rand = new Random();
                if (rand.nextInt(2) == 0) {  // 50% chance de sair para passear
                    sairParaPassear();
                }

                // Simula a estadia do hóspede
                try {
                    Thread.sleep((long) (Math.random() * 5000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                hotel.liberarQuarto(this.quarto);
                break;
            } else {
                tentativas++;
                System.out.println("Hóspede " + id + " não conseguiu quarto na tentativa " + tentativas);
                try {
                    Thread.sleep(1000); // Simula o passeio pela cidade
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
        if (tentativas >= 2) {
            System.out.println("Hóspede " + id + " deixou uma reclamação e foi embora.");
        }
    }

    public void sairParaPassear() {
        this.quarto.setChaveNaRecepcao(true); // Define que a chave foi deixada na recepção.
        hotel.getRecepcionista().receberChave(this.quarto);
        hotel.registrarQuartoParaLimpeza(this.quarto);
        System.out.println("Hóspede " + id + " deixou a chave do quarto " + quarto.getNumero() + " na recepção e foi passear.");
    }

    public void setQuarto(Quarto quarto) {
        this.quarto = quarto;
    }
}