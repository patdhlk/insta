package insta.web.helper;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class ResourceUtil {
	
	private static final Logger log = Logger.getLogger( ResourceUtil.class.getName() );
	
	public static byte[] toByteArray(InputStream is) throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 10)) {
			int length;
			byte[] buffer = new byte[2 * 1024];
			while ((length = is.read(buffer)) != -1) {
				baos.write(buffer, 0, length);
			}
			log.log(Level.INFO, "...read [{}] KBs.", baos.size() / 1024);
			return baos.toByteArray();
		}

	}
	
	public static byte[] resizeImage(byte[] bytes, int desiredWidth, int desiredHeight) throws IOException {
		//Read and resize image
		InputStream in = new ByteArrayInputStream(bytes);
		BufferedImage bufferedImageIn = ImageIO.read(in);
		
		int newHeight = 0;
		int newWidth = 0;
		boolean doResize = false;
		if (bufferedImageIn.getWidth() > desiredWidth) {
			newHeight = desiredWidth*bufferedImageIn.getHeight()/bufferedImageIn.getWidth();
			newWidth = desiredWidth;
			doResize = true;
		}
		if (bufferedImageIn.getHeight() > desiredHeight) {
			newWidth = desiredHeight*bufferedImageIn.getWidth()/bufferedImageIn.getHeight();
			newHeight = desiredHeight;
			doResize = true;
		}
		if (doResize) {
			log.log(Level.INFO, String.format("ResourceUtil.resizeImage Width:%d Height:%d", newWidth, newHeight));
			BufferedImage bufferedImageOut = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D)bufferedImageOut.getGraphics();
			g.drawImage(bufferedImageIn, 0, 0, newWidth, newHeight, null);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write( bufferedImageOut, "jpg", baos );
			baos.flush();
			bytes = baos.toByteArray();
			baos.close();
		}
		return bytes;
	}
}
