public class Main {
    public static void main(String[] args) {
        // Parâmetros: número de quartos, número de hóspedes, número de camareiras, número de recepcionistas
        Hotel hotel = new Hotel(10, 50, 10, 5);

        // Inicia todas as threads do hotel: hóspedes, camareiras, e recepcionistas
        hotel.start();

        // Adicionando um gancho de encerramento para garantir que todos os serviços sejam encerrados corretamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Hotel está fechando. Aguarde enquanto finalizamos todos os serviços.");
            hotel.shutdown();
        }));

        System.out.println("Hotel está agora em pleno funcionamento!");
    }
}