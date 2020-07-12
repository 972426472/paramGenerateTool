package org.unique.generator.ui;

import java.awt.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

import org.apache.poi.ss.usermodel.*;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

/**
 * author 张作栋
 */
public class MainFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 6636098005177941822L;
	private JPanel contentPane;
	private JTextField txt_url;
	private JTextField txt_url2;
	private JTextField txt_url3;
	private JCheckBox boldCheckbox;

	/** UIManager中UI字体相关的key */
	public static String[] DEFAULT_FONT = new String[] { "Table.font", "TableHeader.font", "CheckBox.font",
			"Tree.font", "Viewport.font", "ProgressBar.font", "RadioButtonMenuItem.font", "ToolBar.font",
			"ColorChooser.font", "ToggleButton.font", "Panel.font", "TextArea.font", "Menu.font", "TableHeader.font",
			"TextField.font", "OptionPane.font", "MenuBar.font", "Button.font", "Label.font", "PasswordField.font",
			"ScrollPane.font", "MenuItem.font", "ToolTip.font", "List.font", "EditorPane.font", "Table.font",
			"TabbedPane.font", "RadioButton.font", "CheckBoxMenuItem.font", "TextPane.font", "PopupMenu.font",
			"TitledBorder.font", "ComboBox.font" };
	
	private final Object[] options = {" 确定 "," 取消 "};


	JButton button1 = new JButton("选择导入文件");
	JButton button2 = new JButton("选择输出路径");

	JFileChooser jfc = new JFileChooser();
	JLabel label4 = new JLabel();
	JLabel label5 = new JLabel();
	JButton button5 = new JButton("扫描");
	JTextArea text_result = new JTextArea();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
					UIManager.put("RootPane.setupButtonVisible", false);
					//设置本属性将改变窗口边框样式定义
					BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.osLookAndFeelDecorated;
					org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
					// 调整默认字体
					for (int i = 0; i < DEFAULT_FONT.length; i++) {
						UIManager.put(DEFAULT_FONT[i], new Font("微软雅黑", Font.PLAIN, 13));
					}
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		init();
	}
	
	/**
	 * 初始化布局
	 */
	void init(){
		setTitle("通讯区参数类生成工具 v1.0 by 汉克研发团队");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btn_gener = new JButton("代码生成");
		btn_gener.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				text_result.setText("");
				if(validation()){
					try {

						String url1 = txt_url.getText();//导入文件路径
						String url2 = txt_url2.getText();//生成代码路径
						Map<String,List<List<String>>> map = new HashMap<>();
						boolean ret  = readExcel(boldCheckbox.isSelected(),url1,map);
						if(ret) {
							Map<String,String> classMap = new HashMap<>();
							boolean r = generate(boldCheckbox.isSelected(),map,classMap);
							if(r){
								CodeGenerator(classMap,url2);
							}else{
								showError("操作失败！");
							}
							/*showInfo("操作成功！");*/
						}else {
							showError("操作失败！");
						}
					}catch (Exception e2) {
						e2.printStackTrace();
						showError(e2.getMessage());
						return;
					}
					
				}
			}
		});
		btn_gener.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
		btn_gener.setBounds(300, 615, 95, 25);
		contentPane.add(btn_gener);
		JLabel lblUrl = new JLabel("文件路径：");
		lblUrl.setVerticalAlignment(SwingConstants.BOTTOM);
		lblUrl.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUrl.setBounds(10, 15, 90, 15);
		JLabel lblUrl2 = new JLabel("生成路径：");
		lblUrl2.setVerticalAlignment(SwingConstants.BOTTOM);
		lblUrl2.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUrl2.setBounds(10, 65, 90, 15);
		JLabel lblUrl3 = new JLabel("服务英文名：");
		lblUrl3.setVerticalAlignment(SwingConstants.BOTTOM);
		lblUrl3.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUrl3.setBounds(10, 115, 90, 15);
		JLabel lblUrl4 = new JLabel("<html>使用说明：通讯区文档第一个sheet通常为报文头没有实际解析意义，所以程序从第二个sheet开始读取数据。sheet名称严格遵守“输入-报文体-XXXX”" +
				"、“输出-报文体-XXXX”。 <span style='color:red'>XXXX为通讯区英文名称</span>，(公共通讯区除外)程序会自动屏蔽掉公共通讯区如：“appstatv10”等，所以文档中公共通讯区不必做处理。单个sheet内需包括“变量名称”、" +
				"“类型”、“是否必输”、“长度”、“描述”、“数据字典”等，前后顺序无需刻意调整，程序自动识别。其中，输入通讯区中：变量名、是否必输、长度为必输项。输出通讯区中：变量名、长度为必输项" +
				"。“是否必输”可填写：是、否、true、false其中之一且不用区分大小写。当勾掉“所有字段都为String”选项时，所有通讯区“类型”都为必输项，第一行为标题，程序从第二行开始解析数据。<span style='color:red'>当非必填项为空单元格，需以单个空格填充，否则会解析错误！</span>" +
				"服务英文名和通讯区名都已做首字母大写处理，最终生成文件为：Input/Output+服务英文名+通讯区.java。</html>");
		lblUrl4.setVerticalAlignment(SwingConstants.BOTTOM);
		lblUrl4.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUrl4.setBounds(25, 160, 650, 170);
		label4.setBounds(10, 70, 700, 300);
		label5.setBounds(100, 70, 1000, 20);
		button1.setBounds(530, 15, 100, 20);
		button1.addActionListener(this);
		button2.setBounds(530, 65, 100, 20);
		text_result.setVisible(true);
		//text_result.setBounds(110, 250, 550, 100);
		text_result.setAutoscrolls(true);
		text_result.setLineWrap(false);
		text_result.setEditable(false);
		JScrollPane scroll = new JScrollPane(text_result);
		scroll.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setBounds(25, 340, 650, 260);
		contentPane.add(button2);
		contentPane.add(scroll);
		contentPane.add(lblUrl2);
		contentPane.add(lblUrl3);
		contentPane.add(lblUrl4);
		button2.addActionListener(this);


		contentPane.add(lblUrl);
		button5.addActionListener(this);
		label5.setBounds(100, 70, 1000, 20);

		contentPane.add(button1);
		boldCheckbox = new JCheckBox("所有字段类型都设为String",true);
		boldCheckbox.setBounds(110,150,300,20);
		contentPane.add(jfc);
		contentPane.add(label4);
		contentPane.add(label5);

		contentPane.add(boldCheckbox);
		contentPane.add(button5);
		txt_url = new JTextField();
		txt_url.setText("");
		txt_url.setColumns(10);
		txt_url.setBounds(110, 12, 400, 25);
		txt_url.setEditable(false);
		contentPane.add(txt_url);
		txt_url2 = new JTextField();
		txt_url2.setText("");
		txt_url2.setColumns(10);
		txt_url2.setBounds(110, 62, 400, 25);
		txt_url2.setEditable(false);
		contentPane.add(txt_url2);
		txt_url3 = new JTextField();
		txt_url3.setText("");
		txt_url3.setColumns(10);
		txt_url3.setBounds(110, 112, 400, 25);

		contentPane.add(txt_url3);
		
		JSeparator separator = new JSeparator();//虚线
		separator.setBounds(10, 600, 690, 2);
		contentPane.add(separator);
		
		

		setResizable(false);
		setLocationRelativeTo(null);
	}

	private void CodeGenerator(Map<String, String> classMap,String url2)throws Exception {
		for(Map.Entry<String, String> a:classMap.entrySet()){
			BufferedWriter bw = null;
			FileWriter fileWriter;
			try {
					String className = a.getKey();
					String classTxt = a.getValue();
					File file = new File(url2+"\\"+className+".java");
					if(!file.exists()){
						file.createNewFile();
					}
					fileWriter = new FileWriter(file.getAbsoluteFile());
					bw = new BufferedWriter(fileWriter);
					bw.write(classTxt);
					text_result("已生成文件:"+file.getAbsolutePath());
				}catch (Exception e){
					throw new Exception(e);
				}finally {
					if(bw!=null){
						bw.close();
					}
				}
		}
	}

	private boolean generate(boolean stringFlag,Map<String, List<List<String>>> map,Map<String,String> classMap) {
		String serviceName = toUpperCase(txt_url3.getText());//服务英文名
		Map<String,String> nameMap = new HashMap<>();
		for(Map.Entry<String, List<List<String>>> a:map.entrySet()){
			String txq = a.getKey();//通讯区名字
			List<List<String>> list = a.getValue();//字段信息
			String type = list.get(0).get(0);//输入or输出
			List filed = list.get(1);//标题
			int fieldIndex = -1;
			int inputFlagIndex = -1;
			int lengthIndex = -1;
			int typeIndex = -1;
			int descIndex = -1;
			int dicIndex = -1;
			int memoIndex = -1;
			//检查字段是否完整
			for(int i=0;i<filed.size();i++){
				if("变量名称".equals(filed.get(i)))fieldIndex=i;
				if("是否必输".equals(filed.get(i)))inputFlagIndex=i;
				if("长度".equals(filed.get(i)))lengthIndex=i;
				if("描述".equals(filed.get(i)))descIndex=i;
				if("数据字典".equals(filed.get(i)))dicIndex=i;
				if("备注".equals(filed.get(i)))memoIndex=i;
				if("类型".equals(filed.get(i)))typeIndex=i;
			}
			if(fieldIndex==-1){text_result("sheet:"+type+"-报文体-"+txq+"缺少字段:“变量名称”");return false;}
			if(lengthIndex==-1){text_result("sheet:"+type+"-报文体-"+txq+"缺少字段:“长度”");return false;}
			if("输入".equals(type)){
				if(inputFlagIndex==-1){text_result("sheet:"+type+"-报文体-"+txq+"缺少字段:“是否必输”");return false;}
			}
			if(!stringFlag){
				if(typeIndex==-1){text_result("sheet:"+type+"-报文体-"+txq+"缺少字段:“类型”");return false;}
			}
			String importTxt = "import com.icbc.ifss.lib.core.engine.parameter.ParameterField;\n" +
					"import com.icbc.ifss.lib.core.engine.parameter.ValueCheckFor;\n" +
					"import com.icbc.ifss.lib.core.engine.visitor.MapDataVerifyMeta;\n\n";
			String className = ("输入".equals(type)?"Input":"Output")+serviceName+toUpperCase(txq);
			String classTxtTitle = "public class "+className+" extends MapDataVerifyMeta { \n";
			String fieldTxt = "";
			String classTxtTail = "\n}";
			//开始从二行正文解析
			for(int i=2;i<list.size();i++){
				List<String> l = list.get(i);
				if("".equals(l.get(fieldIndex))){text_result("sheet:"+type+"-报文体-"+txq+"第"+i+"行必输项:“变量名称”为空");return false;}
				if("".equals(l.get(lengthIndex))){text_result("sheet:"+type+"-报文体-"+txq+"第"+i+"行必输项:“长度”为空");return false;}
				if("输入".equals(type)&&"".equals(l.get(inputFlagIndex))){text_result("sheet:"+type+"-报文体-"+txq+"第"+i+"行必输项:“是否必输”为空");return false;}
				if(!stringFlag&&"".equals(l.get(typeIndex))){text_result("sheet:"+type+"-报文体-"+txq+"第"+i+"行必输项:“类型”为空");return false;}
				//for(int j=0;j<l.size();j++){
					StringBuffer filedStr = new StringBuffer("\n\t/**"+"\n");
					filedStr.append("\t*  描述:" + (descIndex != -1 ? l.get(descIndex) : "")+"\n");
					filedStr.append("\t*  数据字典:"+(dicIndex!=-1?l.get(dicIndex):"")+"\n");
					filedStr.append("\t*  备注:"+(memoIndex!=-1?l.get(memoIndex):"")+"\n");
					filedStr.append("\t*/\n");
					boolean bol = false;//是否必输
					if("输入".equals(type)){
						if(!("是".equals(l.get(inputFlagIndex))||"true".equals(l.get(inputFlagIndex).toLowerCase())||
								"否".equals(l.get(inputFlagIndex))||"false".equals(l.get(inputFlagIndex).toLowerCase()))){
							text_result("sheet:"+type+"-报文体-"+txq+"第"+i+"行必输项:“是否必输”不合法");return false;
						}
						if("是".equals(l.get(inputFlagIndex))||"true".equals(l.get(inputFlagIndex).toLowerCase())){
							bol = true;
						}
					}
					filedStr.append("\t@ParameterField(fieldName = \""+l.get(fieldIndex)+"\", checkRule = @ValueCheckFor (mustInput = "+bol+" ," +
							" canBeEmpty = "+!bol+" ,maxCharsLength = "+l.get(lengthIndex)+"))\n");
					filedStr.append("\tpublic "+(stringFlag?"String":l.get(typeIndex))+" "+l.get(fieldIndex)+";\n");
				//}
				fieldTxt+=filedStr.toString();
			}

			System.out.println(importTxt+classTxtTitle+fieldTxt+classTxtTail);
			classMap.put(className,importTxt+classTxtTitle+fieldTxt+classTxtTail);
			nameMap.put(txq,className);
			text_result("已解析参数类:"+className+".java" );
		}
		//2.生成参数类的集合类
		StringBuffer inputTxt = new StringBuffer("import com.icbc.ifss.ats.service.impl.ebc.para.EbankRequestWithHeader;\n" +
				"import com.icbc.ifss.lib.engine.ebankresp.EbankResponse;\n" +
				"import com.icbc.core.lib.parameter.ParameterField;\n\n" +
				"public class "+serviceName+"Parameters {");
		inputTxt.append("\n\tpublic static class ReqDataAts extends EbankRequestWithHeader {\n");
		StringBuffer outputTxt = new StringBuffer("\n\tpublic static class RespDataAts extends EbankResponse {\n");
		for(Map.Entry<String, String> a:nameMap.entrySet()){
			String fieldName = a.getKey();
			String fieldType = a.getValue();
			if(fieldType.startsWith("Input")){
				inputTxt.append("\n\t\t@ParameterField( fieldName = \""+fieldName+"\" )\n");
				inputTxt.append("\t\tpublic "+fieldType+" "+("public".equals(fieldName)?"publicField":fieldName)+";\n");
			}
			if(fieldType.startsWith("Output")){
				outputTxt.append("\n\t\t@ParameterField( fieldName = \""+fieldName+"\" )\n");
				outputTxt.append("\t\tpublic "+fieldType+" "+("public".equals(fieldName)?"publicField":fieldName)+";\n");
			}
		}
		classMap.put(serviceName+"Parameters",inputTxt+"\n\t}\n\n"+outputTxt+"\n\t}\n\n"+"}");
		text_result("已解析参数集合类:"+serviceName+"Parameters"+".java");
		return true;
	}


	/**
	 * 验证是否为空
	 * @return
	 */
	public boolean validation(){
		text_result.setText("");
		if(isEmpty(txt_url)){
			showWarning("请选择导入文件");

			return false;
		}
		if(isEmpty(txt_url2)){
			showWarning("请选择代码输出路径");

			return false;
		}
		if(isEmpty(txt_url3)){
			showWarning("请输入服务英文名");

			return false;
		}

		return true;
	}

	/**
	 * 判断输入框是否为空
	 * @param component
	 * @return
	 */
	public boolean isEmpty(JTextComponent component){
		String content = component.getText();
		return null == content || content.equals("");
	}
	/**
	 * 判断输入框是否为空
	 * @param
	 * @return
	 */
	public boolean isEmpty(String content){
		return null == content || content.equals("");
	}



	/**
	 * 错误提示
	 * @param
	 * @param msg
	 */
	public void showError(String msg){
		JOptionPane.showMessageDialog(null, msg, "错误", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * 警告提示
	 * @param msg
	 */
	public void showWarning(String msg){
		JOptionPane.showMessageDialog(null, msg, "警告", JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * 信息提示
	 * @param
	 * @param msg
	 */
	public void showInfo(String msg){
		JOptionPane.showMessageDialog(null, msg, "信息", JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(button1)) {
			label4.setText("");
			jfc.setFileSelectionMode(2);
			int state = jfc.showOpenDialog(null);
			if (state == 1) {
				return;
			} else {
				File f = jfc.getSelectedFile();
				txt_url.setText(f.getAbsolutePath());
			}
		}

		if (e.getSource().equals(button2)) {
			label4.setText("");
			jfc.setFileSelectionMode(1);
			int state = jfc.showOpenDialog(null);
			if (state == 1) {
				return;
			} else {
				File f = jfc.getSelectedFile();
				txt_url2.setText(f.getAbsolutePath());
			}
		}

		if (e.getSource().equals(button5)) { // 导入
			File file = new File(txt_url.getText());
			if (txt_url.getText().trim().equals("")) {
				label4.setText("请选择文件路径！");
			} else {
				//showFiles(file);
				label4.setText("文件扫描完成!");
				label5.setText("");
			}
		}

	}
	private Boolean  readExcel(boolean b,String url1,Map<String,List<List<String>>> map) throws IOException {

		File xlsFile = new File(url1);
		// 获得工作簿
		Workbook workbook = WorkbookFactory.create(xlsFile);
		// 获得工作表个数
		int sheetCount = workbook.getNumberOfSheets();
		// 遍历工作表从第二个sheet开始
		text_result("正在解析,已自动屏蔽公共通讯区appstatv10、infocommv10、chancommv10");
		for (int i = 1; i < sheetCount; i++)
		{
			List list = new ArrayList();
			List<List<String>> lists = new ArrayList<>();

			Sheet sheet =  workbook.getSheetAt(i);
			String sheetName = sheet.getSheetName().trim();

			if(sheetName.contains("appstatv10")||sheetName.contains("infocommv10")||sheetName.contains("chancommv10")){//屏蔽公共通讯区

				continue;
			}
			if(!sheetName.startsWith("输入-报文体-")&&!sheetName.startsWith("输出-报文体-")){
				showError("第"+(i+1)+"个sheet名字不合法");
				text_result("第"+(i+1)+"个sheet名字不合法");
				return false;
			}
			String txqName = sheetName.substring(7);
			String flag = sheetName.substring(0,2);
			List <String> f = new ArrayList<>();
			f.add(flag);
			lists.add(f);
			// 获得行数
			int rows = sheet.getLastRowNum() + 1;
			// 读取数据
			for (int row = 0; row < rows; row++)
			{
			 	Row r = sheet.getRow(row);
				//Row r = sheet.getRow(row);
				//从第一行开始先获取字段名及顺序,主要记录变量名称,类型,是否必输,长度,描述,数据字典等
				if(row==0){
					for (int j=0;j<r.getPhysicalNumberOfCells();j++) {
						Cell cell = r.getCell(j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
						if(null==cell){
							list.add("");
						}else{
							cell.setCellType(CellType.STRING);
							String value = cell.getStringCellValue().trim();
							list.add(value);
						}
					}
					lists.add(list);
					continue;
				}

				if(r.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)==null){
					break;
				}
				List <String> l = new ArrayList<>();
				//第二行开始
				for (int j=0;j<r.getPhysicalNumberOfCells();j++) {
					Cell cell = r.getCell(j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

					if(null==cell){
						l.add("");
					}else{
						cell.setCellType(CellType.STRING);
						String value = cell.getStringCellValue().trim();
						l.add(value);
					}

				}
				/*for (Cell cell : r) {
					cell.setCellType(CellType.STRING);
					String value = cell.getStringCellValue().trim();
					l.add(value);
				}*/
				lists.add(l);


			}
			map.put(txqName,lists);
		}

		return true;
	}

	void text_result(String str){
		//new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()))+" "+
		text_result.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()))+" "+str+"\n");
	}
	//返回首字母大写
	private String toUpperCase(String  str){
		return str.substring(0,1).toUpperCase().concat(str.substring(1));
	}
}
