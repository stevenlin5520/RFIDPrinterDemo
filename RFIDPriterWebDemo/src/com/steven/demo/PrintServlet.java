package com.steven.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.TcpConnection;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;

/**
 * Servlet implementation class PrintServlet
 */
@WebServlet("/PrintServlet")
public class PrintServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PrintServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		System.out.println("调用deGet方法...");
		
		String printStr = model(new RFIDWebUtil(), "0100000000000000000023EF", "AD钙奶");
		
		// 通过命令打印
//		byCommandPrint(printStr);
//		byCommandPrint2(printStr);
		// 通过文件打印
		byFilePrint(printStr);

		response.getWriter().append("Served at: ").append(request.getContextPath()).append("打印成功！");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	/**
	 * 打印机模板,返回ZPL命令 打印的RFID由商品RFID信息拼上商品表中打印数量
	 * @param rfid 商品RFID信息
	 * @param text 商品名称
	 * @return printStr 打印数据转换成命令
	 */
	public static String model(RFIDWebUtil printer, String rfid, String text) {
		if (printer == null) {
			printer = new RFIDWebUtil();
		} else {
			printer.resetZpl();
		}
		// 设置标签纸张数据
		printer.setTag(160, 584, 25, 2, 10, 0);

		// 条码样式模板 （条形码）
		String barZpl = "^FO20,50^BY2,2.0,56^BAN,56,Y,N,N^FD${data}^FS";
		printer.setBarcode(rfid, barZpl);
		// 设置文本信息
		printer.setText(text, 20, 138, 32, 24, 14, 1, 1, 24);
		// 嵌入RFID数据
		printer.setRFID(rfid);

		// 设置打印数量
		printer.printCount(1);

		// 打印
		System.out.println(printer.getZpl());
		return printer.getZpl();
	}

	/**
	 * 使用命令通过连接进行打印
	 * @param printStr ZPL命令
	 */
	public void byCommandPrint(String printStr) {
		Connection tcpConnection = null;
		try {
			tcpConnection = new TcpConnection("192.168.1.67", 6101);
			// 新建网络连接
			tcpConnection.open();
			tcpConnection.write(printStr.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过连接到打印机端口，发送命令进行打印
	 * @param printStr ZPL命令
	 */
	public void byCommandPrint2(String printStr) {
		Connection tcpConnection = null;
		try {
			tcpConnection = new TcpConnection("231q021j08.imwork.net",37789);
			// 新建网络连接
			tcpConnection.open();

			// 新建网络打印机
			ZebraPrinter printer = ZebraPrinterFactory.getInstance(tcpConnection);
			System.out.println("ZPL_Command:" + printStr);
			// 打印命令
			printer.sendCommand(printStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过连接到打印机端口，创建临时文件存储ZPL命令，发送到打印机进行打印
	 * @param printStr ZPL命令
	 */
	public void byFilePrint(String printStr) {
		Connection tcpConnection = null;
		try {
			tcpConnection = new TcpConnection("192.168.1.67", 6101);
			// 新建网络连接
			tcpConnection.open();

			// 新建网络打印机
			ZebraPrinter printer = ZebraPrinterFactory.getInstance(tcpConnection);
			// 打印文件
			String filePath = createDemoFile(printer.getPrinterControlLanguage(), printStr);
			printer.sendFileContents(filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 创建临时文件存储ZPL命令
	 * @param pl 命令的类型
	 * @param zpl 需要输入到文件的命令
	 * @return 临时文件路径
	 * @throws IOException
	 */
	private static String createDemoFile(PrinterLanguage pl,String zpl) throws IOException {

        File tmpFile = File.createTempFile("TEST_ZEBRA", "LBL");
        FileOutputStream os = new FileOutputStream(tmpFile);

        byte[] configLabel = null;

        if (pl == PrinterLanguage.ZPL) {
            configLabel = zpl.getBytes();
        } 
        os.write(configLabel);
        os.flush();
        os.close();
        return tmpFile.getAbsolutePath();
    }
}
