public class Camareira extends Thread {
    private int id;
    private Hotel hotel;

    public Camareira(int id, Hotel hotel) {
        this.id = id;
        this.hotel = hotel;
    }

    public void run() {
        try {
            while (true) {
                Quarto quarto = hotel.obterQuartoParaLimpeza();
                if (quarto != null && quarto.estaEmLimpeza() && !quarto.estaOcupado() && quarto.temChaveNaRecepcao()) {
                    System.out.println("Camareira " + id + " limpando quarto " + quarto.getNumero());
                    Thread.sleep((long) (Math.random() * 3000));
                    hotel.finalizarLimpeza(quarto);
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}