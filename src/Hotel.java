import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class Hotel {
    private Quarto[] quartos;
    private ExecutorService poolHospedes;
    private ExecutorService poolFuncionarios;
    private Recepcionista[] recepcionistas; 
    private Random rand = new Random();
    private ReentrantLock lock = new ReentrantLock();
    private Condition quartoLiberado = lock.newCondition();

    public Hotel(int numQuartos, int numHospedes, int numCamareiras, int numRecepcionistas) {
        this.quartos = new Quarto[numQuartos];
        recepcionistas = new Recepcionista[numRecepcionistas]; 
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
            quarto.setEmLimpeza(false); 
            quartoLiberado.signalAll(); // Notifica todos os hóspedes que um quarto está disponível
        } finally {
            lock.unlock();
        }
    }

    public Quarto obterQuartoParaLimpeza() {
        lock.lock();
        try {
            for (Quarto quarto : quartos) {
                if (!quarto.estaOcupado() && !quarto.estaEmLimpeza()) { 
                    quarto.setEmLimpeza(true); 
                    return quarto;
                }
            }
            return null;
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

    public synchronized void finalizarLimpeza(Quarto quarto) {
        lock.lock();
        try {
            quarto.setEmLimpeza(false);
            quartoLiberado.signalAll(); // Notifica que o quarto está limpo e pronto para ser ocupado novamente
            System.out.println("Quarto " + quarto.getNumero() + " foi limpo e está pronto para ser ocupado novamente.");
        } finally {
            lock.unlock();
        }
    }
    
   

    public void atenderSolicitacoes() {
        lock.lock();
        try {
            // Simula que o recepcionista verifica se há solicitações pendentes
            if (rand.nextInt(10) < 2) {  // Apenas 20% de chance de haver uma solicitação cada vez que é verificado
                System.out.println("Recepcionista está disponível para atender solicitações.");
                // Simula atendimento a uma solicitação.
                try {
                    Thread.sleep(500); // Simula o tempo de atendimento
                    System.out.println("Recepcionista finalizou uma solicitação.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } finally {
            lock.unlock();
        }
    }

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
    
    
    public void start() {
    
        System.out.println("Hotel está agora aberto e operando com todos os serviços.");
    }
}
