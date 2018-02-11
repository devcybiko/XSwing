package us.thinkable.xswing.zed;

import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import us.thinkable.xswing.editor.XGraphEditor;

public class XGraphNode {
	private ImageIcon imageIcon;
	private int x;
	private int y;

	public XGraphNode(String iconFname) {
		imageIcon = XUtil.createImageIcon(iconFname);
	}
	public ImageIcon getImageIcon() {
		return imageIcon;
	}

	public void setImageIcon(ImageIcon img) {
		imageIcon = img;
	}

	public void paint(Graphics2D g2) {
		g2.drawImage(imageIcon.getImage(), this.getX(), this.getY(), this.getX() + this.imageIcon.getIconWidth(), this.getY() + this.imageIcon.getIconHeight(), 0, 0, this.imageIcon.getIconHeight(), this.imageIcon.getIconWidth(), null);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getW() {
		return this.imageIcon.getIconWidth();
	}

	public int getH() {
		return this.imageIcon.getIconHeight();
	}

	public int getCenterX() {
		return this.x + this.imageIcon.getIconWidth() / 2;
	}

	public int getCenterY() {
		return this.y + this.imageIcon.getIconHeight() / 2;
	}

	public void doubleClick(XGraphEditor editor, int x, int y) {
		System.out.println("DOUBLE CLICK (" + x + "," + y + ")");
	}

	public void singleClick(XGraphEditor editor, int x, int y) {
		System.out.println("SINGLE CLICK (" + x + "," + y + ")");
	}

	public void controlClick(XGraphEditor editor, int x, int y) {
		System.out.println("CONTROL CLICK (" + x + "," + y + ")");
	}

	public void menuClick(XGraphEditor editor, int x, int y) {
		System.out.println("MENU CLICK (" + x + "," + y + ")");
	}

	public void dropTarget(XGraphEditor editor, XGraphNode dropTarget, int mouseEventModifiers) {
		XGraphEdge edge = new XGraphEdge();
		edge.setFrom(this);
		edge.setTo(dropTarget);
		if (editor.addEdge(edge)) {
			//System.out.println("Edge Added");
		} else {
			//System.out.println("Edge Already Exists");
		}
	}
}
