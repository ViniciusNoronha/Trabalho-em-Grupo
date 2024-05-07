
public class Camareira extends Thread {
    private int id;  // Identificador único para a camareira.
    private Hotel hotel;  // Referência ao hotel onde a camareira trabalha.

    // Construtor da classe Camareira.
    public Camareira(int id, Hotel hotel) {
        this.id = id;
        this.hotel = hotel;
    }

    // Método run que é executado quando a thread é iniciada.
    @Override
    public void run() {
        try {
            while (true) {  // Loop infinito para simular o trabalho contínuo da camareira.
                Quarto quarto = hotel.obterQuartoParaLimpeza();  // Obtém um quarto que precisa de limpeza.
                if (quarto != null && quarto.estaEmLimpeza() && !quarto.estaOcupado() && quarto.temChaveNaRecepcao()) {  // Verifica se o quarto pode ser limpo.
                    System.out.println("Camareira " + id + " limpando quarto " + quarto.getNumero());
                    Thread.sleep((long) (Math.random() * 3000));  // Simula o tempo de limpeza.
                    hotel.finalizarLimpeza(quarto);  // Finaliza a limpeza do quarto.
                }
                Thread.sleep(1000);  // Espera antes de procurar outro quarto para limpar.
            }
        } catch (InterruptedException e) {
            e.printStackTrace();  // Trata interrupções na thread.
        }
    }
}
