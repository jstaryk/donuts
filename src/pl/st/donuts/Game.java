package pl.st.donuts;

import java.awt.Graphics2D;

public interface Game {
    void update();
    void draw(Graphics2D gr);
    int width();
    int height();
    String title();
    int ups();
}
