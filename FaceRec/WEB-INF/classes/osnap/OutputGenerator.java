package osnap;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.facepp.result.FaceppResult;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OutputGenerator {
	public FaceppResult fppResult = null;

	public OutputGenerator(FaceppResult result)
	{
		fppResult = result;
	}

	public void createOutput(HttpServletResponse response, float imgHeight, float imgWidth)
	{
		try{
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		//out.println(fppResult);
		int facesDetected = fppResult.get("face").getCount();
		if(facesDetected == 0)
		{
			out.println("{");
			out.println("\"ErrorNumber\" : 2,");
			out.println("\"ErrorDescription\" : \"No faces detected in the image\"");
			out.println("}");	
		}
		else
		{
		out.println("{");
		out.println("\"ErrorNumber\" : 0,");
		out.println("\"session_id\" : \""+ fppResult.get("session_id") +"\",");
		String detected = Integer.toString(facesDetected);
		out.println("\"total_faces_detected\" : " + detected + ",");
		out.println("\"faces\" : [");
		float centerX = 0.0f;
		float centerY = 0.0f;
		float faceWidth = 0.0f;
		float faceHeight = 0.0f;
		int face_top_left_x = 0;
		int face_top_left_y = 0;
        int face_bottom_left_x = 0;
        int face_bottom_left_y = 0;
        int face_top_right_x = 0;
        int face_top_right_y = 0;
        int face_bottom_right_x = 0;
        int face_bottom_right_y = 0;
        int nose_location_x = 0;
        int nose_location_y = 0;
        float match_confidence = 0.0f;
        Date timeStamp = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int i=0;

		for(i=0;i<facesDetected;i++)
		{
			out.println("{");
			float match1_confidence = fppResult.get("face").get(i).get("candidate").get(0).get("confidence").toDouble().floatValue();
			float match2_confidence = fppResult.get("face").get(i).get("candidate").get(1).get("confidence").toDouble().floatValue();
			float match3_confidence = fppResult.get("face").get(i).get("candidate").get(2).get("confidence").toDouble().floatValue();
			if(match1_confidence>=match2_confidence && match1_confidence>=match3_confidence)
			{
				match_confidence = match1_confidence;
				out.println("\"user_id\" : \"" + fppResult.get("face").get(i).get("candidate").get(0).get("person_name") + "\",");
			}
			else if(match2_confidence>=match1_confidence && match2_confidence>=match3_confidence)
			{
				match_confidence = match2_confidence;
				out.println("\"user_id\" : \"" + fppResult.get("face").get(i).get("candidate").get(1).get("person_name") + "\",");
			}
			else
			{
				match_confidence = match3_confidence;
				out.println("\"user_id\" : \"" + fppResult.get("face").get(i).get("candidate").get(2).get("person_name") + "\",");
			}

			centerX = fppResult.get("face").get(i).get("position").get("center").get("x").toDouble().floatValue();
			centerY = fppResult.get("face").get(i).get("position").get("center").get("y").toDouble().floatValue();
			centerX = centerX * imgWidth / 100.0f;
			centerY = centerY * imgHeight / 100.0f;
			faceWidth = fppResult.get("face").get(i).get("position").get("width").toDouble().floatValue() * imgWidth / 100.0f;
			faceHeight = fppResult.get("face").get(i).get("position").get("height").toDouble().floatValue() * imgHeight / 100.0f;
			face_top_left_x = (int) (centerX - faceWidth / 2.0f);
			face_top_left_y = (int) (centerY - faceHeight/ 2.0f);
			face_bottom_left_x = (int) (centerX - faceWidth / 2.0f);
			face_bottom_left_y = (int) (centerY + faceHeight / 2.0f);
			face_top_right_x = (int) (centerX + faceWidth / 2.0f);
			face_top_right_y = (int) (centerY - faceHeight / 2.0f);
			face_bottom_right_x = (int) (centerX + faceWidth / 2.0f);
			face_bottom_right_y = (int) (centerY + faceHeight / 2.0f);
			nose_location_x = (int) centerX;
			nose_location_y = (int) centerY;
			out.println("\"face_top_left_x\" : " + Integer.toString(face_top_left_x) + ",");
			out.println("\"face_top_left_y\" : " + Integer.toString(face_top_left_y) + ",");
			out.println("\"face_bottom_left_x\" : " + Integer.toString(face_bottom_left_x) + ",");
			out.println("\"face_bottom_left_y\" : " + Integer.toString(face_bottom_left_y) + ",");
			out.println("\"face_top_right_x\" : " + Integer.toString(face_top_right_x) + ",");
			out.println("\"face_top_right_y\" : " + Integer.toString(face_top_right_y) + ",");
			out.println("\"face_bottom_right_x\" : " + Integer.toString(face_bottom_right_x) + ",");
			out.println("\"face_bottom_right_y\" : " + Integer.toString(face_bottom_right_y) +",");
			out.println("\"nose_location_x\": " + Integer.toString(nose_location_x) + ",");
			out.println("\"nose_location_y\": " + Integer.toString(nose_location_y) + ",");
			//match_confidence = fppResult.get("face").get(i).get("candidate").get(0).get("confidence").toDouble().floatValue();
			out.println("\"match_confidence\" : " + Float.toString(match_confidence));
			if(i == facesDetected - 1)
			{
				out.println("}");
			}
			else
			{
				out.println("},");
			}

		}

		out.println("],");
		timeStamp = Calendar.getInstance().getTime();
		out.println("\"request_date_time\" : \"" + df.format(timeStamp) + "\"");
		out.println("}");
		}//end of else facesDetected==0
	}//end of try
	catch(Exception e)
	{
		e.printStackTrace();
	}
	}
}
