import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

// Define a classe Hotel que gerencia as operações do hotel, como alocação de quartos e atendimento de hóspedes.
public class Hotel {
    private Quarto[] quartos;  // Array para armazenar os quartos do hotel.
    private ExecutorService poolHospedes;  // Pool de threads para gerenciar hóspedes.
    private ExecutorService poolFuncionarios;  // Pool de threads para gerenciar funcionários como camareiras e recepcionistas.
    private Recepcionista[] recepcionistas;  // Array para armazenar os recepcionistas.
    private Random rand = new Random();  // Objeto para gerar números aleatórios.
    private ReentrantLock lock = new ReentrantLock();  // Lock para gerenciar o acesso concorrente.
    private Condition quartoLiberado = lock.newCondition();  // Condition para sinalizar a liberação de quartos.

    // Construtor da classe Hotel.
    public Hotel(int numQuartos, int numHospedes, int numCamareiras, int numRecepcionistas) {
        this.quartos = new Quarto[numQuartos];  // Inicializa o array de quartos.
        recepcionistas = new Recepcionista[numRecepcionistas];  // Inicializa o array de recepcionistas.
        for (int i = 0; i < numQuartos; i++) {
            quartos[i] = new Quarto(i + 1);  // Cria e armazena os quartos.
        }
        poolHospedes = Executors.newFixedThreadPool(numHospedes);  // Cria um pool de threads para hóspedes.
        poolFuncionarios = Executors.newFixedThreadPool(numCamareiras + numRecepcionistas);  // Cria um pool de threads para funcionários.

        for (int i = 0; i < numHospedes; i++) {
            poolHospedes.execute(new Hospede(i, this));  // Inicializa e executa as threads dos hóspedes.
        }
        for (int i = 0; i < numCamareiras; i++) {
            poolFuncionarios.execute(new Camareira(i, this));  // Inicializa e executa as threads das camareiras.
        }
        for (int i = 0; i < numRecepcionistas; i++) {
            recepcionistas[i] = new Recepcionista(i, this);  // Inicializa e armazena os recepcionistas.
            poolFuncionarios.execute(recepcionistas[i]);  // Executa as threads dos recepcionistas.
        }
    }

    // Método para tentar alocar um quarto para um hóspede.
    public boolean alocarQuarto(Hospede hospede) {
        lock.lock();  // Obtém o lock para operações seguras de thread.
        try {
            while (true) {  // Loop para tentar alocar um quarto.
                for (Quarto quarto : quartos) {
                    if (!quarto.estaOcupado() && !quarto.estaEmLimpeza() && !quarto.temChaveNaRecepcao()) {  // Verifica a disponibilidade do quarto.
                        quarto.setOcupado(true);
                        hospede.setQuarto(quarto);
                        return true;
                    }
                }
                quartoLiberado.await();  // Espera até que um quarto seja liberado.
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();  // Libera o lock.
        }
        return false;
    }

    // Método para finalizar a limpeza de um quarto e notificar que está pronto.
    public void finalizarLimpeza(Quarto quarto) {
        lock.lock();
        try {
            quarto.setEmLimpeza(false);
            quartoLiberado.signalAll();  // Notifica que o quarto está limpo e pronto para ser ocupado novamente.
            System.out.println("Quarto " + quarto.getNumero() + " foi limpo e está pronto para ser ocupado novamente.");
        } finally {
            lock.unlock();
        }
    }

    // Método para atender solicitações gerais no hotel.
    public void atenderSolicitacoes() {
        lock.lock();
        try {
            if (rand.nextInt(10) < 1) {  // Apenas 10% de chance de haver uma solicitação cada vez que é verificado.
                System.out.println("Recepcionista está disponível para atender solicitações.");
                try {
                    Thread.sleep(500);  // Simula o tempo de atendimento.
                    System.out.println("Recepcionista finalizou uma solicitação.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    // Método para desligar todos os serviços e pools de threads do hotel.
    public void shutdown() {
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

    // Método para iniciar as operações do hotel.
    public void start() {
        System.out.println("Hotel está agora aberto e operando com todos os serviços.");
    }
}
