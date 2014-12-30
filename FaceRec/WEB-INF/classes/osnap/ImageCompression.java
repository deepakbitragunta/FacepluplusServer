package osnap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class ImageCompression {

	public File compressImages(String sourceURL, String destDir) {
		File compressedImageFile = null;		
		try {
			
			String fileName = FilenameUtils.getBaseName(sourceURL);
			String ext = FilenameUtils.getExtension(sourceURL);
			
			URL url = new URL(sourceURL);
			
			System.out.println("Undergoing compression");
			
			File imageFile = new File(fileName);
			
			FileUtils.copyURLToFile(url, imageFile);
			
			String compressed_name = fileName + "_compressed" + ext;
			
			compressedImageFile = new File(compressed_name);

			InputStream is = new FileInputStream(imageFile);
			OutputStream os = new FileOutputStream(compressedImageFile);

			float quality = 0.6f;

			// create a BufferedImage as the result of decoding the supplied InputStream
			BufferedImage image = ImageIO.read(is);

			// get all image writers for JPG format
			Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");

			if (!writers.hasNext())
				throw new IllegalStateException("No writers found");

			ImageWriter writer = (ImageWriter) writers.next();
			ImageOutputStream ios = ImageIO.createImageOutputStream(os);
			writer.setOutput(ios);

			ImageWriteParam param = writer.getDefaultWriteParam();

			// compress to a given quality
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(quality);

			// appends a complete image stream containing a single image and
			//associated stream and image metadata and thumbnails to the output
			writer.write(null, new IIOImage(image, null, null), param);

			// close all streams
			is.close();
			os.close();
			ios.close();
			writer.dispose();
		
		} // end of try
		  catch(IOException e) {
			   e.printStackTrace();
		 }
		return compressedImageFile;
	}
}	