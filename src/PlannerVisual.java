import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class PlannerVisual {
    private JFrame janela;
    private JPanel painelPrincipal;
    private JLabel lblSemanaAtual;
    private LocalDate dataFoco; // Guarda o dia que o utilizador está a olhar

    public PlannerVisual() {
        Database.inicializarBanco(); 
        dataFoco = LocalDate.now(); // Ao abrir, começa sempre no dia de hoje
        
        janela = new JFrame("Planner Pessoal - Funcional");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setSize(1250, 700);
        janela.setLayout(new BorderLayout());

        // Painel do Topo (Botões de navegação e Adicionar)
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btnAdicionar = new JButton("+ Adicionar Novo Post-it");
        btnAdicionar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnAdicionar.setBackground(new Color(70, 130, 180));
        btnAdicionar.setForeground(Color.WHITE);
        btnAdicionar.addActionListener(e -> criarNovaTarefa());
        
        JPanel painelNavegacao = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JButton btnAnterior = new JButton("< Semana Anterior");
        btnAnterior.addActionListener(e -> mudarSemana(-1));
        
        JButton btnProxima = new JButton("Próxima Semana >");
        btnProxima.addActionListener(e -> mudarSemana(1));
        
        lblSemanaAtual = new JLabel();
        lblSemanaAtual.setFont(new Font("SansSerif", Font.BOLD, 18));
        
        painelNavegacao.add(btnAnterior);
        painelNavegacao.add(lblSemanaAtual);
        painelNavegacao.add(btnProxima);

        painelTopo.add(btnAdicionar, BorderLayout.WEST);
        painelTopo.add(painelNavegacao, BorderLayout.CENTER);
        janela.add(painelTopo, BorderLayout.NORTH);

        // Painel Principal das Colunas
        painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new GridLayout(1, 7, 10, 0)); 
        painelPrincipal.setBackground(new Color(230, 235, 240)); 
        painelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));

        atualizarQuadro();

        JScrollPane scroll = new JScrollPane(painelPrincipal);
        scroll.setBorder(null);
        janela.add(scroll, BorderLayout.CENTER);
        
        janela.setLocationRelativeTo(null); 
        janela.setVisible(true);
    }

    // Navegação no tempo
    private void mudarSemana(int semanas) {
        dataFoco = dataFoco.plusWeeks(semanas);
        atualizarQuadro();
    }

    private void criarNovaTarefa() {
        JPanel painelForm = new JPanel();
        painelForm.setLayout(new BoxLayout(painelForm, BoxLayout.Y_AXIS));
        painelForm.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField fieldTitulo = new JTextField();
        JTextField fieldHorario = new JTextField();
        
        // A data já vem preenchida com o dia para o qual o utilizador está a olhar, mas ele pode alterar
        JTextField fieldData = new JTextField(dataFoco.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))); 
        
        JTextArea areaDescricao = new JTextArea(4, 25);
        areaDescricao.setLineWrap(true);
        areaDescricao.setWrapStyleWord(true);
        
        String[] opcoesCores = {"Amarelo", "Rosa", "Azul", "Verde"};
        JComboBox<String> comboCor = new JComboBox<>(opcoesCores);

        adicionarCampoComLabel(painelForm, "Título da Tarefa:", fieldTitulo);
        adicionarCampoComLabel(painelForm, "Data (DD/MM/AAAA):", fieldData);
        adicionarCampoComLabel(painelForm, "Horário (ex: 14:00):", fieldHorario);
        
        JLabel lblDesc = new JLabel("Descrição/Detalhes:");
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelForm.add(lblDesc);
        JScrollPane scrollDesc = new JScrollPane(areaDescricao);
        scrollDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelForm.add(scrollDesc);
        painelForm.add(Box.createRigidArea(new Dimension(0, 10)));
        adicionarCampoComLabel(painelForm, "Cor do Post-it:", comboCor);

        int resultado = JOptionPane.showConfirmDialog(janela, painelForm, 
                "Criar Novo Post-it", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (resultado == JOptionPane.OK_OPTION && !fieldTitulo.getText().trim().isEmpty()) {
            try {
                LocalDate dataEscolhida = LocalDate.parse(fieldData.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                Database.adicionarTarefa(
                    fieldTitulo.getText(),
                    dataEscolhida,
                    fieldHorario.getText(),
                    areaDescricao.getText(),
                    (String) comboCor.getSelectedItem()
                );
                
                // Muda a visão automaticamente para a semana da tarefa que acabou de criar
                dataFoco = dataEscolhida; 
                atualizarQuadro(); 
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(janela, "Formato de data inválido! Use DD/MM/AAAA.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void adicionarCampoComLabel(JPanel painel, String texto, JComponent campo) {
        JLabel label = new JLabel(texto);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, campo.getPreferredSize().height));
        painel.add(label);
        painel.add(campo);
        painel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void atualizarQuadro() {
        painelPrincipal.removeAll(); 
        
        LocalDate inicioDaSemana = dataFoco;
        while (inicioDaSemana.getDayOfWeek() != DayOfWeek.MONDAY) {
            inicioDaSemana = inicioDaSemana.minusDays(1);
        }
        
        DateTimeFormatter fmtMes = DateTimeFormatter.ofPattern("MMMM yyyy");
        lblSemanaAtual.setText(inicioDaSemana.format(fmtMes).toUpperCase());

        String[] nomesDias = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo"};
        DateTimeFormatter fmtDia = DateTimeFormatter.ofPattern("dd/MM");

        for (int i = 0; i < 7; i++) {
            LocalDate diaAtualLoop = inicioDaSemana.plusDays(i);
            String tituloColuna = nomesDias[i] + " (" + diaAtualLoop.format(fmtDia) + ")"; 

            JPanel colunaDia = new JPanel();
            colunaDia.setLayout(new BoxLayout(colunaDia, BoxLayout.Y_AXIS));
            
            if (diaAtualLoop.equals(LocalDate.now())) {
                colunaDia.setBackground(new Color(255, 250, 240)); 
                colunaDia.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(255, 140, 0), 2, true), tituloColuna, 
                    0, 0, new Font("SansSerif", Font.BOLD, 14)
                ));
            } else {
                colunaDia.setBackground(Color.WHITE); 
                colunaDia.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true), tituloColuna, 
                    0, 0, new Font("SansSerif", Font.BOLD, 14)
                ));
            }

            ArrayList<Tarefa> tarefasDoDia = Database.buscarTarefasPorData(diaAtualLoop);
            
            for (Tarefa t : tarefasDoDia) {
                JPanel postIt = new JPanel();
                postIt.setLayout(new BoxLayout(postIt, BoxLayout.Y_AXIS));
                postIt.setBackground(descobrirCor(t.getCor())); 
                postIt.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY, 1),
                    new EmptyBorder(8, 8, 8, 8) 
                ));
                
                // TRAVA O TAMANHO DO POST-IT AQUI
                Dimension tamanhoFixo = new Dimension(160, 90);
                postIt.setPreferredSize(tamanhoFixo);
                postIt.setMinimumSize(tamanhoFixo);
                postIt.setMaximumSize(tamanhoFixo);
                postIt.setAlignmentX(Component.CENTER_ALIGNMENT);
                
                JLabel lblTitulo = new JLabel("<html><b style='font-size:11px'>" + t.getTitulo() + "</b></html>");
                JLabel lblHorario = new JLabel("⌚ " + (t.getHorario().isEmpty() ? "--:--" : t.getHorario()));
                lblHorario.setFont(new Font("SansSerif", Font.ITALIC, 11));
                lblHorario.setForeground(Color.DARK_GRAY);
                
                JLabel lblDesc = new JLabel("<html><p style='width:120px'>" + t.getDescricao() + "</p></html>");
                lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 11));
                
                postIt.add(lblTitulo);
                postIt.add(Box.createRigidArea(new Dimension(0, 3)));
                postIt.add(lblHorario);
                postIt.add(Box.createRigidArea(new Dimension(0, 5)));
                postIt.add(lblDesc);
                
                colunaDia.add(Box.createRigidArea(new Dimension(0, 10))); // Espaço acima do post-it
                colunaDia.add(postIt);
            }
            
            // A MOLA INVISÍVEL: Empurra todos os post-its para o topo e impede que estiquem
            colunaDia.add(Box.createVerticalGlue());
            
            painelPrincipal.add(colunaDia);
        }
        painelPrincipal.revalidate();
        painelPrincipal.repaint();
    }
    
    private Color descobrirCor(String nomeCor) {
        if (nomeCor == null) return new Color(255, 255, 153); 
        switch (nomeCor) {
            case "Rosa": return new Color(255, 204, 229);
            case "Azul": return new Color(204, 229, 255);
            case "Verde": return new Color(204, 255, 204);
            case "Amarelo": 
            default: return new Color(255, 255, 153);
        }
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}

        SwingUtilities.invokeLater(() -> new PlannerVisual());
    }
}
