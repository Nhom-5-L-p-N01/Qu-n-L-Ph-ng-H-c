import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
public class HistoryPanel extends JPanel {

    private JTextField txtSearch;
    private JTable table;
    private DefaultTableModel tableModel;
    private String currentUser = "";
    private JScrollPane scrollPane;
    public void setCurrentUser(String user){

        this.currentUser = user;

    }
    public HistoryPanel() {

        setLayout(new BorderLayout(15,15));

        setBackground(new Color(245,247,250));

        setBorder(BorderFactory.createEmptyBorder(
                20,20,20,20));

        //---------------- Tiêu đề ----------------

        JLabel lblTitle =
                new JLabel("LỊCH SỬ ĐẶT PHÒNG");

        lblTitle.setFont(new Font(
                "Segoe UI",
                Font.BOLD,
                28));

        add(lblTitle,BorderLayout.NORTH);

        //---------------- Nội dung ----------------

        JPanel center =
                new JPanel(new BorderLayout(10,10));

        center.setBackground(Color.WHITE);

        center.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(220,220,220)),
                BorderFactory.createEmptyBorder(
                        15,15,15,15)));

        //---------------- Thanh tìm kiếm ----------------

        JPanel top =
                new JPanel(new BorderLayout(10,0));

        top.setOpaque(false);

        JLabel lblSearch =
                new JLabel("Tìm kiếm:");

        lblSearch.setFont(new Font(
                "Segoe UI",
                Font.PLAIN,
                14));

        txtSearch = new JTextField();

        txtSearch.setFont(new Font(
                "Segoe UI",
                Font.PLAIN,
                14));

        top.add(lblSearch,BorderLayout.WEST);

        top.add(txtSearch,BorderLayout.CENTER);

        center.add(top,BorderLayout.NORTH);

        //---------------- Chỗ đặt bảng ----------------

        String[] columns = {
                "Tên phòng",
                "Người đặt",
                "Khung giờ",
                "Trạng thái"
        };

        tableModel = new DefaultTableModel(columns,0){

            @Override
            public boolean isCellEditable(int row,int column){
                return false;
            }

        };

        table = new JTable(tableModel);

        table.setRowHeight(28);

        table.setFont(new Font(
                "Segoe UI",
                Font.PLAIN,
                13));

        table.getTableHeader().setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13));

        scrollPane = new JScrollPane(table);

        center.add(scrollPane,BorderLayout.CENTER);

        add(center,BorderLayout.CENTER);

    }
    public void loadHistory(DefaultTableModel source,
                            boolean isAdmin){

        tableModel.setRowCount(0);

        for(int i=0;i<source.getRowCount();i++){

            String user =
                    source.getValueAt(i,1).toString();

            if(isAdmin || user.equals(currentUser)){

                tableModel.addRow(new Object[]{

                        source.getValueAt(i,0),

                        source.getValueAt(i,1),

                        source.getValueAt(i,2),

                        source.getValueAt(i,3)

                });

            }

        }

    }

}