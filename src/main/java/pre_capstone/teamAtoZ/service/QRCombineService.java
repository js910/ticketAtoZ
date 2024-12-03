package pre_capstone.teamAtoZ.service;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class QRCombineService {

    // 이미지 결합 로직
    public String combineQRWithImage(BufferedImage qrImage, BufferedImage backgroundImage, String size, String position) throws IOException {
        // QR 이미지와 배경 이미지 로드
        //BufferedImage qrImage = ImageIO.read(new File(qrImagePath));

       // BufferedImage backgroundImage = ImageIO.read(new File(backgroundImagePath));

        // 기본 위치와 크기 설정
        int xPosition = 0;
        int yPosition = 0;
        int qrWidth = 100; // 기본 너비
        int qrHeight = 100; // 기본 높이

        // 크기 설정 if 문
        if (size.equalsIgnoreCase("small")) {
            qrWidth = 200;
            qrHeight = 200;
        } else if (size.equalsIgnoreCase("medium")) {
            qrWidth = 400;
            qrHeight = 400;
        } else if (size.equalsIgnoreCase("large")) {
            qrWidth = 600;
            qrHeight = 600;
        }

        // 위치 설정 if 문
        if (position.equalsIgnoreCase("top-left")) {
            xPosition = 0;
            yPosition = 0;
        } else if (position.equalsIgnoreCase("top-right")) {
            xPosition = backgroundImage.getWidth() - qrWidth;
            yPosition = 0;
        } else if (position.equalsIgnoreCase("bottom-left")) {
            xPosition = 0;
            yPosition = backgroundImage.getHeight() - qrHeight;
        } else if (position.equalsIgnoreCase("bottom-right")) {
            xPosition = backgroundImage.getWidth() - qrWidth;
            yPosition = backgroundImage.getHeight() - qrHeight;
        } else if (position.equalsIgnoreCase("center")) {
            xPosition = (backgroundImage.getWidth() - qrWidth) / 2;
            yPosition = (backgroundImage.getHeight() - qrHeight) / 2;
        }

        // QR 이미지의 크기를 조정
        BufferedImage resizedQRImage = new BufferedImage(qrWidth, qrHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2dResize = resizedQRImage.createGraphics();
        g2dResize.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2dResize.drawImage(qrImage, 0, 0, qrWidth, qrHeight, null);
        g2dResize.dispose();

        // 배경 이미지와 동일한 크기의 새 이미지 생성
        BufferedImage combinedImage = new BufferedImage(backgroundImage.getWidth(), backgroundImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // 배경 이미지를 결합 이미지에 그리기
        Graphics2D g2d = combinedImage.createGraphics();
        g2d.drawImage(backgroundImage, 0, 0, null);

        // 오퍼시티 50% 적용
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

        // QR 이미지를 지정된 위치에 그리기
        g2d.drawImage(resizedQRImage, xPosition, yPosition, null);
        g2d.dispose();

        // 결합된 이미지를 바이트 배열로 변환
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(combinedImage, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        // Base64 URL 문자열로 변환
        String base64Image = Base64.encodeBase64String(imageBytes);
        return "data:image/png;base64," + base64Image;
    }
}