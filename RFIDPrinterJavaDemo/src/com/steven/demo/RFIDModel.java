package com.steven.demo;

public class RFIDModel {

	public static void main(String[] args) {
		
		model(new RFIDPrinter(),"Zebra ZD500R (203 dpi)");
	}
	
	/**
	 * 模板打印
	 * @param printer 打印机实例RFIDPrinter
	 * @param printerURI 设备和打印机下的打印机全名
	 */
	public static void model(RFIDPrinter printer,String printerURI){
		//打印机初始化
		printer.init(printerURI);
		//设置标签纸张数据
		printer.setTag(160, 583, 25, 2, 10, 0);
		
		//条码样式模板 （条形码）
        String barZpl = "^FO24,50^BY4,2.0,80^B3N,N,80,N,N^FD${data}^FS"; 
        //条码下方数据 
        String bar = "steven";
        printer.setChar(bar, 120, 140, 32, 40);
        printer.setBarcode(bar, barZpl);
        //设置文本信息
        printer.setText("快达新零售", 300, 128, 32, 32, 24, 2, 2, 20);
        //嵌入RFID数据
        printer.setRFID("adbef20181130");
        //设置打印数量
        printer.printCount(1);
        //打印
        boolean result = printer.print(printer.getZpl());  
        System.out.println(printer.getZpl()); 
	}
}
