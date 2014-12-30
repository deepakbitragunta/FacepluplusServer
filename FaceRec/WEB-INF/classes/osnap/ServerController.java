package osnap;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.nio.channels.*;
import java.net.*;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.facepp.result.FaceppResult;

import org.json.JSONObject;
import org.json.JSONTokener;
import osnap.FaceRecognition;

public class ServerController extends HttpServlet{

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException
	{
		try{
		
		//String pictureURL = request.getParameter("pictureURL");
		String jsonString = request.getParameter("jsondata");
		JSONObject jObj = (JSONObject) new JSONTokener(jsonString).nextValue();
		String pictureURL = jObj.getString("picurl");
		//check the size of the picture in the url
		URL imageURL = new URL(pictureURL);
		URLConnection conn = imageURL.openConnection(); //Throws an IOException when the URL is invalid.
		int size = 0;
		size = conn.getContentLength();

		if(size > 3000000)
		{
			
			response.setContentType("application/json");
			response.setStatus(404);
			PrintWriter out = response.getWriter();
			out.println("{");
			out.println("\"ErrorNumber\" : 1,");
			out.println("\"ErrorDescription\" : \"File size exceeds limit\"");
			out.println("}");
		}	
		else
		{
			FaceRecognition recognizePeople= new FaceRecognition(pictureURL, response);
			int opertationStatus = recognizePeople.recognitionRecognize();
			if(opertationStatus == 0)
			{
				System.out.println("Request executed successfully");
			}
		}

	}//end of try

	catch (IOException x)
	{
		response.setContentType("application/json");
		response.setStatus(404);
		PrintWriter out = response.getWriter();
		out.println("{");
		out.println("\"ErrorNumber\" : 4,");
		out.println("\"ErrorDescription\" : \"Invalid URL\"");
		out.println("}");
	}
	
	catch (Exception e)
	{
		e.printStackTrace();
	}
	
	}//end of doPost


}//end of ServerController