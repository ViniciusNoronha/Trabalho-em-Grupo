import java.util.HashSet;


public class Recepcionista extends Thread {
    private int id;  // Identificador único para o recepcionista.
    private Hotel hotel;  // Referência ao hotel onde o recepcionista trabalha.
    private HashSet<Integer> chavesRecebidas = new HashSet<>();  // Conjunto de números dos quartos cujas chaves foram recebidas pelo recepcionista.

    // Construtor da classe Recepcionista.
    public Recepcionista(int id, Hotel hotel) {
        this.id = id;  // Atribui o ID fornecido ao recepcionista.
        this.hotel = hotel;  // Atribui a referência do hotel ao recepcionista.
    }

    // Método para receber uma chave de quarto.
    public synchronized void receberChave(Quarto quarto) {
        chavesRecebidas.add(quarto.getNumero());  // Adiciona o número do quarto ao conjunto de chaves recebidas.
        System.out.println("Recepcionista " + id + " recebeu a chave do quarto " + quarto.getNumero());  // Imprime uma mensagem indicando o recebimento da chave.
    }

    // Método para devolver uma chave de quarto ao hóspede.
    public synchronized void devolverChave(Quarto quarto) {
        if (chavesRecebidas.contains(quarto.getNumero())) {  // Verifica se a chave do quarto está com o recepcionista.
            chavesRecebidas.remove(quarto.getNumero());  // Remove a chave do conjunto de chaves recebidas.
            System.out.println("Recepcionista " + id + " devolveu a chave do quarto " + quarto.getNumero() + " ao hóspede");  // Imprime uma mensagem indicando a devolução da chave.
        }
    }

    // Método run que é executado quando a thread é iniciada.
    @Override
    public void run() {
        try {
            while (true) {  // Loop infinito para simular o atendimento contínuo.
                hotel.atenderSolicitacoes();  // Chama método do hotel para atender solicitações.
                Thread.sleep(500);  // Simula o tempo entre atendimentos.
            }
        } catch (InterruptedException e) {
            e.printStackTrace();  // Trata interrupções na thread.
        }
    }
}
