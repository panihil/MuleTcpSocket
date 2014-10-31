package edu.bcw.mule35.server;

import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.apache.log4j.Logger;

import bcw.edu.sockets.Server;

public class ServerHL7 extends Server
{
	int maxConnectAttempts = 10;
	private static final Logger log = Logger.getLogger(ServerHL7.class);

	public ServerHL7(int listeningPort, String inboundConnName)
	{
		super(listeningPort, inboundConnName);

	}

	@Override
	public String inboundRead()
	{
		log.info("inboundReplySocket.isBound(): "
				+ inboundReplySocket.isBound());
		log.info("inboundReplySocket.isClosed(): "
				+ inboundReplySocket.isClosed());

		BufferedReader buffReader = null;
		try
		{
			// Create reader and read string data
			buffReader = new BufferedReader(new InputStreamReader(
					inboundReplySocket.getInputStream()));
			// inboundOutStream = new
			// PrintStream(inboundReplySocket.getOutputStream());
			log.info("connected to inbound's input and output stream: "
					+ inboundReplySocket.getInetAddress());

			StringBuilder text = new StringBuilder();
			char msgTerminatingChar = 0;
			// TODO make ctrlChar a short or something to improve speed
			String ctrlChar = "None Control";

			String tmpStr = "";

			String MSH = "";
			log.info("Waiting to read incoming message");
			log.info("is there something to read? " + buffReader.ready());
			try
			{
				Thread.currentThread();
				// TODO Make property
				Thread.sleep(2000);
			} catch (InterruptedException ie)
			{
			}
			if (buffReader.ready())
			{ // if there is something to read then read it
				while (!ctrlChar.equalsIgnoreCase("FS") && (buffReader != null)
						&& (tmpStr = buffReader.readLine()) != null)
				{
					// TODO make prop
					text.append(tmpStr + System.getProperty("line.separator"));
					// log.info("Current text read:" + text);
					// TODO make prop
					if (tmpStr.contains("MSH|^~"))
						MSH = tmpStr;

					// TODO array length check first?
					msgTerminatingChar = tmpStr.charAt(tmpStr.length() - 1);

					if (msgTerminatingChar == this.FS)
					{

						text.trimToSize();
						dbWrite(text.toString());
						/*
						 * wtf(text.toString(), new File(inboundMsgsDir +
						 * File.separator + inboundConnName + "-" +
						 * MSH.split("\\|")[9] + ".msg"));
						 */
						ctrlChar = "FS";

						log.info("returning HL7 message");
						return text.toString();
					}
				} // end while
			} // end if (buffReader.ready())

		} catch (IOException e)
		{
			try
			{
				log.info("Closing inbound because failed to read message");
				inboundClose();
			} catch (IOException ioe)
			{
				return "Unable to close() in Server.read() " + e.getMessage();
			}
		} // end main try/catch
		return "Nothing read";
	}

	private void dbWrite(String string)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void run()
	{

		int numOfAttempts = 0;
		while (numOfAttempts < this.maxConnectAttempts)
		{
			try
			{
				while (true)
				{
					/*
					 * log.info("About to check outboundSocket"); if
					 * (outboundSocket == null || outboundSocket.isBound() ==
					 * false || outboundSocket.isClosed()) {
					 * log.info("About to open outbound connection");
					 * outboundConnect(); log.info("Outbound connected: " +
					 * this.outboundIPName + ":" + this.outboundPort); }
					 * log.info("Outbound is connected to " +
					 * this.outboundIPName + ":" + this.outboundPort);
					 */
					if (inboundReplySocket == null
							|| inboundReplySocket.isBound() == false
							|| inboundReplySocket.isClosed())
					{
						inboundOpen();
						log.info("Inbound opened: " + this.inboundPort);
					}
					log.info("Inbound is listening on: " + this.inboundPort);

					String read = inboundRead();

					if (read.equalsIgnoreCase("NOTHING READ"))
					{
						inboundClose();
						log.info("No more messages from sender!");
					}
					/*
					 * else { log.info("About to call processHL7()"); String
					 * ackReplyFromOutbound = processHL7(this.archiveMsgsDir); }
					 */

				}
			} catch (SocketException se)
			{
				log.fatal("run() SocketException:" + se.getMessage());
			} catch (IOException ioe)
			{
				log.fatal("run() IOException:" + ioe.getMessage());
			} // end main try/catch
		}
		System.exit(1);
	}

