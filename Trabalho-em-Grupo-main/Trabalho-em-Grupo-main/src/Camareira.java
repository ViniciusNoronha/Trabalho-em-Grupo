public class Camareira extends Thread {
    private int id;
    private Hotel hotel;

    public Camareira(int id, Hotel hotel) {
        this.id = id;
        this.hotel = hotel;
    }

    public Quarto obterQuartoParaLimpeza() {
        this.hotel.lock.lock();
        try {
            for (Quarto quarto : this.hotel.quartos) {
                if (!quarto.estaOcupado() && !quarto.estaEmLimpeza()) { // Garanta que o quarto não esteja em limpeza
                    quarto.setEmLimpeza(true); // Marque o quarto como em limpeza
                    return quarto;
                }
            }
            return null;
        } finally {
            this.hotel.lock.unlock();
        }
    }

    public synchronized void finalizarLimpeza(Quarto quarto) {
        this.hotel.lock.lock();
        try {
            quarto.setEmLimpeza(false);
            this.hotel.quartoLiberado.signalAll(); // Notifica que o quarto está limpo e pronto para ser ocupado novamente
            System.out.println("Quarto " + quarto.getNumero() + " foi limpo e está pronto para ser ocupado novamente.");
        } finally {
            this.hotel.lock.unlock();
        }
    }

    public void run() {
        try {
            while (true) {
                Quarto quarto = obterQuartoParaLimpeza();
                if (quarto != null && quarto.estaEmLimpeza() && !quarto.estaOcupado() && quarto.temChaveNaRecepcao()) {
                    System.out.println("Camareira " + id + " limpando quarto " + quarto.getNumero());
                    Thread.sleep((long) (Math.random() * 3000));
                    finalizarLimpeza(quarto);
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
