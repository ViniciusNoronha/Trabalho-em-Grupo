package sistema_hotel;

public class Recepcionista extends Thread {
    private final int id; // Identificador do recepcionista
    private final Hotel hotel; // Referência ao hotel

    public Recepcionista(int id, Hotel hotel) {
        this.id = id;
        this.hotel = hotel;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Verifica se há hóspedes aguardando na fila de espera
                Hospede hospede = hotel.atenderFilaEspera();

                if (hospede != null) {
                    // Tenta alocar quartos para o grupo de hóspedes
                    int tamanhoGrupo = hospede.getTamanhoGrupo();

                    if (hotel.alocarQuartosParaGrupo(hospede, tamanhoGrupo)) {
                        System.out.println("Recepcionista " + id + " alocou o hóspede " + hospede.getId() + " e seu grupo nos quartos: " + hospede.listarNumerosQuartos());
                    } else {
                        // Se não conseguir alocar os quartos necessários, avisa o hóspede
                        System.out.println("Recepcionista " + id + " não conseguiu alocar quartos para o hóspede " + hospede.getId() + " e seu grupo.");
                        hospede.tentativaFalhou(); // Notifica o hóspede sobre a falha
                    }
                }

                // Pausa um pouco antes de verificar novamente a fila de espera
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Se a thread for interrompida, sai do loop
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
