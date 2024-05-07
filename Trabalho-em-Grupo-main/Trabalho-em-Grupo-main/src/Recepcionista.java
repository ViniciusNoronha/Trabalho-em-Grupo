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

    public void atenderSolicitacoes() {
        
        this.hotel.lock.lock();
        try {
            // Simula que o recepcionista verifica se há solicitações pendentes
            if (this.hotel.rand.nextInt(10) < 2) {  // Apenas 20% de chance de haver uma solicitação cada vez que é verificado
                System.out.println("Recepcionista está disponível para atender solicitações.");
                // Simula atendimento a uma solicitação.
                try {
                    Thread.sleep(500); // Simula o tempo de atendimento
                    System.out.println("Recepcionista finalizou uma solicitação.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } finally {
            this.hotel.lock.unlock();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Lógica existente
                atenderSolicitacoes();
                Thread.sleep(500);  // Simula o tempo entre atendimentos
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
