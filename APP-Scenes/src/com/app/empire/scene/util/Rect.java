package com.app.empire.scene.util;

/**
 * 矩形包围盒
 * @author wkghost
 *
 */
public class Rect {
	
	public Rect(){}
	public Rect(float left, float top, float width, float height)
	{
		_xMin = left;
		_yMin = top;
		_width = width;
		_height = height;
	}
	
	public Rect clone()
	{
		return new Rect(_xMin, _yMin, _width, _height);
	}
	
	public boolean contains(Vector2 point) {
		return  ((((point.x >= this.getxMin()) && (point.x < this.getxMax())) && (point.y >= this.getyMin())) && (point.y < this.getyMax()));
	}
	
	public boolean contains(Vector3 point) {
		return  ((((point.x >= this.getxMin()) && (point.x < this.getxMax())) && (point.y >= this.getyMin())) && (point.y < this.getyMax()));
	}
	
	private float _xMin;
	public float getxMin() {
		return _xMin;
	}

	public void setxMin(float xMin) {
		float xMax = this.getxMax();
		_xMin = xMin;
		_width = xMax - _xMin;
	}

	private float _xMax;
	public float getxMax() {
		return _width + _xMin;
	}

	public void setxMax(float xMax) {
		_width = xMax - _xMin;
	}

	private float _yMin;
	public float getyMin() {
		return _yMin;
	}

	public void setyMin(float yMin) {
		float yMax = this.getyMax();
		_yMin = yMin;
		_height = yMax - _yMin;
	}

	private float _yMax;
	
	public float getyMax() {
		return _height + _yMin;
	}

	public void setyMax(float yMax) {
		_height = yMax - _yMin;
	}

	public float getX() {
		return _xMin;
	}
	
	public void setX(float x) {
		_xMin = x;
	}
	
	public float getY() {
		return _yMin;
	}
	
	public void setY(float y) {
		_yMin = y;
	}

	private float _width;
	public float getWidth() {
		return _width;
	}
	public void setWidth(float width) {
		_width = width;
	}

	private float _height;
	
	public float getHeight() {
		return _height;
	}
	public void setHeight(float height) {
		_height = height;
	}
	
	public float bottom() {
		return _yMin + _height;
	}
	
	public float left() {
		return _xMin;
	}
	
	public float right() {
		return _xMin + _width;
	}
	
	public float top()	{
		return _yMin;
	}
	
	public Vector2 getCenter()	{
		return new Vector2(getX() + _width/2, getY() + _height/2);
	}
	
	public void setCenter(Vector2 center) {
		_xMin = center.x - _width / 2;
		_yMin = center.y - _height / 2;
	}
		
	public Vector2 getMax() {
		return new Vector2(_xMax, _yMax);
	}
	
	public void setMax(Vector2 max) {
		_xMax = max.x;
		_yMax = max.y;
	}
	
	public Vector2 getMin() {
		return new Vector2(_xMin, _yMin);
	}
	
	public void setMin(Vector2 min) {
		_xMin = min.x;
		_yMin = min.y;
	}
	
	public Vector2 getPosition() {
		return new Vector2(_xMin, _yMin);
	}
	
	public void setPosition(Vector2 min) {
		_xMin = min.x;
		_yMin = min.y;
	}
	
	public Vector2 getSize() {
		return new Vector2(_width, _height);
	}
	
	public void setSize(Vector2 size) {
		_width = size.x;
		_height = size.y;
	}
}