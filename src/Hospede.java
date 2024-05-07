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
                realizarEstadia();
                break;
            } else {
                tentativas++;
                System.out.println("Hóspede " + id + " não conseguiu quarto na tentativa " + tentativas);
                if (tentativas < 2) {
                    sairParaPassearTemporariamente();
                }
            }
        }
        if (tentativas >= 2) {
            System.out.println("Hóspede " + id + " deixou uma reclamação e foi embora.");
        }
    }

    private void realizarEstadia() {
        try {
            Thread.sleep((long) (Math.random() * 5000)); // Simula a estadia do hóspede
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        hotel.liberarQuarto(this.quarto);
    }

    private void sairParaPassearTemporariamente() {
        try {
            System.out.println("Hóspede " + id + " vai passear pela cidade enquanto espera por um quarto.");
            Thread.sleep(10000); // Simula o passeio pela cidade
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void setQuarto(Quarto quarto) {
        this.quarto = quarto;
    }
}
