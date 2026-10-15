import java.time.LocalDate;

public class Tarefa {
    private int id;
    private String titulo;
    private LocalDate data; // Substituiu o antigo diaSemana
    private String horario;
    private String descricao;
    private String cor;

    public Tarefa(int id, String titulo, LocalDate data, String horario, String descricao, String cor) {
        this.id = id;
        this.titulo = titulo;
        this.data = data;
        this.horario = horario;
        this.descricao = descricao;
        this.cor = cor;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public LocalDate getData() { return data; }
    public String getHorario() { return horario; }
    public String getDescricao() { return descricao; }
    public String getCor() { return cor; }
}