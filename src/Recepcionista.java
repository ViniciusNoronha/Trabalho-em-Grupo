public class Recepcionista extends Thread {
    private int id;
    private Hotel hotel;

    public Recepcionista(int id, Hotel hotel) {
        this.id = id;
        this.hotel = hotel;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Recepcionistas ficam à disposição para atender novos hóspedes ou gerenciar chaves
                hotel.atenderSolicitacoes();
                Thread.sleep(500);  // Simula o tempo entre atendimentos
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
