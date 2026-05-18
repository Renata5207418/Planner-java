import java.util.Scanner;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);

        ArrayList<Tarefa> tarefas = new ArrayList<>();
        int opcao = -1;
        while (opcao != 0) {
            exibirMenu();

            opcao = entrada.nextInt();
            entrada.nextLine(); 

            if (opcao == 1) {
                System.out.println("Digite o titulo da tarefa:");
                String titulo = entrada.nextLine();

                Tarefa novaTarefa = new Tarefa(titulo);
                tarefas.add(novaTarefa);

                System.out.println("Tarefa adicionada com sucesso!");
            } else if (opcao ==2) {
                listarTarefas(tarefas);

            } else if (opcao == 0) {
                System.out.println("Encerrando o planner...");

            } else {
                System.out.println("Opção inválida. Tente novamente.");
            }
        }
        entrada.close();
    }

    public static void exibirMenu() {
        System.out.println("=== Planner de Tarefas ===");
        System.out.println("1. Adicionar Tarefa");
        System.out.println("2. Listar Tarefas");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    public static void listarTarefas(ArrayList<Tarefa> tarefas) {
        System.out.println("\n=== Lista de Tarefas ===");
        if (tarefas.isEmpty()) {
            System.out.println("Nenhuma tarefa cadastrada.");
        } else {
            for (int i = 0; i < tarefas.size(); i++) {
                Tarefa tarefa = tarefas.get(i);

                String status = tarefa.isConcluida() ? "[x]" : "[]";
                System.out.println((i + 1) + " - " + status + " " + tarefa.getTitulo());
}
        }
    }
}