import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class Database {
    private static final String URL = "jdbc:sqlite:planner.db";

    public static void inicializarBanco() {
        try { Class.forName("org.sqlite.JDBC"); } catch (ClassNotFoundException e) { }
        
        String sql = "CREATE TABLE IF NOT EXISTS tarefas ("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "titulo TEXT NOT NULL,"
                   + "data_tarefa TEXT NOT NULL,"
                   + "horario TEXT,"
                   + "descricao TEXT,"
                   + "cor TEXT"
                   + ");";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erro ao criar banco: " + e.getMessage());
        }
    }

    public static void adicionarTarefa(String titulo, LocalDate data, String horario, String descricao, String cor) {
        String sql = "INSERT INTO tarefas(titulo, data_tarefa, horario, descricao, cor) VALUES(?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, titulo);
            pstmt.setString(2, data.toString()); // Salva automaticamente no formato AAAA-MM-DD
            pstmt.setString(3, horario);
            pstmt.setString(4, descricao);
            pstmt.setString(5, cor);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao inserir: " + e.getMessage());
        }
    }

    public static ArrayList<Tarefa> buscarTarefasPorData(LocalDate data) {
        ArrayList<Tarefa> lista = new ArrayList<>();
        String sql = "SELECT * FROM tarefas WHERE data_tarefa = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, data.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(new Tarefa(
                    rs.getInt("id"), 
                    rs.getString("titulo"), 
                    LocalDate.parse(rs.getString("data_tarefa")), // Transforma o texto do banco em Data
                    rs.getString("horario"),
                    rs.getString("descricao"),
                    rs.getString("cor")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar: " + e.getMessage());
        }
        return lista;
    }
}