package com.briteboxbackend.briterbox.dto;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class QRCodeGenerator {

    private static final String QR_CODE_IMAGE_FORMAT = "PNG"; // Change format to PNG for better compression
    private static final int MAX_SIZE = 20000; // 20 KB

    public static DecodedQRCode decodeQRCode(byte[] qrCodeBytes) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(qrCodeBytes);
            BufferedImage qrCodeImage = ImageIO.read(bais);

            LuminanceSource source = new BufferedImageLuminanceSource(qrCodeImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            QRCodeReader reader = new QRCodeReader();
            Result result = reader.decode(bitmap);

            return new DecodedQRCode(result.getText(), true); // Return decoded text and validity true
        } catch (IOException | NotFoundException | ChecksumException | FormatException e) {
            e.printStackTrace();
            return new DecodedQRCode(null, false); // Return invalid QR code if decoding fails
        }
    }

    public static byte[] generateQRCodeImage(String token) {
        try {
            // Set QR code parameters
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L); // Set error correction level to low (L)

            // Generate QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(token, BarcodeFormat.QR_CODE, 100, 100, hints);

            // Create BufferedImage from BitMatrix
            BufferedImage qrCodeImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < 100; x++) {
                for (int y = 0; y < 100; y++) {
                    qrCodeImage.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
                }
            }

            // Convert BufferedImage to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrCodeImage, QR_CODE_IMAGE_FORMAT, baos);

            return baos.toByteArray();
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return null; // Handle error gracefully in your application
        }
    }

    public static String generateSecureRandomToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[8]; // Adjust length as needed
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public static class DecodedQRCode {
        private final String username;
        private final boolean valid;

        public DecodedQRCode(String username, boolean valid) {
            this.username = username;
            this.valid = valid;
        }

        public String getUsername() {
            return username;
        }

        public boolean isValid() {
            return valid;
        }
    }
}
