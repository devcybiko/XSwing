package us.thinkable.xswing.zed;

import java.awt.Color;
import java.awt.Graphics2D;

public class XGraphEdge {
	private XGraphNode from;
	private XGraphNode to;

	public void paint(Graphics2D g2) {
		g2.setColor(Color.black);
		g2.drawLine(from.getCenterX(), from.getCenterY(), to.getCenterX(), to.getCenterY());
	}

	public XGraphNode getFrom() {
		return from;
	}

	public void setFrom(XGraphNode from) {
		this.from = from;
	}

	public XGraphNode getTo() {
		return to;
	}

	public void setTo(XGraphNode to) {
		this.to = to;
	}
	
	
}
