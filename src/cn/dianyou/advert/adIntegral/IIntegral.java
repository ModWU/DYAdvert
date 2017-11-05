package cn.dianyou.advert.adIntegral;

public interface IIntegral {
	float queryPoints();
	boolean spendPoints(float points);
	boolean addPoints(float points);
	void destroy();
}
