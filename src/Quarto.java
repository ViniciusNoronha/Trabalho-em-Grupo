import java.util.ArrayList;
import java.util.List;

public class Quarto {
    private int numero;
    private boolean ocupado;
    private boolean emLimpeza;
    private boolean chaveNaRecepcao; 
    private List<Hospede> hospedes;

    public Quarto(int numero) {
        this.numero = numero;
        this.ocupado = false;
        this.emLimpeza = false;
        this.chaveNaRecepcao = false; // Inicialmente, a chave não está na recepção.
        this.hospedes = new ArrayList<>();
    }

    public synchronized boolean alocarHospedes(List<Hospede> novosHospedes) {
        if (!ocupado && !emLimpeza && novosHospedes.size() <= 4) {
            hospedes.addAll(novosHospedes);
            ocupado = true;
            return true;
        }
        return false;
    }

    public synchronized void liberarQuarto() {
        hospedes.clear();
        ocupado = false;
        emLimpeza = false;
        chaveNaRecepcao = false; // Resetar a chave para não estar na recepção quando o quarto é liberado.
        notifyAll();
    }

    public synchronized boolean estaOcupado() {
        return ocupado;
    }

    public synchronized boolean estaEmLimpeza() {
        return emLimpeza;
    }

    public synchronized void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public synchronized void setEmLimpeza(boolean emLimpeza) {
        this.emLimpeza = emLimpeza;
    }

    public int getNumero() {
        return numero;
    }

    public synchronized boolean temChaveNaRecepcao() {
        return chaveNaRecepcao;
    }

    public synchronized void setChaveNaRecepcao(boolean chaveNaRecepcao) {
        this.chaveNaRecepcao = chaveNaRecepcao;
    }
}
