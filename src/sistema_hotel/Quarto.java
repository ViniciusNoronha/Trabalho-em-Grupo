package sistema_hotel;

import java.util.concurrent.locks.ReentrantLock;

public class Quarto {
    private final int numero; // Número do quarto
    private boolean ocupado; // Indica se o quarto está ocupado
    private boolean emLimpeza; // Indica se o quarto está em limpeza
    private final ReentrantLock lock; // Bloqueio exclusivo para o quarto
    private int groupId; // Identificador de grupo do quarto

    public Quarto(int numero) {
        this.numero = numero;
        this.ocupado = false;
        this.emLimpeza = false;
        this.lock = new ReentrantLock();
        this.groupId = 0; // Inicialmente, o quarto não pertence a nenhum grupo
    }

    public int getNumero() {
        return numero;
    }

    public boolean estaOcupado() {
        return ocupado;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public boolean estaEmLimpeza() {
        return emLimpeza;
    }

    public void setEmLimpeza(boolean emLimpeza) {
        this.emLimpeza = emLimpeza;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    // Método para adquirir o bloqueio do quarto
    public void lockQuarto() {
        lock.lock();
    }

    // Método para liberar o bloqueio do quarto
    public void unlockQuarto() {
        lock.unlock();
    }

	public Object getCapacidadeMaxima() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removerHospedes(Object capacidadeMaxima) {
		// TODO Auto-generated method stub
		
	}
}