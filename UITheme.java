import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public final class UITheme {

    private UITheme() {
    }

    public static final Color BG_DARK      = new Color(13, 18, 32);
    public static final Color PRIMARY      = new Color(56, 189, 248);
    public static final Color PRIMARY_DARK = new Color(14, 116, 144);
    public static final Color ACCENT       = new Color(99, 102, 241);
    public static final Color SUCCESS      = new Color(52, 211, 153);
    public static final Color WARNING      = new Color(251, 191, 36);
    public static final Color DANGER       = new Color(248, 113, 113);
    public static final Color INFO         = new Color(129, 140, 248);
    public static final Color NEUTRAL      = new Color(148, 163, 184);
    public static final Color WHITE        = new Color(248, 250, 252);
    public static final Color TEXT_MUTED   = new Color(203, 213, 225);

    public static final Color GLASS_PANEL    = new Color(15, 23, 42, 205);
    public static final Color GLASS_PANEL_LT = new Color(30, 41, 59, 195);
    public static final Color TABLE_ROW_1    = new Color(15, 23, 42, 180);
    public static final Color TABLE_ROW_2    = new Color(30, 41, 59, 180);

    private static final String FONT_FAMILY = "Segoe UI";

    public static Font font(int style, int size) {
        return new Font(FONT_FAMILY, style, size);
    }

    public static final Font FONT_TITLE         = font(Font.BOLD, 24);
    public static final Font FONT_SUBTITLE      = font(Font.PLAIN, 13);
    public static final Font FONT_LABEL         = font(Font.PLAIN, 14);
    public static final Font FONT_BUTTON        = font(Font.BOLD, 13);
    public static final Font FONT_TABLE         = font(Font.PLAIN, 13);
    public static final Font FONT_TABLE_HEADER  = font(Font.BOLD, 13);
    public static final Font FONT_STAT_NUMBER   = font(Font.BOLD, 21);
    public static final Font FONT_STAT_CAPTION  = font(Font.PLAIN, 12);
    public static final Font FONT_LINK          = font(Font.PLAIN, 12);

    private static Image cachedBackground;
    private static boolean triedLoadBackground = false;

    public static synchronized Image getBackgroundImage() {
        if (!triedLoadBackground) {
            triedLoadBackground = true;
            cachedBackground = loadBackground();
        }
        return cachedBackground;
    }

    private static Image loadBackground() {
        try {
            File direct = new File("background.jpg");
            if (direct.exists()) {
                return ImageIO.read(direct);
            }
        } catch (IOException ignored) {
        }
        try {
            URL resource = UITheme.class.getClassLoader().getResource("background.jpg");
            if (resource != null) {
                return ImageIO.read(resource);
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    public static class BackgroundPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        public BackgroundPanel(LayoutManager layout) {
            super(layout);
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Image bg = getBackgroundImage();
            if (bg != null) {
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                g.setPaint(new GradientPaint(
                        0, 0, new Color(6, 10, 22, 195),
                        0, getHeight(), new Color(6, 10, 22, 150)));
                g.fillRect(0, 0, getWidth(), getHeight());
            } else {
                g.setPaint(new GradientPaint(
                        0, 0, new Color(15, 23, 42),
                        getWidth(), getHeight(), new Color(30, 27, 75)));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            g.dispose();
        }
    }

    public static class RoundedPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final int radius;
        private final Color background;
        private final Color border;

        public RoundedPanel(LayoutManager layout, int radius, Color background, Color border) {
            super(layout);
            this.radius = radius;
            this.background = background;
            this.border = border;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(background);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            if (border != null) {
                g.setColor(border);
                g.setStroke(new BasicStroke(1.4f));
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            }
            g.dispose();
            super.paintComponent(g0);
        }
    }

    public static class RoundedButton extends JButton {
        private static final long serialVersionUID = 1L;
        private final Color base;
        private final Color hover;
        private final Color pressed;
        private final int radius;

        public RoundedButton(String text, Color base) {
            this(text, base, null);
        }

        public RoundedButton(String text, Color base, Icon icon) {
            super(text, icon);
            this.base = base;
            this.hover = adjustBrightness(base, 0.16f);
            this.pressed = adjustBrightness(base, -0.14f);
            this.radius = 14;
            setFont(FONT_BUTTON);
            setForeground(WHITE);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setIconTextGap(8);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        }

        private static Color adjustBrightness(Color c, float amount) {
            float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
            hsb[2] = Math.max(0f, Math.min(1f, hsb[2] + amount));
            return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fill = getModel().isPressed() ? pressed : (getModel().isRollover() ? hover : base);
            g.setColor(fill);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g.dispose();
            super.paintComponent(g0);
        }

        @Override
        public boolean contains(int x, int y) {
            return new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius).contains(x, y);
        }
    }

    public static class RoundedLineBorder extends AbstractBorder {
        private static final long serialVersionUID = 1L;
        private final Color color;
        private final int radius;
        private final float thickness;

        public RoundedLineBorder(Color color, int radius, float thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g0, int x, int y, int w, int h) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(color);
            g.setStroke(new BasicStroke(thickness));
            g.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 12, 8, 12);
        }
    }

    public static JTextField roundedTextField() {
        JTextField field = new JTextField();
        styleField(field);
        return field;
    }

    public static JPasswordField roundedPasswordField() {
        JPasswordField field = new JPasswordField();
        styleField(field);
        return field;
    }

    private static void styleField(JTextField field) {
        field.setFont(FONT_LABEL);
        field.setForeground(new Color(15, 23, 42));
        field.setBackground(Color.WHITE);
        field.setOpaque(true);
        field.setCaretColor(PRIMARY_DARK);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY, 2, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 36));
    }

    public static void styleCombo(JComboBox<String> combo) {
        combo.setFont(FONT_LABEL);
        combo.setBackground(Color.WHITE);
        combo.setForeground(new Color(15, 23, 42));
        combo.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        combo.setFocusable(false);
        combo.setPreferredSize(new Dimension(combo.getPreferredSize().width, 36));
    }

    public static JLabel fieldLabel(String text, Icon icon) {
        JLabel label = new JLabel(text, icon, SwingConstants.LEFT);
        label.setFont(FONT_LABEL);
        label.setForeground(PRIMARY);
        label.setIconTextGap(8);
        return label;
    }

    public static class StatCard extends JPanel {
        private static final long serialVersionUID = 1L;
        private final JLabel lblNumber;

        public StatCard(String caption, String initialValue, Color accent, Icon icon) {
            super(new BorderLayout());
            setOpaque(false);

            RoundedPanel card = new RoundedPanel(new BorderLayout(12, 0), 16, GLASS_PANEL_LT, accent);
            card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

            JLabel lblIcon = new JLabel(icon);
            card.add(lblIcon, BorderLayout.WEST);

            JPanel textColumn = new JPanel();
            textColumn.setOpaque(false);
            textColumn.setLayout(new BoxLayout(textColumn, BoxLayout.Y_AXIS));

            lblNumber = new JLabel(initialValue);
            lblNumber.setFont(FONT_STAT_NUMBER);
            lblNumber.setForeground(accent);
            lblNumber.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel lblCaption = new JLabel(caption);
            lblCaption.setFont(FONT_STAT_CAPTION);
            lblCaption.setForeground(TEXT_MUTED);
            lblCaption.setAlignmentX(Component.LEFT_ALIGNMENT);

            textColumn.add(lblNumber);
            textColumn.add(lblCaption);
            card.add(textColumn, BorderLayout.CENTER);

            add(card, BorderLayout.CENTER);
        }

        public void setValue(String value) {
            lblNumber.setText(value);
        }
    }
}
