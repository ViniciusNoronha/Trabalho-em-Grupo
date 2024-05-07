public class Main {
    public static void main(String[] args) {
        // Aumentando o número mínimo de recepcionistas para 5, se necessário.
        int numeroDeQuartos = 10;
        int numeroDeHospedes = 50;
        int numeroDeCamareiras = 10;
        int numeroDeRecepcionistas = Math.max(5, 5); // Garante que pelo menos 5 recepcionistas sejam criados.

        // Criação do objeto hotel com os parâmetros especificados.
        Hotel hotel = new Hotel(numeroDeQuartos, numeroDeHospedes, numeroDeCamareiras, numeroDeRecepcionistas);

        // Inicia todas as threads do hotel: hóspedes, camareiras, e recepcionistas.
        hotel.start();

        // Adicionando um gancho de encerramento para garantir que todos os serviços sejam encerrados corretamente.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Hotel está fechando. Aguarde enquanto finalizamos todos os serviços.");
            hotel.shutdown();
        }));

        System.out.println("Hotel está agora em pleno funcionamento!");
    }
}
