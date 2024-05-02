package sistema_hotel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Hotel {
    private final Quarto[] quartos; // Array de quartos
    private final ExecutorService poolHospedes; // Executor para gerenciar threads de hóspedes
    private final ExecutorService poolFuncionarios; // Executor para gerenciar threads de funcionários (camareiras e recepcionistas)
    private final ReentrantLock lock; // Lock para sincronização
    private final Condition quartoDisponivel; // Condition para indicar quando um quarto está disponível
    public Hotel(int numQuartos, int numHospedes, int numCamareiras, int numRecepcionistas) {
        this.quartos = new Quarto[numQuartos];
        for (int i = 0; i < numQuartos; i++) {
            quartos[i] = new Quarto(i + 1);
        }
        this.poolHospedes = Executors.newFixedThreadPool(numHospedes);
        this.poolFuncionarios = Executors.newFixedThreadPool(numCamareiras + numRecepcionistas);
        this.lock = new ReentrantLock();
        this.quartoDisponivel = lock.newCondition();
        new LinkedList<>();

        // Inicia as threads de camareiras
        for (int i = 0; i < numCamareiras; i++) {
            poolFuncionarios.execute(new Camareira(i, this));
        }

        // Inicia as threads de recepcionistas
        for (int i = 0; i < numRecepcionistas; i++) {
            poolFuncionarios.execute(new Recepcionista(i, this));
        }

        // Inicia as threads de hóspedes
        for (int i = 0; i < numHospedes; i++) {
            int tamanhoGrupo = (int) (Math.random() * 6) + 1; // Define um tamanho aleatório para o grupo (1 a 6 pessoas)
            poolHospedes.execute(new Hospede(i, this, tamanhoGrupo));
        }
    }

 // Método para alocar quartos para um grupo de hóspedes
    public boolean alocarQuartosParaGrupo(Hospede hospede, int tamanhoGrupo) {
        lock.lock();
        try {
            int quartosNecessarios = (int) Math.ceil((double) tamanhoGrupo / 4); // Número de quartos necessários

            List<Quarto> quartosAlocados = new ArrayList<>();
            int hospedesRestantes = tamanhoGrupo;

            for (Quarto quarto : quartos) {
                // Verifica se o quarto não está ocupado, em limpeza, e não está reservado para outro grupo
                if (!quarto.estaOcupado() && !quarto.estaEmLimpeza() && (quarto.getGroupId() == 0 || quarto.getGroupId() == hospede.getGroupId())) {
                    // Calcula quantos hóspedes podem ser alocados neste quarto
                    int capacidade = Math.min(4, hospedesRestantes);
                    
                    // Aloca o quarto para o grupo de hóspedes
                    hospede.adicionarQuarto(quarto);
                    quarto.setOcupado(true);
                    quarto.setGroupId(hospede.getGroupId()); // Define o identificador de grupo do quarto
                    
                    // Reduz o número de hóspedes restantes a serem alocados
                    hospedesRestantes -= capacidade;

                    // Adiciona o quarto à lista de quartos alocados
                    quartosAlocados.add(quarto);

                    // Se todos os hóspedes foram alocados, interrompe a busca por quartos
                    if (hospedesRestantes == 0) {
                        break;
                    }
                }
            }

            // Verifica se todos os quartos necessários foram alocados
            if (quartosAlocados.size() == quartosNecessarios) {
                return true;
            } else {
                // Desfaz a alocação se não foi possível alocar todos os quartos necessários
                for (Quarto quarto : quartosAlocados) {
                    quarto.setOcupado(false);
                    quarto.setGroupId(0); // Remove o identificador de grupo do quarto
                }
                return false;
            }
        } finally {
            lock.unlock();
        }
    }




    // Lógica para liberar os quartos ocupados pelo hóspede ou grupo
    public void liberarQuartos(Hospede hospede) {
        lock.lock();
        try {
            for (Quarto quarto : hospede.getQuartos()) {
                quarto.removerHospedes(quarto.getCapacidadeMaxima());
                quarto.setOcupado(false);
                quarto.setEmLimpeza(true); // Marca o quarto como pronto para limpeza
            }
            hospede.getQuartos().clear();
            quartoDisponivel.signalAll(); // Notifica camareiras de que quartos estão prontos para limpeza
        } finally {
            lock.unlock();
        }
    }
    public void familiaSaiParaPassear(Hospede hospede) {
        lock.lock();
        try {
            // Todos os quartos alocados para o hóspede devem ser liberados para limpeza
            for (Quarto quarto : hospede.getQuartos()) {
                quarto.setOcupado(false);
                quarto.setEmLimpeza(true); // Marca o quarto como pronto para limpeza
                System.out.println("Hóspede " + hospede.getId() + " deixou a chave do quarto " + quarto.getNumero() + " na recepção.");
            }
            quartoDisponivel.signalAll(); // Notifica camareiras de que quartos estão prontos para limpeza
        } finally {
            lock.unlock();
        }
    }

    public Quarto obterQuartoParaLimpeza() {
        lock.lock();
        try {
            // Encontra um quarto disponível para limpeza
            for (Quarto quarto : quartos) {
                if (!quarto.estaOcupado() && quarto.estaEmLimpeza()) {
                    return quarto;
                }
            }
            // Se nenhum quarto estiver pronto para limpeza, aguarda uma notificação
            quartoDisponivel.await();
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            lock.unlock();
        }
    }

    public void finalizarLimpeza(Quarto quarto) {
        lock.lock();
        try {
            quarto.setEmLimpeza(false); // Finaliza a limpeza
            quartoDisponivel.signalAll(); // Notifica que um quarto está disponível
            System.out.println("Quarto " + quarto.getNumero() + " está pronto para ser ocupado novamente.");
        } finally {
            lock.unlock();
        }
    }

    public void shutdown() {
        // Encerra os pools de executores de hóspedes e funcionários
        poolHospedes.shutdown();
        poolFuncionarios.shutdown();
        try {
            if (!poolHospedes.awaitTermination(60, TimeUnit.SECONDS)) {
                poolHospedes.shutdownNow();
            }
            if (!poolFuncionarios.awaitTermination(60, TimeUnit.SECONDS)) {
                poolFuncionarios.shutdownNow();
            }
        } catch (InterruptedException e) {
            poolHospedes.shutdownNow();
            poolFuncionarios.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // Método principal para iniciar o hotel
    public static void main(String[] args) {
        // Parâmetros: número de quartos, número de hóspedes, número de camareiras, número de recepcionistas
        int numQuartos = 20;
        int numHospedes = 40;
        int numCamareiras = 5;
        int numRecepcionistas = 3;

        // Cria o hotel com os parâmetros definidos
        Hotel hotel = new Hotel(numQuartos, numHospedes, numCamareiras, numRecepcionistas);

        // Adiciona um gancho de encerramento para garantir que todos os serviços sejam encerrados corretamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Hotel está fechando. Aguarde enquanto finalizamos todos os serviços.");
            hotel.shutdown();
        }));

        System.out.println("Hotel está em pleno funcionamento!");
    }

	public Hospede atenderFilaEspera() {
		// TODO Auto-generated method stub
		return null;
	}

	public Queue<Hospede> getFilaEspera() {
		return getFilaEspera();
	}
}
