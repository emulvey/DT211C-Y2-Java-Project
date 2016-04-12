/*
 * Author: C14752305 
 * Class: DrawingCanvas
 * Date: 10/04/2016
 * Generates an area with listeners for drawing on.
 */
package com.project.OOP2;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.*;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class DrawingCanvas extends JComponent implements Serializable{
	//Create ArrayList for storing drawn objects
	private ArrayList<Object> anchoredShapes = new ArrayList<Object>();
	
	//Create ArrayList for undo/redo state
	private ArrayList<Object> undoState = new ArrayList<Object>();
	private ArrayList<Object> redoState = new ArrayList<Object>();
	//Create ArrayList for guidance shape drawing
	ArrayList<Object> tempShapes = new ArrayList<Object>();
	
	private int xDrawStart;
	private int yDrawStart;
	private int xDrawEnd;
	private int yDrawEnd;
	private float xMax;
	private float yMax;
	final int snapvalue = 20;
	Graphics2D graphicsSettings;
	private Rectangle boundsRectangle = new Rectangle();
	
	//Constructor for monitoring events.
	public DrawingCanvas(){
		
		setxMax(800);
		setyMax(800);
		
		//add mouse listeners to canvas
		addMouseListener(new MouseAdapter(){
			//for when mouse pressed get x and y
			public void mousePressed(MouseEvent e){
				setxDrawStart(e.getX());
				setyDrawStart(e.getY());
				
				//Handle right mouse click for delete
				if (SwingUtilities.isRightMouseButton(e)){
					if(getAnchoredShapes().size() > 0){
						for(int index = 0; index<getAnchoredShapes().size(); index++){
							if(getAnchoredShapes().get(index) == null){
								continue;
							}
							if(((ShapeControl) getAnchoredShapes().get(index)).returnShape().getBounds2D().contains(getxDrawStart(), getyDrawStart())){
								//Set undo state
								setUndoState(new ArrayList<Object>());
								getUndoState().trimToSize();
								for(Object u: getAnchoredShapes()){
									if(u == null)
										continue;
									else
										getUndoState().add(u);
								}//end for
								
								getAnchoredShapes().remove(index);
								break;
							}//end if
						
						}//end for
					}//end if
				}
			}//end mousePressed
			
			public void mouseReleased(MouseEvent e){
				setxDrawEnd(e.getX());
				setyDrawEnd(e.getY());
				
				Object aShape = null;
								
				//call relevant drawing class.
				if (NewPage.drawOption == 1){
					DrawTerminal drawTerminal = new DrawTerminal(getxDrawStart(), getxDrawEnd(), getyDrawStart(), getyDrawEnd());
					aShape = drawTerminal;
					}//end if
				else if (NewPage.drawOption == 2){
					DrawArrow drawArrow = new DrawArrow(getxDrawStart(), getxDrawEnd(), getyDrawStart(), getyDrawEnd());
					aShape = drawArrow;
				}//end else if
				else if (NewPage.drawOption == 3){
					DrawRectangle drawRectangle = new DrawRectangle(getxDrawStart(), getxDrawEnd(), getyDrawStart(), getyDrawEnd());
					aShape = drawRectangle;
				}//end else if
				else if (NewPage.drawOption == 4){
					DrawParallelogram drawParallelogram = new DrawParallelogram(getxDrawStart(), getxDrawEnd(), getyDrawStart(), getyDrawEnd());
					aShape = drawParallelogram;
				}//end else if
				else if (NewPage.drawOption == 5){
					DrawRhombus drawRhombus = new DrawRhombus(getxDrawStart(), getxDrawEnd(), getyDrawStart(), getyDrawEnd());
					aShape = drawRhombus;
				}//end else if
				if (NewPage.drawOption == 6){
					DrawText drawText = new DrawText(getxDrawStart(), getyDrawStart());
					aShape = drawText;
					
				}//end else if
				else if (NewPage.drawOption == 0){
					if(getAnchoredShapes().size() > 0){
						for(Object o: getAnchoredShapes()){
							if(o == null){
								continue;
							}//end if
							else if(o instanceof ShapeControl){
								if(((ShapeControl) o).returnShape().getBounds2D().contains(getxDrawStart(), getyDrawStart())){
									//Get anchor positions for shape selected
									double a1Nx = ((ShapeControl) o).getAnchorNx();
									double a1Ny = ((ShapeControl) o).getAnchorNy();
									double a1Sx = ((ShapeControl) o).getAnchorSx();
									double a1Sy = ((ShapeControl) o).getAnchorSy();
									double a1Ex = ((ShapeControl) o).getAnchorEx();
									double a1Ey = ((ShapeControl) o).getAnchorEy();
									double a1Wx = ((ShapeControl) o).getAnchorWx();
									double a1Wy = ((ShapeControl) o).getAnchorWy();
									
									//Variables to hold snap difference, set to 0
									double snapx = 0;
									double snapy = 0;
									
									//Variables to hold location difference
									double xLocDiff = getxDrawEnd() - getxDrawStart();
									double yLocDiff = getyDrawEnd() - getyDrawStart();
									//Move object to new location
									AffineTransform translateTo = new AffineTransform();
									translateTo.translate(xLocDiff, yLocDiff );
									
									//Set undo state
									setUndoState(new ArrayList<Object>());
									getUndoState().trimToSize();
									for(Object u: getAnchoredShapes()){
										if(u == null)
											continue;
										else
											getUndoState().add(u);
									}//end for
									
									//Update drawn object ArrayList
									((ShapeControl) o).setShape(translateTo.createTransformedShape(((ShapeControl) o).returnShape()));
									//Adjust anchors
									((ShapeControl) o).setAnchorNx(((ShapeControl) o).getAnchorNx() + xLocDiff);
									((ShapeControl) o).setAnchorNy(((ShapeControl) o).getAnchorNy() + yLocDiff);
									((ShapeControl) o).setAnchorSx(((ShapeControl) o).getAnchorSx() + xLocDiff);
									((ShapeControl) o).setAnchorSy(((ShapeControl) o).getAnchorSy() + yLocDiff);
									((ShapeControl) o).setAnchorEx(((ShapeControl) o).getAnchorEx() + xLocDiff);
									((ShapeControl) o).setAnchorEy(((ShapeControl) o).getAnchorEy() + yLocDiff);
									((ShapeControl) o).setAnchorWx(((ShapeControl) o).getAnchorWx() + xLocDiff);
									((ShapeControl) o).setAnchorWy(((ShapeControl) o).getAnchorWy() + yLocDiff);
									
									//Check for nearby shapes
									for(Object o2 : getAnchoredShapes()){
										if(o2 == null){
											continue;
										}//end if
										else if(o2 instanceof ShapeControl){
											//Get anchor points for shape
											double a2Nx = ((ShapeControl) o2).getAnchorNx();
											double a2Ny = ((ShapeControl) o2).getAnchorNy();
											double a2Sx = ((ShapeControl) o2).getAnchorSx();
											double a2Sy = ((ShapeControl) o2).getAnchorSy();
											double a2Ex = ((ShapeControl) o2).getAnchorEx();
											double a2Ey = ((ShapeControl) o2).getAnchorEy();
											double a2Wx = ((ShapeControl) o2).getAnchorWx();
											double a2Wy = ((ShapeControl) o2).getAnchorWy();
											
											//test if south is near north
											if((Math.abs(a1Sx - a2Nx) < snapvalue) && (Math.abs(a1Sy - a2Ny) < snapvalue)){
												snapy = a1Sy - a2Ny;
												if(a1Sx > a2Nx)
													snapx = a1Sx - a2Nx;
												else
													snapx = a2Nx - a1Sx;
												break;
											}//end 										
											//test if north is near south
											else if((Math.abs(a1Nx - a2Sx) < snapvalue) && (Math.abs(a1Ny - a2Sy) < snapvalue)){
												snapy = a2Sy - a1Ny;
												if(a1Sx > a2Nx)
													snapx = a1Sx - a2Nx;
												else
													snapx = a2Nx - a1Sx;
												break;
											}//end else if
											//test if east is near west
											else if((Math.abs(a1Ex - a2Wx) < snapvalue) && (Math.abs(a1Ey - a2Wy) < snapvalue)){
												snapx = a2Wx - a1Ex;
												if(a1Ey > a2Wy)
													snapy = a1Ey - a2Wy;
												else
													snapy = a2Wy - a1Ey;
												break;
											}//end else if
											//test if west is near east
											else if((Math.abs(a1Wx - a2Ex) < snapvalue) && (Math.abs(a1Wy - a2Ey) < snapvalue)){
												snapx = a1Wx - a2Ex;
												if(a1Wy > a2Ey)
													snapy = a1Wy - a2Ey;
												else
													snapy = a2Ey - a1Wy;
												break;
											}//end else if
											else{
												continue;
											}
										}//end else if
										else
											continue;
										
										
									}//end for
									
									//Move again
									if(snapx > 0 || snapy > 0){
										translateTo.translate(snapx, snapy);
										
										((ShapeControl) o).setShape(translateTo.createTransformedShape(((ShapeControl) o).returnShape()));
										
										//Adjust anchors
										((ShapeControl) o).setAnchorNx(((ShapeControl) o).getAnchorNx() + snapx);
										((ShapeControl) o).setAnchorNy(((ShapeControl) o).getAnchorNy() + snapy);
										((ShapeControl) o).setAnchorSx(((ShapeControl) o).getAnchorSx() + snapx);
										((ShapeControl) o).setAnchorSy(((ShapeControl) o).getAnchorSy() + snapy);
										((ShapeControl) o).setAnchorEx(((ShapeControl) o).getAnchorEx() + snapx);
										((ShapeControl) o).setAnchorEy(((ShapeControl) o).getAnchorEy() + snapy);
										((ShapeControl) o).setAnchorWx(((ShapeControl) o).getAnchorWx() + snapx);
										((ShapeControl) o).setAnchorWy(((ShapeControl) o).getAnchorWy() + snapy);
										
										
									}//end if
									
									break;
								}//end if
							}//end else if
							else
								continue;
							
						
						}//end for
					}//end if

					
				}//end else if
				
				//Set undo state
				setUndoState(new ArrayList<Object>());
				getUndoState().trimToSize();
				for(Object u: getAnchoredShapes()){
					if(u == null)
						continue;
					else
						getUndoState().add(u);
				}//end for
				
				//Add shapes to arrayList
				if(aShape != null)
					getAnchoredShapes().add(aShape);
				
				NewPage.drawOption = 0;
				setBounds(getBoundsRectangle());
				repaint();
				
			}//end mouseReleased
	
		});//end mouse listeners
	}//end DrawingCanvas
	
	//Control graphical output
	public void paint(Graphics g){
		
		graphicsSettings = (Graphics2D)g;
		
		/*Code taken from: 
		 * http://www.newthinktank.com/2012/07/java-video-tutorial-49/
		 */
		// Antialiasing cleans up the jagged lines and defines rendering rules
            
		graphicsSettings.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
		/*End of code copied*/    
		
		//Set the width of the lines drawn
		graphicsSettings.setStroke(new BasicStroke(1));

		//Set the line color
		graphicsSettings.setColor(Color.BLACK);
			
		
		/*Code taken from: 
		 * http://www.newthinktank.com/2012/07/java-video-tutorial-49/
		 */
		//Draws images onto page
		for(Object i: getAnchoredShapes()){
			if(i != null){
				if(i instanceof ShapeControl){
					graphicsSettings.draw(((ShapeControl) i).returnShape());
					if(((ShapeControl)i).returnShape().getBounds().getMaxX()
							> getxMax()){
						setxMax((float) ((ShapeControl)i).returnShape().getBounds().getMaxX());
						
					}//end if
					if(((ShapeControl)i).returnShape().getBounds().getMaxY()
							> getyMax()){
						setyMax((float) ((ShapeControl)i).returnShape().getBounds().getMaxY());
						
					}//end if
				}//end if
				else{
					graphicsSettings.drawString(((DrawText) i).returnText(), ((DrawText) i).getTextx(), ((DrawText) i).getTexty());
					
				}//end else
			}//end if
		}//end for
		/*End of code copied*/
		
		/*Not working for now	 
		//Create guide image while drawing.
		Point drawStart, drawEnd;
		drawStart = new Point(getxDrawStart(), getyDrawStart());
		drawEnd = new Point(getxDrawEnd(), getyDrawEnd());
		if(drawStart != null && drawEnd != null){
			if (NewPage.drawOption == 1){
				DrawTerminal drawTerminal = new DrawTerminal(getxDrawStart(), getxDrawEnd(), getyDrawStart(), getyDrawEnd());
				aShape = drawTerminal.returnShape();
			}
			else if (NewPage.drawOption == 2){
				DrawArrow drawArrow = new DrawArrow(getxDrawStart(), getxDrawEnd(), getyDrawStart(), getyDrawEnd());
				aShape = drawArrow.returnShape();
			}
			else if (NewPage.drawOption == 3){
				DrawRectangle drawRectangle = new DrawRectangle(getxDrawStart(), getxDrawEnd(), getyDrawStart(), getyDrawEnd());
				aShape = drawRectangle.returnShape();
			}
			else if (NewPage.drawOption == 4){
				DrawParallelogram drawParallelogram = new DrawParallelogram(getxDrawStart(), getxDrawEnd(), getyDrawStart(), getyDrawEnd());
				aShape = drawParallelogram.returnShape();
			}
			else if (NewPage.drawOption == 5){
				DrawRhombus drawRhombus = new DrawRhombus(getxDrawStart(), getxDrawEnd(), getyDrawStart(), getyDrawEnd());
				aShape = drawRhombus.returnShape();
			}
			else{
				aShape = null;
			}
			graphicsSettings.draw(aShape);
		}
       	*/       	
    }//end paint

	//Getters & Setters
	public int getxDrawStart() {
		return xDrawStart;
	}

	public void setxDrawStart(int xDrawStart) {
		this.xDrawStart = xDrawStart;
	}

	public int getyDrawStart() {
		return yDrawStart;
	}

	public void setyDrawStart(int yDrawStart) {
		this.yDrawStart = yDrawStart;
	}

	public int getxDrawEnd() {
		return xDrawEnd;
	}

	public void setxDrawEnd(int xDrawEnd) {
		this.xDrawEnd = xDrawEnd;
	}

	public int getyDrawEnd() {
		return yDrawEnd;
	}

	public void setyDrawEnd(int yDrawEnd) {
		this.yDrawEnd = yDrawEnd;
	}

	public float getxMax() {
		return xMax;
	}

	public void setxMax(float xMax) {
		this.xMax = xMax;
		getBoundsRectangle().setSize((int) xMax, (int) getyMax());
		
	}

	float getyMax() {
		return yMax;
	}

	void setyMax(float yMax) {
		this.yMax = yMax;
		getBoundsRectangle().setSize((int)getxMax(), (int) yMax);
		
	}

	public ArrayList<Object> getAnchoredShapes() {
		return anchoredShapes;
	}

	public void setAnchoredShapes(ArrayList<Object> anchoredShapes) {
		this.anchoredShapes = anchoredShapes;
	}

	public ArrayList<Object> getUndoState() {
		return undoState;
	}

	public void setUndoState(ArrayList<Object> undoState) {
		this.undoState = undoState;
	}

	public ArrayList<Object> getRedoState() {
		return redoState;
	}

	public void setRedoState(ArrayList<Object> redoState) {
		this.redoState = redoState;
	}

	public Rectangle getBoundsRectangle() {
		return boundsRectangle;
	}

	public void setBoundsRectangle(Rectangle boundsRectangle) {
		this.boundsRectangle = boundsRectangle;
	}
	
}
