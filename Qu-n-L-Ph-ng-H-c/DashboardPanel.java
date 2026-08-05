import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
public class DashboardPanel extends JPanel {

    private JPanel cardPanel;
    private JPanel recentPanel;
    private JPanel noticePanel;
    private JLabel lblTotalRoom;
    private JLabel lblTotalBooking;
    private JLabel lblPending;
    private JLabel lblApproved;

    private JTable tableRecent;
    private DefaultTableModel recentModel;

    public DashboardPanel() {

        setLayout(new BorderLayout(15,15));

        setBackground(new Color(240,244,249));

        setBorder(BorderFactory.createEmptyBorder(
                20,20,20,20));

        //------------------ Tiêu đề ------------------

        JLabel title = new JLabel("Dashboard");

        title.setFont(new Font(
                "Segoe UI",
                Font.BOLD,
                30));

        add(title,BorderLayout.NORTH);

        //------------------ Nội dung ------------------

        JPanel center = new JPanel();

        center.setOpaque(false);

        center.setLayout(new BoxLayout(
                center,
                BoxLayout.Y_AXIS));

        //------------------ Card ------------------

        cardPanel = new JPanel(
                new GridLayout(1,4,15,15));

        cardPanel.add(createCard(
                "Tổng phòng",
                "0",
                new Color(52,152,219)));

        cardPanel.add(createCard(
                "Tổng lượt đặt",
                "0",
                new Color(46,204,113)));

        cardPanel.add(createCard(
                "Chờ duyệt",
                "0",
                new Color(241,196,15)));

        cardPanel.add(createCard(
                "Đã duyệt",
                "0",
                new Color(155,89,182)));

        cardPanel.setOpaque(false);

        center.add(cardPanel);

        center.add(Box.createVerticalStrut(20));

        //------------------ Đặt phòng gần đây ------------------

        recentPanel = new JPanel(
                new BorderLayout());
        recentModel = new DefaultTableModel(
                new String[]{
                        "Phòng",
                        "Người đặt",
                        "Thời gian",
                        "Trạng thái"
                },0);

        tableRecent = new JTable(recentModel);

        tableRecent.setRowHeight(28);

        tableRecent.setFont(
                new Font("Segoe UI",
                        Font.PLAIN,
                        13));

        JScrollPane sp =
                new JScrollPane(tableRecent);

        recentPanel.add(sp);


        recentPanel.setPreferredSize(
                new Dimension(100,260));

        recentPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Đặt phòng gần đây"));

        center.add(recentPanel);

        center.add(Box.createVerticalStrut(20));

        //------------------ Thông báo ------------------

        noticePanel = new JPanel(
                new BorderLayout());

        noticePanel.setPreferredSize(
                new Dimension(100,100));

        noticePanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Thông báo"));

        center.add(noticePanel);

        add(center,BorderLayout.CENTER);

    }
    private JPanel createCard(
            String title,
            String value,
            Color color){

        JPanel card = new JPanel();

        card.setLayout(new BorderLayout());

        card.setPreferredSize(
                new Dimension(170,110));

        card.setBackground(Color.WHITE);

        card.setBorder(BorderFactory.createLineBorder(
                new Color(220,220,220)));

        JLabel lblTitle = new JLabel(title);

        lblTitle.setHorizontalAlignment(
                SwingConstants.CENTER);

        lblTitle.setFont(new Font(
                "Segoe UI",
                Font.BOLD,
                16));

        lblTitle.setForeground(Color.DARK_GRAY);

        JLabel lblValue = new JLabel(value);

        lblValue.setHorizontalAlignment(
                SwingConstants.CENTER);

        lblValue.setFont(new Font(
                "Segoe UI",
                Font.BOLD,
                34));

        lblValue.setForeground(color);

        switch (title){

            case "Tổng phòng":
                lblTotalRoom = lblValue;
                break;

            case "Tổng lượt đặt":
                lblTotalBooking = lblValue;
                break;

            case "Chờ duyệt":
                lblPending = lblValue;
                break;

            case "Đã duyệt":
                lblApproved = lblValue;
                break;
        }

        card.add(lblTitle,BorderLayout.NORTH);

        card.add(lblValue,BorderLayout.CENTER);

        return card;

    }
    public void updateDashboard(int totalRoom,
                                int totalBooking,
                                int pending,
                                int approved){

        lblTotalRoom.setText(String.valueOf(totalRoom));

        lblTotalBooking.setText(String.valueOf(totalBooking));

        lblPending.setText(String.valueOf(pending));

        lblApproved.setText(String.valueOf(approved));

    }
    public void updateRecentBooking(
            DefaultTableModel model){

        recentModel.setRowCount(0);

        int start =
                Math.max(0,
                        model.getRowCount()-5);

        for(int i=start;
            i<model.getRowCount();
            i++){

            recentModel.addRow(new Object[]{

                    model.getValueAt(i,0),

                    model.getValueAt(i,1),

                    model.getValueAt(i,2),

                    model.getValueAt(i,3)

            });

        }

    }

}