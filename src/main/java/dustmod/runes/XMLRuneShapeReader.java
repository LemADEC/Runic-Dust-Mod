package dustmod.runes;

import java.io.InputStream;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import dustmod.DustMod;

public class XMLRuneShapeReader extends DefaultHandler {

	public String currentTag = null;
	public String currentValue = "";

	/**** Rune Properties ****/
	private String displayName = "Error:Unloaded rune";
	private String idName = "error";
	private int id = -1;
	private boolean solid = false;
	private String sacrifices = "Error:Unloaded rune";
	private String description = "Error:Unloaded rune";
	private int width=0, height=0;
	private int ox=0, oy=0;
	private int cx=0, cy=0;
	private int[][][] design = new int[0][0][0];
	private int[] rotationMatrix = new int[8];
	private String author = "Error:Unloaded rune";
	private ArrayList<Integer> allowedVariables = new ArrayList<Integer>();

	private RuneEvent event;
	private String runeFile;
	
	public static void readAndRegisterShape(String runeFile, RuneEvent e){
		new XMLRuneShapeReader(runeFile,e).run();
	}
	
	private XMLRuneShapeReader(String runeFile, RuneEvent e) {
		super();
		this.event = e;
		this.runeFile = runeFile;
	}
	
	private void run(){
		try {
			XMLReader xr = XMLReaderFactory.createXMLReader();
			xr.setContentHandler(this);
			xr.setErrorHandler(this);

			
			InputStream fileStream = this.getClass().getClassLoader()
					.getResourceAsStream(runeFile);

			xr.parse(new InputSource(fileStream));
		} catch (Exception ex) {
			DustMod.logger.error("Unable to read rune XML file! " + runeFile, ex);
			ex.printStackTrace();
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes att) throws SAXException {
		currentTag = localName;

		if ("name".equals(currentTag)) {
			this.displayName = att.getValue("display");
			this.idName = att.getValue("id");
		} else if ("id".equals(currentTag)) {
			String num = att.getValue("value");
			if (num != null && !num.isEmpty())
				this.id = Integer.parseInt(num);
			else
				this.id = -1;
		} else if ("solid".equals(currentTag)) {
			this.solid = Boolean.parseBoolean(att.getValue("value"));
		} else if ("design".equals(currentTag)) {
			String num = att.getValue("width");
			if (num != null && !num.isEmpty())
				this.width = Integer.parseInt(num);
			else
				this.width = -1;

			num = att.getValue("height");
			if (num != null && !num.isEmpty())
				this.height = Integer.parseInt(num);
			else
				this.height = -1;

			num = att.getValue("ox");
			if (num != null && !num.isEmpty())
				this.ox = Integer.parseInt(num);
			else
				this.ox = -1;

			num = att.getValue("oy");
			if (num != null && !num.isEmpty())
				this.oy = Integer.parseInt(num);
			else
				this.oy = -1;

			num = att.getValue("cx");
			if (num != null && !num.isEmpty())
				this.cx = Integer.parseInt(num);
			else
				this.cx = -1;

			num = att.getValue("cy");
			if (num != null && !num.isEmpty())
				this.cy = Integer.parseInt(num);
			else
				this.cy = -1;

			this.design = new int[1][height][width];

		} else if (currentTag.length() == 4 && currentTag.startsWith("rot")) {
			int rot = Integer.parseInt(currentTag.charAt(3) + "");

			String x = att.getValue("x");
			if (x != null && !x.isEmpty())
				this.rotationMatrix[rot * 2] = Integer.parseInt(x);
			else
				this.rotationMatrix[rot * 2] = 0;

			String y = att.getValue("z");
			if (y != null && !x.isEmpty())
				this.rotationMatrix[rot * 2 + 1] = Integer.parseInt(y);
			else
				this.rotationMatrix[rot * 2 + 1] = 0;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if ("description".equals(currentTag)) {
			this.description = currentValue;
		} else if ("sacrifices".equals(currentTag)) {
			this.sacrifices = currentValue;
		} else if ("author".equals(currentTag)) {
			this.author = currentValue.trim().replaceAll("\n", " ");
		} else if ("allowedVariables".equals(currentTag)) {
			String[] splitLine = currentValue.split("\n");
			for (String line : splitLine) {
				String[] splitComma = line.split(",");
				for (String element : splitComma) {
					try {
						allowedVariables.add(Integer.parseInt(element.trim()));
					} catch (NumberFormatException e) {
					}
				}
			}
		} else if ("design".equals(currentTag)) {
			int x = 0, y = 0;

			String[] splitLine = currentValue.split("\n");
			if (splitLine.length != height) {
				DustMod.logger.error("Error reading rune XML file! Design height does not match the specified height! {}", idName);
			} else {
				for (String line : splitLine) {
					String[] splitComma = line.split(",");
					if (splitComma.length != width) {
						DustMod.logger.error("Error reading rune XML file! Design width does not match the specified width! {}", idName);
						break;
					}
					for (String element : splitComma) {
						element = element.trim();
						int val = 0;
						if("N".equals(element)) val = -1;
						else if("P".equals(element)) val = 100;
						else if("G".equals(element)) val = 200;
						else if("L".equals(element)) val = 300;
						else if("B".equals(element)) val = 400;
						else
							try {
								val = Integer.parseInt(element.trim());
								
							} catch (NumberFormatException e) {
							}
						design[0][y][x] = val;
						x++;
					}
					x=0;
					y++;
				}
			}
		}
		currentTag = null;
		currentValue = "";
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		for (int i = start; i < start + length; i++) {
			char c = ch[i];
			if (c != '\t') {
				this.currentValue += c;
			}
		}
		this.currentValue = this.currentValue.trim();
	}

	@Override
	public void endDocument() throws SAXException {
		RuneShape s = new RuneShape(width, height, idName, solid, ox,oy,cx,cy, id);
		s.setData(design);
		s.setRotationMatrix(rotationMatrix);
		s.addAllowedVariable(allowedVariables);
		s.setRuneName(displayName);
		s.setNotes(sacrifices);
		s.setDesc(description);
		s.setAuthor(author);
		
		RuneManager.registerLocalDustShape(s, event);
	}
}