	public int getMaxConnectAttempts()
	{
		return maxConnectAttempts;
	}

	public void setMaxConnectAttempts(int maxConnectAttempts)
	{
		this.maxConnectAttempts = maxConnectAttempts;
	}

	@Override
	public String outboundRead() throws IOException
	{
		return null;
	}

	@Override
	protected String readMsg(File file)
	{
		/*
		 * String msg=""; try { BufferedReader in = new BufferedReader(new
		 * FileReader(file)); String temp; while ((temp = in.readLine()) !=
		 * null) msg += temp + "\r"; in.close(); } catch (FileNotFoundException
		 * fnf) { log.fatal( "readMsg(File file):" + fnf.getMessage() );
		 * log.fatal( "Exiting" ); System.exit(1); } catch (IOException ioe) {
		 * log.fatal( "readMsg(File file):" + ioe.getMessage() ); log.fatal(
		 * "Exiting" ); System.exit( 1 ); }
		 * 
		 * return msg;
		 */
		return null;
	}
	/*
	 * private String sendMsg(String msg) { log.info("Sending outbound:" + msg);
	 * try { outboundWrite(msg); } catch (IOException e1) { // TODO
	 * Auto-generated catch block e1.printStackTrace(); }
	 * 
	 * String strOutboundReplyMsg = ""; int curNumOfRetry = 0; while
	 * (strOutboundReplyMsg.trim().isEmpty()) {
	 * log.info("Waiting to get a reply message"); try { strOutboundReplyMsg =
	 * outboundRead(); } catch (IOException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); } // strOutboundReplyMsg = this.VT + //
	 * "MSH|^~\\&|SPACES|CERNERHUB|||20140122095033||ACK|7183288581|P|2.4\rMSA|AR|0|Unknown exception was found\r"
	 * // + this.FS; log.info("here's the reply message:" +
	 * strOutboundReplyMsg); log.info("is reply empty?" +
	 * strOutboundReplyMsg.trim().isEmpty()); log.info("curNumOfRetry:" +
	 * curNumOfRetry); log.info("Param: " + numSecsToWait +
	 * " secs before resending message");
	 * 
	 * if (strOutboundReplyMsg.trim().isEmpty()) { log.info("Waiting for " +
	 * numSecsToWait + " secs before resending message"); try {
	 * Thread.sleep(numSecsToWait * 1000); } catch (InterruptedException ie) {
	 * System.out.println(ie); } try { outboundClose(); outboundConnect();
	 * outboundWrite(msg); } catch (IOException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); } } ++curNumOfRetry; }
	 * 
	 * log.info("Done sending HL7 outbound"); return strOutboundReplyMsg; }
	 */
	/*
	 * private String processHL7(String archiveDir) throws IOException { String
	 * strOutboundReplyMsg = "";
	 * 
	 * String srcDir = this.inboundMsgsDir;
	 * 
	 * String[] msgFileNames = (new File(srcDir)).list(); msgFileNames =
	 * sortFileNames(msgFileNames);
	 * 
	 * log.info("About to read through " + Arrays.toString(msgFileNames)); for
	 * (int i = 0; i < msgFileNames.length; ++i) { String msg = readMsg(new
	 * File(srcDir + File.separator + msgFileNames[i])); String msh =
	 * msg.split("\r")[0];
	 * 
	 * int obrBeginPos = msg.indexOf("\rOBR|"); log.info("OBR beg:" +
	 * obrBeginPos); int obrEndPos = msg.indexOf("\r", obrBeginPos + 1); String
	 * obr = msg.substring(obrBeginPos, obrEndPos); log.info("OBR:" + obr);
	 * String obr4_3 = ""; String[] obr4 = obr.split("\\|")[4].split("\\^");
	 * 
	 * if (obr4.length > 2) obr4_3 = obr4[2];
	 * 
	 * msg = this.VT + msg.trim() + this.FS + "\r";
	 * 
	 * // String pingReplyMsg = outboundRead(); //ping the outbound before // we
	 * write to it
	 * 
	 * if (!isOutboundOK()) { log.info("Need to rest outbound connection"); try
	 * { outboundConnect(); } catch (IOException e) {
	 * log.fatal("Unable to establish outbound connection"); } }
	 * 
	 * strOutboundReplyMsg = sendMsg(msg);
	 * 
	 * Date curDate = new Date(); SimpleDateFormat sdf = new
	 * SimpleDateFormat("yyyyMMddHHmmss"); String curDT = sdf.format(curDate);
	 * 
	 * // create file to store outgoing message for troubleshooting String
	 * sentFileName = msgFileNames[i]; sentFileName =
	 * sentFileName.split("\\.")[0] + "-" + curDT + "-sent." +
	 * sentFileName.split("\\.")[1];
	 * log.info("About to copy/convert message in " + msgFileNames[i] + " to " +
	 * sentFileName); wtf(msg, new File(srcDir + File.separator +
	 * sentFileName));
	 * 
	 * log.info("About to archive " + msgFileNames[i] + " and " + sentFileName);
	 * File srcFile = new File(srcDir + File.separator + msgFileNames[i]); File
	 * archiveFile = new File(archiveDir + File.separator +
	 * msgFileNames[i].split("\\.")[0] + "-" + curDT + "." +
	 * msgFileNames[i].split("\\.")[1]);
	 * 
	 * boolean move = srcFile.renameTo(archiveFile); int moveAttempts = 10;
	 * while (move != true) { if (moveAttempts < 1) { log.info(
	 * "Could not archive.  Please look into issue and restart this service");
	 * System.exit(1); }
	 * 
	 * move = srcFile.renameTo(archiveFile); log.info("Could not archive " +
	 * srcFile); log.info("Will continue to try"); try { Thread.currentThread();
	 * Thread.sleep(2000); } catch (InterruptedException ie) { } --moveAttempts;
	 * }
	 * 
	 * srcFile = new File(srcDir + File.separator + sentFileName); archiveFile =
	 * new File(archiveDir + File.separator + sentFileName); move =
	 * srcFile.renameTo(archiveFile); moveAttempts = 10; while (move != true) {
	 * if (moveAttempts < 1) { log.info(
	 * "Could not archive.  Please look into issue and restart this service");
	 * System.exit(1); }
	 * 
	 * move = srcFile.renameTo(archiveFile); log.info("Could not archive " +
	 * srcFile); log.info("Will continue to try"); try { Thread.sleep(2000); }
	 * catch (InterruptedException ie) { } --moveAttempts; }
	 * 
	 * inboundReplyStr = strOutboundReplyMsg;
	 * 
	 * log.info("Send reply message to sender:" + inboundReplyStr);
	 * inboundOutStream.print(inboundReplyStr);
	 * log.info("Reply message received by sender");
	 * 
	 * strOutboundReplyMsg = ""; log.info("Done reading " + msgFileNames[i]); }
	 * // end main loop through msgFileNames array
	 * 
	 * return strOutboundReplyMsg; // last ACK message from outbound
	 * 
	 * }
	 * 
	 * private String[] sortFileNames(String[] a) {
	 * 
	 * for (int i = 0; i < a.length; ++i) { for (int j = 0; j < a.length - 1;
	 * ++j) { String nextStr = a[j + 1].split("-")[1].split("\\.")[0]; // i.e.
	 * // CernerInbound-Q17614684T17946685.msg String curStr =
	 * a[j].split("-")[1].split("\\.")[0];
	 * 
	 * long nextFileSeqNum = Long.valueOf(nextStr.replaceAll( "[^0-9\\s]", ""));
	 * long curFileSeqNum = Long.valueOf(curStr.replaceAll( "[^0-9\\s]", ""));
	 * 
	 * if (nextFileSeqNum < curFileSeqNum) { String temp = a[j + 1]; a[j + 1] =
	 * a[j]; a[j] = temp; } } }
	 * 
	 * return a; }
	 */

}
