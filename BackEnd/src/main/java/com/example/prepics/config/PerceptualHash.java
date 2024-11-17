package com.example.prepics.config;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class PerceptualHash {

    // Kích thước ảnh giảm để tính DCT
    private static final int SMALL_SIZE = 32;
    private static final int HASH_SIZE = 8;

    public static String calculatePHash(File image) throws Exception {
        // Bước 1: Đọc ảnh từ file
        BufferedImage img = ImageIO.read(image);

        // Bước 2: Resize ảnh xuống 32x32
        BufferedImage resizedImg = resize(img, SMALL_SIZE, SMALL_SIZE);

        // Bước 3: Chuyển đổi sang grayscale
        double[][] grayPixels = toGrayscale(resizedImg);

        // Bước 4: Tính toán DCT
        double[][] dctValues = applyDCT(grayPixels);

        // Bước 5: Lấy vùng DCT 8x8 phía trên bên trái (trừ DCT[0][0])
        double[] dctLowFreq = new double[HASH_SIZE * HASH_SIZE - 1];
        int idx = 0;
        for (int i = 0; i < HASH_SIZE; i++) {
            for (int j = 0; j < HASH_SIZE; j++) {
                if (i == 0 && j == 0) continue; // Bỏ qua DCT[0][0]
                dctLowFreq[idx++] = dctValues[i][j];
            }
        }

        // Bước 6: Tính giá trị trung bình
        double avg = 0;
        for (double val : dctLowFreq) {
            avg += val;
        }
        avg /= dctLowFreq.length;

        // Bước 7: Tạo hash nhị phân dựa trên giá trị trung bình
        StringBuilder hash = new StringBuilder();
        for (double val : dctLowFreq) {
            hash.append(val > avg ? "1" : "0");
        }

        return hash.toString();
    }

    // Resize ảnh
    private static BufferedImage resize(BufferedImage img, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = resized.createGraphics();
        g.drawImage(img, 0, 0, width, height, null);
        g.dispose();
        return resized;
    }

    // Chuyển đổi ảnh thành grayscale
    private static double[][] toGrayscale(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        double[][] grayscale = new double[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                // Công thức chuyển đổi grayscale
                grayscale[x][y] = 0.299 * r + 0.587 * g + 0.114 * b;
            }
        }
        return grayscale;
    }

    // Tính toán DCT
    private static double[][] applyDCT(double[][] matrix) {
        int N = matrix.length;
        double[][] dct = new double[N][N];
        double c1 = Math.sqrt(2.0 / N);

        for (int u = 0; u < N; u++) {
            for (int v = 0; v < N; v++) {
                double sum = 0.0;
                for (int x = 0; x < N; x++) {
                    for (int y = 0; y < N; y++) {
                        sum += matrix[x][y] * Math.cos(((2 * x + 1) * u * Math.PI) / (2 * N))
                                * Math.cos(((2 * y + 1) * v * Math.PI) / (2 * N));
                    }
                }
                double cu = (u == 0) ? 1 / Math.sqrt(2) : 1;
                double cv = (v == 0) ? 1 / Math.sqrt(2) : 1;
                dct[u][v] = c1 * cu * cv * sum;
            }
        }
        return dct;
    }
}