import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class Hotel {
    private Quarto[] quartos;
    private ExecutorService poolHospedes;
    private ExecutorService poolFuncionarios;
    private ReentrantLock lock = new ReentrantLock();
    private Condition quartoLiberado = lock.newCondition();

    public Hotel(int numQuartos, int numHospedes, int numCamareiras, int numRecepcionistas) {
        this.quartos = new Quarto[numQuartos];
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
            poolFuncionarios.execute(new Recepcionista(i, this));
        }
    }

    public boolean alocarQuarto(Hospede hospede) {
        lock.lock();
        try {
            while (true) {
                for (Quarto quarto : quartos) {
                    if (!quarto.estaOcupado() && !quarto.estaEmLimpeza()) {
                        quarto.setOcupado(true);
                        hospede.setQuarto(quarto); // Aloca o quarto ao hóspede
                        return true;
                    }
                }
                quartoLiberado.await(); // Espera até que um quarto seja liberado
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

    public Quarto obterQuartoParaLimpeza() {
        lock.lock();
        try {
            for (Quarto quarto : quartos) {
                if (!quarto.estaOcupado() && !quarto.estaEmLimpeza()) { // Garanta que o quarto não esteja em limpeza
                    quarto.setEmLimpeza(true); // Marque o quarto como em limpeza
                    return quarto;
                }
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    public void finalizarLimpeza(Quarto quarto) {
        lock.lock();
        try {
            // Aqui poderíamos implementar uma lógica para marcar o quarto como limpo.
            // No entanto, dependendo da implementação da classe Quarto, isso pode já ser manipulado por quarto.liberarQuarto().
            // Assumindo que o quarto precisa ser explicitamente marcado como limpo:
            if (!quarto.estaOcupado()) {
                System.out.println("Quarto " + quarto.getNumero() + " foi limpo e está pronto para ser ocupado novamente.");
            }
        } finally {
            lock.unlock();
        }
    }

    public void atenderSolicitacoes() {
        // Implementar a lógica de atendimento a hóspedes por parte dos recepcionistas.
        // Esta é uma função de placeholder, mostrando um exemplo simples.
        lock.lock();
        try {
            System.out.println("Recepcionista está disponível para atender solicitações.");
            // Simula atendimento a uma solicitação.
            try {
                Thread.sleep(500); // Simula o tempo de atendimento
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Recepcionista finalizou uma solicitação.");
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
