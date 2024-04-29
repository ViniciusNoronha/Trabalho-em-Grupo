public class Camareira extends Thread {
    private int id;
    private Hotel hotel;

    public Camareira(int id, Hotel hotel) {
        this.id = id;
        this.hotel = hotel;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Quarto quarto = hotel.obterQuartoParaLimpeza();
                if (quarto != null) {
                    System.out.println("Camareira " + id + " limpando quarto " + quarto.getNumero());
                    Thread.sleep((long) (Math.random() * 3000));  // Simula o tempo de limpeza
                    hotel.finalizarLimpeza(quarto);
                }
                Thread.sleep(1000);  // Espera antes de verificar novamente
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
