package com.sujan.utils.imageprocess;

import java.awt.image.BufferedImage;

/**
 * Flip the image horizontally or vertically<br><br>
 * <p>
 * Added by Sujan Maharjan | April 8, 2018 <br>
 * Reference taken from Zoran Devidovic (https://www.youtube.com/watch?v=HJXl2hmapdo)
 * </p>
 */
public class ImageFlip {

    public static final int FLIP_HORIZONTAL = -1;
    public static final int FLIP_VERTICAL = 1;

    /**
     * @param bufferedImage input source image which needs flipping
     * @param direction     Choose whether to flip horizontally or vertically
     * @return flipped image
     */
    public static BufferedImage flip(BufferedImage bufferedImage, int direction) {

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        BufferedImage flippedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                switch (direction) {
                    case FLIP_HORIZONTAL:
                        flippedImage.setRGB((width - 1) - x, y, bufferedImage.getRGB(x, y));
                        break;
                    case FLIP_VERTICAL:
                        flippedImage.setRGB(x, (height - 1) - y, bufferedImage.getRGB(x, y));
                        break;
                }
            }
        }
        return flippedImage;
    }

}
