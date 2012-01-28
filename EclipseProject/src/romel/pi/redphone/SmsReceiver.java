package romel.pi.redphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver 
{
	private Context context;
	

	
	String msgbody ="";
	String sender = "";
	SmsMessage[] msgs = null;
	String phonenumber = null;
	static String[] nocMobile = {"09471816917","09471816917"};
	SmsManager sm = SmsManager.getDefault();
	
	String forwardCode = "**21*";
	
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		System.out.println("Starting Receiver");
		
		

		//---get the SMS message passed in---
        Bundle bundle = intent.getExtras();        
        if (bundle != null)
        {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];            
            for (int i=0; i<msgs.length; i++)
            {
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
                sender = msgs[i].getOriginatingAddress();
                msgbody = msgs[i].getMessageBody().toString();

            }

            try 
            {
            	this.context = context;
            	MessageBodyParserTrigger();
            	Toast.makeText(context, "Email Sent", Toast.LENGTH_SHORT).show();
            	
            	

               
           /*     
               if(!msgbody.toLowerCase().contains("cascpi2012"))
               {
            	   for(int i=0; i<2; i++)
            		   forwardSms(nocMobile[i] ,msgbody);
            	   Toast.makeText(context, "SMS Sent", Toast.LENGTH_SHORT).show();
               }  */
            	
            this.context.stopService(null);
               
            } 
            catch (Exception e) 
            {   
                Log.v("SendMail", e.getMessage(), e); 
                
            } 
        } 

	}
	
	
	void extractPhonenmbr()
	{
		String nmbr = msgbody;
		String[] temp;
		temp = nmbr.split(" ");
		for(int i =0; i < temp.length ; i++)
			phonenumber = temp[i];
		
	}
	
	void MessageBodyParserTrigger()
    {

		extractPhonenmbr();
		GMailSender sender = new GMailSender("pi.redphone@gmail.com", "ayay!Chix143");
		if(msgbody.toString().startsWith("cascpi2012"))
		{
			
			
			if(msgbody.contains("FRWD"))
			{
				//Toast.makeText(context, "Call forwarding", Toast.LENGTH_SHORT).show();
				
				Object localObject2 = new Intent("android.intent.action.CALL");
				Object cancelForwarding = Uri.encode(Uri.encode("#") + Uri.encode("#") + "21" + Uri.encode("#"));
                ((Intent)localObject2).setData(Uri.parse("tel:"+cancelForwarding));
				((Intent)localObject2).addFlags(268435456);
				this.context.startActivity((Intent)localObject2);
				
				
				
				Object localObject1 = new Intent("android.intent.action.CALL");
				Object fowardTo = Uri.encode("**21*" + phonenumber + Uri.encode("#"));
                ((Intent)localObject1).setData(Uri.parse("tel:"+fowardTo));
                ((Intent)localObject1).addFlags(268435456);
                this.context.startActivity((Intent)localObject1);
			
	            try 
	            {
	            	Toast.makeText(context, "Sending received SMS to Email!", Toast.LENGTH_LONG).show();
	            	sender.sendMail("Call Forward Successful","Forwarded to:"+phonenumber+"\nThis is a test of PI RED PHONE","log-alerts@cascadeo.com","romel@stratloc.com");
				} 
	            catch (Exception e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.v("bodyParser", e.getMessage(), e);
					
				}
							
			}
			else if(msgbody.contains("OFF"))
			{
				Toast.makeText(context, "Call forwarding Cancelled", Toast.LENGTH_SHORT).show();
				
				Object localObject1 = new Intent("android.intent.action.CALL");
				Object cancelForwarding = Uri.encode(Uri.encode("#") + Uri.encode("#") + "21" + Uri.encode("#"));
                ((Intent)localObject1).setData(Uri.parse("tel:"+cancelForwarding));
				((Intent)localObject1).addFlags(268435456);
				this.context.startActivity((Intent)localObject1); 
		
				try 
	            {
	            	sender.sendMail("Call Forward Disabled","Call forwarding disabled","log-alerts@cascadeo.com","romel@stratloc.com");
				} 
	            catch (Exception e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			else //invalid command
			{
				try 
	            {
	            	sender.sendMail("Invalid Command","To activate/To change forwarding number:\n<PIN> <command> <#Phone Number>\n(e.g casc1234 FRWD #09275566789)","log-alerts@cascadeo.com","romel@stratloc.com");
				} 
	            catch (Exception e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

			}
		}
		

    }

	void forwardSms(String toNumber ,String body)
	{
		sm.sendTextMessage(toNumber, null, body, null, null);
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	


}


