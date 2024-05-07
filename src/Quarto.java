import java.util.ArrayList;
import java.util.List;


public class Quarto {
    private int numero;  // Número identificador do quarto.
    private boolean ocupado;  // Flag para indicar se o quarto está ocupado.
    private boolean emLimpeza;  // Flag para indicar se o quarto está em limpeza.
    private boolean chaveNaRecepcao;  // Flag para indicar se a chave do quarto está na recepção.
    private List<Hospede> hospedes;  // Lista de hóspedes atualmente alojados no quarto.

    // Construtor da classe Quarto.
    public Quarto(int numero) {
        this.numero = numero;
        this.ocupado = false;
        this.emLimpeza = false;
        this.chaveNaRecepcao = false;  // Inicialmente, a chave não está na recepção.
        this.hospedes = new ArrayList<>();
    }

    // Método para alocar hóspedes no quarto, se o quarto não estiver ocupado ou em limpeza.
    public synchronized boolean alocarHospedes(List<Hospede> novosHospedes) {
        if (!ocupado && !emLimpeza && novosHospedes.size() <= 4) {  // Checa condições para alocar hóspedes.
            hospedes.addAll(novosHospedes);
            ocupado = true;
            return true;
        }
        return false;
    }

    // Método para liberar o quarto, limpando a lista de hóspedes e resetando os estados.
    public synchronized void liberarQuarto() {
        hospedes.clear();
        ocupado = false;
        emLimpeza = false;
        chaveNaRecepcao = false;  // Resetar a flag da chave na recepção.
        notifyAll();  // Notifica outras threads que podem estar esperando mudanças no estado do quarto.
    }

    // Getter para verificar se o quarto está ocupado.
    public synchronized boolean estaOcupado() {
        return ocupado;
    }

    // Getter para verificar se o quarto está em limpeza.
    public synchronized boolean estaEmLimpeza() {
        return emLimpeza;
    }

    // Setter para o estado ocupado do quarto.
    public synchronized void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    // Setter para o estado de limpeza do quarto.
    public synchronized void setEmLimpeza(boolean emLimpeza) {
        this.emLimpeza = emLimpeza;
    }

    // Getter para o número do quarto.
    public int getNumero() {
        return numero;
    }

    // Getter para verificar se a chave está na recepção.
    public synchronized boolean temChaveNaRecepcao() {
        return chaveNaRecepcao;
    }

    // Setter para definir se a chave do quarto está na recepção.
    public synchronized void setChaveNaRecepcao(boolean chaveNaRecepcao) {
        this.chaveNaRecepcao = chaveNaRecepcao;
    }
}
