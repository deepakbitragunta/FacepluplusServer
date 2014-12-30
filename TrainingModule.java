import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.omg.CosNaming.IstringHelper;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.facepp.result.FaceppResult;

/**
 * Second Prototype server module
 * Prototype2.java
 * @author OSnap developers - Deepak Bitragunta and Haritha Gorijavolu
 * 
 */
	
public class TrainingModule {
	HttpRequests httpRequests;
	
	// Authorize with the Faceplusplus server using the developer token and password
	public void init() {
		this.httpRequests = new HttpRequests("46d488591cd426d3139173f5ad865abe", "Vg6Xl4gOpDxRvcT1vccoX0py6ykFL_FL", true);
	}
	
	public boolean compressImages(String source, String destination) {
		boolean compressed = false;	
		
		try {

			//	url = new URL();
		    File startingDirectory= new File(source);
		    String dest_path = destination;
		    
		    File [] directories;
			File [] files;
		    String  dest_file_path;
		    
		   	directories = startingDirectory.listFiles();
		   	System.out.println("In compression");
		  //print out all file names, in the the order of File.compareTo()
		    for(File dir : directories) {
		    	System.out.println(dir);
		    	dest_path = destination + "/" + dir.getName();
		    	
		    	File destDirectory = new File(dest_path);
		        boolean isdirCreated = destDirectory.mkdir();
				  
		    	if(destDirectory.isDirectory() && isdirCreated) {
		 		    files = dir.listFiles();
		 		    compressed = false;
		 		    for(File sourcefile : files) {
		 		    	if(!(sourcefile.getName().endsWith(".jpg") || sourcefile.getName().endsWith(".JPG"))) {
		 		    		continue;
		 		    	}
		 		    	dest_file_path = dest_path + "/" + sourcefile.getName();
		 		    	File destFile = new File(dest_file_path);
		 		    	float quality = 0.5f;
		 		    	long source_size = sourcefile.length();
		 				float dest_size = 500 * 1024; // in bytes
		 				
		 				
		 				if(source_size < 500 * 1024) { 
		 					Files.copy(sourcefile.toPath(), destFile.toPath());
		 				}
		 				else {
		 					quality =  (dest_size / source_size);  
		 					if(quality > 0.8) {
		 						quality = 0.75f;
		 					}
		 				}
		 				
		 				InputStream is = new FileInputStream(sourcefile);
		 				OutputStream os = new FileOutputStream(destFile);
		 				System.out.println("Quality: " + quality);
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
		 				
		 				compressed = true;
		 				// close all streams
		 				is.close();
		 				os.close();
		 				ios.close();
		 				writer.dispose();
		 				
		 		   } // Inner for loop
		    	} // End of if statement    
		    } // Outer for loop		    
		 } // end of try
		  catch(IOException e) {
		 	   e.printStackTrace();
		 }
		return compressed;
	}
	/*
	 * Input: HttpRequests object and the URL of the image which needs to be trained
	 * Output: Whether the photo is trained or not
	 * Functionality: It takes the image url and detects the faces in the image. It creates a person and
	 * adds faces to that person. It also creates a group and adds the person created to the group
	 * Then, the faceplusplus server is trained with a group which includes a set of persons with a bunch of
	 *  the faces for each person.
	 * 
	 */
	public boolean trainServer(String root_url) {	
		
		FaceppResult result, trainResult = null;
		boolean trained = false;	
		
		try {

			String source_path = "/home/osnap/WWW/files/training";
		//    String dest_path = "/home/osnap/WWW/files/compressed";
			String   dest_path  = "/home/osnap/WWW/files/TrainingSecondRound";
			
		    File startingDirectory= new File(source_path);
		    File destDirectory = new File(dest_path);
		    File [] destFiles = destDirectory.listFiles();
		    if(destFiles.length == 0) {
		      compressImages(source_path, dest_path);
		    }

		    File [] directories;
			File [] files;
		    String  image_url, user_id;
		    
	    	directories = destDirectory.listFiles();
			String trained_ids = "";
			String ignored_images = "", trained_images = "";
		
		  //print out all file names, in the the order of File.compareTo()
		    for(File dir : directories) {
		    	System.out.println(dir);
		    	
		    	if(dir.isDirectory()) {
		    		// Person Create
		    		user_id = dir.getName();
		    		System.out.println("Creating a person with the given user id \n" + httpRequests.personCreate(new PostParameters().setPersonName(user_id)));
		    		
		 		    files = dir.listFiles();
		 		    
		 		    for(File file : files) {
		 		    	if(!(file.getName().endsWith(".jpg") || file.getName().endsWith(".JPG"))) {
		 		    		continue;
		 		    	}
		 		    	
		 		    	image_url = root_url + "/" + dir.getName() + "/" + file.getName() ;
		 		    	
						
						System.out.println("Training a new image with URL*** :  " + image_url + "\n" );
						
		 		    	result = httpRequests.detectionDetect(new PostParameters().setUrl(image_url));
						System.out.println(result);
						
						System.out.println("\nAdd person to group \n" + httpRequests.groupAddPerson(new PostParameters().setGroupName("510_training").setPersonName(user_id)) + "\n");
						
						// Found faces in the photo, add those faces to the person created
						if(result.get("face").getCount() == 0) {
							ignored_images += image_url + "\n";
							continue;
						}
						else{
							trained_images += image_url + "\n";
						}
						for (int i = 0; i < result.get("face").getCount(); ++i)
							if((result.get("face") != null) && (result.get("face").get(i) != null) && (result.get("face").get(i).get("face_id") != null) )
								trainResult = httpRequests.personAddFace(new PostParameters().setPersonName(user_id).setFaceId(result.get("face").get(i).get("face_id").toString()));
						trained = true;
						System.out.println(httpRequests.train(new PostParameters().setGroupName("510_training").setType("all")));
						
		 		    }
		 		    
		 		    trained_ids += "  " + user_id;
		    	}
		    	
		    }	
		    String output_file = "/home/osnap/WWW/files/training_output.txt";
		    PrintWriter writer = new PrintWriter(output_file, "UTF-8");
		    writer.println("\n Trained Users" + trained_ids + "\n\n");
		    writer.println("\n Trained images" + trained_images + "\n\n");
		    writer.println("\n Un-Trained images" + ignored_images + "\n\n");
		    writer.close();	
		    
		} catch(FaceppParseException e) {
				e.printStackTrace();
		  } catch (Exception e) {
				System.out.println(e);
			}
			return trained;
		}		   
				
				
				
		    	
		    	
					
					// Hard coded person name and change the name of the person if you want
					// To be modified and retreive the name from database. 
					
