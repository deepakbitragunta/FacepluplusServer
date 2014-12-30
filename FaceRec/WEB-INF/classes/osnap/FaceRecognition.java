package osnap;

import java.io.*;
import java.net.*;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import java.util.ArrayList;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.facepp.result.FaceppResult;

import osnap.OutputGenerator;

public class FaceRecognition{

	public String imageLocation = null;
	public HttpServletResponse facilitatorResponse = null;

	public FaceRecognition(String photo, HttpServletResponse response)
	{
		imageLocation = photo;
		facilitatorResponse = response;
	}

	public int recognitionRecognize() throws FaceppParseException
	{
		try{
		HttpRequests httpRequests = new HttpRequests("46d488591cd426d3139173f5ad865abe", "Vg6Xl4gOpDxRvcT1vccoX0py6ykFL_FL");
		FaceppResult result = null;
		PostParameters serverParams = new PostParameters();
		serverParams.setGroupName("510_training");
		serverParams.setUrl(imageLocation);
		result = httpRequests.recognitionRecognize(serverParams); //throws FaceppParseException
		URL imgLocation = new URL(imageLocation);
		BufferedImage myImage = ImageIO.read(imgLocation);
		int imgWidth = myImage.getWidth();
		int imgHeight = myImage.getHeight();
		OutputGenerator oGen = new OutputGenerator(result);
		oGen.createOutput(facilitatorResponse,(float)imgHeight,(float)imgWidth);
		int n=0;
		return n;
		}
		
		catch(FaceppParseException x)
		{
			this.generateError(x);
			return 1;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return 2;
		}

	}//end of recognitionRecognize
	
	public void generateError(FaceppParseException x)
	{
		try{
		
			facilitatorResponse.setContentType("application/json");
			facilitatorResponse.setStatus(404);
			PrintWriter out = facilitatorResponse.getWriter();
			out.println("{");
			out.println("\"ErrorNumber\" : 3,");
			out.println("\"ErrorDescription\" : \"Faceplusplus site returned error\"");
			out.println("}");
		
		}//end of try
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}//end of generateError
	
}//end of class
