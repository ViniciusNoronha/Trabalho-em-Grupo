package sistema_hotel;

import java.util.ArrayList;
import java.util.List;

public class Hospede extends Thread {
    private static int nextGroupId = 1; // Contador para gerar IDs de grupo únicos
    private final int id; // Identificador do hóspede
    private final Hotel hotel; // Referência ao hotel
    private final int tamanhoGrupo; // Tamanho do grupo do qual o hóspede faz parte (1 se independente)
    private int groupId; // Identificador de grupo do hóspede
    private List<Quarto> quartos; // Lista de quartos alocados para o grupo do hóspede

    // Construtor
    public Hospede(int id, Hotel hotel, int tamanhoGrupo) {
        this.id = id;
        this.hotel = hotel;
        this.tamanhoGrupo = tamanhoGrupo;
        this.quartos = new ArrayList<>();
        
        // Se este hóspede é o primeiro em um novo grupo, define um novo `groupId`
        if (tamanhoGrupo > 1 && groupId == 0) {
            synchronized (Hospede.class) {
                groupId = nextGroupId++;
            }
        }
    }

    @Override
    public void run() {
        // Tenta alocar quartos para o hóspede e seu grupo
        boolean alocacaoSucesso = hotel.alocarQuartosParaGrupo(this, tamanhoGrupo);
        if (alocacaoSucesso) {
            System.out.println("Hóspede " + id + " alocado com sucesso. Grupo: " + groupId);
            
            // Simula a estadia do hóspede
            try {
                Thread.sleep((long) (Math.random() * 5000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Hóspede libera os quartos após a estadia
            hotel.liberarQuartos(this);
        } else {
            // Se não conseguir alocar, tenta novamente ou faz uma reclamação
            System.out.println("Hóspede " + id + " não conseguiu alocação para o grupo " + groupId);
            tentativaFalhou();
        }
    }

    public long getId() {
        return id;
    }

    public int getTamanhoGrupo() {
        return tamanhoGrupo;
    }

    public int getGroupId() {
        return groupId;
    }

    public List<Quarto> getQuartos() {
        return quartos;
    }

    public void adicionarQuarto(Quarto quarto) {
        quartos.add(quarto);
    }

    public void tentativaFalhou() {
        // Implementar lógica para lidar com tentativas falhadas (por exemplo, reclamação ou desistência)
        System.out.println("Hóspede " + id + " do grupo " + groupId + " deixou uma reclamação e foi embora.");
    }

	public String listarNumerosQuartos() {
		// TODO Auto-generated method stub
		return null;
	}
}