		//			if(httpRequests.personGetInfo(new PostParameters().setPersonName(user_id)) != null) {
		//			String user_id = "ashwin";
					
		//			System.out.println("Deleting existing user:" + user_id + ": " + httpRequests.personDelete(new PostParameters().setPersonName(user_id)));
				
		
					//			}
				//	for(int i = 1; )
				//	String image_url = "http://gala.cs.iastate.edu/~osnap/1.jpg";
				//	System.out.println("Image URL: " + image_url);
		//			URL u 
			//		byte[] = 
					
					
				/*	
					if(result.get("face").getCount() == 0) {
						// Faceplusplus server not able to identify not even a single face in this photo
						// Hard coded image URL's needs to be replaced 
						// with a code to retrieve URL from database		
						System.out.println("Faceplusplus server is not intelligent enough to identify a face in this image"
								+ "\n Please don't use the image in the given url for training");	
					//	return trained;
					}
					else{
						*/
						// Add the created persons to the newly created group
						// Hard coded names which will be modified later to fetch from database
						
						
					//	System.out.println("Add faces recognized in the image to the person created: \n" + trainResult);
					/*	// Create a group with a hardcoded name
						System.out.println("\n Create a group");
						System.out.println(httpRequests.groupCreate(new PostParameters().setGroupName("cricketers")));
					*/	
						
						//Trains the group with a person in it and bunch of faces added to the person
						
				//		return trained;
				//	}
					
			//    }
		
	
	boolean removePerson(String user_id) {
		
		boolean removed = false;
		
		try {
			System.out.println(httpRequests.personDelete(new PostParameters().setPersonName(user_id)));
			removed = true;
		} catch (FaceppParseException e) {
			e.printStackTrace();
		}
		
		return removed;
		
	}
	

	/*
	 * Please run this file by changing in the image url and also make sure you have the correct person name and group name to train  
	 */
	public static void main(String[] args) {
	
				
		boolean trained = false, removed = false;
		
		// replace this with a new url of the photo that needs to be trained 
	//	String image_url = "http://static.tumblr.com/c39ce7b/QSnncskzz/dhoni1.jpg";
	//	String root_url = "http://gala.cs.iastate.edu/~osnap/files/compressed";
		String root_url = "http://gala.cs.iastate.edu/~osnap/files/TrainingSecondRound";
		// Create a new object
		TrainingModule trainObj = new TrainingModule();
		
		// Intialize the HttpRequests object after authenticating with the server.
		trainObj.init();
		// Call to remove the person we trained before
	//	removed = trainObj.removePerson(user_id);
	//	System.out.println("\n Is the person removed:  " + removed + "\n\n");
		
		//call the train method 
		trained = trainObj.trainServer(root_url);
		
		// Result of training the given image	System.out.println("\n\n Are the faces in image trained ? " + trained + "\n");
	}
			
		
}



