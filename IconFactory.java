import javax.swing.Icon;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Bộ icon vector vẽ tay bằng Graphics2D thay vì dùng file ảnh/emoji, để icon
 * luôn hiển thị sắc nét và đồng nhất trên mọi máy (không phụ thuộc font emoji
 * của hệ điều hành, không cần đóng gói thêm tài nguyên ảnh).
 */
public final class IconFactory {

    private IconFactory() {
    }

    public enum Type {
        ADD, TRASH, CHECK, DOOR, GIFT, STAR, CHART, LOGOUT,
        SEARCH, USER, ADD_USER, ROOM, CLOCK, LOCK, MAIL, PHONE
    }

    public static Icon of(Type type, Color color, int size) {
        return new VectorIcon(type, color, size);
    }

    private static class VectorIcon implements Icon {
        private final Type type;
        private final Color color;
        private final int size;

        VectorIcon(Type type, Color color, int size) {
            this.type = type;
            this.color = color;
            this.size = size;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g0, int x, int y) {
            Graphics2D g = (Graphics2D) g0.create();
            g.translate(x, y);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(color);
            g.setStroke(new BasicStroke(Math.max(1.6f, size / 9f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            float s = size;

            switch (type) {
                case ADD -> {
                    g.draw(new Line2D.Float(s * 0.5f, s * 0.16f, s * 0.5f, s * 0.84f));
                    g.draw(new Line2D.Float(s * 0.16f, s * 0.5f, s * 0.84f, s * 0.5f));
                }
                case TRASH -> {
                    g.draw(new Line2D.Float(s * 0.2f, s * 0.3f, s * 0.8f, s * 0.3f));
                    g.draw(new RoundRectangle2D.Float(s * 0.28f, s * 0.3f, s * 0.44f, s * 0.56f, s * 0.08f, s * 0.08f));
                    g.draw(new Line2D.Float(s * 0.4f, s * 0.16f, s * 0.6f, s * 0.16f));
                    g.draw(new Line2D.Float(s * 0.42f, s * 0.42f, s * 0.42f, s * 0.72f));
                    g.draw(new Line2D.Float(s * 0.58f, s * 0.42f, s * 0.58f, s * 0.72f));
                }
                case CHECK -> {
                    Path2D p = new Path2D.Float();
                    p.moveTo(s * 0.18f, s * 0.52f);
                    p.lineTo(s * 0.42f, s * 0.76f);
                    p.lineTo(s * 0.84f, s * 0.26f);
                    g.draw(p);
                }
                case DOOR -> {
                    g.draw(new RoundRectangle2D.Float(s * 0.26f, s * 0.14f, s * 0.46f, s * 0.72f, s * 0.06f, s * 0.06f));
                    g.fill(new Ellipse2D.Float(s * 0.58f, s * 0.48f, s * 0.07f, s * 0.07f));
                }
                case GIFT -> {
                    g.draw(new RoundRectangle2D.Float(s * 0.18f, s * 0.42f, s * 0.64f, s * 0.42f, s * 0.05f, s * 0.05f));
                    g.draw(new Line2D.Float(s * 0.5f, s * 0.42f, s * 0.5f, s * 0.84f));
                    g.draw(new Line2D.Float(s * 0.14f, s * 0.42f, s * 0.86f, s * 0.42f));
                    g.draw(new Ellipse2D.Float(s * 0.3f, s * 0.16f, s * 0.2f, s * 0.26f));
                    g.draw(new Ellipse2D.Float(s * 0.5f, s * 0.16f, s * 0.2f, s * 0.26f));
                }
                case STAR -> g.draw(starPath(s * 0.5f, s * 0.52f, s * 0.4f, s * 0.17f, 5));
                case CHART -> {
                    g.draw(new Line2D.Float(s * 0.16f, s * 0.84f, s * 0.84f, s * 0.84f));
                    g.draw(new Line2D.Float(s * 0.3f, s * 0.84f, s * 0.3f, s * 0.5f));
                    g.draw(new Line2D.Float(s * 0.5f, s * 0.84f, s * 0.5f, s * 0.3f));
                    g.draw(new Line2D.Float(s * 0.7f, s * 0.84f, s * 0.7f, s * 0.62f));
                }
                case LOGOUT -> {
                    g.draw(new RoundRectangle2D.Float(s * 0.16f, s * 0.18f, s * 0.34f, s * 0.64f, s * 0.06f, s * 0.06f));
                    g.draw(new Line2D.Float(s * 0.38f, s * 0.5f, s * 0.84f, s * 0.5f));
                    Path2D arrow = new Path2D.Float();
                    arrow.moveTo(s * 0.66f, s * 0.34f);
                    arrow.lineTo(s * 0.86f, s * 0.5f);
                    arrow.lineTo(s * 0.66f, s * 0.66f);
                    g.draw(arrow);
                }
                case SEARCH -> {
                    g.draw(new Ellipse2D.Float(s * 0.16f, s * 0.16f, s * 0.46f, s * 0.46f));
                    g.draw(new Line2D.Float(s * 0.58f, s * 0.58f, s * 0.84f, s * 0.84f));
                }
                case USER -> {
                    g.draw(new Ellipse2D.Float(s * 0.34f, s * 0.14f, s * 0.32f, s * 0.32f));
                    g.draw(new Arc2D.Float(s * 0.14f, s * 0.52f, s * 0.72f, s * 0.5f, 0, 180, Arc2D.OPEN));
                }
                case ADD_USER -> {
                    g.draw(new Ellipse2D.Float(s * 0.22f, s * 0.14f, s * 0.3f, s * 0.3f));
                    g.draw(new Arc2D.Float(s * 0.06f, s * 0.5f, s * 0.62f, s * 0.46f, 0, 180, Arc2D.OPEN));
                    g.draw(new Line2D.Float(s * 0.78f, s * 0.5f, s * 0.78f, s * 0.82f));
                    g.draw(new Line2D.Float(s * 0.62f, s * 0.66f, s * 0.94f, s * 0.66f));
                }
                case ROOM -> {
                    g.draw(new RoundRectangle2D.Float(s * 0.14f, s * 0.28f, s * 0.72f, s * 0.56f, s * 0.05f, s * 0.05f));
                    g.draw(new Line2D.Float(s * 0.14f, s * 0.5f, s * 0.86f, s * 0.5f));
                }
                case CLOCK -> {
                    g.draw(new Ellipse2D.Float(s * 0.14f, s * 0.14f, s * 0.72f, s * 0.72f));
                    g.draw(new Line2D.Float(s * 0.5f, s * 0.5f, s * 0.5f, s * 0.26f));
                    g.draw(new Line2D.Float(s * 0.5f, s * 0.5f, s * 0.7f, s * 0.6f));
                }
                case LOCK -> {
                    g.draw(new RoundRectangle2D.Float(s * 0.22f, s * 0.46f, s * 0.56f, s * 0.42f, s * 0.06f, s * 0.06f));
                    g.draw(new Arc2D.Float(s * 0.3f, s * 0.14f, s * 0.4f, s * 0.42f, 0, 180, Arc2D.OPEN));
                }
                case MAIL -> {
                    g.draw(new RoundRectangle2D.Float(s * 0.12f, s * 0.26f, s * 0.76f, s * 0.5f, s * 0.05f, s * 0.05f));
                    Path2D p = new Path2D.Float();
                    p.moveTo(s * 0.12f, s * 0.28f);
                    p.lineTo(s * 0.5f, s * 0.58f);
                    p.lineTo(s * 0.88f, s * 0.28f);
                    g.draw(p);
                }
                case PHONE -> {
                    g.draw(new RoundRectangle2D.Float(s * 0.32f, s * 0.1f, s * 0.36f, s * 0.8f, s * 0.08f, s * 0.08f));
                    g.draw(new Line2D.Float(s * 0.44f, s * 0.8f, s * 0.56f, s * 0.8f));
                }
            }
            g.dispose();
        }

        private Path2D starPath(float cx, float cy, float rOuter, float rInner, int points) {
            Path2D p = new Path2D.Float();
            double angle = -Math.PI / 2;
            double step = Math.PI / points;
            for (int i = 0; i < points * 2; i++) {
                float r = (i % 2 == 0) ? rOuter : rInner;
                float px = (float) (cx + r * Math.cos(angle));
                float py = (float) (cy + r * Math.sin(angle));
                if (i == 0) {
                    p.moveTo(px, py);
                } else {
                    p.lineTo(px, py);
                }
                angle += step;
            }
            p.closePath();
            return p;
        }
    }
}
