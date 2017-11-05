package cn.dianyou.beans;

public class PartImageInfo {
	private String title;
	private String url;
	private float animDregee;
	private boolean isCenter;
	private int width;
	private int height;
	private boolean isAnimation;
	private float rotateRadiusX;
	private float RotateRadiusY;
	private float rotateXOffset;
	private float rotateYOffset;
	private float leftOffset;
	private float topOffset;
	private float leftRadius;
	private float topRadius;
	
	public PartImageInfo() {
	}
	
	public PartImageInfo(String title, String url, float animDregee, boolean isCenter, int width, int height,
			boolean isAnimation, float rotateRadiusX, float rotateRadiusY, float rotateXOffset, float rotateYOffset,
			float leftOffset, float topOffset, float leftRadius, float topRadius) {
		super();
		this.title = title;
		this.url = url;
		this.animDregee = animDregee;
		this.isCenter = isCenter;
		this.width = width;
		this.height = height;
		this.isAnimation = isAnimation;
		this.rotateRadiusX = rotateRadiusX;
		RotateRadiusY = rotateRadiusY;
		this.rotateXOffset = rotateXOffset;
		this.rotateYOffset = rotateYOffset;
		this.leftOffset = leftOffset;
		this.topOffset = topOffset;
		this.leftRadius = leftRadius;
		this.topRadius = topRadius;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public float getAnimDregree() {
		return animDregee;
	}
	public void setAnimDregee(float animDregee) {
		this.animDregee = animDregee;
	}
	public float getRotateRadiusX() {
		return rotateRadiusX;
	}
	public void setRotateRadiusX(float rotateRadiusX) {
		this.rotateRadiusX = rotateRadiusX;
	}
	public float getRotateRadiusY() {
		return RotateRadiusY;
	}
	public void setRotateRadiusY(float rotateRadiusY) {
		RotateRadiusY = rotateRadiusY;
	}
	public boolean isCenter() {
		return isCenter;
	}
	public void setCenter(boolean isCenter) {
		this.isCenter = isCenter;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public boolean isAnimation() {
		return isAnimation;
	}
	public void setAnimation(boolean isAnimation) {
		this.isAnimation = isAnimation;
	}
	public float getLeftRadius() {
		return leftRadius;
	}
	public void setLeftRadius(float leftRadius) {
		this.leftRadius = leftRadius;
	}
	public float getTopRadius() {
		return topRadius;
	}
	public void setTopRadius(float topRadius) {
		this.topRadius = topRadius;
	}
	
	public float getRotateXOffset() {
		return rotateXOffset;
	}

	public void setRotateXOffset(float rotateXOffset) {
		this.rotateXOffset = rotateXOffset;
	}

	public float getRotateYOffset() {
		return rotateYOffset;
	}

	public void setRotateYOffset(float rotateYOffset) {
		this.rotateYOffset = rotateYOffset;
	}

	public float getLeftOffset() {
		return leftOffset;
	}

	public void setLeftOffset(float leftOffset) {
		this.leftOffset = leftOffset;
	}

	public float getTopOffset() {
		return topOffset;
	}

	public void setTopOffset(float topOffset) {
		this.topOffset = topOffset;
	}

	public float getAnimDregee() {
		return animDregee;
	}

	@Override
	public String toString() {
		return "PartImageInfo [title=" + title + ", url=" + url + ", animDregee=" + animDregee + ", isCenter="
				+ isCenter + ", width=" + width + ", height=" + height + ", isAnimation=" + isAnimation
				+ ", rotateRadiusX=" + rotateRadiusX + ", RotateRadiusY=" + RotateRadiusY + ", rotateXOffset="
				+ rotateXOffset + ", rotateYOffset=" + rotateYOffset + ", leftOffset=" + leftOffset + ", topOffset="
				+ topOffset + ", leftRadius=" + leftRadius + ", topRadius=" + topRadius + "]";
	}
	
	
	
}
