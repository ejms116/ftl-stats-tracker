package net.gausman.ftl.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IconUtils {

    // cache: (originalIcon + color + size) → tinted icon
    private static final Map<String, ImageIcon> cache = new ConcurrentHashMap<>();

    public static ImageIcon tintIcon(ImageIcon srcIcon, Color color, int size) {
        if (srcIcon == null) return null;

        String key = srcIcon.hashCode() + "_" + color.getRGB() + "_" + size;
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        // scale synchronously
        BufferedImage scaled = scaleImage(srcIcon.getImage(), size, size);

        // tint
        BufferedImage tinted = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = tinted.createGraphics();
        g2.drawImage(scaled, 0, 0, null);

        g2.setComposite(AlphaComposite.SrcIn);
        g2.setColor(color);
        g2.fillRect(0, 0, size, size);

        g2.dispose();

        ImageIcon result = new ImageIcon(tinted);
        cache.put(key, result);
        return result;
    }


    private static BufferedImage scaleImage(Image src, int width, int height) {
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(src, 0, 0, width, height, null);
        g2.dispose();
        return scaled;
    }

}

