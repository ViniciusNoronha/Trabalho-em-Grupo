import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class Hotel {
    private ExecutorService poolHospedes;
    private ExecutorService poolFuncionarios;
    private Recepcionista[] recepcionistas; // Adiciona um array para recepcionistas
    public Quarto[] quartos;
    public Random rand = new Random();
    public ReentrantLock lock = new ReentrantLock();
    public Condition quartoLiberado = lock.newCondition();

    public Hotel(int numQuartos, int numHospedes, int numCamareiras, int numRecepcionistas) {
        this.quartos = new Quarto[numQuartos];
        recepcionistas = new Recepcionista[numRecepcionistas]; // Inicializa o array
        for (int i = 0; i < numQuartos; i++) {
            quartos[i] = new Quarto(i + 1);
        }
        poolHospedes = Executors.newFixedThreadPool(numHospedes);
        poolFuncionarios = Executors.newFixedThreadPool(numCamareiras + numRecepcionistas);

        for (int i = 0; i < numHospedes; i++) {
            poolHospedes.execute(new Hospede(i, this));
        }
        for (int i = 0; i < numCamareiras; i++) {
            poolFuncionarios.execute(new Camareira(i, this));
        }
        for (int i = 0; i < numRecepcionistas; i++) {
            recepcionistas[i] = new Recepcionista(i, this);
            poolFuncionarios.execute(recepcionistas[i]);
        }
    }

    public Recepcionista getRecepcionista() {
        // Retorna um recepcionista aleatório
        return recepcionistas[rand.nextInt(recepcionistas.length)];
    }
    

    public boolean alocarQuarto(Hospede hospede) {
        lock.lock();
        try {
            while (true) {
                for (Quarto quarto : quartos) {
                    if (!quarto.estaOcupado() && !quarto.estaEmLimpeza()) {
                        quarto.setOcupado(true);
                        hospede.setQuarto(quarto);
                        return true;
                    }
                }
                if (!quartoLiberado.await(5000, TimeUnit.MILLISECONDS)) {  // Espera por até 5 segundos
                    return false;  // Retorna falso se nenhum quarto ficar disponível em 5 segundos
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            lock.unlock();
        }
    }

    

    public synchronized void liberarQuarto(Quarto quarto) {
        lock.lock();
        try {
            quarto.setOcupado(false);
            quarto.setEmLimpeza(false); // Assumindo que a limpeza foi concluída
            quartoLiberado.signalAll(); // Notifica todos os hóspedes que um quarto está disponível
        } finally {
            lock.unlock();
        }
    }


    
    
    public synchronized void registrarQuartoParaLimpeza(Quarto quarto) {
        lock.lock();
        try {
            if (!quarto.estaOcupado() && quarto.temChaveNaRecepcao()) {
                quarto.setEmLimpeza(true);
                quartoLiberado.signalAll();
            }
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
    
    
    public void start() {
        // Todos os serviços já foram inicializados no construtor, então este método pode ser usado
        // para iniciar quaisquer outras preparações necessárias ou simplesmente para sinalizar o início das operações.
        System.out.println("Hotel está agora aberto e operando com todos os serviços.");
    }
}
