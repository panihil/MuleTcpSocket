/**
 * 
 */
package edu.bcw.mule35.socket;

import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;
import org.mule.api.transport.PropertyScope;
import edu.bcw.mule35.server.ServerHL7;

/**
 * @author manskema
 * 
 */
public class HL7SocketServer implements Callable
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mule.api.lifecycle.Callable#onCall(org.mule.api.MuleEventContext)
	 */
	@Override
	public Object onCall(MuleEventContext context) throws Exception
	{
		String name = context.getMessage()
				.getProperty("name", PropertyScope.INVOCATION).toString();
/*		String inboundMsgDir = context.getMessage()
				.getProperty("inboundMsgDir", PropertyScope.INVOCATION)
				.toString();*/
		String listeningPort = context.getMessage()
				.getProperty("listeningPort", PropertyScope.INVOCATION)
				.toString();

/*		String outboundIPName = context.getMessage()
				.getProperty("outboundIPName", PropertyScope.INVOCATION)
				.toString(); 
		String outboundPort = context.getMessage()
				.getProperty("outboundPort", PropertyScope.INVOCATION)
				.toString();
		String archiveMsgDir = context.getMessage()
				.getProperty("archiveMsgDir", PropertyScope.INVOCATION)
				.toString();*/

		String numOfTimesToRetry = context.getMessage()
				.getProperty("numOfTimesToRetry", PropertyScope.INVOCATION)
				.toString();
		String numSecsToWait = context.getMessage()
				.getProperty("numSecsToWait", PropertyScope.INVOCATION)
				.toString();

		ServerHL7 serverHL7 = new ServerHL7(Integer.parseInt(listeningPort),
				name);

	/*	serverHL7.setInboundMsgsDir(inboundMsgDir);
		serverHL7.setOutboundConn(outboundIPName,
				Integer.parseInt(outboundPort), archiveMsgDir); */
		serverHL7.setNumOfTimesToRetry(Integer.parseInt(numOfTimesToRetry));
		serverHL7.setNumSecsToWait(Integer.parseInt(numSecsToWait));
		(new Thread(serverHL7)).start();

		return context;
	}

}
