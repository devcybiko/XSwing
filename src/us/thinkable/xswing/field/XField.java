package us.thinkable.xswing.field;

public interface XField {

	public String getType();

	public String getTitle();

	public void setTitle(String title);

	public String getValue();

	public void setValue(String value);

	public void remove();

	public void setVisible(boolean visible);

	public boolean canResize();

	public void toFront();

	public void toBack();

}
