package com.steven.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;

/**
 * 将打印数据排版转换成字符串
 * 板式是以73*21的RFID标签纸为基础的模板
 * @author Windows
 */
public class RFIDWebUtil {

	//标签格式以^XA开始
    private String begin = "^XA ^JMA^LL160^PW584^MD30^PR2^PON^LRN^LH10,0";     
    //标签格式以^XZ结束
    private String end = "^XZ"; 
    //标签内容
    private String content = "";
    private static byte[] dotFont; 
    
    public RFIDWebUtil() {
		super();
		//加载字体
		File file = new File(RFIDWebUtil.class.getResource("").getPath()+"../../../ts24.lib");
    	if(file.exists()){
    		FileInputStream fis = null;
    		try{
    			fis = new FileInputStream(file);
    			
    			dotFont = new byte[fis.available()]; 
    			fis.read(dotFont);
    			
    			fis.close();
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
    	}else{
    		System.out.println("字体库文件不存在！");
    	}
	}

	/**
     * 设置RFID嵌入数据
     * @param str 需要嵌入RFID的数据
     */
    public void setRFID(String str){
    	//采用16进制
    	content += "^RS8^RFW,H^FD"+str+"^FS";
    	
    	//采用EPC
    	//content += "^RB64,2,20,18,24^RFw,E^FD01,02,03,0F^FS";
    }
    
    /** 
     * 设置条形码 
     * @param barcode 条码字符 
     * @param zpl 条码样式模板 
     */   
    public void setBarcode(String barcode,String zpl) {  
        content += zpl.replace("${data}", barcode);  
    }
    
    /** 
     * 中文字符、英文字符(包含数字)混合 
     * @param str 中文、英文 
     * @param x x坐标 
     * @param y y坐标 
     * @param eh 英文字体高度height 
     * @param ew 英文字体宽度width 
     * @param es 英文字体间距spacing 
     * @param mx 中文x轴字体图形放大倍率。范围1-10，默认1 
     * @param my 中文y轴字体图形放大倍率。范围1-10，默认1 
     * @param ms 中文字体间距。24是个比较合适的值。 
     */  
    public void setText(String str, int x, int y, int eh, int ew, int es, int mx, int my, int ms) {  
        byte[] ch = str2bytes(str);  
        for (int off = 0; off < ch.length;) { 
        	//中文字符
            if (((int) ch[off] & 0x00ff) >= 0xA0) {  
                int qcode = ch[off] & 0xff;  
                int wcode = ch[off + 1] & 0xff;  
                content += String.format("^FO%d,%d^XG0000%01X%01X,%d,%d^FS\n", x, y, qcode, wcode, mx, my);  
                begin += String.format("~DG0000%02X%02X,00072,003,\n", qcode, wcode);  
                qcode = (qcode + 128 - 32) & 0x00ff;  
                wcode = (wcode + 128 - 32) & 0x00ff;  
                int offset = ((int) qcode - 16) * 94 * 72 + ((int) wcode - 1) * 72;  
                for (int j = 0; j < 72; j += 3) {  
                    qcode = (int) dotFont[j + offset] & 0x00ff;  
                    wcode = (int) dotFont[j + offset + 1] & 0x00ff;  
                    int qcode1 = (int) dotFont[j + offset + 2] & 0x00ff;  
                    begin += String.format("%02X%02X%02X\n", qcode, wcode, qcode1);  
                }  
                x = x + ms * mx;  
                off = off + 2;  
            } else if (((int) ch[off] & 0x00FF) < 0xA0) {  
                setChar(String.format("%c", ch[off]), x, y, eh, ew);  
                x = x + es;  
                off++;  
            }  
        }  
    }
    
    /** 
     * 英文字符串(包含数字) 
     * @param str 英文字符串 
     * @param x x坐标 （单位均为：dpi）
     * @param y y坐标 
     * @param h 高度 
     * @param w 宽度 
     */  
    public void setChar(String str, int x, int y, int h, int w) {  
        content += "^FO" + x + "," + y + "^A0," + h + "," + w + "^FD" + str + "^FS";  
    }  
    
    /** 
     * 英文字符(包含数字)顺时针旋转90度 
     * @param str 英文字符串 
     * @param x x坐标 
     * @param y y坐标 
     * @param h 高度 
     * @param w 宽度 
     */ 
    public void setCharR(String str, int x, int y, int h, int w) {  
        content += "^FO" + x + "," + y + "^A0R," + h + "," + w + "^FD" + str + "^FS";  
    } 
    
    /** 
     * 获取完整的ZPL 
     * @return 返回条形码完整模板
     */ 
    public String getZpl() {  
        return begin + content + end;  
    } 
    
    /** 
     * 重置ZPL指令，当需要打印多张纸的时候需要调用 
     */  
    public void resetZpl() {  
        begin = "^XA ^JMA^LL160^PW584^MD30^PR2^PON^LRN^LH10,0";  
        setTag(160, 584, 25, 2, 10, 0);
        end = "^XZ";  
        content = "";  
    } 
    
    /**
     * 设置标签纸张数据
     * @param ll	标签长度（纵向，相当于高度）,单位为点数（dpi）	,默认160
     * @param pw 标签宽度（横向），默认583
     * @param md 打印浓度，-30 至 30，在配置标签上的值进行加减，默认30
     * @param v 打印速度，1~14（表示1~14英寸/s）
     * @param x	标签起点x轴
     * @param y 标签起点y轴
     */
    public void setTag(int ll, int pw, int md, int v,int x, int y){
    	begin = "^XA ^JMA^LL"+ll;
    	begin += "^PW"+pw;
    	begin += "^MD"+md;
    	begin += "^PR"+v;
    	begin += "^PON^LRN";
    	begin += "^LH"+x+","+y;
    }
    
    /**
     * 打印标签数量
     * @param printCount 打印数量
     */
    public void printCount(int printCount){
    	content += "^PQ"+printCount+",0,1,Y";
    }
    
    /** 
     * 字符串转byte[] 
     * @param s 待转换的字符串
     * @return 
     */ 
    private byte[] str2bytes(String str) {  
        if (null == str || "".equals(str)) {  
            return null;  
        }  
        
        byte[] result = null;  
        try {  
        	result = str.getBytes("gb2312");  
        } catch (UnsupportedEncodingException ex) {  
            ex.printStackTrace();  
        }  
        return result;  
    }
    
}
