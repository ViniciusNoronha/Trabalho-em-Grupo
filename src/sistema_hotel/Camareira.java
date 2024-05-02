package sistema_hotel;

public class Camareira extends Thread {
    private final int id; // Identificador da camareira
    private final Hotel hotel; // Referência ao hotel

    public Camareira(int id, Hotel hotel) {
        this.id = id;
        this.hotel = hotel;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Obter um quarto para limpeza
                Quarto quarto = hotel.obterQuartoParaLimpeza();

                // Verifica se um quarto está disponível para limpeza
                if (quarto != null) {
                    // Bloqueia o quarto para garantir limpeza exclusiva
                    quarto.lockQuarto();
                    try {
                        System.out.println("Camareira " + id + " limpando quarto " + quarto.getNumero());
                        
                        // Simula o tempo de limpeza
                        Thread.sleep((long) (Math.random() * 3000));
                        
                        // Finaliza a limpeza
                        hotel.finalizarLimpeza(quarto);
                    } finally {
                        // Libera o bloqueio do quarto
                        quarto.unlockQuarto();
                    }
                }

                // Aguarda um pouco antes de verificar novamente
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
