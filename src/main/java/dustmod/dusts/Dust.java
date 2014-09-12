package dustmod.dusts;

public class Dust {
	
	private final String id;
	
	private final String name;
	
	private final int primaryColor;
	
	private final int secondaryColor;
	
	private final int floorColor;
	
	private final int lightLevel;
	
	public Dust(String id, String name, int primaryColor, int secondaryColor, int floorColor, int lightLevel) {
		this.id = id;
		this.name = name;
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
		this.floorColor = floorColor;
		this.lightLevel = lightLevel;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public int getPrimaryColor() {
		return primaryColor;
	}
	
	public int getSecondaryColor() {
		return secondaryColor;
	}
	
	public int getFloorColor() {
		return floorColor;
	}
	
	public int getLightLevel() {
		return lightLevel;
	}

}
