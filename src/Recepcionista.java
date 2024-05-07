import java.util.HashSet;

public class Recepcionista extends Thread {
    private int id;
    private Hotel hotel;
    private HashSet<Integer> chavesRecebidas = new HashSet<>();

    public Recepcionista(int id, Hotel hotel) {
        this.id = id;
        this.hotel = hotel;
    }

    public synchronized void receberChave(Quarto quarto) {
        chavesRecebidas.add(quarto.getNumero());
        System.out.println("Recepcionista " + id + " recebeu a chave do quarto " + quarto.getNumero());
    }

    public synchronized void devolverChave(Quarto quarto) {
        if (chavesRecebidas.contains(quarto.getNumero())) {
            chavesRecebidas.remove(quarto.getNumero());
            System.out.println("Recepcionista " + id + " devolveu a chave do quarto " + quarto.getNumero() + " ao hóspede");
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                hotel.atenderSolicitacoes();
                Thread.sleep(500);  // Simula o tempo entre atendimentos
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}